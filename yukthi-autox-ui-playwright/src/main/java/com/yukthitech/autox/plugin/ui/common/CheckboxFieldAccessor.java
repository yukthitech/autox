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
	 * @return the list of group elements
	 */
	private List<UiElement> findGroupedElements(UiElement element)
	{
		String name = element.getAttribute("name");
		
		if(name == null || name.isEmpty())
		{
			List<UiElement> single = new ArrayList<>(1);
			single.add(element);
			return single;
		}
		
		UiElement parentElement = element.domParent();
		return parentElement.findAll("css=[name=\"" + name.replace("\\", "\\\\").replace("\"", "\\\"") + "\"]");
	}

	@Override
	public String getValue(AutomationContext context, UiElement element)
	{
		List<UiElement> elements = findGroupedElements(element);
		StringBuilder builder = new StringBuilder();

		for(UiElement welem : elements)
		{
			if(!welem.isSelected())
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

	@SuppressWarnings({ "rawtypes"})
	@Override
	public void setValue(String driverName, List<UiElement> webElements, Object value)
	{
		Set<String> valueSet = toValueSet(value);

		for(UiElement webElem : webElements)
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
	
	/**
	 * Used for radio buttons (where single element represents the group). Selects the radio button
	 * with specified value from the group.
	 */
	@Override
	public void setValue(String driverName, UiElement element, Object value)
	{
		Set<String> valueSet = toValueSet(value);
		
		for(UiElement webElem : findGroupedElements(element))
		{
			if(valueSet.contains(webElem.getAttribute(VALUE)) && !webElem.isSelected())
			{
				webElem.click();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Set<String> toValueSet(Object value)
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
		
		return valueSet;
	}
	
	@Override
	public List<FieldOption> getOptions(AutomationContext context, UiElement element)
	{
		List<UiElement> webElements = findGroupedElements(element);
		List<FieldOption> options = new ArrayList<>(webElements.size());

		for(UiElement elem : webElements)
		{
			options.add(new FieldOption(elem.getAttribute(VALUE), null));
		}

		return options;
	}
}
