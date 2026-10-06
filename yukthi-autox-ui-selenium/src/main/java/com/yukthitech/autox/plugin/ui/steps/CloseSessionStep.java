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
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;

/**
 * Simulates the click event on the specified button.
 * @author akiran
 */
@Executable(name = "uiCloseSession", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Closes the current session (not only window but also driver).")
public class CloseSessionStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * To be set to true, if the driver needs to be reset. So that browser can be opened freshly.
	 */
	@Param(description = "To be set to true, if the driver needs to be reset. So that browser can be opened freshly. Default: false")
	private boolean resetDriver;
	
	public void setResetDriver(boolean resetDriver)
	{
		this.resetDriver = resetDriver;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing current session");
		
		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		
		if(resetDriver)
		{
			exeLogger.debug("Waiting for 2 Secs, for current session to close completely before resetting driver");
			
			AutomationUtils.sleep(2000);
			seleniumSession.resetDriver(driverName);
		}
		else
		{
			seleniumSession.closeDriver(driverName);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Close Session");
		return builder.toString();
	}

}
