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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
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
 * Validation to Compare specified values for equality.
 * @author akiran
 */
@Executable(name = "assertEquals", group = Group.Common, message = "Compares specified values for euqality.")
public class AssertEqualsStep extends AbstractValidation
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Expected value in comparison..
	 */
	@Param(description = "Expected value in comparison.", sourceType = SourceType.EXPRESSION)
	private Object expected;

	/**
	 * Actual value in comparison.
	 */
	@Param(description = "Actual value in comparison", sourceType = SourceType.EXPRESSION)
	private Object actual;

	/**
	 * Sets the expected value in comparison..
	 *
	 * @param expected the new expected value in comparison
	 */
	public void setExpected(Object expected)
	{
		this.expected = expected;
	}

	/**
	 * Sets the actual value in comparison.
	 *
	 * @param actual the new actual value in comparison
	 */
	public void setActual(Object actual)
	{
		this.actual = actual;
	}

	/**
	 * Gets the type.
	 *
	 * @param val the val
	 * @return the type
	 */
	@SuppressWarnings("rawtypes")
	static String getType(Object val)
	{
		if(val == null)
		{
			return null;
		}
		
		String type = val.getClass().getName();
		
		if(val instanceof Collection)
		{
			type += " [Size: " + ((Collection) val).size() + "]";
		}
		else if(val instanceof Map)
		{
			type += " [Size: " + ((Map) val).size() + "]";
		}
		
		return type;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.info(false, "Comparing values for equality. <span style=\"white-space: pre-wrap\">[Expected: {} [{}], Actual: {} [{}]]</span>", 
				expected, getType(expected),  
				actual, getType(actual));

		boolean isEqual = false;
		
		if((expected instanceof byte[]) && (actual instanceof byte[]))
		{
			exeLogger.debug("Performing byte-array comparison..");
			isEqual = Arrays.equals((byte[]) expected, (byte[]) actual);
		}
		else
		{
			isEqual = Objects.equals(expected, actual);
		}
		
		exeLogger.debug("Result of comparison is: {}", isEqual);
		
		if(!isEqual)
		{
			AssertEqualsStep actualStep = (AssertEqualsStep) super.sourceStep;
			throw new AutoxValidationException(this, "Found objects are unequal [Actual: {}, Expected: {}]", 
					actualStep.actual, actualStep.expected);
		}
	}
}
