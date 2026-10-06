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
 * Creates temporary file with specified content.
 * 
 * @author akiran
 */
@Executable(name = "ioCreateTempDir", group = Group.Io, message = "Creates temporary directory within the work folder.")
public class CreateTempDirStep extends AbstractStep
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the attribute to use to set the generated file path.
	 */
	@Param(description = "Name of the attribute to use to set the generated file path.", required = true, attrName = true)
	private String pathAttr;

	/**
	 * Prefix to be used for generated file. Default: temp.
	 */
	@Param(description = "Prefix to be used for generated file. Default: temp", required = false, sourceType = SourceType.EXPRESSION)
	private String prefix = "temp";

	/**
	 * Sets the name of the attribute to use to set the generated file path.
	 *
	 * @param pathAttr the new name of the attribute to use to set the generated file path
	 */
	public void setPathAttr(String pathAttr)
	{
		this.pathAttr = pathAttr;
	}

	/**
	 * Sets the prefix to be used for generated file. Default: temp.
	 *
	 * @param prefix the new prefix to be used for generated file
	 */
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Creating temp dir with [Prefix: {}, Path attr: {}]", prefix, pathAttr);
		
		try
		{
			String timeStamp = Long.toHexString(System.currentTimeMillis());
			
			File tempFolder = null;
			boolean found = false;
			
			for(int i = 1; i <= 50; i++)
			{
				tempFolder = new File(context.getWorkDirectory().getPath(), String.format("%s-%s-%s", prefix, timeStamp, i));
				
				if(!tempFolder.exists())
				{
					found = true;
					break;
				}
			}
			
			if(!found)
			{
				throw new InvalidStateException("Failed to create temp directory even after 50 attempts.");
			}
			
			FileUtils.forceMkdir(tempFolder);
			
			exeLogger.debug("Created empty temp folder '{}'.", tempFolder.getPath());
			
			context.setAttribute(pathAttr, tempFolder.getPath());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating temp folder", ex);
		}
	}
}
