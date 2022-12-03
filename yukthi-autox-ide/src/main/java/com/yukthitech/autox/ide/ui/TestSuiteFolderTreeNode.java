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
package com.yukthitech.autox.ide.ui;

import java.io.File;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.projexplorer.FileTreeNode;
import com.yukthitech.autox.ide.projexplorer.FolderTreeNode;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;
import com.yukthitech.autox.ide.projexplorer.TestFolderTreeNode;

public class TestSuiteFolderTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	public TestSuiteFolderTreeNode(String id, ProjectExplorer projectExplorer, Project project, String name, File testSuiteFolder)
	{
		super(id, projectExplorer, project, name, testSuiteFolder);
		
		super.setIcon(IdeUtils.loadIcon("/ui/icons/source-folder.svg", 20));
		reload(false);
	}
	
	public FileTreeNode getFileNode(File file, String path[], int index)
	{
		String relativePath = IdeFileUtils.getRelativePath(super.getFolder(), file);
		
		if(relativePath == null || relativePath.length() == 0)
		{
			return null;
		}
		
		String newPath[] = relativePath.split("\\" + File.separator);
		return super.getFileNode(file, newPath, 0);
	}
	
	@Override
	protected FolderTreeNode newFolderTreeNode(String id, ProjectExplorer projectExplorer, Project project, String name, File folder)
	{
		return new TestFolderTreeNode(id, projectExplorer, project, name, folder);
	}
}