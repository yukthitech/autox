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
package com.yukthitech.autox.test.ui.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines types of locators that can be used for automation.
 * @author akiran
 */
public enum LocatorType
{
	/**
	 * Element id based locator.
	 */
	ID("id", "Used to find ui elements using id."),
	
	/**
	 * Element's CSS class based locator.
	 */
	CLASS("class", "Used to find ui elements using css class"),
	
	/**
	 * Element's tag name based locator.
	 */
	TAG("tag", "Used to find ui elements using tag name"),
	
	/**
	 * Element's name based locator.
	 */
	NAME("name", "Used to find ui elements using name of the element"),
	
	/**
	 * CSS expression based locator.
	 */
	CSS("css", "Used to find ui elements using css locator."),
	
	/**
	 * Xpath based locator.
	 */
	XPATH("xpath", "Used to find ui elements using xpath."),
	
	/**
	 * JS expression based locator.
	 */
	JS("js", "Used to find ui elements using js expression.")
	
	;
	
	/**
	 * Mapping from key to locator type.
	 */
	private static Map<String, LocatorType> keyToType = null;

	/**
	 * Prefix used in locator string to use particular locator type.
	 */
	private String key;
	
	/**
	 * Description of the locator.
	 */
	private String description;
	
	/**
	 * Instantiates a new locator type.
	 *
	 * @param key the key
	 * @param description the description
	 */
	private LocatorType(String key, String description)
	{
		this.key = key;
		this.description = description;
	}
	
	/**
	 * Gets the prefix used in locator string to use particular locator type.
	 *
	 * @return the prefix used in locator string to use particular locator type
	 */
	public String getKey()
	{
		return key;
	}
	
	/**
	 * Gets the description of the locator.
	 *
	 * @return the description of the locator
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Fetches the locator type based on specified key.
	 * @param key Key for which locator type to be fetched
	 * @return Matching locator type
	 */
	public static synchronized LocatorType getLocatorType(String key)
	{
		if(keyToType != null)
		{
			return keyToType.get(key);
		}
		
		keyToType = new HashMap<String, LocatorType>();
		
		for(LocatorType type : LocatorType.values())
		{
			keyToType.put(type.key, type);
		}
		
		return keyToType.get(key);
	}
}
