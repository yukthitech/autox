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
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Represents else-if block.
 * 
 * @author akiran
 */
@Executable(name = "elseIf", group = Group.Lang, partOf = IfConditionStep.class, message = "Represents steps to be executed based on condition when prior if condition fails.")
public class ElseIfStep extends AbstractContainerStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Freemarker condition to be evaluated.", required = true, sourceType = SourceType.CONDITION)
	private String condition;

	public void setCondition(String condition)
	{
		this.condition = condition;
	}
	
	public String getCondition()
	{
		return condition;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		throw new UnsupportedOperationException("else-if cannot be used without if block.");
	}
}
