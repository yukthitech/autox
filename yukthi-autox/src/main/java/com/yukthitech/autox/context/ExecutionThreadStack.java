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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.IPluginSession;
import com.yukthitech.autox.plugin.PluginManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * One instance of this class will be created per thread. Threads which are created
 * as part of single execution will be sharing {@link #executionContextStack}.
 * 
 * @author akranthikiran
 */
public class ExecutionThreadStack
{
	private ExecutionContext executionContext;
	
	private Map<Class<?>, IPluginSession> pluginSessions = new HashMap<>();
	
	/**
	 * Used to maintain function parameter stack.
	 */
	private Stack<Map<String, Object>> parametersStack = new Stack<>();

	/**
	 * Manages the stack trace of execution.
	 */
	ExecutionStack executionStack = new ExecutionStack();
	
	public ExecutionThreadStack(ExecutionContext context)
	{
		this.executionContext = context;
	}
	
	public void setExecutionContext(ExecutionContext executionContext)
	{
		this.executionContext = executionContext;
	}

	public ExecutionContext getExecutionContext()
	{
		return executionContext;
	}
	
	public void clearExecutionContext()
	{
		this.executionContext = null;
		
		pluginSessions.values().forEach(session -> session.release());
		pluginSessions.clear();
	}

	@SuppressWarnings("unchecked")
	public <P, S extends IPluginSession> S getPluginSession(Class<? extends IPlugin<?, S>> pluginType)
	{
		IPluginSession session = pluginSessions.get(pluginType);
		
		if(session == null)
		{
			IPlugin<?, ?> plugin = PluginManager.getInstance().getPlugin(pluginType);
			session = plugin.newSession();
			
			pluginSessions.put(pluginType, session);
		}
		
		return (S) session;
	}
	
	public Collection<IPluginSession> getPluginSessions()
	{
		return pluginSessions.values();
	}

	public void pushParameters(Map<String, Object> parameters)
	{
		if(parameters == null)
		{
			parameters = Collections.emptyMap();
		}
		
		parametersStack.push(parameters);
	}

	public void popParameters()
	{
		parametersStack.pop();
	}
	
	public boolean isParamPresent()
	{
		return !parametersStack.isEmpty();
	}
	
	public Map<String, Object> getParam()
	{
		if(parametersStack.isEmpty())
		{
			throw new InvalidStateException("Parameters are accessed outside the function");
		}
		
		return (Map<String, Object>) parametersStack.peek();
	}
	
	public Object getParameter(String name)
	{
		Map<String, Object> paramMap = (Map<String, Object>) getParam();
		
		if(paramMap == null)
		{
			return null;
		}
		
		return paramMap.get(name);
	}
}
