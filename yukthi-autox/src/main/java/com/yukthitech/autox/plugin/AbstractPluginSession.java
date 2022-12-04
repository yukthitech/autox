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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPluginSession<S extends IPluginSession, P extends IPlugin<?, S>> implements IPluginSession
{
	protected P parentPlugin;
	
	/**
	 * Used to maintain plugin related attributes.
	 */
	private Map<String, Object> attributes = Collections.synchronizedMap(new HashMap<>());

	public AbstractPluginSession(P parentPlugin)
	{
		this.parentPlugin = parentPlugin;
	}
	
	@Override
	public P getParentPlugin()
	{
		return parentPlugin;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void release()
	{
		parentPlugin.releaseSession((S) this);
	}
	
	public void setAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return attributes.get(name);
	}
	
	public Map<String, Object> getAttributes()
	{
		return attributes;
	}
}
