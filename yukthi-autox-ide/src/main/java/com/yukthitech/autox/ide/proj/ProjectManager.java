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
package com.yukthitech.autox.ide.proj;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.events.ProjectRemovedEvent;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.model.ProjectState;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;
import com.yukthitech.autox.ide.services.IdeEventManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manager to manage projects.
 * @author akranthikiran
 */
@Service
public class ProjectManager
{
	private static Logger logger = LogManager.getLogger(ProjectManager.class);
	
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private ProjectExplorer projectExplorer;
	
	@Autowired
	private IdeEventManager ideEventManager;

	private Set<Project> projects = new HashSet<>();

	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void saveState(IdeState state)
			{
				state.retainProjects(projects);
			}
			
			@Override
			public void loadState(IdeState state)
			{
				for(ProjectState project : state.getOpenProjects())
				{
					openProject(project.getPath());
				}
				
				projectExplorer.loadFilesToIndex();
			}
		});
	}
	
	/**
	 * Opens the project from specified base folder path.
	 * @param path base folder path of project to open
	 */
	public Project openProject(String path)
	{
		logger.debug("Loading project at path: {}", path);
		
		Project existingProj = projects.stream()
				.filter(proj -> proj.getProjectFilePath().equals(path))
				.findFirst()
				.orElse(null);
		
		if(existingProj != null)
		{
			return existingProj;
		}
		
		Project project = Project.load(path);
		
		if(project == null)
		{
			logger.debug("Failed to load project from path: " + path);
			return null;
		}
		
		if(projects.contains(project))
		{
			throw new InvalidStateException("A project specified name is already open: {}", project.getName());
		}
		
		projectExplorer.openProject(project);
		projects.add(project);
		return project;
	}

	public void deleteProject(Project project, boolean deleteContent)
	{
		if(!projects.contains(project))
		{
			logger.debug("Specified project is not found in open list. Ignoring project delete request: {}", project.getName());
			return;
		}
		
		projectExplorer.deleteProject(project);
		projects.remove(project);
		
		if(deleteContent)
		{
			try
			{
				project.deleteProjectContents();
			}catch(Exception ex)
			{
				JOptionPane.showMessageDialog(projectExplorer, "Failed to delete project '" + project.getName() +"'. \nError: " + ex);
			}
		}
		
		ideEventManager.raiseAsyncEvent(new ProjectRemovedEvent(project));
	}
	
	public Project getProject(String name)
	{
		Project res = projects.stream()
				.filter(proj -> name.equals(proj.getName()))
				.findFirst()
				.orElse(null);
		
		return res;
	}
	
	public Map<String, File> getAllProjectFolders()
	{
		return projects.stream()
				.collect(Collectors.toMap(proj -> proj.getName(), proj -> proj.getBaseFolder()));
	}
	
	public void updateProject(Project project)
	{
		project.save();

		//as the project name might have changed, remove existing one
		//  by reference and readd modified project
		this.projects.removeIf(proj -> (proj == project));
		this.projects.add(project);
		
		projectExplorer.reloadProjectNode(project);
	}
}
