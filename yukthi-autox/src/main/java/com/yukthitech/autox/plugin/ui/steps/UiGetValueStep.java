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

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.common.UiFreeMarkerMethods;

/**
 * Waits for locator to be part of the page and is visible.
 * @author akiran
 */
@Executable(name = "uiGetValue", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Fetches value of specified ui element")
public class UiGetValueStep extends AbstractParentUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the element for which value needs to be fetched.
	 */
	@Param(description = "Locator of the element for which value needs to be fetched", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * Name of attribute to set.
	 */
	@Param(description = "Name of the attribute to set.", attrName = true)
	private String name;

	@Param(description = "If set to true, instead of value, display value will be fetched (currently non-select fields will return value itself).", required = false)
	private boolean displayValue = false;
	
	/**
	 * Sets the locator of the element for which value needs to be fetched.
	 *
	 * @param locator the new locator of the element for which value needs to be fetched
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Sets the name of attribute to set.
	 *
	 * @param name the new name of attribute to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Sets the display value.
	 *
	 * @param displayValue the new display value
	 */
	public void setDisplayValue(boolean displayValue)
	{
		this.displayValue = displayValue;
	}

	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Fetching ui element value for locator - {} [Display Value Flag: {}]", getLocatorWithParent(locator), displayValue);
		
		String elementValue = displayValue? 
				UiFreeMarkerMethods.uiDisplayValue(locator, parentElement, driverName) : 
				UiFreeMarkerMethods.uiValue(locator, parentElement, driverName);
		
		exeLogger.debug("Setting context attribute '{}' with value of loctor '{}'. Value of locator was found to be: {}", name, getLocatorWithParent(locator), elementValue);
		context.setAttribute(name, elementValue);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Get Value [");

		builder.append("Locator: ").append(locator);
		builder.append(",").append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}
}
