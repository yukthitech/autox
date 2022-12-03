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
package com.yukthitech.autox.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.rest.RestClient;

public class RestPluginSession extends AbstractPluginSession<RestPluginSession, RestPlugin>
{
	/**
	 * Mapping from base url to client.
	 */
	private Map<String, RestClient> urlToClient = new HashMap<>();

	public RestPluginSession(RestPlugin parentPlugin)
	{
		super(parentPlugin);
	}
	
	/**
	 * Gets the default headers to be passed with every method invocation.
	 *
	 * @return the default headers to be passed with every method invocation
	 */
	public Map<String, String> getDefaultHeaders()
	{
		return Collections.unmodifiableMap(parentPlugin.getDefaultHeaders());
	}

	/**
	 * Gets rest client for specified base url. If base url is not, default base url will be used.
	 * @param baseUrl Base url
	 * @param proxy to be used.
	 * @return Client with specified base url.
	 */
	public synchronized RestClient getRestClient(String baseUrl, String proxy)
	{
		baseUrl = StringUtils.isBlank(baseUrl) ? parentPlugin.getBaseUrl() : baseUrl;

		//determine the cache key to be used
		String cacheKey = baseUrl;
		
		if(StringUtils.isNotBlank(proxy))
		{
			cacheKey = cacheKey + "@" + proxy;
		}
		
		RestClient client = urlToClient.get(cacheKey);
		
		if(client == null)
		{
			client = new RestClient(baseUrl, proxy);
			urlToClient.put(cacheKey, client);
		}
		
		return client;
	}

	@Override
	public void close()
	{
		for(RestClient restClient : this.urlToClient.values())
		{
			restClient.close();
		}
	}
}
