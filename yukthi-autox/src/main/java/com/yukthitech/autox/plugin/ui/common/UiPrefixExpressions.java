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
package com.yukthitech.autox.plugin.ui.common;

import com.yukthitech.autox.plugin.ui.steps.UiSetValueStep;
import com.yukthitech.autox.prefix.IPropertyPath;
import com.yukthitech.autox.prefix.PrefixExprParam;
import com.yukthitech.autox.prefix.PrefixExpression;
import com.yukthitech.autox.prefix.PrefixExpressionContext;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Prefix expressions for ui.
 * 
 * @author akranthikiran
 */
public class UiPrefixExpressions
{
	@PrefixExpression(type = "uiVal", description = "Used to set/fetch value from ui element.", 
		example = "ui(driver=default): xpath: //input[@name='statusFld']",
		params = {
			@PrefixExprParam(name = "driver", type = "String", defaultValue = "Default driver", 
				description = "Driver to be used to access ui browser."),
			@PrefixExprParam(name = "parentElement", type = "String", defaultValue = "<none>", 
				description = "Parent locator under which current locator should be accessed."),
			@PrefixExprParam(name = "pressEnter", type = "String", defaultValue = "false", 
				description = "If true, post value population, enter key will be pressed on the element."),
			@PrefixExprParam(name = "displayValue", type = "String", defaultValue = "false", 
			 description = "If set to true, instead of value, display value will be fetched (currently non-select fields will return value itself).")

		})
	public IPropertyPath uiVal(PrefixExpressionContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				boolean successful = UiSetValueStep.setUiValue(parserContext.getAutomationContext(), 
						parserContext.getParameter("driver"), 
						parserContext.getParameter("parentElement"), 
						expression, 
						(String) value, 
						"true".equals(parserContext.getParameter("pressEnter")));
				
				if(!successful)
				{
					throw new InvalidStateException("Failed to set ui-value on element with locator: {}", expression);
				}
			}
			
			@Override
			public Object getValue() throws Exception
			{
				String locator = expression;
				String parentElement = parserContext.getParameter("parentElement");
				String driver = parserContext.getParameter("driver");
				boolean displayValue = "true".equals(parserContext.getParameter("displayValue"));
				
				return displayValue? 
						UiFreeMarkerMethods.uiDisplayValue(locator, parentElement, driver) 
						: UiFreeMarkerMethods.uiValue(locator, parentElement, driver);
			}
		};
	}
}
