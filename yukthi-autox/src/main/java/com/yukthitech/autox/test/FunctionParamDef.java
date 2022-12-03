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

/**
 * Represents definition of function parameter.
 * @author akiran
 */
public class FunctionParamDef
{
	/**
	 * Name of the function parameter.
	 */
	private String name;
	
	/**
	 * Description of the function parameter.
	 */
	private String description;
	
	/**
	 * Flag indicating if this parameter is required or not. Default is false.
	 */
	private boolean required = false;

	/**
	 * Gets the name of the function parameter.
	 *
	 * @return the name of the function parameter
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the function parameter.
	 *
	 * @param name the new name of the function parameter
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description of the function parameter.
	 *
	 * @return the description of the function parameter
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the function parameter.
	 *
	 * @param description the new description of the function parameter
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the flag indicating if this parameter is required or not. Default is false.
	 *
	 * @return the flag indicating if this parameter is required or not
	 */
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * Sets the flag indicating if this parameter is required or not. Default is false.
	 *
	 * @param required the new flag indicating if this parameter is required or not
	 */
	public void setRequired(boolean required)
	{
		this.required = required;
	}
}
