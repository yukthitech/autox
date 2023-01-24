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
package com.yukthitech.prism.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.prism.FileDetails;
import com.yukthitech.prism.IdeIndex;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.editor.FileEditorTabbedPane;

public class OpenResourceDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private final JLabel lblSelectAnItem = new JLabel("Select an item to open (? = any character, * = any string): ");
	private final JTextField fldResName = new JTextField();
	private final JLabel lblMatchingItems = new JLabel("Matching Items: ");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JList<FileDetails> listFiles = new JList<FileDetails>();
	private final JTextField fldFilePath = new JTextField();
	
	private DefaultListModel<FileDetails> listModel = new DefaultListModel<>();

	@Autowired
	private IdeIndex ideIndex;
	
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;

	/**
	 * Create the dialog.
	 */
	public OpenResourceDialog(Window window)
	{
		super(window);
		
		super.setModalityType(ModalityType.DOCUMENT_MODAL);
		fldFilePath.setEditable(false);
		fldFilePath.setFont(new Font("Tahoma", Font.BOLD, 12));
		fldFilePath.setColumns(10);
		fldResName.setColumns(10);
		setTitle("Open Resource...");
		setBounds(100, 100, 440, 472);
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(5);
		getContentPane().setLayout(borderLayout);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		GridBagConstraints gbc_lblSelectAnItem = new GridBagConstraints();
		gbc_lblSelectAnItem.anchor = GridBagConstraints.WEST;
		gbc_lblSelectAnItem.insets = new Insets(0, 0, 5, 0);
		gbc_lblSelectAnItem.gridx = 0;
		gbc_lblSelectAnItem.gridy = 0;
		contentPanel.add(lblSelectAnItem, gbc_lblSelectAnItem);
		
		GridBagConstraints gbc_fldResName = new GridBagConstraints();
		gbc_fldResName.insets = new Insets(0, 0, 5, 0);
		gbc_fldResName.fill = GridBagConstraints.HORIZONTAL;
		gbc_fldResName.gridx = 0;
		gbc_fldResName.gridy = 1;
		contentPanel.add(fldResName, gbc_fldResName);
		
		GridBagConstraints gbc_lblMatchingItems = new GridBagConstraints();
		gbc_lblMatchingItems.insets = new Insets(0, 0, 5, 0);
		gbc_lblMatchingItems.anchor = GridBagConstraints.WEST;
		gbc_lblMatchingItems.gridx = 0;
		gbc_lblMatchingItems.gridy = 2;
		contentPanel.add(lblMatchingItems, gbc_lblMatchingItems);
		
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		contentPanel.add(scrollPane, gbc_scrollPane);
		listFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listFiles.setModel(listModel);
		scrollPane.setViewportView(listFiles);
		
		GridBagConstraints gbc_fldFilePath = new GridBagConstraints();
		gbc_fldFilePath.fill = GridBagConstraints.HORIZONTAL;
		gbc_fldFilePath.gridx = 0;
		gbc_fldFilePath.gridy = 4;
		contentPanel.add(fldFilePath, gbc_fldFilePath);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				
				okButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						FileDetails fileDetails = listFiles.getSelectedValue();
						
						if(fileDetails == null)
						{
							return;
						}
						
						openResource(fileDetails);
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				
				cancelButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						OpenResourceDialog.this.setVisible(false);
					}
				});
			}
		}
		
		fldResName.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				filterTextChanged();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				filterTextChanged();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				filterTextChanged();
			}
		});
		
		fldResName.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					listFiles.setSelectedIndex(0);
					listFiles.requestFocus();
				}
			}
		});
		
		listFiles.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				FileDetails fileDetails = listFiles.getSelectedValue();
				
				if(fileDetails == null)
				{
					return;
				}
				
				fldFilePath.setText("[" + fileDetails.getProject().getName() + "] " + fileDetails.getPath());
			}
		});
		
		listFiles.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() < 2)
				{
					return;
				}
				
				FileDetails fileDetails = listFiles.getSelectedValue();
				
				if(fileDetails == null)
				{
					return;
				}
				
				openResource(fileDetails);
			}
		});

		super.getRootPane().registerKeyboardAction(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				OpenResourceDialog.this.dispatchEvent(new WindowEvent(OpenResourceDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
	
	private void filterTextChanged()
	{
		IdeUtils.executeConsolidatedJob("OpenResourceDlg", new Runnable()
		{
			@Override
			public void run()
			{
				filter();
			}
		}, 100);
	}

	public void display()
	{
		fldResName.setText("");
		filter();
		
		fldResName.requestFocus();
		
		IdeUtils.executeUiTask(new Runnable()
		{
			@Override
			public void run()
			{
				setVisible(true);
			}
		});
	}
	
	private boolean filter(FileDetails file, Pattern filter)
	{
		if(filter == null)
		{
			return true;
		}
		
		Matcher matcher = filter.matcher(file.getFile().getName());
		return matcher.matches();
	}
	
	private void filter()
	{
		String filterStr = fldResName.getText().trim();
		Pattern filter = null;
		
		if(filterStr.length() > 0)
		{
			//Place * before each capital letters
			filterStr = filterStr.replaceAll(".([A-Z])", "*$1");
			
			//place * before special characters
			filterStr = filterStr.replaceAll(".(\\W)", "*$1");
					
			if(!filterStr.endsWith("*") && !filterStr.endsWith("?"))
			{
				filterStr = filterStr + "*";
			}

			filterStr = filterStr.replace("?", ".");
			filterStr = filterStr.replace("*", ".*?");
			filter = Pattern.compile(filterStr, Pattern.CASE_INSENSITIVE);
		}

		listModel = new DefaultListModel<>();
		
		for(FileDetails file : ideIndex.getFiles())
		{
			if(!filter(file, filter))
			{
				continue;
			}
			
			listModel.addElement(file);
		}
		
		listFiles.setModel(listModel);
	}
	
	private void openResource(FileDetails file)
	{
		fileEditorTabbedPane.openOrActivateFile(file.getProject(), file.getFile());
		super.setVisible(false);
	}
}
