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

import com.yukthitech.autox.plugin.IPluginSession;

/**
 * Handle to a plugin session that runs work on the interactive live-executor thread.
 * Prefer this over holding a raw {@link IPluginSession} across threads (e.g. Playwright).
 */
public interface IPluginSessionHandle<S extends IPluginSession>
{
	/**
	 * Invokes {@code func} with the live plugin session on the interactive worker thread.
	 */
	<T> T execute(Function<S, T> func);
}
