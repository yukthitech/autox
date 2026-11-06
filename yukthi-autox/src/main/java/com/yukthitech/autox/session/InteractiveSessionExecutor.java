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
package com.yukthitech.autox.session;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.yukthitech.autox.exec.IExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.IEntryPoint;

/**
 * Long-lived executor bound for an interactive {@link AutoxSession}.
 * Plugin sessions stay checked out until the session closes.
 */
final class InteractiveSessionExecutor implements IExecutor, IEntryPoint
{
	private final String uniqueId = "interactive-session-" + UUID.randomUUID();
	
	@Override
	public String getUniqueId()
	{
		return uniqueId;
	}
	
	@Override
	public Object getExecutable()
	{
		return this;
	}
	
	@Override
	public IExecutor getParentExecutor()
	{
		return null;
	}
	
	@Override
	public boolean isParentContextShared()
	{
		return false;
	}
	
	@Override
	public List<? extends IExecutor> getDependencies()
	{
		return Collections.emptyList();
	}
	
	@Override
	public IExecutionLogger getActiveExecutionLogger()
	{
		return null;
	}
	
	@Override
	public String toText()
	{
		return uniqueId;
	}
}
