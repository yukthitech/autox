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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.Attributable;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.ccg.xml.XMLUtil;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Information about the parameter.
 * @author akiran
 */
public class ParamInfo implements Comparable<ParamInfo>
{
	/**
	 * Name of the parameter.
	 */
	private String name;
	
	/**
	 * Name with hyphens.
	 */
	private String nameWithHyphens;
	
	/**
	 * Description about the parameter.
	 */
	private String description;
	
	/**
	 * Flag indicating if parameter is mandatory or not.
	 */
	private boolean mandatory;
	
	/**
	 * Type of the parameter.
	 */
	private String type;
	
	/**
	 * Type of source for this param.
	 */
	private SourceType sourceType;
	
	/**
	 * Flag indicating if this parameter represents attribute name.
	 */
	private boolean attrName;
	
	/**
	 * Default value used by param.
	 */
	private String defaultValue;
	
	/**
	 * Flag indicating if this param can be specified as attribute or not.
	 */
	private boolean attributable;
	
	/**
	 * Instantiates a new param info.
	 *
	 * @param field the field
	 * @param paramAnnot the param annot
	 */
	public ParamInfo(Field field, Param paramAnnot)
	{
		this.name = field.getName();
		this.nameWithHyphens = name.replaceAll("([A-Z])", "-$1").toLowerCase();
		
		if(StringUtils.isNotBlank(paramAnnot.name()))
		{
			this.name = paramAnnot.name();
		}
		
		this.description = paramAnnot.description();
		this.mandatory = paramAnnot.required();
		this.sourceType = paramAnnot.sourceType();
		this.attrName = paramAnnot.attrName();
		this.defaultValue = paramAnnot.defaultValue();
		
		Type genericType = field.getGenericType();
		
		if(genericType instanceof Class)
		{
			Class<?> cls = (Class<?>) genericType; 
			type = cls.getName();
			
			attributable = (Object.class.equals(cls) || XMLUtil.isSupportedAttributeClass(cls))
					&& (paramAnnot.attributable() == Attributable.DEFAULT || paramAnnot.attributable() == Attributable.TRUE);
		}
		else if(genericType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			StringBuilder builder = new StringBuilder( ((Class<?>) parameterizedType.getRawType()).getName());
			
			builder.append("<");
			
			for(Type type : parameterizedType.getActualTypeArguments())
			{
				builder.append(type.getTypeName());
			}
			
			builder.append(">");
			
			type = builder.toString();
			attributable = (paramAnnot.attributable() == Attributable.TRUE);
		}
		else
		{
			throw new InvalidStateException("Unsupported field type encountered for field {}.{}", field.getDeclaringClass().getName(), field.getName());
		}
	}

	/**
	 * Instantiates a new param info.
	 *
	 * @param name the name
	 * @param description the description
	 * @param type the type
	 */
	public ParamInfo(String name, String description, String type)
	{
		this.name = name;
		this.description = description;
		this.type = type;
	}

	/**
	 * Gets the name of the parameter.
	 *
	 * @return the name of the parameter
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the name with hyphens.
	 *
	 * @return the name with hyphens
	 */
	public String getNameWithHyphens()
	{
		return nameWithHyphens;
	}

	/**
	 * Gets the description about the parameter.
	 *
	 * @return the description about the parameter
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Checks if is flag indicating if parameter is mandatory or not.
	 *
	 * @return the flag indicating if parameter is mandatory or not
	 */
	public boolean isMandatory()
	{
		return mandatory;
	}

	/**
	 * Gets the type of the parameter.
	 *
	 * @return the type of the parameter
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Gets the type of source for this param.
	 *
	 * @return the type of source for this param
	 */
	public SourceType getSourceType()
	{
		return sourceType;
	}
	
	/**
	 * Gets the flag indicating if this parameter represents attribute name.
	 *
	 * @return the flag indicating if this parameter represents attribute name
	 */
	public boolean isAttrName()
	{
		return attrName;
	}

	/**
	 * Gets the default value used by param.
	 *
	 * @return the default value used by param
	 */
	public String getDefaultValue()
	{
		return defaultValue;
	}
	
	/**
	 * Checks if is flag indicating if this param can be specified as attribute or not.
	 *
	 * @return the flag indicating if this param can be specified as attribute or not
	 */
	public boolean isAttributable()
	{
		return attributable;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ParamInfo o)
	{
		return this.name.compareTo(o.name);
	}
}
