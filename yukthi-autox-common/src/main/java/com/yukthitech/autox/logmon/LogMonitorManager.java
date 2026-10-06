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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.context.AutomationContext;

public class LogMonitorManager
{
	private static LogMonitorManager instance;
	
	/**
	 * List of configured log monitors.
	 */
	private Map<String, ILogMonitor> logMonitors;

	private LogMonitorManager()
	{}
	
	public synchronized static LogMonitorManager getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		
		instance = new LogMonitorManager();
		List<ILogMonitor> monitors = AutomationContext.getInstance().getAppConfiguration().getLogMonitors();
		
		instance.logMonitors = new HashMap<>();
		
		if(CollectionUtils.isEmpty(monitors))
		{
			return instance;
		}
		
		monitors.forEach(monitor -> instance.logMonitors.put(monitor.getName(), monitor));
		return instance;
	}
	
	public Collection<ILogMonitor> getLogMonitors()
	{
		return logMonitors.values();
	}
}
