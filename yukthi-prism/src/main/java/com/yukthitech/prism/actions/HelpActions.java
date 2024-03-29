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
package com.yukthitech.prism.actions;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.dialog.AboutPrismDialog;
import com.yukthitech.prism.editor.FileEditor;
import com.yukthitech.prism.editor.FileEditorTabbedPane;
import com.yukthitech.prism.help.HelpPanel;
import com.yukthitech.prism.layout.Action;
import com.yukthitech.prism.layout.ActionHolder;

@ActionHolder
public class HelpActions
{
	@Autowired
	private HelpPanel helpPanel;
	
	@Autowired
	private FileEditorTabbedPane fileTabbedPane;
	
	/**
	 * Used to display about autox info.
	 */
	private AboutPrismDialog aboutAutoxDialog = new AboutPrismDialog();
	
	@Action
	public void help()
	{
		helpPanel.activatePanel(null);
	}

	@Action
	public void contextHelp()
	{
		FileEditor fileEditor = fileTabbedPane.getCurrentFileEditor();
		String currentWord = (fileEditor == null) ? null : fileEditor.getCursorWord();
		
		if(currentWord == null)
		{
			return;
		}
		
		helpPanel.activatePanel(currentWord);
	}
	
	@Action
	public void aboutAutoxIde()
	{
		IdeUtils.centerOnScreen(aboutAutoxDialog);
		aboutAutoxDialog.display();
	}
}
