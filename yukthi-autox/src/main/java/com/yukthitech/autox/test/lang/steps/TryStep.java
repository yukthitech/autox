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
package com.yukthitech.autox.test.lang.steps;

import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.AbstractContainerStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IMultiPartStep;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ExecutionStack;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.exec.HandledException;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Used to enclose steps to catch errors.
 * 
 * @author akiran
 */
@Executable(name = "try", group = Group.Lang, message = "Used to enclose steps to catch errors.")
public class TryStep extends AbstractContainerStep implements IStepContainer, IMultiPartStep
{
	private static final long serialVersionUID = 1L;
	
	private CatchStep catchStep;
	
	@Override
	public void addChildPart(IStep step)
	{
		if(catchStep != null)
		{
			throw new InvalidArgumentException("Multiple catch steps specified for same try-block");
		}
		
		this.catchStep = (CatchStep) step;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		try
		{
			StepsExecutor.execute(steps, null);
		}catch(Exception ex)
		{
			if(catchStep == null)
			{
				throw ex;
			}

			if(ex instanceof LangException)
			{
				throw ex;
			}
			
			if(ex instanceof HandledException)
			{
				ex = (Exception) ex.getCause();
			}

			exeLogger.warn("Exception occurred while executing try-block. Executing catch block. Exception: {}", ex);
			
			ExecutionStack executionStack = ExecutionContextManager.getInstance().getExecutionStack();
			
			executionStack.push(catchStep);
			DebugFlowManager.getInstance().checkForDebugPoint(catchStep);
			
			try
			{
				context.setAttribute(catchStep.getErrorAttr(), ex);
				StepsExecutor.execute(catchStep.getSteps(), null);
			}finally
			{
				executionStack.pop(catchStep);
			}
		}
	}
}
