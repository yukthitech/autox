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
package com.yukthitech.autox.ide.editor;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class GutterPopup extends JPopupMenu
{
	private static final long serialVersionUID = 1L;

	private JMenuItem breakPointToggleItem = new JMenuItem("Toggle Breakpoint");
	private JMenuItem breakPointPropItem = new JMenuItem("Breakpoint Properties");
	
	private FileEditor activeEditor;
	
	private Point mousePoint;
	
	public GutterPopup()
	{
		add(breakPointToggleItem);
		add(breakPointPropItem);
		
		breakPointToggleItem.addActionListener(this::takeDefaultAction);
	}
	
	public void setActiveEditor(FileEditor activeEditor, Point mousePoint)
	{
		this.activeEditor = activeEditor;
		this.mousePoint = mousePoint;
	}
	
	private void takeDefaultAction(ActionEvent e)
	{
		activeEditor.getIconManager().toggleBreakPoint(mousePoint);
	}
}
