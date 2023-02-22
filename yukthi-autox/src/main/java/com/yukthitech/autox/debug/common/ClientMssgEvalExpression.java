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
 * Used to evaluate expression as part of current debug thread.
 * @author akiran
 */
public class ClientMssgEvalExpression extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	private String executionId;

	private String expression;

	public ClientMssgEvalExpression(String executionId, String expression)
	{
		super(UUID.randomUUID().toString());
		
		if(StringUtils.isEmpty(expression))
		{
			throw new InvalidArgumentException("Expresion cannot be empty");
		}

		if(StringUtils.isEmpty(executionId))
		{
			throw new InvalidArgumentException("Execution-id cannot be empty");
		}

		this.executionId = executionId;
		this.expression = expression;
	}
	
	public String getExecutionId()
	{
		return executionId;
	}

	public String getExpression()
	{
		return expression;
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
		builder.append(",").append("Eval Expression: ").append(StringUtils.left(expression, 100));

		builder.append("]");
		return builder.toString();
	}
}
