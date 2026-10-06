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
package com.yukthitech.autox.test.storage.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Deletes value from store for specified key.
 * 
 * @author akiran
 */
@Executable(name = "storeDelete", group = Group.Store, message = "Deletes value from store for specified key.")
public class StoreDeleteStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Key to be deleted.
	 */
	@Param(description = "Key to be deleted.", required = true)
	private String key;

	/**
	 * Sets the key to be stored.
	 *
	 * @param key the new key to be stored
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		boolean res = context.getPersistenceStorage().remove(key);

		if(res)
		{
			exeLogger.debug("Successfully deleted entry with key: {}", key);
			return;
		}

		exeLogger.debug("No entry found with key: {}. Ignoring delete request.", key);
	}
}
