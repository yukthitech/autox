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
package com.yukthitech.autox.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yukthitech.autox.AbstractLocationBased;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ExecutionStack;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.IEntryPoint;
import com.yukthitech.autox.test.lang.steps.ReturnException;

/**
 * Represents event handler, invoked based on event name specified. 
 * @author akiran
 */
public class EventHandler extends AbstractLocationBased implements IStepContainer, Cloneable, IEntryPoint
{
	/**
	 * Name of the event to handle.
	 */
	@Param(name = "name", description = "Name of the event to handle.", required = true)
	private String name;
	
	/**
	 * Description of the event handler.
	 */
	@SkipParsing
	@Param(name = "description", description = "Description of the event handler.", required = false)
	private String description;
	
	/**
	 * Steps for the test case.
	 */
	@SkipParsing
	private List<IStep> steps = new ArrayList<>();
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toText() 
	{
		return "Event Handler [" + name + "]";
	}

	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}

	/**
	 * Gets the steps for the test case.
	 *
	 * @return the steps for the test case
	 */
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	public Object execute(Map<String, Object> params) throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		ExecutionStack executionStack = ExecutionContextManager.getInstance().getExecutionStack();
		
		context.pushParameters(params);
		executionStack.push(this);
		
		try
		{
			IExecutionLogger logger = AutomationContext.getInstance().getExecutionLogger();
			logger.debug("Executing event handler: {}", name);

			StepsExecutor.execute(steps, null);
		} catch(Exception ex)
		{
			//occurs during return statement execution
			if(ex instanceof ReturnException)
			{
				return ((ReturnException) ex).getValue();
			}
			
			throw ex;
		} finally
		{
			executionStack.pop(this);
			context.popParameters();
		}
		
		return null;
	}
	
	@Override
	public EventHandler clone()
	{
		try
		{
			return (EventHandler) super.clone();
		} catch (CloneNotSupportedException ex)
		{
			throw new IllegalStateException(ex);
		}
	}
}
