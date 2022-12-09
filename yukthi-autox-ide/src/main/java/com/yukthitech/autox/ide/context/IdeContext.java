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
package com.yukthitech.autox.ide.context;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.utils.event.EventListenerManager;

/**
 * Context of the ide.
 * @author akiran
 */
@Component
public class IdeContext
{
	/**
	 * Event listener manager for managing events.
	 */
	private EventListenerManager<IContextListener> eventListenerManager = EventListenerManager.newEventListenerManager(IContextListener.class, false);

	/**
	 * Currently active project.
	 */
	private Project activeProject;
	
	/**
	 * Current active file.
	 */
	private File activeFile;
	
	/**
	 * List of files currently selected.
	 */
	private List<File> selectedFiles;
	
	public IdeContext()
	{
	}
	
	/**
	 * Adds specified listener to the context.
	 * @param listener listener to add.
	 */
	public void addContextListener(IContextListener listener)
	{
		eventListenerManager.addListener(listener);
	}
	
	/**
	 * Fetches proxy which can be used to execute listener method.
	 * @return proxy representing all listeners
	 */
	public IContextListener getProxy()
	{
		return eventListenerManager.get();
	}
	
	public void setActiveDetails(Project project, File file)
	{
		this.setActiveDetails(project, file, null);
	}
	
	public void setActiveDetails(Project project, File file, List<File> selectedFiles)
	{
		this.activeProject = project;
		this.activeFile = file;
		
		this.selectedFiles = selectedFiles;
	}

	/**
	 * Gets the currently active project.
	 *
	 * @return the currently active project
	 */
	public Project getActiveProject()
	{
		return activeProject;
	}
	
	/**
	 * Gets the current active file.
	 *
	 * @return the current active file
	 */
	public File getActiveFile()
	{
		return activeFile;
	}
	
	/**
	 * Gets the list of files currently selected.
	 *
	 * @return the list of files currently selected
	 */
	public List<File> getSelectedFiles()
	{
		return selectedFiles;
	}
}
