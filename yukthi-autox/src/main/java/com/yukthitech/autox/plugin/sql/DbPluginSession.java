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
package com.yukthitech.autox.plugin.sql;

import javax.sql.DataSource;

import com.yukthitech.autox.plugin.AbstractPluginSession;

/**
 * DB Plugin session.
 * 
 * @author akranthikiran
 */
public class DbPluginSession extends AbstractPluginSession<DbPluginSession, DbPlugin>
{
	public DbPluginSession(DbPlugin parentPlugin)
	{
		super(parentPlugin);
	}
	
	/**
	 * Fetches data source with specified name.
	 * @param name Name of the data source.
	 * @return Matching data source name.
	 */
	public DataSource getDataSource(String name)
	{
		return parentPlugin.getDataSource(name);
	}
}