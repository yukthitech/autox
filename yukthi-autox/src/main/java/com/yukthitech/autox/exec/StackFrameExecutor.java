/**
 * Copyright (c) 2023 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
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

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.test.DropToStackFrameException;

public class StackFrameExecutor<E>
{
	private static Logger logger = LogManager.getLogger(StackFrameExecutor.class);
	
	public static interface IExecutor<E>
	{
		public void execute(E executable, String stackFrameId) throws Exception;
	}
	
	private String identity;
	private E executable;
	private IExecutor<E> executor;
	
	private Function<E, E> reloadFunc;
	
	private Function<E, IStep> firstStepFetcher;

	private StackFrameExecutor(String identity, E executable, IExecutor<E> executor)
	{
		this.identity = identity;
		this.executable = executable;
		this.executor = executor;
	}

	public static <E> StackFrameExecutor<E> newExecutor(String identity, E executable, IExecutor<E> executor)
	{
		return new StackFrameExecutor<>(identity, executable, executor);
	}

	public StackFrameExecutor<E> onReload(Function<E, E> reloadFunc)
	{
		this.reloadFunc = reloadFunc;
		return this;
	}
	
	public StackFrameExecutor<E> firstStepFetcher(Function<E, IStep> firstStepFetcher)
	{
		this.firstStepFetcher = firstStepFetcher;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private IStep fetchFirstStep()
	{
		if(firstStepFetcher != null)
		{
			return firstStepFetcher.apply(executable);
		}
		
		//As part of executor (test-cases), executable is list of steps
		if(executable instanceof List)
		{
			List<Object> lst = (List<Object>) executable;
			
			if(CollectionUtils.isEmpty(lst))
			{
				return null;
			}
			
			Object firstObject = lst.get(0);
			
			if(firstObject instanceof IStep)
			{
				return (IStep) firstObject;
			}
			
			return null;
		}
		
		if(executable instanceof IStepContainer)
		{
			List<IStep> steps = ((IStepContainer) executable).getSteps();

			if(CollectionUtils.isEmpty(steps))
			{
				return null;
			}
			
			return steps.get(0);
		}
		
		return null;
	}
	
	private void setDebugPointOnFirstStep()
	{
		IStep firstStep = fetchFirstStep();
		
		if(firstStep == null)
		{
			return;
		}
		
		DebugFlowManager debugFlowManager = DebugFlowManager.getInstance();
		debugFlowManager.addSinglePauseDebugPoint(firstStep.getLocation(), firstStep.getLineNumber());
	}
	
	public void execute() throws Exception
	{
		String uniqueStackId = UUID.randomUUID().toString();
		
		while(true)
		{
			try
			{
				executor.execute(executable, uniqueStackId);
				break;
			} catch(DropToStackFrameException ex)
			{
				if(uniqueStackId.equals(ex.getStackElementId()))
				{
					logger.debug("Dropping to execution start of: {}", identity);
					
					if(reloadFunc != null)
					{
						executable = reloadFunc.apply(executable);
					}
					
					setDebugPointOnFirstStep();
					continue;
				}
				
				throw ex;
			}
		}

	}
}
