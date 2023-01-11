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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;
import com.yukthitech.autox.test.TestCaseFailedException;

/**
 * Fill form with action fills the form and then goes for the provided action.
 * 
 * @author Pritam.
 */
@Executable(name = "uiSetValue", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Populates specified field with specified value")
public class UiSetValueStep extends AbstractParentUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(UiSetValueStep.class);

	/**
	 * Locator of the element to be populated.
	 */
	@Param(description = "Locator of the element to be populated", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	/**
	 * Value to be filled. 
	 */
	@Param(description = "Value to be filled with. Defaults to empty string.", required = false)
	private String value;

	/**
	 * PressEnterAtEnd if true then for the provided action or else ignore.
	 */
	@Param(description = "If true, an enter-key press will be simulated on target element after populating value. Default: false", required = false)
	private boolean pressEnterAtEnd = false;

	/**
	 * Sets the html locator of the form or container (like DIV) enclosing the
	 * input elements.
	 *
	 * @param locator
	 *            the new html locator of the form or container (like DIV)
	 *            enclosing the input elements
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Sets the value to be filled.
	 *
	 * @param value the new value to be filled
	 */
	public void setValue(String value)
	{
		this.value = value;
	}


	/**
	 * Sets value for press enter at the end.
	 * 
	 * @param pressEnterAtEnd
	 *            the new press enter at the end.
	 */
	public void setPressEnterAtEnd(boolean pressEnterAtEnd)
	{
		this.pressEnterAtEnd = pressEnterAtEnd;
	}
	
	public static boolean setUiValue(AutomationContext context, String driver, String parentElement, String locator, String value, boolean pressEnter)
	{
		if(value == null)
		{
			value = "";
		}
		
		if(!UiAutomationUtils.populateField(driver, parentElement, locator, value))
		{
			logger.error("Failed to fill element '{}' with value - {}", getLocatorWithParent(parentElement, locator), value);
			return false;
		}

		if(pressEnter)
		{
			logger.debug( "User has provided enter key to be pressed");
			WebElement webElement = UiAutomationUtils.findElement(driver, parentElement, locator);
			webElement.sendKeys(Keys.ENTER);

			logger.debug("Successfully enter key is pressed");
		}
		
		return true;
	}

	/**
	 * Loops throw the properties specified data bean and populates the fields
	 * with matching names.
	 * 
	 * @param context
	 *            Current automation context
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger)
	{
		logger.debug("Populating field {} with value - {}", getLocatorWithParent(locator), value);
		
		if(!setUiValue(context, driverName, parentElement, locator, value, pressEnterAtEnd))
		{
			throw new TestCaseFailedException(this, "Failed to fill element '{}' with value - {}", getLocatorWithParent(locator), value);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Set Value [");

		builder.append("Locator: ").append(locator);
		builder.append(",").append("Value: ").append(value);

		builder.append("]");
		return builder.toString();
	}

}
