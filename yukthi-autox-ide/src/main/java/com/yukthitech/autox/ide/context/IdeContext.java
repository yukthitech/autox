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

import org.springframework.stereotype.Component;

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
}
