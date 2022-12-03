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
package com.yukthitech.autox.test.ui.steps;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Waits for locator to be part of the page and is visible.
 * @author akiran
 */
@Executable(name = "uiWaitFor", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Waits for (at least one) specified element to become visible/hidden")
public class WaitForStep extends AbstractParentUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator to wait for.
	 */
	@Param(description = "Locator(s) of the element to be waited for", sourceType = SourceType.UI_LOCATOR)
	private List<String> locators;
	
	/**
	 * If true, this step waits for element with specified locator gets removed or hidden.
	 */
	@Param(description = "If true, this step waits for element with specified locator gets removed or hidden.\nDefault: false", required = false)
	private String hidden = "false";
	
	
	/**
	 * Number of retries to happen. Default: 5
	 */
	@Param(description = "Number of retries to happen. Default: 5", required = false)
	private int retryCount = 60;
	
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
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Waiting for element '{}' to become {}", locators, "true".equals(hidden) ? "Invisible" : "Visible");
		
		try
		{
			UiAutomationUtils.validateWithWait(() -> 
			{
				for(String locator : this.locators)
				{
					WebElement element = null;
					
					try
					{
						element = UiAutomationUtils.findElement(driverName, parentElement, locator);
					}catch(Exception ex)
					{
						if(UiAutomationUtils.isElementNotAvailableException(ex))
						{
							exeLogger.debug("Found locator '{}' to be not accessible or available. Hence assuming element is not available. Error: {}", locator, "" + ex);
							element = null;
						}
						else
						{
							exeLogger.error("An error occurred while trying to find element with locator - {}. Error: {}", locator, "" + ex);
							return false;
						}
					}
					
					//if element needs to be checked for invisibility
					try
					{
						if("true".equals(hidden))
						{
							if(element == null || !element.isDisplayed())
							{
								exeLogger.debug("Found locator '{}' to be hidden", getLocatorWithParent(locator));
								return true;
							}
						}
						//if element needs to be checked for visibility
						else if(element != null && element.isDisplayed())
						{
							exeLogger.debug("Found locator '{}' to be visible.", getLocatorWithParent(locator));
							return true;
						}
					}catch(StaleElementReferenceException ex)
					{
						exeLogger.debug("Locator '{}' check resulted in stale exception. Which is going to be ignored", getLocatorWithParent(locator));
					}
				}
				
				return false;
			}, retryCount, retryTimeGapMillis, "Waiting for element: " + locators, 
				new InvalidStateException("Failed to find element - " + locators));
			
		} catch(InvalidStateException ex)
		{
			//exeLogger.error(ex, ex.getMessage());
			throw new TestCaseFailedException(this, ex.getMessage(), ex);
		}
	}

	/**
	 * Gets the locator to wait for.
	 *
	 * @return the locator to wait for
	 */
	public List<String> getLocators()
	{
		return locators;
	}
	
	/**
	 * Sets the locator to wait for.
	 *
	 * @param locator the new locator to wait for
	 */
	public void addLocator(String locator)
	{
		if(this.locators == null)
		{
			this.locators = new ArrayList<>();
		}
		
		this.locators.add(locator);
	}
	
	/**
	 * Sets the if true, this step waits for element with specified locator gets removed or hidden.
	 *
	 * @param hidden the new if true, this step waits for element with specified locator gets removed or hidden
	 */
	public void setHidden(String hidden)
	{
		this.hidden = hidden;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Waiting For [");

		builder.append("Locators: ").append(locators);
		builder.append(",").append("Hidden: ").append(hidden);
		builder.append(",").append("Retry Count: ").append(retryCount);
		builder.append(",").append("Retry Gap: ").append(retryTimeGapMillis);

		builder.append("]");
		return builder.toString();
	}
}
