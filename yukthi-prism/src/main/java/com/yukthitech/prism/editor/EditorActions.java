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
package com.yukthitech.prism.editor;

import java.awt.Frame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.dialog.FindAndReplaceDialog;
import com.yukthitech.prism.dialog.GotoLineDialog;
import com.yukthitech.prism.dialog.OpenResourceDialog;
import com.yukthitech.prism.layout.Action;
import com.yukthitech.prism.layout.ActionHolder;

/**
 * Actions related to editor.
 * @author akiran
 */
@ActionHolder
public class EditorActions
{
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private FindAndReplaceDialog findAndReplaceDialog;
	
	private GotoLineDialog gotoLineDialog;
	
	private OpenResourceDialog openResourceDialog;
	
	private void init()
	{
		if(gotoLineDialog != null)
		{
			return;
		}
		
		gotoLineDialog = new GotoLineDialog( (Frame) IdeUtils.getCurrentWindow() );
		IdeUtils.autowireBean(applicationContext, gotoLineDialog);
		
		IdeUtils.centerOnScreen(gotoLineDialog);
		
		openResourceDialog = new OpenResourceDialog(IdeUtils.getCurrentWindow());
		IdeUtils.autowireBean(applicationContext, openResourceDialog);
		IdeUtils.centerOnScreen(openResourceDialog);
	}

	@Action
	public void findAndReplace()
	{
		init();
		findAndReplaceDialog.display();
	}
	
	@Action
	public void gotoLine()
	{
		init();
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(editor == null)
		{
			return;
		}

		gotoLineDialog.display(editor);
	}
	
	@Action
	public void openResource()
	{
		init();
		openResourceDialog.display();
	}
	
	@Action
	public void toUpperCase()
	{
		init();
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(editor == null)
		{
			return;
		}
		
		editor.changeCase(true);
	}
	
	@Action
	public void toLowerCase()
	{
		init();
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(editor == null)
		{
			return;
		}
		
		editor.changeCase(false);
	}
}
