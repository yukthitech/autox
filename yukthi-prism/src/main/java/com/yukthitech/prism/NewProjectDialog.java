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
package com.yukthitech.prism;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.prism.model.Project;

public class NewProjectDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(NewProjectDialog.class);

	private final JPanel contentPanel = new JPanel();
	private JTextField baseFolderPath;
	private JTextField projectName;
	private JFileChooser fileChooser = new JFileChooser();
	private Project project;
	private boolean projectCreated = false;

	/**
	 * Create the dialog.
	 */
	public NewProjectDialog(Window window)
	{
		super(window, ModalityType.APPLICATION_MODAL);
		setTitle("New Project");

		setBounds(100, 100, 450, 148);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {64, 30, 257};
		gbl_contentPanel.rowHeights = new int[] {20, 20};
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0 };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		contentPanel.add(lblName, gbc_lblName);

		projectName = new JTextField();
		projectName.setColumns(10);
		GridBagConstraints gbc_projectName = new GridBagConstraints();
		gbc_projectName.anchor = GridBagConstraints.NORTH;
		gbc_projectName.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectName.insets = new Insets(0, 0, 5, 0);
		gbc_projectName.gridwidth = 2;
		gbc_projectName.gridx = 2;
		gbc_projectName.gridy = 0;
		contentPanel.add(projectName, gbc_projectName);

		JLabel lblBaseFolder = new JLabel("Base folder");
		GridBagConstraints gbc_lblBaseFolder = new GridBagConstraints();
		gbc_lblBaseFolder.anchor = GridBagConstraints.WEST;
		gbc_lblBaseFolder.insets = new Insets(0, 0, 5, 5);
		gbc_lblBaseFolder.gridx = 0;
		gbc_lblBaseFolder.gridy = 1;
		contentPanel.add(lblBaseFolder, gbc_lblBaseFolder);

		JButton btnBaseFolder = new JButton("...");
		btnBaseFolder.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onBaseFolderSelect();
			}
		});

		baseFolderPath = new JTextField();
		GridBagConstraints gbc_baseFolderPath = new GridBagConstraints();
		gbc_baseFolderPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_baseFolderPath.insets = new Insets(0, 0, 5, 5);
		gbc_baseFolderPath.gridx = 2;
		gbc_baseFolderPath.gridy = 1;
		contentPanel.add(baseFolderPath, gbc_baseFolderPath);
		baseFolderPath.setColumns(10);
		GridBagConstraints gbc_btnBaseFolder = new GridBagConstraints();
		gbc_btnBaseFolder.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnBaseFolder.insets = new Insets(0, 0, 5, 0);
		gbc_btnBaseFolder.gridx = 3;
		gbc_btnBaseFolder.gridy = 1;
		contentPanel.add(btnBaseFolder, gbc_btnBaseFolder);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Create");
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						onCreateProject();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void onCreateProject()
	{
		if(StringUtils.isBlank(projectName.getText()))
		{
			JOptionPane.showMessageDialog(NewProjectDialog.this, "Project name can not be empty");
			return;
		}

		if(StringUtils.isBlank(baseFolderPath.getText()))
		{
			JOptionPane.showMessageDialog(NewProjectDialog.this, "Base folder path can not be empty");
			return;
		}

		project.setName(projectName.getText());
		project.setProjectFilePath(new File(baseFolderPath.getText(), Project.PROJECT_FILE_NAME).getPath());

		try
		{
			project.createProject();
			logger.debug("Created new project with name '{}' at path: {}", project.getName(), project.getBaseFolderPath());

			this.setVisible(false);
			projectCreated = true;
		} catch(IOException e1)
		{
			JOptionPane.showMessageDialog(NewProjectDialog.this, e1.toString());
		}
	}

	private void onBaseFolderSelect()
	{
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select Base folder");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);

		int res = fileChooser.showOpenDialog(this);

		if(res == JFileChooser.APPROVE_OPTION)
		{
			File baseFolderFile = fileChooser.getSelectedFile();
			String projectName = this.projectName.getText();

			if(!baseFolderFile.getName().equals(projectName))
			{
				baseFolderFile = new File(baseFolderFile, projectName);
			}

			baseFolderPath.setText(baseFolderFile.getPath());
		}
	}
	
	private void reset()
	{
		projectName.setText("");
		baseFolderPath.setText("");
	}

	public Project display()
	{
		project = new Project();
		projectCreated = false;
		reset();

		super.setVisible(true);

		if(projectCreated)
		{
			return project;
		}

		return null;
	}
}
