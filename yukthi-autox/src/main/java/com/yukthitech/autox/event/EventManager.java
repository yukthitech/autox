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
package com.yukthitech.autox.event;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.IPluginSession;
import com.yukthitech.autox.plugin.PluginEvent;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Central managing points for events.
 * @author akranthikiran
 */
public class EventManager
{
	private static EventManager instance;
	
	private Set<String> supportedEventNames = new HashSet<>();
	
	private Map<String, EventHandler> eventHandlers = new HashMap<>();
	
	public static synchronized EventManager getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		
		instance = new EventManager();
		
		Collection<IPlugin<?, ?>> plugins = AutomationContext.getInstance().getAppConfiguration().getAllPlugins();
		
		for(IPlugin<?, ?> plugin : plugins)
		{
			PluginEvent events[] = plugin.getClass().getAnnotationsByType(PluginEvent.class);
			
			if(events.length == 0)
			{
				continue;
			}
			
			for(PluginEvent event : events)
			{
				instance.supportedEventNames.add(plugin.getName() + "." + event.name());
			}
		}
		
		return instance;
	}
	
	public synchronized void addEventHandler(EventHandler handler)
	{
		String name = handler.getName();
		
		if(!supportedEventNames.contains(name))
		{
			throw new InvalidStateException("Event handler is defined for unsupported event: {}", name);
		}
		
		if(eventHandlers.containsKey(name))
		{
			throw new InvalidStateException("Event handler for event '{}' already defined", name);
		}
				
		this.eventHandlers.put(name, handler);
	}
	
	public Object invokePluginEventHandler(IPluginSession pluginSession, String eventName, Map<String, Object> params)
	{
		params = (params == null) ? Collections.emptyMap() : params;
		
		String fullName = pluginSession.getParentPlugin().getName() + "." + eventName;
		EventHandler handler = eventHandlers.get(fullName);
		
		if(handler == null)
		{
			return null;
		}
		
		ExecutionContext context = ExecutionContextManager.getExecutionContext();
		context.setActivePlugin(pluginSession);
		
		try
		{
			return handler.execute(params);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while invoking plugin event-handler: {}", fullName, ex);
		}finally
		{
			context.setActivePlugin(null);
		}
	}
}
