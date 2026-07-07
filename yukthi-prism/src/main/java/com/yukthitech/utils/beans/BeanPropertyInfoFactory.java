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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory and cache of bean property infos.
 * @author akiran
 */
public class BeanPropertyInfoFactory
{
	private Map<Class<?>, BeanPropertyInfo> typeToInfo = new HashMap<Class<?>, BeanPropertyInfo>();
	
	/**
	 * Fetches bean property info for specified type.
	 * @param type type for which property info needs to be fetched
	 * @return bean property info
	 */
	public synchronized BeanPropertyInfo getBeanPropertyInfo(Class<?> type)
	{
		BeanPropertyInfo beanPropertyInfo = typeToInfo.get(type);
		
		if(beanPropertyInfo != null)
		{
			return beanPropertyInfo;
		}
		
		List<BeanProperty> props = BeanProperty.loadProperties(type, false, false, true);
		props = (props == null) ? Collections.<BeanProperty>emptyList() : props;
		
		beanPropertyInfo = new BeanPropertyInfo(type, props);
		typeToInfo.put(type, beanPropertyInfo);
		
		return beanPropertyInfo;
	}
}
