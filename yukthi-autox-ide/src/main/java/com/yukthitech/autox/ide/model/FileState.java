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
package com.yukthitech.autox.ide.model;

import java.io.Serializable;

/**
 * State of open project file.
 * @author akiran
 */
public class FileState implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Path of the open file.
	 */
	private String path;
	
	/**
	 * Position of the cursor in the file.
	 */
	private int cursorPositon;
	
	/**
	 * Instantiates a new file state.
	 */
	public FileState()
	{}

	/**
	 * Instantiates a new file state.
	 *
	 * @param path the path
	 * @param cursorPositon the cursor positon
	 */
	public FileState(String path, int cursorPositon)
	{
		this.path = path;
		this.cursorPositon = cursorPositon;
	}

	/**
	 * Gets the path of the open file.
	 *
	 * @return the path of the open file
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Sets the path of the open file.
	 *
	 * @param path the new path of the open file
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * Gets the position of the cursor in the file.
	 *
	 * @return the position of the cursor in the file
	 */
	public int getCursorPositon()
	{
		return cursorPositon;
	}

	/**
	 * Sets the position of the cursor in the file.
	 *
	 * @param cursorPositon the new position of the cursor in the file
	 */
	public void setCursorPositon(int cursorPositon)
	{
		this.cursorPositon = cursorPositon;
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

		if(!(obj instanceof FileState))
		{
			return false;
		}

		FileState other = (FileState) obj;
		return path.equals(other.path);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return path.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append(path);
		
		builder.append("]");
		return builder.toString();
	}

}
