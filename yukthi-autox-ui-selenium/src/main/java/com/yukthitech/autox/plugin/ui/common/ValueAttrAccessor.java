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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;

/**
 * Accessor to access value of simple field types like - TEXT, Text area, int, etc.
 */
public class ValueAttrAccessor implements IFieldAccessor
{
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getValue(org.openqa.selenium.WebElement)
	 */
	@Override
	public String getValue(AutomationContext context, WebElement element)
	{
		return element.getAttribute("value");
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#setValue(org.openqa.selenium.WebElement, java.lang.String)
	 */
	@Override
	public void setValue(String driverName, WebElement element, Object value)
	{
		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);
		
		try
		{
			((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", 
	                element, "value", "" + value);
		}catch(Exception ex)
		{
			AutomationContext.getInstance().getExecutionLogger().debug("Failed to set the field value using JS set attribute.", ex);
			throw ex;
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getOptions(org.openqa.selenium.WebElement)
	 */
	@Override
	public List<FieldOption> getOptions(AutomationContext context, WebElement element)
	{
		return null;
	}
}
