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
package com.yukthitech.autox.ide.search;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.dialog.RegexSandboxDialog;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;
import com.yukthitech.autox.ide.search.FileSearchQuery.Scope;
import com.yukthitech.autox.ide.state.PersistableState;
import java.awt.Color;

public class TextSearchPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final JPanel panel_2 = new JPanel();
	private final JButton findAllBut = new JButton("Find All");
	private final JButton txtReplaceAllBut = new JButton("Replace All");
	private final JPanel mainSearchPanel = new JPanel();
	private final JLabel lblNewLabel = new JLabel("Search:");
	
	@PersistableState
	private final JTextField searchStrFld = new JTextField();
	
	private final JPanel panel = new JPanel();
	
	@PersistableState
	private final JCheckBox txtCaseSensitiveCbox = new JCheckBox("Case Sensitive");
	
	@PersistableState
	private final JCheckBox txtRegexCbox = new JCheckBox("Regular Expression");
	
	private final JLabel lblNewLabel_3 = new JLabel("Replace With:");
	private final JLabel lblNewLabel_1 = new JLabel("File Pattern:");
	
	@PersistableState
	private final JTextField filePtrnFld = new JTextField();
	
	private final JLabel lblNewLabel_2 = new JLabel("(Comma separated patterns. * = any string, ? = any character)");
	private final JLabel lblNewLabel_4 = new JLabel("Scope:");
	private final JPanel panel_1 = new JPanel();
	
	private final JRadioButton txtAllProjRbut = new JRadioButton("All Projects");
	private final JRadioButton txtSelFoldersRbut = new JRadioButton("Selected Folders/Files");
	
	private ButtonGroup scopeGroup = new ButtonGroup();
	private final JButton findReplaceBut = new JButton("Find & Replace");
	
	@Autowired
	private SearchDialog parentDialog;
	
	@Autowired
	private ProjectExplorer projectExplorer;
	
	@Autowired
	private FileSearchService searchService;
	
	@Autowired
	private RegexSandboxDialog regexSandboxDialog;
	
	@PersistableState
	private final JCheckBox matchMultiLineCbox = new JCheckBox("Match multiple lines");
	private final JScrollPane scrollPane = new JScrollPane();
	
	@PersistableState
	private final JTextArea replaceWithFld = new JTextArea();
	
	private final JLabel lblNewLabel_5 = new JLabel("(Regex - caturing group values can be refered using $1, $2.. $n)");
	private final JPanel panel_3 = new JPanel();
	private final JPanel panel_4 = new JPanel();
	private final JButton regexSandboxBut = new JButton("Regex Sandbox");

	/**
	 * Create the panel.
	 */
	public TextSearchPanel()
	{
		setLayout(new BorderLayout(0, 0));
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		flowLayout.setVgap(2);
		flowLayout.setHgap(2);
		
		panel_2.add(panel_3);
		panel_3.add(findAllBut);
		panel_3.add(findReplaceBut);
		panel_3.add(txtReplaceAllBut);
		FlowLayout flowLayout_3 = (FlowLayout) panel_4.getLayout();
		flowLayout_3.setVgap(2);
		flowLayout_3.setHgap(2);
		
		panel_2.add(panel_4, BorderLayout.WEST);
		
		panel_4.add(regexSandboxBut);
		regexSandboxBut.addActionListener(this::displayRegexSandbox);
		
		txtReplaceAllBut.addActionListener(this::replaceAll);
		findReplaceBut.addActionListener(this::replaceAndFind);
		findAllBut.addActionListener(this::findAll);
		
		mainSearchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		add(mainSearchPanel);
		GridBagLayout gbl_mainSearchPanel = new GridBagLayout();
		gbl_mainSearchPanel.columnWidths = new int[]{0, 0, 0};
		gbl_mainSearchPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_mainSearchPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_mainSearchPanel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		mainSearchPanel.setLayout(gbl_mainSearchPanel);
		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		
		mainSearchPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		GridBagConstraints gbc_searchStrFld = new GridBagConstraints();
		gbc_searchStrFld.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchStrFld.insets = new Insets(0, 0, 5, 0);
		gbc_searchStrFld.gridx = 1;
		gbc_searchStrFld.gridy = 0;
		searchStrFld.setColumns(10);
		mainSearchPanel.add(searchStrFld, gbc_searchStrFld);
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		FlowLayout flowLayout_2 = (FlowLayout) panel.getLayout();
		flowLayout_2.setVgap(0);
		flowLayout_2.setHgap(2);
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		mainSearchPanel.add(panel, gbc_panel);
		
		panel.add(txtCaseSensitiveCbox);
		
		txtRegexCbox.addChangeListener(new ChangeListener() 
		{
			public void stateChanged(ChangeEvent e) 
			{
				matchMultiLineCbox.setEnabled(txtRegexCbox.isSelected());
			}
		});
		
		panel.add(txtRegexCbox);
		
		panel.add(matchMultiLineCbox);
		matchMultiLineCbox.setEnabled(false);
		
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 2;
		mainSearchPanel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 2;
		mainSearchPanel.add(scrollPane, gbc_scrollPane);
		replaceWithFld.setTabSize(3);
		scrollPane.setViewportView(replaceWithFld);
		replaceWithFld.setRows(3);
		
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_5.gridx = 1;
		gbc_lblNewLabel_5.gridy = 3;
		lblNewLabel_5.setForeground(Color.GRAY);
		mainSearchPanel.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 4;
		mainSearchPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		GridBagConstraints gbc_filePtrnFld = new GridBagConstraints();
		gbc_filePtrnFld.fill = GridBagConstraints.HORIZONTAL;
		gbc_filePtrnFld.insets = new Insets(0, 0, 5, 0);
		gbc_filePtrnFld.gridx = 1;
		gbc_filePtrnFld.gridy = 4;
		filePtrnFld.setText("*");
		filePtrnFld.setColumns(10);
		mainSearchPanel.add(filePtrnFld, gbc_filePtrnFld);
		
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 5;
		lblNewLabel_2.setForeground(Color.GRAY);
		mainSearchPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 6;
		mainSearchPanel.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 6;
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setVgap(0);
		flowLayout_1.setHgap(2);
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		mainSearchPanel.add(panel_1, gbc_panel_1);
		txtAllProjRbut.setSelected(true);
		
		panel_1.add(txtAllProjRbut);
		
		panel_1.add(txtSelFoldersRbut);
		
		scopeGroup.add(txtAllProjRbut);
		scopeGroup.add(txtSelFoldersRbut);
		
	}
	
	void resetForDisplay(boolean selectedTab)
	{
		File file = projectExplorer.getSelectedFile();
		
		//if at least one file is selected, then only enable selected-folders option
		txtSelFoldersRbut.setEnabled(file != null);
		txtSelFoldersRbut.setSelected(file != null);
		
		if(selectedTab)
		{
			searchStrFld.requestFocus();
		}
	}

	private FileSearchQuery buildQuery()
	{
		String srchStr = searchStrFld.getText();
		
		if(srchStr.length() == 0)
		{
			JOptionPane.showMessageDialog(this, "Please provide search string and then try!");
			return null;
		}
		
		boolean isRegex = this.txtRegexCbox.isSelected();
		
		if(isRegex)
		{
			try
			{
				Pattern.compile(srchStr);
			}catch(Exception ex)
			{
				JOptionPane.showMessageDialog(this, "Invalid regular expression specified for search. Please correct and try!\nError: " + ex.getMessage());
				return null;
			}
		}
		
		String filePatternStr = this.filePtrnFld.getText().trim();
		
		if(filePatternStr.length() == 0)
		{
			JOptionPane.showMessageDialog(this, "Please provide valid file pattern to search for and then try!");
			return null;
		}

		List<String> filePatterns = Arrays.asList(filePatternStr.split("\\s*\\,\\s*"));
		
		return FileSearchQuery.newTextQuery(
				srchStr, 
				this.txtCaseSensitiveCbox.isSelected(), 
				isRegex, 
				this.matchMultiLineCbox.isSelected(),
				this.replaceWithFld.getText(), 
				filePatterns, 
				this.txtAllProjRbut.isSelected() ? Scope.ALL_PROJECTS : Scope.SELECTED_FOLDERS
				);
	}
	
	private void findAll(ActionEvent e)
	{
		FileSearchQuery query = buildQuery();
		
		if(query == null)
		{
			return;
		}
		
		searchService.findAll(query, false);
		parentDialog.closeDialog();
	}
	
	private void replaceAndFind(ActionEvent e)
	{
		FileSearchQuery query = buildQuery();
		
		if(query == null)
		{
			return;
		}
		
		searchService.findAll(query, true);
		parentDialog.closeDialog();
	}

	private void replaceAll(ActionEvent e)
	{
		FileSearchQuery query = buildQuery();
		
		if(query == null)
		{
			return;
		}
		
		int res = JOptionPane.showConfirmDialog(this, "This operation may change content of multiple files which cannot be undone.\nAre you sure you want to continue.",
				"Replace All", JOptionPane.YES_NO_OPTION);
		
		if(res == JOptionPane.NO_OPTION)
		{
			return;
		}
		
		int replaceCount = searchService.replaceAll(query);
		
		if(replaceCount >= 0)
		{
			parentDialog.closeDialog();
			JOptionPane.showMessageDialog(this, "Number of occurrences replaced: " + replaceCount);
		}
	}
	
	private void displayRegexSandbox(ActionEvent e)
	{
		regexSandboxDialog.display();
	}
}
