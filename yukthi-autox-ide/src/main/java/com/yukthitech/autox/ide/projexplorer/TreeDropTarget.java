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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;

@Component
public class TreeDropTarget implements DropTargetListener
{
	private static Logger logger = LogManager.getLogger(TreeDropTarget.class);

	private JTree targetTree;

	@Autowired
	private ProjectExplorer projectExplorer;
	
	public TreeDropTarget()
	{
	}

	public void setTargetTree(JTree targetTree)
	{
		this.targetTree = targetTree;
		new DropTarget(targetTree, this);
	}

	private TreeNode getNodeForEvent(Point p)
	{
		TreePath path = targetTree.getPathForLocation(p.x, p.y); 
		
		if(path == null)
		{
			return null;
		}
		
		return (TreeNode) path.getLastPathComponent();
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde)
	{
		TreeNode node = getNodeForEvent(dtde.getLocation());
		
		// if(node.isLeaf())
		if(node instanceof FileTreeNode)
		{
			dtde.rejectDrag();
		}
		else if(node instanceof FolderTreeNode)
		{
			TreePath path = targetTree.getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);

			targetTree.expandPath(path);
			dtde.acceptDrag(dtde.getDropAction());
		}
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde)
	{
		TreeNode node = getNodeForEvent(dtde.getLocation());

		if(node instanceof FileTreeNode)
		{
			dtde.rejectDrag();
		}
		else if(node instanceof FolderTreeNode)
		{
			TreePath path = targetTree.getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);

			targetTree.expandPath(path);
			dtde.acceptDrag(dtde.getDropAction());
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde)
	{
	}

	@Override
	public void dragExit(DropTargetEvent dte)
	{
	}

	@Override
	public void drop(DropTargetDropEvent dtde)
	{
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getNodeForEvent(dtde.getLocation());
		File activeFolder = null;
		Project project = null;

		if(parent instanceof FolderTreeNode)
		{
			activeFolder = ((FolderTreeNode) parent).getFolder();
			project = ((FolderTreeNode) parent).getProject();
		}
		else if(parent instanceof ProjectTreeNode)
		{
			project = ((ProjectTreeNode) parent).getProject();
			
			String baseFolder = project.getBaseFolderPath();
			activeFolder = new File(baseFolder);
		}
		else
		{
			dtde.rejectDrop();
			return;
		}
		
		try
		{
			Transferable tr = dtde.getTransferable();

			if(!tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.rejectDrop();
				return;
			}

			@SuppressWarnings("unchecked")
			List<File> list = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);
			
			if(list == null || list.isEmpty())
			{
				dtde.rejectDrop();
				return;
			}
			
			activeFolder = activeFolder.getCanonicalFile();
			
			logger.debug("Dnd operation, dragging files {} and dropping to {}", list, activeFolder.getPath());
			
			//ensure file/folder is not replacing itself
			for(File file : list)
			{
				if(file.getParentFile().equals(activeFolder))
				{
					logger.info("File {} is trying to replace itself, which is not allowed in DND. Hence cancelling the DnD", file.getPath());
					dtde.rejectDrop();
					return;
				}
			}
			
			int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(),
						String.format("Are you sure you want to move below files/folders \n   %s \nto folder: %s", list, activeFolder),
						"Move Files",
						JOptionPane.YES_NO_OPTION
					);
			
			if(res == JOptionPane.NO_OPTION)
			{
				return;
			}

			Set<File> foldersToRefresh = new HashSet<>(); 
			foldersToRefresh.add(activeFolder);
			
			for(File file : list)
			{
				FileUtils.moveToDirectory(file, activeFolder, false);
				foldersToRefresh.add(file.getParentFile());
			}
			
			dtde.dropComplete(true);
			
			projectExplorer.reloadFolders(foldersToRefresh);
		} catch(Exception ex)
		{
			logger.error("An error occoure during running of drop method ",ex);
			dtde.rejectDrop();
			return;
		}
	}

}