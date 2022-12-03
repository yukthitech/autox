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

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.MaximizableTabbedPane;
import com.yukthitech.autox.ide.actions.ProjectActions;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.FileState;
import com.yukthitech.autox.ide.model.IdeSettings;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.model.ProjectState;
import com.yukthitech.autox.ide.services.IdeEventHandler;
import com.yukthitech.autox.ide.services.IdeSettingChangedEvent;
import com.yukthitech.autox.ide.services.IdeStartedEvent;
import com.yukthitech.utils.exceptions.InvalidStateException;

@ActionHolder
@Component
public class FileEditorTabbedPane extends MaximizableTabbedPane
{
	private static final String ACTIVE_FILE = "fileEditor$activeFile";

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(FileEditorTabbedPane.class);
	
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ProjectActions projectActions;
	
	private Map<String, FileEditor> pathToEditor = new HashMap<>();
	
	private int currentTabIndex = -1;
	
	private Font editorFont = null;
	
	private boolean enableTextWrapping = false;
	
	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void saveState(IdeState state)
			{
				try
				{
					saveFilesState(state);
				}catch(IOException ex)
				{
					throw new InvalidStateException("An error occurred while saving file states", ex);
				}
			}
			
			@Override
			public void loadState(IdeState state)
			{
				//opening files before window is displayed, so that slow processing is completed before hand
				openFilesFromState(state);
				
				changeEditorSettings(state.getIdeSettings());
			}
			
