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
package com.yukthitech.prism.model;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.prism.IdeUtils;

/**
 * Maintains the state of ide.
 * @author akiran
 */
public class IdeState implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(IdeState.class);

	/**
	 * File in which ide state will be persisted.
	 */
	private static final File IDE_STATE_FILE = new File("autox-ide.state");
	
	/**
	 * List of project currently open.
	 */
	private Set<ProjectState> openProjects = new LinkedHashSet<>();
	
	/**
	 * Custom attributes.
	 */
	private Map<String, Object> attributes = new HashMap<>();
	
	/**
	 * Ide settings.
	 */
	private IdeSettings ideSettings = new IdeSettings();
	
	/**
	 * Gets the list of project currently open.
	 *
	 * @return the list of project currently open
	 */
	public Set<ProjectState> getOpenProjects()
	{
		return Collections.unmodifiableSet(openProjects);
	}
	
	public ProjectState getProjectState(Project project)
	{
		if(CollectionUtils.isEmpty(openProjects))
		{
			return null;
		}
		
		return openProjects.stream()
				.filter(curState -> curState.getPath().equals(project.getProjectFilePath()))
				.findFirst()
				.orElse(null);
	}
	
	public void retainProjects(Collection<Project> projects)
	{
		Set<ProjectState> openProjects = new LinkedHashSet<>();
		
		for(Project project : projects)
		{
			ProjectState state = getProjectState(project);
			
			if(state == null)
			{
				state = createNewState(project);
			}
			
			openProjects.add(state);
		}
		
		this.openProjects = openProjects;
	}
	
	private ProjectState createNewState(Project project)
	{
		ProjectState state = new ProjectState(project.getProjectFilePath());
		
		//remove existing stale state, if any (which may exist because of loading from cache file)
		this.openProjects.remove(state);
		
		//add the fresh state
		this.openProjects.add(state);
		return state;
	}
	
	/**
	 * Adds open project to current state.
	 * @param project
	 */
	public ProjectState addOpenProject(Project project)
	{
		ProjectState state = getProjectState(project);
		
		if(state != null)
		{
			return state;
		}
		
		logger.debug("Adding project to ide state: {}", project.getName());
		return createNewState(project);
	}
	
	/**
	 * Sets the attribute with specified name and value.
	 * @param name
	 * @param value
	 */
	public void setAtribute(String name, Object value)
	{
		this.attributes.put(name, value);
	}
	
	/**
	 * Gets attributed with specified name.
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name)
	{
		return this.attributes.get(name);
	}
	
	public IdeSettings getIdeSettings()
	{
		return ideSettings;
	}
	
	/**
	 * Saves the current state of ide.
	 */
	public void save()
	{
		IdeUtils.serialize(this, IDE_STATE_FILE);
	}
	
	/**
	 * Loads the ide state.
	 * @return
	 */
	public static IdeState load()
	{
		IdeState savedState = (IdeState) IdeUtils.deserialize(IDE_STATE_FILE);
		
		if(savedState == null)
		{
			savedState = new IdeState();
		}
		
		if(savedState.ideSettings == null)
		{
			savedState.ideSettings = new IdeSettings();
		}
		
		return savedState;
	}
}
