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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.yukthitech.prism.editor.FileEditor;

public class GotoLineDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private final JLabel lblInput = new JLabel("Enter line number (in range 1 - )?");
	private final JTextField textField = new JTextField();
	private final JLabel lblMssg = new JLabel("mssg");
	
	private JButton okButton;
	
	private int maxLine;
	
	private FileEditor editor;

	/**
	 * Create the dialog.
	 */
	public GotoLineDialog(Window window)
	{
		super(window);
		setTitle("Go to line");
		super.setModalityType(ModalityType.DOCUMENT_MODAL);
		
		setResizable(false);
		textField.setColumns(10);
		setBounds(100, 100, 450, 126);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		GridBagConstraints gbc_lblInput = new GridBagConstraints();
		gbc_lblInput.anchor = GridBagConstraints.WEST;
		gbc_lblInput.insets = new Insets(0, 0, 5, 0);
		gbc_lblInput.gridx = 0;
		gbc_lblInput.gridy = 0;
		contentPanel.add(lblInput, gbc_lblInput);
		
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 1;
		contentPanel.add(textField, gbc_textField);
		
		textField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				valueChanged();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				valueChanged();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				valueChanged();
			}
		});
		
		GridBagConstraints gbc_lblMssg = new GridBagConstraints();
		gbc_lblMssg.anchor = GridBagConstraints.WEST;
		gbc_lblMssg.gridx = 0;
		gbc_lblMssg.gridy = 2;
		
		contentPanel.add(lblMssg, gbc_lblMssg);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				
				okButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						gotoLine();
					}
				});
				
				okButton.setEnabled(false);
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
						GotoLineDialog.this.dispatchEvent(new WindowEvent(GotoLineDialog.this, WindowEvent.WINDOW_CLOSING));
					}
				});
			}
		}

		super.getRootPane().registerKeyboardAction(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				GotoLineDialog.this.dispatchEvent(new WindowEvent(GotoLineDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
	
	private int valueChanged()
	{
		okButton.setEnabled(false);
		lblMssg.setText("");
		
		String value = textField.getText().trim();
		
		if(value.length() == 0)
		{
			return -1;
		}
		
		try
		{
			int line = Integer.parseInt(value);
			
			if(line < 1 || line > maxLine)
			{
				lblMssg.setText("Line number out of range.");
				return -1;
			}
			
			okButton.setEnabled(true);
			return line;
		}catch(Exception ex)
		{
			lblMssg.setText("Invalid line number specified.");
		}
		
		return -1;
	}
	
	private void gotoLine()
	{
		int line = valueChanged();
		this.editor.gotoLine(line);
		
		setVisible(false);
	}

	public void display(FileEditor editor)
	{
		this.maxLine = editor.getLineCount();
		this.editor = editor;
		
		lblInput.setText("Enter line number in range (1.. " + maxLine + "): ");
		lblMssg.setText("");
		textField.setText("");
		
		textField.requestFocus();
		
		setVisible(true);
	}
}
