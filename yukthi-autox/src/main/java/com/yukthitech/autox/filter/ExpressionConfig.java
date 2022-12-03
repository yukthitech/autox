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
package com.yukthitech.autox.filter;

/**
 * Custom configuration that can be set during expression parsing.
 * @author akiran
 */
public class ExpressionConfig
{
	/**
	 * Initial value that can be passed to first expression part.
	 */
	private Object initValue;
	
	/**
	 * Default expected type for expression result type. 
	 */
	private Class<?> defaultExpectedType;

	/**
	 * Instantiates a new expression config.
	 *
	 * @param initValue the init value
	 * @param defaultExpectedType the default expected type
	 */
	public ExpressionConfig(Object initValue, Class<?> defaultExpectedType)
	{
		this.initValue = initValue;
		this.defaultExpectedType = defaultExpectedType;
	}

	/**
	 * Gets the initial value that can be passed to first expression part.
	 *
	 * @return the initial value that can be passed to first expression part
	 */
	public Object getInitValue()
	{
		return initValue;
	}

	/**
	 * Sets the initial value that can be passed to first expression part.
	 *
	 * @param initValue the new initial value that can be passed to first expression part
	 */
	public void setInitValue(Object initValue)
	{
		this.initValue = initValue;
	}

	/**
	 * Gets the default expected type for expression result type.
	 *
	 * @return the default expected type for expression result type
	 */
	public Class<?> getDefaultExpectedType()
	{
		return defaultExpectedType;
	}

	/**
	 * Sets the default expected type for expression result type.
	 *
	 * @param defaultExpectedType the new default expected type for expression result type
	 */
	public void setDefaultExpectedType(Class<?> defaultExpectedType)
	{
		this.defaultExpectedType = defaultExpectedType;
	}
}
