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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Plugin for REST based steps and validations.
 * @author akiran
 */
@Executable(name = "RestPlugin", group = Group.NONE, message = "Plugin for REST based steps and validations.")
public class RestPlugin extends AbstractPlugin<Object, RestPluginSession> implements Validateable
{
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
	
	@Override
	public Class<Object> getArgumentBeanType()
	{
		return null;
	}

	/**
	 * Gets the base url for REST api invocation.
	 *
	 * @return the base url for REST api invocation
	 */
	public String getBaseUrl()
	{
		return baseUrl;
	}

	/**
	 * Sets the base url for REST api invocation.
	 *
	 * @param baseUrl the new base url for REST api invocation
	 */
	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	/**
	 * Adds default header with specified name and value. 
	 * @param name Name of header to add
	 * @param value value of header to add.
	 */
	public void addDefaultHeader(String name, String value)
	{
		defaultHeaders.put(name, value);
	}
	
	/**
	 * Gets the default headers to be passed with every method invocation.
	 *
	 * @return the default headers to be passed with every method invocation
	 */
	Map<String, String> getDefaultHeaders()
	{
		return defaultHeaders;
	}
	
	@Override
	protected RestPluginSession createSession()
	{
		return new RestPluginSession(this);
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
