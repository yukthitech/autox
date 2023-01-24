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
package com.yukthitech.prism.actions;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.NewProjectDialog;
import com.yukthitech.prism.dialog.DeleteProjectDialog;
import com.yukthitech.prism.layout.Action;
import com.yukthitech.prism.layout.ActionHolder;
import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.proj.ProjectManager;
import com.yukthitech.prism.projexplorer.BaseTreeNode;
import com.yukthitech.prism.projexplorer.ProjectExplorer;
import com.yukthitech.prism.projexplorer.ProjectTreeNode;
import com.yukthitech.prism.projpropdialog.ProjectPropertiesDialog;
import com.yukthitech.prism.ui.InProgressDialog;

@ActionHolder
public class ProjectActions
{
	private JFileChooser projectChooser = new JFileChooser();

	@Autowired
	private ProjectExplorer projectExplorer;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private ApplicationContext applicationContext;

	private NewProjectDialog newProjDialog;

	private ProjectPropertiesDialog projPropertiesDialog;
	
	private DeleteProjectDialog deleteProjectDialog;

	@PostConstruct
	private void init()
	{
		newProjDialog = new NewProjectDialog(IdeUtils.getCurrentWindow());
		deleteProjectDialog = new DeleteProjectDialog(projectManager);

		projectChooser.setDialogTitle("Open Project");
		projectChooser.setAcceptAllFileFilterUsed(false);
		projectChooser.setFileFilter(new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return "AutoX Project Files (" + Project.PROJECT_FILE_NAME + ")";
			}

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() || Project.PROJECT_FILE_NAME.equals(f.getName());
			}
		});
	}
	
	private void openProject(String projectPath, String openMssg)
	{
		InProgressDialog.getInstance().display(openMssg, new Runnable()
		{
			@Override
			public void run()
			{
				projectManager.openProject(projectPath);
			}
		}, 
		ex -> 
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), ex.getMessage(), "Project Open Error", JOptionPane.ERROR_MESSAGE);
		});
	}

	@Action
	public void newProject()
	{
		Project project = newProjDialog.display();
		
		if(project != null)
		{
			openProject(project.getProjectFilePath(), "Opening new project. Please wait...");
		}
	}

	@Action
	public void openProject()
	{
		if(projectChooser.showOpenDialog(IdeUtils.getCurrentWindow()) == JFileChooser.APPROVE_OPTION)
		{
			openProject(projectChooser.getSelectedFile().getPath(), "Opening project. Please wait...");
		}
	}

	@Action
	public void deleteProject()
	{
		BaseTreeNode selectedNode = projectExplorer.getActiveNode();
		
		if(!(selectedNode instanceof ProjectTreeNode))
		{
			return;
		}
		
		Project proj = ((ProjectTreeNode) selectedNode).getProject();
		deleteProjectDialog.displayFor(proj);
	}

	@Action
	public void projectProperties()
	{
		if(projPropertiesDialog == null)
		{
			projPropertiesDialog = new ProjectPropertiesDialog(IdeUtils.getCurrentWindow());
			IdeUtils.autowireBean(applicationContext, projPropertiesDialog);
		}
		
		Project project = projectExplorer.getSelectedProject();
		
		if(project == null)
		{
			return;
		}

		boolean updated = projPropertiesDialog.display(project);
		
		if(updated)
		{
			projectExplorer.reloadProjectNode(project);
		}
	}
}
