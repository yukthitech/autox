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
package com.yukthitech.autox.debug;

import java.io.Serializable;

/**
 * Outbound channel for debug / interactive server messages ({@code ServerMssg*} types
 * used by Prism). Implementations must deliver events <b>off</b> the paused execution
 * thread when the host may call resume/step from the listener (avoids deadlock).
 *
 * @author akranthikiran
 */
public interface IDebugEventSink
{
	/**
	 * Delivers a server-side debug event to the host / client.
	 *
	 * @param event typically a {@code ServerMssg*} instance
	 */
	void onEvent(Serializable event);
	
	/**
	 * Releases sink resources. Idempotent.
	 */
	default void close()
	{}
}
