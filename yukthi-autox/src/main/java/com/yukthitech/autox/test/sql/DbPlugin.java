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
package com.yukthitech.autox.test.sql;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.AbstractPlugin;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * plugin related to db related steps or validators.
 * @author akiran
 */
@Executable(name = "DbPlugin", group = Group.NONE, message = "Plugin related to db related steps or validators.")
public class DbPlugin extends AbstractPlugin<Object, DbPluginSession> implements Validateable
{
	private static Logger logger = LogManager.getLogger(DbPlugin.class);
	
	/**
	 * Application data sources.
	 */
	@Param(description = "Name to data source mapping for different data sources that are available in this app automation.", required = true)
	private Map<String, DataSource> dataSourceMap = new HashMap<>();
	
	/**
	 * Name of the default data source name.
	 */
	@Param(description = "Name of the default data source name.", required = false)
	private String defaultDataSource;
	
	/**
	 * Sets the name of the default data source name.
	 *
	 * @param defaultDataSource the new name of the default data source name
	 */
	public void setDefaultDataSource(String defaultDataSource)
	{
		this.defaultDataSource = defaultDataSource;
	}
	
	@Override
	protected DbPluginSession createSession()
	{
		return new DbPluginSession(this);
	}
	
	/**
	 * Adds specified data source with specified name.
	 * @param name Name of the data source.
	 * @param dataSource Data source to add.
	 */
	public void addDataSource(String name, DataSource dataSource)
	{
		if(StringUtils.isBlank(name))
		{
			throw new NullPointerException("Data source name can not be null or empty.");
		}
		
		if(dataSource == null)
		{
			throw new NullPointerException("Data source can not be null");
		}
		
		this.dataSourceMap.put(name, dataSource);
	}
	
	/**
	 * Fetches data source with specified name.
	 * @param name Name of the data source.
	 * @return Matching data source name.
	 */
	DataSource getDataSource(String name)
	{
		return dataSourceMap.get(name);
	}
	
	/**
	 * Fetches default data source if one is configured.
	 * @return default data source, if any.
	 */
	DataSource getDefaultDataSource()
	{
		return dataSourceMap.get(defaultDataSource);
	}

	@Override
	public void close()
	{
		for(DataSource dataSource : this.dataSourceMap.values())
		{
			if(dataSource instanceof BasicDataSource)
			{
				try
				{
					((BasicDataSource) dataSource).close();
				}catch(Exception ex)
				{
					logger.warn("An error occurred while closing the data source", ex);
				}
			}
		}
	}

	@Override
	public void validate() throws ValidateException
	{
		if(dataSourceMap.isEmpty())
		{
			throw new ValidateException("No data sources are defined.");
		}
	}
}
