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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.prism.IdeFileUtils;
import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.proj.ProjectManager;
import com.yukthitech.swing.SimpleFileChooserFilter;

public class ProjectBasicPropPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final JLabel lblNewLabel = new JLabel("Name: ");
	private final JTextField nameFld = new JTextField();
	private final JLabel lblNewLabel_1 = new JLabel("Config File:");
	private final JTextField configFileFld = new JTextField();
	private final JButton chooseConfigFileBut = new JButton("...");
	private final JLabel lblNewLabel_2 = new JLabel("App Prop File:");
	private final JTextField propFileFld = new JTextField();
	private final JButton choosePropFileBut = new JButton("...");
	
	private JFileChooser fileChooser = new JFileChooser();

	private FileFilter configFileFilter = new SimpleFileChooserFilter("App Config file (*.xml)", "xml");
	
	private FileFilter propFileFilter = new SimpleFileChooserFilter("App Prop file (*.properties)", "properties");
	
	private Project project;

	/**
	 * Create the panel.
	 */
	public ProjectBasicPropPanel()
	{
		propFileFld.setColumns(10);
		configFileFld.setColumns(10);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		nameFld.setColumns(10);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		GridBagConstraints gbc_nameFld = new GridBagConstraints();
		gbc_nameFld.insets = new Insets(0, 0, 5, 5);
		gbc_nameFld.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameFld.gridx = 1;
		gbc_nameFld.gridy = 0;
		add(nameFld, gbc_nameFld);
		
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		GridBagConstraints gbc_configFileFld = new GridBagConstraints();
		gbc_configFileFld.insets = new Insets(0, 0, 5, 5);
		gbc_configFileFld.fill = GridBagConstraints.HORIZONTAL;
		gbc_configFileFld.gridx = 1;
		gbc_configFileFld.gridy = 1;
		add(configFileFld, gbc_configFileFld);
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 1;
		chooseConfigFileBut.addActionListener(this::onChooseConfigFile);
		add(chooseConfigFileBut, gbc_btnNewButton);
		
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		GridBagConstraints gbc_propFileFld = new GridBagConstraints();
		gbc_propFileFld.insets = new Insets(0, 0, 0, 5);
		gbc_propFileFld.fill = GridBagConstraints.HORIZONTAL;
		gbc_propFileFld.gridx = 1;
		gbc_propFileFld.gridy = 2;
		add(propFileFld, gbc_propFileFld);
		
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.gridx = 2;
		gbc_btnNewButton_1.gridy = 2;
		choosePropFileBut.addActionListener(this::onChoosePropFile);
		add(choosePropFileBut, gbc_btnNewButton_1);
	}
	
	private void onChooseConfigFile(ActionEvent e)
	{
		fileChooser.removeChoosableFileFilter(propFileFilter);
		fileChooser.removeChoosableFileFilter(configFileFilter);
		
		fileChooser.addChoosableFileFilter(configFileFilter);
		int result = fileChooser.showOpenDialog(this);

		if(result != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		
		File file = fileChooser.getSelectedFile();
		String path = IdeFileUtils.getRelativePath(project.getBaseFolder(), file);
		
		if(path == null)
		{
			JOptionPane.showMessageDialog(this, 
					String.format("Invalid file selected. File should be within project folder."
							+ "\nProject folder: %s"
							+ "\nSelecte file: %s", project.getBaseFolder().getPath(), file.getPath()));
			return;
		}
		
		configFileFld.setText(path);
	}

	private void onChoosePropFile(ActionEvent e)
	{
		fileChooser.removeChoosableFileFilter(propFileFilter);
		fileChooser.removeChoosableFileFilter(configFileFilter);
		
		fileChooser.addChoosableFileFilter(propFileFilter);
		int result = fileChooser.showOpenDialog(this);

		if(result != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		
		File file = fileChooser.getSelectedFile();
		String path = IdeFileUtils.getRelativePath(project.getBaseFolder(), file);
		
		if(path == null)
		{
			JOptionPane.showMessageDialog(this, 
					String.format("Invalid file selected. File should be within project folder."
							+ "\nProject folder: %s"
							+ "\nSelect file: %s", project.getBaseFolder().getPath(), file.getPath()));
			return;
		}
		
		propFileFld.setText(path);
	}
	
	public void setProject(Project project)
	{
		this.project = project;
		
		nameFld.setText(project.getName());
		configFileFld.setText(project.getAppConfigFilePath());
		propFileFld.setText(project.getAppPropertyFilePath());
	}
	
	public boolean applyChanges(ProjectManager projectManager)
	{
		if(StringUtils.isBlank(nameFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "Project name cannot be empty");
			return false;
		}
		
		if(StringUtils.isBlank(configFileFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "Config file path cannot be empty");
			return false;
		}

		if(StringUtils.isBlank(propFileFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "App property file path cannot be empty");
			return false;
		}

		File appXml = new File(project.getBaseFolder(), configFileFld.getText());
		
		if(!appXml.exists())
		{
			JOptionPane.showMessageDialog(this, "Invalid/non-exising app config xml file specified: " + appXml.getPath());
			return false;
		}
		
		File appProp = new File(project.getBaseFolder(), propFileFld.getText());
		
		if(!appProp.exists())
		{
			JOptionPane.showMessageDialog(this, "Invalid/non-exising app properties file specified: " + appProp.getPath());
			return false;
		}
		
		String newName = nameFld.getText();
		Project existingProject = projectManager.getProject(newName);
		
		//if another project exists with same name
		if(existingProject != null && existingProject != project)
		{
			JOptionPane.showMessageDialog(this, "Another project with same name is already open: " + newName);
			return false;
		}
		
		project.setName(newName);
		project.setAppConfigFilePath(configFileFld.getText());
		project.setAppPropertyFilePath(propFileFld.getText());

		return true;
	}
}
