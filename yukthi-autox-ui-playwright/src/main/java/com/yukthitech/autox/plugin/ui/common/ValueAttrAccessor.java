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
 * Accessor to access value of fields like hidden fields, whose value can be set only using js.
 */
public class ValueAttrAccessor implements IFieldAccessor
{
	@Override
	public String getValue(AutomationContext context, UiElement element)
	{
		return element.getAttribute("value");
	}

	@Override
	public void setValue(String driverName, UiElement element, Object value)
	{
		try
		{
			element.evaluate("(e, v) => { e.setAttribute('value', v); e.value = v; }", "" + value);
		}catch(RuntimeException ex)
		{
			AutomationContext.getInstance().getExecutionLogger().debug("Failed to set the field value using JS set attribute.", ex);
			throw ex;
		}
	}

	@Override
	public List<FieldOption> getOptions(AutomationContext context, UiElement element)
	{
		return null;
	}
}
