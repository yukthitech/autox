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
package com.yukthitech.autox.doc;

import java.io.Serializable;

/**
 * Abstract class for doc info objects to hold common properties.
 * @author akiran
 */
public class AbstractDocInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Documentation generated for current info object.
	 */
	private String documentation;
	
	/**
	 * Additional information about the element.
	 */
	private String additionalInfo;

	/**
	 * Gets the documentation generated for current info object.
	 *
	 * @return the documentation generated for current info object
	 */
	public String getDocumentation()
	{
		return documentation;
	}

	/**
	 * Sets the documentation generated for current info object.
	 *
	 * @param documentation the new documentation generated for current info object
	 */
	public void setDocumentation(String documentation)
	{
		this.documentation = documentation;
	}
	
	/**
	 * Sets the additional information about the element.
	 *
	 * @param additionalInfo
	 *            the new additional information about the element
	 */
	public void setAdditionalInfo(String additionalInfo)
	{
		this.additionalInfo = additionalInfo;
	}
	
	/**
	 * Gets the additional information about the element.
	 *
	 * @return the additional information about the element
	 */
	public String getAdditionalInfo()
	{
		return additionalInfo;
	}

	/**
	 * Checks for return info.
	 *
	 * @return true, if successful
	 */
	public boolean hasReturnInfo()
	{
		return false;
	}
	
	/**
	 * Checks for parameters.
	 *
	 * @return true, if successful
	 */
	public boolean hasParameters()
	{
		return false;
	}
}
