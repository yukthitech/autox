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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.utils.exceptions.InvalidStateException;

class FolderTreeNode extends BaseTreeNode
{
	private static final long serialVersionUID = 1L;
	
	protected static class NodeInfo
	{
		private String id;
		
		private File file;
		
		private boolean testSuiteFolder;
		
		private boolean resourceFolder;
		
		private String label;

		public NodeInfo(String id, File file, String label)
		{
			this.id = id;
			this.file = file;
			this.label = label;
		}

		public static NodeInfo newTestSuiteFolderNode(String id, File file, String label)
		{
			NodeInfo node = new NodeInfo(id, file, label);
			node.testSuiteFolder = true;
			
			return node;
		}

		public static NodeInfo newResourceFolderNode(String id, File file, String label)
		{
			NodeInfo node = new NodeInfo(id, file, label);
			node.resourceFolder = true;
			
			return node;
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
			
			String id = file.isDirectory() ? IProjectExplorerConstants.ID_PREFIX_FOLDER : "";
			id += file.getName();

			nodeList.add(new NodeInfo(id, file, file.getName()));
		}
		
		return nodeList;
	}

	/**
	 * This method takes care of adding new files and also removing non-existing files.
	 */
	@Override
	public synchronized void reload(boolean childReload)
	{
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
				else if(nodeInfo.resourceFolder)
				{
					folderTreeNode = new ResourceFolderTreeNode(nodeInfo.id, projectExplorer, project, nodeInfo.label, file);
				}
				else
				{
					folderTreeNode = newFolderTreeNode(nodeInfo.id, projectExplorer, project, nodeInfo.label, file);
				}
				
				treeNodes.add(folderTreeNode);
			}
			else if(file.isFile())
			{
				FileTreeNode fileNode = new FileTreeNode(nodeInfo.id, projectExplorer, project, nodeInfo.label, file);
				treeNodes.add(fileNode);
			}
		}
		
		super.setChildNodes(treeNodes);
	}
	
	@Override
	protected void reloadOnInit()
	{
		super.visitChildNodes(node -> 
		{
			node.reloadOnInit();
			return true;
		});
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
	
	public BaseTreeNode getNode(File file)
	{
		String relativePath = IdeFileUtils.getRelativePath(folder, file);
		
		if(relativePath == null || relativePath.length() == 0)
		{
			return null;
		}
		
		//search in special folders first
		BaseTreeNode res = checkInSpecialFolders(file);
		
		//if file is found in special folders return the same
		if(res != null)
		{
			return res;
		}
		
		String path[] = relativePath.split("\\" + File.separator);
		BaseTreeNode nextNode = this;
		
		for(int i = 0; i < path.length; i++)
		{
			BaseTreeNode newNode = nextNode.getChild(IProjectExplorerConstants.ID_PREFIX_FOLDER + path[i]);

			//if no folder is found with current name, check for file
			newNode = newNode == null ? nextNode.getChild(path[i]) : newNode;
			
			if(newNode == null)
			{
				return null;
			}
			
			nextNode = newNode;
		}
		
		return nextNode;
	}
	
	/**
	 * This should check for file in special folders managed by current node
	 * and return matching child node if any.
	 * 
	 * @param file
	 * @return
	 */
	protected BaseTreeNode checkInSpecialFolders(File file)
	{
		return null;
	}
	
	public void filesRemoved(List<File> files)
	{
		Set<String> nodeIds = new HashSet<>();
		
		for(File file : files)
		{
			if(file.exists())
			{
				continue;
			}
			
			//as file/folder is already deleted, there is no direct way to identify the prefix to use
			String folderLikeId = IProjectExplorerConstants.ID_PREFIX_FOLDER + file.getName();
			String fileLikeId = file.getName();
			
			if(super.getChild(folderLikeId) != null)
			{
				nodeIds.add(folderLikeId);
			}
			else
			{
				nodeIds.add(fileLikeId);
			}
		}

		if(nodeIds.size() > 0)
		{
			super.removeChildNodes(nodeIds);
		}
	}
	
	public void newFilesAdded(List<File> files)
	{
		List<BaseTreeNode> newNodes = new ArrayList<>(files.size());
		
		for(File file : files)
		{
			File parent = file.getParentFile();
			
			if(!this.folder.equals(parent))
			{
				throw new InvalidStateException("Specified file '{}' does not belong to current folder: {}", file.getPath(), this.folder.getPath());
			}
			
			if(!file.exists())
			{
				continue;
			}
			
			BaseTreeNode newNode = null;
			
			if(file.isDirectory())
			{
				newNode = newFolderTreeNode(IProjectExplorerConstants.ID_PREFIX_FOLDER + file.getName(), projectExplorer, project, file.getName(), file);
			}
			else if(file.isFile())
			{
				newNode = new FileTreeNode(file.getName(), projectExplorer, project, file.getName(), file);
			}
			
			newNodes.add(newNode);
		}

		super.addChildNodes(newNodes);
	}
	
	@Override
	public synchronized void rename(String id, String newName)
	{
		super.rename(id, newName);

		File newFolder = new File(folder.getParentFile(), newName);
		this.folder = newFolder;
		
		parentFolderRenamed(newFolder.getParentFile());
	}
	
	public void parentFolderRenamed(File newParent)
	{
		File newFolder = new File(newParent, this.folder.getName());
		this.folder = newFolder;

		super.visitChildNodes(node -> 
		{
			if(node instanceof FolderTreeNode)
			{
				((FolderTreeNode) node).parentFolderRenamed(newFolder);
			}
			else
			{
				((FileTreeNode) node).parentFolderRenamed(newFolder);
			}
			
			return true;
		});
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

