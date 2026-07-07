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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * For internal use only. Used for caching bean properties and mappings
 * for property copying.
 * 
 * @author akiran
 */
public class BeanInfo
{
	/**
	 * Bean type for which this info is being defined
	 */
	private Class<?> beanType;
	
	/**
	 * Map of property information
	 */
	private Map<String, PropertyInfo> properties = new HashMap<String, PropertyInfo>();
	
	/**
	 * Maintains custom mappings. Key would be the source class which needs custom mapping
	 * and value would field to mapping-info which involves custom mapping. 
	 */
	private Map<Class<?>, List<MappingInfo>> customMappings = new HashMap<Class<?>, List<MappingInfo>>();
	
	/**
	 * Instantiates a new bean info.
	 *
	 * @param beanType the bean type
	 */
	public BeanInfo(Class<?> beanType)
	{
		this.beanType = beanType;
	}
	
	/**
	 * Adds property information
	 * @param info property info
	 */
	public void addProperty(PropertyInfo info)
	{
		properties.put(info.getName(), info);
	}
	
	/**
	 * Gets the property info with specified name
	 * @param name Name of the property to fetch
	 * @return Matching property info
	 */
	public PropertyInfo getProperty(String name)
	{
		return properties.get(name);
	}
	
	/**
	 * Fetches property names
	 * @return set of property names
	 */
	public Set<String> getPropertyNames()
	{
		return properties.keySet();
	}

	/**
	 * Gets the bean type for which this info is being defined.
	 *
	 * @return the bean type for which this info is being defined
	 */
	public Class<?> getBeanType()
	{
		return beanType;
	}
	
	/**
	 * Indicates if custom mapping is required when properties are copied from 
	 * specified "fromType"
	 * @param fromType Type to be queried
	 * @return true, if "fromType" requires custom mapping to current bean
	 */
	public boolean needsCustomMapping(Class<?> fromType)
	{
		return customMappings.containsKey(fromType);
	}
	
	public void addCustomMapping(Class<?> forType, MappingInfo mapping)
	{
		List<MappingInfo> mappings = customMappings.get(forType);
				
		if(mappings == null)
		{
			mappings = new ArrayList<MappingInfo>();
			customMappings.put(forType, mappings);
		}
		
		mappings.add(mapping);
		
	}
	
	/**
	 * Fetches custom mapping required by current bean when properties are copied from
	 * "fromType" for field "fieldName"
	 * @param fromType
	 * @return Custom mappings for specified field
	 */
	public List<MappingInfo> getMappings(Class<?> fromType)
	{
		return customMappings.get(fromType);
	}
}
