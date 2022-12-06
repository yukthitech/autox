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
public class ClientMssgExecuteSteps extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	private String executionId;

	private String stepsToExecute;
	
	/**
	 * In case steps to execute has functions to be reloaded, 
	 * then this field will be considered. If this field is not null,
	 * then functions will be loaded into this function, if not will
	 * be loaded to global context.
	 */
	private String targetTestSuite;

	public ClientMssgExecuteSteps(String executionId, String stepsToExecute, String targetTestSuite)
	{
		super(UUID.randomUUID().toString());
		
		if(StringUtils.isEmpty(stepsToExecute))
		{
			throw new InvalidArgumentException("Steps to execute cannot be empty");
		}

		if(StringUtils.isEmpty(executionId))
		{
			throw new InvalidArgumentException("Execution-id cannot be empty");
		}
		
		this.executionId = executionId;
		this.stepsToExecute = stepsToExecute;
		this.targetTestSuite = targetTestSuite;
	}
	
	public String getExecutionId()
	{
		return executionId;
	}

	public String getStepsToExecute()
	{
		return stepsToExecute;
	}

	public String getTargetTestSuite()
	{
		return targetTestSuite;
	}
}
