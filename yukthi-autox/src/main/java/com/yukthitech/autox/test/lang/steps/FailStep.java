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
package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Fails the current test case by throwing fail exception.
 * @author akiran
 */
@Executable(name = "fail", group = Group.Lang, message = "Fails the current test case by throwing fail exception.")
public class FailStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Message to be used to fail test case or test suite.
	 */
	@Param(description = "Message to the fail exception to be thrown", required = false, sourceType = SourceType.EXPRESSION)
	private String message;
	
	/**
	 * Sets the message to be used to fail test case or test suite.
	 *
	 * @param message the new message to be used to fail test case or test suite
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		exeLogger.error("Failing the current test case with message: {}", message);
		
		if(message == null)
		{
			throw new FailException(this);
		}
		else
		{
			throw new FailException(this, message);
		}
	}
}
