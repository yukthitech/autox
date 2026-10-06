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

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Asserts given value is either boolean true or string 'true'.
 * @author akiran
 */
@Executable(name = "assertTrue", group = Group.Common, message = "Asserts given value is either boolean true or string 'true'")
public class AssertTrueStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	@Param(description = "Value to be checked.", sourceType = SourceType.EXPRESSION)
	private Object value;

	public void setValue(Object value)
	{
		this.value = value;
	}

	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Checking the value for true [value : {}]", value);
		boolean isTrue = false;
		
		if(value instanceof Boolean)
		{
			isTrue = (Boolean) value;
		}
		else
		{
			isTrue = "true".equalsIgnoreCase(("" + value).trim());
		}
		
		exeLogger.debug("Found value to be: {}", isTrue);

		if(!isTrue)
		{
			AssertTrueStep actualStep = (AssertTrueStep) super.sourceStep;
			throw new AutoxValidationException(this, "Expression evaluated to be false: {}", 
					actualStep.value);
		}
	}

}
