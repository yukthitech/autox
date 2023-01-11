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
package com.yukthitech.autox.plugin.ui.steps;

import com.yukthitech.autox.Param;

/**
 * Base abstract class for ui steps with optional parent element.
 * @author akiran
 */
public abstract class AbstractParentUiStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
	 */
	@Param(description = "Name of the parent element under which locator needs to be searched. If not specified, fetches globally.", required = false)
	protected String parentElement;

	/**
	 * Sets the name of the parent element under which locator needs to be searched. If not specified, fetches globally.
	 *
	 * @param parentElement the new name of the parent element under which locator needs to be searched
	 */
	public void setParentElement(String parentElement)
	{
		this.parentElement = parentElement;
	}
	
	/**
	 * Fetches string which includes specified locator and parent locatory, if any.
	 * @param locator locator to format
	 * @return locator string along with parent.
	 */
	protected String getLocatorWithParent(String locator)
	{
		return getLocatorWithParent(parentElement, locator);
	}

	protected static String getLocatorWithParent(String parentElement, String locator)
	{
		if(parentElement != null)
		{
			return String.format("[Locator: %s, Parent: %s]", locator, parentElement);
		}
		
		return String.format("[Locator: %s]", locator);
	}
}
