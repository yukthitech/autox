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

import org.openqa.selenium.WebDriver.Window;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Simulates the click event on the specified button.
 * @author akiran
 */
@Executable(name = "uiMaximizeBrowser", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Maximizes the current browser window.")
public class MaximizeBrowserStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		Window window = seleniumSession.getWebDriver(driverName).manage().window();
		
		exeLogger.debug("Maximizing the browser window. Before maximize browser details are: [Position: {}, Size: {}]",
				window.getPosition(), window.getSize());
		
		window.maximize();
		
		exeLogger.debug("Waiting for 5 Sec for maximize to take affect");
		
		try
		{
			Thread.sleep(5000);
		}catch(Exception ex)
		{}
		
		exeLogger.debug("Post maximizing the browser window browser details are: [Position: {}, Size: {}]",
				window.getPosition(), window.getSize());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Maximize Browser");
		return builder.toString();
	}

}
