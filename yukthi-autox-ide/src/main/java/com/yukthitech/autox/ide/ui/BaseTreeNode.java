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
package com.yukthitech.autox.ide.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
	 * Flag indicating if the curent resource has warnings.
	 */
	private boolean warned;
	
	/**
	 * Child nodes added to this node.
	 */
	private Map<String, BaseTreeNode> childNodes = new LinkedHashMap<>();
	
	private DefaultTreeModel model;
	
	public BaseTreeNode(String id, DefaultTreeModel model)
	{
		this.id = id;
		this.model = model;
	}

	public BaseTreeNode(String id, DefaultTreeModel model, Icon icon, String label)
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
	
	protected boolean checkErrorStatus()
	{
		boolean isErr = false;
		
		for(BaseTreeNode cnode : this.childNodes.values())
		{
			if(cnode.isErrored())
			{
				isErr = true;
			}
		}
		
		if(this.errored == isErr)
		{
			return false;
		}
		
		this.errored = isErr;
		
		if(parent != null && (parent instanceof BaseTreeNode))
		{
			boolean parentLoaded = ((BaseTreeNode)parent).checkErrorStatus();
			
			if(parentLoaded)
			{
				return true;
			}
		}
		
		model.reload(this);
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
		
		if(parent != null && (parent instanceof BaseTreeNode))
		{
			boolean parentLoaded = ((BaseTreeNode)parent).checkErrorStatus();
			
			if(parentLoaded)
			{
				return;
			}
		}
		
		model.reload(this);
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
		
		if(parent != null && (parent instanceof BaseTreeNode))
		{
			((BaseTreeNode)parent).setWarned(warned);
		}
	}

	/*
	public void addChild(String id, BaseTreeNode node)
	{
		if(childNodes.containsKey(id))
		{
			throw new InvalidStateException("A node with specified id already exist: {}", id);
		}
		
		node.id = id;
		childNodes.put(id, node);
		super.add(node);
	}
	*/
	
	/*
	public void insert(String id, BaseTreeNode node, int index)
	{
		if(childNodes.containsKey(id))
		{
			throw new InvalidStateException("A node with specified id already exist: {}", id);
		}
		
		node.id = id;
		childNodes.put(id, node);
		super.insert(node, index);
	}
	*/
	
	public void setChildNodes(List<BaseTreeNode> childNodes)
	{
		super.removeAllChildren();
		
		for(BaseTreeNode node : childNodes)
		{
			super.add(node);
			this.childNodes.put(node.getId(), node);
		}
	}

	public BaseTreeNode getChild(String id)
	{
		return childNodes.get(id);
	}
	
	public Collection<BaseTreeNode> getChildNodes()
	{
		return childNodes.values();
	}
	
	public void removeChildNodes(Set<String> nodeIds)
	{
		BaseTreeNode node = null;
		
		for(String id : nodeIds)
		{
			node = childNodes.remove(id);
			
			if(node == null)
			{
				continue;
			}
			
			((BaseTreeNode) node.getParent()).remove(node);
		}
	}
	
	public void reload(boolean childReload)
	{}
}
