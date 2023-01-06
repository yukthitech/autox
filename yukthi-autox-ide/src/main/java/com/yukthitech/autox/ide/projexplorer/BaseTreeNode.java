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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ObjectWrapper;

/**
 * Base class for tree nodes.
 */
public class BaseTreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Id of the node.
	 */
	private String id;
	
	/**
	 * Icon for tree node.
	 */
	private Icon icon;
	
	/**
	 * Label for node.
	 */
	private String label;
	
	/**
	 * flag indicating if the current resource has errors.
	 */
	private boolean errored;
	
	/**
	 * Flag indicating if the current resource has warnings.
	 */
	private boolean warned;
	
	/**
	 * Child nodes added to this node.
	 */
	private SortedNodeList childNodes = new SortedNodeList();
	
	private ProjectExplorerModel model;
	
	public BaseTreeNode(String id, ProjectExplorerModel model)
	{
		this.id = id;
		this.model = model;
	}
	
	void setModel(ProjectExplorerModel model)
	{
		this.model = model;
	}

	public BaseTreeNode(String id, ProjectExplorerModel model, Icon icon, String label)
	{
		this(id, model);
		
		this.icon = icon;
		this.label = label;
	}
	
	/**
	 * Gets the id of the node.
	 *
	 * @return the id of the node
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Gets the icon for tree node.
	 *
	 * @return the icon for tree node
	 */
	public Icon getIcon()
	{
		return icon;
	}

	/**
	 * Sets the icon for tree node.
	 *
	 * @param icon the new icon for tree node
	 */
	public void setIcon(Icon icon)
	{
		this.icon = icon;
	}

	/**
	 * Gets the label for node.
	 *
	 * @return the label for node
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label for node.
	 *
	 * @param label the new label for node
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	/**
	 * Gets the flag indicating if the current resource has errors.
	 *
	 * @return the flag indicating if the current resource has errors
	 */
	public boolean isErrored()
	{
		return errored;
	}
	
	/**
	 * Calls the specified visitor for each of the node. The visiting will stop end or if the 
	 * visitor returns false.
	 * @param visitor
	 */
	public void visitChildNodes(Function<BaseTreeNode, Boolean> visitor)
	{
		this.childNodes.visit(visitor);
	}
	
	protected boolean checkForStatus()
	{
		AtomicBoolean isErr = new AtomicBoolean(false);
		AtomicBoolean isWarn = new AtomicBoolean(false);
		
		this.childNodes.visit(cnode -> 
		{
			if(cnode.isErrored())
			{
				isErr.set(true);
			}

			if(cnode.isWarned())
			{
				isWarn.set(true);
			}
			
			//if both flags are found to be set, then
			//  no need for further processing
			if(isErr.get() && isWarn.get())
			{
				return false;
			}
			
			return true;
		});
		
		if(this.errored == isErr.get() && this.warned == isWarn.get())
		{
			return false;
		}
		
		this.errored = isErr.get();
		this.warned = isWarn.get();
		
		if(parent != null)
		{
			((BaseTreeNode)parent).checkForStatus();
		}
		
		model.nodeChanged(this);
		return true;
	}

	/**
	 * Sets the flag indicating if the current resource has errors.
	 *
	 * @param errored the new flag indicating if the current resource has errors
	 */
	public void setErrored(boolean errored)
	{
		if(this.errored == errored)
		{
			return;
		}
		
		this.errored = errored;
		
		if(parent != null)
		{
			((BaseTreeNode)parent).checkForStatus();
		}
		
		model.nodeChanged(this);
	}

	/**
	 * Gets the flag indicating if the current resource has warnings.
	 *
	 * @return the flag indicating if the current resource has warnings
	 */
	public boolean isWarned()
	{
		return warned;
	}

	/**
	 * Sets the flag indicating if the current resource has warnings.
	 *
	 * @param warned the new flag indicating if the current resource has warnings
	 */
	public void setWarned(boolean warned)
	{
		if(this.warned == warned)
		{
			return;
		}
		
		this.warned = warned;
		
		if(parent != null)
		{
			((BaseTreeNode)parent).checkForStatus();
		}
		
		model.nodeChanged(this);
	}
	
	public void rename(String newId, String newName)
	{
		String curId = this.id;
		
		this.id = newId;
		this.label = newName;
		
		BaseTreeNode parent = (BaseTreeNode) super.parent;
		parent.removeChildNodes(CommonUtils.toSet(curId));
		parent.addChildNode(this);
	}

	public void setChildNodes(List<BaseTreeNode> newNodes)
	{
		super.removeAllChildren();
		this.childNodes.clear();
		
		newNodes.forEach(node -> childNodes.add(node));
		
		childNodes.visit(node -> 
		{
			super.add(node);
			return true;
		});

		//model.reload(this);
		if(super.getParent() != null)
		{
			model.nodeStructureChanged(this);
		}
		
		IdeUtils.execute(() -> 
		{
			newNodes.forEach(node -> node.reloadOnInit());
		}, 1);

	}
	
	public void addChildNode(BaseTreeNode node)
	{
		ObjectWrapper<Boolean> isReplaceOp = new ObjectWrapper<>(false);
		int index = childNodes.addOrReplace(node, isReplaceOp);
		
		if(isReplaceOp.getValue())
		{
			model.nodeChanged(node);
		}
		else
		{
			super.insert(node, index);
			model.nodesWereInserted(this, new int[] {index});
		}
		
		IdeUtils.execute(() -> node.reloadOnInit(), 1);
	}

	public void addChildNodes(Collection<BaseTreeNode> nodes)
	{
		//sort nodes in ascending order
		TreeMap<String, BaseTreeNode> nodeMap = new TreeMap<>();
		nodes.forEach(node -> nodeMap.put(node.id, node));
		
		int insertIndexes[] = new int[nodeMap.size()];
		int insertIdx = 0;
		
		int replacedIndexes[] = new int[nodeMap.size()];
		int replaceIdx = 0;

		ObjectWrapper<Boolean> isReplaceOp = new ObjectWrapper<>(false);
		
		for(BaseTreeNode node : nodeMap.values())
		{
			int index = childNodes.addOrReplace(node, isReplaceOp);
			
			if(isReplaceOp.getValue())
			{
				replacedIndexes[insertIdx] = index;
				replaceIdx++;
			}
			else
			{
				super.insert(node, index);
				
				insertIndexes[insertIdx] = index;
				insertIdx++;
			}
		}
		
		if(insertIdx > 0)
		{
			model.nodesWereInserted(this, Arrays.copyOf(insertIndexes, insertIdx));
		}
		
		if(replaceIdx > 0)
		{
			model.nodesChanged(this, Arrays.copyOf(replacedIndexes, replaceIdx));
		}
		
		IdeUtils.execute(() -> 
		{
			nodes.forEach(node -> node.reloadOnInit());
		}, 1);
	}

	public BaseTreeNode getChild(String id)
	{
		return childNodes.get(id);
	}
	
	public Collection<BaseTreeNode> getChildNodes()
	{
		return childNodes.getNodes();
	}
	
	public void removeChildNodes(Set<String> nodeIds)
	{
		//order the node ids in descending order
		nodeIds = new TreeSet<>(nodeIds).descendingSet();
		nodeIds.retainAll(childNodes.idSet());
		
		if(nodeIds.isEmpty())
		{
			return;
		}
		
		SortedNodeList.NodeWrapper nodeWrapper = null;
		int indexes[] = new int[nodeIds.size()];
		Object delChilds[] = new Object[nodeIds.size()];
		int arrIdx = nodeIds.size() - 1;
		
		for(String id : nodeIds)
		{
			nodeWrapper = childNodes.remove(id);
			super.remove(nodeWrapper.getNode());
			
			indexes[arrIdx] = nodeWrapper.getIndex();
			delChilds[arrIdx] = nodeWrapper.getNode();
			arrIdx--;
		}
		
		model.fireTreeNodesRemoved(this, super.getPath(), indexes, delChilds);

		//this will ensure, if error/warning childs are removed,
		//  current parent is updated approp
		IdeUtils.execute(() -> checkForStatus(), 1);
	}
	
	/**
	 * Called after node is added to the tree for first time.
	 */
	protected void reloadOnInit()
	{
	}
	
	public void reload(boolean childReload)
	{}
}
