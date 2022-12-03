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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to capture the expected exception details for a test case.
 * @author akiran
 */
public class ExpectedException implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expected property of the test case.
	 * @author akiran
	 */
	public static class Property implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Name of the property.
		 */
		private String name;
		
		/**
		 * Value of the property.
		 */
		private String value;

		/**
		 * Sets the name of the property.
		 *
		 * @param name the new name of the property
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Sets the value of the property.
		 *
		 * @param value the new value of the property
		 */
		public void setValue(String value)
		{
			this.value = value;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder(name);
			builder.append(" = ").append(value);
			
			return builder.toString();
		}
	}
	
	/**
	 * Type of exception expected.
	 */
	@Param(description = "Type of exception expected.")
	private String type;
	
	/**
	 * Expected properties (key-value) pairs of the exception.
	 */
	@Param(description = "Expected properties (key-value) pairs of the exception.", required = false)
	private List<Property> properties = new ArrayList<>();
	
	/**
	 * Flag indicating if expected exception is enabled or not. Useful for enabling/disabling with data provider using expressions.
	 */
	@Param(description = "Flag indicating if expected exception is enabled or not. Useful for enabling/disabling with data provider using expressions.\nDefault: true", required = false)
	private String enabled = "true";
	
	/**
	 * Sets the type of exception expected.
	 *
	 * @param type the new type of exception expected
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	/**
	 * Gets the type of exception expected.
	 *
	 * @return the type of exception expected
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Adds expected property to this exception details.
	 * @param property property to add
	 */
	public void addProperty(Property property)
	{
		this.properties.add(property);
	}
	
	/**
	 * Sets the flag indicating if expected exception is enabled or not. Useful for enabling/disabling with data provider using expressions.
	 *
	 * @param enabled the new flag indicating if expected exception is enabled or not
	 */
	public void setEnabled(String enabled)
	{
		this.enabled = enabled;
	}
	
	/**
	 * Gets the flag indicating if expected exception is enabled or not. Useful for enabling/disabling with data provider using expressions.
	 *
	 * @return the flag indicating if expected exception is enabled or not
	 */
	public String getEnabled()
	{
		return enabled;
	}
	
	/**
	 * Validates the provided exception is matching with current expected exception.
	 * @param ex Actual exception object.
	 */
	public void validateMatch(Exception ex)
	{
		Class<?> type = null;
		
		try
		{
			type = Class.forName(this.type);
		}catch(Exception rex)
		{
			throw new InvalidStateException("Invalid class name specified for exception type - {}", this.type);
		}
		
		if(!Exception.class.isAssignableFrom(type))
		{
			throw new InvalidArgumentException("Invalid exception type specified: {}", type.getName());
		}

		if(!type.equals(ex.getClass()))
		{
			throw new InvalidArgumentException(ex, "Expected exception type {} and actual exception type are not matching", type.getName(), ex.getClass().getName());
		}
		
		String actualValue = null;
		
		for(Property prop : properties)
		{
			try
			{
				actualValue = BeanUtils.getProperty(ex, prop.name);
			}catch(Exception bex)
			{
				throw new InvalidArgumentException(bex, "An error occurred while fetching property '{}' of exception: {}", prop.name, ex.getClass().getName());
			}
			
			if(prop.value == null)
			{
				if(actualValue != null)
				{
					throw new InvalidArgumentException(ex, "Property value of exception is not matching expected value [Exception: {}, Property: {}, Actual Value: {}, Expected Value: {}]", 
							type.getName(), prop.name, actualValue, prop.value);
				}
			}
			else if(!prop.value.equals(actualValue))
			{
				throw new InvalidArgumentException(ex, "Property value of exception is not matching expected value [Exception: {}, Property: {}, Actual Value: {}, Expected Value: {}]", 
						type.getName(), prop.name, actualValue, prop.value);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public ExpectedException clone()
	{
		return AutomationUtils.deepClone(this);
	}
}
