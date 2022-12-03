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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

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
@Executable(name = "sqlLoadQueryRowBean", group = Group.Rdbms, requiredPluginTypes = DbPlugin.class, message = "Executes specified query and loads the results as bean(s) on context. "
		+ "\nIn case of zero results empty map will be kept on context. \nPer row new bean will be created.")
public class LoadQueryRowBeanStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute, the results will be used to create bean.")
	private String query;
	
	/**
	 * Flag to indicate if all rows should be processed into bean. If true, list of beans will be loaded on context otherwise first row map will be loaded
	 * on context.
	 */
	@Param(description = "If false, only first row will be processed into bean. If true, per row new map will be created and loads of this beans into context.\nDefault: true", required = false)
	private boolean processAllRows = true;

	/**
	 * Context attribute to be used to load the map.
	 */
	@Param(description = "Name of the attribute which should be used to keep the result bean on the context.", attrName = true)
	private String contextAttribute;

	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Name of the data source to be used for query execution.")
	private String dataSourceName;
	
	/**
	 * Type of beans to which rows should be converted.
	 */
	@Param(description = "Type of bean to which rows should be converted.")
	private Class<?> beanType;

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
	 * Sets the flag to indicate if all rows should be processed into map. If true, list of maps will be loaded on context otherwise first row map will be loaded on context.
	 *
	 * @param processAllRows the new flag to indicate if all rows should be processed into map
	 */
	public void setProcessAllRows(boolean processAllRows)
	{
		this.processAllRows = processAllRows;
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
	
	/**
	 * Sets the type of beans to which rows should be converted.
	 *
	 * @param beanType the new type of beans to which rows should be converted
	 */
	public void setBeanType(Class<?> beanType)
	{
		this.beanType = beanType;
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
			throw new InvalidStateException("No data source found with specified name: {}", dataSourceName);
		}

		Connection connection = null;

		try
		{
			connection = dataSource.getConnection();

			Map<String, Object> paramMap = new HashMap<>();
			List<Object> values = new ArrayList<>();

			String processedQuery = QueryUtils.extractQueryParams(query, context, paramMap, values);
			
			exeLogger.debug(false, "On data-source '{}' executing query: \n<code class='SQL'>{}</code> \n\nParams: {}", dataSourceName, query, paramMap);
			
			exeLogger.trace(false, "On data-source '{}' executing processed query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, processedQuery, values);

			Object result = null;
			Object valueArr[] = values.isEmpty() ? null : values.toArray();
			
			if(processAllRows)
			{
				exeLogger.debug("Loading muliple row beans on context attribute: {}", contextAttribute);
				result = QueryUtils.getQueryRunner().query(connection, processedQuery, new BeanHandler<>(beanType), valueArr);
				
				if(result == null)
				{
					result = new ArrayList<>();
				}
			}
			else
			{
				exeLogger.debug("Loading first-row as bean on context attribute: {}", contextAttribute);
				result = QueryUtils.getQueryRunner().query(connection, processedQuery, new BeanListHandler<>(beanType), valueArr);
				
				if(result == null)
				{
					result = new HashMap<>();
				}
			}

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
