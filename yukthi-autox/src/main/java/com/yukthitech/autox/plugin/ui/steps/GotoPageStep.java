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

import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;

/**
 * Goes to the specified page url.
 * @author akiran
 */
@Executable(name = "uiGotoPage", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Loads page with specified uri")
public class GotoPageStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Url to which browser should be taken.
	 */
	@Param(description = "URI of the page to load")
	private String uri;

	/**
	 * Takes the browser to specified page url.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Going to page with uri - {}", uri);

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		
		WebDriver driver = seleniumSession.getWebDriver(driverName);
		driver.navigate().to( seleniumSession.getResourceUrl(uri) );
	}

	/**
	 * Gets the url to which browser should be taken.
	 *
	 * @return the url to which browser should be taken
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * Sets the url to which browser should be taken.
	 *
	 * @param url the new url to which browser should be taken
	 */
	public void setUri(String url)
	{
		this.uri = url;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Goto Page [");

		builder.append("Uri: ").append(uri);

		builder.append("]");
		return builder.toString();
	}
}
