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
package com.yukthitech.autox.plugin;

/**
 * Interface indicating the target object is Plugin.
 * @author akiran
 */
public interface IPlugin<AT, S extends IPluginSession>
{
	/**
	 * Fetches the argument bean type required by this configuration. Can be null. Command line arguments will be mapped
	 * to instance of this type and will be passed during initialization. 
	 * @return required argument bean
	 */
	public default Class<AT> getArgumentBeanType()
	{
		return null;
	}
	
	/**
	 * Called by framework once all configurations and test suites are loaded
	 * and before executing test suites.
	 * @param context Current automation context.
	 * @param args Mapped command line arguments bean
	 */
	public default void initialize(AT args)
	{}
	
	public S newSession();
	
	/**
	 * Will be called by session itself during release.
	 * @param session
	 */
	public void releaseSession(S session);
	
	/**
	 * Called when close is called on global execution context. Which generally happens before destroying
	 * global execution context. And this should take care of closing any open/cached sessions.
	 */
	public default void close()
	{
	}
	
	/**
	 * Returns unique name of plugin.
	 * @return
	 */
	public String getName();
}
