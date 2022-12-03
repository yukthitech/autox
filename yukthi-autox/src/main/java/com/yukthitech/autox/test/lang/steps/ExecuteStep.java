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
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Used to execute specified expression.
 * @author akiran
 */
@Executable(name = "execute", group = Group.Lang, message = "Execute specified freemarker expression.")
public class ExecuteStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Expression to execute.
	 */
	@Param(description = "Expression to execute.")
	private String expression;
	
	/**
	 * If true, encloses the specified expression in ${}. Default: true.
	 */
	@Param(description = "If true, encloses the specified expression in ${}. Default: true")
	private boolean enclose = true;
	
	/**
	 * Sets the expression to execute.
	 *
	 * @param expression the new expression to execute
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}
	
	public void setEnclose(boolean enclose)
	{
		this.enclose = enclose;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		String finalExpr = enclose ? "${" + expression + "}" : expression;
		
		exeLogger.debug("Executing expression: {}", finalExpr);
		
		String output = AutomationUtils.replaceExpressionsInString("finalExpr", context, finalExpr);
		exeLogger.debug("Output of expression execution: {}", output);
	}
}
