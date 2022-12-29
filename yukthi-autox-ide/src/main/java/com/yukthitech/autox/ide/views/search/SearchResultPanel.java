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
package com.yukthitech.autox.ide.views.search;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.IViewPanel;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.actions.FileActions;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.proj.ProjectManager;
import com.yukthitech.autox.ide.search.FileSearchService;
import com.yukthitech.autox.ide.search.ISearchOperation;
import com.yukthitech.autox.ide.search.SearchResult;
import com.yukthitech.autox.ide.views.search.SearchResultTreeModel.FolderNode;
import com.yukthitech.autox.ide.views.search.SearchResultTreeModel.ProjectNode;
import com.yukthitech.autox.ide.views.search.SearchResultTreeModel.SearchResultNode;
import com.yukthitech.swing.IconButton;
import com.yukthitech.swing.tree.BasicTreeNodeRenderer;

@Component
public class SearchResultPanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;
	
	private static ImageIcon REPEAT_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/repeat.svg", 18);
	private static ImageIcon REPLACE_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/replace.svg", 18);
	private static ImageIcon REMOVE_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/clear.svg", 18);
	
	private static ImageIcon EXPAND_ALL_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/expand-all.svg", 18);
	private static ImageIcon COLLAPSE_ALL_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/collapse-all.svg", 18);
	
	private final JScrollPane resutsScrollPane = new JScrollPane();
	private final JTree searchResTree = new JTree();
	private SearchResultTreeModel searchResultTreeModel = new SearchResultTreeModel();
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private FileActions fileActions;
	
	@Autowired
	private FileSearchService fileSearchService;
	
	private final JPanel panel = new JPanel();

	private final IconButton repeatBut = new IconButton(REPEAT_ICON);
	private final IconButton replaceBut = new IconButton(REPLACE_ICON);
	private final IconButton removeSelectedBut = new IconButton(REMOVE_ICON);
	private final JSeparator separator_1_1 = new JSeparator();

	private final IconButton expandAllBut = new IconButton(EXPAND_ALL_ICON);
	private final IconButton collapseAllBut = new IconButton(COLLAPSE_ALL_ICON);
	private final JSeparator separator = new JSeparator();

	private final JLabel noMatchesLbl = new JLabel("            No matches found !!!");
	private final JPanel resultsContainerPnl = new JPanel();
	
	private CardLayout resCardLayout = new CardLayout(0, 0);
	
	private JTabbedPane parentTabbedPane;
	
	private ISearchOperation currentOperation;

	/**
	 * Create the panel.
	 */
	public SearchResultPanel()
	{
		setLayout(new BorderLayout(0, 0));
		
		resutsScrollPane.setViewportView(searchResTree);

		searchResTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				onMouseClick(e);
			}
		});
		
		BasicTreeNodeRenderer renderer = new BasicTreeNodeRenderer()
				.setTypeIcon(ProjectNode.class, IdeUtils.loadIcon("/ui/icons/project.svg", 16))
				.setTypeIcon(FolderNode.class, IdeUtils.loadIcon("/ui/icons/folder.svg", 16))
				.setTypeIcon(SearchResultNode.class, IdeUtils.loadIcon("/ui/icons/search-result.svg", 16));
		
		searchResTree.setCellRenderer(renderer);
		searchResTree.setRootVisible(false);
		searchResTree.setShowsRootHandles(true);
		searchResTree.setModel(searchResultTreeModel);
		searchResTree.addTreeSelectionListener(this::onTreeItemSelection);
		
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(2);
		flowLayout.setHgap(2);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		
		add(panel, BorderLayout.NORTH);
		repeatBut.setToolTipText("Repeat Search");
		repeatBut.addActionListener(this::onRepeat);
		
		panel.add(repeatBut);
		removeSelectedBut.setToolTipText("Remove Selected");
		removeSelectedBut.addActionListener(this::onRemove);
		
		panel.add(removeSelectedBut);
		replaceBut.setToolTipText("Replace Matches");
		replaceBut.addActionListener(this::onReplace);
		
		panel.add(replaceBut);
		separator_1_1.setPreferredSize(new Dimension(2, 20));
		separator_1_1.setOrientation(SwingConstants.VERTICAL);
		
		panel.add(separator_1_1);
		expandAllBut.setToolTipText("Expand All");
		expandAllBut.addActionListener(this::onExpandAll);
		
		panel.add(expandAllBut);
		collapseAllBut.setToolTipText("Collapse All");
		collapseAllBut.addActionListener(this::onCollapseAll);
		
		panel.add(collapseAllBut);
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 20));
		
		
		add(resultsContainerPnl, BorderLayout.CENTER);
		resultsContainerPnl.setLayout(resCardLayout);

		noMatchesLbl.setFont(new Font("Tahoma", Font.BOLD, 13));
		resultsContainerPnl.add("resutsScrollPane", resutsScrollPane);
		resultsContainerPnl.add("noMatchesLbl", noMatchesLbl);
	}

	@Override
	public void setParent(JTabbedPane parentTabPane)
	{
		this.parentTabbedPane = parentTabPane;
	}
	
	public void setSearchResults(ISearchOperation query, List<SearchResult> results)
	{
		this.currentOperation = query;
		this.replaceBut.setEnabled(query.isReplaceOperation());
		
		if(CollectionUtils.isEmpty(results))
		{
			resCardLayout.show(resultsContainerPnl, "noMatchesLbl");
		}
		else
		{
			resCardLayout.show(resultsContainerPnl, "resutsScrollPane");
		}
		
		searchResultTreeModel.setResults(results, projectManager);
		parentTabbedPane.setSelectedComponent(this);
		
		expandFirstFolder();
	}

	private void onMouseClick(MouseEvent e)
	{
		if(e.getClickCount() <= 1)
		{
			return;
		}
		
		TreePath treePath = searchResTree.getPathForLocation(e.getX(), e.getY());
		
		if(treePath == null)
		{
			return;
		}
		
		Object clickedItem = treePath.getLastPathComponent();
		
		if(!(clickedItem instanceof SearchResultNode))
		{
			return;
		}
		
		SearchResultNode searchResNode = (SearchResultNode) clickedItem;
		SearchResult res = searchResNode.getResult();

		Project project = projectManager.getProject(searchResNode.getProjectName());
		fileActions.gotoFilePath(project, res.getFile().getPath(), res.getLineNo());
	}
	
	private void onTreeItemSelection(TreeSelectionEvent e)
	{
		int count = searchResTree.getSelectionCount();
		removeSelectedBut.setEnabled(count > 0);
	}
	
	private void onRepeat(ActionEvent e)
	{
		fileSearchService.repeatSearch(currentOperation);
	}

	private void onReplace(ActionEvent e)
	{
		List<SearchResult> resLst = searchResultTreeModel.fetchResults();
		
		if(resLst.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "No matches found for replacement");
			return;
		}
		
		searchResultTreeModel.reset();
		fileSearchService.replaceMatches(currentOperation, resLst);
	}

	private void onRemove(ActionEvent e)
	{
		TreePath treePaths[] = searchResTree.getSelectionPaths();
		
		if(treePaths == null || treePaths.length == 0)
		{
			return;
		}
		
		searchResultTreeModel.removePaths(treePaths);
	}

	private void onCollapseAll(ActionEvent e)
	{
		IdeUtils.execute(() -> 
		{
			int rowCount = searchResTree.getRowCount() - 1;
			
			while(rowCount >= 0)
			{
				searchResTree.collapseRow(rowCount);
				rowCount--;
			}
			
		}, 1);
	}

	private void onExpandAll(ActionEvent e)
	{
		IdeUtils.execute(() -> 
		{
			int rowCount = searchResTree.getRowCount();
			int idx = 0;
			
			while(idx < rowCount)
			{
				searchResTree.expandRow(idx);
				
				rowCount = searchResTree.getRowCount();
				idx++;
			}
		}, 1);
	}
	
	private void expandFirstFolder()
	{
		IdeUtils.execute(() -> 
		{
			int rowCount = searchResTree.getRowCount();
			int idx = 0;
			
			while(idx < rowCount)
			{
				searchResTree.expandRow(idx);
				TreePath path = searchResTree.getPathForRow(idx);
				
				if(path.getLastPathComponent() instanceof FolderNode)
				{
					break;
				}
				
				rowCount = searchResTree.getRowCount();
				idx++;
			}
		}, 1);
	}
}
