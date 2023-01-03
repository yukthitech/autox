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
package com.yukthitech.autox.ide.projexplorer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.FreeMarkerMethodDocInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.ide.FileDetails;
import com.yukthitech.autox.ide.IIdeConstants;
import com.yukthitech.autox.ide.IIdeFileManager;
import com.yukthitech.autox.ide.IdeFileManagerFactory;
import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeIndex;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.actions.FileActions;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.events.ActiveFileChangedEvent;
import com.yukthitech.autox.ide.events.FileSavedEvent;
import com.yukthitech.autox.ide.help.HelpPanel;
import com.yukthitech.autox.ide.index.FileParseCollector;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.IdePopupMenu;
import com.yukthitech.autox.ide.layout.UiIdElementsManager;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.proj.ProjectManager;
import com.yukthitech.autox.ide.services.IdeEventHandler;
import com.yukthitech.swing.IconButton;
import com.yukthitech.swing.ToggleIconButton;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

@Component
public class ProjectExplorer extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ProjectExplorer.class);
	
	private static ImageIcon EDITOR_LINK_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/toggle-button.svg", 16);
	
	private static ImageIcon COLLAPSE_ALL_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/collapse-all.svg", 16);
	
	private static final String STATE_ATTR_EDITOR_LINK_BUTTON_STATE = "ProjectExplorer.editorLinkState";
	
	private JTree tree = new JTree();
	private JScrollPane treeScrollPane = new JScrollPane(tree);
	
	private ProjectExplorerModel projectTreeModel;

	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private TreeDragSource dragSource;
	
	@Autowired
	private TreeDropTarget dragTarget;
	
	@Autowired
	private IdeFileManagerFactory ideFileManagerFactory;
	
	@Autowired
	private IdeIndex ideIndex;
	
	@Autowired
	private FileActions fileActions;
	
	private IdePopupMenu projectExplorerTreePopup;
	
	private IdePopupMenu projectExplorerPopup;
	
	@Autowired
	private UiLayout uiLayout;
	
	@Autowired
	private ActionCollection actionCollection;
	
	/**
	 * Used to update project indexes.
	 */
	@Autowired
	private ProjectManager projectManager;
	
	private BaseTreeNode activeTreeNode;
	
	private JPanel iconPanel = new JPanel();
	
	private ToggleIconButton editorSyncButton = new ToggleIconButton();
	
	private IconButton minimizeAllButton = new IconButton();

	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	/**
	 * Create the panel.
	 */
	@PostConstruct
	private void init()
	{
		MouseListener listener = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				//handleMouseClick(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				handleMouseClick(e);
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() < 2 || e.getButton() != MouseEvent.BUTTON1)
				{
					return;
				}
				
				handleOpenEvent(e);
			}
		};
		
		addMouseListener(listener);
		
		projectTreeModel = new ProjectExplorerModel();
		tree.setModel(projectTreeModel);
		tree.setRootVisible(false);
		tree.setCellRenderer(new BaseTreeNodeRenderer());

		setLayout(new BorderLayout(0, 0));
		tree.setShowsRootHandles(true);
		tree.addMouseListener(listener);
		dragSource.setSourceTree(tree);
		dragTarget.setTargetTree(tree);
		
		tree.getInputMap().put(KeyStroke.getKeyStroke("ctrl C"), "dummy");
		tree.getInputMap().put(KeyStroke.getKeyStroke("ctrl V"), "dummy");
		tree.getInputMap().put(KeyStroke.getKeyStroke("ctrl X"), "dummy");
		tree.getInputMap().put(KeyStroke.getKeyStroke("F2"), "dummy");
		
		//set tool bar panel
		super.add(iconPanel, BorderLayout.NORTH);
		iconPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 1));
		
		minimizeAllButton.setIcon(COLLAPSE_ALL_ICON);
		minimizeAllButton.setToolTipText("Collapse All");
		minimizeAllButton.addActionListener(this::onCollapseAll);
		iconPanel.add(minimizeAllButton);

		editorSyncButton.setIcon(EDITOR_LINK_ICON);
		iconPanel.add(editorSyncButton);
		
		editorSyncButton.addActionListener(this::onEditorSync);
		
		tree.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_DELETE)
				{
					fileActions.deleteFile();
				}
			}
		});
		
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				if(tree.getSelectionCount() > 1)
				{
					return;
				}
				
				TreePath path = e.getPath();
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				
				if(treeNode instanceof FileTreeNode)
				{
					FileTreeNode fileNode = (FileTreeNode) treeNode;
					
					if(editorSyncButton.isSelected())
					{
						fileEditorTabbedPane.selectProjectFile(fileNode.getProject(), fileNode.getFile());
						tree.requestFocus();
					}
				}
			}
		});
		
		super.add(treeScrollPane, BorderLayout.CENTER);
		
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void saveState(IdeState state)
			{
				state.setAtribute(STATE_ATTR_EDITOR_LINK_BUTTON_STATE, editorSyncButton.isSelected());
			}
			
			@Override
			public void loadState(IdeState state)
			{
				if(Boolean.TRUE.equals(state.getAttribute(STATE_ATTR_EDITOR_LINK_BUTTON_STATE)))
				{
					editorSyncButton.setSelected(true);
				}
			}
		});
	}
	
	/*
	@IdeEventHandler
	public void onStartup(IdeStartedEvent e)
	{
		onEditorSync(null);
	}
	*/
	
	@IdeEventHandler
	private void onActiveFileChanged(ActiveFileChangedEvent event)
	{
		if(event.getSource() == ProjectExplorer.this)
		{
			return;
		}
		
		IdeUtils.executeUiTask(() -> setActiveFile(event.getFile()));
	}
	
	@IdeEventHandler
	private void onFileSave(FileSavedEvent e)
	{
		FileTreeNode fileNode = (FileTreeNode) getFileNode(e.getFile());
		
		if(fileNode == null)
		{
			return;
		}
		
		checkFile(fileNode);
	}
	
	private void onCollapseAll(ActionEvent e)
	{
		IdeUtils.execute(() -> 
		{
			int rowCount = tree.getRowCount() - 1;
			
			while(rowCount >= 0)
			{
				tree.collapseRow(rowCount);
				rowCount--;
			}
			
		}, 1);
	}
	
	private synchronized void onEditorSync(ActionEvent e)
	{
		if(!editorSyncButton.isSelected())
		{
			return;
		}
		
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(editor == null)
		{
			return;
		}
		
		File file = editor.getFile();
		
		if(file == null)
		{
			return;
		}
		
		setActiveFile(file);
	}
	
	private synchronized void setActiveFile(File file)
	{
		if(!editorSyncButton.isSelected())
		{
			return;
		}
		
		FileTreeNode node = (FileTreeNode) getFileNode(file);
		
		if(node == null)
		{
			return;
		}
		
		TreePath treePath = new TreePath(node.getPath());
		
		if(tree.isPathSelected(treePath))
		{
			return;
		}
		
		tree.setSelectionPath(treePath);
		tree.scrollPathToVisible(treePath);
	}
	
	private BaseTreeNode getFileNode(File file)
	{
		List<ProjectTreeNode> nodes = projectTreeModel.getProjectNodes();
		
		if(nodes == null || nodes.isEmpty())
		{
			return null;
		}
		
		file = IdeFileUtils.getCanonicalFile(file);
		BaseTreeNode fileNode = null;
		
		for(ProjectTreeNode projNode : nodes)
		{
			fileNode = projNode.getNode(file);
			
			if(fileNode != null)
			{
				return fileNode;
			}
		}
		
		return null;
	}
	
	public void setActiveTreeNode(BaseTreeNode activeTreeNode)
	{
		this.activeTreeNode = activeTreeNode;
	}

	private void initDocs(Project project)
	{
		String documentTemplate = null;
		String fmMethTemp = null;
		
		try
		{
			documentTemplate = IOUtils.toString(HelpPanel.class.getResourceAsStream("/autocomp-doc-templates/documentation.html"), Charset.defaultCharset());
			fmMethTemp = IOUtils.toString(HelpPanel.class.getResourceAsStream("/autocomp-doc-templates/fm-method-doc.html"), Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading templates", ex);
		}
		
		DocInformation docInfo = project.getDocInformation();
		String finalDoc = null;
		
		for(StepInfo step : docInfo.getSteps())
		{
			finalDoc = IIdeConstants.FREE_MARKER_ENGINE.processTemplate("/autocomp-doc-templates/documentation.html", 
						documentTemplate, 
						CommonUtils.toMap("node", step)
					);
			step.setDocumentation(finalDoc);
		}

		for(ValidationInfo validation : docInfo.getValidations())
		{
			finalDoc = IIdeConstants.FREE_MARKER_ENGINE.processTemplate("/autocomp-doc-templates/documentation.html", 
						documentTemplate, 
						CommonUtils.toMap("node", validation)
					);
			validation.setDocumentation(finalDoc);
		}
		
		for(FreeMarkerMethodDocInfo method : docInfo.getFreeMarkerMethods())
		{
			finalDoc = IIdeConstants.FREE_MARKER_ENGINE.processTemplate("/autocomp-doc-templates/fm-method-doc.html", 
					fmMethTemp, 
					CommonUtils.toMap("node", method)
				);
			
			method.setDocumentation(finalDoc);
		}
	}

	/**
	 * Called when an existing project object needs to be opened.
	 * @param project
	 */
	public synchronized void openProject(Project project)
	{
		projectTreeModel.addProject(new ProjectTreeNode(project.getName(), this, project));
		
		initDocs(project);
		
		logger.debug("Adding project {} to project tree", project.getName());
	}
	
	private void initMenus()
	{
		if(projectExplorerPopup != null)
		{
			return;
		}
		
		projectExplorerPopup = uiLayout.getPopupMenu("projectExplorerPopup").toPopupMenu(actionCollection);
		projectExplorerTreePopup = uiLayout.getPopupMenu("projectExplorerTreePopup").toPopupMenu(actionCollection);
	}
	
	public Project getSelectedProject()
	{
		TreePath path = tree.getSelectionPath();
		
		if(path == null)
		{
			return null;
		}
		
		Object lastComp = path.getLastPathComponent();
		
		if(lastComp instanceof FolderTreeNode)
		{
			return ((FolderTreeNode) lastComp).getProject();
		}
		else if(lastComp instanceof FileTreeNode)
		{
			return ((FileTreeNode) lastComp).getProject();
		}
		
		return null;
	}
	
	/**
	 * Fetches currently selected files.
	 * @return
	 */
	public List<File> getSelectedFiles()
	{
		TreePath selectedPaths[] = tree.getSelectionPaths();
		List<File> selectedFiles = new ArrayList<>();
		
		for(TreePath path : selectedPaths)
		{
			Object selectedItem = path.getLastPathComponent();
			
			if(selectedItem instanceof FolderTreeNode)
			{
				selectedFiles.add( ((FolderTreeNode)selectedItem).getFolder() );
			}
			else if(selectedItem instanceof FileTreeNode)
			{
				selectedFiles.add( ((FileTreeNode)selectedItem).getFile() );
			}
		}
		
		return selectedFiles;
	}
	
	public void openSelectedFiles()
	{
		TreePath selectedPaths[] = tree.getSelectionPaths();
		
		for(TreePath path : selectedPaths)
		{
			Object selectedItem = path.getLastPathComponent();
			
			if(selectedItem instanceof FileTreeNode)
			{
				FileTreeNode node = (FileTreeNode) selectedItem;
				fileEditorTabbedPane.openOrActivateFile(node.getProject(), node.getFile());
			}
		}
	}

	public boolean isSpecialNodeSelected()
	{
		TreePath selectedPaths[] = tree.getSelectionPaths();
		
		for(TreePath path : selectedPaths)
		{
			BaseTreeNode selectedItem = (BaseTreeNode) path.getLastPathComponent();
			
			if(IProjectExplorerConstants.isSpecialNode(selectedItem.getId()))
			{
				return true;
			}
		}
		
		return false;
	}

	public File getSelectedFile()
	{
		TreePath selectedPath = tree.getSelectionPath();
		
		if(selectedPath == null)
		{
			return null;
		}
		
		Object selectedItem = selectedPath.getLastPathComponent();
		
		if(selectedItem instanceof FolderTreeNode)
		{
			return ((FolderTreeNode)selectedItem).getFolder();
		}
		else if(selectedItem instanceof FileTreeNode)
		{
			return ((FileTreeNode)selectedItem).getFile();
		}
		
		return null;
	}

	private void selectRow(int row, boolean controlDown)
	{
		if(tree.isRowSelected(row))
		{
			return;
		}
		
		if(!controlDown)
		{
			tree.setSelectionRow(row);
			return;
		}
		
		tree.addSelectionRow(row);
	}
	
	private void handleMouseClick(MouseEvent e)
	{
		if(!e.isPopupTrigger())
		{
			return;
		}
		
		initMenus();
		
		int clickedRow = tree.getRowForLocation(e.getX(), e.getY());
		
		if(clickedRow < 0)
		{
			//This click may come from tree or project explorer itself. So approp component should
			//  be used for popup
			projectExplorerPopup.show( (JComponent) e.getSource(), e.getX(), e.getY());
			return;
		}

		selectRow(clickedRow, e.isControlDown() || e.isShiftDown());
		
		
		//find the selected files and types
		TreePath selectedPaths[] = tree.getSelectionPaths();
		List<File> selectedFiles = new LinkedList<>();
		Set<String> selectedTypes = new HashSet<>();
		boolean specialNodeSelected = false;
		boolean onlyFiles = true;
		
		for(TreePath path : selectedPaths)
		{
			BaseTreeNode node = (BaseTreeNode) path.getLastPathComponent();
			
			if(node instanceof ProjectTreeNode)
			{
				selectedTypes.add(IProjectExplorerConstants.TYPE_PROJECT);
				selectedFiles.add(((ProjectTreeNode) node).getFolder());
				specialNodeSelected = true;
				onlyFiles = false;
			}
			else if(node instanceof TestSuiteFolderTreeNode)
			{
				selectedTypes.add(IProjectExplorerConstants.TYPE_TEST_SUITE_FOLDER);
				selectedFiles.add(((TestSuiteFolderTreeNode) node).getFolder());
				specialNodeSelected = true;
				onlyFiles = false;
			}
			else if(node instanceof TestFolderTreeNode)
			{
				selectedTypes.add(IProjectExplorerConstants.TYPE_TEST_FOLDER);
				selectedFiles.add(((TestFolderTreeNode) node).getFolder());
				onlyFiles = false;
			}
			else if(node instanceof FolderTreeNode)
			{
				selectedTypes.add(IProjectExplorerConstants.TYPE_NORMAL_FOLDER);
				selectedFiles.add(((FolderTreeNode) node).getFolder());
				onlyFiles = false;
			}
			else if(node instanceof FileTreeNode)
			{
				BaseTreeNode parentNode = (BaseTreeNode) node.getParent();
				
				if((parentNode instanceof TestFolderTreeNode) || (parentNode instanceof TestSuiteFolderTreeNode))
				{
					selectedTypes.add(IProjectExplorerConstants.TYPE_TEST_FILE);					
				}
				else
				{
					selectedTypes.add(IProjectExplorerConstants.TYPE_NORMAL_FILE);
				}
				
				selectedFiles.add(((FileTreeNode) node).getFile());
				
				if(node.getId().startsWith(IProjectExplorerConstants.ID_PREFIX_APP_CONFIG) 
						|| node.getId().startsWith(IProjectExplorerConstants.ID_PREFIX_APP_PROP))
				{
					specialNodeSelected = true;
				}
			}
		}
		
		//Disable project explorer menu items approp
		boolean multiItemsSelected = (selectedFiles.size() > 1);
		boolean singleType = (selectedTypes.size() == 1);
		boolean testFolder = (selectedTypes.contains(IProjectExplorerConstants.TYPE_TEST_FOLDER)
				|| selectedTypes.contains(IProjectExplorerConstants.TYPE_TEST_SUITE_FOLDER))
				|| selectedTypes.contains(IProjectExplorerConstants.TYPE_TEST_FILE);
		
		UiIdElementsManager.getComponent("peNewMenuList").setEnabled(!multiItemsSelected 
				&& !selectedFiles.isEmpty() 
				&& selectedFiles.get(0).isDirectory());
		
		UiIdElementsManager.getComponent("peNewTestFile").setEnabled(testFolder);
		
		UiIdElementsManager.getComponent("peOpenFile").setEnabled(onlyFiles);
		
		boolean executable = 
				(!multiItemsSelected && selectedTypes.contains(IProjectExplorerConstants.TYPE_PROJECT))
				|| (!multiItemsSelected && selectedTypes.contains(IProjectExplorerConstants.TYPE_TEST_SUITE_FOLDER));
		
		UiIdElementsManager.getComponent("peExecute").setEnabled(executable);
		UiIdElementsManager.getComponent("peCut").setEnabled(!specialNodeSelected);
		UiIdElementsManager.getComponent("peCopy").setEnabled(!selectedFiles.isEmpty());
		UiIdElementsManager.getComponent("peCopyPath").setEnabled(!multiItemsSelected);
		UiIdElementsManager.getComponent("peRename").setEnabled(!multiItemsSelected && !specialNodeSelected);
		UiIdElementsManager.getComponent("peDelete").setEnabled(!specialNodeSelected);
		UiIdElementsManager.getComponent("peProjectProp").setEnabled(singleType && selectedTypes.contains(IProjectExplorerConstants.TYPE_PROJECT));
		
		Clipboard clip = (Clipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
		UiIdElementsManager.getComponent("pePaste").setEnabled(clip.isDataFlavorAvailable(DataFlavor.javaFileListFlavor));

		//show the popup
		TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
		Object clickedItem = treePath.getLastPathComponent();
		
		activeTreeNode = (BaseTreeNode) clickedItem;
		projectExplorerTreePopup.show(tree, e.getX(), e.getY());
	}
	
	private void handleOpenEvent(MouseEvent e)
	{
		int clickedRow = tree.getRowForLocation(e.getX(), e.getY());
		
		if(clickedRow < 0)
		{
			return;
		}
		
		TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
		Object clickedItem = treePath.getLastPathComponent();
		
		if(clickedItem instanceof FileTreeNode)
		{
			FileTreeNode fileTreeNode = (FileTreeNode) clickedItem;
			
			logger.debug("Setting active file as: [Project: {}, File: {}]", fileTreeNode.getProject().getName(), fileTreeNode.getFile().getPath());
			
			fileEditorTabbedPane.openOrActivateFile(fileTreeNode.getProject(), fileTreeNode.getFile());
		}
		else
		{
			logger.debug("As selected node is not file tree node, not setting active file. Current node: {}", clickedItem);
		}
	}
	
	public synchronized void reloadProjectNode(Project project)
	{
		ProjectTreeNode node = projectTreeModel.getProjectNode(project);
		
		if(node == null)
		{
			return;
		}
		
		node.reload(true);
		projectTreeModel.reload(node);
		projectTreeModel.nodeChanged(node);
		
		loadFilesToIndex();
	}
	
	public void newFilesAdded(List<File> files)
	{
		FolderTreeNode folderNode = null;
		
		if(activeTreeNode instanceof FolderTreeNode)
		{
			folderNode = (FolderTreeNode) activeTreeNode;
		}
		else
		{
			folderNode = (FolderTreeNode) ((FileTreeNode) activeTreeNode).getParent();
		}
		
		folderNode.newFilesAdded(files);
	}
	
	public void newFilesAdded(List<File> files, File parentFolder)
	{
		FolderTreeNode folderNode = (FolderTreeNode) getFileNode(parentFolder);
		folderNode.newFilesAdded(files);
	}

	public void filesRemoved(List<File> files)
	{
		//group the files by directory
		Map<File, List<File>> folderToFiles = new HashMap<>();

		for(File file : files)
		{
			File parent = file.getParentFile();
			
			List<File> dirFiles = folderToFiles.get(parent);
			
			if(dirFiles == null)
			{
				dirFiles = new ArrayList<>();
				folderToFiles.put(parent, dirFiles);
			}
			
			dirFiles.add(file);
		}
		
		//parent wise remove the files
		for(Map.Entry<File, List<File>> entry : folderToFiles.entrySet())
		{
			FolderTreeNode node = (FolderTreeNode) getFileNode(entry.getKey());
			
			if(node == null)
			{
				continue;
			}
			
			node.filesRemoved(entry.getValue());
		}
	}
	
	public void selectedFilesRemoved()
	{
		TreePath selectedPaths[] = tree.getSelectionPaths();
		Map<BaseTreeNode, Set<String>> removedNodes =  new IdentityHashMap<>();
		
		for(TreePath path : selectedPaths)
		{
			Object selectedItem = path.getLastPathComponent();
			File filePath = null;
			
			if(selectedItem instanceof FolderTreeNode)
			{
				filePath = ((FolderTreeNode)selectedItem).getFolder();
			}
			else if(selectedItem instanceof FileTreeNode)
			{
				filePath = ((FileTreeNode)selectedItem).getFile();
			}
			else
			{
				continue;
			}
			
			if(!filePath.exists())
			{
				Set<String> lst = removedNodes.get(path.getParentPath().getLastPathComponent());
				
				if(lst == null)
				{
					lst = new HashSet<>();
					removedNodes.put((BaseTreeNode) path.getParentPath().getLastPathComponent(), lst);
				}
				
				BaseTreeNode deletedNode = (BaseTreeNode) path.getLastPathComponent();
				lst.add(deletedNode.getId());
			}
		}

		for(Map.Entry<BaseTreeNode, Set<String>> entry : removedNodes.entrySet())
		{
			entry.getKey().removeChildNodes(entry.getValue());
		}
	}
	
	public void selectedFileRenamed(String newName)
	{
		TreePath selectedPath = tree.getSelectionPath();
		BaseTreeNode selectedNode = (BaseTreeNode) selectedPath.getLastPathComponent();
		BaseTreeNode parentNode = (BaseTreeNode) selectedNode.getParent();
		
		//rename the current node (which would change its id also)
		String selectedId = selectedNode.getId();
		String idPrefix = IProjectExplorerConstants.extractIdPrefix(selectedId);
		
		String newId = (idPrefix != null) ? idPrefix + newName : newName;
		selectedNode.rename(newId, newName);
		
		//post rename select the renamed node
		BaseTreeNode renamedNode = parentNode.getChild(newId);
		Object objPath[] = selectedPath.getPath();
		objPath[objPath.length - 1] = renamedNode;
		
		tree.setSelectionPath(new TreePath(objPath));
	}
	
	public BaseTreeNode reloadActiveNode()
	{
		if(activeTreeNode == null)
		{
			logger.debug("No active node found for reload.");
			return null;
		}

		activeTreeNode.reload(true);
		projectTreeModel.reload(activeTreeNode);
		
		loadFilesToIndex();
		return activeTreeNode;
	}

	
	public void reloadActiveNodeParent()
	{
		if(activeTreeNode == null)
		{
			return;
		}
		
		Object parentNodeObj = activeTreeNode.getParent();
		
		if(!(parentNodeObj instanceof BaseTreeNode))
		{
			return;
		}
		
		BaseTreeNode parentNode = (BaseTreeNode) parentNodeObj;
		
		parentNode.reload(true);
		projectTreeModel.reload(parentNode);
		
		loadFilesToIndex();
	}
	
	void checkFile(FileTreeNode fileNode)
	{
		File file = fileNode.getFile();
		Project project = fileNode.getProject();
		
		//ignore files which are outside test suite folders
		if(!project.isTestSuiteFolderFile(file))
		{
			return;
		}

		IIdeFileManager fileManager = ideFileManagerFactory.getFileManager(fileNode.getProject(), file);
		
		if(fileManager != null)
		{
			//logger.debug("Parsing and loading file: {}", file.getPath());
			
			FileParseCollector collector = new FileParseCollector(fileNode.getProject(), file);
		
			try
			{
				fileManager.parseFile(fileNode.getProject(), file, collector);
				projectManager.getProjectIndex(project.getName()).addReferableElements(file, collector.getReferableElements());
				
				fileNode.setErrored(collector.getErrorCount() > 0);
				fileNode.setWarned(collector.getWarningCount() > 0);
			} catch(Exception ex)
			{
				logger.error("An error occurred while loading file: {}", file.getPath(), ex);
			}
		}
	}
	
	public ProjectExplorerModel getProjectTreeModel()
	{
		return projectTreeModel;
	}
	
	public void loadFilesToIndex()
	{
		ideIndex.cleanFileIndex();
		
		for(ProjectTreeNode projNode : projectTreeModel.getProjectNodes())
		{
			loadFilesFromNode(projNode, projNode);
		}
	}
	
	private void loadFilesFromNode(ProjectTreeNode projNode, BaseTreeNode node)
	{
		if(node instanceof FileTreeNode)
		{
			File file = ((FileTreeNode) node).getFile();
			ideIndex.addFile( new FileDetails(file, projNode.getProject()) );
			return;
		}
		
		Collection<BaseTreeNode> childNodes = node.getChildNodes();
		
		if(childNodes == null || childNodes.isEmpty())
		{
			return;
		}
		
		for(BaseTreeNode cnode : childNodes)
		{
			loadFilesFromNode(projNode, cnode);
		}
	}
	
	public void deleteProject(Project project)
	{
		projectTreeModel.deleteProject(project);
		fileEditorTabbedPane.filePathRemoved(project.getBaseFolder());
	}
	
	public BaseTreeNode getActiveNode()
	{
		TreePath selectedPath = tree.getSelectionPath();
		
		if(selectedPath == null)
		{
			return null;
		}
		
		return (BaseTreeNode) selectedPath.getLastPathComponent();
	}
}