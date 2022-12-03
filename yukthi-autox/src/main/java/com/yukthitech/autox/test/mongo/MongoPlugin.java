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
package com.yukthitech.autox.test.mongo;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.AbstractPlugin;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * The Class MongoPlugin.
 */
public class MongoPlugin extends AbstractPlugin<Object, MongoPluginSession> implements Validateable
{
	/**
	 * Application data sources.
	 */
	@Param(description = "Name to mongo resource mapping for different mongo-resources that are available in this app automation.", required = true)
	private Map<String, MongoResource> mongoResourceMap = new HashMap<>();
	
	/**
	 * Name of the default data source name.
	 */
	@Param(description = "Name of the default mongo resource.", required = false)
	private String defaultMongoResource;

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.config.IPlugin#getArgumentBeanType()
	 */
	@Override
	public Class<Object> getArgumentBeanType()
	{
		return null;
	}
	
	/**
	 * Maps specified name with specified resource.
	 * @param name
	 * @param resource
	 */
	public void addMongoResource(MongoResource resource)
	{
		if(resource == null)
		{
			throw new NullPointerException("Resource can not be null");
		}

		this.mongoResourceMap.put(resource.getName(), resource);
	}
	
	/**
	 * Fetches a mongo resource with specified name.
	 * @param name
	 * @return
	 */
	MongoResource getMongoResource(String name)
	{
		return this.mongoResourceMap.get(name);
	}
	
	/**
	 * Gets the name of the default data source name.
	 *
	 * @return the name of the default data source name
	 */
	String getDefaultMongoResource()
	{
		return defaultMongoResource;
	}

	/**
	 * Sets the name of the default data source name.
	 *
	 * @param defaultMongoResource the new name of the default data source name
	 */
	public void setDefaultMongoResource(String defaultMongoResource)
	{
		this.defaultMongoResource = defaultMongoResource;
	}
	
	@Override
	protected MongoPluginSession createSession()
	{
		return new MongoPluginSession(this);
	}
	
	@Override
	public void close()
	{
		for(MongoResource res : mongoResourceMap.values())
		{
			res.getMongoClient().close();
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(mongoResourceMap.isEmpty())
		{
			throw new ValidateException("No mong-resources are defined.");
		}
	}
}
