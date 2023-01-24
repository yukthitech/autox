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

import java.awt.event.MouseEvent;
import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.prism.IMaximizationListener;
import com.yukthitech.prism.MaximizableTabbedPaneTab;
import com.yukthitech.prism.layout.ActionCollection;
import com.yukthitech.prism.layout.IdePopupMenu;
import com.yukthitech.prism.layout.UiIdElementsManager;
import com.yukthitech.prism.layout.UiLayout;
import com.yukthitech.prism.model.Project;

/**
 * Tab component used by file editor.
 * @author akiran
 */
public class FileEditorTab extends MaximizableTabbedPaneTab
{
	private static final long serialVersionUID = 1L;
	
	private static IdePopupMenu popupMenu;
	
	@Autowired
	private UiLayout uiLayout;
	
	@Autowired
	private ActionCollection actionCollection;
	
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	private Project project;
	
	private File file;
	
	private FileEditor fileEditor;
	
	public FileEditorTab(Project project, File file, FileEditor fileEditor, FileEditorTabbedPane fileTabPane, IMaximizationListener maximizationListener)
	{
		super(file.getName(), fileTabPane, fileEditor, maximizationListener, true);
		
		this.project = project;
		this.file = file;
		this.fileEditor = fileEditor;
		this.fileEditorTabbedPane = fileTabPane;
	}
	
	@Override
	protected void closeTab()
	{
		fileEditorTabbedPane.closeFile(this);
		
		super.checkForMaximizationStatus();
	}
	
	@Override
	protected void activateTab()
	{
		fileEditorTabbedPane.openOrActivateFile(project, file);
	}
	
	@Override
	protected void displayPopup(MouseEvent e)
	{
		if(popupMenu == null)
		{
			popupMenu = uiLayout.getPopupMenu("fileTabPopup").toPopupMenu(actionCollection);
		}

		int curIdx = fileEditorTabbedPane.getIndexOfTab(this);
		int count = fileEditorTabbedPane.getTabCount();
		
		UiIdElementsManager.getElement("ftCloseAllButThis").setEnabled(count > 1);
		UiIdElementsManager.getElement("ftCloseAll").setEnabled(count > 1);
		UiIdElementsManager.getElement("ftCloseToLeft").setEnabled(curIdx > 0);
		UiIdElementsManager.getElement("ftCloseToRight").setEnabled(curIdx < (count - 1));
		
		popupMenu.show(this, e.getX(), e.getY());
	}

	public File getFile()
	{
		return file;
	}
	
	public void setFile(File file)
	{
		label.setText(file.getName());
		this.file = file;
	}
	
	public FileEditor getFileEditor()
	{
		return fileEditor;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	void fileContentChanged()
	{
		changeLabel.setText("*");
	}
	
	void fileContentSaved()
	{
		changeLabel.setText("");
	}
	
	void setParseResults(boolean errored, boolean warning)
	{
		super.setErrored(errored);
		super.setWarned(warning);
	}
	
	public boolean isFileChanged()
	{
		return changeLabel.getText().length() > 0;
	}
}
