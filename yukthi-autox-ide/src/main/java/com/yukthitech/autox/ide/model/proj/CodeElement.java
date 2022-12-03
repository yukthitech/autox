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
package com.yukthitech.autox.ide.model.proj;

import java.io.File;
import java.util.Set;

import com.yukthitech.autox.ide.FileParseCollector;

/**
 * Base class for elements.
 * @author akiran
 */
public abstract class CodeElement
{
	/**
	 * File in which element is defined.
	 */
	protected File file;
	
	/**
	 * Position at which element is defined.
	 */
	protected int position;
	
	protected CodeElement parent;

	/**
	 * Instantiates a new element.
	 *
	 * @param file the file
	 * @param position the position
	 */
	public CodeElement(File file, int position)
	{
		this.file = file;
		this.position = position;
	}

	/**
	 * Gets the file in which element is defined.
	 *
	 * @return the file in which element is defined
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Gets the position at which element is defined.
	 *
	 * @return the position at which element is defined
	 */
	public int getPosition()
	{
		return position;
	}
	
	void setParent(CodeElement parent)
	{
		this.parent = parent;
	}
	
	protected void addFileElement(CodeElement element)
	{
		if(parent != null)
		{
			parent.addFileElement(element);
		}
	}
	
	/**
	 * Checks for reference errors recursively.
	 * @param collector
	 */
	public abstract void compile(FileParseCollector collector);
	
	public boolean isValidAppProperty(String name)
	{
		return parent.isValidAppProperty(name);
	}
	
	/**
	 * Fetches the def of specified attr into specified set.
	 * @param name name of attr being searched
	 * @param attrDefElem set to collect defs
	 */
	public void getAttrDef(String name, Set<AttrDefElement> attrDefElem)
	{}

	public void getFunctionDef(String name, Set<FunctionDefElement> funcDefSet)
	{}
}
