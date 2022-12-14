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
package com.yukthitech.autox.ide.exeenv.debug;

import com.yukthitech.autox.ide.services.IIdeEvent;

/**
 * Raised when a debug point is added or removed.
 * @author akranthikiran
 */
public class DebugPointsChangedEvent implements IIdeEvent
{
	/**
	 * Project under which debug points are changed.
	 */
	private String projectName;

	public DebugPointsChangedEvent(String projectName)
	{
		this.projectName = projectName;
	}
	
	public String getProjectName()
	{
		return projectName;
	}
}
