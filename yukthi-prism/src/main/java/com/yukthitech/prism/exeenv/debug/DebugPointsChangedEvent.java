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
package com.yukthitech.prism.exeenv.debug;

import java.util.List;

import com.yukthitech.prism.services.IIdeEvent;

/**
 * Raised when a debug point is added or removed.
 * @author akranthikiran
 */
public class DebugPointsChangedEvent implements IIdeEvent
{
	/**
	 * Source which is responsible for generating this event.
	 */
	private Object source;
	
	/**
	 * Operation which resulted in this event.
	 */
	private DebugPointOp operation;
	
	/**
	 * Debug points which got affected (added/modified/removed).
	 */
	private List<IdeDebugPoint> debugPoints;
	
	/**
	 * Instantiates a new debug points changed event.
	 *
	 * @param source source which is responsible for generating this event.
	 * @param operation operation which resulted in this event.
	 * @param debugPoints debug points which got affected
	 *        (added/modified/removed).
	 */
	public DebugPointsChangedEvent(Object source, DebugPointOp operation, List<IdeDebugPoint> debugPoints)
	{
		this.source = source;
		this.operation = operation;
		this.debugPoints = debugPoints;
	}
	
	/**
	 * Gets the source which is responsible for generating this event.
	 *
	 * @return the source which is responsible for generating this event
	 */
	public Object getSource()
	{
		return source;
	}

	/**
	 * Gets the operation which resulted in this event.
	 *
	 * @return the operation which resulted in this event
	 */
	public DebugPointOp getOperation()
	{
		return operation;
	}

	/**
	 * Gets the debug points which got affected (added/modified/removed).
	 *
	 * @return the debug points which got affected (added/modified/removed)
	 */
	public List<IdeDebugPoint> getDebugPoints()
	{
		return debugPoints;
	}
}
