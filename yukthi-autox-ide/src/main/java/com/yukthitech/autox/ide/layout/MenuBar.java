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
package com.yukthitech.autox.ide.layout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuBar;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Menu bar for the ide.
 * @author akiran
 */
public class MenuBar implements Validateable
{
	/**
	 * Menus of the menu bar.
	 */
	private List<Menu> menus;

	/**
	 * Gets the menus of the menu bar.
	 *
	 * @return the menus of the menu bar
	 */
	public List<Menu> getMenus()
	{
		return menus;
	}

	/**
	 * Sets the menus of the menu bar.
	 *
	 * @param menus the new menus of the menu bar
	 */
	public void setMenus(List<Menu> menus)
	{
		this.menus = menus;
	}
	
	/**
	 * Adds the menu.
	 *
	 * @param menu the menu
	 */
	public void addMenu(Menu menu)
	{
		if(this.menus == null)
		{
			this.menus = new ArrayList<>();
		}
		
		this.menus.add(menu);
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(CollectionUtils.isEmpty(menus))
		{
			throw new ValidateException("Menus can not be null.");
		}
	}
	
	public JMenuBar toJMenuBar(ActionCollection actionCollection)
	{
		JMenuBar jmBar = new JMenuBar();
		
		for(Menu menu : this.menus)
		{
			jmBar.add(menu.toJMenu(actionCollection));
		}
		
		return jmBar;
	}
}
