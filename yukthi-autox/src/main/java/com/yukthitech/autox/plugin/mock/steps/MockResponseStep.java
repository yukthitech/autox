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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Mocks the specified request (url + method) with specified response.
 */
@Executable(name = "mockResponse", group = Group.Mock, message = "Mocks the specified request (url + method) with specified response.")
public class MockResponseStep extends AbstractStep
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the server where mocking should be done.
	 */
	@Param(description = "Name of the server where mocking should be done.", required = true, sourceType = SourceType.EXPRESSION)
	private String name;

	/**
	 * Request uri to be mocked.
	 */
	@Param(description = "Request uri to be mocked", required = true, sourceType = SourceType.EXPRESSION)
	private String uri;

	/**
	 * Http method of the request to be mocked.
	 */
	@Param(description = "Http method of the request to be mocked", required = true, sourceType = SourceType.EXPRESSION)
	private String method;

	/**
	 * Status code to be sent as part of response.
	 */
	@Param(description = "Status code to be sent as part of mock response", required = false, sourceType = SourceType.EXPRESSION)
	private String responseStatusCode = Integer.toString(HttpServletResponse.SC_OK);

	/**
	 * Response headers to be sent as part of response.
	 */
	@Param(name="responseHeader", description = "Headers to be added to the mock response")
	private Map<String, String> responseHeaders = new HashMap<String, String>();

	/**
	 * Expected Response.
	 */
	@Param(description = "Body of the mocked response", required = true, sourceType = SourceType.EXPRESSION)
	private String responseBody;
	
	/**
	 * Number of times for which response should be available for given request. Default: Integer max value.
	 */
	@Param(description = "Number of times for which response should be available for given request. Default: Integer max value", required = false)
	private int times = Integer.MAX_VALUE;
	
	/**
	 * Wait configuration to be used before sending response.
	 */
	@Param(description = "Wait configuration to be used before sending response.", required = false)
	private WaitConfig waitConfig;

	/**
	 * Sets the name of the server where mocking should be done.
	 *
	 * @param name the new name of the server where mocking should be done
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the request uri to be mocked.
	 *
	 * @param uri the new request uri to be mocked
	 */
	public void setUri(String uri)
	{
		this.uri = uri;
	}

	/**
	 * Sets Http method of the request to be mocked.
	 *
	 * @param method Http method of the request to be mocked
	 */
	public void setMethod(String method)
	{
		this.method = method;
	}

	/**
	 * Sets the status code to be sent as part of response.
	 *
	 * @param responseStatusCode the new status code to be sent as part of response
	 */
	public void setResponseStatusCode(String responseStatusCode)
	{
		this.responseStatusCode = responseStatusCode;
	}

	/**
	 * Adds the response header.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public void addResponseHeader(String name, String value)
	{
		this.responseHeaders.put(name, value);
	}

	/**
	 * Sets the expected Response.
	 *
	 * @param responseBody the new expected Response
	 */
	public void setResponseBody(String responseBody)
	{
		this.responseBody = responseBody;
	}
	
	/**
	 * Sets the number of times for which response should be available for given request. Default: Integer max value.
	 *
	 * @param times the new number of times for which response should be available for given request
	 */
	public void setTimes(int times)
	{
		if(times <= 0)
		{
			throw new InvalidArgumentException("Times value should be greater than zero: {}", times);
		}
		
		this.times = times;
	}
	
	/**
	 * Gets the wait configuration to be used before sending response.
	 *
	 * @return the wait configuration to be used before sending response
	 */
	public WaitConfig getWaitConfig()
	{
		return waitConfig;
	}

	/**
	 * Sets the wait configuration to be used before sending response.
	 *
	 * @param waitConfig the new wait configuration to be used before sending response
	 */
	public void setWaitConfig(WaitConfig waitConfig)
	{
		this.waitConfig = waitConfig;
	}

	/**
	 * Execute.
	 *
	 * @param context the context
	 * @param logger the logger
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.
	 * AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		logger.debug(true, "Adding mock response for [Method: {}, Uri: {}] as [Status code: {}, <br>Headers: {}, <br>Body: {}, <br>Wait Config: {}]", 
				method, uri, responseStatusCode, responseHeaders, responseBody, waitConfig);
		
		MockResponse response = new MockResponse(uri, method, responseHeaders, Integer.parseInt(responseStatusCode), responseBody, waitConfig);
		response.setCountLeft(times);

		logger.debug("On server '{}' mocking response {}", name, response);
		
		MockServer server = MockServerFactory.getMockServer(name);
		server.addMockResponse(response);
	}
}
