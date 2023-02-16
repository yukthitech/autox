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
package com.yukthitech.autox.test.io.steps;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Deletes specified directory.
 * 
 * @author akiran
 */
@Executable(name = "ioDeleteFile", group = Group.Io, message = "Deletes specified file.")
public class DeleteFileStep extends AbstractStep
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Path of file to delete.
	 */
	@Param(description = "Path of file to delete.", required = true, sourceType = SourceType.EXPRESSION)
	private String path;

	/**
	 * Sets the path of file to delete.
	 *
	 * @param path the new path of file to delete
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
		exeLogger.debug("Deleting file: {}", path);
		
		File file = new File(path);
		
		if(!file.exists())
		{
			exeLogger.debug("Specified path does not exist. So ignoring delete request. Path: {}", path);
			return;
		}
		
		if(!file.isFile())
		{
			exeLogger.debug("Specified path is not a file. So ignoring delete request. Path: {}", path);
			return;
		}

		try
		{
			FileUtils.delete(file);
			exeLogger.debug("Successfully deleted file - {}", path);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while deleting file: {}", path, ex);
		}
	}
}
