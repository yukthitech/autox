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

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.autox.ide.ui.TestSuiteFolderTreeNode;

public class ProjectTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	private Project project;
	
	public ProjectTreeNode(String id, ProjectExplorer projectExplorer, Project project)
	{
		super(id, projectExplorer, IdeUtils.loadIcon("/ui/icons/project.svg", 20), project, project.getName(), new File(project.getBaseFolderPath()));

		this.project = project;
		
		//reload once project is set
		reload(false);
	}
	
	@Override
	public void reload(boolean childReload)
	{
		//project will be during init, and when this method is called by super class <init>
		if(project == null)
		{
			return;
		}
		
		if(!super.getLabel().equals(project.getName()))
		{
			super.setLabel(project.getName());
		}
		
		super.reload(childReload);
	}
	
	@Override
	protected boolean checkForRemoval(BaseTreeNode child)
	{
		if( super.checkForRemoval(child) )
		{
			return true;
		}
		
		if(child instanceof TestSuiteFolderTreeNode)
		{
			TestSuiteFolderTreeNode tsNode = (TestSuiteFolderTreeNode) child;
			
			if(project.getTestSuitesFoldersList() == null || !project.getTestSuitesFoldersList().contains(tsNode.getLabel()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	protected List<NodeInfo> getNodes()
	{
		List<NodeInfo> nodes = super.getNodes();
		
		int index = 0;
		Set<String> testSuitesFolders = project.getTestSuitesFoldersList();
		
		if(testSuitesFolders != null)
		{
			testSuitesFolders = new TreeSet<>( project.getTestSuitesFoldersList() );
			
			for(String tsf : testSuitesFolders)
			{
				File file = new File(project.getBaseFolderPath(), tsf);
				String id = ":testSuite:" + tsf;
				
				nodes.add(index, new NodeInfo(id, file, tsf, true));
				index++;
			}
		}
		
		// add app config file
		if(super.getChild(":appConfig") == null)
		{
			File appConfigFile = new File(project.getBaseFolderPath(), project.getAppConfigFilePath());
			nodes.add(index, new NodeInfo(":appConfig", appConfigFile, "App Configuration", false));
			index++;
		}

		// add app prop file
		if(super.getChild(":appProp") == null)
		{
			File appPropFile = new File(project.getBaseFolderPath(), project.getAppPropertyFilePath());
			nodes.add(index, new NodeInfo(":appProp", appPropFile, "App Properties", false));
		}
		
		return nodes;
	}

	/*
	@Override
	public void reload(boolean childReload)
	{
		//project will be null, when reload is called by folder-tree-node constructor
		if(project == null)
		{
			return;
		}
		
		super.reload(childReload);
		
		/*
		for(BaseTreeNode child : super.getChildNodes())
		{
			if(childReload)
			{
				child.reload(true);
			}
		}
		* /

		int index = 0;
		Set<String> testSuitesFolders = project.getTestSuitesFoldersList();
		
		if(testSuitesFolders != null)
		{
			testSuitesFolders = new TreeSet<>( project.getTestSuitesFoldersList() );
			
			BaseTreeNode existingNode = null;
			
			for(String tsf : testSuitesFolders)
			{
				File file = new File(project.getBaseFolderPath(), tsf);
				String id = ":testSuite:" + tsf;
				existingNode = super.getChild(id);
				
				if(existingNode != null)
				{
					existingNode.reload(childReload);
				}
				else
				{
					super.insert(id, new TestSuiteFolderTreeNode(projectExplorer, project, tsf, file), index);
				}
				
				index++;
			}
		}
		
		// add app config file
		if(super.getChild(":appConfig") == null)
		{
			File appConfigFile = new File(project.getBaseFolderPath(), project.getAppConfigFilePath());
			super.insert(":appConfig", new FileTreeNode(projectExplorer, project, "App Configuration", appConfigFile, null), index);
		}

		index++;

		// add app prop file
		if(super.getChild(":appProp") == null)
		{
			File appPropFile = new File(project.getBaseFolderPath(), project.getAppPropertyFilePath());
			super.insert(":appProp", new FileTreeNode(projectExplorer, project, "App Properties", appPropFile, null), index);
		}
	}
	*/

	public Project getProject()
	{
		return project;
	}
}