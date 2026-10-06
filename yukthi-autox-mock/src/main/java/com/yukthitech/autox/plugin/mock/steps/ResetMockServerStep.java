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
package com.yukthitech.autox.plugin.mock.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Resets specified mock server.
 * @author akiran
 */
@Executable(name = "mockServerReset", group = Group.Mock, message = "Resets specified mock server, that is cleaning up all mocked responses and requests..")
public class ResetMockServerStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the server.
	 */
	@Param(description = "Name of the server.", required = true, sourceType = SourceType.EXPRESSION)
	private String name;

	/**
	 * Sets the name of the server.
	 *
	 * @param name
	 *            the new name of the server
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.
	 * AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		logger.debug("Reseting mock server: {}", name);
		
		MockServer server = MockServerFactory.getMockServer(name);
		server.reset();
	}
}
