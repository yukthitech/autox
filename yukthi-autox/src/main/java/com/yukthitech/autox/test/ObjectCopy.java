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
package com.yukthitech.autox.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Encapsulation of a bean, and override properties.
 * @author akiran
 */
public class ObjectCopy implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static class Property implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Name or nested property.
		 */
		private String name;
		
		/**
		 * Value for the property.
		 */
		private String value;

		public void setName(String name)
		{
			this.name = name;
		}

		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	/**
	 * Base object to be used.
	 */
	private Object baseObject;
	
	/**
	 * Properties to override on base object copy.
	 */
	private List<Property> properties = new ArrayList<>();

	public void setBaseObject(Object baseObject)
	{
		this.baseObject = baseObject;
	}

	/**
	 * Adds property to override on bean.
	 * @param property property
	 */
	public void addProperty(Property property)
	{
		this.properties.add(property);
	}
	
	/**
	 * Creates copy of base object and override specified properties.
	 * @return base object copy with overridden properties.
	 */
	public Object createCopy()
	{
		try
		{
			Object copy = baseObject.getClass().newInstance();
			BeanUtils.copyProperties(copy, baseObject);
			
			for(Property prop : this.properties)
			{
				BeanUtils.setProperty(copy, prop.name, prop.value);
			}
			
			return copy;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating copy of object: [}", baseObject, ex);
		}
	}
}
