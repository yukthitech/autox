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
package com.yukthitech.prism.events;

import java.io.File;

import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.services.IIdeEvent;

/**
 * Event indicating active file is change.
 * @author akranthikiran
 */
public class ActiveFileChangedEvent implements IIdeEvent
{
	private Project project;
	
	private File file;
	
	/**
	 * Source generating this event.
	 */
	private Object source;

	public ActiveFileChangedEvent(Project project, File file, Object source)
	{
		this.project = project;
		this.file = file;
		this.source = source;
	}

	public Project getProject()
	{
		return project;
	}

	public File getFile()
	{
		return file;
	}

	public Object getSource()
	{
		return source;
	}
}
