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
package com.yukthitech.prism.projpropdialog;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;

import org.springframework.stereotype.Component;

import com.yukthitech.prism.IdeFileUtils;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.model.Project;
import com.yukthitech.swing.DirectoryChooserDialog;
import com.yukthitech.swing.DirectoryDataProvider;
import com.yukthitech.swing.IconButton;

@Component
public class ProjectSourceFolderPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JList<String> srcFolderLst;
	
	private Project project;
	
	private DirectoryChooserDialog directoryChooserDialog = new DirectoryChooserDialog();
	
	private Map<String, File> sourceFolders = new TreeMap<>();
	private Map<String, File> resourceFolders = new TreeMap<>();
	
	private final JPanel tsFolderActionPanel = new JPanel();
	
	private IconButton addSrcBut = new IconButton(IdeUtils.loadIconWithoutBorder("/ui/icons/add.svg", 16));
	private IconButton deleteSrcBut = new IconButton(IdeUtils.loadIconWithoutBorder("/ui/icons/delete.svg", 16));
	
	private final JPanel panel_1 = new JPanel();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final JList<String> resFolderLst = new JList<String>();
	
	private final IconButton addResBut = new IconButton(IdeUtils.loadIconWithoutBorder("/ui/icons/add.svg", 16));
	private final IconButton deleteResBut = new IconButton(IdeUtils.loadIconWithoutBorder("/ui/icons/delete.svg", 16));
	private final JLabel lblNewLabel = new JLabel("Resource Folders:");
	private final JLabel lblNewLabel_1 = new JLabel("Source Folders:");

	/**
	 * Create the panel.
	 */
	public ProjectSourceFolderPanel()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0};
		gridBagLayout.rowHeights = new int[] {0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0};
		setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		GridBagConstraints gbc_tsFolderActionPanel = new GridBagConstraints();
		gbc_tsFolderActionPanel.insets = new Insets(0, 0, 5, 0);
		gbc_tsFolderActionPanel.fill = GridBagConstraints.BOTH;
		gbc_tsFolderActionPanel.gridx = 1;
		gbc_tsFolderActionPanel.gridy = 0;
		FlowLayout flowLayout = (FlowLayout) tsFolderActionPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		flowLayout.setVgap(2);
		flowLayout.setHgap(2);
		panel.add(tsFolderActionPanel, gbc_tsFolderActionPanel);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panel.add(scrollPane, gbc_scrollPane);
		
		srcFolderLst = new JList<String>(new DefaultListModel<String>());
		srcFolderLst.addListSelectionListener(this::sourceFolderSelected);
		srcFolderLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(srcFolderLst);
		addSrcBut.setToolTipText("Add Source Folder");
		
		addSrcBut.addActionListener(this::addSourceFolder);
		tsFolderActionPanel.add(addSrcBut);
		deleteSrcBut.setToolTipText("Delete Source Folder");
		
		
		deleteSrcBut.addActionListener(this::removeSourceFolder);
		tsFolderActionPanel.add(deleteSrcBut);
		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.SOUTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		flowLayout_1.setVgap(2);
		flowLayout_1.setHgap(2);
		panel.add(panel_1, gbc_panel_1);
		
		addResBut.setToolTipText("Add Resource Folder");
		addResBut.addActionListener(this::addResourceFolder);
		
		panel_1.add(addResBut);
		deleteResBut.setToolTipText("Delete Resource Folder");
		deleteResBut.addActionListener(this::removeResourceFolder);
		
		panel_1.add(deleteResBut);
		
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridwidth = 2;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 3;
		panel.add(scrollPane_1, gbc_scrollPane_1);
		resFolderLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resFolderLst.setModel(new DefaultListModel<>());		
		resFolderLst.addListSelectionListener(this::resourceFolderSelected);
		scrollPane_1.setViewportView(resFolderLst);
	}
	
	private void addNewFolders(Map<String, File> sourceFolders, JList<String> listUi)
	{
		File baseFolder = this.project.getBaseFolder();
		List<File> filteredFolders = sourceFolders.values()
			.stream()
			.filter(path -> path.exists())
			.collect(Collectors.toList());
		
		DirectoryDataProvider directoryDataProvider = new DirectoryDataProvider(baseFolder);
		directoryDataProvider.setSelectedFolders(filteredFolders);
		List<File> selectedFolders = directoryChooserDialog.display(directoryDataProvider);
		
		if(selectedFolders == null)
		{
			return;
		}
		
		sourceFolders.clear();
		
		for(File selFolder : selectedFolders)
		{
			String path = IdeFileUtils.getRelativePath(baseFolder, selFolder);
			sourceFolders.put(path, selFolder);
		}

		DefaultListModel<String> sourceFolderModel = (DefaultListModel<String>) listUi.getModel();
		sourceFolderModel.removeAllElements();
		
		sourceFolders.keySet().forEach(path -> sourceFolderModel.addElement(path));
	}
	
	private void addSourceFolder(ActionEvent e)
	{
		addNewFolders(this.sourceFolders, this.srcFolderLst);
	}
	
	private void removeSourceFolder(ActionEvent e)
	{
		int idx = this.srcFolderLst.getSelectedIndex();
		
		if(idx < 0)
		{
			return;
		}
		
		if(this.sourceFolders.size() == 1)
		{
			JOptionPane.showMessageDialog(this, "All source folders cannot be removed. Please add another one before removing current one");
			return;
		}
		
		String item = srcFolderLst.getSelectedValue();
		this.sourceFolders.remove(item);
		
		DefaultListModel<String> listUiModel = (DefaultListModel<String>) this.srcFolderLst.getModel();
		listUiModel.remove(idx);
		
		srcFolderLst.setSelectedIndex(0);
	}
	
	private void sourceFolderSelected(ListSelectionEvent e)
	{
		deleteSrcBut.setEnabled(srcFolderLst.getSelectedIndex() >= 0);
	}
	
	private void addResourceFolder(ActionEvent e)
	{
		addNewFolders(this.resourceFolders, this.resFolderLst);
	}
	
	private void removeResourceFolder(ActionEvent e)
	{
		int idx = this.resFolderLst.getSelectedIndex();
		
		if(idx < 0)
		{
			return;
		}
		
		if(this.resourceFolders.size() == 1)
		{
			JOptionPane.showMessageDialog(this, "All resource folders cannot be removed. Please add another one before removing current one");
			return;
		}

		String item = resFolderLst.getSelectedValue();
		this.resourceFolders.remove(item);
		
		DefaultListModel<String> listUiModel = (DefaultListModel<String>) this.resFolderLst.getModel();
		listUiModel.remove(idx);
		
		resFolderLst.setSelectedIndex(0);
	}

	private void resourceFolderSelected(ListSelectionEvent e)
	{
		deleteResBut.setEnabled(resFolderLst.getSelectedIndex() >= 0);
	}

	private void setFolder(Map<String, File> sourceFolders, JList<String> listUi, Set<String> currentPaths)
	{
		File baseFolder = this.project.getBaseFolder();
		sourceFolders.clear();
		DefaultListModel<String> listUiModel = (DefaultListModel<String>) listUi.getModel();
		
		for(String path : currentPaths)
		{
			try
			{
				sourceFolders.put(path, new File(baseFolder, path).getCanonicalFile());
			} catch(IOException ex)
			{
				throw new IllegalStateException("An error occurred while getting cannoical path of folder: " + path, ex);
			}
		}

		listUiModel.removeAllElements();
		sourceFolders.keySet().forEach(path ->listUiModel.addElement(path));
	}

	public void setProject(Project project)
	{
		this.project = project;
		
		setFolder(this.sourceFolders, this.srcFolderLst, project.getTestSuitesFoldersList());
		setFolder(this.resourceFolders, this.resFolderLst, project.getResourceFoldersList());
	}

	public void applyChanges()
	{
		project.setTestSuiteFoldersList(this.sourceFolders.keySet());
		project.setResourceFoldersList(this.resourceFolders.keySet());
	}
}
