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
package com.yukthitech.prism.views.debug;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.debug.common.ClientMssgDropToFrame;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused.StackElement;
import com.yukthitech.prism.IViewPanel;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.actions.FileActions;
import com.yukthitech.prism.editor.FileEditor;
import com.yukthitech.prism.editor.FileEditorTabbedPane;
import com.yukthitech.prism.events.IdeStartedEvent;
import com.yukthitech.prism.exeenv.EnvironmentActivationEvent;
import com.yukthitech.prism.exeenv.ExecutionEnvironment;
import com.yukthitech.prism.exeenv.ExecutionEnvironmentManager;
import com.yukthitech.prism.exeenv.debug.DebugExecutionPausedEvent;
import com.yukthitech.prism.exeenv.debug.DebugExecutionReleasedEvent;
import com.yukthitech.prism.exeenv.debug.DebugPointManager;
import com.yukthitech.prism.exeenv.debug.DebugPointsChangedEvent;
import com.yukthitech.prism.exeenv.debug.DebugStepsExecutedEvent;
import com.yukthitech.prism.exeenv.debug.IdeDebugPoint;
import com.yukthitech.prism.layout.UiIdElementsManager;
import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.proj.ProjectManager;
import com.yukthitech.prism.services.IdeEventHandler;
import com.yukthitech.prism.views.debug.StackTraceTreeModel.BaseNode;
import com.yukthitech.prism.views.debug.StackTraceTreeModel.StackElementNode;
import com.yukthitech.prism.views.debug.StackTraceTreeModel.ThreadNode;
import com.yukthitech.swing.IconButton;
import com.yukthitech.swing.list.SimpleListCellRenderer;
import com.yukthitech.swing.tree.BasicTreeNodeRenderer;

