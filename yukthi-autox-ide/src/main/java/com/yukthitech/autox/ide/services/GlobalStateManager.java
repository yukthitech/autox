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
package com.yukthitech.autox.ide.services;

import javax.swing.text.JTextComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.events.ActiveComponentChangedEvent;

/**
 * Manages global state of the ide, on which other components may depend up on.
 * 
 * @author akranthikiran
 */
@Service
public class GlobalStateManager
{
	@Autowired
	private IdeEventManager ideEventManager;
	
	/**
	 * Maintains the current active editor which has focus.
	 * This in turn is used by find operation.
	 */
	private JTextComponent activeComponent;
	
	public synchronized void focusGained(JTextComponent component)
	{
		if(activeComponent == component)
		{
			return;
		}
		
		this.activeComponent = component;
		ideEventManager.raiseAsyncEvent(new ActiveComponentChangedEvent(component));
	}
	
	public synchronized void componentRemoved(JTextComponent component)
	{
		if(this.activeComponent == component)
		{
			this.activeComponent = null;
			ideEventManager.raiseAsyncEvent(new ActiveComponentChangedEvent(null));
		}
	}
	
	public JTextComponent getActiveComponent()
	{
		return activeComponent;
	}
}
