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

import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Request received on mock server.
 * 
 * @author akiran
 */
public class MockRequest implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Uri used by request.
	 */
	private String uri;

	/**
	 * Name of the request method.
	 */
	private String method;

	/**
	 * Headers of the request.
	 */
	private Map<String, List<String>> headers = new HashMap<>();

	/**
	 * The request parameters.
	 */
	private Map<String, List<String>> parameters = new HashMap<>();

	/**
	 * Body of the request.
	 */
	private String body;

	/**
	 * Response used to serve this request.
	 */
	private MockResponse mockResponse;

	/**
	 * Instantiates a new mock request.
	 *
	 * @param body
	 *            the body
	 * @param headers
	 *            the headers
	 * @param parameters
	 *            the parameters
	 * @param method
	 *            the method
	 */
	@SuppressWarnings("unchecked")
	MockRequest(HttpServletRequest request)
	{
		this.uri = request.getRequestURI();
		this.method = request.getMethod();

		// fetch the headers
		Enumeration<String> headerNames = request.getHeaderNames();

		while(headerNames.hasMoreElements())
		{
			String key = (String) headerNames.nextElement();
			headers.put(key, EnumerationUtils.toList(request.getHeaders(key)));
		}

		// fetch the parameters
		Enumeration<String> parameterNames = request.getParameterNames();

		while(parameterNames.hasMoreElements())
		{
			String key = (String) parameterNames.nextElement();
			parameters.put(key, Arrays.asList(request.getParameterValues(key)));
		}

		try
		{
			InputStream is = request.getInputStream();
			this.body = IOUtils.toString(is, Charset.defaultCharset());
			is.close();
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while reading body of the request", ex);
		}
	}

	/**
	 * Gets the uri used by request.
	 *
	 * @return the uri used by request
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * Sets the uri used by request.
	 *
	 * @param uri the new uri used by request
	 */
	public void setUri(String uri)
	{
		this.uri = uri;
	}

	/**
	 * Gets the name of the request method.
	 *
	 * @return the name of the request method
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * Sets the name of the request method.
	 *
	 * @param method
	 *            the new name of the request method
	 */
	public void setMethod(String method)
	{
		this.method = method;
	}

	/**
	 * Gets the headers of the request.
	 *
	 * @return the headers of the request
	 */
	public Map<String, List<String>> getHeaders()
	{
		return headers;
	}

	/**
	 * Sets the headers of the request.
	 *
	 * @param headers
	 *            the new headers of the request
	 */
	public void setHeaders(Map<String, List<String>> headers)
	{
		this.headers = headers;
	}

	/**
	 * Gets the request parameters.
	 *
	 * @return the request parameters
	 */
	public Map<String, List<String>> getParameters()
	{
		return parameters;
	}

	/**
	 * Sets the request parameters.
	 *
	 * @param parameters
	 *            the new request parameters
	 */
	public void setParameters(Map<String, List<String>> parameters)
	{
		this.parameters = parameters;
	}

	/**
	 * Gets the body of the request.
	 *
	 * @return the body of the request
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * Sets the body of the request.
	 *
	 * @param body
	 *            the new body of the request
	 */
	public void setBody(String body)
	{
		this.body = body;
	}

	/**
	 * Gets the response used to serve this request.
	 *
	 * @return the response used to serve this request
	 */
	public MockResponse getMockResponse()
	{
		return mockResponse;
	}

	/**
	 * Sets the response used to serve this request.
	 *
	 * @param mockResponse
	 *            the new response used to serve this request
	 */
	public void setMockResponse(MockResponse mockResponse)
	{
		this.mockResponse = mockResponse;
	}
}
