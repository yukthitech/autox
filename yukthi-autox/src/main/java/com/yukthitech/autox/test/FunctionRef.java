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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStackableStep;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.ccg.xml.DynamicDataAcceptor;
import com.yukthitech.ccg.xml.IDynamicAttributeAcceptor;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Reference step to execute target function with specified parameters.
 * @author akiran
 */
@Executable(name = "functionRef", group = Group.Lang, message = "Reference step to execute target function with specified parameters.")
public class FunctionRef extends AbstractStep implements IDynamicAttributeAcceptor, DynamicDataAcceptor, IStackableStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the step group to execute.
	 */
	@Param(description = "Name of the function to execute.")
	private String name;
	
	/**
	 * Parameters for the step group to be executed.
	 */
	@Param(description = "Parameters to be passed to function.", required = false)
	private Map<String, FunctionParam> params;
	
	/**
	 * Attribute name to be used to specify return value. If not specified, return value will be ignored. Default: null.
	 */
	@Param(description = "Attribute name to be used to specify return value. If not specified, return value will be ignored. Default: null", required = false)
	private String returnAttr;
	
	public FunctionRef()
	{}
	
	public FunctionRef(String name, String returnAttr)
	{
		this.name = name;
		this.returnAttr = returnAttr;
	}

	/**
	 * Sets the name of the step group to execute.
	 *
	 * @param name the new name of the step group to execute
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Sets the attribute name to be used to specify return value. If not specified, return value will be ignored. Default: null.
	 *
	 * @param returnAttr the new attribute name to be used to specify return value
	 */
	public void setReturnAttr(String returnAttr)
	{
		if(StringUtils.isEmpty(returnAttr))
		{
			return;
		}
		
		this.returnAttr = returnAttr;
	}
	
	/**
	 * Adds the specified param.
	 * @param param
	 */
	public void addParam(FunctionParam param)
	{
		if(this.params == null)
		{
			this.params = new HashMap<>();
		}
		
		this.params.put(param.getName(), param);
	}
	
	@Override
	public void set(String attrName, String value)
	{
		addParam(new FunctionParam(attrName, value));
	}
	
	@Override
	public void add(String propName, Object obj)
	{
		addParam(new FunctionParam(propName, obj));
	}
	
	@Override
	public void add(String propName, String id, Object obj)
	{
		throw new UnsupportedOperationException("This method is not expected to be called.");
	}
	
	@Override
	public boolean isIdBased(String propName)
	{
		return false;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		Function function = context.getFunction(name);
		
		if(function == null)
		{
			throw new InvalidStateException("No function found with specified name: {}", name);
		}

		Map<String, Object> paramValues = null;

		//build the params for step group to execute
		if(this.params != null)
		{
			paramValues = new HashMap<>();
			
			for(String key : params.keySet())
			{
				paramValues.put(key, params.get(key).getValue());
			}
		}
		
		function = (Function) function.clone();
		
		logger.debug("Executing function '{}' with parameters: {}", name, paramValues);
		
		Object resVal = function.execute(context, paramValues);
		
		if(returnAttr != null)
		{
			logger.debug("Seting return attr '{}' with function return value: {}", returnAttr, resVal);
			context.setAttribute(returnAttr, resVal);
		}
		else if(resVal != null)
		{
			logger.debug("Non-null return value is ignored as no return-attr is set. Return value was: {}", resVal);
		}

	}
	
	@Override
	public IStep clone()
	{
		return AutomationUtils.deepClone(this);
	}
}
