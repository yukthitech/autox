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
package com.yukthitech.autox.debug.common;

import java.util.List;
import java.util.UUID;

/**
 * Used when debug points are added or removed.
 * @author akranthikiran
 */
public class ClientMssgDebugPoints extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Debug points to be used.
	 */
	private List<DebugPoint> debugPoints;
	
	public ClientMssgDebugPoints(List<DebugPoint> debugPoints)
	{
		super(UUID.randomUUID().toString());
		this.debugPoints = debugPoints;
	}

	public List<DebugPoint> getDebugPoints()
	{
		return debugPoints;
	}
}
