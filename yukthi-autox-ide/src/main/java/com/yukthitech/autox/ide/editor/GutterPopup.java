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

import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.exeenv.debug.DebugPointManager;
import com.yukthitech.autox.ide.exeenv.debug.IdeDebugPoint;
import com.yukthitech.autox.ide.services.SpringServiceProvider;

@Component
public class GutterPopup extends JPopupMenu
{
	private static final long serialVersionUID = 1L;

	private JMenuItem breakPointToggleItem = new JMenuItem("Toggle Breakpoint");
	private JMenuItem breakPointPropItem = new JMenuItem("Breakpoint Properties");
	
	private FileEditor activeEditor;
	
	private Point mousePoint;
	
	private int lineNumber;
	
	public GutterPopup()
	{
		add(breakPointToggleItem);
		add(breakPointPropItem);
		
		breakPointToggleItem.addActionListener(this::toggleBreakPoint);
		breakPointPropItem.addActionListener(this::openDebugPointProp);
	}
	
	public void setActiveEditor(FileEditor activeEditor, Point mousePoint, int lineNumber, boolean hasDebugPoint)
	{
		this.activeEditor = activeEditor;
		this.mousePoint = mousePoint;
		this.lineNumber = lineNumber;
		
		if(hasDebugPoint)
		{
			breakPointToggleItem.setText("Remove Breakpoint");
			breakPointPropItem.setEnabled(true);
		}
		else
		{
			breakPointToggleItem.setText("Add Breakpoint");
			breakPointPropItem.setEnabled(false);
		}
	}
	
	private void toggleBreakPoint(ActionEvent e)
	{
		activeEditor.getIconManager().toggleBreakPoint(mousePoint);
	}
	
	private void openDebugPointProp(ActionEvent e)
	{
		DebugPointPropDialog dlg = SpringServiceProvider.getService(DebugPointPropDialog.class);
		DebugPointManager debugPointManager = SpringServiceProvider.getService(DebugPointManager.class);
		
		IdeDebugPoint degugPoint = debugPointManager.getDebugPoint(activeEditor.getFile(), lineNumber);
		
		if(dlg.display(degugPoint))
		{
			activeEditor.getIconManager().reloadDebugPoints();
		}
	}
}
