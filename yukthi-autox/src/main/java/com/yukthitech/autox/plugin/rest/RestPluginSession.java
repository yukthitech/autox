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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.event.EventManager;
import com.yukthitech.autox.plugin.AbstractPluginSession;
import com.yukthitech.autox.plugin.rest.RestPlugin.RestConnection;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.rest.RestClient;

public class RestPluginSession extends AbstractPluginSession<RestPluginSession, RestPlugin>
{
	private static class ConnectionDetails
	{
		private RestPlugin.RestConnection connection;
		
		/**
		 * Mapping from base url to client.
		 */
		private Map<String, RestClient> urlToClient = new HashMap<>();
		
		private Map<String, Object> attributes = Collections.synchronizedMap(new HashMap<>());

		public ConnectionDetails(RestConnection connection)
		{
			this.connection = connection;
		}
	}
	
	private Map<String, ConnectionDetails> connectionMap = new HashMap<>();
	
	public RestPluginSession(RestPlugin parentPlugin)
	{
		super(parentPlugin);
	}
	
	private ConnectionDetails getConnectionDetails(String connectionName)
	{
		if(connectionName == null)
		{
			connectionName = RestPlugin.DEFAULT_CONNECTION;
		}
		
		ConnectionDetails det = null;
		
		synchronized(this)
		{
			det = connectionMap.get(connectionName);
			
			if(det != null)
			{
				return det;
			}
			
			det = new ConnectionDetails(parentPlugin.getRestConnection(connectionName));
			this.connectionMap.put(connectionName, det);
		}
		
		EventManager.getInstance().invokePluginEventHandler(this, IRestPluginEvent.EVENT_INIT, 
				CommonUtils.toMap("connectionName", connectionName));

		return det;
	}
	
	/**
	 * Gets the default headers to be passed with every method invocation.
	 *
	 * @return the default headers to be passed with every method invocation
	 */
	public Map<String, String> getDefaultHeaders(String connectionName)
	{
		ConnectionDetails connectionDetails = getConnectionDetails(connectionName);
		return Collections.unmodifiableMap(connectionDetails.connection.getDefaultHeaders());
	}
	
	public String getBaseUrl(String connectionName)
	{
		ConnectionDetails connectionDetails = getConnectionDetails(connectionName);
		return connectionDetails.connection.getBaseUrl();
	}

	/**
	 * Gets rest client for specified base url. If base url is not, default base url will be used.
	 * @param baseUrl Base url
	 * @param proxy to be used.
	 * @return Client with specified base url.
	 */
	public synchronized RestClient getRestClient(String connectionName, String baseUrl, String proxy)
	{
		ConnectionDetails connectionDetails = getConnectionDetails(connectionName);
		
		baseUrl = StringUtils.isBlank(baseUrl) ? connectionDetails.connection.getBaseUrl() : baseUrl;

		//determine the cache key to be used
		String cacheKey = baseUrl;
		
		if(StringUtils.isNotBlank(proxy))
		{
			cacheKey = cacheKey + "@" + proxy;
		}
		
		RestClient client = connectionDetails.urlToClient.get(cacheKey);
		
		if(client == null)
		{
			client = new RestClient(baseUrl, proxy);
			client.setRestClientListener(new AutoxRestClientListener(connectionDetails.connection.getName(), this));
			
			connectionDetails.urlToClient.put(cacheKey, client);
		}
		
		return client;
	}
	
	@Override
	public Map<String, Object> getAttributes(String connectionName)
	{
		ConnectionDetails connectionDetails = getConnectionDetails(connectionName);
		return connectionDetails.attributes;
	}

	@Override
	public void close()
	{
		for(ConnectionDetails conDet : this.connectionMap.values())
		{
			for(RestClient restClient : conDet.urlToClient.values())
			{
				restClient.close();
			}
		}
	}
}
