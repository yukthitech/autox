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
package com.yukthitech.autox.ide.views.debug;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.views.debug.StackTraceTreeModel.StackElementNode;
import com.yukthitech.autox.ide.views.debug.StackTraceTreeModel.ThreadNode;

public class StackTraceNodeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer
{
	private static final long serialVersionUID = 1L;

	private static ImageIcon THREAD_ICON = IdeUtils.loadIcon("/ui/icons/debug-thread.svg", 16);
	
	private static ImageIcon STACK_ELEM_ICON = IdeUtils.loadIcon("/ui/icons/stack-trace-element.svg", 16);
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
		if(value instanceof ThreadNode)
		{
			component.setIcon(THREAD_ICON);
		}
		else if(value instanceof StackElementNode)
		{
			component.setIcon(STACK_ELEM_ICON);
		}
		
		return component;
	}

}
