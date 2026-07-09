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
package com.yukthitech.autox.dataprovider.dyn;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.HandledException;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Property instruction in dynamic test-case-data value. Evaluated at runtime via getValue().
 */
public class DataPropertyInstruction implements IDataValueInstruction, Validateable
{
	private String name;

	private String value;
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	@Override
	public void execute(AutomationContext context, PrefixExpressionFactory exprFactory,
			Map<String, DataProviderBean> beanRegistry, LinkedHashMap<String, Object> result)
	{
		try
		{
			Object value = exprFactory.getValueByExpression(context, this.value);
			result.put(name, value);
		}catch(Exception ex)
		{
			if(ex instanceof HandledException)
			{
				ex = (Exception) ex.getCause();
			}
			
			throw new InvalidStateException("An error ocurred during property evaluation", ex);
		}
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Property name is required");
		}

		if(StringUtils.isBlank(value))
		{
			throw new ValidateException("Property value is required");
		}
	}
}
