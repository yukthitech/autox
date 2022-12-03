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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.proj.ProjectManager;

public class ProjectPropertiesDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private Project project;

	private ProjectSourceFolderPanel projectSourceFolderPanel;
	private ProjectClassPathPanel projectPropertiesClassPath;
	
	private JTabbedPane tabbedPane;

	@Autowired
	private ProjectManager projectManager;
	
	private final ProjectBasicPropPanel projectBasicPropPanel = new ProjectBasicPropPanel();
	
	/**
	 * Create the dialog.
	 */
	public ProjectPropertiesDialog(Window window)
	{
		super(window, ModalityType.APPLICATION_MODAL);

		setBounds(100, 100, 550, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane);
			
			tabbedPane.addTab("Basic", null, projectBasicPropPanel, null);
			{
				projectSourceFolderPanel = new ProjectSourceFolderPanel();
				tabbedPane.addTab("Source Folders", null, projectSourceFolderPanel, null);
			}
			{
				projectPropertiesClassPath = new ProjectClassPathPanel();
				tabbedPane.addTab("ClassPath", null, projectPropertiesClassPath, null);
			}
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

	@Override
	protected JRootPane createRootPane()
	{
		JRootPane rootPane = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");

		Action actionListener = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent actionEvent)
			{
				setVisible(false);
			}
		};
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);

		return rootPane;
	}

	public Project display(IdeContext ideContext)
	{
		this.project = ideContext.getActiveProject();
		
		projectBasicPropPanel.setProject(project);
		projectPropertiesClassPath.setProject(project);
		projectSourceFolderPanel.setProject(project);
		setTitle("Project Properties - " + project.getName());
		
		tabbedPane.setSelectedIndex(0);

		IdeUtils.centerOnScreen(this);
		super.setVisible(true);
		
		return project;
	}

	protected void saveProjectProperties()
	{
		if(!projectBasicPropPanel.applyChanges(projectManager))
		{
			tabbedPane.setSelectedIndex(0);
			return;
		}
		
		projectSourceFolderPanel.applyChanges();
		projectPropertiesClassPath.applyChanges();
		
		projectManager.updateProject(project);
		
		super.setVisible(false);
	}
}
