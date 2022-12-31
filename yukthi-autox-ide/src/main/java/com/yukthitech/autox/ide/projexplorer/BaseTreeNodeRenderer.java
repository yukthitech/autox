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

import java.awt.Component;
import java.awt.Image;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.yukthitech.autox.ide.IdeUtils;

class BaseTreeNodeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	private static final int ICON_HEIGHT = 12;
	
	private static final int ICON_BORDER = 5;
	
	private static Image ERROR_ICON = IdeUtils.loadIcon("/ui/icons/bookmark-error.svg", ICON_HEIGHT).getImage();
	
	private static Image WARN_ICON = IdeUtils.loadIcon("/ui/icons/bookmark-warn.svg", ICON_HEIGHT).getImage();
	
	private boolean hasErrors = false;
	
	private boolean hasWarnings = false;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		DefaultTreeCellRenderer component = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
		if(!(value instanceof BaseTreeNode))
		{
			return component;
		}
		
		BaseTreeNode node = (BaseTreeNode) value;
		component.setIcon(node.getIcon());
		component.setText(node.getLabel());
		
		this.hasErrors = node.isErrored();
		this.hasWarnings = node.isWarned();
		
		return component;
	}
	
	protected void paintComponent(java.awt.Graphics g) 
	{
		super.paintComponent(g);
		
		int height = super.getSize().height;
		
		if(hasErrors)
		{
			g.drawImage(ERROR_ICON, -ICON_BORDER, height - ICON_HEIGHT - 3, this);
		}
		else if(hasWarnings)
		{
			g.drawImage(WARN_ICON, -ICON_BORDER, height - ICON_HEIGHT - 3, this);
		}
	}
}
