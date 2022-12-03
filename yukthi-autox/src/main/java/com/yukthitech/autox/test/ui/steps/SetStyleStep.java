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

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = "uiSetStyle", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Used to manipulate the style of the element.")
public class SetStyleStep extends AbstractParentUiStep
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the element whose style needs to be modified.
	 */
	@Param(description = "Locator of the element whose style needs to be modified.", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * Styles to be modified.
	 */
	@Param(description = "Styles to be modified.")
	private Map<String, String> styles = new HashMap<>();
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("On locator '{}' setting styles: {}", locator, styles);

		WebElement webElement = UiAutomationUtils.findElement(driverName, super.parentElement, locator);

		if(webElement == null)
		{
			exeLogger.error("Failed to find element with locator: {}", getLocatorWithParent(locator));
			throw new NullPointerException("Failed to find element with locator: " + getLocatorWithParent(locator));
		}

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);
		String code = null;

		for(String style: styles.keySet())
		{
			code = "arguments[0].style['" + style + "']=" + styles.get(style);
			exeLogger.debug("Executing js code: {}", code);
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(code, webElement);
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

	public void addStyle(String name, String value)
	{
		this.styles.put(name, value);
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
		builder.append("Set Style [");

		builder.append("Locator: ").append(locator);

		builder.append("]");
		return builder.toString();
	}
}
