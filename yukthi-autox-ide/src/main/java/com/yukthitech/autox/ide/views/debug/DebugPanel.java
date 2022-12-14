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
package com.yukthitech.autox.ide.views.debug;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.ide.IViewPanel;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.exeenv.EnvironmentActivationEvent;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironmentManager;
import com.yukthitech.autox.ide.exeenv.debug.DebugExecutionPausedEvent;
import com.yukthitech.autox.ide.exeenv.debug.DebugExecutionReleasedEvent;
import com.yukthitech.autox.ide.exeenv.debug.DebugPointManager;
import com.yukthitech.autox.ide.exeenv.debug.DebugPointsChangedEvent;
import com.yukthitech.autox.ide.exeenv.debug.IdeDebugPoint;
import com.yukthitech.autox.ide.services.IdeEventHandler;
import com.yukthitech.autox.ide.services.IdeStartedEvent;
import com.yukthitech.swing.IconButton;
import com.yukthitech.swing.list.SimpleListCellRenderer;

@Component
public class DebugPanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;
	
	private static ImageIcon DEBUG_POINT_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/debug-point.svg", 12);
	
	private static ImageIcon REMOVE_SELECTED_ICON = IdeUtils.loadIcon("/ui/icons/clear.svg", 16);
	private static ImageIcon REMOVE_ALL_ICON = IdeUtils.loadIcon("/ui/icons/clearAll.svg", 16);
	
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
	private final JPanel stackTraceTab = new JPanel();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel debugTab = new JPanel();
	private final JPanel panel_2 = new JPanel();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final ContextAttributesPanel contextAttributesPanel = new ContextAttributesPanel();
	private final DebugSandbox sandboxTab = new DebugSandbox();
	
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

	/**
	 * Create the panel.
	 */
	public DebugPanel()
	{
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.15);
		add(splitPane, BorderLayout.CENTER);
		
		splitPane.setLeftComponent(tabbedPane);
		
		tabbedPane.addTab("Stack Trace", null, stackTraceTab, null);
		stackTraceTab.setLayout(new BorderLayout(0, 0));
		
		stackTraceTab.add(scrollPane, BorderLayout.CENTER);
		
		stackTraceTree.setModel(stackTraceTreeModel);
		//stackTraceTree.setRootVisible(false);
		stackTraceTree.setShowsRootHandles(true);
		stackTraceTree.setCellRenderer(new StackTraceNodeRenderer());
		
		scrollPane.setViewportView(stackTraceTree);
		
		tabbedPane.addTab("Debug Points", null, debugTab, null);
		debugTab.setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setVgap(2);
		flowLayout.setHgap(2);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		debugTab.add(panel_2, BorderLayout.NORTH);
		removeSelPointButton.setIcon(REMOVE_SELECTED_ICON);
		removeSelPointButton.setToolTipText("Remove Selected Debug Points");
		removeSelPointButton.addActionListener(this::removeSelectedDebugPoints);
		
		panel_2.add(removeSelPointButton);
		removeAllPointsBut.setIcon(REMOVE_ALL_ICON);
		removeAllPointsBut.setToolTipText("Remove All Debug Points");
		removeAllPointsBut.addActionListener(this::removeAllDebugPoints);
		
		panel_2.add(removeAllPointsBut);
		
		debugTab.add(scrollPane_1, BorderLayout.CENTER);
		debugPointsLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollPane_1.setViewportView(debugPointsLst);

		debugPointsListRenderer = new SimpleListCellRenderer<>(
				point -> DEBUG_POINT_ICON, 
				point -> point.getFile().getName() + ":" + (point.getLineNo() + 1)
			);
		debugPointsLst.setCellRenderer(debugPointsListRenderer);
		debugPointsLst.setModel(debugPointModel);
		
		splitPane.setRightComponent(tabbedPane_1);
		
		tabbedPane_1.addTab("Context Attributes", null, contextAttributesPanel, null);
		
		tabbedPane_1.addTab("Sandbox", null, sandboxTab, null);
	}

	@Override
	public void setParent(JTabbedPane parentTabPane)
	{
	}
	
	@IdeEventHandler
	private void onActiveEnvChange(EnvironmentActivationEvent event)
	{
		ExecutionEnvironment activeEnv = event.getNewActiveEnvironment();
		
		//if new active env is not debug env then set env to null
		//  so that all panels gets cleaned
		if(activeEnv != null && !activeEnv.isDebugEnv())
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
		updateActiveThread(activeEnv);
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

	private void updateActiveThread(ExecutionEnvironment activeEnv)
	{
		ServerMssgExecutionPaused threadDet = activeEnv == null ? null : activeEnv.getActiveThreadDetails();
		Map<String, byte[]> contextAttr = threadDet == null ? null : threadDet.getContextAttr();
		contextAttr = (contextAttr == null) ? Collections.emptyMap() : new TreeMap<>(contextAttr);

		contextAttributesPanel.setContextAttributes(contextAttr);
	}
	
	private void updateDebugPoints()
	{
		List<IdeDebugPoint> points = debugPointManager.getDebugPoints();
		
		debugPointModel.clear();
		points.forEach(point -> debugPointModel.addElement(point));
	}
	
	private void removeSelectedDebugPoints(ActionEvent e)
	{
		
	}
	
	private void removeAllDebugPoints(ActionEvent e)
	{
		
	}
}
