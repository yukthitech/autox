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

import java.util.List;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Fetches mock request details with specified filter details.
 * 
 * @author akiran
 */
@Executable(name = "mockFetchRequest", group = Group.Mock, message = "Fetches mock request details with specified filter details")
public class MockFetchRequestStep extends AbstractStep
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the server.
	 */
	@Param(description = "Name of the server.", required = true)
	private String name;

	/**
	 * Url filter to be used to fetch mock request.
	 */
	@Param(description = "Uri filter to be used to fetch mock request.", required = false, sourceType = SourceType.EXPRESSION)
	private String uriFilter;

	/**
	 * Method filter to be used to fetch mock request.
	 */
	@Param(description = "Method filter to be used to fetch mock request.", required = false, sourceType = SourceType.EXPRESSION)
	private String methodFilter;

	/**
	 * Attribute name to be used to store filtered-request on context.
	 */
	@Param(description = "Attribute name to be used to store filtered-request on context.", required = true, attrName = true)
	private String attributeName;

	/**
	 * Sets the name of the server.
	 *
	 * @param name the new name of the server
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the url filter to be used to fetch mock request.
	 *
	 * @param urlFilter the new url filter to be used to fetch mock request
	 */
	public void setUriFilter(String uriFilter)
	{
		this.uriFilter = uriFilter;
	}

	/**
	 * Sets the method filter to be used to fetch mock request.
	 *
	 * @param methodFilter the new method filter to be used to fetch mock request
	 */
	public void setMethodFilter(String methodFilter)
	{
		this.methodFilter = methodFilter;
	}

	/**
	 * Sets the attribute name to be used to store filtered-request on context.
	 *
	 * @param attributeName the new attribute name to be used to store filtered-request on context
	 */
	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
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
		MockRequestFilter filter = new MockRequestFilter(uriFilter, methodFilter);

		logger.debug("Fetching mock-requests from server '{}' with filter: {}", name, filter);

		MockServer mockServer = MockServerFactory.getMockServer(name);
		List<MockRequest> requests = mockServer.fetchRequests(filter);

		logger.debug("From server '{}' number of requests filtered are {}. Setting requests as attribute: {}", name, requests.size(), attributeName);
		context.setAttribute(attributeName, requests);
	}
}
