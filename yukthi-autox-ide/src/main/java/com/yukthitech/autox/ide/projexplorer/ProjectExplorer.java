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
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.IIdeConstants;
import com.yukthitech.autox.ide.IIdeFileManager;
import com.yukthitech.autox.ide.IdeFileManagerFactory;
import com.yukthitech.autox.ide.IdeIndex;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.help.HelpPanel;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.autox.ide.ui.BaseTreeNodeRenderer;
import com.yukthitech.autox.ide.ui.TestSuiteFolderTreeNode;
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
	
	private ProjectsTreeModel projectTreeModel;

	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private TreeDragSource source;
	
	@Autowired
	private TreeDropTarget target;
	
	@Autowired
	private IdeFileManagerFactory ideFileManagerFactory;
	
	@Autowired
	private IdeIndex ideIndex;
	
	private JPopupMenu filePopup;
	
	private JPopupMenu folderPopup;
	
	private JPopupMenu projectExplorerPopup;
	
	private JPopupMenu projectPopup;
	
	private JPopupMenu testSuitePopup;
	
	private JPopupMenu testFolderPopup;
	
	@Autowired
	private UiLayout uiLayout;
	
	@Autowired
	private ActionCollection actionCollection;
	
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
				handleMouseClick(e);
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
		
		projectTreeModel = new ProjectsTreeModel();
		tree.setModel(projectTreeModel);
		tree.setRootVisible(false);
		tree.setCellRenderer(new BaseTreeNodeRenderer());

		setLayout(new BorderLayout(0, 0));
		tree.setShowsRootHandles(true);
		tree.addMouseListener(listener);
		source.setSourceTree(tree);
		target.setTargetTree(tree);
		
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
		
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				TreePath path = e.getPath();
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				
				if(treeNode instanceof FileTreeNode)
				{
					FileTreeNode fileNode = (FileTreeNode) treeNode;
					ideContext.setActiveDetails(fileNode.getProject(), fileNode.getFile());
					
					if(editorSyncButton.isSelected())
					{
						ideContext.getProxy().activeFileChanged(fileNode.getFile(), ProjectExplorer.this);
					}
				}
				else if(treeNode instanceof FolderTreeNode)
				{
					FolderTreeNode folderNode = (FolderTreeNode) treeNode;
					ideContext.setActiveDetails(folderNode.getProject(), folderNode.getFolder());
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
			
			@Override
			public void projectStateChanged(Project project)
			{
				reloadProjectNode(project);
			}

			@Override
			public void fileSaved(File file)
			{
				FileTreeNode fileNode = getFileNode(file);
				
				if(fileNode == null)
				{
					return;
				}
				
				checkFile(fileNode);
			}
			
			@Override
			public void activeFileChanged(File file, Object source)
			{
				if(source == ProjectExplorer.this)
				{
					return;
				}
				
				setActiveFile(file);
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
		
		FileTreeNode node = getFileNode(file);
		
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
	
	public FileTreeNode getFileNode(File file)
	{
		List<ProjectTreeNode> nodes = projectTreeModel.getProjectNodes();
		
		if(nodes == null || nodes.isEmpty())
		{
			return null;
		}
		
		FileTreeNode fileNode = null;
		
		for(ProjectTreeNode projNode : nodes)
		{
			fileNode = projNode.getFileNode(file);
			
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
		
		filePopup = uiLayout.getPopupMenu("filePopup").toPopupMenu(actionCollection);
		folderPopup = uiLayout.getPopupMenu("folderPopup").toPopupMenu(actionCollection);
		projectPopup = uiLayout.getPopupMenu("projectPopup").toPopupMenu(actionCollection);
		projectExplorerPopup = uiLayout.getPopupMenu("projectExplorerPopup").toPopupMenu(actionCollection);
		testSuitePopup = uiLayout.getPopupMenu("testStuitePopup").toPopupMenu(actionCollection);
		
		testFolderPopup = uiLayout.getPopupMenu("testFolderPopup").toPopupMenu(actionCollection);
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
	
	public File getSelectedFile()
	{
		TreePath selectedPath = tree.getSelectionPath();
		
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
			projectExplorerPopup.show(this, e.getX(), e.getY());
			return;
		}

		selectRow(clickedRow, e.isControlDown() || e.isShiftDown());
		
		//find the selected file
		List<File> selectedFiles = getSelectedFiles();

		TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
		Object clickedItem = treePath.getLastPathComponent();
		
		activeTreeNode = (BaseTreeNode) clickedItem;
		
		if(clickedItem instanceof ProjectTreeNode)
		{
			ProjectTreeNode projTreeNode = (ProjectTreeNode) clickedItem;
			ideContext.setActiveDetails(projTreeNode.getProject(), projTreeNode.getFolder(), selectedFiles);
			
			projectPopup.show(tree, e.getX(), e.getY());
		}
		else if(clickedItem instanceof TestSuiteFolderTreeNode) 
		{
			TestSuiteFolderTreeNode testSuiteFolderTreeNode = (TestSuiteFolderTreeNode) clickedItem;
			ideContext.setActiveDetails(testSuiteFolderTreeNode.getProject(), testSuiteFolderTreeNode.getFolder(), selectedFiles);
			
			testSuitePopup.show(tree, e.getX(), e.getY());
		}
		else if(clickedItem instanceof TestFolderTreeNode) 
		{
			TestFolderTreeNode testFolderTreeNode = (TestFolderTreeNode) clickedItem;
			ideContext.setActiveDetails(testFolderTreeNode.getProject(), testFolderTreeNode.getFolder(), selectedFiles);
			
			testFolderPopup.show(tree, e.getX(), e.getY());
		}
		else if(clickedItem instanceof FileTreeNode)
		{
			FileTreeNode fileTreeNode = (FileTreeNode) clickedItem;
			ideContext.setActiveDetails(fileTreeNode.getProject(), fileTreeNode.getFile(), selectedFiles);
			
			filePopup.show(tree, e.getX(), e.getY());
		}
		else if(clickedItem instanceof FolderTreeNode)
		{
			FolderTreeNode folderTreeNode = (FolderTreeNode) clickedItem;
			ideContext.setActiveDetails(folderTreeNode.getProject(), folderTreeNode.getFolder(), selectedFiles);
	
			folderPopup.show(tree, e.getX(), e.getY());
		}
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
			
			ideContext.setActiveDetails(fileTreeNode.getProject(), fileTreeNode.getFile());
			actionCollection.invokeAction("openFile");
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
	
	public synchronized void reloadFolders(Set<File> folders)
	{
		logger.debug("Reloading folder in project tree: {}", folders);
		
		List<FolderTreeNode> reloadedNodes = new ArrayList<>();
		
		for(ProjectTreeNode projNode : projectTreeModel.getProjectNodes())
		{
			projNode.reloadFolders(folders, reloadedNodes);
		}
		
		for(FolderTreeNode node : reloadedNodes)
		{
			EventQueue.invokeLater(() -> {
				projectTreeModel.reload(node);
			});
		}
		
		loadFilesToIndex();
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
		
		//ignore files which are outside test suite folders
		if(!fileNode.getProject().isTestSuiteFolderFile(file))
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
				
				fileNode.setErrored(collector.getErrorCount() > 0);
				fileNode.setWarned(collector.getWarningCount() > 0);
			} catch(Exception ex)
			{
				logger.error("An error occurred while loading file: {}", file.getPath(), ex);
			}
		}
	}
	
	public ProjectsTreeModel getProjectTreeModel()
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