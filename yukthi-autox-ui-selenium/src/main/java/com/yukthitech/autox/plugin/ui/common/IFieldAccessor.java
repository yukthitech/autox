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

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.context.AutomationContext;

/**
 * Abstract type for accessing field data information.
 * @author akiran
 */
public interface IFieldAccessor
{
	/**
	 * Fetches value of the specified field.
	 * @param element Element from which value needs to be fetched.
	 * @return Value of the element
	 */
	public String getValue(AutomationContext context, WebElement element);
	
	/**
	 * Fetches the default value of the specified element. This may represent lable for some elements like - Select, checkbox, etc.
	 * For others this will return the value of the element.
	 * @param context
	 * @param element
	 * @return
	 */
	public default String getDisplayValue(AutomationContext context, WebElement element)
	{
		return getValue(context, element);
	}
	
	/**
	 * Sets the specified value on specified element.
	 * @param element Element on which value needs to be set.
	 * @param value Value to set.
	 */
	public default void setValue(String driverName, WebElement element, Object value)
	{}
	
	/**
	 * Invoked on field-types which can support multiple field for single value set invocation.
	 * @param elements elements to set value on
	 * @param value value to set
	 */
	public default void setValue(String driverName, List<WebElement> elements, Object value)
	{}
	
	/**
	 * Fetches options from specified element.
	 * @param element Element from which options needs to be fetched.
	 * @return field options
	 */
	public List<FieldOption> getOptions(AutomationContext context, WebElement element);
}
