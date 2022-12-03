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
package com.yukthitech.autox.filter;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.context.AutomationContext;

/**
 * Context used by expression parsers.
 * @author akiran
 */
public class FilterContext
{
	/**
	 * Automation context being used.
	 */
	private AutomationContext automationContext;
	
	/**
	 * Last expression value in expression chain.
	 */
	private Object currentValue;
	
	/**
	 * Expression type parameters specified as part of expression.
	 */
	private String expressionTypeParameters[];
	
	/**
	 * Current parser in use.
	 */
	private ExpressionParserDetails currentParser;
	
	/**
	 * Parameters passed to expression.
	 */
	private Map<String, String> parameters = new HashMap<>();
	
	/**
	 * Default type expected out of expression.
	 */
	private Class<?> defaultExpressionType;

	/**
	 * Instantiates a new expression parser context.
	 *
	 * @param context the context
	 */
	public FilterContext(AutomationContext context, Object currentValue)
	{
		this.automationContext = context;
		this.currentValue = currentValue;
	}
	
	/**
	 * Sets the default type expected out of expression.
	 *
	 * @param defaultExpressionType the new default type expected out of expression
	 */
	void setDefaultExpressionType(Class<?> defaultExpressionType)
	{
		this.defaultExpressionType = defaultExpressionType;
	}
	
	/**
	 * Gets the default type expected out of expression.
	 *
	 * @return the default type expected out of expression
	 */
	public Class<?> getDefaultExpressionType()
	{
		return defaultExpressionType;
	}

	/**
	 * Sets the last expression value in expression chain.
	 *
	 * @param currentValue the new last expression value in expression chain
	 */
	void setCurrentValue(Object currentValue)
	{
		this.currentValue = currentValue;
	}

	/**
	 * Gets the automation context being used.
	 *
	 * @return the automation context being used
	 */
	public AutomationContext getAutomationContext()
	{
		return automationContext;
	}
	
	/**
	 * Gets the last expression value in expression chain.
	 *
	 * @return the last expression value in expression chain
	 */
	public Object getCurrentValue()
	{
		return currentValue;
	}
	
	/**
	 * Fetches effective context to be used for parsing expressions.
	 * @return effective context
	 */
	public Object getEffectiveContext()
	{
		return (currentValue == null) ? automationContext : currentValue;
	}
	
	/**
	 * Sets the expression type parameters specified as part of expression.
	 *
	 * @param expressionTypeParameters the new expression type parameters specified as part of expression
	 */
	void setExpressionTypeParameters(String[] expressionTypeParameters)
	{
		this.expressionTypeParameters = expressionTypeParameters;
	}
	
	/**
	 * Gets the expression type parameters specified as part of expression.
	 *
	 * @return the expression type parameters specified as part of expression
	 */
	public String[] getExpressionTypeParameters()
	{
		return expressionTypeParameters;
	}

	/**
	 * Gets the current parser in use.
	 *
	 * @return the current parser in use
	 */
	public ExpressionParserDetails getCurrentParser()
	{
		return currentParser;
	}

	/**
	 * Sets the current parser in use.
	 *
	 * @param currentParser the new current parser in use
	 */
	void setCurrentParser(ExpressionParserDetails currentParser)
	{
		this.currentParser = currentParser;
	}
	
	/**
	 * Clears the current parameters from the context.
	 */
	public void clearParameters()
	{
		parameters.clear();
	}
	
	/**
	 * Adds the parameter to the context.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public void addParameter(String name, String value)
	{
		parameters.put(name.toLowerCase(), value);
	}
	
	/**
	 * Fetches the parameter value with specified name.
	 * @param name name of param to fetch
	 * @return matching param value
	 */
	public String getParameter(String name)
	{
		return parameters.get(name.toLowerCase());
	}
}
