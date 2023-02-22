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

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;

/**
 * Used in debug environment to drop current execution in a particular stack-element frame.
 * @author akiran
 */
public class ClientMssgDropToFrame extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	private String executionId;

	private String stackElementId;

	public ClientMssgDropToFrame(String executionId, String stackElementId)
	{
		super(UUID.randomUUID().toString());
		
		if(StringUtils.isBlank(executionId))
		{
			throw new InvalidArgumentException("Invalid execution id specified");
		}
		
		if(StringUtils.isBlank(stackElementId))
		{
			throw new InvalidArgumentException("Invalid stack-element id specified");
		}
		
		this.executionId = executionId;
		this.stackElementId = stackElementId;
	}
	
	public String getExecutionId()
	{
		return executionId;
	}

	public String getStackElementId()
	{
		return stackElementId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Request Id: ").append(super.getRequestId());
		builder.append(",").append("Execution Id: ").append(executionId);
		builder.append(",").append("Stack Element Id: ").append(stackElementId);

		builder.append("]");
		return builder.toString();
	}
}
