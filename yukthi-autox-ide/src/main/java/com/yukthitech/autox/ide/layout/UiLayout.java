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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.AutoxIDE;
import com.yukthitech.ccg.xml.XMLBeanParser;

/**
 * Layout details of the ide.
 * @author akiran
 */
@Component
public class UiLayout
{
	/**
	 * Menu bar to be used.
	 */
	private MenuBar menuBar;
	
	/**
	 * Toolbar to be used.
	 */
	private ToolBar toolBar;
	
	/**
	 * Popup menu map.
	 */
	private Map<String, Menu> nameToPopup = new HashMap<>();
	
	/**
	 * Standard console line pattern expected. If not matched, should be assumed
	 * to be continuation of previous line.
	 */
	private ConsoleLinePattern standardConsoleLinePattern;
	
	/**
	 * Console line patterns for console text rendering.
	 */
	private List<ConsoleLinePattern> consoleLinePatterns = new ArrayList<>();
	
	@PostConstruct
	private void init()
	{
		XMLBeanParser.parse(AutoxIDE.class.getResourceAsStream("/ui/ui-layout.xml"), this);
	}

	/**
	 * Gets the menu bar to be used.
	 *
	 * @return the menu bar to be used
	 */
	public MenuBar getMenuBar()
	{
		return menuBar;
	}

	/**
	 * Sets the menu bar to be used.
	 *
	 * @param menuBar the new menu bar to be used
	 */
	public void setMenuBar(MenuBar menuBar)
	{
		this.menuBar = menuBar;
	}

	/**
	 * Gets the toolbar to be used.
	 *
	 * @return the toolbar to be used
	 */
	public ToolBar getToolBar()
	{
		return toolBar;
	}

	/**
	 * Sets the toolbar to be used.
	 *
	 * @param toolBar the new toolbar to be used
	 */
	public void setToolBar(ToolBar toolBar)
	{
		this.toolBar = toolBar;
	}
	
	public void addPopupMenu(String name, Menu popupMenu)
	{
		this.nameToPopup.put(name, popupMenu);
	}
	
	public Menu getPopupMenu(String name)
	{
		return this.nameToPopup.get(name);
	}
	
	public ConsoleLinePattern getStandardConsoleLinePattern()
	{
		return standardConsoleLinePattern;
	}

	public void setStandardConsoleLinePattern(ConsoleLinePattern standardConsoleLinePattern)
	{
		this.standardConsoleLinePattern = standardConsoleLinePattern;
	}

	public void addConsoleLinePattern(ConsoleLinePattern pattern)
	{
		this.consoleLinePatterns.add(pattern);
	}
	
	public List<ConsoleLinePattern> getConsoleLinePatterns()
	{
		return consoleLinePatterns;
	}
}
