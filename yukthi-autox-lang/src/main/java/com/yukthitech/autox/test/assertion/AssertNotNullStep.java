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
package com.yukthitech.autox.test.assertion;

import java.util.Objects;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Asserts the value for not null.
 * @author akiran
 */
@Executable(name = "assertNotNull", group = Group.Common, message = "Asserts the specified value is not null.")
public class AssertNotNullStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Value to check.
	 */
	@Param(description = "Value to check.", sourceType = SourceType.EXPRESSION)
	private Object value;

	/**
	 * Sets the value to check.
	 *
	 * @param value the new value to check
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Checking the value is not null [value : {}]", value);
		
		boolean nonNull = Objects.nonNull(value);
		exeLogger.debug("Found value to be non-null: {}", nonNull);
		
		if(!nonNull)
		{
			AssertNotNullStep actualStep = (AssertNotNullStep) super.sourceStep;
			throw new AutoxValidationException(this, "Found specified value to be null: {}", 
					actualStep.value);
		}
	}
}
