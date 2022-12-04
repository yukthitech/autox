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
package com.yukthitech.autox.plugin.ui.assertion;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.Param;

/**
 * Base abstract class for ui validations, which will hold optional parent element.
 * @author akiran
 */
public abstract class AbstractUiAssert extends AbstractValidation
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
	 */
	@Param(description = "Name of the parent element under which locator needs to be searched. If not specified, fetches globally.", required = false)
	protected String parentElement;
	
	@Param(description = "Name of the driver to be used for the step. Defaults to default driver.", required = false)
	protected String driverName;

	/**
	 * Sets the name of the parent element under which locator needs to be searched. If not specified, fetches globally.
	 *
	 * @param parentElement the new name of the parent element under which locator needs to be searched
	 */
	public void setParentElement(String parentElement)
	{
		this.parentElement = parentElement;
	}
	
	public void setDriverName(String driverName)
	{
		this.driverName = driverName;
	}
	
	/**
	 * Fetches string which includes specified locator and parent locatory, if any.
	 * @param locator locator to format
	 * @return locator string along with parent.
	 */
	protected String getLocatorWithParent(String locator)
	{
		if(parentElement != null)
		{
			return String.format("[Locator: {}, Parent: {}]", locator, parentElement);
		}
		
		return String.format("[Locator: {}]", locator);
	}
}
