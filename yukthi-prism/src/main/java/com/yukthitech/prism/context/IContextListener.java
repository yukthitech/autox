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
package com.yukthitech.prism.context;

import com.yukthitech.prism.model.IdeState;

/**
 * Listener to listen to the events of the ide and take approp actions.
 * @author akiran
 */
public interface IContextListener
{
	/**
	 * Called before closing the ide. The data set on state will be persisted
	 * and during next ide start, the same state will be sent via {@link #loadState(IdeState)}
	 * @param state object on which target object needs to be set.
	 */
	public default void saveState(IdeState state)
	{}
	
	/**
	 * Invoked when ide is started by passing previously persisted state.
	 * @param state state to be loaded
	 */
	public default void loadState(IdeState state)
	{}
}