@Component
public class DebugPanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;
	
	private static ImageIcon DEBUG_POINT_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/debug-point.svg", 12);
	
	private static ImageIcon DEBUG_POINT_W_CONDITION_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/debug-point-w-cond.svg", 12);
	
	private static ImageIcon REMOVE_SELECTED_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/clear.svg", 16);
	private static ImageIcon REMOVE_ALL_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/clearAll.svg", 16);
	
	private static class FileLocation
	{
		private Project project;
		
		private File file;
		
		private String executionId;
		
		public FileLocation(Project project, File file, String executionId)
		{
			this.project = project;
			this.file = file;
			this.executionId = executionId;
		}
	}
	
	private final JTabbedPane primTabPane = new JTabbedPane(JTabbedPane.TOP);
	private final JTabbedPane ctxTabPane = new JTabbedPane(JTabbedPane.TOP);
	private final JPanel stackTraceTab = new JPanel();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel debugTab = new JPanel();
	private final JPanel panel_2 = new JPanel();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	
	@Autowired
	private ContextAttributesPanel contextAttributesPanel;
	
	private final DebugSandboxPanel sandboxPanel = new DebugSandboxPanel();
	
	private final JTree stackTraceTree = new JTree();
	private StackTraceTreeModel stackTraceTreeModel = new StackTraceTreeModel();
	
	private final JList<IdeDebugPoint> debugPointsLst = new JList<>();
	private SimpleListCellRenderer<IdeDebugPoint> debugPointsListRenderer;
	private DefaultListModel<IdeDebugPoint> debugPointModel = new DefaultListModel<>();

	private final IconButton removeSelPointButton = new IconButton();
	private final IconButton removeAllPointsBut = new IconButton();

	@Autowired
	private ExecutionEnvironmentManager executionEnvironmentManager;
	
	@Autowired
	private DebugPointManager debugPointManager;
	
	@Autowired
	private FileActions fileAction;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;

	private JTabbedPane parentTabbedPane;
	
	private FileLocation previousDebugHighlight;
	
	/**
	 * Used to inform event handler to ignore event
	 * when it is generated by internal code.
	 */
	private boolean ignoreTreeSelection = false;
	
	private JPopupMenu stackTracePopupMenu = new JPopupMenu();
	
	private JMenuItem dropToFrameMenuItem = new JMenuItem("Drop To Frame");

	/**
	 * Create the panel.
	 */
	@PostConstruct
	private void init()
	{
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.15);
		add(splitPane, BorderLayout.CENTER);
		
		splitPane.setLeftComponent(primTabPane);
		
		stackTracePopupMenu.add(dropToFrameMenuItem);
		
		primTabPane.addTab("Stack Trace", null, stackTraceTab, null);
		stackTraceTab.setLayout(new BorderLayout(0, 0));
		stackTraceTab.add(scrollPane, BorderLayout.CENTER);
		
		BasicTreeNodeRenderer renderer = new BasicTreeNodeRenderer()
				.setTypeIcon(ThreadNode.class, IdeUtils.loadIcon("/ui/icons/debug-thread.svg", 16))
				.setTypeIcon(StackElementNode.class, IdeUtils.loadIcon("/ui/icons/stack-trace-element.svg", 16));
		
		stackTraceTree.setCellRenderer(renderer);
		scrollPane.setViewportView(stackTraceTree);
		
		stackTraceTree.addTreeSelectionListener(this::onStackTraceSelectionChange);
		stackTraceTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				onStackTraceMouseClick(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					handleStackTracePopup(e);
					return;
				}
			}
		});
		
		dropToFrameMenuItem.addActionListener(this::onDropToFrame);
		
		primTabPane.addTab("Debug Points", null, debugTab, null);
		debugTab.setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setVgap(2);
		flowLayout.setHgap(2);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		debugTab.add(panel_2, BorderLayout.NORTH);
		removeSelPointButton.setMargin(new Insets(1, 1, 1, 1));
		removeSelPointButton.setIcon(REMOVE_SELECTED_ICON);
		removeSelPointButton.setToolTipText("Remove Selected Debug Points");
		removeSelPointButton.addActionListener(this::removeSelectedDebugPoints);
		
		panel_2.add(removeSelPointButton);
		removeAllPointsBut.setIcon(REMOVE_ALL_ICON);
		removeAllPointsBut.setMargin(new Insets(1, 1, 1, 1));
		removeAllPointsBut.setToolTipText("Remove All Debug Points");
		removeAllPointsBut.addActionListener(this::removeAllDebugPoints);
		
		panel_2.add(removeAllPointsBut);
		
		debugTab.add(scrollPane_1, BorderLayout.CENTER);
		
		debugPointsLst.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(e.getClickCount() > 1)
				{
					openDebugPoint();
				}
			}
		});
		
		scrollPane_1.setViewportView(debugPointsLst);
		

		debugPointsListRenderer = new SimpleListCellRenderer<>(
				point -> StringUtils.isBlank(point.getCondition()) ? DEBUG_POINT_ICON : DEBUG_POINT_W_CONDITION_ICON, 
				point -> point.getFile().getName() + ":" + (point.getLineNo() + 1)
			);
		debugPointsLst.setCellRenderer(debugPointsListRenderer);
		debugPointsLst.setModel(debugPointModel);
		
		splitPane.setRightComponent(ctxTabPane);
		
		ctxTabPane.addTab("Context Attributes", null, contextAttributesPanel, null);
		contextAttributesPanel.setDebugPanel(this);
		
		ctxTabPane.addTab("Sandbox", null, sandboxPanel, null);
		sandboxPanel.setDebugPanel(this);
		
		reloadTree();
	}
	
	private void reloadTree()
	{
		stackTraceTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		stackTraceTree.setRootVisible(false);
		stackTraceTree.setShowsRootHandles(true);
		stackTraceTree.setModel(stackTraceTreeModel);
	}

	@Override
	public void setParent(JTabbedPane parentTabPane)
	{
		this.parentTabbedPane = parentTabPane;
	}
	
	private void onStackTraceSelectionChange(TreeSelectionEvent e)
	{
		if(ignoreTreeSelection)
		{
			return;
		}
		
		ExecutionEnvironment activeEnv = executionEnvironmentManager.getActiveEnvironment();
		
		TreePath selectedPath = stackTraceTree.getSelectionPath();
		
		if(selectedPath == null)
		{
			return;
		}
		
		BaseNode baseNode = (BaseNode) selectedPath.getLastPathComponent();
		String executionId = baseNode.getExecutionId();
		
		if(activeEnv != null && !executionId.equals(activeEnv.getActiveThreadId()))
		{
			activeEnv.setActiveThreadId(executionId);
		}
		
		updateActiveThread(activeEnv);
	}
	
	private void onDropToFrame(ActionEvent e)
	{
		ExecutionEnvironment activeEnv = executionEnvironmentManager.getActiveEnvironment();
		
		if(activeEnv == null)
		{
			return;
		}
		
		TreePath treePath = stackTraceTree.getSelectionPath();
		
		if(treePath == null)
		{
			return;
		}
		
		Object clickedItem = treePath.getLastPathComponent();
		
		if(!(clickedItem instanceof StackElementNode))
		{
			return;
		}
		
		StackElementNode stackElementNode = (StackElementNode) clickedItem;
		activeEnv.sendDataToServer(new ClientMssgDropToFrame(stackElementNode.getExecutionId(), stackElementNode.getElement().getStackElementId()));
	}
	
	private void handleStackTracePopup(MouseEvent e)
	{
		int clickedRow = stackTraceTree.getRowForLocation(e.getX(), e.getY());
		
		if(clickedRow < 0)
		{
			return;
		}
		
		TreePath treePath = stackTraceTree.getPathForLocation(e.getX(), e.getY());
		Object clickedItem = treePath.getLastPathComponent();

		if(!(clickedItem instanceof StackElementNode))
		{
			return;
		}
		
		StackElementNode stackElementNode = (StackElementNode) clickedItem;
		String stackElemId = stackElementNode.getElement().getStackElementId();
		
		dropToFrameMenuItem.setEnabled(StringUtils.isNotBlank(stackElemId));
		
		stackTraceTree.setSelectionPath(treePath);
		
		stackTracePopupMenu.show(stackTraceTree, e.getX(), e.getY());
	}
	
	private void onStackTraceMouseClick(MouseEvent e)
	{
		if(e.getClickCount() <= 1)
		{
			return;
		}
		
		TreePath treePath = stackTraceTree.getPathForLocation(e.getX(), e.getY());
		
		if(treePath == null)
		{
			return;
		}
		
		Object clickedItem = treePath.getLastPathComponent();
		
		if(!(clickedItem instanceof StackElementNode))
		{
			return;
		}
		
		StackElementNode stackElemNode = (StackElementNode) clickedItem;
		StackElement stackElem = stackElemNode.getElement();
		
		ExecutionEnvironment activeEnv = executionEnvironmentManager.getActiveEnvironment();
		fileAction.gotoFilePath(activeEnv.getProject(), stackElem.getFile(), stackElem.getLineNumber());
	}
	
	@IdeEventHandler
	private void onActiveEnvChange(EnvironmentActivationEvent event)
	{
		ExecutionEnvironment activeEnv = event.getNewActiveEnvironment();
		
		//if new active env is not debug env then set env to null
		//  so that all panels gets cleaned
		if(activeEnv != null && (!activeEnv.isDebugEnv() || activeEnv.isTerminated()))
		{
			activeEnv = null;
		}
		
		stackTraceTreeModel.setEnvironment(activeEnv);
		updateActiveThread(activeEnv);
	}
	
	@IdeEventHandler
	private void onExecutionPause(DebugExecutionPausedEvent event)
	{
		ExecutionEnvironment activeEnv = executionEnvironmentManager.getActiveEnvironment();
		
		//consider only active env events
		if(activeEnv != event.getExecutionEnvironment() || !activeEnv.isDebugEnv())
		{
			return;
		}
		
		stackTraceTreeModel.addOrUpdate(event.getPausedMssg());
		
		if(event.getPausedMssg().getExecutionId().equals(activeEnv.getActiveThreadId()))
		{
			updateActiveThread(activeEnv);
		}
	}
	
	@IdeEventHandler
	private void onExecutionRelease(DebugExecutionReleasedEvent event)
	{
		ExecutionEnvironment activeEnv = executionEnvironmentManager.getActiveEnvironment();

		//consider only active env events
		if(activeEnv != event.getExecutionEnvironment() || !activeEnv.isDebugEnv())
		{
			return;
		}

		stackTraceTreeModel.removeStackTrace(event.getPausedMssg().getExecutionId());

		//post release active env should be considered as null
		updateActiveThread(activeEnv.getActiveThreadId() == null ? null : activeEnv);
	}
	
	@IdeEventHandler
	private void onStepExecution(DebugStepsExecutedEvent event)
	{
		ExecutionEnvironment activeEnv = executionEnvironmentManager.getActiveEnvironment();
		
		if(activeEnv != event.getExecutionEnvironment() || !activeEnv.isDebugEnv())
		{
			return;
		}

		if(event.getStepExecuted().getExecutionId().equals(activeEnv.getActiveThreadId()) && event.getStepExecuted().getContextAttr() != null)
		{
			updateActiveThread(activeEnv);
		}
	}

	@IdeEventHandler
	private void onDebugPointChange(DebugPointsChangedEvent event)
	{
		updateDebugPoints();
	}
	
	@IdeEventHandler
	private void appStarted(IdeStartedEvent event)
	{
		updateDebugPoints();
	}
	
	private void selectStackTracePath(TreePath path)
	{
		ignoreTreeSelection = true;
		
		try
		{
			stackTraceTree.setSelectionPath(path);
		}finally
		{
			ignoreTreeSelection = false;
		}
	}

	private void updateActiveThread(ExecutionEnvironment activeEnv)
	{
		ServerMssgExecutionPaused threadDet = activeEnv == null ? null : activeEnv.getActiveThreadDetails();

		if(threadDet != null)
		{
			TreePath selectedPath = stackTraceTree.getSelectionPath();
			BaseNode activeNode = selectedPath != null ? (BaseNode) selectedPath.getLastPathComponent() : null;
			
			if(activeNode == null || !activeNode.getExecutionId().equals(threadDet.getExecutionId()))
			{
				TreePath treePath = stackTraceTreeModel.getPath(threadDet.getExecutionId());
				
				IdeUtils.executeConsolidatedJob("debugPanel.stackTraceSel", () -> 
				{
					stackTraceTree.expandPath(treePath);
					selectStackTracePath(treePath);
				}, 200);
				
				activateTab();
			}

			if(previousDebugHighlight == null || !previousDebugHighlight.executionId.equals(threadDet.getExecutionId()))
			{
				activateTab();
				
				highlightDebugPoint(activeEnv, threadDet);
			}
		}
		//if no active env is found or active thread is not found
		else
		{
			if(previousDebugHighlight != null)
			{
				FileEditor editor = fileEditorTabbedPane.getOpenFile(previousDebugHighlight.project, previousDebugHighlight.file);
				
				if(editor != null)
				{
					editor.clearHighlightDebugLine();
				}
				
				previousDebugHighlight = null;
			}
		}

		//Get context params and sort them
		Map<String, byte[]> contextAttr = threadDet == null ? null : activeEnv.getContextAttributes();
		contextAttr = (contextAttr == null) ? Collections.emptyMap() : new TreeMap<>(contextAttr);
		
		//get param map and sort them
		Map<String, byte[]> paramMap = threadDet == null ? null : threadDet.getParams();
		paramMap = (paramMap == null) ? Collections.emptyMap() : new TreeMap<>(paramMap);
		
		//build final attributes
		Map<String, byte[]> finalAttr = new LinkedHashMap<>();
		paramMap.forEach((key, val) -> 
		{
			finalAttr.put("(param) " + key, val);
		});
		
		finalAttr.putAll(contextAttr);

		contextAttributesPanel.setContextAttributes(finalAttr);
		sandboxPanel.setActiveEnvironment(activeEnv, threadDet == null ? null : threadDet.getExecutionId());
	}
	
	private void updateDebugPoints()
	{
		List<IdeDebugPoint> points = debugPointManager.getDebugPoints();
		
		if(!points.isEmpty())
		{
			points = new ArrayList<>(points);
			Collections.sort(points, (p1, p2) -> 
			{
				int diff = p1.getFile().getName().compareTo(p2.getFile().getName());
				
				if(diff != 0)
				{
					return diff;
				}
				
				diff = p1.getLineNo() - p2.getLineNo();
				
				if(diff != 0)
				{
					return diff;
				}
				
				return 1;
			});
		}
		
		debugPointModel.clear();
		points.forEach(point -> debugPointModel.addElement(point));
	}
	
	protected void activateTab()
	{
		if(parentTabbedPane != null)
		{
			parentTabbedPane.setSelectedComponent(this);
		}
	}
	
	private void openDebugPoint()
	{
		int idx = debugPointsLst.getSelectedIndex();
		
		if(idx < 0)
		{
			return;
		}
		
		IdeDebugPoint debugPoint = debugPointModel.get(idx);
		Project project = projectManager.getProject(debugPoint.getProject());
		
		fileAction.gotoFilePath(project, debugPoint.getFile().getPath(), debugPoint.getLineNo() + 1);
	}
	
	private void removeSelectedDebugPoints(ActionEvent e)
	{
		int indexes[] = debugPointsLst.getSelectedIndices();
		
		if(indexes.length == 0)
		{
			return;
		}
		
		List<IdeDebugPoint> points = new ArrayList<>(indexes.length);
		
		for(int idx : indexes)
		{
			points.add(debugPointModel.get(idx));
		}
		
		debugPointManager.removeDebugPoints(points, this);
	}
	
	private void removeAllDebugPoints(ActionEvent e)
	{
		debugPointManager.removeAllDebugPoints(this);
	}
	
	public static void controlDebugActions(ExecutionEnvironment activeEnv)
	{
		if(activeEnv == null || activeEnv.isTerminated() || !activeEnv.isDebugEnv())
		{
			UiIdElementsManager.setEnableFlagByGroups(false, "debugGroup", "debugErrGroup");
			return;
		}
		
		ServerMssgExecutionPaused currentPauseMssg = activeEnv.getActiveThreadDetails();
		boolean errorPoint = currentPauseMssg != null && currentPauseMssg.isErrorPoint();
		
		if(errorPoint)
		{
			UiIdElementsManager.setEnableFlagByGroup("debugGroup", false);
			UiIdElementsManager.setEnableFlagByGroup("debugErrGroup", true);
		}
		else
		{
			UiIdElementsManager.setEnableFlagByGroup("debugErrGroup", false);
			UiIdElementsManager.setEnableFlagByGroup("debugGroup", true);
		}
	}
	
	private synchronized void highlightDebugPoint(ExecutionEnvironment activeEnv, ServerMssgExecutionPaused threadDet)
	{
		if(previousDebugHighlight != null)
		{
			FileEditor editor = fileEditorTabbedPane.getOpenFile(previousDebugHighlight.project, previousDebugHighlight.file);
			
			if(editor != null)
			{
				editor.clearHighlightDebugLine();
			}
			
			previousDebugHighlight = null;
		}
		
		File file = new File(threadDet.getDebugFilePath());
		int lineNo = threadDet.getLineNumber() + 1; 
		
		FileEditor editor = fileEditorTabbedPane.openProjectFile(activeEnv.getProject(), file);
		editor.highlightDebugLine(lineNo, threadDet.isErrorPoint());
		
		previousDebugHighlight = new FileLocation(activeEnv.getProject(), file, threadDet.getExecutionId());
		controlDebugActions(activeEnv);
	}
	
	public void openSandboxTab(String prefix, String name)
	{
		ctxTabPane.setSelectedComponent(sandboxPanel);
		sandboxPanel.setExpression(prefix + ": " + name);
	}
}
