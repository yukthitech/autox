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

import com.yukthitech.ccg.xml.util.ValueProvider;

/**
 * Value provider for #{} expressions in config and test case files. This class
 * can provider values from properties file, system and environment properties also.
 * @author akiran
 */
public class AppConfigValueProvider implements ValueProvider
{
	/**
	 * Application properties, main source of values.
	 */
	private Map<String, String> appProperties;

	/**
	 * Instantiates a new app config value provider.
	 *
	 * @param appProperties the app properties
	 */
	public AppConfigValueProvider(Map<String, String> appProperties)
	{
		if(appProperties == null)
		{
			appProperties = new HashMap<>();
		}
		
		this.appProperties = appProperties;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.ValueProvider#getValue(java.lang.String)
	 */
	@Override
	public Object getValue(String name)
	{
		if(name.startsWith("system."))
		{
			name = name.substring("system.".length());
			return System.getProperty(name);
		}
		
		if(name.startsWith("env."))
		{
			name = name.substring("env.".length());
			return System.getenv(name);
		}
		
		return "" + appProperties.get(name);
	}
}
