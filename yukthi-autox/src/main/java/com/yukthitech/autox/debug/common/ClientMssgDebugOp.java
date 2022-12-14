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
 * Used in interactive environments to execute steps.
 * @author akiran
 */
public class ClientMssgDebugOp extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	private String executionId;

	private DebugOp debugOp;

	public ClientMssgDebugOp(String executionId, DebugOp debugOp)
	{
		super(UUID.randomUUID().toString());
		
		if(StringUtils.isBlank(executionId))
		{
			throw new InvalidArgumentException("Invalid execution id specified");
		}
		
		if(debugOp == null)
		{
			throw new InvalidArgumentException("Debug op cannot be null");
		}
		
		this.executionId = executionId;
		this.debugOp = debugOp;
	}
	
	public String getExecutionId()
	{
		return executionId;
	}

	public DebugOp getDebugOp()
	{
		return debugOp;
	}
}
