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
 * Adds the specified key-value entry to specified map.
 * 
 * @author akiran
 */
@Executable(name = "mapPutAll", group = Group.Common, message = "Adds all the entries from the specified new-map to specified map.")
public class MapPutAllStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expression to be used to set the value.
	 */
	@Param(description = "Map expression to which specified entries needs to be added.", required = true, sourceType = SourceType.EXPRESSION)
	private Object map;

	@Param(description = "New map whose entries has to be added.", required = true, sourceType = SourceType.EXPRESSION)
	private Object newMap;
	
	public void setMap(Object map)
	{
		this.map = map;
	}

	public void setNewMap(Object newMap)
	{
		this.newMap = newMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("To map of type {} adding entries from map of type: {}", 
				(map != null) ? map.getClass().getName() : "null", 
				(newMap != null) ? newMap.getClass().getName() : "null"
				);

		if(!(map instanceof Map))
		{
			throw new InvalidArgumentException("Non-map object specified as map: {}", map.getClass().getName());
		}

		if(!(newMap instanceof Map))
		{
			throw new InvalidArgumentException("Non-map object specified as new-map: {}", map.getClass().getName());
		}

		((Map) map).putAll((Map) newMap);
	}
}
