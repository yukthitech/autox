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
package com.yukthitech.autox.test.ui.assertion;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;

/**
 * Waits for locator to be part of the page and is visible.
 * 
 * @author akiran
 */
@Executable(name = "uiAssertValue", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Validates specified element has specified value/text")
public class UiAssertValue extends AbstractUiAssert
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator of which value needs to be validated.
	 */
	@Param(description = "Locator of the element to be validated.", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	/**
	 * Value expected in the target element.
	 */
	@Param(description = "Expected value of the element.")
	private String value;

	/**
	 * Execute.
	 *
	 * @param context
	 *            the context
	 * @param exeLogger
	 *            the exe logger
	 * @return true, if successful
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		if(!"true".equals(enabled))
		{
			exeLogger.debug("Current validation is disabled. Skipping validation execution.");
			return;
		}
		
		exeLogger.trace("Validating if locator '{}' has value - {}", getLocatorWithParent(locator), value);
		
		WebElement element = UiAutomationUtils.findElement(driverName, parentElement, locator);
		String actualMessage = null;
		
		if("input".equals(element.getTagName().toLowerCase()))
		{
			actualMessage = element.getAttribute("value").trim();
		}
		else
		{
			actualMessage = element.getText().trim();
		}

		if(!value.equals(actualMessage))
		{
			exeLogger.error("Expected value '{}' is not matching with actual value '{}' for locator: {}", value, actualMessage, getLocatorWithParent(locator));
			throw new AutoxValidationException(this, "Expected value '{}' is not matching with actual value '{}' for locator: {}", value, actualMessage, getLocatorWithParent(locator));
		}
	}

	/**
	 * Gets the locator to wait for.
	 *
	 * @return the locator to wait for
	 */
	public String getLocator()
	{
		return locator;
	}

	/**
	 * Sets the locator to wait for.
	 *
	 * @param locator
	 *            the new locator to wait for
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Gets the value expected in the targer element.
	 *
	 * @return the value expected in the targer element
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value expected in the targer element.
	 *
	 * @param value the new value expected in the targer element
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String comma = ", ";
		StringBuilder builder = new StringBuilder();
		builder.append("Assert Value [");

		builder.append("Locator: ").append(locator);
		builder.append(comma).append("Value: ").append(value);

		builder.append("]");
		return builder.toString();
	}
}
