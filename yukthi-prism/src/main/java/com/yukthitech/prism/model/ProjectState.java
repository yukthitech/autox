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
package com.yukthitech.prism.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * State of the project.
 * @author akiran
 */
public class ProjectState implements Serializable
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the open project.
	 */
	private String path;
	
	/**
	 * Open files state.
	 */
	private Set<FileState> openFiles = new LinkedHashSet<>();
	
	/**
	 * Instantiates a new project state.
	 */
	public ProjectState()
	{}
	
	/**
	 * Instantiates a new project state.
	 *
	 * @param path the name
	 */
	public ProjectState(String path)
	{
		this.path = path;
	}

	/**
	 * Gets the name of the open project.
	 *
	 * @return the name of the open project
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Sets the name of the open project.
	 *
	 * @param path the new name of the open project
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * Gets the open files state.
	 *
	 * @return the open files state
	 */
	public Set<FileState> getOpenFiles()
	{
		return openFiles;
	}

	/**
	 * Sets the open files state.
	 *
	 * @param openFiles the new open files state
	 */
	public void setOpenFiles(Set<FileState> openFiles)
	{
		if(openFiles != null)
		{
			this.openFiles.removeAll(openFiles);
			this.openFiles.addAll(openFiles);
		}
	}
	
	/**
	 * Adds open file to project state.
	 * @param fileState
	 */
	public void addOpenFile(FileState fileState)
	{
		this.openFiles.remove(fileState);
		this.openFiles.add(fileState);
	}
	
	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
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

		if(!(obj instanceof ProjectState))
		{
			return false;
		}

		ProjectState other = (ProjectState) obj;
		return path.equals(other.path);
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
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
