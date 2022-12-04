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

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to validate and click ok of alert prompt.
 * @author akiran
 */
@Executable(name = "uiHandleAlert", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Used to validate and click ok of alert prompt.")
public class HandleAlertStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Messaged expected in alert. If specified, alert message will be validated with this message..
	 */
	@Param(description = "Messaged expected in alert. If specified, alert message will be validated with this message.", required = false)
	private String expectedMessage;

	/**
	 * Sets the messaged expected in alert. If specified, alert message will be validated with this message..
	 *
	 * @param expectedMessage the new messaged expected in alert
	 */
	public void setExpectedMessage(String expectedMessage)
	{
		this.expectedMessage = expectedMessage;
	}
	
	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		if(expectedMessage != null)
		{
			exeLogger.debug("Handling alert and validating message to be - '{}'", expectedMessage);
		}
		else
		{
			exeLogger.debug("Handling alert without validation of message..");
		}

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);

		Alert alert = driver.switchTo().alert();
		
		if(expectedMessage != null)
		{
			if(expectedMessage.trim().equals(alert.getText().trim()))
			{
				exeLogger.debug("Found alert message to be as expected");
			}
			else
			{
				exeLogger.error("Found alert message '{}' and expected message '{}' are different", alert.getText(), expectedMessage);
				throw new InvalidStateException("Found alert message '{}' and expected message '{}' are different", alert.getText(), expectedMessage);
			}
		}
		
		alert.accept();
		exeLogger.debug("Successfully closed the alert.");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Handle alert [");

		builder.append("Expected Mssg: ").append(expectedMessage);

		builder.append("]");
		return builder.toString();
	}
}
