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

import com.yukthitech.autox.AbstractContainerStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Represents catch block.
 * 
 * @author akiran
 */
@Executable(name = "catch", group = Group.Lang, partOf = TryStep.class, message = "Represents steps to be executed on error. This step has to be preceeded by try-step.")
public class CatchStep extends AbstractContainerStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Attribute name for error. Default: error.
	 */
	@Param(description = "Attribute name for error. Default: error", attrName = true)
	private String errorAttr = "error";
	
	public void setErrorAttr(String errorAttr)
	{
		this.errorAttr = errorAttr;
	}
	
	public String getErrorAttr()
	{
		return errorAttr;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		throw new UnsupportedOperationException("Catch cannot be used without try block.");
	}
}
