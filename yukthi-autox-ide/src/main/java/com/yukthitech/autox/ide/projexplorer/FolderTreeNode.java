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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.autox.ide.ui.TestSuiteFolderTreeNode;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class FolderTreeNode extends BaseTreeNode
{
	private static final long serialVersionUID = 1L;
	
	protected static class NodeInfo
	{
		private String id;
		
		private File file;
		
		private boolean testSuiteFolder;
		
		private String label;

		public NodeInfo(String id, File file, String label, boolean testSuiteFolder)
		{
			this.id = id;
			this.file = file;
			this.label = label;
			this.testSuiteFolder = testSuiteFolder;
		}
	}

	private File folder;

	private Project project;
	
	protected ProjectExplorer projectExplorer;
	
	protected FolderTreeNode(String id, ProjectExplorer projectExplorer, Icon icon, Project project, String name, File folder)
	{
		super(id, projectExplorer.getProjectTreeModel(), icon, name);

		this.project = project;
		this.folder = folder;
		this.projectExplorer = projectExplorer;
		
		reload(false);
	}

	public FolderTreeNode(String id, ProjectExplorer projectExplorer, Project project, String name, File folder)
	{
		this(id, projectExplorer, IdeUtils.loadIcon("/ui/icons/folder.svg", 20), project, name, folder);
	}

	protected void removeNonExistingNodes()
	{
		Set<String> nodesToRemove = new HashSet<>();

		for(BaseTreeNode child : super.getChildNodes())
		{
			if(checkForRemoval(child))
			{
				nodesToRemove.add(child.getId());
			}
		}

		if(nodesToRemove != null)
		{
			super.removeChildNodes(nodesToRemove);

		}
	}
	
	protected boolean checkForRemoval(BaseTreeNode child)
	{
		File file = null;
		
		if(child instanceof FolderTreeNode)
		{
			file = ((FolderTreeNode) child).folder;
		}
		else
		{
			file = ((FileTreeNode) child).getFile();
		}

		if(!file.exists())
		{
			return true;
		}
		
		return false;
	}
	
	protected List<NodeInfo> getNodes()
	{
		File[] files = null;

		try
		{
			files = folder.getCanonicalFile().listFiles();
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching cannoical children of folder: {}", folder.getPath(), ex);
		}

		if(files == null)
		{
			return null;
		}

		List<File> list = new ArrayList<>(Arrays.asList(files));
		
		Collections.sort(list, new Comparator<File>()
		{
			@Override
			public int compare(File o1, File o2)
			{
				if(o1.isDirectory() != o2.isDirectory())
				{
					return o1.isDirectory() ? -1 : 1;
				}

				String name1 = o1.getName();
				String name2 = o2.getName();
				return name1.compareTo(name2);
			}
		});
		
		List<NodeInfo> nodeList = new ArrayList<>(list.size());
		
		for(File file : list)
		{
			if(file.getName().startsWith("."))
			{
				continue;
			}
			
			if(project.isReservedFile(file))
			{
				continue;
			}

			nodeList.add(new NodeInfo(file.getPath(), file, file.getName(), false));
		}
		
		return nodeList;
	}

	@Override
	public synchronized void reload(boolean childReload)
	{
		removeNonExistingNodes();

		BaseTreeNode existingNode = null;
		
		List<NodeInfo> nodeList = getNodes();
		
		if(nodeList == null)
		{
			nodeList = new ArrayList<>();
		}
		
		List<BaseTreeNode> treeNodes = new LinkedList<>();
		
		for(NodeInfo nodeInfo : nodeList)
		{
			File file = nodeInfo.file;
			existingNode = super.getChild(nodeInfo.id);

			if(existingNode != null)
			{
				if(childReload)
				{
					existingNode.reload(true);
				}
				
				treeNodes.add(existingNode);
				continue;
			}

			if(file.isDirectory())
			{
				FolderTreeNode folderTreeNode = null;
				
				if(nodeInfo.testSuiteFolder)
				{
					folderTreeNode = new TestSuiteFolderTreeNode(nodeInfo.id, projectExplorer, project, nodeInfo.label, file);
				}
				else
				{
					folderTreeNode = newFolderTreeNode(nodeInfo.id, projectExplorer, project, nodeInfo.label, file);
				}
				
				treeNodes.add(folderTreeNode);
			}
			else if(file.isFile())
			{
				FileTreeNode fileNode = new FileTreeNode(nodeInfo.id, projectExplorer, project, nodeInfo.label, file, null);
				treeNodes.add(fileNode);
			}
		}
		
		super.setChildNodes(treeNodes);
		
		super.checkErrorStatus();
	}
	
	protected FolderTreeNode newFolderTreeNode(String id, ProjectExplorer projectExplorer, Project project, String name, File folder)
	{
		return new FolderTreeNode(id, projectExplorer, project, folder.getName(), folder);
	}

	public File getFolder()
	{
		return folder;
	}

	public Project getProject()
	{
		return project;
	}
	
	public FileTreeNode getFileNode(File file)
	{
		String relativePath = IdeFileUtils.getRelativePath(folder, file);
		
		if(relativePath == null || relativePath.length() == 0)
		{
			return null;
		}
		
		String path[] = relativePath.split("\\" + File.separator);
		return getFileNode(file, path, 0);
	}
	
	public FileTreeNode getFileNode(File file, String path[], int index)
	{
		int count = super.getChildCount();
		
		if(count <= 0)
		{
			return null;
		}

		//from second level
		if(index > 0)
		{
			//before proceeding further, current folder matches with parent in path
			if(!folder.getName().equals(path[index - 1]))
			{
				return null;
			}
		}
		
		boolean immediateChild = ( index == (path.length - 1) );
		BaseTreeNode node = null;
		
		for(int i = 0; i < count; i++)
		{
			node = (BaseTreeNode) super.getChildAt(i);
			
			if(node instanceof FolderTreeNode)
			{
				if(immediateChild)
				{
					continue;
				}
				
				FolderTreeNode folderNode = (FolderTreeNode) node;
				FileTreeNode fileNode = folderNode.getFileNode(file, path, index + 1);
				
				if(fileNode != null)
				{
					return fileNode;
				}
			}
			
			if(!immediateChild)
			{
				continue;
			}
			
			if(node instanceof FileTreeNode)
			{
				FileTreeNode fileNode = (FileTreeNode) node;
				
				if(fileNode.getFile().getName().equals(path[index]))
				{
					return fileNode;
				}
			}
		}
		
		return null;
	}

	public void reloadFolders(Set<File> folders, List<FolderTreeNode> reloadedNodes)
	{
		FolderTreeNode nodeToReload = null;
		String path[] = null;
		String relativePath = null;
		
		for(File dir : folders)
		{
			relativePath = IdeFileUtils.getRelativePath(folder, dir);

			if(relativePath == null)
			{
				continue;
			}
			
			//if current path matches with required path
			if("".equals(relativePath))
			{
				reloadedNodes.add(this);
				this.reload(false);
				
				//as entire tree is reloaded simply return
				return;
			}
			
			path = relativePath.split("\\" + File.separator);
			nodeToReload = getFolderNode(dir, path, 0);
			
			if(nodeToReload == null)
			{
				continue;
			}
			
			nodeToReload.reload(false);
			reloadedNodes.add(nodeToReload);
		}
	}
	
	private FolderTreeNode getFolderNode(File folderToFetch, String path[], int index)
	{
		if(folderToFetch.equals(folder))
		{
			return this;
		}
		
		int count = super.getChildCount();
		
		if(count <= 0)
		{
			return null;
		}

		//from second level
		if(index > 0)
		{
			//before proceeding further, current folder matches with parent in path
			if(!folder.getName().equals(path[index - 1]))
			{
				return null;
			}
		}
		
		BaseTreeNode node = null;
		
		for(int i = 0; i < count; i++)
		{
			node = (BaseTreeNode) super.getChildAt(i);
			
			if(node instanceof FolderTreeNode)
			{
				FolderTreeNode folderNode = (FolderTreeNode) node;
				FolderTreeNode resNode = folderNode.getFolderNode(folderToFetch, path, index + 1);
				
				if(resNode != null)
				{
					return resNode;
				}
			}
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("FolderTreeNode");
		builder.append("[").append(folder.getPath());
		builder.append("]");
		return builder.toString();
	}

}

