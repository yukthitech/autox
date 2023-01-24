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
package com.yukthitech.prism.help;

import javax.swing.tree.DefaultMutableTreeNode;

public class HelpTreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 1L;
	
	private HelpNodeData helpNodeData;
	
	public HelpTreeNode(HelpNodeData helpNodeData)
	{
		super(helpNodeData.getLabel());
		this.helpNodeData = helpNodeData;
		
		if(helpNodeData.getChildNodes() == null)
		{
			return;
		}
		
		for(HelpNodeData child : helpNodeData.getChildNodes())
		{
			if(!child.isFiltered())
			{
				continue;
			}
			
			super.add(new HelpTreeNode(child));
		}
	}

	public HelpNodeData getHelpNodeData()
	{
		return helpNodeData;
	}
	
	public HelpTreeNode getNode(String id)
	{
		if(helpNodeData != null && id.equals(helpNodeData.getId()))
		{
			return this;
		}
		
		int count = super.getChildCount();
		
		if(count <= 0)
		{
			return null;
		}
		
		for(int i = 0; i < count; i++)
		{
			HelpTreeNode childNode = (HelpTreeNode) super.getChildAt(i);
			HelpTreeNode selectedNode = childNode.getNode(id);
			
			if(selectedNode != null)
			{
				return selectedNode;
			}
		}
		
		return null;
	}
}
