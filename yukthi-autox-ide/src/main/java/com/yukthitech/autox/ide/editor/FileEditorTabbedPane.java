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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
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
import com.yukthitech.autox.ide.events.ActiveFileChangedEvent;
import com.yukthitech.autox.ide.events.IdeStartedEvent;
import com.yukthitech.autox.ide.exeenv.debug.DebugPointsChangedEvent;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.layout.IdeActionEvent;
import com.yukthitech.autox.ide.model.FileState;
import com.yukthitech.autox.ide.model.IdeSettings;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.model.ProjectState;
import com.yukthitech.autox.ide.services.IdeEventHandler;
import com.yukthitech.autox.ide.services.IdeSettingChangedEvent;
import com.yukthitech.swing.MessageDialog;
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
		super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
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
	private void onActiveFileChanged(ActiveFileChangedEvent event)
	{
		if(event.getSource() == this)
		{
			return;
		}
		
		selectProjectFile(event.getProject(), event.getFile());
	}
	
	@IdeEventHandler
	private synchronized void onIdeSettingsChanged(IdeSettingChangedEvent event)
	{
		changeEditorSettings(event.getIdeSettings());
	}
	
	@IdeEventHandler
	private void onStartup(IdeStartedEvent event)
	{
		List<FileEditorTab> tabs = getAllTabs();
		
		if(CollectionUtils.isNotEmpty(tabs))
		{
			tabs.forEach(tab -> 
			{
				FileEditor editor = tab.getFileEditor();
				editor.scrollToCaretPosition();
			});
		}
		
		FileEditor fileEditor = getCurrentFileEditor();
		
		if(fileEditor != null)
		{
			fileEditor.getTextArea().requestFocus();
		}
	}
	
	@IdeEventHandler
	private void onDebugPointChange(DebugPointsChangedEvent event)
	{
		//if the event is generated as part of file editor, ignore it
		if(event.getSource() instanceof FileEditorIconManager)
		{
			return;
		}
		
		List<FileEditorTab> tabs = getAllTabs();
		
		if(CollectionUtils.isNotEmpty(tabs))
		{
			tabs.forEach(tab -> 
			{
				FileEditor editor = tab.getFileEditor();
				editor.getIconManager().reloadDebugPoints();
			});
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
		
		synchronized(pathToEditor)
		{
			for(FileEditor editor : pathToEditor.values())
			{
				editor.changeSettings(editorFont, enableTextWrapping);
			}
		}
	}
	
	private void saveFilesState(IdeState state) throws IOException
	{
		int tabCount = super.getTabCount();
		Set<String> existingProjects = new HashSet<>();
		
		for(int i = 0; i < tabCount; i++)
		{
			FileEditorTab tab = (FileEditorTab) super.getTabComponentAt(i);
			FileEditor editor = (FileEditor) super.getComponentAt(i);
			ProjectState projectState = state.addOpenProject(tab.getProject());
			
			if(!existingProjects.contains(tab.getProject().getName()))
			{
				existingProjects.add(tab.getProject().getName());
				projectState.getOpenFiles().clear();
			}
			
			projectState.addOpenFile(new FileState(tab.getFile().getCanonicalPath(), editor.getCaretPosition()));
		}
		
		FileEditor activeTab = getCurrentFileEditor();
		
		if(activeTab != null)
		{
			state.setAtribute(ACTIVE_FILE, activeTab.getFile().getCanonicalPath());
		}
	}
	
	public FileEditor getOpenFile(Project project, File file)
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

		synchronized(pathToEditor)
		{
			FileEditor fileEditor = pathToEditor.get(canonicalPath);
			return fileEditor;
		}
	}
	
	public FileEditor getFileEditor(File file)
	{
		String canonicalPath = null;
		
		try
		{
			canonicalPath = file.getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while fetching cannonical path of file: {}", file.getPath(), ex);
		}
		
		synchronized(pathToEditor)
		{
			FileEditor fileEditor = pathToEditor.get(canonicalPath);
			return fileEditor;
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

		synchronized(pathToEditor)
		{
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

			fileEditor.changeSettings(editorFont, enableTextWrapping);
			
			FileEditorTab fileEditorTab = new FileEditorTab(project, file, fileEditor, this, maximizationListener);
			IdeUtils.autowireBean(applicationContext, fileEditorTab);
			
			fileEditor.setFileEditorTab(fileEditorTab);
			
			addTab(file.getName(), null, fileEditor);
			super.setTabComponentAt(nextTabIndex, fileEditorTab);
			
			pathToEditor.put(canonicalPath, fileEditor);
			
			super.setSelectedComponent(fileEditor);
			return fileEditor;
		}
	}
	
	public void selectProjectFile(Project project, File file)
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

		synchronized(pathToEditor)
		{
			FileEditor fileEditor = pathToEditor.get(canonicalPath);
			
			if(fileEditor != null)
			{
				//logger.debug("Selecting existing tab for file: {}", canonicalPath);
				super.setSelectedComponent(fileEditor);
			}
		}
	}

	public FileEditor getCurrentFileEditor()
	{
		return (FileEditor) super.getSelectedComponent();
	}
	
	public void openOrActivateFile(Project project, File file)
	{
		openProjectFile(project, file);
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
	
	int getIndexOfTab(FileEditorTab tab)
	{
		int tabCount = super.getTabCount();
		
		for(int i = 0; i < tabCount; i++)
		{
			if(super.getTabComponentAt(i) == tab)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public boolean checkForUnsavedFiles()
	{
		int tabCount = super.getTabCount();
		boolean saveAll = false;
		
		for(int i = 0; i < tabCount; i++)
		{
			FileEditorTab tab = (FileEditorTab) super.getTabComponentAt(i);
			
			if(!tab.isFileChanged())
			{
				continue;
			}
			
			if(saveAll)
			{
				tab.getFileEditor().saveFile();
				continue;
			}
			
			String action = MessageDialog.display("IDE Closing", String.format("File '%s' is not saved. Do you want to save the file before closing?", tab.getFile().getName()), 
					Arrays.asList("Save", "Save All", "Skip", "Skip All", "Cancel"));
			
			if(action == null || "Cancel".equals(action))
			{
				return false;
			}
			
			if("Skip All".equals(action))
			{
				return true;
			}
			
			if("Skip".equals(action))
			{
				continue;
			}
			
			if("Save All".equals(action))
			{
				saveAll = true;
			}
			
			tab.getFileEditor().saveFile();
		}
		
		return true;
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
		synchronized(pathToEditor)
		{
			FileEditorTab fileEditorTab = (FileEditorTab)super.getTabComponentAt(index); 
			File file = fileEditorTab.getFile();
			super.removeTabAt(index);
			
			fileEditorTab.getFileEditor().editorClosed();
			
			String canonicalPath = file.getCanonicalPath();
			pathToEditor.remove(canonicalPath);
		}
	}
	
	@Action
	public void closeFile(IdeActionEvent e) throws IOException
	{
		FileEditorTab fileEditorTab = (FileEditorTab) e.getActionSource();
		closeFile(fileEditorTab);
	}
	
	void closeFile(FileEditorTab tab)
	{
		int index = getIndexOfTab(tab);
		
		if(index < 0)
		{
			logger.debug("No tab found with active file. Hence ignoring close file request. File: {}", tab.getFile().getPath());
			return;
		}
		
		if(!checkForChanges(index))
		{
			return;
		}
		
		logger.debug("Closing file with path: {}", tab.getFile().getPath());
		
		try
		{
			closeFileAtIndex(index);
		}catch(IOException ex)
		{
			logger.error("An error occurred while closing file: " + tab.getFile().getPath(), ex);
		}
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
	
	private void closeTabs(int fromIdx, int toIdx) throws IOException
	{
		for(int i = toIdx; i >= fromIdx; i--)
		{
			if(!checkForChanges(i))
			{
				return;
			}
		
			closeFileAtIndex(i);
		}
	}
	
	@Action
	public void closeAllButThis(IdeActionEvent e) throws IOException
	{
		FileEditorTab fileEditorTab = (FileEditorTab) e.getActionSource();
		int curIdx = getIndexOfTab(fileEditorTab);
		
		int tabCount = super.getTabCount();
		int maxIdx = tabCount - 1;
		
		if(curIdx < maxIdx)
		{
			closeTabs(curIdx + 1, maxIdx);
		}
		
		if(curIdx > 0)
		{
			closeTabs(0, curIdx - 1);
		}
	}
	
	@Action
	public void closeTabsToRight(IdeActionEvent e) throws IOException
	{
		FileEditorTab fileEditorTab = (FileEditorTab) e.getActionSource();
		int curIdx = getIndexOfTab(fileEditorTab);
		
		int tabCount = super.getTabCount();
		closeTabs(curIdx + 1, tabCount - 1);
	}

	@Action
	public void closeTabsToLeft(IdeActionEvent e) throws IOException
	{
		FileEditorTab fileEditorTab = (FileEditorTab) e.getActionSource();
		int curIdx = getIndexOfTab(fileEditorTab);
		
		if(curIdx > 0)
		{
			closeTabs(0, curIdx - 1);
		}
	}

	private void copyToClipboard(String content)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(content), null);
	}
	
	@Action
	public void copyFilePath(IdeActionEvent e) throws IOException
	{
		FileEditorTab fileEditorTab = (FileEditorTab) e.getActionSource();
		File file = fileEditorTab.getFile();
		
		if(file == null)
		{
			return;
		}
		
		copyToClipboard(file.getCanonicalPath());
	}
	
	@Action
	public void copyFileName(IdeActionEvent e)
	{
		FileEditorTab fileEditorTab = (FileEditorTab) e.getActionSource();
		File file = fileEditorTab.getFile();
		
		if(file == null)
		{
			return;
		}
		
		copyToClipboard(file.getName());
	}
	
	@Action
	public void copyDirPath(IdeActionEvent e) throws IOException
	{
		FileEditorTab fileEditorTab = (FileEditorTab) e.getActionSource();
		File file = fileEditorTab.getFile();
		
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

	public void filePathChanged(File oldPath, File newPath)
	{
		List<FileEditorTab> tabs = getAllTabs();
		String relativePath = null;
		File relativeFile = null;
		
		for(FileEditorTab tab : tabs)
		{
			//if the current file path is not modified
			if(tab.getFile().exists())
			{
				//continue to next file
				continue;
			}
			
			relativePath = IdeFileUtils.getRelativePath(oldPath, tab.getFile());
			
			//if the file is not part of modified path
			if(relativePath == null)
			{
				continue;
			}
			
			if(newPath != null)
			{
				if("".equals(relativePath))
				{
					relativeFile = newPath;
				}
				else
				{
					relativeFile = new File(newPath, relativePath);
				}
			}
			
			if(!relativeFile.exists())
			{
				logger.debug("As the file does not exist anymore, closing tab with file: [old path: {}, Modified Path: {}]", tab.getFile().getPath(), relativeFile.getPath());
				
				synchronized(pathToEditor)
				{
					pathToEditor.remove(tab.getFile().getPath());
					super.remove(tab.getFileEditor());
				}
				
				continue;
			}
			
			synchronized(pathToEditor)
			{
				relativePath = IdeFileUtils.getCanonicalPath(relativeFile);
				pathToEditor.put(relativePath, tab.getFileEditor());
			}
			
			tab.setFile(relativeFile);
			tab.getFileEditor().setFile(relativeFile);
		}
	}

	public void filePathRemoved(File file)
	{
		List<FileEditorTab> tabs = getAllTabs();
		String relativePath = null;
		
		for(FileEditorTab tab : tabs)
		{
			relativePath = IdeFileUtils.getRelativePath(file, tab.getFile());
			
			//if the file is not part of modified path
			if(relativePath == null)
			{
				continue;
			}
			
			logger.debug("Closing file from folder being removed: [Path: {}]", 
					tab.getFile().getPath());
			
			synchronized(pathToEditor)
			{
				pathToEditor.remove(tab.getFile().getPath());
				super.remove(tab.getFileEditor());
			}
		}
	}
}
	
