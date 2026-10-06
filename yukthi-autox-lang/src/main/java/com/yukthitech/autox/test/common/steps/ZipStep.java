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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.ZipUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Zips specified file(s) into specified zip file.
 * 
 * @author akiran
 */
@Executable(name = "zip", group = Group.Common, message = "Zips specified file(s) into specified zip file.")
public class ZipStep extends AbstractStep
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	@Param(description = "Zip file to be created. If not specified, a temp file will be created.", required = false, sourceType = SourceType.EXPRESSION)
	private String zipFile;

	@Param(description = "Root folder whose files will be zipped.", required = true, sourceType = SourceType.EXPRESSION)
	private String rootFolder;

	@Param(description = "Collection of file names. If specified, only these files from root folder will be zipped.", required = false, sourceType = SourceType.EXPRESSION)
	private Object fileNames;

	@Param(description = "Attribute name to be set with generated zip file path. Default: zipFilePath", required = false, 
			sourceType = SourceType.EXPRESSION, attrName = true)
	private String zipFileAttr = "zipFilePath";

	public void setZipFile(String zipFile)
	{
		this.zipFile = zipFile;
	}

	public void setRootFolder(String rootFolder)
	{
		this.rootFolder = rootFolder;
	}

	public void setFileNames(Object fileNames)
	{
		this.fileNames = fileNames;
	}

	public void setZipFileAttr(String zipFileAttr)
	{
		this.zipFileAttr = zipFileAttr;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		File rootFolder = new File(this.rootFolder);
		
		if(!rootFolder.exists() || !rootFolder.isDirectory())
		{
			throw new InvalidArgumentException("Invalid root-folder specified: {}", rootFolder.getPath());
		}

		File outZipFile = StringUtils.isNotBlank(this.zipFile) ? new File(this.zipFile) : 
			AutomationUtils.createTempFile(null, "zip-step", ".zip");
		
		exeLogger.debug("Zipping files from folder {} as zip file: {}", rootFolder.getPath(), outZipFile.getPath());
		
		List<String> fileNames = null;
		
		if(this.fileNames != null)
		{
			if(!(this.fileNames instanceof Collection))
			{
				throw new InvalidArgumentException("Specified file-names did not result in collection. Specified files type: {}", 
						this.fileNames.getClass().getName());
			}
			
			fileNames = new ArrayList<>((Collection) this.fileNames);
		}
		
		if(CollectionUtils.isNotEmpty(fileNames))
		{
			exeLogger.debug("Limiting zipping to {} files from root folder", fileNames.size());
			
			Map<String, File> files = new HashMap<>();
			
			for(String fileName : fileNames)
			{
				File file = new File(rootFolder, fileName);
				
				if(!file.exists())
				{
					throw new InvalidArgumentException("Specified file '{}' does not exist in specified root folder: {}", fileName, rootFolder.getPath());
				}
				
				files.put(fileName, file);
			}
			
			ZipUtils.zipFiles(files, outZipFile);
		}
		else
		{
			ZipUtils.zipDirectory(rootFolder, outZipFile);
		}

		exeLogger.debug("Created zip file '{}' successfully. Setting the path as attr: {}", outZipFile.getPath(), zipFileAttr);

		context.setAttribute(zipFileAttr, outZipFile.getPath());
	}
}
