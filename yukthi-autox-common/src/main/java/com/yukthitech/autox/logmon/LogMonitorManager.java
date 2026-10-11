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
package com.yukthitech.autox.logmon;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

public class LogMonitorManager
{
	/**
	 * List of configured log monitors.
	 */
	private Map<String, ILogMonitor> logMonitors = new HashMap<>();

	public LogMonitorManager(List<ILogMonitor> monitors)
	{
		if(CollectionUtils.isEmpty(monitors))
		{
			return;
		}
		
		monitors.forEach(monitor -> logMonitors.put(monitor.getName(), monitor));
	}
	
	public void reset()
	{
		logMonitors.clear();
	}
	
	public Collection<ILogMonitor> getLogMonitors()
	{
		return Collections.unmodifiableCollection(logMonitors.values());
	}
}
