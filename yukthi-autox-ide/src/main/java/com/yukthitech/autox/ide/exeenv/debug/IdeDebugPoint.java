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
package com.yukthitech.autox.ide.exeenv.debug;

import java.io.File;
import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * Represents a debug point.
 * @author akranthikiran
 */
public class IdeDebugPoint implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Project of source file in which debug point is created.
	 */
	private String project;
	
	/**
	 * File in which debug point is created.
	 */
	private File file;
	
	/**
	 * Line number at which debug point is created.
	 */
	private int lineNo;
	
	private String condition;
	
	public IdeDebugPoint(String project, File file, int lineNo)
	{
		this.project = project;
		this.file = file;
		this.lineNo = lineNo;
	}

	public String getProject()
	{
		return project;
	}

	public File getFile()
	{
		return file;
	}
	
	public void setLineNo(int lineNo)
	{
		this.lineNo = lineNo;
	}

	public int getLineNo()
	{
		return lineNo;
	}
	
	public String getCondition()
	{
		return condition;
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
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

		if(!(obj instanceof IdeDebugPoint))
		{
			return false;
		}

		IdeDebugPoint other = (IdeDebugPoint) obj;
		return file.equals(other.file) && lineNo == other.lineNo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return Objects.hashCode(file, lineNo);
	}
}
