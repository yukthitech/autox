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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.annotations.RecursiveAnnotationFactory;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;

public class BeanInfoFactory
{
	private static Logger logger = Logger.getLogger(BeanInfoFactory.class.getName());

	private RecursiveAnnotationFactory recursiveAnnotationFactory = new RecursiveAnnotationFactory();
	
	/**
	 * Cache map which caches the properties of different classes
	 */
	private Map<Class<?>, BeanInfo> typeToProp = new HashMap<Class<?>, BeanInfo>();

	/**
	 * Validates the field information specified in the "mapping" and adds the mapping information
	 * to specified bean info
	 * @param beanInfo Bean info to which mapping needs to be added
	 * @param field Field on which mapping is found
	 * @param mapping Mapping to be added
	 */
	private void addMapping(BeanInfo beanInfo, Field field, PropertyMapping mapping)
	{
		NestedProperty externalProperty = null, localProperty = null;
		
		try
		{
			externalProperty = NestedProperty.getNestedProperty(mapping.type(), mapping.from());
			
			String localPropertyName = field.getName();
			
			//if local sub property is specified
			if(mapping.subproperty().length() > 0)
			{
				localPropertyName = localPropertyName + "." + mapping.subproperty();
			}
			
			localProperty = NestedProperty.getNestedProperty(field.getDeclaringClass(), localPropertyName);
		}catch(Exception ex)
		{
			throw new InvalidConfigurationException("Invalid property mapping specified on field - {}.{}. "
					+ "An error occurred while processing mapping properties.", 
					field.getDeclaringClass().getName(), field.getName(), ex);
		}
		
		//ensure target and source are of same types
		if(!CommonUtils.isAssignable(externalProperty.getType(), localProperty.getType()))
		{
			throw new InvalidConfigurationException("Invalid property mapping specified on field - {}.{}. "
					+ "Source property type and target property type are not matching", 
					field.getDeclaringClass().getName(), field.getName());
		}
		
		beanInfo.addCustomMapping(mapping.type(), new MappingInfo(externalProperty, localProperty));
	}

	/**
	 * Fetches the mapping annotations from specified field and adds them to specified bean info
	 * @param beanInfo Bean info to which mappings needs to be added
	 * @param field Field from which mappings needs to be fetched
	 */
	private void getMappingsFromField(BeanInfo beanInfo, Field field)
	{
		//check for direct mappings
		PropertyMapping directMapping = recursiveAnnotationFactory.findAnnotationRecursively(field, PropertyMapping.class); 
		
		if(directMapping != null)
		{
			addMapping(beanInfo, field, directMapping);
		}
		
		//check for group mappings, used to specify multiple mappings
		PropertyMappings propertyMappings = recursiveAnnotationFactory.findAnnotationRecursively(field, PropertyMappings.class);
		
		if(propertyMappings != null)
		{
			for(PropertyMapping mapping : propertyMappings.value())
			{
				addMapping(beanInfo, field, mapping);
			}
		}
	}

	/**
	 * Checks in the cache if the specified bean type property details is already loaded. If loaded returns the same. If not, builds the property map
	 * caches it and returns it.
	 * @param beanType Bean types for which property map needs to be fetched
	 * @return Property details of specified bean type
	 */
	public synchronized BeanInfo getBeanInfo(Class<?> beanType)
	{
		BeanInfo beanInfo = typeToProp.get(beanType);
		
		//if type is already loaded return the same
		if(beanInfo != null)
		{
			return beanInfo;
		}
		
		beanInfo = new BeanInfo(beanType);
		Field fields[] = null;
		NestedProperty nestedProp = null;
		IgnorePropertyDestination ignorePropertyDestination = null;
		
		while(!beanType.getName().startsWith("java"))
		{
			fields = beanType.getDeclaredFields();
			
			//loop through property descriptors and add to bean property map
			for(Field field : fields)
			{
				if(field.isSynthetic())
				{
					continue;
				}
				
				if(Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				
				try
				{
					nestedProp = NestedProperty.getNestedProperty(beanType, field.getName());
				}catch(Exception ex)
				{
					logger.log(Level.INFO, 
							String.format("Ignoring %s.%%s property, as property fetch resulted in error - %s", beanType.getName(), field.getName()), ex);
					continue;
				}
				
				ignorePropertyDestination = field.getAnnotation(IgnorePropertyDestination.class);
				
				beanInfo.addProperty(new PropertyInfo(nestedProp, ignorePropertyDestination != null));
	
				getMappingsFromField(beanInfo, field);
			}
			
			beanType = beanType.getSuperclass();
		}
		
		//cache and return property map
		typeToProp.put(beanType, beanInfo);
		return beanInfo;
	}

}
