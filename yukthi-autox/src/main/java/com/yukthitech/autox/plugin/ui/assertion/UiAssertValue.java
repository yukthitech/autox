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
package com.yukthitech.autox.plugin.ui.assertion;

import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.common.UiFreeMarkerMethods;
import com.yukthitech.autox.test.TestCaseFailedException;

/**
 * Waits for locator to be part of the page and is visible.
 * 
 * @author akiran
 */
@Executable(name = "uiAssertValue", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Validates specified element has specified value/text")
public class UiAssertValue extends AbstractParentUiAssert
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator of which value needs to be validated.
	 */
	@Param(description = "Locator of the element to be validated.", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	/**
	 * Value expected in the target element.
	 */
	@Param(description = "Expected value of the element.")
	private String value;

	@Param(description = "If set to true, instead of value, display value will be fetched (currently non-select fields will return value itself).", required = false)
	private boolean displayValue = false;

	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public void setDisplayValue(boolean displayValue)
	{
		this.displayValue = displayValue;
	}
	
	/**
	 * Execute.
	 *
	 * @param context
	 *            the context
	 * @param exeLogger
	 *            the exe logger
	 * @return true, if successful
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		if(!"true".equals(enabled))
		{
			exeLogger.debug("Current validation is disabled. Skipping validation execution.");
			return;
		}
		
		exeLogger.trace("Validating if locator '{}' has value - {}", getLocatorWithParent(locator), value);

		try
		{
			String elementValue = displayValue? 
					UiFreeMarkerMethods.uiDisplayValue(locator, parentElement, driverName) : 
					UiFreeMarkerMethods.uiValue(locator, parentElement, driverName);
	
			if(!value.equals(elementValue))
			{
				exeLogger.error("Expected value '{}' is not matching with actual value '{}' for locator: {}", value, elementValue, getLocatorWithParent(locator));
				throw new AutoxValidationException(this, "Expected value '{}' is not matching with actual value '{}' for locator: {}", value, elementValue, getLocatorWithParent(locator));
			}
		} catch (AutoxValidationException ex) 
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TestCaseFailedException(this, "Failed to fetch the value. Error: {}", "" + ex, ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String comma = ", ";
		StringBuilder builder = new StringBuilder();
		builder.append("Assert Value [");

		builder.append("Locator: ").append(locator);
		builder.append(comma).append("Value: ").append(value);

		builder.append("]");
		return builder.toString();
	}
}
