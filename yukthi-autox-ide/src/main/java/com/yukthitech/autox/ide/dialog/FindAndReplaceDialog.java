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
package com.yukthitech.autox.ide.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;

@Service
public class FindAndReplaceDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private final JLabel lblFind = new JLabel("Find: ");
	private final JTextField fldFindStr = new JTextField();
	private final JLabel lblReplaceWith = new JLabel("Replace With: ");
	private final JTextField fldReplaceStr = new JTextField();
	private final JPanel panel_1 = new JPanel();
	private final JCheckBox chckbxCaseSensitive = new JCheckBox("Case Sensitive");
	private final JCheckBox chckbxWrapSearch = new JCheckBox("Wrap Search");
	private final JCheckBox chckbxRegularExpression = new JCheckBox("Regular Expression");
	private final JPanel panel_2 = new JPanel();
	private final JLabel lblStatus = new JLabel("");
	private final JPanel panel_3 = new JPanel();
	private final JButton btnFind = new JButton("Find");
	private final JButton btnReplaceFind = new JButton("Replace / Find");
	private final JPanel panel_4 = new JPanel();
	private final JButton btnReplace = new JButton("Replace");
	private final JButton btnReplaceAll = new JButton("Replace All");
	private final JCheckBox chckbxReverseDirection = new JCheckBox("Reverse Direction");

	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	private final JPanel panelRegex = new JPanel();
	private final JCheckBox chckbxCaseInsensitive = new JCheckBox("Case Insensitive");
	private final JCheckBox chckbxMultiLined = new JCheckBox("Multi line");
	private final JCheckBox chckbxDotAll = new JCheckBox("Dot All");

	/**
	 * Create the dialog.
	 */
	public FindAndReplaceDialog(Frame frame)
	{
		super(frame);

		fldReplaceStr.setToolTipText("Replacement string");
		fldReplaceStr.setColumns(10);
		fldFindStr.setToolTipText("String/Expression to be searched");
		fldFindStr.setColumns(20);
		setTitle("Find & Replace");
		setBounds(100, 100, 327, 425);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);

		GridBagConstraints gbc_lblFind = new GridBagConstraints();
		gbc_lblFind.insets = new Insets(0, 0, 5, 5);
		gbc_lblFind.anchor = GridBagConstraints.EAST;
		gbc_lblFind.gridx = 0;
		gbc_lblFind.gridy = 0;
		contentPanel.add(lblFind, gbc_lblFind);

		GridBagConstraints gbc_fldFindStr = new GridBagConstraints();
		gbc_fldFindStr.weightx = 1.0;
		gbc_fldFindStr.insets = new Insets(0, 0, 5, 0);
		gbc_fldFindStr.fill = GridBagConstraints.HORIZONTAL;
		gbc_fldFindStr.gridx = 1;
		gbc_fldFindStr.gridy = 0;
		contentPanel.add(fldFindStr, gbc_fldFindStr);

		GridBagConstraints gbc_lblReplaceWith = new GridBagConstraints();
		gbc_lblReplaceWith.anchor = GridBagConstraints.EAST;
		gbc_lblReplaceWith.insets = new Insets(0, 0, 5, 5);
		gbc_lblReplaceWith.gridx = 0;
		gbc_lblReplaceWith.gridy = 1;
		contentPanel.add(lblReplaceWith, gbc_lblReplaceWith);

		GridBagConstraints gbc_fldReplaceStr = new GridBagConstraints();
		gbc_fldReplaceStr.weightx = 1.0;
		gbc_fldReplaceStr.insets = new Insets(0, 0, 5, 0);
		gbc_fldReplaceStr.fill = GridBagConstraints.HORIZONTAL;
		gbc_fldReplaceStr.gridx = 1;
		gbc_fldReplaceStr.gridy = 1;
		contentPanel.add(fldReplaceStr, gbc_fldReplaceStr);

		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		panel_1.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		GridBagConstraints gbc_chckbxReverseDirection = new GridBagConstraints();
		gbc_chckbxReverseDirection.anchor = GridBagConstraints.WEST;
		gbc_chckbxReverseDirection.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxReverseDirection.gridx = 0;
		gbc_chckbxReverseDirection.gridy = 0;
		chckbxReverseDirection.setToolTipText("To be checked if search should happen in reverse direction");
		panel_1.add(chckbxReverseDirection, gbc_chckbxReverseDirection);

		GridBagConstraints gbc_chckbxWrapSearch = new GridBagConstraints();
		gbc_chckbxWrapSearch.anchor = GridBagConstraints.WEST;
		gbc_chckbxWrapSearch.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxWrapSearch.gridx = 1;
		gbc_chckbxWrapSearch.gridy = 0;
		chckbxWrapSearch.setToolTipText("To be checked if search needs to be wrapped");
		panel_1.add(chckbxWrapSearch, gbc_chckbxWrapSearch);

		GridBagConstraints gbc_chckbxRegularExpression = new GridBagConstraints();
		gbc_chckbxRegularExpression.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxRegularExpression.gridx = 0;
		gbc_chckbxRegularExpression.gridy = 1;
		
		chckbxRegularExpression.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				boolean enabled = chckbxRegularExpression.isSelected();
				
				chckbxCaseInsensitive.setEnabled(enabled);
				chckbxMultiLined.setEnabled(enabled);
				chckbxDotAll.setEnabled(enabled);
			}
		});
		
		chckbxRegularExpression.setToolTipText("To be checked if search string is regular expression");
		panel_1.add(chckbxRegularExpression, gbc_chckbxRegularExpression);

		GridBagConstraints gbc_chckbxCaseSensitive = new GridBagConstraints();
		gbc_chckbxCaseSensitive.anchor = GridBagConstraints.WEST;
		gbc_chckbxCaseSensitive.gridx = 1;
		gbc_chckbxCaseSensitive.gridy = 1;
		chckbxCaseSensitive.setToolTipText("To be checked if search needs to be case sensitive");
		panel_1.add(chckbxCaseSensitive, gbc_chckbxCaseSensitive);

		GridBagConstraints gbc_panelRegex = new GridBagConstraints();
		gbc_panelRegex.gridwidth = 2;
		gbc_panelRegex.insets = new Insets(0, 0, 5, 0);
		gbc_panelRegex.fill = GridBagConstraints.BOTH;
		gbc_panelRegex.gridx = 0;
		gbc_panelRegex.gridy = 3;
		panelRegex.setBorder(new TitledBorder(null, "Regex Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(panelRegex, gbc_panelRegex);
		GridBagLayout gbl_panelRegex = new GridBagLayout();
		gbl_panelRegex.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelRegex.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelRegex.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelRegex.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelRegex.setLayout(gbl_panelRegex);

		GridBagConstraints gbc_chckbxCaseInsensitive = new GridBagConstraints();
		gbc_chckbxCaseInsensitive.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxCaseInsensitive.gridx = 0;
		gbc_chckbxCaseInsensitive.gridy = 0;
		chckbxCaseInsensitive.setToolTipText("Enables case-insensitive matching.");
		chckbxCaseInsensitive.setEnabled(false);
		panelRegex.add(chckbxCaseInsensitive, gbc_chckbxCaseInsensitive);

		GridBagConstraints gbc_chckbxMultiLined = new GridBagConstraints();
		gbc_chckbxMultiLined.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxMultiLined.gridx = 1;
		gbc_chckbxMultiLined.gridy = 0;
		chckbxMultiLined.setToolTipText("In multiline mode the expressions ^ and $ match just after or just before, respectively, a line terminator or the end of the input sequence.");
		chckbxMultiLined.setEnabled(false);
		panelRegex.add(chckbxMultiLined, gbc_chckbxMultiLined);

		GridBagConstraints gbc_chckbxDotAll = new GridBagConstraints();
		gbc_chckbxDotAll.anchor = GridBagConstraints.WEST;
		gbc_chckbxDotAll.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxDotAll.gridx = 0;
		gbc_chckbxDotAll.gridy = 1;
		chckbxDotAll.setToolTipText("In dotall mode, the expression . matches any character, including a line terminator. ");
		chckbxDotAll.setEnabled(false);
		panelRegex.add(chckbxDotAll, gbc_chckbxDotAll);

		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridwidth = 2;
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 4;
		contentPanel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 1.0, 0.0, 0.0 };
		panel_2.setLayout(gbl_panel_2);

		GridBagConstraints gbc_lblStatus = new GridBagConstraints();
		gbc_lblStatus.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblStatus.insets = new Insets(0, 0, 5, 0);
		gbc_lblStatus.gridx = 0;
		gbc_lblStatus.gridy = 0;
		lblStatus.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		lblStatus.setBorder(new EmptyBorder(3, 5, 3, 3));
		lblStatus.setMinimumSize(new Dimension(20, 40));
		panel_2.add(lblStatus, gbc_lblStatus);

		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 1;
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_2.add(panel_3, gbc_panel_3);
		btnFind.setMnemonic('F');

		btnFind.addActionListener(this::find);
		btnFind.setPreferredSize(new Dimension(120, 25));

		panel_3.add(btnFind);
		btnReplaceFind.setMnemonic('e');

		btnReplaceFind.setPreferredSize(new Dimension(120, 25));
		btnReplaceFind.addActionListener(this::replaceAndFind);
		panel_3.add(btnReplaceFind);

		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 2;
		FlowLayout flowLayout_1 = (FlowLayout) panel_4.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		panel_2.add(panel_4, gbc_panel_4);
		btnReplace.setMnemonic('R');

		btnReplace.setPreferredSize(new Dimension(120, 25));
		btnReplace.addActionListener(this::replace);
		panel_4.add(btnReplace);
		btnReplaceAll.setMnemonic('A');
		btnReplaceAll.setMnemonic(KeyEvent.VK_A);
		btnReplaceAll.setPreferredSize(new Dimension(120, 25));
		btnReplaceAll.addActionListener(this::replaceAll);

		panel_4.add(btnReplaceAll);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.setActionCommand("Cancel");
				buttonPane.add(closeButton);
				closeButton.addActionListener(this::close);
			}
		}
		
		super.getRootPane().registerKeyboardAction(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				FindAndReplaceDialog.this.dispatchEvent(new WindowEvent(FindAndReplaceDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		super.getRootPane().registerKeyboardAction(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				find(e);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public void display()
	{
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();

		if(editor == null)
		{
			return;
		}

		String defText = editor.getSelectedText();
		
		if(defText != null)
		{
			fldFindStr.setText(defText);
		}
		
		fldFindStr.selectAll();
		fldFindStr.requestFocus();
		fldReplaceStr.setText("");
		this.setVisible(true);
	}

	private FindCommand buildCommand()
	{
		String searchStr = this.fldFindStr.getText();

		if(searchStr.length() == 0)
		{
			JOptionPane.showMessageDialog(this, "Please provide search string and then try!");
			return null;
		}

		FindCommand command = new FindCommand();
		command.setSearchString(searchStr);

		command.setReplaceWith(fldReplaceStr.getText());
		command.setCaseSensitive(chckbxCaseSensitive.isSelected());
		command.setRegularExpression(chckbxRegularExpression.isSelected());
		command.setReverseDirection(chckbxReverseDirection.isSelected());
		command.setWrapSearch(chckbxWrapSearch.isSelected());
		
		int regOptions = chckbxCaseInsensitive.isSelected() ? Pattern.CASE_INSENSITIVE : 0;
		regOptions |= chckbxMultiLined.isSelected() ? Pattern.MULTILINE : 0;
		regOptions |= chckbxDotAll.isSelected() ? Pattern.DOTALL : 0;

		command.setRegexOptions(regOptions);
		
		return command;
	}

	private void execute(FindOperation op)
	{
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();

		if(editor == null)
		{
			JOptionPane.showMessageDialog(this, "No active file editor found.");
			return;
		}

		FindCommand command = buildCommand();
		String res = editor.executeFindOperation(command, op);

		lblStatus.setText("<html><body>" + res + "</body></html>");
	}

	private void find(ActionEvent e)
	{
		execute(FindOperation.FIND);
	}

	private void replaceAndFind(ActionEvent e)
	{
		execute(FindOperation.REPLACE_AND_FIND);
	}

	private void replace(ActionEvent e)
	{
		execute(FindOperation.REPLACE);
	}

	private void replaceAll(ActionEvent e)
	{
		execute(FindOperation.REPLACE_ALL);
	}

	private void close(ActionEvent e)
	{
		setVisible(false);
	}
}
