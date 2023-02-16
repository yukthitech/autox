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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Lists files from specified directory.
 * 
 * @author akiran
 */
@Executable(name = "ioListFiles", group = Group.Io, message = "Fetches file list from specified directory.")
public class ListFilesStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	@Param(description = "Folder under which files list to be fetched.", required = true, sourceType = SourceType.EXPRESSION)
	private String folder;

	/**
	 * File pattern to be fetched.
	 */
	@Param(description = "File regex pattern to be matche", required = false, sourceType = SourceType.EXPRESSION)
	private String pattern;
	
	@Param(description = "Flag indicating folders has to be included. Default true", required = false)
	private boolean folders = true;
	
	@Param(description = "Flag indicating files has to be included. Default true", required = false)
	private boolean files = true;

	@Param(description = "Name of attribute to which file list has to be set. Default: fileList", required = false)
	private String listAttr = "fileList";

	public void setFolder(String folder)
	{
		this.folder = folder;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public void setFolders(boolean folders)
	{
		this.folders = folders;
	}

	public void setFiles(boolean files)
	{
		this.files = files;
	}

	public void setListAttr(String listAttr)
	{
		this.listAttr = listAttr;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Listing file with parameters: [Folder: {}, Pattern: {}, Folders: {}, Files: {}, List attr: {}]", 
				folder, pattern, folders, files, listAttr);
		
		File file = new File(folder);
		Pattern filePattern = pattern == null ? null : Pattern.compile(pattern);
		
		List<String> resLst = new ArrayList<>();
		
		file.listFiles(sfile -> 
		{
			if(filePattern != null && !filePattern.matcher(sfile.getName()).matches())
			{
				return false;
			}
			
			if(folders && !sfile.isDirectory())
			{
				return false;
			}
			
			if(files && !sfile.isFile())
			{
				return false;
			}
			
			resLst.add(sfile.getPath());

			return false;
		});
		
		exeLogger.debug("Got filtered number of files as: {}", resLst.size());
		context.setAttribute(listAttr, resLst);
	}
}
