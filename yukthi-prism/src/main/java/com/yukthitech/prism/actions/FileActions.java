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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.common.IndexRange;
import com.yukthitech.prism.FileDetails;
import com.yukthitech.prism.IdeFileUtils;
import com.yukthitech.prism.IdeIndex;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.editor.FileEditor;
import com.yukthitech.prism.editor.FileEditorTabbedPane;
import com.yukthitech.prism.layout.Action;
import com.yukthitech.prism.layout.ActionHolder;
import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.projexplorer.BaseTreeNode;
import com.yukthitech.prism.projexplorer.ProjectExplorer;
import com.yukthitech.prism.projexplorer.ProjectTreeNode;
import com.yukthitech.prism.search.FileSearchQuery;
import com.yukthitech.prism.search.FileSearchService;
import com.yukthitech.prism.search.SearchDialog;
import com.yukthitech.prism.ui.InProgressDialog;
import com.yukthitech.utils.exceptions.InvalidStateException;

@ActionHolder
public class FileActions
{
	private static Logger logger = LogManager.getLogger(FileActions.class);

	private static final String TEST_FILE_TEMPLATE = "testFileTemplate";

	private static Map<String, String> templates = new HashMap<>();

	static
	{
		try
		{
			templates.put(TEST_FILE_TEMPLATE, 
					IOUtils.toString(FileActions.class.getResourceAsStream("/templates/test-file-template.xml"), Charset.defaultCharset()));
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading templates", ex);
		}
	}

	@Autowired
	private ProjectExplorer projectExplorer;

	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;

	@Autowired
	private IdeIndex ideIndex;
	
	@Autowired
	private ProjectActions projectActions;
	
	@Autowired
	private SearchDialog searchDialog;

	@Autowired
	private FileEditorTabbedPane fileTabbedPane;
	
	@Autowired
	private FileSearchService fileSearchService;

	@Action
	public void newFolder()
	{
		File activeFolder = projectExplorer.getSelectedFile();
		String newFolder = JOptionPane.showInputDialog("Please provide new folder name?");

		if(newFolder == null)
		{
			return;
		}

		newFolder = newFolder.replace("\\", File.separator);
		newFolder = newFolder.replace("/", File.separator);

		File finalFolder = new File(activeFolder, newFolder);

		if(finalFolder.exists())
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Specified folder already exists: \n" + finalFolder.getPath());
			return;
		}

		try
		{
			FileUtils.forceMkdir(finalFolder);
		} catch(Exception ex)
		{
			logger.error("Failed to create folder: " + finalFolder.getPath(), ex);
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Failed to create folder: \n" + finalFolder.getPath() + "\nError: " + ex.getMessage());
		}

