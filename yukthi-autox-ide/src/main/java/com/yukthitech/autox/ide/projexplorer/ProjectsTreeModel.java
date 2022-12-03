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

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.model.Project;

public class ProjectsTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ProjectsTreeModel.class);
	
	DefaultMutableTreeNode rootNode;
	
	public ProjectsTreeModel()
	{
		super(new DefaultMutableTreeNode("Root"));
		rootNode = (DefaultMutableTreeNode) super.getRoot();
	}
	
	public void addProject(ProjectTreeNode projectTreeNode)
	{
		rootNode.add(projectTreeNode);
		reload();
	}
	
	public ProjectTreeNode getProjectNode(Project project)
	{
		int count = rootNode.getChildCount();
		
		for(int i = 0; i < count; i++)
		{
			ProjectTreeNode node = (ProjectTreeNode) root.getChildAt(i);
			
			if(node.getProject() == project)
			{
				return node;
			}
		}
		
		return null;
	}
	
	public void deleteProject(Project project)
	{
		ProjectTreeNode node = getProjectNode(project);
		
		if(node == null)
		{
			logger.debug("As no node found for project '{}' ignoring project node delete request", project.getName());
			return;
		}
	
		logger.debug("Deleting project node for project '{}' from project explorer.", project.getName());
		super.removeNodeFromParent(node);
	}
	
	public List<ProjectTreeNode> getProjectNodes()
	{
		List<ProjectTreeNode> projLst = new ArrayList<>();
		int count = rootNode.getChildCount();
		
		for(int i = 0; i < count; i++)
		{
			ProjectTreeNode node = (ProjectTreeNode) root.getChildAt(i);
			projLst.add(node);
		}
		
		return projLst;
	}
}
