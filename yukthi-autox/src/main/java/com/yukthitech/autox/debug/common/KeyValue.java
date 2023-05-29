/**
 * Copyright (c) 2023 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
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
package com.yukthitech.autox.debug.common;

import java.io.Serializable;

/**
 * Encapsulation of context attribute/param related info.
 * @author akranthikiran
 */
public class KeyValue implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of attribute.
	 */
	private String name;
	
	/**
	 * Value of attribute.
	 */
	private byte[] value;
	
	/**
	 * Type of this attribute.
	 */
	private String type;
	
	/**
	 * Size of this attribute (in case of list of map).
	 */
	private Integer size;

	public KeyValue(String name, byte[] value, String type, Integer size)
	{
		this.name = name;
		this.value = value;
		this.type = type;
		this.size = size;
	}

	public String getName()
	{
		return name;
	}

	public byte[] getValue()
	{
		return value;
	}

	public String getType()
	{
		return type;
	}

	public Integer getSize()
	{
		return size;
	}
}
