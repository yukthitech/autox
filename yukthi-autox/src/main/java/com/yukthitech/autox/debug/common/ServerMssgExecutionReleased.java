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

import java.io.Serializable;

/**
 * Used when execution is release from pause state of debug point.
 * @author akranthikiran
 */
public class ServerMssgExecutionReleased implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Unique id representing current step or execution.
	 */
	private String executionId;
	
	public ServerMssgExecutionReleased(String executionId)
	{
		this.executionId = executionId;
	}

	public String getExecutionId()
	{
		return executionId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Exeuction Id: ").append(executionId);

		builder.append("]");
		return builder.toString();
	}
}
