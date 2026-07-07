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
package com.yukthitech.utils.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bean property info.
 * @author akiran
 */
public class BeanPropertyInfo
{
	/**
	 * Bean Type.
	 */
	private Class<?> beanType;
	
	/**
	 * Property mapping.
	 */
	private Map<String, BeanProperty> propertyMap = new HashMap<String, BeanProperty>();

	public BeanPropertyInfo(Class<?> beanType, List<BeanProperty> properties)
	{
		this.beanType = beanType;
		
		for(BeanProperty prop : properties)
		{
			propertyMap.put(prop.getName(), prop);
		}
	}
	
	/**
	 * Gets the bean Type.
	 *
	 * @return the bean Type
	 */
	public Class<?> getBeanType()
	{
		return beanType;
	}
	
	/**
	 * Fetches property with specified name.
	 * @param name name of prop
	 * @return matching prop
	 */
	public BeanProperty getProperty(String name)
	{
		return propertyMap.get(name);
	}
	
	/**
	 * Fetches all property names.
	 * @return property names
	 */
	public Set<String> getPropertyNames()
	{
		return propertyMap.keySet();
	}
	
	/**
	 * Fetches all properties.
	 * @return properties.
	 */
	public Collection<BeanProperty> getProperties()
	{
		return propertyMap.values();
	}
}
