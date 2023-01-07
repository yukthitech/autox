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

import java.io.File;

import com.yukthitech.autox.ide.exeenv.debug.IdeDebugPoint;
import com.yukthitech.autox.ide.model.Project;

/**
 * Parameters encapsulation for execution.
 * @author akranthikiran
 */
public class ExecuteCommand
{
	/**
	 * Execution type.
	 */
	private ExecutionType executionType;
	
	/**
	 * Owning project.
	 */
	private Project project;
	
	/**
	 * Parent test suite folder.
	 */
	private File testSuiteFolder;
	
	/**
	 * Name of executable to execute.
	 */
	private String name;
	
	/**
	 * If execution should be in debug mode.
	 */
	private boolean debug;
	
	/**
	 * Line details where execution should pause.
	 */
	private IdeDebugPoint runToLinePoint;

	public ExecuteCommand(ExecutionType executionType, Project project, File testSuiteFolder, String name, boolean debug)
	{
		this.executionType = executionType;
		this.project = project;
		this.testSuiteFolder = testSuiteFolder;
		this.name = name;
		this.debug = debug;
	}

	public ExecutionType getExecutionType()
	{
		return executionType;
	}

	public Project getProject()
	{
		return project;
	}

	public File getTestSuiteFolder()
	{
		return testSuiteFolder;
	}

	public String getName()
	{
		return name;
	}

	public boolean isDebug()
	{
		return debug;
	}
	
	public ExecuteCommand setRunToLinePoint(File file, int lineNo)
	{
		this.runToLinePoint = new IdeDebugPoint(project.getName(), file, lineNo);
		return this;
	}
	
	public IdeDebugPoint getRunToLinePoint()
	{
		return runToLinePoint;
	}
}
