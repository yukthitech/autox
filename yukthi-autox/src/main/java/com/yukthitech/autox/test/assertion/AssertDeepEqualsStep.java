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
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.DeepEqualsUtil;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Validation to Compare specified values for deep equality.
 * @author akiran
 */
@Executable(name = "assertDeepEquals", group = Group.Common, message = "Compares specified values for deep equality. This will not compare the java types, but compares only the structure.")
public class AssertDeepEqualsStep extends AbstractValidation
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
	 * If true, extra properties in actual will be ignored and will only ensure expected
	 * structure is found in actual object.
	 */
	@Param(description = "If true, extra properties in actual will be ignored and will only ensure expected structure is found in actual object. Default: false", required = false)
	private boolean ignoreExtraProperties = false;

	@Param(description = "If false, instead of checking for equality, check will be done for non equality. Default: true", required = false)
	private boolean checkEquality = true;
	
	@Param(description = "Failed path, if any, will be set on context with this attribute. Default: failedPath", attrName = true, defaultValue = "failedPath", required = false)
	private String failedPathAttr = "failedPath";

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

	public void setIgnoreExtraProperties(boolean ignoreExtraProperties)
	{
		this.ignoreExtraProperties = ignoreExtraProperties;
	}
	
	public void setCheckEquality(boolean checkEquality)
	{
		this.checkEquality = checkEquality;
	}

	public void setFailedPathAttr(String failedPathAttr)
	{
		this.failedPathAttr = failedPathAttr;
	}

	/**
	 * Gets the type.
	 *
	 * @param val the val
	 * @return the type
	 */
	private Class<?> getType(Object val)
	{
		if(val == null)
		{
			return null;
		}
		
		return val.getClass();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug(false, "Comparing values for deep-equality. <span style=\"white-space: pre-wrap\">[\nExpected: {} [{}], \nActual: {} [{}], \nIgnore exta Properties: {}]</span>", 
				expected, getType(expected),  
				actual, getType(actual),
				ignoreExtraProperties);
		
		String diffPath = DeepEqualsUtil.deepCompare(this.actual, this.expected, ignoreExtraProperties, context, exeLogger);
		boolean isEqual = (diffPath == null);
		
		context.setAttribute(failedPathAttr, diffPath);
		
		exeLogger.debug("Result of comparison is: {}", isEqual);
		
		if(!checkEquality)
		{
			isEqual = !isEqual;
			exeLogger.debug("As non-equality has to be checked, setting final result as: {}", isEqual);
		}
		
		if(!isEqual)
		{
			AssertDeepEqualsStep actualStep = (AssertDeepEqualsStep) super.sourceStep;
			throw new AutoxValidationException(this, "Found objects are unequal [Actual: {}, Expected: {}, Diff Path: {}]", 
					actualStep.actual, actualStep.expected, diffPath);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("ignoreExtraProperties: ").append(ignoreExtraProperties);
		builder.append(",").append("checkEquality: ").append(checkEquality);
		builder.append(",\n").append("expected: ").append(AutomationUtils.toJson(expected));
		builder.append(",\n").append("actual: ").append(AutomationUtils.toJson(actual));

		builder.append("]");
		return builder.toString();
	}

}
