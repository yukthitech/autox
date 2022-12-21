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
package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.AutoxCountDownLatch;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executors thread pool.
 * @author akranthikiran
 */
public class ExecutionPool
{
	private static Logger logger = LogManager.getLogger(ExecutionPool.class);

	private static ExecutionPool instance;
	
	private static class ExecutorRunnable implements Runnable
	{
		private Executor executor;
		
		private AutoxCountDownLatch countDownLatch;
		
		private List<ExecutorRunnable> dependentItems;
		
		private ExecutorService threadPool;
		
		private Setup beforeChildFromParent;
		
		private Cleanup afterChildFromParent;
		
		public ExecutorRunnable(Executor executor, AutoxCountDownLatch countDownLatch, ExecutorService threadPool, Setup beforeChildFromParent, Cleanup afterChildFromParent)
		{
			this.executor = executor;
			this.countDownLatch = countDownLatch;
			this.threadPool = threadPool;
			this.beforeChildFromParent = beforeChildFromParent;
			this.afterChildFromParent = afterChildFromParent;
		}
		
		public void addDependent(ExecutorRunnable executorRunnable)
		{
			if(this.dependentItems == null)
			{
				this.dependentItems = new ArrayList<>();
			}
			
			this.dependentItems.add(executorRunnable);
		}
		
		private void checkDependencyItems()
		{
			if(CollectionUtils.isEmpty(dependentItems))
			{
				return;
			}
			
			dependentItems.forEach(executorRunnable -> 
			{
				//NOTE: for skipped executor in all parent executors this condition will fail
				// and never gets executed
				if(!executorRunnable.executor.isStarted() && executorRunnable.executor.isReadyToExecute())
				{
					threadPool.execute(executorRunnable);
				}
			});
		}
		
		public void run() 
		{
			AsyncTryCatchBlock.doTry(executor.getUniqueId(), callback -> 
			{
				//if init fail, consider execution to be failed and dont invoke execute()
				executor.execute(beforeChildFromParent, afterChildFromParent, callback);
			}).onError((callback, ex) -> 
			{
				logger.error("An error occurred while executing executor: " + executor, ex);
			}).onFinally(callback -> 
			{
				checkDependencyItems();
				
				logger.debug("Execution completed: {}", executor.getUniqueId());
				countDownLatch.countDown();
			}).execute();
		}
	}
	
	private ExecutorService threadPool;
	
	private ExecutionPool()
	{
		String parallelExecutionCountStr = AutomationContext.getInstance().getOverridableProp(IAutomationConstants.AUTOX_PROP_PARALLEL_POOL_SIZE);
		int parallelExecutionCount = 0;
		
		if(StringUtils.isNotBlank(parallelExecutionCountStr))
		{
			try
			{
				parallelExecutionCount = Integer.parseInt(parallelExecutionCountStr);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Invalid value specified for parallel execution count config '{}'. Value specified: {}", 
						IAutomationConstants.AUTOX_PROP_PARALLEL_POOL_SIZE, parallelExecutionCountStr, ex);
			}
			
			if(parallelExecutionCount > 0)
			{
				threadPool = Executors.newFixedThreadPool(parallelExecutionCount);
				logger.debug("Created parallel execution pool with size: {}", parallelExecutionCount);
			}
			else
			{
				logger.debug("Parallel execution is disabled as parallel pool size is specified (Config Name: {}) with zero or negative value: {}"
						, IAutomationConstants.AUTOX_PROP_PARALLEL_POOL_SIZE, parallelExecutionCount);		
			}
		}
		else
		{
			logger.debug("Parallel execution is disabled as no parallel pool size is specified (Config Name: {})", IAutomationConstants.AUTOX_PROP_PARALLEL_POOL_SIZE);
		}
	}
	
	public synchronized static ExecutionPool getInstance()
	{
		if(instance == null)
		{
			instance = new ExecutionPool();
		}
		
		return instance;
	}
	
	public static void reset()
	{
		if(instance != null && instance.threadPool != null)
		{
			instance.threadPool.shutdownNow();
		}
		
		instance = null;
	}
	
	private void executeSequentially(Executor parent, List<? extends Executor> executors, Setup beforeChild, Cleanup afterChild, AsyncTryCatchBlock parentCallback)
	{
		//Note: in sequence execution, dependencies will not be considered as ordering would be done
		// during load time
		AutoxCountDownLatch latch = new AutoxCountDownLatch(executors.size());
		
		latch.setCallback(() -> 
		{
			logger.debug("{} SERIAL execution completed", parent.getUniqueId());
			//Note: for sequential execution auto-complete of parent is not disable
			//  so no need to call triggerComplete method
		});
		
		for(Executor executor : executors)
		{
			parentCallback.newChild(executor.getUniqueId() + "-trigger", callback -> 
			{
				if(executor.isReadyToExecute())
				{
					executor.execute(beforeChild, afterChild, callback);
				}
			}).onError((callback, ex) -> 
			{
				parentCallback.triggerError(ex);
			}).onFinally(callback -> 
			{
				latch.countDown();
			});
		}
	}

	private void executeParallelly(Executor parent, List<? extends Executor> executors, Setup beforeChildFromParent, Cleanup afterChildFromParent, AsyncTryCatchBlock parentCallback)
	{
		AutoxCountDownLatch latch = new AutoxCountDownLatch(executors.size());
		
		latch.setCallback(() -> 
		{
			logger.debug("{} PARALLEL execution completed.", parent.getUniqueId());
			parentCallback.triggerComplete();
		});
		
		
		IdentityHashMap<Executor, ExecutorRunnable> runnableMap = new IdentityHashMap<>();
		
		//create runnable objects
		for(Executor executor : executors)
		{
			ExecutorRunnable runnable = new ExecutorRunnable(executor, latch, threadPool, beforeChildFromParent, afterChildFromParent);
			runnableMap.put(executor, runnable);
		}
		
		//add dependent runnables
		//NOTE: Immediate execution is not done to ensure all dependents are added properly
		List<ExecutorRunnable> directExecutables = new ArrayList<>();
		
		for(Map.Entry<Executor, ExecutorRunnable> entry : runnableMap.entrySet())
		{
			List<Executor> dependencies = entry.getKey().getDependencies();
			
			if(CollectionUtils.isEmpty(dependencies))
			{
				directExecutables.add(entry.getValue());
				continue;
			}
			
			dependencies.forEach(dependency -> 
			{
				runnableMap.get(dependency).addDependent(entry.getValue());
			});
		}
		
		//parent callback should be considered completed only
		// after all tasks are completed
		parentCallback.setAutoComplete(false);
		
		//once dependents are added, execute the executors
		directExecutables.forEach(runnable -> threadPool.execute(runnable));
	}
	
	public void execute(Executor parent, List<? extends Executor> executors, Setup beforeChildFromParent, Cleanup afterChildFromParent, 
			boolean parallelExecutionEnabled, AsyncTryCatchBlock parentCallback)
	{
		if(parallelExecutionEnabled && threadPool != null)
		{
			logger.debug("Executor {} executing {} child executors in PARALLEL", parent.getUniqueId(), executors.size());
			executeParallelly(parent, executors, beforeChildFromParent, afterChildFromParent, parentCallback);
		}
		else
		{
			logger.debug("Executor {}  executing {} child executors in SERIAL", parent.getUniqueId(), executors.size());
			executeSequentially(parent, executors, beforeChildFromParent, afterChildFromParent, parentCallback);
		}
	}
}
