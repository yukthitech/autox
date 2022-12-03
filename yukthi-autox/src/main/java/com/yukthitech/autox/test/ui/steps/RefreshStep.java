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

import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Helps in switching between windows.
 * 
 * @author akiran
 */
@Executable(name = "uiRefresh", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Refreshes the current page. This step uses 2 min post-verification delay by default.")
public class RefreshStep extends AbstractPostCheckStep
{
	private static final long serialVersionUID = 1L;
	
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
	
	public RefreshStep()
	{
		super.setPostVerificationDelay(IAutomationConstants.TWO_MIN_MILLIS);
	}

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

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Refreshing the current page");

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);
		
		UiAutomationUtils.validateWithWait(() -> 
		{
			try
			{
				exeLogger.trace("Performing refresh operation..");
				driver.navigate().refresh();
				
				//wait for page refresh to complete
				AutomationUtils.sleep(2000);
				
				//after refresh check the post-check and return result approp
				return doPostCheck(context, exeLogger, "Post Refresh");
			} catch(RuntimeException ex)
			{
				exeLogger.debug("An error occurred while doing refresh. Error: {}", "" + ex);
				
				throw ex;
			}
		} , retryCount, retryTimeGapMillis,
				"Waiting for refresh to be successful", 
				new InvalidStateException("Failed to refresh"));
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
		builder.append("Refresh Page");
		return builder.toString();
	}
}
