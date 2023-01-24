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

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents a menu.
 * @author akiran
 */
public class Menu implements Validateable
{
	/**
	 * Id for menu.
	 */
	private String id;
	
	/**
	 * Label for the menu.
	 */
	private String label;
	
	/**
	 * Mnemonic for the menu.
	 */
	private char mnemonic = 0;
	
	/**
	 * Group name of this item.
	 */
	private String group;

	/**
	 * Menu items for the label.
	 */
	private List<Object> menuItems;
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Gets the label for the menu.
	 *
	 * @return the label for the menu
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label for the menu.
	 *
	 * @param label the new label for the menu
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Gets the mnemonic for the menu.
	 *
	 * @return the mnemonic for the menu
	 */
	public char getMnemonic()
	{
		return mnemonic;
	}

	/**
	 * Sets the mnemonic for the menu.
	 *
	 * @param mnemonic the new mnemonic for the menu
	 */
	public void setMnemonic(char mnemonic)
	{
		this.mnemonic = mnemonic;
	}
	
	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group = group;
	}

	/**
	 * Adds the menu item.
	 *
	 * @param menuItem the menu item
	 */
	public void addItem(MenuItem menuItem)
	{
		if(this.menuItems == null)
		{
			this.menuItems = new ArrayList<>();
		}
		
		this.menuItems.add(menuItem); 
	}
	
	public void addMenu(Menu menu)
	{
		if(this.menuItems == null)
		{
			this.menuItems = new ArrayList<>();
		}
		
		this.menuItems.add(menu); 
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(label))
		{
			label = "";
		}
		
		if(CollectionUtils.isEmpty(menuItems))
		{
			throw new ValidateException("No menu item specified for menu.");
		}
	}
	
	public JMenu toJMenu(ActionCollection actionCollection)
	{
		JMenu menu = new JMenu(label);
		
		if(mnemonic > 0)
		{
			menu.setMnemonic(mnemonic);
		}
		
		for(Object itemObj : this.menuItems)
		{
			if(itemObj instanceof Menu)
			{
				Menu submenu = (Menu) itemObj;
				menu.add(submenu.toJMenu(actionCollection));
				continue;
			}
			
			MenuItem item = (MenuItem) itemObj;
			
			if("-".equals(item.getLabel()))
			{
				menu.addSeparator();
				continue;
			}
			
			menu.add(item.toJMenuItem(actionCollection, null));
		}
		
		if(id != null)
		{
			UiIdElementsManager.registerElement(id, group, menu);
		}

		return menu;
	}
	
	public IdePopupMenu toPopupMenu(ActionCollection actionCollection)
	{
		JPopupMenu popupMenu = new JPopupMenu();
		IdePopupMenu idePopupMenu = new IdePopupMenu(popupMenu);

		for(Object itemObj : this.menuItems)
		{
			if(itemObj instanceof Menu)
			{
				Menu submenu = (Menu) itemObj;
				popupMenu.add(submenu.toJMenu(actionCollection));
				continue;
			}
			
			MenuItem item = (MenuItem) itemObj;
			
			if("-".equals(item.getLabel()))
			{
				popupMenu.addSeparator();
				continue;
			}
			
			popupMenu.add(item.toJMenuItem(actionCollection, idePopupMenu));
		}
		
		if(id != null)
		{
			UiIdElementsManager.registerElement(id, group, popupMenu);
		}

		return idePopupMenu;
	}
	
}
