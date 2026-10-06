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
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;

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
	@Param(description = "Parent element (Webelement or ui-locator or attr-name of parent web-element) under which current operation should be performed. "
			+ "If not specified, fetches globally.", 
			required = false, sourceType = SourceType.EXPRESSION)
	protected Object parentElement;

	/**
	 * Sets the name of the parent element under which locator needs to be searched. If not specified, fetches globally.
	 *
	 * @param parentElement the new name of the parent element under which locator needs to be searched
	 */
	public void setParentElement(Object parentElement)
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

	protected static String getLocatorWithParent(Object parentElement, String locator)
	{
		String locatorType = UiAutomationUtils.getLocatorType(locator);
		
		if(parentElement != null)
		{
			return String.format("[Type: %s, Locator: %s, Parent: %s]", locatorType, locator, parentElement);
		}
		
		return String.format("[Type: %s, Locator: %s]", locatorType, locator);
	}
}
