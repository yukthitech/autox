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
package com.yukthitech.autox.test.lang.steps;

import java.io.File;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Validates specified path exists.
 */
@Executable(name = "assertFileExists", group = Group.Common, message = "Validates specified path exists.")
public class AssertFileExists extends AbstractValidation
{
	private static final long serialVersionUID = 1L;
	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Path of file to check.")
	private String path;

	/**
	 * Sets the name of the data source to use.
	 *
	 * @param path the new name of the data source to use
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Checking for file existence: {}", path);
		
		File file = new File(path);
		
		exeLogger.debug("Found file '{}' existence status as: {}", path, file.exists());
		
		if(!file.exists())
		{
			throw new AutoxValidationException(this, "Found file does not exist: {}", path);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Path: ").append(path);

		builder.append("]");
		return builder.toString();
	}
}
