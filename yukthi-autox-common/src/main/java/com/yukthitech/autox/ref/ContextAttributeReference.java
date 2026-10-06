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
package com.yukthitech.autox.ref;

import java.io.Serializable;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.autox.context.AutomationContext;

/**
 * Represents a reference to context attribute.
 * @author akiran
 */
public class ContextAttributeReference implements Serializable, IReference
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the attribute being referenced.
	 */
	private String name;

	public ContextAttributeReference(String name)
	{
		this.name = name;
	}
	
	@Override
	public Object getValue(AutomationContext context)
	{
		try
		{
			return PropertyUtils.getProperty(context.getAttr(), name);
		} catch(NoSuchMethodException ex)
		{
			return null;
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while fetching context attribute: " + name, ex);
		}
	}
}
