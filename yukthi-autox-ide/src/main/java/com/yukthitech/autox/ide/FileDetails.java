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
package com.yukthitech.autox.ide;

import java.io.File;

import com.yukthitech.autox.ide.model.Project;

/**
 * File details of the ide.
 * @author akiran
 */
public class FileDetails implements Comparable<FileDetails>
{
	private File file;
	
	private Project project;
	
	private String path;
	
	public FileDetails(File file, Project project)
	{
		this.file = file;
		this.project = project;
		
		this.path = IdeFileUtils.getRelativePath(project.getBaseFolder(), file);
	}

	public File getFile()
	{
		return file;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	public String getPath()
	{
		return path;
	}

	@Override
	public int compareTo(FileDetails o)
	{
		int diff = file.getName().compareTo(o.file.getName());
		
		if(diff == 0)
		{
			return file.compareTo(o.file);
		}
		
		return diff;
	}

	@Override
	public String toString()
	{
		return file.getName();
	}
}
