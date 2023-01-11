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
import java.sql.Statement;

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
 * Step to execute DDL query.
 */
@Executable(name = "sqlDdlQuery", group = Group.Rdbms, requiredPluginTypes = DbPlugin.class, message = "Executes specified DDL query on specified data source.")
public class DdlQueryStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Ddl query to execute.
	 */
	@Param(description = "DDL query to execute", attributable = Attributable.FALSE)
	private String query;
	
	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Data source to be used for sql execution.", required = false)
	private String dataSourceName;
	
	@Param(description = "If set to true, exceptions during query execution will be ignored. Helpful to rest the db without assuming initial state.")
	private boolean ignoreErrors;
	
	/**
	 * Sets the ddl query to execute.
	 *
	 * @param query the new ddl query to execute
	 */
	public void setQuery(String query)
	{
		this.query = query;
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
	
	public void setIgnoreErrors(boolean ignoreErrors)
	{
		this.ignoreErrors = ignoreErrors;
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
		Statement statement = null;

		try
		{
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			
			exeLogger.debug(false, "On data-source '{}' executing DDL query: \n <code class='SQL'>{}</code>", dataSourceName, query);
			
			statement.execute(query);
		} catch(SQLException ex)
		{
			//exeLogger.error(ex, "An error occurred while executing DDL query");
			
			if(ignoreErrors)
			{
				exeLogger.info("IGNORED Error: An error occurred while executing DDL query. Error: " + ex);
			}
			else
			{
				throw new TestCaseFailedException(this, "An erorr occurred while executing DDL query - {}", query, ex);
			}
		} finally
		{
			DbUtils.closeQuietly(connection, statement, null);
		}
	}
}
