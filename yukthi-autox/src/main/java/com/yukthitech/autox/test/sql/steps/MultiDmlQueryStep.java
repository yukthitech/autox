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
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.sql.DbPlugin;
import com.yukthitech.autox.test.sql.DbPluginSession;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Step to execute multiple dml queries in single transaction.
 * @author akiran
 */
@Executable(name = "sqlMultiDmlQuery", group = Group.Rdbms, requiredPluginTypes = DbPlugin.class, message = "Executes specified multiple DML queries in single transaction")
public class MultiDmlQueryStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * List of queries executed in single transaction.
	 */
	private List<String> queries;
	
	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Data source to be used for sql execution.", sourceType = SourceType.EXPRESSION)
	private String dataSourceName;
	
	/**
	 * Flag to indicate, if test case has to failed if there are no updates.
	 */
	@Param(description = "If true, error will be thrown if no rows are affected by specified DML.\nDefault Value: false", required = false)
	private boolean failOnNoUpdate = false;

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
	 * Adds the query to execute.
	 * 
	 * @param query query to add.
	 */
	@ChildElement(description = "DML Query to execute.")
	public void addQuery(String query) 
	{
		if(StringUtils.isBlank(query))
		{
			throw new NullPointerException("Null or blank query is specified");
		}
		
		if(this.queries == null)
		{
			this.queries = new ArrayList<>();
		}
		
		this.queries.add(query);
	}
	
	/**
	 * Sets the flag to indicate, if test case has to failed if there are no updates.
	 *
	 * @param failOnNoUpdate the new flag to indicate, if test case has to failed if there are no updates
	 */
	public void setFailOnNoUpdate(boolean failOnNoUpdate)
	{
		this.failOnNoUpdate = failOnNoUpdate;
	}

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

		String query = null;
		Map<String, Object> paramMap = new HashMap<>();
		List<Object> values = new ArrayList<>();
		String processedQuery = null;
		
		try
		{
			connection = dataSource.getConnection();
			boolean autoCommit = connection.getAutoCommit();
			
			connection.setAutoCommit(false);
			
			for (int i = 0; i < queries.size(); i++) 
			{
				query = queries.get(i);
				
				paramMap.clear();
				values.clear();
				
				processedQuery = QueryUtils.extractQueryParams(query, context, paramMap, values);
				
				exeLogger.debug(false, "On data-source '{}' executing query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, query, paramMap);
				
				exeLogger.trace(false, "On data-source '{}' executing processed query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, processedQuery, values);
				
				int count = QueryUtils.executeDml(connection, processedQuery, values);
				
				exeLogger.debug("Number of rows affected: {}", count);
				
				if(count <= 0 && failOnNoUpdate)
				{
					exeLogger.error("No records got updated by query failing test case.");
					throw new InvalidStateException("No records got updated by query: {}", query);
				}
			}
			
			connection.commit();
			connection.setAutoCommit(autoCommit);

		} catch(SQLException ex)
		{
			//exeLogger.error(ex, "An error occurred while executing query: {}", query);
			DbUtils.rollbackAndCloseQuietly(connection);
			
			throw new TestCaseFailedException(this, "An erorr occurred while executing sql query: {}", query, ex);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
}
