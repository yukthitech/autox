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
package com.yukthitech.prism.swing;

import java.awt.Dialog.ModalityType;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JPanel;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.swing.EscapableDialog;

/**
 * Simple Panel extension which can be displayed in run-time 
 * created dialog which will be attached to main window. And also which can maintain dialog
 * state.
 * 
 * @author akranthikiran
 */
public class IdeDialogPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Main app window.
	 */
	private static Window mainAppWindow;
	
	/**
	 * Parent dialog used to display panel.
	 */
	private EscapableDialog parentDialog;
	
	/**
	 * Title for parent dialog.
	 */
	private String title;
	
	/**
	 * Parent dialog bounds.
	 */
	private Rectangle dialogBounds;
	
	private Boolean dialogResizable;
	
	private ModalityType modalityType;
	
	private boolean centerOnScreen;
	
	public static void setMainAppWindow(Window mainAppWindow)
	{
		IdeDialogPanel.mainAppWindow = mainAppWindow;
	}

	public Rectangle getDialogBounds()
	{
		if(parentDialog != null)
		{
			return parentDialog.getBounds();
		}
		
		return dialogBounds;
	}

	public void setDialogBounds(int x, int y, int w, int h)
	{
		if(dialogBounds == null)
		{
			dialogBounds = new Rectangle();
		}
		
		dialogBounds.setBounds(x, y, w, h);
	}

	public void setDialogBounds(Rectangle dialogBounds)
	{
		this.dialogBounds = dialogBounds;
		
		if(parentDialog != null)
		{
			parentDialog.setBounds(dialogBounds);
		}
	}
	
	public void setCenterOnScreen(boolean centerOnScreen)
	{
		this.centerOnScreen = centerOnScreen;
	}
	
	public Boolean getDialogResizable()
	{
		return dialogResizable;
	}

	public void setDialogResizable(Boolean dialogResizable)
	{
		this.dialogResizable = dialogResizable;

		if(parentDialog != null && dialogResizable != null)
		{
			parentDialog.setResizable(dialogResizable);
		}
	}

	public void setTitle(String title)
	{
		this.title = title;
		
		if(parentDialog != null)
		{
			parentDialog.setTitle(title);
		}
	}
	
	public void setModalityType(ModalityType modalityType)
	{
		this.modalityType = modalityType;
		
		if(parentDialog != null)
		{
			parentDialog.setModalityType(modalityType);
		}
	}
	
	public String getTitle()
	{
		return title;
	}
	
	protected void displayInDialog()
	{
		if(parentDialog == null)
		{
			parentDialog = new EscapableDialog(mainAppWindow);
			
			if(dialogBounds != null)
			{
				parentDialog.setBounds(dialogBounds);
			}
			
			if(title != null)
			{
				parentDialog.setTitle(title);
			}
			
			if(modalityType != null)
			{
				parentDialog.setModalityType(modalityType);
			}
			
			if(dialogResizable != null)
			{
				parentDialog.setResizable(dialogResizable);
			}
			
			parentDialog.setContentPane(this);
		}
		
		if(centerOnScreen)
		{
			IdeUtils.centerOnScreen(parentDialog);
		}
		
		parentDialog.setVisible(true);
	}
	
	public void closeDialog()
	{
		parentDialog.setVisible(false);
	}
}
