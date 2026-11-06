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
package com.yukthitech.autox.session;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.ContentStream;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.event.AutomationEventManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Marshals all context access onto a dedicated worker that keeps a live
 * {@link InteractiveSessionExecutor} bound so plugin sessions are not returned
 * to the pool until {@link #close()}.
 */
final class InteractiveContextWrapper implements IContextWrapper
{
	private static final Logger logger = LogManager.getLogger(InteractiveContextWrapper.class);
	
	private final AutomationContext context;
	
	private final ExecutorService worker;
	
	private final InteractiveSessionExecutor liveExecutor;
	
	private volatile boolean closed;
	
	private InteractiveContextWrapper(AutomationContext context, ExecutorService worker,
			InteractiveSessionExecutor liveExecutor)
	{
		this.context = context;
		this.worker = worker;
		this.liveExecutor = liveExecutor;
	}
	
	static InteractiveContextWrapper create(ContentStream appConfiguration, String[] extendedCommandLineArgs,
			AutomationEventManager automationEventManager) throws Exception
	{
		ExecutorService worker = Executors.newSingleThreadExecutor(new ThreadFactory()
		{
			@Override
			public Thread newThread(Runnable r)
			{
				Thread t = new Thread(r, "autox-interactive");
				t.setDaemon(false);
				return t;
			}
		});
		
		InteractiveSessionExecutor liveExecutor = new InteractiveSessionExecutor();
		AtomicReference<AutomationContext> contextRef = new AtomicReference<>();
		
		Future<?> init = worker.submit(() ->
		{
			try
			{
				AutomationContext ctx = AutoxSession.createContext(appConfiguration, extendedCommandLineArgs,
						automationEventManager);
				ctx.getExecutionContextManager().bindLiveExecutor(liveExecutor);
				contextRef.set(ctx);
			}catch(Exception ex)
			{
				throw new RuntimeException(ex);
			}
		});
		
		try
		{
			init.get();
		}catch(ExecutionException ex)
		{
			worker.shutdownNow();
			Throwable cause = ex.getCause();
			
			if(cause instanceof RuntimeException && cause.getCause() instanceof Exception)
			{
				throw (Exception) cause.getCause();
			}
			
			if(cause instanceof Exception)
			{
				throw (Exception) cause;
			}
			
			throw ex;
		}catch(InterruptedException ex)
		{
			worker.shutdownNow();
			Thread.currentThread().interrupt();
			throw new InvalidStateException("Interrupted while starting interactive session", ex);
		}
		
		return new InteractiveContextWrapper(contextRef.get(), worker, liveExecutor);
	}
	
	@Override
	public <T> T execute(Function<AutomationContext, T> func)
	{
		ensureOpen();
		
		Future<T> future = worker.submit(() -> func.apply(context));
		
		try
		{
			return future.get();
		}catch(InterruptedException ex)
		{
			Thread.currentThread().interrupt();
			throw new InvalidStateException("Interrupted while executing on interactive worker", ex);
		}catch(ExecutionException ex)
		{
			Throwable cause = ex.getCause();
			
			if(cause instanceof RuntimeException)
			{
				throw (RuntimeException) cause;
			}
			
			if(cause instanceof Error)
			{
				throw (Error) cause;
			}
			
			throw new InvalidStateException("Interactive worker execution failed", cause);
		}
	}
	
	@Override
	public boolean isInteractive()
	{
		return true;
	}
	
	@Override
	public synchronized void close()
	{
		if(closed)
		{
			return;
		}
		
		closed = true;
		
		try
		{
			Future<?> shutdown = worker.submit(() ->
			{
				try
				{
					context.getExecutionContextManager().unbindLiveExecutor(liveExecutor);
				}catch(Exception ex)
				{
					logger.warn("Error while unbinding live interactive executor", ex);
				}
				
				try
				{
					context.reset();
				}catch(Exception ex)
				{
					logger.warn("Error while resetting automation context", ex);
				}
			});
			
			try
			{
				shutdown.get(120, TimeUnit.SECONDS);
			}catch(Exception ex)
			{
				logger.warn("Error while closing interactive context on worker", ex);
			}
		}finally
		{
			worker.shutdown();
			
			try
			{
				if(!worker.awaitTermination(30, TimeUnit.SECONDS))
				{
					worker.shutdownNow();
				}
			}catch(InterruptedException ex)
			{
				worker.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}
	
	private void ensureOpen()
	{
		if(closed)
		{
			throw new InvalidStateException("AutoxSession is already closed");
		}
	}
}
