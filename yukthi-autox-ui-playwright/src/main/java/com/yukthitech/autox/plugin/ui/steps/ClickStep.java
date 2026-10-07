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

import java.util.concurrent.atomic.AtomicInteger;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;
import com.yukthitech.autox.plugin.ui.common.UiElement;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = "uiClick", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Clicks the specified target")
public class ClickStep extends AbstractPostCheckStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator for button.
	 */
	@Param(description = "Locator of the element to be clicked. Out of located elements, first element will be clicked.", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * Number of retries to happen. Default: 5
	 */
	@Param(description = "Number of retries to happen. Default: 10", required = false)
	private int retryCount = 10;
	
	/**
	 * Time gap between retries.
	 */
	@Param(description = "Time gap between retries. Default: 1000", required = false)
	private int retryTimeGapMillis = IAutomationConstants.ONE_SECOND;
	
	@Param(description = "Flag to enforce clicking using js instead of playwright click", required = false)
	private boolean clickByJs = false;
	
	@Param(description = "Default: true. When true, if click by playwright fails, js code will be tried to click the locator.", required = false)
	private boolean tryJsOnError = true;
	
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
	
	public void setClickByJs(boolean clickByJs)
	{
		this.clickByJs = clickByJs;
	}
	
	public void setTryJsOnError(boolean tryJsOnError)
	{
		this.tryJsOnError = tryJsOnError;
	}
	
	private boolean clickWithJs(UiElement uiElement, AutomationContext context, IExecutionLogger exeLogger)
	{
		uiElement.clickByJs();
		return doPostCheck(context, exeLogger, "Post Click");
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Clicking the element specified by locator: {}", locator);

		try
		{
			AtomicInteger clickedAtleastOnce = new AtomicInteger(-1);
			
			UiAutomationUtils.validateWithWait(() -> 
			{
				try
				{
					UiElement uiElement = UiAutomationUtils.findVisibleElement(driverName, super.parentElement, locator, exeLogger);

					if(uiElement == null)
					{
						return false;
					}
					
					clickedAtleastOnce.incrementAndGet();

					//if at least once button is clicked
					if(clickedAtleastOnce.get() > 0)
					{
						if(super.isPostCheckAvailable() && doPostCheck(context, exeLogger, "Before re-click"))
						{
							exeLogger.trace("Before re-click as post check is successful, skipping re-click");
							return true;
						}
					}
					
					exeLogger.trace("Trying to click element specified by locator: {}", locator);
					
					try
					{
						if(clickByJs)
						{
							exeLogger.debug("As per config clicking the target element using js..");
							return clickWithJs(uiElement, context, exeLogger);
						}
						else
						{
							uiElement.click();	
						}
					}catch(RuntimeException ex)
					{
						//if second click is also resulted in error, try js way of clicking.
						if(tryJsOnError && clickedAtleastOnce.get() > 0)
						{
							exeLogger.debug(
									"Click element failed with error twice or more than twice. So trying to click the element using JS. Error: {} "
									, "" + ex);
							
							return clickWithJs(uiElement, context, exeLogger);
						}
						
						throw ex;
					}
					
					//after click check the post-check and return result approp
					return doPostCheck(context, exeLogger, "Post Click");
				} catch(RuntimeException ex)
				{
					exeLogger.debug("IGNORED: An error occurred while clicking locator - {}. Error: {}", locator,  "" + ex);
					
					if(UiAutomationUtils.isElementNotAvailableException(ex))
					{
						return false;
					}
	
					throw ex;
				}
			} , retryCount, retryTimeGapMillis,
					"Waiting for element to be clickable: " + getLocatorWithParent(locator), 
					new InvalidStateException("Failed to click element - " + getLocatorWithParent(locator)));
		}catch(InvalidStateException ex)
		{
			throw new TestCaseFailedException(this, "Failed to click element - {}", getLocatorWithParent(locator), ex);
		}
	}

	/**
	 * Sets the locator for button.
	 *
	 * @param locator
	 *            the new locator for button
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Click [");

		builder.append("Locator: ").append(locator);

		builder.append("]");
		return builder.toString();
	}
}
