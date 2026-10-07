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

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession.PendingDialog;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to validate and click ok/cancel of confirm prompt.
 * @author akiran
 */
@Executable(name = "uiHandleConfirm", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Used to validate and click ok/cancel of confirm prompt.")
public class HandleConfirmStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Max time to wait for the dialog to appear.
	 */
	private static final long DIALOG_WAIT_MILLIS = 10000;

	/**
	 * Messaged expected in alert. If specified, alert message will be validated with this message..
	 */
	@Param(description = "Messaged expected in alert. If specified, alert message will be validated with this message.", required = false, sourceType = SourceType.EXPRESSION)
	private Object expectedMessage;
	
	/**
	 * Flag used to accept or cancel confirm box. Default: true.
	 */
	@Param(description = "Flag used to accept or cancel confirm box. Default: true")
	private boolean accept = true;

	/**
	 * Sets the messaged expected in alert. If specified, alert message will be validated with this message..
	 *
	 * @param expectedMessage the new messaged expected in alert
	 */
	public void setExpectedMessage(Object expectedMessage)
	{
		this.expectedMessage = expectedMessage;
	}
	
	public void setAccept(boolean accept)
	{
		this.accept = accept;
	}
	
	/**
	 * Validates the confirm message and accepts/dismisses the confirm box.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		if(expectedMessage != null)
		{
			exeLogger.debug("Handling confirm and validating message to be - '{}'", expectedMessage);
		}
		else
		{
			exeLogger.debug("Handling confirm without validation of message..");
		}

		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
		PendingDialog confirm = session.pollDialog(driverName, DIALOG_WAIT_MILLIS);
		
		if(confirm == null)
		{
			throw new InvalidStateException("No confirm dialog found on browser within {} millis", DIALOG_WAIT_MILLIS);
		}
		
		if(expectedMessage != null)
		{
			String expected = String.valueOf(expectedMessage);
			String actual = confirm.getMessage() == null ? "" : confirm.getMessage();
			
			if(expected.trim().equals(actual.trim()))
			{
				exeLogger.debug("Found confirm message to be as expected");
			}
			else
			{
				//dialog should not be left open
				confirm.dismiss();
				
				exeLogger.error("Found confirm message '{}' and expected message '{}' are different", actual, expectedMessage);
				throw new InvalidStateException("Found confirm message '{}' and expected message '{}' are different", actual, expectedMessage);
			}
		}
	
		if(accept)
		{
			confirm.accept();
			exeLogger.debug("Successfully accepted the confirmation.");
		}
		else
		{
			confirm.dismiss();
			exeLogger.debug("Successfully dismissed the confirmation.");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("Handle Alert [");

		builder.append("Expected Mssg: ").append(expectedMessage);
		builder.append(", ").append("Accept: ").append(accept);

		builder.append("]");
		return builder.toString();
	}
}
