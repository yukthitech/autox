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
package com.yukthitech.autox.test.ui.steps;

import java.beans.PropertyDescriptor;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Step to fill the target form with specified data.
 * 
 * @author akiran
 */
@Executable(name = "uiFillForm", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Fills the form with specified data")
public class FillFormStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Html locator of the form or container (like DIV) enclosing the input
	 * elements.
	 */
	@Param(description = "Html locator of the form or container (like DIV) enclosing the input elements", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	/**
	 * Data to be filled. All the fields matching with the property names of
	 * specified bean will be searched and populated with corresponding data.
	 */
	@Param(description = "Data to populate in the form", sourceType = SourceType.EXPRESSION)
	private Object data;
	
	/**
	 * Delay in millis time between field to field filling. Useful in forms, in which field options are fetched based on other field values. Default: 10.
	 */
	@Param(description = "Delay in millis time between field to field filling. Useful in forms, in which field options are fetched based on other field values. Default: 10", required = false)
	private int delay = 10;
	
	/**
	 * Fills the form using standard bean properties.
	 * 
	 * @param context
	 *            Automation context.
	 * @param exeLogger
	 *            Logger to be used.
	 */
	private void fillWithStandardBean(Object data, AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Filling form '{}' with standard bean - {}", locator, data);
		
		WebElement parentElement = UiAutomationUtils.findElement(driverName, (WebElement) null, locator);

		PropertyDescriptor propDescLst[] = PropertyUtils.getPropertyDescriptors(data.getClass());
		Object value = null;

		for(PropertyDescriptor desc : propDescLst)
		{
			// ignore java core properties like - getClass()
			if(desc.getReadMethod() == null || desc.getReadMethod().getDeclaringClass().getName().startsWith("java."))
			{
				continue;
			}

			try
			{
				value = PropertyUtils.getProperty(data, desc.getName());

				if(value == null)
				{
					continue;
				}
			} catch(Exception ex)
			{
				exeLogger.error("An error occurred while setting property - " + desc.getName(), ex);
				continue;
			}

			exeLogger.debug("Populating field {} with value - {}", desc.getName(), value);

			if(!UiAutomationUtils.populateField(driverName, parentElement, desc.getName(), value))
			{
				exeLogger.error("Failed to fill element '{}' under parent '{}' with value - {}", desc.getName(), locator, value);
				throw new TestCaseFailedException(this, "Failed to fill element '{}' under parent '{}' with value - {}", desc.getName(), locator, value);
			}
			
			try
			{
				Thread.sleep(delay);
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while sleeping for specified time: {}", delay, ex);
			}
		}
	}

	/**
	 * Fills the form using dynamic bean.
	 * 
	 * @param context
	 *            Automation context
	 * @param exeLogger
	 *            logger
	 */
	private void fillWithMap(Map<String, Object> properties, AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Filling form '{}' with dynamic bean - {}", locator, data);
		
		WebElement parentElement = UiAutomationUtils.findElement(driverName, (WebElement) null, locator);

		Object value = null;

		for(String name : properties.keySet())
		{
			value = properties.get(name);

			// ignore java core properties like - getClass()
			if(properties.get(name) == null)
			{
				continue;
			}

			exeLogger.debug("Populating field '{}' with value - {}", name, value);
			
			if(!UiAutomationUtils.populateField(driverName, parentElement, name, value))
			{
				exeLogger.error("Failed to fill element '{}' under parent '{}' with value - {}", name, locator, value);
				throw new TestCaseFailedException(this, "Failed to fill element '{}' under parent '{}' with value - {}", name, locator, value);
			}
			
			try
			{
				Thread.sleep(delay);
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while sleeping for specified time: {}", delay, ex);
			}
		}
	}

	/**
	 * Loops throw the properties specified data bean and populates the fields
	 * with matching names.
	 * 
	 * @param context
	 *            Current automation context
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		Object resObj = AutomationUtils.parseObjectSource(context, exeLogger, data, null);
		
		if(resObj instanceof Map)
		{
			fillWithMap((Map<String, Object>) resObj, context, exeLogger);
		}
		else
		{
			fillWithStandardBean(resObj, context, exeLogger);
		}
	}

	/**
	 * Sets the html locator of the form or container (like DIV) enclosing the
	 * input elements.
	 *
	 * @param locator
	 *            the new html locator of the form or container (like DIV)
	 *            enclosing the input elements
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Sets the data to be filled. All the fields matching with the property
	 * names of specified bean will be searched and populated with corresponding
	 * data.
	 *
	 * @param data
	 *            the new data to be filled
	 */
	@ChildElement(description = "Data to fill")
	public void setData(Object data)
	{
		this.data = data;
	}
	
	/**
	 * Sets the delay in millis time between field to field filling. Useful in forms, in which field options are fetched based on other field values. Default: 10.
	 *
	 * @param delay the new delay in millis time between field to field filling
	 */
	public void setDelay(int delay)
	{
		if(delay <= 0)
		{
			throw new InvalidArgumentException("Delay should be greater than zero - {}", delay);
		}
		
		this.delay = delay;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Fill Form [");

		builder.append("Locator: ").append(locator);
		builder.append(",").append("Delay: ").append(delay);

		builder.append("]");
		return builder.toString();
	}
}
