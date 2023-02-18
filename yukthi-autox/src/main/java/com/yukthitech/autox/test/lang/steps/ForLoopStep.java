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
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.LogLevel;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Loops through specified range of values and for each iteration executed underlying steps
 * 
 * @author akiran
 */
@Executable(name = "for", group = Group.Lang, message = "Loops through specified range of values and for each iteration executed underlying steps")
public class ForLoopStep extends AbstractStep implements IStepContainer
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
	 * Inclusive start of range.
	 */
	@Param(description = "Inclusive start of range.", sourceType = SourceType.EXPRESSION)
	private Object start;
	
	/**
	 * Inclusive end of range.
	 */
	@Param(description = "Inclusive end of range.", sourceType = SourceType.EXPRESSION)
	private Object end;

	/**
	 * Loop variable that will be used to set loop iteration object on context. Default: loopVar.
	 */
	@Param(description = "Loop variable that will be used to set loop iteration object on context. Default: loopVar", required = false,
			attrName = true, defaultValue = "loopVar")
	private String loopVar = "loopVar";

	/**
	 * Sets the inclusive start of range.
	 *
	 * @param start the new inclusive start of range
	 */
	public void setStart(Object start)
	{
		this.start = start;
	}

	/**
	 * Sets the inclusive end of range.
	 *
	 * @param end the new inclusive end of range
	 */
	public void setEnd(Object end)
	{
		this.end = end;
	}
	
	/**
	 * Sets the loop variable that will be used to set loop iteration object on context. Default: loopVar.
	 *
	 * @param loopVar the new loop variable that will be used to set loop iteration object on context
	 */
	public void setLoopVar(String loopVar)
	{
		this.loopVar = loopVar;
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
		int start = 0, end = 0;
		
		try
		{
			start = Integer.parseInt(this.start.toString());
		}catch(Exception ex)
		{
			exeLogger.log(LogLevel.ERROR, "Invalid/non-int-convertable start value specified: {}", this.start);
			throw new InvalidArgumentException("Invalid/non-int-convertable start value specified: {}", this.start);
		}
		
		try
		{
			end = Integer.parseInt(this.end.toString());
		}catch(Exception ex)
		{
			exeLogger.log(LogLevel.ERROR, "Invalid/non-int-convertable end value specified: {}", this.end);
			throw new InvalidArgumentException("Invalid/non-int-convertable end value specified: {}", this.end);
		}
		
		if(start > end)
		{
			return;
		}
		
		int index = -1;
		
		for(int i = start; i <= end; i++)
		{
			index++;
			
			//from second time check for debug point for current step
			if(index > 0)
			{
				DebugFlowManager.getInstance().checkForDebugPoint(this);
			}
			
			context.setAttribute(loopVar, i);
			
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
