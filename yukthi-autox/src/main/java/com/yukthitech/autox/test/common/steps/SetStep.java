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
package com.yukthitech.autox.test.common.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;

/**
 * Sets the specified context attribute with specified value.
 * 
 * @author akiran
 */
@Executable(name = "set", group = Group.Common, message = "Sets the specified value using specified expression")
public class SetStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expression to be used to set the value.
	 */
	@Param(description = "Expression to be used to set the value.", required = true, sourceType = SourceType.EXPRESSION_PATH, attrName = true)
	private String expression;

	/**
	 * Value of the attribute to set.
	 */
	@Param(description = "Value of the attribute to set. Default: empty string", required = false, sourceType = SourceType.EXPRESSION)
	private Object value = "";

	/**
	 * Sets the expression to be used to set the value.
	 *
	 * @param expression the new expression to be used to set the value
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	/**
	 * Sets the value of the attribute to set.
	 *
	 * @param value the new value of the attribute to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		PrefixExpressionFactory.getExpressionFactory().setExpressionValue(context, expression, value);
	}
}
