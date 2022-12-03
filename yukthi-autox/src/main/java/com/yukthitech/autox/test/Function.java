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
package com.yukthitech.autox.test;

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
import com.yukthitech.autox.test.lang.steps.ReturnException;
import com.yukthitech.ccg.xml.IParentAware;

/**
 * Represents group of steps and/or validations. That can be referenced 
 * @author akiran
 */
public class Function extends AbstractLocationBased implements IStepContainer, Cloneable, IEntryPoint, IParentAware
{
	/**
	 * Name of this group.
	 */
	@Param(name = "name", description = "Name of the function", required = true)
	private String name;
	
	/**
	 * Description of the function.
	 */
	@SkipParsing
	@Param(name = "description", description = "Description of the function", required = false)
	private String description;
	
	/**
	 * Description of the return value. This should be omitted for functions which dont return any value.
	 */
	@SkipParsing
	@Param(name = "returnDescription", 
		description = "Return description of the function. If not specified, function is assumed will not return any value", 
		required = false)
	private String returnDescription;
	
	/**
	 * Parameter definitions of the function.
	 */
	@SkipParsing
	private List<FunctionParamDef> parameterDefs;
	
	/**
	 * Steps for the test case.
	 */
	@SkipParsing
	private List<IStep> steps = new ArrayList<>();
	
	/**
	 * Params for step group execution. This are expected to be set by step-group-ref
	 */
	private Map<String, Object> params;
	
	/**
	 * Optional data provider for the step.
	 */
	@SkipParsing
	private IDataProvider dataProvider;

	@SkipParsing
	private Object parent;
	
	@Override
	public void setParent(Object parent)
	{
		this.parent = parent;
	}

	@Override
	public String toText()
	{
		return parent + "." + name;
	}
	
	/**
	 * Sets the name of this group.
	 *
	 * @param name the new name of this group
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the name of this group.
	 *
	 * @return the name of this group
	 */
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the function.
	 *
	 * @param description the new description of the function
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Adds the specified parameter def to this function.
	 * @param def def to add.
	 */
	public void addParamDef(FunctionParamDef def)
	{
		if(this.parameterDefs == null)
		{
			this.parameterDefs = new ArrayList<>();
		}
		
		this.parameterDefs.add(def);
	}

	/**
	 * Sets the parent where this function is defined.
	 *
	 * @param parent the new parent where this function is defined
	 */
	public void setParent(String parent)
	{
		this.parent = parent;
	}
	
	void setParams(Map<String, Object> params)
	{
		this.params = params;
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
	
	public Object execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		context.pushParameters(params);
		
		ExecutionStack executionStack = ExecutionContextManager.getInstance().getExecutionStack();
		executionStack.push(this);
		
		try
		{
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
	public Function clone()
	{
		try
		{
			return (Function) super.clone();
		} catch (CloneNotSupportedException ex)
		{
			throw new IllegalStateException(ex);
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#getDataProvider()
	 */
	public IDataProvider getDataProvider()
	{
		return dataProvider;
	}

	/**
	 * Sets the optional data provider for the step.
	 *
	 * @param dataProvider the new optional data provider for the step
	 */
	public void setDataProvider(IDataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}

	/**
	 * Sets the specified list data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setListDataProvider(ListDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}
	
	/**
	 * Sets the specified range data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setRangeDataProvider(RangeDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}
}
