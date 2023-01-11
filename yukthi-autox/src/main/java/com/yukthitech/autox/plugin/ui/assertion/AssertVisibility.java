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

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Waits for locator to be part of the page and is visible.
 * 
 * @author akiran
 */
@Executable(name = "uiAssertVisibility", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Validates specified element is visible/hidden")
public class AssertVisibility extends AbstractParentUiAssert
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * locator to wait for.
	 */
	@Param(description = "Locator of the element to validate", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	/**
	 * If true, this step waits for element with specified locator gets removed
	 * or hidden.
	 */
	@Param(description = "Flag indicating if the validation is for visibility or invisibility.\nDefault: true", required = false)
	private String visible = "true";

	/**
	 * Message expected in the target element.
	 */
	@Param(description = "Message expected in the target element.", required = false)
	private String message = null;
	
	/**
	 * Number of retries to happen. Default: 5
	 */
	@Param(description = "Number of retries to happen. Default: 5", required = false)
	private int retryCount = 5;
	
	/**
	 * Time gap between retries.
	 */
	@Param(description = "Time gap between retries. Default: 1000", required = false)
	private int retryTimeGapMillis = IAutomationConstants.ONE_SECOND;

	/**
	 * Sets the number of retries to happen. Default: 5.
	 *
	 * @param retryCount the new number of retries to happen
	 */
	public void setRetryCount(int retryCount)
	{
		this.retryCount = retryCount;
	}
	
	/**
	 * Sets the time gap between retries.
	 *
	 * @param retryTimeGapMillis the new time gap between retries
	 */
	public void setRetryTimeGapMillis(int retryTimeGapMillis)
	{
		this.retryTimeGapMillis = retryTimeGapMillis;
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
	 * Gets the if true, this step waits for element with specified locator gets removed or hidden.
	 *
	 * @return the if true, this step waits for element with specified locator gets removed or hidden
	 */
	public String getVisible()
	{
		return visible;
	}

	/**
	 * Sets the if true, this step waits for element with specified locator gets removed or hidden.
	 *
	 * @param visible the new if true, this step waits for element with specified locator gets removed or hidden
	 */
	public void setVisible(String visible)
	{
		this.visible = visible;
	}

	/**
	 * Gets the message expected in the target element.
	 *
	 * @return the message expected in the target element
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message expected in the target element.
	 *
	 * @param message
	 *            the new message expected in the target element
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

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
		
		exeLogger.trace("Checking for element Visibility is {}", locator, "true".equals(visible) ? "Visible" : "Invisible");
		
		UiAutomationUtils.validateWithWait(() -> 
		{
			WebElement element = UiAutomationUtils.findElement(driverName, parentElement, locator);

			if(!"true".equals(visible))
			{
				return (element == null || !element.isDisplayed());
			}

			return (element != null && element.isDisplayed());
		} , retryCount, retryTimeGapMillis, 
				"Waiting for element: " + getLocatorWithParent(locator), 
				new InvalidStateException("Failed to find element - " + getLocatorWithParent(locator)));

		if(message != null)
		{
			WebElement element = UiAutomationUtils.findElement(driverName, parentElement, locator);
			String actualMessage = element.getText().trim();

			if(actualMessage == null || !actualMessage.contains(message))
			{
				exeLogger.error("Expected message '{}' is not matching with actual message '{}' for locator: {}", message, actualMessage, getLocatorWithParent(locator));

				throw new AutoxValidationException(this, "Expected message '{}' is not matching with actual message '{}' for locator: {}", 
						message, actualMessage, getLocatorWithParent(locator));
			}
		}
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
		builder.append("Assert Visibility [");

		builder.append("Locator: ").append(locator);
		builder.append(comma).append("Visible: ").append(visible);
		builder.append(comma).append("Message: ").append(message);

		builder.append("]");
		return builder.toString();
	}
}
