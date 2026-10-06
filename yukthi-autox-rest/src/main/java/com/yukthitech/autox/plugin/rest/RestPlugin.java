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
package com.yukthitech.autox.plugin.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.plugin.AbstractPlugin;
import com.yukthitech.autox.plugin.PluginEvent;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Plugin for REST based steps and validations.
 * @author akiran
 */
@Executable(name = "RestPlugin", group = Group.NONE, message = "Plugin for REST based steps and validations.")
@PluginEvent(
	name = IRestPluginEvent.EVENT_INIT, 
	description = "Invoked when creating new session. Authentication and setting default token header can be done here",
	params = {
		@Param(name = "connectionName", description = "Name of current active connection.")
	})
@PluginEvent(
	name = IRestPluginEvent.EVENT_UNAUTHORIZED, 
	description = "Invoked when unauthorized response is received with http-status 401. Which may occur during session timeout. "
			+ "This event can be used to reset session with new token without disturbing testcases",
	returnDescription = "Boolean value (true/false). True indicates session is reset successfully and current request (which caused unauthoirzed response)"
			+ " will be reinvoked (this retry logic is executed only once). If null or non-true value is returned, no action will be taken.",
	params = {
		@Param(name = "connectionName", description = "Name of current active connection."),
		@Param(name = "request", description = "Rest Request which resulted in unauthorized status. Test cases can send special values in form of headers or params, when session invalidation is being simulated."),
		@Param(name = "result", description = "Current rest result which has unauthorized status.")
	})
public class RestPlugin extends AbstractPlugin<Object, RestPluginSession> implements Validateable
{
	public static final String DEFAULT_CONNECTION = "default";
	
	/**
	 * Different rest connection configurations.
	 * 
	 * @author akranthikiran
	 */
	public static class RestConnection implements Validateable
	{
		/**
		 * Name of this connection.
		 */
		private String name;
		
		/**
		 * Base url for REST api invocation.
		 */
		@Param(description = "Default base url to be used for REST steps and validations.", required = true)
		private String baseUrl;
		
		/**
		 * Default headers to be passed with every method invocation.
		 */
		@Param(description = "Default HTTP headers that will be added before sending the request. The headers defined at step/validation level will override this header."
				+ "<b>The values can contain free-marker expressions.</b>", required = false)
		private Map<String, String> defaultHeaders = new HashMap<>();

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getBaseUrl()
		{
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl)
		{
			this.baseUrl = baseUrl;
		}

		public Map<String, String> getDefaultHeaders()
		{
			return defaultHeaders;
		}

		public void setDefaultHeaders(Map<String, String> defaultHeaders)
		{
			this.defaultHeaders = defaultHeaders;
		}

		public void addDefaultHeader(String name, String value)
		{
			defaultHeaders.put(name, value);
		}

		@Override
		public void validate() throws ValidateException
		{
			if(StringUtils.isBlank(baseUrl))
			{
				throw new ValidateException("Base url can not be null.");
			}
		}
	}
	
	private Map<String, RestConnection> connectionMap = new HashMap<>();
	
	public void addConnection(RestConnection connection)
	{
		this.connectionMap.put(connection.getName(), connection);
	}
	
	@Override
	public Class<Object> getArgumentBeanType()
	{
		return null;
	}
	
	public RestConnection getRestConnection(String name)
	{
		if(name == null)
		{
			name = DEFAULT_CONNECTION;
		}
		
		RestConnection connection = connectionMap.get(name);
		
		if(connection == null)
		{
			throw new InvalidStateException("No connection is configured with name: {}", name);
		}
		
		return connection;
	}
	
	private RestConnection getDefaultConnection()
	{
		RestConnection connection = connectionMap.get(DEFAULT_CONNECTION);
		
		if(connection == null)
		{
			connection = new RestConnection();
			connectionMap.put(DEFAULT_CONNECTION, connection);
		}
		
		return connection;
	}

	/**
	 * Sets the base url for REST api invocation.
	 *
	 * @param baseUrl the new base url for REST api invocation
	 */
	public void setBaseUrl(String baseUrl)
	{
		getDefaultConnection().setBaseUrl(baseUrl);
	}

	/**
	 * Adds default header with specified name and value. 
	 * @param name Name of header to add
	 * @param value value of header to add.
	 */
	public void addDefaultHeader(String name, String value)
	{
		getDefaultConnection().addDefaultHeader(name, value);
	}
	
	/**
	 * Gets the default headers to be passed with every method invocation.
	 *
	 * @return the default headers to be passed with every method invocation
	 */
	Map<String, String> getDefaultHeaders()
	{
		return getDefaultConnection().getDefaultHeaders();
	}
	
	@Override
	protected RestPluginSession createSession()
	{
		return new RestPluginSession(this);
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(MapUtils.isEmpty(connectionMap))
		{
			throw new ValidateException("No connections are configured.");
		}
	}
}
