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
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
import com.yukthitech.autox.test.TestCaseFailedException;

/**
 * Waits for specified conditions for specified amount of time.
 * @author akiran
 */
@Executable(name = "uiWaitForConditions", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Waits for all specified conditions to be true")
public class WaitForConditionsStep extends BaseConditions
{
	private static final long serialVersionUID = 1L;

	/**
	 * Waits for all the conditions to be satisfied.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Waiting for {} conditions", conditions.size());

		PlaywrightPluginSession session = getSession();
		
		try
		{
			for(BaseCondition condition : super.conditions)
			{
				exeLogger.debug("Waiting for condition: {}", condition);
				
				condition.condition.await(session, driverName, parentElement, condition.timeOutInSec * 1000L, condition.timeGapMillis);
			}
		} catch(Exception ex)
		{
			throw new TestCaseFailedException(this, ex.getMessage(), ex);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Waiting For Conditions [");
		builder.append(conditions);
		builder.append("]");
		return builder.toString();
	}
}
