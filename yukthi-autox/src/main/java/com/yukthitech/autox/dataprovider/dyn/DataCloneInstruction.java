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
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Clone instruction that reloads a bean value expression at runtime.
 */
public class DataCloneInstruction implements IDataValueInstruction, Validateable
{
	private String beanId;
	
	private String property;
	
	public void setBeanId(String beanId)
	{
		this.beanId = beanId;
	}
	
	public String getBeanId()
	{
		return beanId;
	}
	
	public void setProperty(String property)
	{
		this.property = property;
	}
	
	public String getProperty()
	{
		return property;
	}
	
	@Override
	public void execute(AutomationContext context, PrefixExpressionFactory exprFactory,
			Map<String, DataProviderBean> beanRegistry, LinkedHashMap<String, Object> result)
	{
		DataProviderBean bean = beanRegistry.get(beanId);
		
		if(bean == null)
		{
			throw new InvalidArgumentException("Invalid/no bean id specified for clone: {}", beanId);
		}
		
		if(StringUtils.isBlank(property))
		{
			throw new InvalidArgumentException("No property name specified for clone of bean: {}", beanId);
		}

		try
		{
			Object value = exprFactory.getValueByExpressionString(context, bean.getValue());
			result.put(property, value);
		}catch(Exception ex)
		{
			if(ex instanceof HandledException)
			{
				ex = (Exception) ex.getCause();
			}
			
			throw new InvalidStateException("An error ocurred during data clone", ex);
		}
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(beanId))
		{
			throw new ValidateException("Bean id is required");
		}

		if(StringUtils.isBlank(property))
		{
			throw new ValidateException(String.format("Property name is required for clone of bean: %s", beanId));
		}
	}
}
