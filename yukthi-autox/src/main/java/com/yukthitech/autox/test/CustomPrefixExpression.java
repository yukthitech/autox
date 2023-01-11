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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractLocationBased;
import com.yukthitech.autox.IStepIntegral;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.prefix.PrefixExpressionContext;
import com.yukthitech.autox.test.lang.steps.FailException;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.doc.Doc;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to define custom prefix expressions (for ui also).
 * @author akiran
 */
public class CustomPrefixExpression extends AbstractLocationBased implements Validateable, IStepIntegral
{
	public static class ExprFunction implements IStepContainer
	{
		/**
		 * Steps for the function.
		 */
		@SkipParsing
		private List<IStep> steps = new ArrayList<>();
		
		public void addStep(IStep step)
		{
			steps.add(step);
		}
		
		@Override
		public List<IStep> getSteps()
		{
			return steps;
		}
	}
	
	/**
	 * Name of this prefix expression.
	 */
	@Doc(value = "Name of this prefix expression.", required = true)
	private String name;
	
	@Doc(value = "Description of this prefix expression.")
	private String description;
	
	/**
	 * Function to get value out of this type of locator.
	 */
	private ExprFunction getFunction;
	
	/**
	 * Function to set value into this type of locator.
	 */
	private ExprFunction setFunction;
	
	/**
	 * Function to remove value using locator.
	 */
	private ExprFunction removeFunction;

	/**
	 * Parameter definitions of the function.
	 */
	@SkipParsing
	private List<FunctionParamDef> parameterDefs;
	
	/**
	 * Gets the name of this locator. The same has to be used as prefix in locators.
	 *
	 * @return the name of this locator
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this locator. The same has to be used as prefix in locators.
	 *
	 * @param name the new name of this locator
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about this locator.
	 *
	 * @param description the new description about this locator
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setGetFunction(ExprFunction getFunction)
	{
		this.getFunction = getFunction;
	}

	public void setSetFunction(ExprFunction setFunction)
	{
		this.setFunction = setFunction;
	}

	public void setRemoveFunction(ExprFunction removeFunction)
	{
		this.removeFunction = removeFunction;
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
	
	private Object executeFunction(PrefixExpressionContext context, String expression, Object value, ExprFunction function)
	{
		try
		{
			Map<String, Object> params = new HashMap<>();
			
			if(context.getParameters() != null)
			{
				params.putAll(context.getParameters());
			}
			
			AutomationContext autoContext = AutomationContext.getInstance();
			
			params.put("context", CommonUtils.toMap(
				"value", value,
				"expression", expression,
				"effectiveExpression", AutomationUtils.getStringValue(context, expression)
			));
			
			return Function.execute(autoContext, params, this, function.getSteps());
		} catch(FailException ex)
		{
			throw new CustomExpressionFailedException("Custom-Custom-prefix-expression '{}' operation failed with message: {} ", name, ex.getMessage(), ex);
		} catch(RuntimeException ex)
		{
			throw ex;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while trying to set value", ex);
		}
	}

	public void setValue(PrefixExpressionContext context, String expression, Object value)
	{
		if(setFunction == null)
		{
			throw new InvalidStateException("Custom-prefix-expression '{}' does not support writing of value", name);
		}
		
		Object resObj = executeFunction(context, expression, value, setFunction);
		
		if(resObj != null && "false".equalsIgnoreCase(resObj.toString()))
		{
			throw new CustomExpressionFailedException("Failed to set value by Custom-Custom-prefix-expression '{}'. Reason: set-function resulted in false value.", name);
		}
	}
	
	public void removeValue(PrefixExpressionContext context, String expression)
	{
		if(removeFunction == null)
		{
			throw new InvalidStateException("Custom-prefix-expression '{}' does not support removal of value", name);
		}
		
		Object resObj = executeFunction(context, expression, null, removeFunction);
		
		if(resObj != null && "false".equalsIgnoreCase(resObj.toString()))
		{
			throw new CustomExpressionFailedException("Failed to remove value by Custom-Custom-prefix-expression '{}'. Reason: remove-function resulted in false value.", name);
		}
	}

	public Object getValue(PrefixExpressionContext context, String expression)
	{
		if(getFunction == null)
		{
			throw new InvalidStateException("Custom-prefix-expression '{}' does not support reading of value", name);
		}
		
		return executeFunction(context, expression, null, getFunction);
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Name can not be blank");
		}
		
		if(setFunction == null && getFunction == null)
		{
			throw new ValidateException("Both setter and getter is not specified.");
		}
		
		if(setFunction != null && CollectionUtils.isEmpty(setFunction.steps))
		{
			throw new ValidateException("No steps are specified for setter.");
		}
		
		if(getFunction != null && CollectionUtils.isEmpty(getFunction.steps))
		{
			throw new ValidateException("No steps are specified for getter.");
		}
	}
}
