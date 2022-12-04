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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.context.AutomationContext;

/**
 * Field accessor for checkboxes and radio buttons.
 */
public class CheckboxFieldAccessor implements IFieldAccessor
{
	
	/** 
	 * The value. 
	 **/
	private static String VALUE = "value";
	
	/**
	 * Fetches check-boxes or similar elements from specified parent element
	 * with same name.
	 *
	 * @param element
	 *            Element whose groups needs to be fetched
	 * @return the list of group elementss
	 */
	private List<WebElement> findGroupedElements(WebElement element)
	{
		WebElement parentElement = element.findElement(By.xpath(".."));
		return parentElement.findElements(By.name(element.getAttribute("name")));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getValue(org.openqa.
	 * selenium.WebElement)
	 */
	@Override
	public String getValue(AutomationContext context, WebElement element)
	{
		List<WebElement> elements = findGroupedElements(element);
		StringBuilder builder = new StringBuilder();

		for(WebElement welem : elements)
		{
			if(welem.isSelected())
			{
				continue;
			}

			if(builder.length() > 0)
			{
				builder.append(",");
			}

			builder.append(welem.getAttribute(VALUE));
		}

		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#setValue(org.openqa.
	 * selenium.WebElement, java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes"})
	@Override
	public void setValue(String driverName, List<WebElement> webElements, Object value)
	{
		Set<String> valueSet = new HashSet<>();

		if(value instanceof Collection)
		{
			for(Object obj : ((Collection) value))
			{
				valueSet.add("" + obj);
			}
		}
		else
		{
			valueSet.add("" + value);
		}

		for(WebElement webElem : webElements)
		{
			if(valueSet.contains(webElem.getAttribute(VALUE)))
			{
				if(!webElem.isSelected())
				{
					webElem.click();
				}
			}
			else
			{
				if(webElem.isSelected())
				{
					webElem.click();
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.common.IFieldAccessor#getOptions(org.openqa.
	 * selenium.WebElement)
	 */
	@Override
	public List<FieldOption> getOptions(AutomationContext context, WebElement element)
	{
		List<WebElement> webElements = findGroupedElements(element);
		List<FieldOption> options = new ArrayList<>(webElements.size());

		for(WebElement elem : webElements)
		{
			options.add(new FieldOption(elem.getAttribute(VALUE), null));
		}

		return options;
	}
}
