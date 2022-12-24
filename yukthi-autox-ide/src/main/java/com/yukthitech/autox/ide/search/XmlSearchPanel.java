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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.border.EtchedBorder;

public class XmlSearchPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final JPanel panel_2 = new JPanel();
	private final JButton srchBut = new JButton("Search");
	private final JButton txtReplaceAllBut = new JButton("Replace All");
	private final JButton txtCancelBut = new JButton("Cancel");
	
	private final JPanel repAllBut = new JPanel();
	private final JLabel lblNewLabel = new JLabel("xPath to Search:");
	private final JTextField xpathFld = new JTextField();
	private final JLabel lblNewLabel_4 = new JLabel("Scope:");
	private final JPanel panel_1 = new JPanel();
	private final JRadioButton txtAllProjRbut = new JRadioButton("All Projects");
	private final JRadioButton txtSelFoldersRbut = new JRadioButton("Selected Folders");
	private final JLabel lblNewLabel_1 = new JLabel("Replacement Script:");
	private final RTextScrollPane textScrollPane = new RTextScrollPane();
	private final JLabel lblNewLabel_2 = new JLabel("<html>\r\ncurrentElement - Current matched element.  This script should return one or more element(s) which would replace current node.\r\n</html>");
	private final RSyntaxTextArea replaceScriptFld = new RSyntaxTextArea();
	private final JButton helpBut = new JButton("Help");
	private final JPanel panel = new JPanel();
	private final JPanel panel_3 = new JPanel();
	
	@Autowired
	private SearchDialog parentDialog;

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
		panel.add(helpBut);
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		flowLayout.setHgap(2);
		flowLayout.setVgap(2);
		
		panel_2.add(panel_3, BorderLayout.CENTER);
		panel_3.add(srchBut);
		panel_3.add(txtReplaceAllBut);
		panel_3.add(txtCancelBut);
		repAllBut.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		add(repAllBut);
		GridBagLayout gbl_repAllBut = new GridBagLayout();
		gbl_repAllBut.columnWidths = new int[]{0, 0, 0};
		gbl_repAllBut.rowHeights = new int[] {0, 0, 0, 0, 0};
		gbl_repAllBut.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_repAllBut.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
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
		xpathFld.setColumns(10);
		repAllBut.add(xpathFld, gbc_xpathFld);
		
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 1;
		repAllBut.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 1;
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setVgap(0);
		flowLayout_1.setHgap(2);
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		repAllBut.add(panel_1, gbc_panel_1);
		txtAllProjRbut.setSelected(true);
		
		panel_1.add(txtAllProjRbut);
		
		panel_1.add(txtSelFoldersRbut);
		
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		repAllBut.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_2.gridwidth = 2;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 3;
		lblNewLabel_2.setForeground(Color.GRAY);
		repAllBut.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		GridBagConstraints gbc_textScrollPane = new GridBagConstraints();
		gbc_textScrollPane.gridwidth = 2;
		gbc_textScrollPane.fill = GridBagConstraints.BOTH;
		gbc_textScrollPane.gridx = 0;
		gbc_textScrollPane.gridy = 4;
		repAllBut.add(textScrollPane, gbc_textScrollPane);
		replaceScriptFld.setTabSize(3);
		
		textScrollPane.setViewportView(replaceScriptFld);
	}

	void resetForDisplay()
	{
		
	}
}
