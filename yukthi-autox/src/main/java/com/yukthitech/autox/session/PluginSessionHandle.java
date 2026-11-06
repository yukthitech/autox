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
package com.yukthitech.autox.session;

import java.util.function.Function;

import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.IPluginSession;

/**
 * Marshals plugin-session work onto the interactive live-executor thread.
 */
final class PluginSessionHandle<S extends IPluginSession> implements IPluginSessionHandle<S>
{
	private final AutoxSession session;
	
	private final Class<? extends IPlugin<?, S>> pluginType;
	
	PluginSessionHandle(AutoxSession session, Class<? extends IPlugin<?, S>> pluginType)
	{
		this.session = session;
		this.pluginType = pluginType;
	}
	
	@Override
	public <T> T execute(Function<S, T> func)
	{
		session.ensureOpen();
		session.ensureInteractive();
		
		return session.contextWrapper.execute(ctx ->
				func.apply(ctx.getExecutionContextManager().getPluginSession(pluginType)));
	}
}
