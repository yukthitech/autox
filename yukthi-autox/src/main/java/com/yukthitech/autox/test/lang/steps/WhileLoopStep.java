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

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Loops through specified range of values and for each iteration executed underlying steps
 * 
 * @author akiran
 */
@Executable(name = "while", group = Group.Lang, message = "Loops till specified condition is evaluated to true executed underlying steps")
public class WhileLoopStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed in loop.")
	private List<IStep> steps = new ArrayList<IStep>();

	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Freemarker condition to be evaluated.", sourceType = SourceType.CONDITION)
	private String condition;

	/**
	 * Sets the freemarker condition to be evaluated.
	 *
	 * @param condition the new freemarker condition to be evaluated
	 */
	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStepContainer#addStep(com.yukthitech.autox.IStep)
	 */
	@ChildElement(description = "Steps to be executed")
	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}

	@Override
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		int index = -1;
		
		while(AutomationUtils.evaluateCondition(context, condition))
		{
			index++;
			
			//from second time check for debug point for current step
			if(index > 0)
			{
				DebugFlowManager.getInstance().checkForDebugPoint(this);
			}

			try
			{
				StepsExecutor.execute(steps, null, null);
			}catch(Exception ex)
			{
				if(ex instanceof BreakException)
				{
					break;
				}
				
				if(ex instanceof ContinueException)
				{
					continue;
				}
				
				throw ex;
			}
		}
	}
}
