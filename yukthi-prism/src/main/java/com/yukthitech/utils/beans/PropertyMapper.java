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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Like Apache's {@link PropertyUtils} this class also maps properties from source bean to destination bean. With additional support of 
 * copying properties based on camel case property names. For example, relationId would be mapped to relation.id
 * @author akiran
 */
public class PropertyMapper
{
	private static Logger logger = Logger.getLogger(PropertyMapper.class.getName());
	
	private static BeanInfoFactory beanInfoFactory = new BeanInfoFactory();
	
	/**
	 * Checks in the cache if the specified bean type property details is already loaded. If loaded returns the same. If not, builds the property map
	 * caches it and returns it.
	 * @param beanType Bean types for which property map needs to be fetched
	 * @return Property details of specified bean type
	 */
	public static BeanInfo getBeanInfo(Class<?> beanType)
	{
		return beanInfoFactory.getBeanInfo(beanType);
	}
	
	/**
	 * Called to check when property copy has to be done mismatching fields. Simple types like primitives, java core classes and arrays will be skipped.
	 * @param type Type to be checked
	 * @return
	 */
	private static boolean isIgnorableType(Class<?> type)
	{
		if(type.isPrimitive() || type.getName().startsWith("java") || type.isArray())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Gets the class of specified object. In case of CGLIB proxies, actual class name will be returned.
	 * @param bean
	 * @return
	 */
	private static Class<?> getClass(Object bean)
	{
		String className = bean.getClass().getName();
		int idx = className.indexOf("$$EnhancerByCGLIB$$");
		
		if(idx < 0)
		{
			return bean.getClass();
		}
		
		try
		{
			return Class.forName(className.substring(0, idx));
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching actual class name from proxy class name - {}", bean.getClass().getName(), ex);
		}
	}
	
	/**
	 * Copies properties from "source" to "destination". This will be shallow copy. This copy also maps property camel case
	 * naming. For example, relationId would be mapped to relation.id
	 * @param destination Destination to which properties needs to be copied
	 * @param source Source from property values needs to be fetched
	 */
	public static void copyProperties(Object destination, Object source)
	{
		//ensure destination and source are provided
		if(destination == null)
		{
			throw new NullPointerException("Destination can not be null");
		}
		
		if(source == null)
		{
			throw new NullPointerException("Source can not be null");
		}
		
		//load source and destination property maps
		BeanInfo sourceBeanInfo = getBeanInfo(getClass(source));
		BeanInfo destinationBeanInfo = getBeanInfo(getClass(destination));
		Object value = null, destValue = null;
		
		PropertyInfo destPropInfo = null;
		NestedProperty sourceProperty = null, destProperty = null;;
		
		//loop through source property and copy all simple (directly matching) properties
		for(String srcProp : sourceBeanInfo.getPropertyNames())
		{
			destPropInfo = destinationBeanInfo.getProperty(srcProp);
			
			//if property is not found on destination ignore
			if(destPropInfo == null || destPropInfo.isIgnoreDestination())
			{
				continue;
			}
			
			destProperty = destPropInfo.getProperty();
			sourceProperty = sourceBeanInfo.getProperty(srcProp).getProperty();
			
			value = sourceProperty.getValue(source);
			
			//if source value is not present
			if(value == null)
			{
				continue;
			}
			
			//if source and destination types are not matching throw error
			if(!CommonUtils.isAssignable(sourceProperty.getType(), destProperty.getType()))
			{
				/*
				if(isIgnorableType(sourceProperty.getType()) || isIgnorableType(destProperty.getType()))
				{
					logger.log(Level.INFO, String.format("Ignoring property '%s' as source and destination data types are not matching "
							+ "[Source type : %s, Source Property Type: %s, Desctination Type: %s, Destination Property Type: %s] ", 
							srcProp, source.getClass().getName(), sourceProperty.getType().getName(), 
							destination.getClass().getName(), destProperty.getType().getName()));
					continue;
				}
				*/
				
				try
				{
					destValue = destProperty.getValue(destination);
					
					if(destValue == null)
					{
						destValue = destProperty.getType().getConstructor().newInstance();
					}
					
					copyProperties(destValue, value);
					value = destValue;
				}catch(Exception ex)
				{
					logger.log(Level.INFO, String.format("Ignoring mismatching property '%s' as source and destination data types are not "
							+ "matching and an error occurred while creating desination property bean "
							+ "[Source type : %s, Source Property Type: %s, Desctination Type: %s, Destination Property Type: %s]. Error - %s", 
							srcProp, source.getClass().getName(), sourceProperty.getType().getName(), 
							destination.getClass().getName(), destProperty.getType().getName(), ex), ex);
					continue;
				}
			}
			
			//copy property from source to destination
			destProperty.setValue(destination, value);
		}
		
		List<MappingInfo> mappings = sourceBeanInfo.getMappings(destination.getClass()); 
		
		//loop through custom mappings from source
		if(mappings != null)
		{
			for(MappingInfo mapping : mappings)
			{
				value = mapping.getLocalProperty().getValue(source);
				
				if(value == null)
				{
					continue;
				}
				
				mapping.getExternalProperty().setValue(destination, value);
			}
		}
	
		//loop through custom mappings from destination
		mappings = destinationBeanInfo.getMappings(source.getClass());
		
		if(mappings != null)
		{
			for(MappingInfo mapping : mappings)
			{
				value = mapping.getExternalProperty().getValue(source);
				
				if(value == null)
				{
					continue;
				}
				
				mapping.getLocalProperty().setValue(destination, value);
			}
		}
	}
}
