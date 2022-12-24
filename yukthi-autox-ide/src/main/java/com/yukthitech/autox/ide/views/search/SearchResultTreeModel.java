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
package com.yukthitech.autox.ide.views.search;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.proj.ProjectManager;
import com.yukthitech.autox.ide.search.SearchResult;

public class SearchResultTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	public static class ProjectNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = 1L;
		
		private String name;

		public ProjectNode(String name)
		{
			super(name);
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
	}
	
	public static class FolderNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = 1L;
		
		private String relativePath;
		
		public FolderNode(String relativePath)
		{
			this.relativePath = relativePath;
		}
		
		@Override
		public String toString()
		{
			return relativePath;
		}
	}

	public static class SearchResultNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = 1L;
		
		private String projectName;
		private SearchResult result;
		
		public SearchResultNode(String projectName, SearchResult result)
		{
			this.projectName = projectName;
			this.result = result;
		}
		
		public String getProjectName()
		{
			return projectName;
		}

		public SearchResult getResult()
		{
			return result;
		}
		
		@Override
		public String toString()
		{
			return result.getLineHtml();
		}
	}
	
	private DefaultMutableTreeNode root;
	
	public SearchResultTreeModel()
	{
		super(new DefaultMutableTreeNode("root"));
		
		this.root = (DefaultMutableTreeNode) super.getRoot();
	}
	
	private ProjectNode getProjectNode(Map<String, ProjectNode> projectNodes, String projName)
	{
		ProjectNode node = projectNodes.get(projName);
		
		if(node != null)
		{
			return node;
		}
		
		node = new ProjectNode(projName);
		projectNodes.put(projName, node);
		
		root.add(node);
		return node;
	}
	
	private FolderNode getFolderNode(Map<String, FolderNode> folderNodes, ProjectNode projNode, String folderPath)
	{
		FolderNode node = folderNodes.get(folderPath);
		
		if(node != null)
		{
			return node;
		}
		
		node = new FolderNode(folderPath);
		folderNodes.put(folderPath, node);
		
		projNode.add(node);
		return node;
	}

	public void setResults(List<SearchResult> results, ProjectManager projectManager)
	{
		root.removeAllChildren();
		
		if(CollectionUtils.isEmpty(results))
		{
			super.nodeStructureChanged(root);
			return;
		}

		Map<String, ProjectNode> projectNodes = new HashMap<>();
		Map<String, FolderNode> folderNodes = new HashMap<>();
		
		Map<String, File> baseFolders = projectManager.getAllProjectFolders();
		String relPath = null, projName = null;
		
		for(SearchResult res : results)
		{
			File parentFolder = res.getFile().getParentFile();
			relPath = null;
			
			for(Map.Entry<String, File> entry : baseFolders.entrySet())
			{
				relPath = IdeFileUtils.getRelativePath(entry.getValue(), parentFolder);
				
				if(relPath != null)
				{
					projName = entry.getKey();
					break;
				}
			}
			
			if(relPath == null)
			{
				continue;
			}
			
			ProjectNode projNode = getProjectNode(projectNodes, projName);
			FolderNode folderNode = getFolderNode(folderNodes, projNode, relPath);
			
			SearchResultNode resNode = new SearchResultNode(projName, res);
			folderNode.add(resNode);
		}
		
		super.nodeStructureChanged(root);
	}
	
	/**
	 * Removes duplicates especially in terms of parent-child relation. if parent is selected,
	 * child will be removed.
	 * 
	 * @param treePaths
	 * @return
	 */
	private Map<TreePath, TreeSet<Integer>> filter(TreePath treePaths[])
	{
		//collect the root elements on map
		//Note: Assumption is duplicate paths will not come
		Map<Object, TreePath> pathMap = new HashMap<>();
		
		for(TreePath path : treePaths)
		{
			pathMap.put(path.getLastPathComponent(), path);
		}
		
		Map<TreePath, TreeSet<Integer>> res = new LinkedHashMap<>();
		
		NEXT_PATH: for(TreePath actPath : treePaths)
		{
			TreePath path = actPath.getParentPath();
			
			//check if any parent in hierarchy is already present, if so, ignore it
			while(path != null)
			{
				if(pathMap.containsKey(path.getLastPathComponent()))
				{
					continue NEXT_PATH;
				}
				
				path = path.getParentPath();
			}
			
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) actPath.getParentPath().getLastPathComponent();
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) actPath.getLastPathComponent();
			int idx = parent.getIndex(child);
			
			if(idx < 0)
			{
				continue;
			}

			TreeSet<Integer> childElemIndexes = res.get(actPath.getParentPath());
			
			if(childElemIndexes == null)
			{
				childElemIndexes = new TreeSet<Integer>();
				res.put(actPath.getParentPath(), childElemIndexes);
			}
			
			childElemIndexes.add(idx);
		}
		
		return res;
	}
	
	public void removePaths(TreePath treePaths[])
	{
		Map<TreePath, TreeSet<Integer>> filteredPaths = filter(treePaths);
		
		for(Map.Entry<TreePath, TreeSet<Integer>> entry : filteredPaths.entrySet())
		{
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) entry.getKey().getLastPathComponent();
			Set<Integer> childIndexes = entry.getValue().descendingSet();
			
			int indexes[] = new int[childIndexes.size()];
			Object delChilds[] = new Object[childIndexes.size()];
			int arrIdx = childIndexes.size() - 1;
			
			for(Integer idx : childIndexes)
			{
				indexes[arrIdx] = idx;
				delChilds[arrIdx] = parent.getChildAt(idx);

				parent.remove(idx);
				arrIdx--;
			}
			
			super.fireTreeNodesRemoved(this, entry.getKey().getPath(), indexes, delChilds);
		}
	}
}
