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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.config.IPluginSession;
import com.yukthitech.autox.exec.Executor;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manages execution scopes across different executions/threads.
 * @author akranthikiran
 */
public class ExecutionContextManager
{
	private static Logger logger = LogManager.getLogger(ExecutionContextManager.class);
	
	private static ExecutionContextManager instance = new ExecutionContextManager();
	
	private Map<Thread, ExecutionThreadStack> executionThreadLocal = new IdentityHashMap<>();
	
	private Map<Executor, ExecutionContext> executorContexts = new IdentityHashMap<>();
	
	/**
	 * Context which is created first (which would be test-suite-group based in general).
	 */
	private ExecutionContext globalContext;
	
	private ExecutionContextManager()
	{}
	
	public static ExecutionContextManager getInstance()
	{
		return instance;
	}
	
	public static void reset()
	{
		instance.executionThreadLocal.clear();
		instance.executorContexts.clear();
	}
	
	/**
	 * Executes specified supplier in the context of specified executor.
	 * @param <T>
	 * @param executor
	 * @param suppier
	 * @return
	 */
	public static <T> T executeInContext(Executor executor, Supplier<T> suppier)
	{
		Thread currentThread = Thread.currentThread();
		String actName = currentThread.getName();
		
		instance.setExecutor(executor);
		currentThread.setName(executor.getUniqueId());
		
		try
		{
			return suppier.get();
		}catch(RuntimeException ex) 
		{
			logger.error("An error occurred during context based execution: " + ex);
			throw ex;
		} finally
		{
			currentThread.setName(actName);
			instance.clearExecutor(executor);
		}
	}
	
	public static void executeInContext(Executor executor, Runnable suppier)
	{
		Supplier<Object> adapter = new Supplier<Object>()
		{
			@Override
			public Object get()
			{
				suppier.run();
				return null;
			}
		};

		executeInContext(executor, adapter);
	}

	synchronized ExecutionContext getContext(Executor executor)
	{
		ExecutionContext context = this.executorContexts.get(executor);
		
		if(context != null)
		{
			return context;
		}
		
		context = new ExecutionContext(this, executor);
		this.executorContexts.put(executor, context);
		
		return context;
	}
	
	private synchronized void setExecutor(Executor executor)
	{
		ExecutionThreadStack executionContextStack = executionThreadLocal.get(Thread.currentThread());
		ExecutionContext context = getContext(executor);
		
		if(executionContextStack == null)
		{
			executionContextStack = new ExecutionThreadStack(context);
			executionThreadLocal.put(Thread.currentThread(), executionContextStack);
		}
		else
		{
			if(executionContextStack.getExecutionContext() != null)
			{
				throw new InvalidStateException("New executor being set without clearing old one. [Executor on stack: {}, Executor being set: {}]", 
						executionContextStack.getExecutionContext().executor, executor);
			}
			
			executionContextStack.setExecutionContext(context);
		}
		
		if(globalContext == null)
		{
			globalContext = context;
		}
		
		executionContextStack.executionStack.push(executor.getExecutable());
	}

	private synchronized void clearExecutor(Executor executor)
	{
		ExecutionThreadStack executionContextStack = executionThreadLocal.get(Thread.currentThread());
		
		if(executionContextStack == null || executionContextStack.getExecutionContext().executor != executor)
		{
			throw new InvalidStateException("Executor being cleared is not same executor found on stack. [Executor on stack: {}, Executor being poped: {}]", 
					executionContextStack.getExecutionContext().executor, executor);
		}
		
		executionContextStack.executionStack.pop(executor.getExecutable());
		executionContextStack.clearExecutionContext();
	}
	
	public static ExecutionContext getExecutionContext()
	{
		return instance.getCurrentContext();
	}
	
	public synchronized ExecutionThreadStack getExecutionContextStack()
	{
		ExecutionThreadStack executionContextStack = executionThreadLocal.get(Thread.currentThread());
		
		if(executionContextStack == null || executionContextStack.getExecutionContext() == null)
		{
			throw new NonExecutionThreadException("Execution context method is invoked by non-executor thread");
		}

		return executionContextStack;
	}
	
	private ExecutionContext getCurrentContext()
	{
		return getExecutionContextStack().getExecutionContext();
	}
	
	public <P, S extends IPluginSession> S getPluginSession(Class<? extends IPlugin<?, S>> pluginType)
	{
		return getExecutionContextStack().getPluginSession(pluginType);
	}
	
	public ExecutionStack getExecutionStack()
	{
		return getExecutionContextStack().executionStack;
	}
	
	public void setGlobalAttribute(String name, Object value)
	{
		if(globalContext == null)
		{
			throw new InvalidStateException("No global context is created yet");
		}
		
		globalContext.setAttribute(name, value);
		
		executorContexts.values().forEach(context -> 
		{
			if(context == globalContext)
			{
				return;
			}
			
			context.setAttribute(name, value);
		});
	}
	
	public Object getGlobalAttribute(String name)
	{
		if(globalContext == null)
		{
			throw new InvalidStateException("No global context is created yet");
		}
		
		return globalContext.getAttribute(name);
	}
	
	public synchronized void close()
	{
		this.executionThreadLocal.clear();
	}
}