		//projectExplorer.reloadActiveNode();
		projectExplorer.newFilesAdded(Arrays.asList(finalFolder));
	}

	private File createFile(String templateName, String defaultExtension)
	{
		File activeFolder = projectExplorer.getSelectedFile();
		String newFile = JOptionPane.showInputDialog("Please provide new test-file name?");

		if(newFile == null)
		{
			return null;
		}

		newFile = newFile.replace("\\", File.separator);
		newFile = newFile.replace("/", File.separator);
		
		if(defaultExtension != null && !newFile.toLowerCase().endsWith(defaultExtension.toLowerCase()))
		{
			newFile += defaultExtension;
		}
		
		//if the action was invoked on file, use its parent folder
		if(!activeFolder.isDirectory())
		{
			activeFolder = activeFolder.getParentFile();
		}

		File finalFile = new File(activeFolder, newFile);

		if(finalFile.exists())
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Specified file already exists: \n" + finalFile.getPath());
			return null;
		}

		if(templateName != null)
		{
			try
			{
				FileUtils.write(finalFile, templates.get(templateName), Charset.defaultCharset());
			} catch(Exception ex)
			{
				logger.error("Failed to create file: " + finalFile.getPath(), ex);
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Failed to create file: \n" + finalFile.getPath() + "\nError: " + ex.getMessage());
			}
		}
		else
		{
			try
			{
				finalFile.createNewFile();
			} catch(Exception ex)
			{
				logger.error("Failed to create file: " + finalFile.getPath(), ex);
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Failed to create file: \n" + finalFile.getPath() + "\nError: " + ex.getMessage());
			}
		}

		projectExplorer.newFilesAdded(Arrays.asList(finalFile));
		return finalFile;
	}

	@Action
	public void newTestFile() throws IOException
	{
		File file = createFile(TEST_FILE_TEMPLATE, ".xml");

		if(file != null)
		{
			fileEditorTabbedPane.openOrActivateFile(projectExplorer.getSelectedProject(), file);
		}
	}

	@Action
	public void newFile() throws IOException
	{
		File file = createFile(null, null);

		if(file != null)
		{
			fileEditorTabbedPane.openOrActivateFile(projectExplorer.getSelectedProject(), file);
		}
	}

	@Action
	public void openFile()
	{
		projectExplorer.openSelectedFiles();
	}

	@Action
	public void deleteFile()
	{
		BaseTreeNode baseNode = projectExplorer.getActiveNode();
		
		if(baseNode instanceof ProjectTreeNode)
		{
			projectActions.deleteProject();
			return;
		}
		
		if(projectExplorer.isSpecialNodeSelected())
		{
			return;
		}
		
		List<File> activeFiles = projectExplorer.getSelectedFiles();

		if(CollectionUtils.isEmpty(activeFiles))
		{
			return;
		}
		
		String mssg = "Delete these " + activeFiles.size() + " files/folders?";
		
		if(activeFiles.size() == 1)
		{
			mssg = String.format("Delete %s '%s'?", 
					(activeFiles.get(0).isDirectory() ? "folder" : "file"),
					activeFiles.get(0).getName());
		}
		
		int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), mssg, "Delete", JOptionPane.YES_NO_OPTION);
		
		if(res != JOptionPane.YES_OPTION)
		{
			return;
		}
		
		List<File> filesRemoved = new ArrayList<File>();

		for(File activeFile : activeFiles)
		{
			try
			{
				FileUtils.forceDelete(activeFile);
				filesRemoved.add(activeFile);
			}catch(Exception ex)
			{
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Failed to delete file/folder: " + activeFile.getPath() + "\nError: " + ex.getMessage());
				break;
			}
		}
		
		projectExplorer.selectedFilesRemoved();
		filesRemoved.forEach(file -> fileEditorTabbedPane.filePathRemoved(file));
	}

	private void renameFile(File activeFile)
	{
		String newName = JOptionPane.showInputDialog("Please provide new name for file?", activeFile.getName());

		if(newName == null || newName.equals(activeFile.getName()))
		{
			return;
		}

		File newNameFile = new File(activeFile.getParentFile(), newName);
		activeFile.renameTo(newNameFile);

		projectExplorer.selectedFileRenamed(newNameFile.getName());
		fileEditorTabbedPane.filePathChanged(activeFile, newNameFile);
	}

	private void renameFolder(File activeFolder)
	{
		String newName = JOptionPane.showInputDialog("Please provide new name for folder?", activeFolder.getName());

		if(newName == null || newName.equals(activeFolder.getName()))
		{
			return;
		}

		File newNameFile = new File(activeFolder.getParentFile(), newName);
		activeFolder.renameTo(newNameFile);

		projectExplorer.selectedFileRenamed(newNameFile.getName());
		fileEditorTabbedPane.filePathChanged(activeFolder, newNameFile);
	}

	@Action
	public void renameFile()
	{
		if(projectExplorer.isSpecialNodeSelected())
		{
			return;
		}

		File activeFile = projectExplorer.getSelectedFile();

		if(activeFile == null)
		{
			return;
		}

		if(activeFile.isDirectory())
		{
			renameFolder(activeFile);
		}
		else
		{
			renameFile(activeFile);
		}
	}
	
	@Action
	public void cutFile() throws UnsupportedFlavorException, IOException 
	{
		if(projectExplorer.isSpecialNodeSelected())
		{
			return;
		}

		List<File> activeFiles = projectExplorer.getSelectedFiles();
		
		if(CollectionUtils.isEmpty(activeFiles))
		{
			return;
		}
		
		copyFile(activeFiles, true);
	}

	public void copyFile(List<File> listOfFiles, boolean moveOperation) throws UnsupportedFlavorException, IOException
	{
		TransferableFiles fileTransferable = new TransferableFiles(listOfFiles, moveOperation);
		Clipboard clip = (Clipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
		clip.setContents(fileTransferable, null);
	}

	@Action
	public void copyFile() throws UnsupportedFlavorException, IOException
	{
		if(projectExplorer.isSpecialNodeSelected())
		{
			return;
		}

		List<File> activeFiles = projectExplorer.getSelectedFiles();

		if(CollectionUtils.isEmpty(activeFiles))
		{
			return;
		}

		copyFile(activeFiles, false);
	}
	
	@Action
	public void copyPath() throws UnsupportedFlavorException, IOException
	{
		File activeFile = projectExplorer.getSelectedFile();
		
		StringSelection selection = new StringSelection(activeFile.getPath());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	private File getNewNameFor(File file)
	{
		String name = FilenameUtils.getBaseName(file.getName());
		String ext = FilenameUtils.getExtension(file.getName());
		int idx = 1;
		File parent = file.getParentFile();
		
		ext = StringUtils.isNotBlank(ext) ? "." + ext : "";
		
		while(file.exists())
		{
			file = new File(parent, name + "-" + idx + ext);
			idx++;
		}
		
		return file;
	}

	public void pasteFile(File activeFolder, List<File> list, boolean moveOperation) throws IOException, InterruptedException
	{
		//if active folder is a file, consider its parent directory
		if(activeFolder.isFile())
		{
			activeFolder = activeFolder.getParentFile();
		}
		
		List<File> destFiles = new ArrayList<>(list.size());
		List<File> srcFiles = new ArrayList<>(list.size());
		
		for(File srcFile : list)
		{
			String fname = srcFile.getName();
			File destFile = new File(activeFolder.getAbsolutePath() + File.separator + fname);

			if(destFile.exists())
			{
				if(srcFile.isDirectory() != destFile.isDirectory())
				{
					if(srcFile.isDirectory())
					{
						JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
								String.format("A file (not folder) with same name as source folder already exist '%s'. Skipping the copy of this folder.", srcFile.getName())
						);
					}
					else
					{
						JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
								String.format("A folder (not file) with same name as source file already exist '%s'. Skipping the copy of this file.", srcFile.getName())
						);
					}
					
					continue;
				}
				
				int res = 0;
				
				if(!destFile.isDirectory())
				{
					if(srcFile.equals(destFile))
					{
						destFile = getNewNameFor(destFile);
					}
					else
					{
						res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), 
								String.format("A file with name '%s' already exist. Do you want to overwrite?", destFile.getName()), 
								"Overwrite", 
								JOptionPane.YES_NO_CANCEL_OPTION);
					}
				}
				else
				{
					res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), 
							String.format("A folder with name '%s' already exist. The subfolder and files will be merged. "
									+ "\nExisting file(s) will be overwritten."
									+ "\nDo you want to continue?", destFile.getName()), 
							"Overwrite", 
							JOptionPane.YES_NO_CANCEL_OPTION);
				}

				if(res == JOptionPane.CANCEL_OPTION)
				{
					return;
				}
				
				if(res == JOptionPane.NO_OPTION)
				{
					continue;
				}
			}
			
			if(srcFile.isDirectory())
			{
				FileUtils.copyDirectory(srcFile, destFile);
			}
			else
			{
				FileUtils.copyFile(srcFile, destFile);
			}
			
			destFiles.add(destFile);
			srcFiles.add(srcFile);
		}
		
		if(!destFiles.isEmpty())
		{
			projectExplorer.newFilesAdded(destFiles, activeFolder);
		}
		
		if(moveOperation && !srcFiles.isEmpty()) 
		{
			for(File file : srcFiles)
			{
				FileUtils.forceDelete(file);
			}
			
			projectExplorer.filesRemoved(srcFiles);
		}
	}

	@SuppressWarnings("unchecked")
	private void pasteFile(File activeFolder) throws UnsupportedFlavorException, IOException, InterruptedException
	{
		Clipboard clip = (Clipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
		
		if(!clip.isDataFlavorAvailable(DataFlavor.javaFileListFlavor))
		{
			return;
		}
		
		TransferableFiles.FileData fileData = clip.isDataFlavorAvailable(TransferableFiles.SERIALIZED_DATA) ? 
				(TransferableFiles.FileData) clip.getData(TransferableFiles.SERIALIZED_DATA) : null;
		
		if(fileData != null)
		{
			pasteFile(activeFolder, fileData.getListOfFiles(), fileData.isMoveOperation());
		}
		else
		{
			List<File> list = (List<File>) clip.getData(DataFlavor.javaFileListFlavor);
			pasteFile(activeFolder, list, false);
		}
	}

	@Action
	public void pasteFile() throws UnsupportedFlavorException, IOException, InterruptedException
	{
		File activeFolder = projectExplorer.getSelectedFile();
		pasteFile(activeFolder);
	}

	@Action
	public void refreshFolder()
	{
		BaseTreeNode node = projectExplorer.getActiveNode();
		
		if(node instanceof ProjectTreeNode)
		{
			ProjectTreeNode projNode = (ProjectTreeNode) node;
			projNode.getProject().reset();
		}

		InProgressDialog.getInstance().display("Refresh in progress", () -> projectExplorer.reloadActiveNode());
	}
	
	public void gotoFile(Project project, String file, int lineNo, boolean limitToTestSuiteFolder)
	{
		FileDetails selectedFile = null;
		
		for(FileDetails fileDet : ideIndex.getFiles())
		{
			if(fileDet.getProject() != project || !fileDet.getFile().getName().equals(file))
			{
				continue;
			}
			
			//if limitToTestSuiteFolder flag is true, consider current only if it is part of test suite folder 
			if(limitToTestSuiteFolder)
			{
				boolean tsFolderFile = false;
				
				for(String tsFolder : project.getTestSuitesFoldersList())
				{
					File folder = new File(project.getBaseFolderPath(), tsFolder);
					
					if(IdeFileUtils.getRelativePath(folder, fileDet.getFile()) != null)
					{
						tsFolderFile = true;
						break;
					}
				}
				
				if(!tsFolderFile)
				{
					continue;
				}
			}

			selectedFile = fileDet;
			break;
		}
		
		if(selectedFile == null)
		{
			return;
		}

		FileEditor editor = fileEditorTabbedPane.openProjectFile(project, selectedFile.getFile());
		
		if(lineNo < 0 || editor == null)
		{
			return;
		}
		
		editor.gotoLine(lineNo);
	}
	
	public FileEditor gotoFilePath(Project project, String filePath, int lineNo)
	{
		return gotoFilePath(project, filePath, lineNo, null);
	}

	public FileEditor gotoFilePath(Project project, String filePath, int lineNo, IndexRange indexRange)
	{
		File fileToOpen = new File(filePath);
		String path = IdeFileUtils.getRelativePath(project.getBaseFolder(), fileToOpen);
		
		if(path == null)
		{
			return null;
		}
		
		FileEditor editor = fileEditorTabbedPane.openProjectFile(project, fileToOpen);
		
		if(editor == null)
		{
			return editor;
		}
		
		if(lineNo >= 0)
		{
			editor.gotoLine(lineNo);
		}
		
		if(indexRange != null)
		{
			editor.selectText(indexRange.getStart(), indexRange.getEnd());
		}
		
		return editor;
	}

	@Action
	public void search()
	{
		searchDialog.display();
	}

	@Action
	public void searchContextWord()
	{
		FileEditor fileEditor = fileTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			return;
		}
		
		String currentWord = fileEditor.getSelectedText();
		
		if(currentWord == null)
		{
			currentWord = fileEditor.getCursorWord();
			
			if(currentWord == null)
			{
				return;
			}
		}
		//when using selected text, use only first line, in case text spans multiple lines
		else
		{
			int lineIdx = currentWord.indexOf("\n");
			
			if(lineIdx == 0)
			{
				return;
			}
			
			if(lineIdx > 0)
			{
				currentWord = currentWord.substring(0, lineIdx);
			}
		}
		
		FileSearchQuery qry = FileSearchQuery.newTextQuery(currentWord, true, false, false, "", Arrays.asList("*"), fileEditor.getProject());
		fileSearchService.findAll(qry, false);
	}
}
