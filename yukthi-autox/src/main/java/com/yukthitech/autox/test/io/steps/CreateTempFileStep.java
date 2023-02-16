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
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Creates temporary file with specified content.
 * 
 * @author akiran
 */
@Executable(name = "ioCreateTempFile", group = Group.Io, message = "Creates temporary file with specified content.")
public class CreateTempFileStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the attribute to use to set the generated file path.
	 */
	@Param(description = "Name of the attribute to use to set the generated file path.", required = true, attrName = true)
	private String pathAttr;

	/**
	 * Content to be written to the file being created.
	 */
	@Param(description = "Content (in string format) to be written to the file being created. If not specified empty file will be created.", 
			required = false, sourceType = SourceType.EXPRESSION)
	private Object content;

	/**
	 * Prefix to be used for generated file. Default: temp.
	 */
	@Param(description = "Prefix to be used for generated file. Default: temp", required = false, sourceType = SourceType.EXPRESSION)
	private String prefix = "temp";

	/**
	 * Suffix to be used for generated file. Default: .txt.
	 */
	@Param(description = "Suffix to be used for generated file. Default: .txt", required = false, sourceType = SourceType.EXPRESSION)
	private String suffix = ".txt";
	
	@Param(description = "Folder under which file should be created. If not specified, file will be created in work folder.", required = false, sourceType = SourceType.EXPRESSION)
	private String folder;

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
	 * Sets the content to be written to the file being created.
	 *
	 * @param content the new content to be written to the file being created
	 */
	public void setContent(Object content)
	{
		this.content = content;
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

	/**
	 * Sets the suffix to be used for generated file. Default: .txt.
	 *
	 * @param suffix the new suffix to be used for generated file
	 */
	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}
	
	public void setFolder(String folder)
	{
		this.folder = folder;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Creating temp file with [Prefix: {}, Suffix: {}, Path attr: {}, Folder: {}]", prefix, suffix, pathAttr, folder);
		
		try
		{
			File fileToCreate = AutomationUtils.createTempFile(folder, prefix, suffix);

			exeLogger.debug("Created empty temp file '{}'. Now writing specified content to this file", fileToCreate.getPath());
			
			if(content != null)
			{
				FileUtils.write(fileToCreate, content.toString(), Charset.defaultCharset());
			}
			
			context.setAttribute(pathAttr, fileToCreate.getPath());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating temp file", ex);
		}
	}
}
