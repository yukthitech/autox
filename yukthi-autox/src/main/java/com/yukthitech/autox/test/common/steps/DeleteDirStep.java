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
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Deletes specified directory.
 * 
 * @author akiran
 */
@Executable(name = "deleteDir", group = Group.Common, message = "Deletes specified directory.")
public class DeleteDirStep extends AbstractStep
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Path of directory to delete.
	 */
	@Param(description = "Path of directory to delete.", required = true)
	private String path;

	/**
	 * Sets the path of directory to delete.
	 *
	 * @param path the new path of directory to delete
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Deleting directory: {}", path);
		
		File folder = new File(path);
		
		if(!folder.exists())
		{
			exeLogger.debug("Specified path does not exist. So ignoring delete request. Path: {}", path);
			return;
		}
		
		if(!folder.isDirectory())
		{
			exeLogger.debug("Specified path is not a directory. So ignoring delete request. Path: {}", path);
			return;
		}

		try
		{
			FileUtils.deleteDirectory(folder);
			exeLogger.debug("Successfully deleted folder - {}", path);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while deleting folder: {}", path, ex);
		}
	}
}
