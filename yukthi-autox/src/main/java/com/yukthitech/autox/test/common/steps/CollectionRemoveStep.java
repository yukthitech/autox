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
package com.yukthitech.autox.test.common.steps;

import java.util.Collection;
import java.util.Map;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Removes the specified value from specified collection.
 * 
 * @author akiran
 */
@Executable(name = "collectionRemove", group = Group.Common, message = "Removes the specified value / key from specified collection or map")
public class CollectionRemoveStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expression to be used to set the value.
	 */
	@Param(description = "Collection or expression from which specified value (or key) needs to be removed.", required = true, sourceType = SourceType.EXPRESSION)
	private Object collection;

	/**
	 * Value of the attribute to set.
	 */
	@Param(description = "Value (or key) expression which needs to be removed from specified collection. Default: null (null will be removed from specified collection)", required = false, sourceType = SourceType.EXPRESSION)
	private Object value = null;

	public void setCollection(String collection)
	{
		this.collection = collection;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("From collection/map of type {} removing value of type: {}", 
				(collection != null) ? collection.getClass().getName() : "null", 
				((value != null) ? value.getClass().getName() : value)
				);
		
		if(collection instanceof Collection)
		{
			((Collection) collection).remove(value);
		}
		else if(collection instanceof Map)
		{
			((Map) collection).remove(value);
		}
		else
		{
			throw new InvalidArgumentException("Non-collection and non-map object specified as collection: {}", collection);
		}
	}
}
