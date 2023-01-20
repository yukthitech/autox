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
package com.yukthitech.autox.prefix;

import com.yukthitech.autox.test.CustomPrefixExpression;

/**
 * Expression path wrapper over custom prefix expression functions.
 * @author akranthikiran
 */
class CustomPrefixExpressionWrapper extends PrefixEpression
{
	private CustomPrefixExpression customPrefixExpression;
	
	private PrefixExpressionContext context;
	
	private String expression;
	
	public CustomPrefixExpressionWrapper(CustomPrefixExpression customPrefixExpression, PrefixExpressionContext context, String expression)
	{
		this.customPrefixExpression = customPrefixExpression;
		this.context = context;
		this.expression = expression;
	}

	@Override
	public Object getValue() throws Exception
	{
		return customPrefixExpression.getValue(context, expression);
	}

	@Override
	public void setValue(Object value) throws Exception
	{
		customPrefixExpression.setValue(context, expression, value);
	}

	@Override
	public void removeValue() throws Exception
	{
		customPrefixExpression.removeValue(context, expression);
	}
	
	@Override
	public String getPrefix()
	{
		return "custom";
	}
}
