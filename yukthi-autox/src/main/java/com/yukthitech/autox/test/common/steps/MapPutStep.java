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
@Executable(name = "mapPut", group = Group.Common, message = "Adds the specified key-value entry to specified map.")
public class MapPutStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expression to be used to set the value.
	 */
	@Param(description = "Map expression to which specified entry needs to be added.", required = true, sourceType = SourceType.EXPRESSION)
	private Object map;

	/**
	 * Value expression which needs to be added to specified collection.
	 * Default: null (null will be added)
	 */
	@Param(description = "Value expression which needs to be added to specified collection. Default: null (null will be added)", required = false, sourceType = SourceType.EXPRESSION)
	private Object value = null;

	/**
	 * Key expression which needs to be added to specified collection. Default:
	 * null (null will be added)
	 */
	@Param(description = "Key expression which needs to be added to specified collection. Default: null (null will be added)", required = false, sourceType = SourceType.EXPRESSION)
	private Object key = null;

	public void setMap(Object map)
	{
		this.map = map;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public void setKey(Object key)
	{
		this.key = key;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("To map of type {} adding entry of [Key Type: {}, Value Type: {}]", 
				(map != null) ? map.getClass().getName() : "null", 
				((key != null) ? key.getClass().getName() : key),
				((value != null) ? value.getClass().getName() : value)
				);

		if(!(map instanceof Map))
		{
			throw new InvalidArgumentException("Non-map object specified as map: {}", map);
		}

		((Map) map).put(key, value);
	}
}
