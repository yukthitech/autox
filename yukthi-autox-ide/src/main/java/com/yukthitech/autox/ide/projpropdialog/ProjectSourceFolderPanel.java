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
package com.yukthitech.autox.ide.projpropdialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.swing.DirectoryChooserDialog;
import com.yukthitech.swing.DirectoryDataProvider;

@Component
public class ProjectSourceFolderPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JList<String> sourceFolderListUi;
	
	private Project project;
	
	private DirectoryChooserDialog directoryChooserDialog = new DirectoryChooserDialog();
	
	private LinkedHashMap<String, File> sourceFolders = new LinkedHashMap<>();
	
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
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panel.add(scrollPane, gbc_scrollPane);
		
		sourceFolderListUi = new JList<String>(new DefaultListModel<String>());
		sourceFolderListUi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(sourceFolderListUi);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 0;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JButton addSrcBut = new JButton("Add");
		addSrcBut.addActionListener(this::addSourceFolder);
		GridBagConstraints gbc_addSrcBut = new GridBagConstraints();
		gbc_addSrcBut.fill = GridBagConstraints.HORIZONTAL;
		gbc_addSrcBut.insets = new Insets(0, 0, 5, 0);
		gbc_addSrcBut.gridx = 0;
		gbc_addSrcBut.gridy = 0;
		panel_2.add(addSrcBut, gbc_addSrcBut);
		
		JButton deleteSrcBut = new JButton("Delete");
		deleteSrcBut.addActionListener(this::removeSourceFolder);
		GridBagConstraints gbc_deleteSrcBut = new GridBagConstraints();
		gbc_deleteSrcBut.fill = GridBagConstraints.HORIZONTAL;
		gbc_deleteSrcBut.gridx = 0;
		gbc_deleteSrcBut.gridy = 1;
		panel_2.add(deleteSrcBut, gbc_deleteSrcBut);
	}
	
	public void setProject(Project project)
	{
		this.project = project;
		
		setFolder(this.sourceFolders, this.sourceFolderListUi, project.getTestSuitesFoldersList());
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
	
	private void addNewFolders(Map<String, File> sourceFolders, JList<String> listUi)
	{
		File baseFolder = this.project.getBaseFolder();
		DirectoryDataProvider directoryDataProvider = new DirectoryDataProvider(baseFolder);
		directoryDataProvider.setSelectedFolders(sourceFolders.values());
		List<File> selectedFolders = directoryChooserDialog.display(directoryDataProvider);
		
		if(selectedFolders == null)
		{
			return;
		}
		
		for(File selFolder : selectedFolders)
		{
			String path = IdeFileUtils.getRelativePath(baseFolder, selFolder);
			
			if(sourceFolders.containsKey(path))
			{
				continue;
			}
			
			sourceFolders.put(path, selFolder);
		}

		DefaultListModel<String> sourceFolderModel = (DefaultListModel<String>) listUi.getModel();
		sourceFolderModel.removeAllElements();
		
		sourceFolders.keySet().forEach(path ->sourceFolderModel.addElement(path));
	}
	
	private void addSourceFolder(ActionEvent e)
	{
		addNewFolders(this.sourceFolders, this.sourceFolderListUi);
	}
	
	private void removeSourceFolder(ActionEvent e)
	{
		int idx = this.sourceFolderListUi.getSelectedIndex();
		
		if(idx < 0)
		{
			return;
		}
		
		if(this.sourceFolders.size() == 1)
		{
			JOptionPane.showMessageDialog(this, "All source folders cannot be removed. Please add another one before removing current one");
			return;
		}
		
		String item = sourceFolderListUi.getSelectedValue();
		this.sourceFolders.remove(item);
		
		DefaultListModel<String> listUiModel = (DefaultListModel<String>) this.sourceFolderListUi.getModel();
		listUiModel.remove(idx);
	}

	public void applyChanges()
	{
		project.setTestSuiteFolders(new LinkedHashSet<String>(this.sourceFolders.keySet()));
	}
}
