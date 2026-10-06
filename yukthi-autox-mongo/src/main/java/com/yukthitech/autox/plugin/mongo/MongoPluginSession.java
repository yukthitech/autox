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
package com.yukthitech.autox.plugin.mongo;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.plugin.AbstractPluginSession;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class MongoPluginSession extends AbstractPluginSession<MongoPluginSession, MongoPlugin>
{
	public MongoPluginSession(MongoPlugin parentPlugin)
	{
		super(parentPlugin);
	}
	
	/**
	 * Fetches a mongo resource with specified name.
	 * @param name
	 * @return
	 */
	public MongoResource getMongoResource(String name)
	{
		if(StringUtils.isBlank(name))
		{
			name = parentPlugin.getDefaultMongoResource();
			
			if(StringUtils.isBlank(name))
			{
				throw new InvalidStateException("No default mongo resource specified for query execution");
			}
		}
		
		MongoResource res = parentPlugin.getMongoResource(name);

		if(res == null)
		{
			throw new InvalidArgumentException("No mongo-resource found with name: {}", name);
		}
		
		return res;
	}
}
