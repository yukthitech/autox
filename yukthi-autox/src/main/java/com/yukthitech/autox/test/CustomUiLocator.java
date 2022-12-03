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

import com.yukthitech.autox.AbstractLocationBased;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Custom ui locator used to customized way of handling ui 
 * elements in an app.
 * @author akiran
 */
public class CustomUiLocator extends AbstractLocationBased
{
	/**
	 * Name of this locator. The same has to be used as prefix in locators.
	 */
	private String name;
	
	/**
	 * Description about this locator.
	 */
	private String description;
	
	/**
	 * Function to get value out of this type of locator.
	 */
	private Function getter;
	
	/**
	 * Function to set value into this type of locator.
	 */
	private Function setter;
	
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

	/**
	 * Gets the description about this locator.
	 *
	 * @return the description about this locator
	 */
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

	/**
	 * Gets the function to get value out of this type of locator.
	 *
	 * @return the function to get value out of this type of locator
	 */
	public Function getGetter()
	{
		return getter;
	}

	/**
	 * Sets the function to get value out of this type of locator.
	 *
	 * @param getter the new function to get value out of this type of locator
	 */
	public void setGetter(Function getter)
	{
		this.getter = getter;
	}

	/**
	 * Gets the function to set value into this type of locator.
	 *
	 * @return the function to set value into this type of locator
	 */
	public Function getSetter()
	{
		return setter;
	}

	/**
	 * Sets the function to set value into this type of locator.
	 *
	 * @param setter the new function to set value into this type of locator
	 */
	public void setSetter(Function setter)
	{
		this.setter = setter;
	}
	
	public boolean setValue(String query, Object value)
	{
		try
		{
			AutomationContext context = AutomationContext.getInstance();
			Object resObj = setter.execute(context, context.getExecutionLogger());
			
			if(resObj != null && "false".equalsIgnoreCase(resObj.toString()))
			{
				return false;
			}
			
			return true;
		} catch(RuntimeException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while trying to set value", ex);
		}
	}
	
	public String getValue(String query)
	{
		try
		{
			AutomationContext context = AutomationContext.getInstance();
			Object resObj = getter.execute(context, context.getExecutionLogger());
			
			if(resObj != null)
			{
				return resObj.toString();
			}
			
			return null;
		} catch(RuntimeException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while trying to get value", ex);
		}
	}
}
