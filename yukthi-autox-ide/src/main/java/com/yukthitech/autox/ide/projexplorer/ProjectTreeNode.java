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
package com.yukthitech.autox.ide.projexplorer;

import static com.yukthitech.autox.ide.projexplorer.IProjectExplorerConstants.ID_PREFIX_APP_CONFIG;
import static com.yukthitech.autox.ide.projexplorer.IProjectExplorerConstants.ID_PREFIX_APP_PROP;
import static com.yukthitech.autox.ide.projexplorer.IProjectExplorerConstants.ID_PREFIX_RESOURCE_FOLDER;
import static com.yukthitech.autox.ide.projexplorer.IProjectExplorerConstants.ID_PREFIX_TEST_SUITE_FOLDER;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;

public class ProjectTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	private Project project;
	
	private List<TestSuiteFolderTreeNode> testSuiteFolderNodes = new ArrayList<>();
	
	private List<ResourceFolderTreeNode> resourceFolderNodes = new ArrayList<>();
	
	private FileTreeNode appConfigNode;
	
	private FileTreeNode appPropNode;
	
	public ProjectTreeNode(String id, ProjectExplorer projectExplorer, Project project)
	{
		super(IProjectExplorerConstants.ID_PREFIX_PROJECT + id, 
				projectExplorer, IdeUtils.loadIcon("/ui/icons/project.svg", 20), project, project.getName(), new File(project.getBaseFolderPath()));

		this.project = project;
		
		//reload once project is set
		reload(false);
	}
	
	public File getTestSuiteFolder(File file)
	{
		for(TestSuiteFolderTreeNode node : this.testSuiteFolderNodes)
		{
			String relPath = IdeFileUtils.getRelativePath(node.getFolder(), file);
			
			if(relPath != null)
			{
				return node.getFolder();
			}
		}
		
		return null;
	}
	
	@Override
	public void reload(boolean childReload)
	{
		//project will be null during init, and when this method is called by super class <init>
		if(project == null)
		{
			return;
		}
		
		if(!super.getLabel().equals(project.getName()))
		{
			super.setLabel(project.getName());
		}
		
		super.reload(childReload);
		
		synchronized(testSuiteFolderNodes)
		{
			testSuiteFolderNodes.clear();
			resourceFolderNodes.clear();
			
			super.visitChildNodes(node -> 
			{
				if(node instanceof TestSuiteFolderTreeNode)
				{
					testSuiteFolderNodes.add((TestSuiteFolderTreeNode) node);
					return true;
				}
				else if(node instanceof ResourceFolderTreeNode)
				{
					resourceFolderNodes.add((ResourceFolderTreeNode) node);
					return true;
				}
				else if(node.getId().startsWith(IProjectExplorerConstants.ID_PREFIX_APP_CONFIG))
				{
					appConfigNode = (FileTreeNode) node;
					return true;
				}
				else if(node.getId().startsWith(IProjectExplorerConstants.ID_PREFIX_APP_PROP))
				{
					appPropNode = (FileTreeNode) node;
					return true;
				}
				
				//all test suite folders will exist on top
				// so if non-test suite folder is found, then no more
				// test suite folders can be expected.
				return false;
			});
		}
	}
	
	protected List<NodeInfo> getNodes()
	{
		List<NodeInfo> nodes = super.getNodes();
		
		Set<String> testSuitesFolders = project.getTestSuitesFoldersList();
		
		if(testSuitesFolders != null)
		{
			testSuitesFolders = new TreeSet<>(testSuitesFolders);
			
			for(String tsf : testSuitesFolders)
			{
				File file = new File(project.getBaseFolderPath(), tsf);
				String id = ID_PREFIX_TEST_SUITE_FOLDER + tsf;
				
				nodes.add(NodeInfo.newTestSuiteFolderNode(id, file, tsf));
			}
		}
		
		Set<String> resourceFolders = project.getResourceFoldersList();
		
		if(resourceFolders != null)
		{
			resourceFolders = new TreeSet<>(resourceFolders);
			
			for(String rf : resourceFolders)
			{
				File file = new File(project.getBaseFolderPath(), rf);
				String id = ID_PREFIX_RESOURCE_FOLDER + rf;
				
				nodes.add(NodeInfo.newResourceFolderNode(id, file, rf));
			}
		}

		// add app config file
		File appConfigFile = new File(project.getBaseFolderPath(), project.getAppConfigFilePath());
		nodes.add(new NodeInfo(ID_PREFIX_APP_CONFIG, appConfigFile, "App Configuration"));

		// add app prop file
		File appPropFile = new File(project.getBaseFolderPath(), project.getAppPropertyFilePath());
		nodes.add(new NodeInfo(ID_PREFIX_APP_PROP, appPropFile, "App Properties"));
		
		return nodes;
	}

	@Override
	protected BaseTreeNode checkInSpecialFolders(File file)
	{
		if(file.equals(appConfigNode.getFile()))
		{
			return appConfigNode;
		}
		
		if(file.equals(appPropNode.getFile()))
		{
			return appPropNode;
		}

		for(TestSuiteFolderTreeNode tsfNode : this.testSuiteFolderNodes)
		{
			BaseTreeNode node = tsfNode.getNode(file);
			
			if(node != null)
			{
				return node;
			}
		}
		
		for(ResourceFolderTreeNode rsNode : this.resourceFolderNodes)
		{
			BaseTreeNode node = rsNode.getNode(file);
			
			if(node != null)
			{
				return node;
			}
		}

		return null;
	}
	
	public Project getProject()
	{
		return project;
	}
}