			@Override
			public void activeFileChanged(File file, Object source)
			{
				if(source == FileEditorTabbedPane.this)
				{
					return;
				}
				
				selectProjectFile(ideContext.getActiveProject(), file);
			}
		});
		
		super.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				int newIdx = FileEditorTabbedPane.this.getSelectedIndex();
				
				if(newIdx < 0 || currentTabIndex == newIdx)
				{
					return;
				}
				
				currentTabIndex = newIdx;

				FileEditor editor = (FileEditor) FileEditorTabbedPane.this.getComponentAt(newIdx);
				editor.getTextArea().requestFocus();
			}
		});
	}
	
	private void openFilesFromState(IdeState state)
	{
		Map<String, FileEditor> editors = new HashMap<String, FileEditor>();
		
		for(ProjectState projState : state.getOpenProjects())
		{
			Project proj = projectActions.openExistingProject(projState.getPath());
			
			if(proj == null)
			{
				continue;
			}
			
			for(FileState fileState : projState.getOpenFiles())
			{
				FileEditor fileEditor = openProjectFile(proj, new File(fileState.getPath()) );
				
				if(fileEditor == null)
				{
					continue;
				}
				
				fileEditor.setCaretPosition(fileState.getCursorPositon());

				editors.put(fileState.getPath(), fileEditor);
			}
		}
		
		String activePath = (String) state.getAttribute(ACTIVE_FILE);
		
		if(activePath != null)
		{
			FileEditor editor = editors.get(activePath);
			
			if(editor != null)
			{
				super.setSelectedComponent(editor);
			}
		}
	}
	
	@IdeEventHandler
	public void onIdeSettingsChanged(IdeSettingChangedEvent event)
	{
		changeEditorSettings(event.getIdeSettings());
	}
	
	@IdeEventHandler
	public void onStartup(IdeStartedEvent event)
	{
		List<FileEditorTab> tabs = getAllTabs();
		
		if(CollectionUtils.isNotEmpty(tabs))
		{
			tabs.forEach(tab -> 
			{
				FileEditor editor = tab.getFileEditor();
				editor.setCaretPosition(editor.getCaretPosition());
			});
		}
		
		FileEditor fileEditor = getCurrentFileEditor();
		
		if(fileEditor != null)
		{
			fileEditor.getTextArea().requestFocus();
		}
	}
	
	private void changeEditorSettings(IdeSettings ideSettings)
	{
		boolean changesPresent = false;
		Font font = ideSettings.getEditorFont();
		
		if(font != null && font != this.editorFont)
		{
			changesPresent = true;
			this.editorFont = font;
		}
		else
		{
			font = null;
		}
		
		Boolean wrap = ideSettings.isEnableTextWrapping();
		
		if(wrap != this.enableTextWrapping)
		{
			this.enableTextWrapping = wrap;
			changesPresent = true;
		}
		else
		{
			wrap = null;
		}
		
		if(!changesPresent)
		{
			return;
		}
		
		for(FileEditor editor : pathToEditor.values())
		{
			if(font != null)
			{
				editor.setEditorFont(editorFont);
				editor.setEnableTextWrapping(enableTextWrapping);
			}
			
			if(wrap != null)
			{
				editor.setEnableTextWrapping(wrap);
			}
		}
	}
	
	private void saveFilesState(IdeState state) throws IOException
	{
		int tabCount = super.getTabCount();
		
		for(int i = 0; i < tabCount; i++)
		{
			FileEditorTab tab = (FileEditorTab) super.getTabComponentAt(i);
			FileEditor editor = (FileEditor) super.getComponentAt(i);
			ProjectState projectState = state.addOpenProject(tab.getProject());
			
			projectState.addOpenFile(new FileState(tab.getFile().getCanonicalPath(), editor.getCaretPosition()));
		}
		
		FileEditor activeTab = getCurrentFileEditor();
		
		if(activeTab != null)
		{
			state.setAtribute(ACTIVE_FILE, activeTab.getFile().getCanonicalPath());
		}
	}
	
	public FileEditor openProjectFile(Project project, File file)
	{
		if(!file.exists())
		{
			logger.warn("Tried to open non-existing project file '{}' under project '{}'. Ignoring open request.", 
					file.getPath(), project.getName());
			return null;
		}
		
		String canonicalPath = null;
		
		try
		{
			canonicalPath = file.getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while fetching cannonical path of file: {}", file.getPath(), ex);
		}
		
		String projectPath = project.getBaseFolderPath();
		
		if(!canonicalPath.startsWith(projectPath))
		{
			logger.warn("Tried to open project file '{}' under project '{}'. Ignoring open request specified file as it is not part of project base folder: {}", 
					canonicalPath, project.getName(), projectPath);
			return null;
		}

		FileEditor fileEditor = pathToEditor.get(canonicalPath);
		
		if(fileEditor != null)
		{
			logger.trace("Selecting existing tab for file: {}", canonicalPath);
			super.setSelectedComponent(fileEditor);
			return fileEditor;
		}
		
		logger.trace("Opening new tab for file: {}", canonicalPath);
		
		int nextTabIndex = super.getTabCount();
		
		fileEditor = new FileEditor(project, file);
		IdeUtils.autowireBean(applicationContext, fileEditor);
		
		if(editorFont != null)
		{
			fileEditor.setEditorFont(editorFont);
		}
		
		fileEditor.setEnableTextWrapping(enableTextWrapping);
		
		FileEditorTab fileEditorTab = new FileEditorTab(project, file, fileEditor, this, maximizationListener);
		IdeUtils.autowireBean(applicationContext, fileEditorTab);
		
		addTab(file.getName(), null, fileEditor);
		super.setTabComponentAt(nextTabIndex, fileEditorTab);
		
		pathToEditor.put(canonicalPath, fileEditor);
		
		super.setSelectedComponent(fileEditor);
		return fileEditor;
	}
	
	private void selectProjectFile(Project project, File file)
	{
		if(!file.exists())
		{
			logger.warn("Tried to open non-existing project file '{}' under project '{}'. Ignoring open request.", 
					file.getPath(), project.getName());
			return;
		}
		
		String canonicalPath = null;
		
		try
		{
			canonicalPath = file.getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while fetching cannonical path of file: {}", file.getPath(), ex);
		}
		
		String projectPath = project.getBaseFolderPath();
		
		if(!canonicalPath.startsWith(projectPath))
		{
			logger.warn("Tried to open project file '{}' under project '{}'. Ignoring open request specified file as it is not part of project base folder: {}", 
					canonicalPath, project.getName(), projectPath);
			return;
		}

		FileEditor fileEditor = pathToEditor.get(canonicalPath);
		
		if(fileEditor != null)
		{
			//logger.debug("Selecting existing tab for file: {}", canonicalPath);
			super.setSelectedComponent(fileEditor);
		}
	}

	public FileEditor getCurrentFileEditor()
	{
		return (FileEditor) super.getSelectedComponent();
	}
	
	@Action
	public void openFile() throws IOException
	{
		Project activeProject = ideContext.getActiveProject();
		File activeFile = ideContext.getActiveFile();
		
		if(activeProject == null || activeFile == null || !activeFile.isFile()) 
		{
			logger.debug("As no active file on context, ignoring request for new tab.");
			return;
		}
		
		openProjectFile(activeProject, activeFile);
	}
	
	@Action
	public void saveFile()
	{
		FileEditor currentEditor = (FileEditor) super.getSelectedComponent();
		
		if(currentEditor == null)
		{
			return;
		}
		
		currentEditor.saveFile();
	}
	
	/**
	 * Fetches the tab  index where this file is open currently.
	 * @param file
	 * @return
	 */
	private int getIndexWithFile(File file)
	{
		int tabCount = super.getTabCount();
		
		for(int i = 0; i < tabCount; i++)
		{
			FileEditorTab tab = (FileEditorTab) super.getTabComponentAt(i);
			
			if(tab.getFile().equals(file))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Checks for unsaved changes in specified file tab. If no changes are present
	 * or if changes are saved, this method will return true.
	 * @param index
	 * @return
	 */
	private boolean checkForChanges(int index)
	{
		FileEditorTab tab = (FileEditorTab) super.getTabComponentAt(index);
		
		//if file is not changed simply return true
		if(!tab.isFileChanged())
		{
			return true;
		}
		
		String mssg = String.format("Changes to file '%s' is not saved yet."
					+ "\nChoose 'Yes' if changes should be saved before closing"
					+ "\nChosse 'No' to close file without saving"
					+ "\nChoose 'Cancel' to stop close operation.", 
				tab.getFile().getName());
		
		int res = JOptionPane.showConfirmDialog(tab, mssg, "Save File...", JOptionPane.YES_NO_CANCEL_OPTION);
		
		if(res == JOptionPane.NO_OPTION)
		{
			logger.debug("Closing file '{}' without saving changes", tab.getFile().getName());
			return true;
		}
		
		if(res == JOptionPane.CANCEL_OPTION)
		{
			logger.debug("Cancelling close operation.");
			return false;
		}
		
		//on yes option
		FileEditor editor = (FileEditor) super.getComponentAt(index);
		editor.saveFile();
		
		return true;
	}
	
	private void closeFileAtIndex(int index) throws IOException
	{
		FileEditorTab fileEditorTab = (FileEditorTab)super.getTabComponentAt(index); 
		File file = fileEditorTab.getFile();
		super.removeTabAt(index);
		
		String canonicalPath = file.getCanonicalPath();
		pathToEditor.remove(canonicalPath);
	}
	
	@Action
	public void closeFile() throws IOException
	{
		File file = ideContext.getActiveFile();
		int index = getIndexWithFile(file);
		
		if(index < 0)
		{
			logger.debug("No tab found with active file. Hence ignoring close file request. File: {}", file);
			return;
		}
		
		if(!checkForChanges(index))
		{
			return;
		}
		
		logger.debug("Closing file with path: {}", file.getPath());
		closeFileAtIndex(index);
	}
	
	@Action
	public void closeAllFiles() throws IOException
	{
		int tabCount = super.getTabCount();
		
		for(int i = tabCount - 1; i >= 0; i--)
		{
			if(!checkForChanges(i))
			{
				return;
			}
		
			closeFileAtIndex(i);
		}
	}
	
	@Action
	public void closeAllButThis() throws IOException
	{
		File file = ideContext.getActiveFile();
		int curIdx = getIndexWithFile(file);
		
		if(curIdx < 0)
		{
			logger.debug("As no tab is found with active file, ignoring acion closeAllButThis");
			return;
		}
		
		int tabCount = super.getTabCount();
		
		for(int i = tabCount - 1; i >= 0; i--)
		{
			if(i == curIdx)
			{
				continue;
			}
			
			if(!checkForChanges(i))
			{
				return;
			}
		
			closeFileAtIndex(i);
		}
	}
	
	private void copyToClipboard(String content)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(content), null);
	}
	
	@Action
	public void copyFilePath() throws IOException
	{
		File file = ideContext.getActiveFile();
		
		if(file == null)
		{
			return;
		}
		
		copyToClipboard(file.getCanonicalPath());
	}
	
	@Action
	public void copyFileName()
	{
		File file = ideContext.getActiveFile();
		
		if(file == null)
		{
			return;
		}
		
		copyToClipboard(file.getName());
	}
	
	@Action
	public void copyDirPath() throws IOException
	{
		File file = ideContext.getActiveFile();
		
		if(file == null)
		{
			return;
		}
		
		copyToClipboard(file.getParentFile().getCanonicalPath());
	}
	
	private List<FileEditorTab> getAllTabs()
	{
		int tabCount = super.getTabCount();
		List<FileEditorTab>  tabs = new ArrayList<>();
		
		for(int i = 0; i < tabCount; i++)
		{
			tabs.add( (FileEditorTab) super.getTabComponentAt(i));
		}
		
		return tabs;
	}

	public void filePathChanged(File oldFolder, File newFolder)
	{
		List<FileEditorTab> tabs = getAllTabs();
		String relativePath = null;
		File relativeFile = null;
		
		for(FileEditorTab tab : tabs)
		{
			//if the current file path is not modified
			if(newFolder != null && tab.getFile().exists())
			{
				//continue to next file
				continue;
			}
			
			relativePath = IdeFileUtils.getRelativePath(oldFolder, tab.getFile());
			
			//if the file is not part of modified path
			if(relativePath == null)
			{
				continue;
			}
			
			if(newFolder != null)
			{
				if("".equals(relativePath))
				{
					relativeFile = newFolder;
				}
				else
				{
					relativeFile = new File(newFolder, relativePath);
				}
			}
			
			if(newFolder == null || !relativeFile.exists())
			{
				logger.debug("As the file does not exist anymore, closing tab with file: [old path: {}, Modified Path: {}]", tab.getFile().getPath(), relativeFile.getPath());
				
				pathToEditor.remove(tab.getFile().getPath());
				super.remove(tab.getFileEditor());
				continue;
			}
			
			relativePath = IdeFileUtils.getCanonicalPath(relativeFile);
			pathToEditor.put(relativePath, tab.getFileEditor());
			tab.setFile(relativeFile);
			tab.getFileEditor().setFile(relativeFile);
		}
	}

	public void filePathRemoved(File folder)
	{
		List<FileEditorTab> tabs = getAllTabs();
		String relativePath = null;
		
		for(FileEditorTab tab : tabs)
		{
			relativePath = IdeFileUtils.getRelativePath(folder, tab.getFile());
			
			//if the file is not part of modified path
			if(relativePath == null)
			{
				continue;
			}
			
			logger.debug("Closing file from folder being removed: [Path: {}]", 
					tab.getFile().getPath());
			
			pathToEditor.remove(tab.getFile().getPath());
			super.remove(tab.getFileEditor());
		}
	}
}
	
