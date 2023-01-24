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

import java.awt.Component;

import javax.swing.JPopupMenu;

/**
 * Wrapper over popup and help in capturing the source of popup menu.
 * Note: same popup menu can be triggered from multiple places, for further processing this popup source is 
 * important.
 * 
 * @author akranthikiran
 */
public class IdePopupMenu
{
	private JPopupMenu popupMenu;
	
	private Component lastSource;
	
	public IdePopupMenu(JPopupMenu popupMenu)
	{
		this.popupMenu = popupMenu;
	}

	public void show(Component c, int x, int y)
	{
		this.lastSource = c;
		popupMenu.show(c, x, y);
	}
	
	public Component getLastSource()
	{
		return lastSource;
	}
}
