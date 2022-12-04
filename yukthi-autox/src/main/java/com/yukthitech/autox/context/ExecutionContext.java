/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.autox.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.exec.Executor;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.Log4jExecutionLogger;
import com.yukthitech.autox.logmon.ILogMonitor;
import com.yukthitech.autox.logmon.ILogMonitorSession;
import com.yukthitech.autox.logmon.LogMonitorManager;
import com.yukthitech.autox.plugin.IPluginSession;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents execution (or thread) specific execution data. 
 * @author akranthikiran
 */
public class ExecutionContext
{
	private ExecutionContextManager parent;
	
	/**
	 * Parent executor.
	 */
	Executor executor;
	
	/**
	 * Map to hold context attributes.
	 */
	private Map<String, Object> nameToAttr = Collections.synchronizedMap(new HashMap<>());
	
	/**
	 * Internal context attributes.
	 */
	private Map<String, Object> internalContextAtt = Collections.synchronizedMap(new HashMap<>());
	
	/**
	 * Parent context, if any.
	 */
	private ExecutionContext parentContext;

	private List<ILogMonitorSession> monitorSessions;
	
	/**
	 * Active plugin session. This will be set during plugin event 
	 * handler execution and represent current session for which
	 * event handling is being done.
	 */
	private Stack<IPluginSession> activePluginStack = new Stack<>();
	
	public ExecutionContext(ExecutionContextManager parent, Executor executor)
	{
		this.parent = parent;
		this.executor = executor;

		if(this.executor.getParentExecutor() != null)
		{
			this.parentContext = parent.getContext(executor.getParentExecutor());
			
			if(executor.isParentContextShared())
			{
				this.nameToAttr = parentContext.nameToAttr;
				this.internalContextAtt = parentContext.internalContextAtt;
			}
			else
			{
				this.parentContext.populateParentAttributes(nameToAttr);
			}
		}

		//note: data-provider data test-cases (not the main one) will not have dependencies
		populateDependecyAttributes();
	}
	
	private void populateParentAttributes(Map<String, Object> newMap)
	{
		if(parentContext != null)
		{
			parentContext.populateParentAttributes(newMap);
		}
		
		newMap.putAll(nameToAttr);
	}
	
	private void populateDependecyAttributes()
	{
		List<Executor> dependencies = executor.getDependencies();
		
		if(CollectionUtils.isEmpty(dependencies))
		{
			return;
		}

		for(Executor depExecutor : dependencies)
		{
			ExecutionContext depContext = parent.getContext(depExecutor);
			
			if(depContext.nameToAttr != null)
			{
				nameToAttr.putAll(depContext.nameToAttr);
			}
		}
	}
	
	public void setInternalAttribute(String name, Object value)
	{
		internalContextAtt.put(name, value);
	}

	public Object getInternalAttribute(String name)
	{
		return internalContextAtt.get(name);
	}
	
	public void setAttribute(String name, Object value)
	{
		nameToAttr.put(name, value);
	}
	
	public Object getAttribute(String name)
	{
		Object val = nameToAttr.get(name);
		
		if(val != null)
		{
			return val;
		}
		
		return (parentContext != null) ? parentContext.getAttribute(name) : null;
	}

	public Object removeAttribute(String name)
	{
		return nameToAttr.remove(name);
	}

	public Map<String, Object> getAttr()
	{
		return nameToAttr;
	}
	
	public synchronized void pushActivePlugin(IPluginSession activePlugin)
	{
		this.activePluginStack.push(activePlugin);
	}
	
	public synchronized void popActivePlugin()
	{
		this.activePluginStack.pop();
	}
	
	public synchronized IPluginSession getActivePlugin()
	{
		return activePluginStack.peek();
	}
	
	public synchronized Map<String, Object> getPluginAttr(String name)
	{
		if(activePluginStack.isEmpty())
		{
			throw new InvalidStateException("No active plugin session found on context");
		}
		
		return activePluginStack.peek().getAttributes(name);
	}

	public Function getFunction(String name)
	{
		if(executor.getExecutable() instanceof TestSuite)
		{
			return ((TestSuite) executor.getExecutable()).getFunction(name);
		}
		
		if(parentContext != null)
		{
			return parentContext.getFunction(name);
		}
		
		return null;
	}
	
	public IExecutionLogger getExecutionLogger()
	{
		IExecutionLogger executionLogger = executor.getActiveExecutionLogger();
		
		if(executionLogger == null)
		{
			return Log4jExecutionLogger.getInstance();
		}
		
		return executionLogger;
	}
	
	public synchronized void startLogMonitoring()
	{
		if(monitorSessions != null)
		{
			throw new InvalidStateException("Monitors are already started");
		}
		
		Collection<ILogMonitor> monitors = LogMonitorManager.getInstance().getLogMonitors();
		
		if(CollectionUtils.isEmpty(monitors))
		{
			return;
		}
		
		this.monitorSessions = new ArrayList<>();
		
		monitors.forEach(monitor -> 
		{
			ILogMonitorSession session = monitor.newSession();
			monitorSessions.add(session);
			
			session.startMonitoring();
		});
	}
	
	public synchronized Map<String, ReportLogFile> stopMonitoring(boolean flowErrored)
	{
		if(monitorSessions == null)
		{
			return null;
		}

		Map<String, ReportLogFile> logFiles = new HashMap<>();
		
		for(ILogMonitorSession monitorSession : this.monitorSessions)
		{
			if(!monitorSession.getParentMonitor().isEnabled())
			{
				continue;
			}
			
			if(monitorSession.getParentMonitor().isOnErrorOnly() && !flowErrored)
			{
				continue;
			}

			List<ReportLogFile> monitorLogFiles = monitorSession.stopMonitoring();
			
			if(CollectionUtils.isEmpty(monitorLogFiles))
			{
				continue;
			}
			
			for(ReportLogFile logFile : monitorLogFiles)
			{
				String key = monitorSession.getParentMonitor().getName();
				logFiles.put(key, logFile);
			}
		}
		
		this.monitorSessions.clear();
		return logFiles;
	}
}
