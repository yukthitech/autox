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
package com.yukthitech.prism.projexplorer;

import java.io.File;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.model.Project;

class FileTreeNode extends BaseTreeNode 
{
	private static final long serialVersionUID = 1L;
	
	private File file;
	
	private Project project;
	
	private ProjectExplorer projectExplorer;

	public FileTreeNode(String id, ProjectExplorer projectExplorer, Project project, String name, File file)
	{
		super(id, projectExplorer.getProjectTreeModel());
		
		if(project == null)
		{
			throw new NullPointerException("Project can not be null");
		}
		
		if(file == null)
		{
			throw new NullPointerException("File can not be null.");
		}
		
		this.projectExplorer = projectExplorer;
		this.project = project;
		
		super.setLabel(name);
		super.setIcon(IdeUtils.getFileIcon(file));
		this.file=file;
		
		//reload(false);
	}
	
	@Override
	protected void reloadOnInit()
	{
		reload(false);
	}
	
	@Override
	public void reload(boolean childReload)
	{
		if(projectExplorer != null)
		{
			projectExplorer.checkFile(this);
		}
	}
	
	@Override
	public void rename(String id, String newName)
	{
		super.rename(id, newName);
		
		File newFile = new File(file.getParentFile(), newName);
		this.file = newFile;
	}
	
	public void parentFolderRenamed(File newParent)
	{
		File newFile = new File(newParent, this.file.getName());
		this.file = newFile;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	public File getFile()
	{
		return file;
	}
}
