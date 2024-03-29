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
package com.yukthitech.prism.index;

import java.io.File;

import com.yukthitech.autox.common.IndexRange;

/**
 * Represents an element which can be referenced.
 * @author akranthikiran
 */
public class ReferableElement
{
	/**
	 * Type of this referable element.
	 */
	private ReferenceType type;
	
	/**
	 * Name of the element.
	 */
	private String name;
	
	/**
	 * Scope in which this element is accessible.
	 */
	private String scope;
	
	/**
	 * File in which this element is defined.
	 */
	private File file;
	
	/**
	 * Range to be used to select when this element is referred.
	 */
	private IndexRange selectionRange;

	public ReferableElement(ReferenceType type, String name, String scope, File file, IndexRange selectionRange)
	{
		this.type = type;
		this.name = name;
		this.scope = scope;
		this.file = file;
		this.selectionRange = selectionRange;
	}

	public ReferenceType getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public String getScope()
	{
		return scope;
	}

	public File getFile()
	{
		return file;
	}

	public IndexRange getSelectionRange()
	{
		return selectionRange;
	}
	
	public static String getRefName(String scope, String name, ReferenceType type)
	{
		if(scope == null)
		{
			return name + "@" + type;
		}
		
		return scope + ">" + name + "@" + type;
	}
	
	public String getRefName()
	{
		return getRefName(scope, name, type);
	}
}
