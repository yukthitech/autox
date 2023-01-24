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
package com.yukthitech.prism.xmlfile;

/**
 * Attribute of xml node.
 */
public class Attribute
{
	/**
	 * Prefix of attribute;
	 */
	private String prefix;
	
	/**
	 * The namespace.
	 */
	private String namespace;
	
	/**
	 * The name.
	 */
	private String name;
	
	/**
	 * The value.
	 */
	private String value;
	
	/**
	 * The attribute type.
	 */
	private Class<?> attributeType;
	
	/**
	 * The name location.
	 */
	private LocationRange nameLocation;
	
	/**
	 * The value location.
	 */
	private LocationRange valueLocation;
	
	/**
	 * Instantiates a new attribute.
	 */
	public Attribute()
	{}

	/**
	 * Instantiates a new attribute.
	 *
	 * @param namespace the namespace
	 * @param name the name
	 * @param value the value
	 * @param nameLocation the name location
	 * @param valueLocation the value location
	 */
	public Attribute(String prefix, String namespace, String name, String value, LocationRange nameLocation, LocationRange valueLocation)
	{
		this.prefix = prefix;
		this.namespace = namespace;
		this.name = name;
		this.value = value;
		this.nameLocation = nameLocation;
		this.valueLocation = valueLocation;
	}
	
	/**
	 * Gets the prefix of attribute;.
	 *
	 * @return the prefix of attribute;
	 */
	public String getPrefix()
	{
		return prefix;
	}

	/**
	 * Sets the prefix of attribute;.
	 *
	 * @param prefix the new prefix of attribute;
	 */
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	/**
	 * Sets the attribute type.
	 *
	 * @param attributeType the new attribute type
	 */
	public void setAttributeType(Class<?> attributeType)
	{
		this.attributeType = attributeType;
	}
	
	/**
	 * Gets the attribute type.
	 *
	 * @return the attribute type
	 */
	public Class<?> getAttributeType()
	{
		return attributeType;
	}

	/**
	 * Gets the namespace.
	 *
	 * @return the namespace
	 */
	public String getNamespace()
	{
		return namespace;
	}

	/**
	 * Sets the namespace.
	 *
	 * @param namespace the new namespace
	 */
	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Gets the name location.
	 *
	 * @return the name location
	 */
	public LocationRange getNameLocation()
	{
		return nameLocation;
	}

	/**
	 * Gets the value location.
	 *
	 * @return the value location
	 */
	public LocationRange getValueLocation()
	{
		return valueLocation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("Value: ").append(value);
		
		builder.append("]");
		return builder.toString();
	}
}
