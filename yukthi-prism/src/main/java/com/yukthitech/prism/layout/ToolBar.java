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
package com.yukthitech.prism.layout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JSeparator;
import javax.swing.JToolBar;

/**
 * Toolbar to be created.
 * @author akiran
 */
public class ToolBar
{
	/**
	 * Items for the toolbar.
	 */
	private List<ToolBarIcon> items = new ArrayList<>();

	public List<ToolBarIcon> getItems()
	{
		return items;
	}

	public void setItems(List<ToolBarIcon> items)
	{
		this.items = items;
	}
	
	public void addItem(ToolBarIcon item)
	{
		this.items.add(item);
	}
	
	public void addDropDownItem(ToolBarDropDownIcon item)
	{
		this.items.add(item);
	}

	public void addToggleItem(ToolBarToggleIcon item)
	{
		this.items.add(item);
	}

	public JToolBar toJToolBar(ActionCollection actionCollection)
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBackground(null);
		toolBar.setOpaque(false);
		toolBar.setBorder(null);
		
		for(ToolBarIcon item : items)
		{
			if("-".equals(item.getIcon()))
			{
				toolBar.add(new JSeparator(JSeparator.VERTICAL));
				continue;
			}
			
			toolBar.add(item.toButton(actionCollection));
		}
		
		return toolBar;
	}
}
