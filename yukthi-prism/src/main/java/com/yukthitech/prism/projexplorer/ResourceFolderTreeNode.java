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

class ResourceFolderTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	public ResourceFolderTreeNode(String id, ProjectExplorer projectExplorer, Project project, String name, File resourceFolder)
	{
		super(id, projectExplorer, project, name, resourceFolder);
		
		super.setIcon(IdeUtils.loadIcon("/ui/icons/resource-folder.svg", 20));
		reload(false);
	}
}