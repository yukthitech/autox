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

import org.apache.commons.jxpath.JXPathContext;

import com.yukthitech.autox.context.AutomationContext;

/**
 * Represents a reference to context attribute using xpath.
 * @author akiran
 */
public class ContextAttributeXpathReference implements Serializable, IReference
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Path of the attribute being referenced.
	 */
	private String path;

	public ContextAttributeXpathReference(String name)
	{
		this.path = name;
	}
	
	@Override
	public Object getValue(AutomationContext context)
	{
		return JXPathContext.newContext(context.getAttr()).getValue(path);
	}
}
