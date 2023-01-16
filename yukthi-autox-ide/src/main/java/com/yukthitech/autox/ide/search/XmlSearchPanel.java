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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;
import com.yukthitech.autox.ide.search.FileSearchQuery.Scope;
import com.yukthitech.autox.ide.state.PersistableState;

public class XmlSearchPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final JPanel panel_2 = new JPanel();
	private final JButton findAllBut = new JButton("Find All");
	private final JButton replaceAllBut = new JButton("Replace All");
	
	private final JPanel repAllBut = new JPanel();
	private final JLabel lblNewLabel = new JLabel("xPath to Search:");
	
	@PersistableState
	private final JTextField xpathFld = new JTextField();
	
	private final JLabel lblNewLabel_4 = new JLabel("Scope:");
	private final JPanel panel_1 = new JPanel();
	
	private final JRadioButton txtAllProjRbut = new JRadioButton("All Projects");
	private final JRadioButton txtSelFoldersRbut = new JRadioButton("Selected Folders/Files");
	
	private final JLabel lblNewLabel_1 = new JLabel("Replacement Script (javascript):");
	private final RTextScrollPane textScrollPane = new RTextScrollPane();
	private final JLabel lblNewLabel_2 = new JLabel("<html>\r\nelement - Current matched element (XmlElement type).<br/>\r\ndocument - Current xml DOM document.\r\n</html>");
	
	@PersistableState
	private final RSyntaxTextArea replaceScriptFld = new RSyntaxTextArea();
	private final JPanel panel = new JPanel();
	private final JPanel panel_3 = new JPanel();
	
	private ButtonGroup scopeGroup = new ButtonGroup();

	@Autowired
	private SearchDialog parentDialog;
	
	@Autowired
	private ProjectExplorer projectExplorer;

	@Autowired
	private FileSearchService searchService;

	private final JButton replaceFindBut = new JButton("Find & Replace");
	private final JLabel lblNewLabel_3 = new JLabel("(Only xml files will be processed)");
	private final JLabel lblNewLabel_3_1 = new JLabel("(Should point to xml elements only)");

	/**
	 * Create the panel.
	 */
	public XmlSearchPanel()
	{
		setLayout(new BorderLayout(0, 0));
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout_2 = (FlowLayout) panel.getLayout();
		flowLayout_2.setVgap(2);
		flowLayout_2.setHgap(2);
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		
		panel_2.add(panel, BorderLayout.WEST);
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		flowLayout.setHgap(2);
		flowLayout.setVgap(2);
		
		panel_2.add(panel_3, BorderLayout.CENTER);
		panel_3.add(findAllBut);
		findAllBut.addActionListener(this::findAll);
		
		panel_3.add(replaceFindBut);
		replaceFindBut.addActionListener(this::replaceAndFind);
		
		panel_3.add(replaceAllBut);
		replaceAllBut.addActionListener(this::replaceAll);
		repAllBut.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		add(repAllBut);
		GridBagLayout gbl_repAllBut = new GridBagLayout();
		gbl_repAllBut.columnWidths = new int[]{0, 0, 0};
		gbl_repAllBut.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
		gbl_repAllBut.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_repAllBut.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		repAllBut.setLayout(gbl_repAllBut);
		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		repAllBut.add(lblNewLabel, gbc_lblNewLabel);
		
		GridBagConstraints gbc_xpathFld = new GridBagConstraints();
		gbc_xpathFld.fill = GridBagConstraints.HORIZONTAL;
		gbc_xpathFld.insets = new Insets(0, 0, 5, 0);
		gbc_xpathFld.gridx = 1;
		gbc_xpathFld.gridy = 0;
		xpathFld.setFont(new Font("Monospaced", Font.BOLD, 13));
		xpathFld.setColumns(10);
		repAllBut.add(xpathFld, gbc_xpathFld);
		
		GridBagConstraints gbc_lblNewLabel_3_1 = new GridBagConstraints();
		gbc_lblNewLabel_3_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_3_1.gridx = 1;
		gbc_lblNewLabel_3_1.gridy = 1;
		lblNewLabel_3_1.setForeground(Color.GRAY);
		repAllBut.add(lblNewLabel_3_1, gbc_lblNewLabel_3_1);
		
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 2;
		repAllBut.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setVgap(0);
		flowLayout_1.setHgap(2);
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		repAllBut.add(panel_1, gbc_panel_1);
		txtAllProjRbut.setSelected(true);
		
		panel_1.add(txtAllProjRbut);
		
		panel_1.add(txtSelFoldersRbut);
		
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_3.gridx = 1;
		gbc_lblNewLabel_3.gridy = 3;
		lblNewLabel_3.setForeground(Color.GRAY);
		repAllBut.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.gridwidth = 2;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 4;
		repAllBut.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_2.gridwidth = 2;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 5;
		lblNewLabel_2.setForeground(Color.GRAY);
		repAllBut.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		GridBagConstraints gbc_textScrollPane = new GridBagConstraints();
		gbc_textScrollPane.gridwidth = 2;
		gbc_textScrollPane.fill = GridBagConstraints.BOTH;
		gbc_textScrollPane.gridx = 0;
		gbc_textScrollPane.gridy = 6;
		repAllBut.add(textScrollPane, gbc_textScrollPane);
		
		replaceScriptFld.setTabSize(3);
		replaceScriptFld.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVASCRIPT);
		replaceScriptFld.setCodeFoldingEnabled(true);
		
		textScrollPane.setViewportView(replaceScriptFld);
		textScrollPane.setLineNumbersEnabled(true);

		scopeGroup.add(txtAllProjRbut);
		scopeGroup.add(txtSelFoldersRbut);
	}

	void resetForDisplay(boolean selectedTab)
	{
		xpathFld.requestFocus();
		
		File file = projectExplorer.getSelectedFile();
		
		//if at least one file is selected, then only enable selected-folders option
		txtSelFoldersRbut.setEnabled(file != null);
		txtSelFoldersRbut.setSelected(file != null);
		
		xpathFld.requestFocus();
	}

	private FileSearchQuery buildQuery(boolean replaceOp)
	{
		String srchStr = xpathFld.getText();
		
		if(srchStr.length() == 0)
		{
			JOptionPane.showMessageDialog(this, "Please provide xpath string and then try!");
			return null;
		}
		
		try
		{
			XpathSearchOperation.parseXpath(srchStr, null);
		}catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "An error occurred while parsing specified xpath.\nError: " + ex.getMessage());
			return null;
		}
		
		String replacementScript = this.replaceScriptFld.getText();
		
		if(replaceOp && replacementScript.trim().length() == 0)
		{
			JOptionPane.showMessageDialog(this, "Please provide replacement script and then try!");
			return null;
		}

		return FileSearchQuery.newXmlQuery(
				srchStr, 
				this.txtAllProjRbut.isSelected() ? Scope.ALL_PROJECTS : Scope.SELECTED_FOLDERS,
				replacementScript
				);
	}

	private void findAll(ActionEvent e)
	{
		FileSearchQuery query = buildQuery(false);
		
		if(query == null)
		{
			return;
		}
		
		searchService.findAll(query, false);
		parentDialog.closeDialog();
	}
	
	private void replaceAndFind(ActionEvent e)
	{
		FileSearchQuery query = buildQuery(true);
		
		if(query == null)
		{
			return;
		}
		
		searchService.findAll(query, true);
		parentDialog.closeDialog();
	}

	private void replaceAll(ActionEvent e)
	{
		FileSearchQuery query = buildQuery(true);
		
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
}
