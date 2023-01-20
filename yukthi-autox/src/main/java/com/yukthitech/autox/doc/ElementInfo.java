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
package com.yukthitech.autox.doc;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.ChildElement;
import com.yukthitech.ccg.xml.XMLUtil;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Information about the chile elements.
 * @author akiran
 */
public class ElementInfo extends StepInfo
{
	private static final long serialVersionUID = 1L;

	/**
	 * Indicates if multiple elements of this node can be present or not.
	 */
	private boolean multiple;
	
	/**
	 * Flag indicating if parameter is mandatory or not.
	 */
	private boolean mandatory;
	
	/**
	 * Name of the key attribute.
	 */
	private String keyName;
	
	/**
	 * Description of the key description.
	 */
	private String keyDescription;
	
	/**
	 * Java type associated with this element.
	 */
	private String type;

	/**
	 * Instantiates a new param info.
	 *
	 * @param field the field
	 * @param paramAnnot the param annot
	 */
	public ElementInfo(Method method, ChildElement childElem)
	{
		String name = method.getName();
		String desc = childElem.description();
		
		if(name.startsWith("set"))
		{
			name = name.substring(3);
			multiple = false;
			
			if(method.getParameterTypes().length != 1)
			{
				throw new InvalidStateException("@ChildElement is used on invalid setter method: {}.{}()", method.getDeclaringClass().getName(), method.getName());
			}
			
			Class<?> paramType = method.getParameterTypes()[0];
			this.type = paramType.getName();
			
			if(!XMLUtil.isSupportedAttributeClass(paramType))
			{
				super.loadParams(paramType);
			}
		}
		else if(name.startsWith("add"))
		{
			name = name.substring(3);
			multiple = true;

			if(method.getParameterTypes().length == 1)
			{
				Class<?> paramType = method.getParameterTypes()[0];
				this.type = paramType.getName();
				
				if(!XMLUtil.isSupportedAttributeClass(paramType))
				{
					super.loadParams(paramType);
				}
			}
			else if(method.getParameterTypes().length == 2)
			{
				Class<?> keyType = method.getParameterTypes()[0];
				
				if(!XMLUtil.isSupportedAttributeClass(keyType))
				{
					throw new InvalidStateException("@ChildElement is used on invalid adder method {}.{}() whose key (first) attribue is not supported attr type: {}", 
							method.getDeclaringClass().getName(), method.getName(), keyType.getName());
				}
				
				this.keyName = childElem.key();
				
				if(StringUtils.isBlank(keyName))
				{
					throw new InvalidStateException("In @ChildElement of method {}.{}() key name is not specified.", 
							method.getDeclaringClass().getName(), method.getName());
				}
				
				this.keyDescription = childElem.keyDescription();

				if(StringUtils.isBlank(keyDescription))
				{
					throw new InvalidStateException("In @ChildElement of method {}.{}() key description is not specified.", 
							method.getDeclaringClass().getName(), method.getName());
				}

				Class<?> paramType = method.getParameterTypes()[1];
				this.type = paramType.getName();
				
				if(!XMLUtil.isSupportedAttributeClass(paramType))
				{
					super.loadParams(paramType);
				}
			}
			else
			{
				throw new InvalidStateException("@ChildElement is used on invalid setter method: {}.{}()", method.getDeclaringClass().getName(), method.getName());
			}
		}
		else
		{
			throw new InvalidStateException("@ChildElement is used on non setter/adder method: {}.{}()", method.getDeclaringClass().getName(), method.getName());
		}
		
		name = com.yukthitech.utils.StringUtils.toStartLower(name);
		super.setDetails(name, desc);
	}

	/**
	 * Gets the indicates if multiple elements of this node can be present or not.
	 *
	 * @return the indicates if multiple elements of this node can be present or not
	 */
	public boolean isMultiple()
	{
		return multiple;
	}
	
	/**
	 * Gets the flag indicating if parameter is mandatory or not.
	 *
	 * @return the flag indicating if parameter is mandatory or not
	 */
	public boolean isMandatory()
	{
		return mandatory;
	}
	
	/**
	 * Gets the name of the key attribute.
	 *
	 * @return the name of the key attribute
	 */
	public String getKeyName()
	{
		return keyName;
	}
	
	/**
	 * Gets the description of the key description.
	 *
	 * @return the description of the key description
	 */
	public String getKeyDescription()
	{
		return keyDescription;
	}
	
	/**
	 * Gets the java type associated with this element.
	 *
	 * @return the java type associated with this element
	 */
	public String getType()
	{
		return type;
	}
	
	@Override
	public boolean isExecutable()
	{
		return false;
	}
}
