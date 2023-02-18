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

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepListener;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ExecutionStack;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.DropToStackFrameException;
import com.yukthitech.autox.test.lang.steps.LangException;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.event.EventListenerManager;

/**
 * Executor for steps.
 * @author akranthikiran
 */
public class StepsExecutor
{
	private static Logger logger = LogManager.getLogger(StepsExecutor.class);
	
	private static ThreadLocal<Boolean> topLevelStep = new ThreadLocal<>();
	
	private static EventListenerManager<IStepListener> stepListeners = EventListenerManager.newEventListenerManager(IStepListener.class, false);
	
	public static void addStepListener(IStepListener listener)
	{
		stepListeners.addListener(listener);
	}
	
	public static void removeStepListener(IStepListener listener)
	{
		stepListeners.removeListener(listener);
	}
	
	private static boolean isTopLevel()
	{
		Boolean val = topLevelStep.get();
		
		if(val == null)
		{
			topLevelStep.set(Boolean.TRUE);
			return true;
		}
		
		return false;
	}
	
	private static void clearTopLevel()
	{
		topLevelStep.remove();
	}
	
	/**
	 * Executes specified step/validation. In case of validations, ensures result is true, in not {@link AutoxValidationException} will 
	 * be thrown.
	 * @param context context to be used
	 * @param exeLogger logger to be used
	 * @param step step to be executed
	 */
	private static void executeStep(IStep sourceStep, String parentFrameId) throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		IExecutionLogger exeLogger = context.getExecutionLogger();
		
		//if step is marked not to log anything
		if(sourceStep.isLoggingDisabled())
		{
			//disable logging
			exeLogger.setDisabled(true);
		}
		
		//clone the step, so that expression replacement will not affect actual step
		IStep step = sourceStep.clone();
		step.setSourceStep(sourceStep);

		ExecutionStack executionStack = ExecutionContextManager.getInstance().getExecutionStack();
		
		executionStack.push(step, parentFrameId);

		try
		{
			DebugFlowManager.getInstance().checkForDebugPoint(step);

			AutomationUtils.replaceExpressions("step-" + step.getClass().getName(), context, step);

			stepListeners.get().stepStarted(step);
			
			step.execute(context, exeLogger);
			
			stepListeners.get().stepCompleted(step);
		} catch(HandledException | LangException | DropToStackFrameException ex)
		{
			//already handled exception and lang exception should be thrown
			// without logging
			throw ex;
		} catch(Exception ex)
		{
			//log the unhandled exception
			stepListeners.get().stepErrored(step, ex);
			String stackTrace = executionStack.toStackTrace();
			
			if(ex instanceof AutoxValidationException)
			{
				logger.error("Validation error occurred during step execution. Error: {}\n{}", ex.toString(), stackTrace);
			}
			else
			{
				logger.error("An error occurred with message at stack trace: \n{}", stackTrace, ex);				
			}
			
			exeLogger.error("Execution result in error: \n{}.\nStack Trace:{}", CommonUtils.getRootCauseMessages(ex), stackTrace);
			
			throw new HandledException(ex);
		}finally
		{
			executionStack.pop(step);
			
			//re-enable logging, in case it is disabled
			exeLogger.setDisabled(false);
		}

	}
	
	public static void execute(List<IStep> steps, ObjectWrapper<IStep> currentStep, String parentFrameId) throws Exception
	{
		if(CollectionUtils.isEmpty(steps))
		{
			return;
		}
		
		boolean topLevel = isTopLevel();
		
		try
		{
			for(IStep step : steps)
			{
				if(currentStep != null)
				{
					currentStep.setValue(step);
				}
				
				executeStep(step, parentFrameId);
			}
		}catch(HandledException ex)
		{
			//only top level should unwrap the exception and throw it back
			// only deepest level will log error
			//  rest of levels will ignore this exception and rethrow
			if(topLevel)
			{
				throw (Exception) ex.getCause();
			}
			
			throw ex;
		} finally
		{
			if(topLevel)
			{
				clearTopLevel();
			}
		}
	}
}
