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
package com.yukthitech.autox.test.sql.steps;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.sql.DbPlugin;
import com.yukthitech.autox.test.sql.DbPluginSession;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executes specified query and creates map out of results. And sets this map on
 * the context.
 */
@Executable(name = "sqlFetchValueQuery", group = Group.Rdbms, requiredPluginTypes = DbPlugin.class, message = "Fetches single value (first row, first column value) from the results of specified query.")
public class FetchValueQueryStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute, the result's first column will be used to create list.")
	private String query;
	
	/**
	 * Context attribute to be used to load the map.
	 */
	@Param(description = "Name of the attribute which should be used to keep the result value.", attrName = true)
	private String contextAttribute;

	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Name of the data source to be used for query execution.")
	private String dataSourceName;

	/**
	 * Sets the query to execute.
	 *
	 * @param query
	 *            the new query to execute
	 */
	public void setQuery(String query)
	{
		this.query = query;
	}

	/**
	 * Sets the context attribute to be used to load the map.
	 *
	 * @param contextAttribute the new context attribute to be used to load the map
	 */
	public void setContextAttribute(String contextAttribute)
	{
		this.contextAttribute = contextAttribute;
	}

	/**
	 * Sets the name of the data source to use.
	 *
	 * @param dataSourceName the new name of the data source to use
	 */
	public void setDataSourceName(String dataSourceName)
	{
		this.dataSourceName = dataSourceName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IValidation#execute(com.yukthitech.ui.automation.
	 * AutomationContext, com.yukthitech.ui.automation.IExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		DbPluginSession dbSession = ExecutionContextManager.getInstance().getPluginSession(DbPlugin.class);
		DataSource dataSource = dbSession.getDataSource(dataSourceName);

		if(dataSource == null)
		{
			throw new InvalidStateException("No data source found with specified name - {}", dataSourceName);
		}

		Connection connection = null;

		try
		{
			connection = dataSource.getConnection();

			Map<String, Object> paramMap = new HashMap<>();
			List<Object> values = new ArrayList<>();

			String processedQuery = QueryUtils.extractQueryParams(query, context, paramMap, values);
			
			exeLogger.debug(false, "On data-source '{}' executing query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, query, paramMap);
			
			exeLogger.trace(false, "On data-source '{}' executing processed query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, processedQuery, values);

			Object result = null;
			Object valueArr[] = values.isEmpty() ? null : values.toArray();
			
			exeLogger.debug("Loading single valued result on context attribute: {}", contextAttribute);
			
			ResultSetHandler<Object> handler = new ResultSetHandler<Object>()
			{
				@Override
				public Object handle(ResultSet rs) throws SQLException
				{
					if(!rs.next())
					{
						return null;
					}
					
					return rs.getObject(1);
				}
			};
			
			result = QueryUtils.getQueryRunner().query(connection, processedQuery, handler, valueArr);
			
			context.setAttribute(contextAttribute, result);
			exeLogger.debug("Data loaded on context with name {}. Data: {}", contextAttribute, result);
		} catch(SQLException ex)
		{
			//exeLogger.error(ex, "An error occurred while executing query: {}", query);
			
			throw new TestCaseFailedException(this, "An erorr occurred while executing query: {}", query, ex);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
}
