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

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Adds the specified value to specified collection.
 * 
 * @author akiran
 */
@Executable(name = "collectionAddAll", group = Group.Common, message = "Adds the elements from specified new-collection to specified collection")
public class CollectionAddAllStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expression to be used to set the value.
	 */
	@Param(description = "Collection expression to which specified values needs to be added.", required = true, sourceType = SourceType.EXPRESSION)
	private Object collection;

	@Param(description = "Collection from which values needs to be added.", required = true, sourceType = SourceType.EXPRESSION)
	private Object newCollection;

	public void setCollection(String collection)
	{
		this.collection = collection;
	}

	public void setNewCollection(Object newCollection)
	{
		this.newCollection = newCollection;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("To collection of type {} adding values from collection of type: {}", 
				(collection != null) ? collection.getClass().getName() : "null", 
				(newCollection != null) ? newCollection.getClass().getName() : "null"
				);
		
		if(!(collection instanceof Collection))
		{
			throw new InvalidArgumentException("Non-collection object specified as collection: {}", collection.getClass().getName());
		}
		
		if(!(newCollection instanceof Collection))
		{
			throw new InvalidArgumentException("Non-collection object specified as new-collection: {}", newCollection.getClass().getName());
		}

		((Collection) collection).addAll((Collection) newCollection);
	}
}
