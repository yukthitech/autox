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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Field accessor for select elements.
 * @author akiran
 */
public class SelectFieldAccessor implements IFieldAccessor
{
	private static Logger logger = LogManager.getLogger(SelectFieldAccessor.class);
	
	/**
	 * Pattern to find how to populate the value.
	 */
	private static final Pattern VALUE_PATTERN = Pattern.compile("(\\w+)\\s*\\:\\s*(.+)");
	
	/**
	 * Value prefix when selection should be done based on index.
	 */
	private static final String BY_INDEX = "index";
	
	/**
	 * Value prefix when selection should be done based on label.
	 */
	private static final String BY_LABEL = "label";
	
	/**
	 * Value prefix when selection should be done based on value.
	 */
	private static final String BY_VALUE = "value";
	
	/** 
	 * The invalid message. 
	 **/
	private static String INVALID_MESSAGE = "Invalid select element specified - {}";		
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getValue(org.openqa.selenium.WebElement)
	 */
	@Override
	public String getValue(AutomationContext context, WebElement element)
	{
		if(element instanceof Select)
		{
			throw new InvalidArgumentException(INVALID_MESSAGE, element);
		}

		WebElement selectedOption =  new Select(element).getFirstSelectedOption();
		
		if(selectedOption == null)
		{
			return null;
		}
		
		return selectedOption.getAttribute(BY_VALUE );
	}
	
	@Override
	public String getDisplayValue(AutomationContext context, WebElement element)
	{
		if(element instanceof Select)
		{
			throw new InvalidArgumentException(INVALID_MESSAGE, element);
		}

		WebElement selectedOption =  new Select(element).getFirstSelectedOption();
		
		if(selectedOption == null)
		{
			return null;
		}
		
		return selectedOption.getText();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#setValue(org.openqa.selenium.WebElement, java.lang.String)
	 */
	@Override
	public void setValue(String driverName, WebElement element, Object valueObj)
	{
		if(element instanceof Select)
		{
			throw new InvalidArgumentException(INVALID_MESSAGE, element);
		}

		String value = "" + valueObj;
		
		Select select =  new Select(element);
		Matcher matcher = VALUE_PATTERN.matcher(value);
		String type = BY_VALUE;

		if(matcher.matches())
		{
			type = matcher.group(1);
			value = matcher.group(2);
		}
		
		if(BY_INDEX.equals(type))
		{
			select.selectByIndex(Integer.parseInt(type));
		}
		else if(BY_LABEL.equals(type))
		{
			select.selectByVisibleText(value);
		}
		else if(BY_VALUE.equals(type))
		{
			select.selectByValue(value);
		}
		else
		{
			AutomationContext automationContext = AutomationContext.getInstance();
			IExecutionLogger exeLogger = automationContext.getExecutionLogger();
			
			try
			{
				select.selectByValue(value);
			}catch(NoSuchElementException ex)
			{
				if(exeLogger != null)
				{
					exeLogger.debug(null, "As no select option is found with value '{}' trying to select option using label with this value.", value);
				}
				else
				{
					logger.debug("As no select option is found with value '{}' trying to select option using label with this value.", value);
				}
				
				select.selectByVisibleText(value);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getOptions(org.openqa.selenium.WebElement)
	 */
	@Override
	public List<FieldOption> getOptions(AutomationContext context, WebElement element)
	{
		if(element instanceof Select)
		{
			throw new InvalidArgumentException(INVALID_MESSAGE, element);
		}

		Select select = new Select(element);
		List<WebElement> options = select.getOptions();
		
		if(options == null || options.isEmpty())
		{
			return null;
		}
		
		List<FieldOption> fieldOptions = new ArrayList<>(options.size());
		
		for(WebElement optElement : options)
		{
			fieldOptions.add(new FieldOption(optElement.getAttribute(BY_VALUE ), optElement.getText()));
		}
		
		return fieldOptions;
	}
}
