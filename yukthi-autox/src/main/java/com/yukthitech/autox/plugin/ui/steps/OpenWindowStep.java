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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;

/**
 * Opens new window with specifie name and url.
 * 
 * @author akiran
 */
@Executable(name = "uiOpenWindow", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Opens new window with specifie name and url.")
public class OpenWindowStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(OpenWindowStep.class);

	/**
	 * Url to be opened.
	 */
	@Param(description = "Url to be opened.")
	private String url;
	
	/**
	 * Name of the window being opened.
	 */
	@Param(description = "Name of the window being opened.")
	private String name;
	
	/**
	 * Sets the url to be opened.
	 *
	 * @param url the new url to be opened
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * Sets the name of the window being opened.
	 *
	 * @param name the new name of the window being opened
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Opening window '{}' with url: {}", name, url);

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);
		
		String openScript = String.format("window.open('%s', '%s')", url, name);
		
		((JavascriptExecutor) driver).executeScript(openScript);
		
		UiAutomationUtils.waitWithPoll(() -> 
		{
			try
			{
				driver.switchTo().window(name);
			}catch(Exception ex)
			{
				logger.debug("An error occurred while switching to window '{}'. Error: {}", name, "" + ex);
				return false;
			}
			
			return true;
		}, 10000, 1000);
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
		builder.append("Open Window [");

		builder.append("Name: ").append(name);
		builder.append(", Url: ").append(url);

		builder.append("]");
		return builder.toString();
	}
}
