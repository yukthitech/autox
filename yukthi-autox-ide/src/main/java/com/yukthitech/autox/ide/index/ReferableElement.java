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
package com.yukthitech.autox.ide.index;

import java.io.File;

import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.ide.xmlfile.IndexRange;

/**
 * Represents an element which can be referenced.
 * @author akranthikiran
 */
public class ReferableElement
{
	/**
	 * Type of this referable element.
	 */
	private String type;
	
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
	 * Actual element in the xml.
	 */
	private Element element;
	
	/**
	 * Range to be used to select when this element is referred.
	 */
	private IndexRange selectionRange;

	public ReferableElement(String type, String name, String scope, File file, Element element, IndexRange selectionRange)
	{
		this.type = type;
		this.name = name;
		this.scope = scope;
		this.file = file;
		this.element = element;
		this.selectionRange = selectionRange;
	}

	public String getType()
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

	public Element getElement()
	{
		return element;
	}
	
	public IndexRange getSelectionRange()
	{
		return selectionRange;
	}
	
	public static String getRefName(String scope, String name, String type)
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
