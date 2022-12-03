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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.proj.ProjectManager;

public class DeleteProjectDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private final JLabel lblAreYouSure = new JLabel("Are you sure you want to delete project - ?");
	private final JCheckBox deleteContentCbox = new JCheckBox("Delete project contents also from disk");

	private boolean deleteProject = false;
	
	private ProjectManager projectManager;

	/**
	 * Create the dialog.
	 */
	public DeleteProjectDialog(ProjectManager projectManager)
	{
		this.projectManager = projectManager;
		
		super.setModalityType(ModalityType.DOCUMENT_MODAL);

		setResizable(false);
		setTitle("Delete Project");
		setBounds(100, 100, 450, 124);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		lblAreYouSure.setFont(new Font("Tahoma", Font.BOLD, 12));

		contentPanel.add(lblAreYouSure, BorderLayout.CENTER);

		contentPanel.add(deleteContentCbox, BorderLayout.SOUTH);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Delete");
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						deleteProject = true;
						setVisible(false);
					}
				});
				okButton.setFont(new Font("Tahoma", Font.BOLD, 11));
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

	public boolean displayFor(Project project)
	{
		this.deleteProject = false;
		
		lblAreYouSure.setText("Are you sure you want to delete project - " + project.getName() + " ?");

		IdeUtils.centerOnScreen(this);
		super.setVisible(true);
		
		if(!deleteProject)
		{
			return false;
		}
		
		projectManager.deleteProject(project, deleteContentCbox.isSelected());
		return true;
	}
}
