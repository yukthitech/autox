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
package com.yukthitech.autox.test.common.steps;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.TestCaseFailedException;

/**
 * Creates a directory with required parent folder as needed in work folder.
 * 
 * @author akiran
 */
@Executable(name = "mkdir", group = Group.Common, message = "Creates a directory with required parent folder as needed in work folder.")
public class MkDirStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Directory path to create.
	 */
	@Param(description = "Directory path to create.")
	private String path;

	/**
	 * Context attribute to which result folder path will be set.
	 */
	@Param(description = "Context attribute to which result folder path will be set")
	private String name;

	/**
	 * Sets the directory path to create.
	 *
	 * @param path the new directory path to create
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * Sets the context attribute to which result folder path will be set.
	 *
	 * @param name the new context attribute to which result folder path will be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.IStep#execute(com.yukthitech.automation.AutomationContext, com.yukthitech.automation.IExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Creating directory {} in work folder: {}", path, context.getWorkDirectory().getPath());
		
		File dirToCreate = new File(context.getWorkDirectory().getPath() + File.separator + path);
		
		try
		{
			FileUtils.forceMkdir(dirToCreate);
			
			exeLogger.debug("Setting created directory path {} on context with attribute - {}", dirToCreate, name);
			
			context.setAttribute(name, dirToCreate);
		}catch(Exception ex)
		{
			//exeLogger.error(ex, "An error occurred while creating directory - {}", path);
			throw new TestCaseFailedException(this, "An error occurred while creating directory - {}", path, ex);
		}
	}
}
