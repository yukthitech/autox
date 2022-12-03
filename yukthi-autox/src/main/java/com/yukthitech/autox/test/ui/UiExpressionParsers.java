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
package com.yukthitech.autox.test.ui;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.filter.ExpressionFilter;
import com.yukthitech.autox.filter.FilterContext;
import com.yukthitech.autox.filter.IPropertyPath;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Ui related expression parsers.
 * @author akiran
 */
public class UiExpressionParsers
{
	@ExpressionFilter(type = "uival", description = "Parses provided exprssion as ui locator and fetches/sets its value.", example = "uival: id:name")
	public IPropertyPath propertyParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				Object parent = parserContext.getCurrentValue();
				
				if(parent != null && !(parent instanceof String) && !(parent instanceof WebElement))
				{
					throw new InvalidArgumentException("Invalid/incompatible parent specified from piped input. Input: {}", parent);
				}
				
				if(parent == null)
				{
					UiAutomationUtils.populateField(parserContext.getParameter("driverName"), (WebElement) null, expression, value);
				}
				else if(parent instanceof String)
				{
					UiAutomationUtils.populateField(parserContext.getParameter("driverName"), (String) parent, expression, value);
				}
				else
				{
					UiAutomationUtils.populateField(parserContext.getParameter("driverName"), (WebElement) parent, expression, value);
				}
			}
			
			@Override
			public Object getValue() throws Exception
			{
				Object parent = parserContext.getCurrentValue();
				
				if(parent != null && !(parent instanceof String) && !(parent instanceof WebElement))
				{
					throw new InvalidArgumentException("Invalid/incompatible parent specified from piped input. Input: {}", parent);
				}
				
				WebElement element = null;
				
				if(parent == null)
				{
					element = UiAutomationUtils.findElement(parserContext.getParameter("driverName"), (String) null, expression);
				}
				else if(parent instanceof String)
				{
					element = UiAutomationUtils.findElement(parserContext.getParameter("driverName"), (String) parent, expression);
				}
				else
				{
					element = UiAutomationUtils.findElement(parserContext.getParameter("driverName"), (WebElement) parent, expression);
				}
				
				String value = null;
				
				if("input".equals(element.getTagName().toLowerCase()))
				{
					value = element.getAttribute("value").trim();
				}
				else
				{
					value = element.getText().trim();
				}
				
				return value;
			}
		};
	}

}
