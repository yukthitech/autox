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

import java.awt.event.MouseEvent;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.JPopupMenu;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.IIdeFileManager;
import com.yukthitech.autox.ide.IMaximizationListener;
import com.yukthitech.autox.ide.IdeFileManagerFactory;
import com.yukthitech.autox.ide.MaximizableTabbedPaneTab;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.model.Project;

/**
 * Tab component used by file editor.
 * @author akiran
 */
public class FileEditorTab extends MaximizableTabbedPaneTab
{
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private UiLayout uiLayout;
	
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private ActionCollection actionCollection;
	
	@Autowired
	private IdeFileManagerFactory ideFileManagerFactory;
	
	private Project project;
	
	private File file;
	
	private JPopupMenu popupMenu;
	
	private FileEditor fileEditor;
	
	private IIdeFileManager ideFileManager;
	
	public FileEditorTab(Project project, File file, FileEditor fileEditor, FileEditorTabbedPane fileTabPane, IMaximizationListener maximizationListener)
	{
		super(file.getName(), fileTabPane, fileEditor, maximizationListener, true);
		
		this.project = project;
		this.file = file;
		this.fileEditor = fileEditor;
	}
	
	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void fileChanged(File file)
			{
				fileContentChanged(file);
			}
			
			@Override
			public void fileSaved(File file)
			{
				fileContentSaved(file);
			}
		});
		
		this.ideFileManager = ideFileManagerFactory.getFileManager(project, file);
		parseFile();
	}

	@Override
	protected void closeTab()
	{
		ideContext.setActiveDetails(project, file);
		actionCollection.invokeAction("closeFile");
		
		super.checkForMaximizationStatus();
	}
	
	@Override
	protected void activateTab()
	{
		ideContext.setActiveDetails(project, file);
		actionCollection.invokeAction("openFile");
	}
	
	@Override
	protected void displayPopup(MouseEvent e)
	{
		if(popupMenu == null)
		{
			 popupMenu = uiLayout.getPopupMenu("fileTabPopup").toPopupMenu(actionCollection);
		}
		
		ideContext.setActiveDetails(project, file);
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
	
	private void fileContentChanged(File file)
	{
		if(!this.file.equals(file))
		{
			return;
		}
		
		changeLabel.setText("*");
	}
	
	private void fileContentSaved(File file)
	{
		if(!this.file.equals(file))
		{
			return;
		}
		
		changeLabel.setText("");

		parseFile();
	}
	
	private void parseFile()
	{
		FileParseCollector collector = new FileParseCollector(project, file);
		ideFileManager.parseFile(project, file, collector);
		
		super.setErrored(collector.getErrorCount() > 0);
		super.setWarned(collector.getWarningCount() > 0);
	}
	
	public boolean isFileChanged()
	{
		return changeLabel.getText().length() > 0;
	}
}
