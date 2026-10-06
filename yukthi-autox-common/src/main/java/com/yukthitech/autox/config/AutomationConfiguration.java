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

import com.yukthitech.ccg.xml.XMLBeanParser;


/**
 * Internal configuration required by automation.
 * @author akiran
 */
public class AutomationConfiguration
{
	/**
	 * Singleton instance.
	 */
	private static AutomationConfiguration instance;
	
	/**
	 * Java script snippet that can be used in automation utils. 
	 */
	private Map<String, String> scripts = new HashMap<>();
	
	/**
	 * Means of getting singleton instance.
	 * @return Single ton instance of this class.
	 */
	public static AutomationConfiguration getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		
		instance = new AutomationConfiguration();
		XMLBeanParser.parse(AutomationConfiguration.class.getResourceAsStream("/automation-config.xml"), instance);
		return instance;
	}
	
	/**
	 * Adds specified script with specified name.
	 * @param name Name of the script snippet.
	 * @param script Script snippet.
	 */
	public void addScript(String name, String script)
	{
		scripts.put(name, script);
	}
	
	/**
	 * Fetches script with specified name.
	 * @param name Name of the script to fetch.
	 * @return matching script.
	 */
	public String getScript(String name)
	{
		return scripts.get(name);
	}
}
