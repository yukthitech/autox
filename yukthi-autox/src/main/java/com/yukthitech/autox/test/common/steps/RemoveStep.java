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

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * Removes the specified context attribute.
 * 
 * @author akiran
 */
@Executable(name = "remove", group = Group.Common, message = "Removes the specified context attribute or values matching with specified path.")
public class RemoveStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of attribute to set.
	 */
	@Param(description = "Name of the attribute to remove.", required = false)
	private String name;
	
	@Param(description = "Expression to be used to remove the values. Currently supported expressions: xpath, attr, store", required = false)
	private String expression;

	/**
	 * Sets the name of attribute to set.
	 *
	 * @param name the new name of attribute to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		if(StringUtils.isNotBlank(name))
		{
			exeLogger.debug("Removing context attribute '{}'", name);
			context.removeAttribute(name);
		}
		
		if(StringUtils.isNotBlank(expression))
		{
			exeLogger.debug("Removing data using expression: {}", expression);
			PrefixExpressionFactory.getExpressionFactory().removeByExpression(context, expression);	
		}
	}
	
	@Override
	public void validate() throws ValidateException
	{
		super.validate();
		
		if(StringUtils.isBlank(name) && StringUtils.isBlank(expression))
		{
			throw new ValidateException("Both name and expression are not specified.");
		}
	}
}
