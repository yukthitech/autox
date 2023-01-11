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
package com.yukthitech.autox.plugin.sql.steps;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Attributable;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.sql.DbPlugin;
import com.yukthitech.autox.plugin.sql.DbPluginSession;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Step to execute DML Query.
 * @author akiran
 */
@Executable(name = "sqlDmlQuery", group = Group.Rdbms, requiredPluginTypes = DbPlugin.class, message = "Executes specified DML Query on specified data source.")
public class DmlQueryStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute.", attributable = Attributable.FALSE)
	private String query;
	
	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Data source to be used for sql execution.", required = false)
	private String dataSourceName;
	
	/**
	 * Flag to indicate, if test case has to failed if there are no updates.
	 */
	@Param(description = "If true, error will be thrown if no rows are affected by specified DML.\nDefault Value: false", required = false)
	private boolean failOnNoUpdate = false;
	
	/**
	 * If specified, number of rows affected will be set on the context.
	 */
	@Param(description = "If specified, number of rows affected will be set on the context", required = false, attrName = true)
	private String countAttribute;
	
	/**
	 * If true, calls commit at end. Default: true.
	 */
	@Param(description = "If true, calls commit at end. Default: true", required = false)
	private boolean commitAtEnd = true;
	
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
	 * Sets the query.
	 * 
	 * @param query the new query.
	 */
	public void setQuery(String query) 
	{
		this.query = query;
	}
	
	/**
	 * Sets the if specified, number of rows affected will be set on the context.
	 *
	 * @param countAttribute the new if specified, number of rows affected will be set on the context
	 */
	public void setCountAttribute(String countAttribute)
	{
		this.countAttribute = countAttribute;
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
	
	/**
	 * Sets the if true, calls commit at end. Default: true.
	 *
	 * @param commitAtEnd the new if true, calls commit at end
	 */
	public void setCommitAtEnd(boolean commitAtEnd)
	{
		this.commitAtEnd = commitAtEnd;
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

		try
		{
			connection = dataSource.getConnection();
			
			Map<String, Object> paramMap = new HashMap<>();
			List<Object> values = new ArrayList<>();
			
			String processedQuery = QueryUtils.extractQueryParams(query, context, paramMap, values);
			
			exeLogger.debug(false, "On data-source '{}' executing query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, query, paramMap);
			
			exeLogger.trace(false, "On data-source '{}' executing processed query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, processedQuery, values);
			
			int count = QueryUtils.executeDml(connection, processedQuery, values);
	
			if(commitAtEnd)
			{
				exeLogger.debug("Executing commit...");
				connection.commit();
			}
			else
			{
				exeLogger.debug("Skipping commit execution...");
			}
			
			exeLogger.debug("Number of rows affected: {}", count);
			
			if(count <= 0 && failOnNoUpdate)
			{
				exeLogger.error("No records got updated by query");
				throw new InvalidStateException("No records got updated by query - {}", query);
			}
			
			if(countAttribute != null)
			{
				exeLogger.debug("Setting result count {} on context attribute: {}", count, countAttribute);
				context.setAttribute(countAttribute, count);
			}

		} catch(SQLException ex)
		{
			//exeLogger.error(ex, "An error occurred while executing query");
			throw new TestCaseFailedException(this, "An erorr occurred while executing sql query - {}", query, ex);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
}
