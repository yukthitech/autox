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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.utils.ZipUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Unzips specified file or resource to specified directory.
 * 
 * @author akiran
 */
@Executable(name = "unzip", group = Group.Common, message = "Unzips specified file or resource to specified directory.")
public class UnzipStep extends AbstractStep
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Zip file to be unzipped. Either of zipFile or ZipResouce is mandatory.
	 */
	@Param(description = "Zip file to be unzipped. Either of zipFile or ZipResouce is mandatory.", required = false)
	private String zipFile;

	/**
	 * Zip file to be unzipped. Either of zipFile or ZipResouce is mandatory.
	 */
	@Param(description = "Zip resource to be unzipped. Either of zipFile or ZipResouce is mandatory.", required = false)
	private String zipResource;

	/**
	 * Folder path in which unzip should happen.
	 */
	@Param(description = "Folder path in which unzip should happen.", required = true)
	private String outFolder;

	/**
	 * Sets the zip file to be unzipped. Either of zipFile or ZipResouce is mandatory.
	 *
	 * @param zipFile the new zip file to be unzipped
	 */
	public void setZipFile(String zipFile)
	{
		this.zipFile = zipFile;
	}

	/**
	 * Sets the zip file to be unzipped. Either of zipFile or ZipResouce is mandatory.
	 *
	 * @param zipResource the new zip file to be unzipped
	 */
	public void setZipResource(String zipResource)
	{
		this.zipResource = zipResource;
	}

	/**
	 * Sets the folder path in which unzip should happen.
	 *
	 * @param outFolder the new folder path in which unzip should happen
	 */
	public void setOutFolder(String outFolder)
	{
		this.outFolder = outFolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.
	 * AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		File outFolder = new File(this.outFolder);
		
		if(!outFolder.exists() || !outFolder.isDirectory())
		{
			throw new InvalidArgumentException("Invalid output-directory specified: {}", outFolder.getPath());
		}
		
		InputStream is = null;
		
		if(zipFile != null)
		{
			is = new FileInputStream(new File(zipFile));
		}
		else
		{
			is = UnzipStep.class.getResourceAsStream(zipResource);
			
			if(is == null)
			{
				throw new InvalidArgumentException("Invalid zipResource specified: {}", zipResource);
			}
		}
		
		List<File> files = ZipUtils.unzip(is, outFolder);
		exeLogger.debug("Unzipped {} files successfully", files.size());
		
		is.close();
	}
	
	@Override
	public void validate() throws ValidateException
	{
		super.validate();
		
		if(StringUtils.isBlank(zipFile) && StringUtils.isBlank(zipResource))
		{
			throw new ValidateException("Either of zipFile or zipResource is mandatory.");
		}
	}
}
