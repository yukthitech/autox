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

import com.yukthitech.autox.ide.FileParseCollector;

/**
 * Element where attribute is defined.
 * @author akiran
 */
public class AttrDefElement extends CodeElement
{
	/**
	 * Name of the attribute def.
	 */
	private String name;
	
	private int lineNo;

	public AttrDefElement(File file, int pos, int lineNo, String name)
	{
		super(file, pos);
		this.name = name;
		this.lineNo = lineNo;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getLineNo()
	{
		return lineNo;
	}

	@Override
	public void compile(FileParseCollector collector)
	{
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof AttrDefElement))
		{
			return false;
		}

		AttrDefElement other = (AttrDefElement) obj;
		return name.equals(other.name) && super.file.equals(other.file) && (position == other.position);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return name.hashCode() + super.file.hashCode() + position;
	}
}
