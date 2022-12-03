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
package com.yukthitech.autox.test.proxy.steps;

/**
 * Filter used to filter the requests from the server.
 * @author akiran
 */
public class MockRequestFilter
{
	/**
	 * Uri to be used as part of filter.
	 */
	private String uri;
	
	/**
	 * Method to be used as part of filter.
	 */
	private String method;
	
	/**
	 * Instantiates a new mock request filter.
	 */
	public MockRequestFilter()
	{}

	/**
	 * Instantiates a new mock request filter.
	 *
	 * @param url the url
	 * @param method the method
	 */
	public MockRequestFilter(String uri, String method)
	{
		this.uri = uri;
		this.method = method;
	}
	
	/**
	 * Used to check if specified request matches with current filter.
	 * @param request
	 * @return
	 */
	public boolean isMatching(MockRequest request)
	{
		if(uri != null && !uri.equals(request.getUri()))
		{
			return false;
		}
		
		if(method != null && !method.equals(request.getMethod()))
		{
			return false;
		}
		
		return true;
	}

	/**
	 * Gets the uri to be used as part of filter.
	 *
	 * @return the uri to be used as part of filter
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * Sets the uri to be used as part of filter.
	 *
	 * @param uri the new uri to be used as part of filter
	 */
	public void setUri(String uri)
	{
		this.uri = uri;
	}

	/**
	 * Gets the method to be used as part of filter.
	 *
	 * @return the method to be used as part of filter
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * Sets the method to be used as part of filter.
	 *
	 * @param method the new method to be used as part of filter
	 */
	public void setMethod(String method)
	{
		this.method = method;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Uri: ").append(uri);
		builder.append(",").append("Method: ").append(method);

		builder.append("]");
		return builder.toString();
	}

}
