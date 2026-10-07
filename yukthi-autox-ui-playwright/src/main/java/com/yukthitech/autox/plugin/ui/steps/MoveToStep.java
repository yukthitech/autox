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

import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.BoundingBox;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;
import com.yukthitech.autox.plugin.ui.common.UiElement;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Moves the mouse over the specified element and optionally clicks it.
 * 
 * @author akiran
 */
@Executable(name = "uiMoveTo", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Moves the view to specified target and optionally clicks the element.")
public class MoveToStep extends AbstractParentUiStep
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * locator for button.
	 */
	@Param(description = "Locator of the element to which view needs to be moved.", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * If true, moves the mouse to target and clicks the element. Default: false.
	 */
	@Param(description = "If true, moves the mouse to target and clicks the element. Default: false", required = false)
	private boolean click = false;
	
	/**
	 * Time gap which will be used before clicking and after moving the mouse over the element.
	 */
	@Param(description = "Time gap (in millis) which will be used before clicking and after moving the mouse over the element. Default: 10", required = false)
	private long timeGap = 10;
	
	/**
	 * Before moving the element, the element will be aligned to document. This flag indicates if alignment should be to top or bottom. Default: true.
	 */
	@Param(description = "Before moving the element, the element will be aligned to document. This flag indicates if alignment should be to top or bottom. Default: true", required = false)
	private boolean alignToTop = true;
	
	
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

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Moving to element specified by locator: {}", locator);

		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);

		try
		{
			UiAutomationUtils.validateWithWait(() -> 
			{
				UiElement uiElement = UiAutomationUtils.findVisibleElement(driverName, super.parentElement, locator, exeLogger);

				if(uiElement == null)
				{
					return false;
				}

				exeLogger.debug("Aliging element to {}", alignToTop ? "Top" : "Bottom");
				
				try
				{
					//before performing move-to ensure the target element is in view port of browser
					uiElement.scrollIntoView(alignToTop);
					
					for(int retry = 0; ; retry++)
					{
						try
						{
							uiElement.hover();
							
							BoundingBox bounds = uiElement.getBounds();
							
							if(bounds != null)
							{
								//keep track of mouse position
								session.moveMouseTo(driverName, bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
							}
							
							break;
						}catch(PlaywrightException ex)
						{
							if(retry > 2)
							{
								break;
							}
							
							exeLogger.debug("As the target is not reachable (might be out of bounds) waiting for 1.5 sec before retry! Error: {}", "" + ex);
	
							//sleep for sometime before move, so that scrolling is completed
							UiAutomationUtils.waitFor(1500);
						}
					}
				}catch(RuntimeException ex)
				{
					if(UiAutomationUtils.isElementNotAvailableException(ex))
					{
						return false;
					}
					
					throw ex;
				}
				
				return true;
			} , retryCount, retryTimeGapMillis,
					"Waiting for element to be clickable: " + getLocatorWithParent(locator), 
					new InvalidStateException("Failed to move to element - " + getLocatorWithParent(locator)));
			
			if(!click)
			{
				return;
			}

			UiAutomationUtils.waitFor(timeGap);

			UiElement uiElement = UiAutomationUtils.findVisibleElement(driverName, super.parentElement, locator, exeLogger);

			if(uiElement == null)
			{
				exeLogger.error("Failed to find visible element with locator: {}", getLocatorWithParent(locator));
				throw new NullPointerException("Failed to find visible element with locator: " + getLocatorWithParent(locator));
			}

			uiElement.click();
			
		}catch(InvalidStateException ex)
		{
			throw new TestCaseFailedException(this, "Failed to move to element - {}", getLocatorWithParent(locator), ex);
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
	
	/**
	 * Sets the if true, moves the mouse to target and clicks the element. Default: false.
	 *
	 * @param click the new if true, moves the mouse to target and clicks the element
	 */
	public void setClick(boolean click)
	{
		this.click = click;
	}

	/**
	 * Sets the time gap which will be used before clicking and after moving the mouse over the element.
	 *
	 * @param timeGap the new time gap which will be used before clicking and after moving the mouse over the element
	 */
	public void setTimeGap(long timeGap)
	{
		if(timeGap <= 0)
		{
			throw new InvalidArgumentException("Time gap should be non-zero positive value: {}", timeGap);
		}
		
		this.timeGap = timeGap;
	}
	
	/**
	 * Sets the before moving the element, the element will be aligned to document. This flag indicates if alignment should be to top or bottom. Default: true.
	 *
	 * @param alignToTop the new before moving the element, the element will be aligned to document
	 */
	public void setAlignToTop(boolean alignToTop)
	{
		this.alignToTop = alignToTop;
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
		builder.append("Move To [");

		builder.append("Locator: ").append(locator);

		builder.append("]");
		return builder.toString();
	}
}
