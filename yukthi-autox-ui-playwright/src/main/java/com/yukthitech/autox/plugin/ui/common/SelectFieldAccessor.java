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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	 * Script to fetch the selected option details.
	 */
	private static final String SELECTED_OPTION_SCRIPT = 
			"e => { if(!e.options || e.selectedIndex < 0) { return null; } "
			+ "var o = e.options[e.selectedIndex]; return { value: o.value, label: o.text.trim() }; }";

	/**
	 * Script to fetch all options.
	 */
	private static final String ALL_OPTIONS_SCRIPT = 
			"e => Array.from(e.options || []).map(o => ({ value: o.value, label: o.text.trim() }))";
	
	/** 
	 * The invalid message. 
	 **/
	private static String INVALID_MESSAGE = "Invalid select element specified - {}";
	
	private void validate(UiElement element)
	{
		if(!"select".equals(element.getTagName()))
		{
			throw new InvalidArgumentException(INVALID_MESSAGE, element);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getSelectedOption(UiElement element)
	{
		validate(element);
		return (Map<String, Object>) element.evaluate(SELECTED_OPTION_SCRIPT);
	}
	
	@Override
	public String getValue(AutomationContext context, UiElement element)
	{
		Map<String, Object> selectedOption = getSelectedOption(element);
		
		if(selectedOption == null)
		{
			return null;
		}
		
		return (String) selectedOption.get(BY_VALUE);
	}
	
	@Override
	public String getDisplayValue(AutomationContext context, UiElement element)
	{
		Map<String, Object> selectedOption = getSelectedOption(element);
		
		if(selectedOption == null)
		{
			return null;
		}
		
		return (String) selectedOption.get(BY_LABEL);
	}

	@Override
	public void setValue(String driverName, UiElement element, Object valueObj)
	{
		validate(element);

		String value = "" + valueObj;
		String fullValue = value;
		
		Matcher matcher = VALUE_PATTERN.matcher(value);
		String type = BY_VALUE;

		if(matcher.matches())
		{
			type = matcher.group(1);
			value = matcher.group(2);
		}
		
		if(BY_INDEX.equals(type))
		{
			element.selectByIndex(Integer.parseInt(value.trim()));
		}
		else if(BY_LABEL.equals(type))
		{
			element.selectByLabel(value);
		}
		else if(BY_VALUE.equals(type))
		{
			element.selectByValue(value);
		}
		else
		{
			//value was not having any known prefix, so use full value (as prefix is also part of value) 
			AutomationContext automationContext = AutomationContext.getInstance();
			IExecutionLogger exeLogger = automationContext.getExecutionLogger();
			
			if(element.hasOptionWithValue(fullValue))
			{
				element.selectByValue(fullValue);
				return;
			}
			
			if(exeLogger != null)
			{
				exeLogger.debug("As no select option is found with value '{}' trying to select option using label with this value.", fullValue);
			}
			else
			{
				logger.debug("As no select option is found with value '{}' trying to select option using label with this value.", fullValue);
			}
			
			element.selectByLabel(fullValue);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FieldOption> getOptions(AutomationContext context, UiElement element)
	{
		validate(element);
		
		List<Map<String, Object>> options = (List<Map<String, Object>>) element.evaluate(ALL_OPTIONS_SCRIPT);
		
		if(options == null || options.isEmpty())
		{
			return null;
		}
		
		List<FieldOption> fieldOptions = new ArrayList<>(options.size());
		
		for(Map<String, Object> option : options)
		{
			fieldOptions.add(new FieldOption((String) option.get(BY_VALUE), (String) option.get(BY_LABEL)));
		}
		
		return fieldOptions;
	}
}
