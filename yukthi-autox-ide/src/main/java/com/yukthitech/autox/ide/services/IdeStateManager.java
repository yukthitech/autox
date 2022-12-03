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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.model.IdeState;

@Service
public class IdeStateManager
{
	private static Logger logger = LogManager.getLogger(IdeStateManager.class);
	
	private IdeState currentState;
	
	private IdeState loadState()
	{
		currentState = IdeState.load();
		return currentState;
	}
	
	public IdeState getState()
	{
		if(currentState == null)
		{
			loadState();
		}
		
		return currentState;
	}
	
	public void saveState(IdeState ideState)
	{
		logger.debug("Saving ide state..");
		ideState.save();
	}
}
