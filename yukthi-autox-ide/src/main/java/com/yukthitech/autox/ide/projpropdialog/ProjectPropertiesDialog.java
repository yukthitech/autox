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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.proj.ProjectManager;
import com.yukthitech.swing.EscapableDialog;

public class ProjectPropertiesDialog extends EscapableDialog
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private Project project;
	
	private boolean updated = false;

	private ProjectSourceFolderPanel projectSourceFolderPanel = new ProjectSourceFolderPanel();
	//private ProjectClassPathPanel projectPropertiesClassPath = new ProjectClassPathPanel();
	
	private JTabbedPane tabbedPane;

	@Autowired
	private ProjectManager projectManager;
	
	private final ProjectBasicPropPanel projectBasicPropPanel = new ProjectBasicPropPanel();
	private final ProjectIgnoreFolderPanel ignoredFoldersPanel = new ProjectIgnoreFolderPanel();
	
	/**
	 * Create the dialog.
	 */
	public ProjectPropertiesDialog(Window window)
	{
		super.setModalityType(ModalityType.APPLICATION_MODAL);

		setBounds(100, 100, 550, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane);
			
			tabbedPane.addTab("Basic", null, projectBasicPropPanel, "Basic Project Properties");
			tabbedPane.addTab("Source Folders", null, projectSourceFolderPanel, "Source & Resource Folders");
			//tabbedPane.addTab("ClassPath", null, projectPropertiesClassPath, null);
			tabbedPane.addTab("Ignored Folders", null, ignoredFoldersPanel, "Folders to ignore");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Apply & close");
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						saveProjectProperties();
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
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public boolean display(Project project)
	{
		this.project = project;
		updated = false;
		
		projectBasicPropPanel.setProject(project);
		//projectPropertiesClassPath.setProject(project);
		projectSourceFolderPanel.setProject(project);
		ignoredFoldersPanel.setProject(project);
		setTitle("Project Properties - " + project.getName());
		
		//tabbedPane.setSelectedIndex(0);

		IdeUtils.centerOnScreen(this);
		super.repaint();
		super.setVisible(true);
		
		return updated;
	}

	protected void saveProjectProperties()
	{
		if(!projectBasicPropPanel.applyChanges(projectManager))
		{
			tabbedPane.setSelectedIndex(0);
			return;
		}
		
		projectSourceFolderPanel.applyChanges();
		//projectPropertiesClassPath.applyChanges();
		ignoredFoldersPanel.applyChanges();
		
		projectManager.updateProject(project);
		
		updated = true;
		super.setVisible(false);
	}
}
