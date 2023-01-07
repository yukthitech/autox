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
package com.yukthitech.autox.debug.common;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * Represents debug point.
 * @author akranthikiran
 */
public class DebugPoint implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Path of file in which debug point is added.
	 */
	private String filePath;
	
	/**
	 * Line number of this debug point.
	 */
	private int lineNumber;
	
	/**
	 * Condition of debug point.
	 */
	private String condition;
	
	/**
	 * Indicates this debug point has to be used for only one pause.
	 */
	private boolean singlePauseOnly;
	
	public DebugPoint()
	{}
	
	public DebugPoint(String filePath, int lineNumber, String condition)
	{
		this.filePath = filePath;
		this.lineNumber = lineNumber;
		this.condition = condition;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	public String getCondition()
	{
		return condition;
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
	}
	
	public boolean isSinglePauseOnly()
	{
		return singlePauseOnly;
	}

	public void setSinglePauseOnly(boolean onePauseOnly)
	{
		this.singlePauseOnly = onePauseOnly;
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

		if(!(obj instanceof DebugPoint))
		{
			return false;
		}

		DebugPoint other = (DebugPoint) obj;
		return filePath.equals(other.filePath) && (lineNumber == other.lineNumber);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return Objects.hashCode(filePath, lineNumber);
	}
}
