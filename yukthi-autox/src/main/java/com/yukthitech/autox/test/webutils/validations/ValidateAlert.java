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
package com.yukthitech.autox.test.webutils.validations;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;
import com.yukthitech.autox.plugin.ui.steps.WaitForStep;

/**
 * Validates alert box is displayed and closes the dialog.
 */
@Executable(name = "validateAlert", group = Group.NONE, requiredPluginTypes = SeleniumPlugin.class, message = "Validates an webutils-specific alert comes up with specified message")
public class ValidateAlert extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	@Param(description = "Name of the driver to be used for the step. Defaults to default driver.", required = false)
	protected String driverName;

	/**
	 * Expected alert message.
	 */
	private String message;
	
	/**
	 * Gets the expected alert message;.
	 *
	 * @return the expected alert message;
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the expected alert message;.
	 *
	 * @param message the new expected alert message;
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.IValidation#execute(com.yukthitech.ui.automation.AutomationContext, java.io.PrintWriter)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		if(!"true".equals(enabled))
		{
			exeLogger.debug("Current validation is disabled. Skipping validation execution.");
			return;
		}
		
		exeLogger.debug("Waiting for alert with message - {}", message);
		
		//wait and validate alert box is displayed
		WaitForStep waitStep = new WaitForStep();
		waitStep.addLocator("//div[@id='webutilsAlertDialog']");
		waitStep.execute(context, exeLogger);
		
		//ensure alert has required message
		WebElement alertBox = UiAutomationUtils.findElement(driverName, (WebElement) null, "id: webutilsAlertDialog");
		WebElement bodyElement = UiAutomationUtils.findElement(driverName, alertBox, "xpath: .//div[@class='modal-body']");
		String bodyText = bodyElement.getAttribute("innerHTML");
		
		if(!bodyText.equals(message))
		{
			exeLogger.error("Expected alert message '{}' is not matching with actual value - {}", message, bodyText);

			throw new AutoxValidationException(this, "Found alert message to be different [Actual: {}, Expected: {}]", 
					bodyText, message);
		}
		
		WebElement buttonElement = UiAutomationUtils.findElement(driverName, alertBox, "xpath: .//button");
		buttonElement.click();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Message: ").append(message);

		builder.append("]");
		return builder.toString();
	}
}
