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
 * Asserts the value to be null.
 * @author akiran
 */
@Executable(name = "assertNull", group = Group.Common, message = "Asserts the value to be null.")
public class AssertNullStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Value to check.
	 */
	@Param(description = "Value to check.", sourceType = SourceType.EXPRESSION)
	private Object value;

	public void setValue(Object value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Checking the value is null [value : {}]", value);
		boolean isNull = Objects.isNull(value);
		exeLogger.debug("Found object to be null: {}", isNull);


		if(!isNull)
		{
			AssertNullStep actualStep = (AssertNullStep) super.sourceStep;
			throw new AutoxValidationException(this, "Found specified object to be non-null: {}", 
					actualStep.value);
		}
	}
}
