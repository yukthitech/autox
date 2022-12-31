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

import static com.yukthitech.autox.ide.projexplorer.IProjectExplorerConstants.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;

public class ProjectTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	private Project project;
	
	private List<TestSuiteFolderTreeNode> testSuiteFolderNodes = new ArrayList<>();
	
	public ProjectTreeNode(String id, ProjectExplorer projectExplorer, Project project)
	{
		super(IProjectExplorerConstants.ID_PREFIX_PROJECT + id, 
				projectExplorer, IdeUtils.loadIcon("/ui/icons/project.svg", 20), project, project.getName(), new File(project.getBaseFolderPath()));

		this.project = project;
		
		//reload once project is set
		reload(false);
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
			
			super.visitChildNodes(node -> 
			{
				if(node instanceof TestSuiteFolderTreeNode)
				{
					testSuiteFolderNodes.add((TestSuiteFolderTreeNode) node);
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
		
		int index = 0;
		Set<String> testSuitesFolders = project.getTestSuitesFoldersList();
		
		if(testSuitesFolders != null)
		{
			testSuitesFolders = new TreeSet<>( project.getTestSuitesFoldersList() );
			
			for(String tsf : testSuitesFolders)
			{
				File file = new File(project.getBaseFolderPath(), tsf);
				String id = ID_PREFIX_TEST_SUITE_FOLDER + tsf;
				
				nodes.add(index, new NodeInfo(id, file, tsf, true));
				index++;
			}
		}
		
		// add app config file
		File appConfigFile = new File(project.getBaseFolderPath(), project.getAppConfigFilePath());
		nodes.add(index, new NodeInfo(ID_PREFIX_APP_CONFIG, appConfigFile, "App Configuration", false));
		index++;

		// add app prop file
		File appPropFile = new File(project.getBaseFolderPath(), project.getAppPropertyFilePath());
		nodes.add(index, new NodeInfo(ID_PREFIX_APP_PROP, appPropFile, "App Properties", false));
		
		return nodes;
	}

	@Override
	protected BaseTreeNode checkInSpecialFolders(File file)
	{
		for(TestSuiteFolderTreeNode tsfNode : this.testSuiteFolderNodes)
		{
			BaseTreeNode node = tsfNode.getNode(file);
			
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