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

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Resets the driver for usage.
 * @author akiran
 */
@Executable(name = "uiResetSession", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Resets the driver for usage.")
public class ResetSessionStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Resetting current session");
		
		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		seleniumSession.resetDriver(driverName);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("Reset Session");
		return builder.toString();
	}

}
