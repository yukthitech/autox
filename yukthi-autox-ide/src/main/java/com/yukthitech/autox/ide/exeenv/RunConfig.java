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
package com.yukthitech.autox.ide.exeenv;

import java.io.Serializable;

/**
 * Run configuration.
 * @author akranthikiran
 */
public class RunConfig implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the run config.
	 */
	private String name;
	
	/**
	 * Execution type.
	 */
	private ExecutionType executionType;
	
	/**
	 * Project name under which this execution executed.
	 */
	private String projectName;
	
	/**
	 * Name of the executable.
	 */
	private String executableName;
	
	/**
	 * Unique id of the execution.
	 */
	private transient String uniqueId;
	
	/**
	 * Instantiates a new run config.
	 */
	public RunConfig()
	{
	}

	public RunConfig(ExecutionType executionType, String projectName, String executableName)
	{
		this.executionType = executionType;
		this.projectName = projectName;
		this.executableName = executableName;
	}

	/**
	 * Gets the name of the run config.
	 *
	 * @return the name of the run config
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the run config.
	 *
	 * @param name the new name of the run config
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the execution type.
	 *
	 * @return the execution type
	 */
	public ExecutionType getExecutionType()
	{
		return executionType;
	}

	/**
	 * Sets the execution type.
	 *
	 * @param executionType the new execution type
	 */
	public void setExecutionType(ExecutionType executionType)
	{
		this.executionType = executionType;
	}

	/**
	 * Gets the project name under which this execution executed.
	 *
	 * @return the project name under which this execution executed
	 */
	public String getProjectName()
	{
		return projectName;
	}

	/**
	 * Sets the project name under which this execution executed.
	 *
	 * @param projectName the new project name under which this execution
	 *        executed
	 */
	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	/**
	 * Gets the name of the executable.
	 *
	 * @return the name of the executable
	 */
	public String getExecutableName()
	{
		return executableName;
	}

	/**
	 * Sets the name of the executable.
	 *
	 * @param executableName the new name of the executable
	 */
	public void setExecutableName(String executableName)
	{
		this.executableName = executableName;
	}
	
	public synchronized String getUniqueId()
	{
		if(uniqueId != null)
		{
			return uniqueId;
		}
		
		String id = projectName + "#" + executionType.name() + "#" + executableName;
		id = id.replaceAll("\\W", "_");
		return (uniqueId = id);
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

		if(!(obj instanceof RunConfig))
		{
			return false;
		}

		RunConfig other = (RunConfig) obj;
		return getUniqueId().equals(other.getUniqueId());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return getUniqueId().hashCode();
	}
}
