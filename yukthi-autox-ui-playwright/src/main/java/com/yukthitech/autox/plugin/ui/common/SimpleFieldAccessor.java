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

import java.util.List;

import com.yukthitech.autox.context.AutomationContext;

/**
 * Accessor to access value of simple field types like - TEXT, Text area, int, etc.
 * <p>
 * Playwright's fill/clear require the element to be visible. Selenium sendKeys works on
 * display:none fields as well. To keep Selenium-compatible behavior (including tests that
 * intentionally use hidden text inputs), invisible elements are populated via JS.
 */
public class SimpleFieldAccessor implements IFieldAccessor
{
	private static final String SET_VALUE_SCRIPT = 
			"(e, v) => { e.value = v; e.setAttribute('value', v); "
			+ "e.dispatchEvent(new Event('input', { bubbles: true })); "
			+ "e.dispatchEvent(new Event('change', { bubbles: true })); }";
	
	@Override
	public String getValue(AutomationContext context, UiElement element)
	{
		String tagName = element.getTagName();
		
		if("input".equals(tagName) || "textarea".equals(tagName))
		{
			// Prefer live property (same as Selenium getAttribute("value") for inputs)
			String value = element.getAttribute("value");
			return (value == null) ? "" : value.trim();
		}
		
		return element.getText().trim();
	}

	@Override
	public void setValue(String driverName, UiElement element, Object value)
	{
		AutomationContext context = AutomationContext.getInstance();
		String strValue = "" + value;
		
		// Playwright fill/clear/type require visibility; Selenium sendKeys does not.
		// Hidden (display:none / visibility:hidden) fields must use JS, matching Selenium fallback.
		if(!element.isVisible())
		{
			context.getExecutionLogger().debug("Element is not visible. Setting value using JS (selenium-compatible for hidden fields).");
			setValueByJs(element, strValue);
			return;
		}
		
		try
		{
			element.clear();
		}catch(Exception ex)
		{
			context.getExecutionLogger().debug("Ignoring error while trying to clear the field. Error: {}", "" + ex);
		}
		
		try
		{
			element.fill(strValue);
			return;
		}catch(Exception ex)
		{
			context.getExecutionLogger().debug("Failed to set the field value using fill(). Trying JS. Error: {}", "" + ex);
			
			try
			{
				setValueByJs(element, strValue);
			}catch(Exception ex1)
			{
				context.getExecutionLogger().debug("Failed to set the field value using JS. Throwing back the fill exception. Error: {}", "" + ex1);
				throw ex;
			}
		}
	}
	
	private void setValueByJs(UiElement element, String value)
	{
		element.evaluate(SET_VALUE_SCRIPT, value);
	}

	@Override
	public List<FieldOption> getOptions(AutomationContext context, UiElement element)
	{
		return null;
	}
}
