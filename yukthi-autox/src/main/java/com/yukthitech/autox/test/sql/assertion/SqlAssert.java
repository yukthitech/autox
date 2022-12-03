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
package com.yukthitech.autox.test.sql.assertion;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.sql.DbPlugin;
import com.yukthitech.autox.test.sql.DbPluginSession;
import com.yukthitech.autox.test.sql.steps.QueryUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * SQL based validation.
 */
@Executable(name = "sqlAssert", group = Group.Rdbms, requiredPluginTypes = DbPlugin.class, message = "Executes specified query and validates expected data is returned")
public class SqlAssert extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Result row expectation.
	 * 
	 * @author akiran
	 */
	public static class ExpectedRow implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Expected column to value mapping.
		 */
		private Map<String, String> columnToValue = new HashMap<>();

		/**
		 * Adds the column and value expectation.
		 * 
		 * @param name
		 *            Name of column.
		 * @param value
		 *            Expected value.
		 */
		@ChildElement(description = "Used to specify column-value pair to be validated.", key = "name", keyDescription = "Name of the column to validate.")
		public void addColumn(String name, String value)
		{
			if(name == null || name.trim().length() == 0)
			{
				throw new NullPointerException("Name can not be null or empty");
			}

			if(value == null || value.trim().length() == 0)
			{
				throw new NullPointerException("Value can not be null or empty");
			}

			columnToValue.put(name, value);
		}
	}
	
	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Name of the data source to be used for query execution.")
	private String dataSourceName;

	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute whose results needs to be validated.")
	private String query;

	/**
	 * Rows and expected values.
	 */
	@Param(description = "Expected rows of values from query result. Each row will have list of column (name-value pairs)")
	private List<ExpectedRow> expectedRows = new ArrayList<>();
	
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
	 * Adds row expectation.
	 * 
	 * @param row
	 *            row expectation
	 */
	public void addExpectedRow(ExpectedRow row)
	{
		expectedRows.add(row);
	}

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
		if(!"true".equals(enabled))
		{
			exeLogger.debug("Current validation is disabled. Skipping validation execution.");
			return;
		}
		
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
			
			exeLogger.debug(false, "On data-source '{}' executing query: \n<code class='SQL'>{}</code>\nParams: {}", dataSourceName, query, paramMap);
			
			exeLogger.trace(false, "On data-source '{}' executing processed query: \n<code class='SQL'>{}</code>\nParams: {}", dataSourceName, processedQuery, values);
			
			AtomicInteger recCount = new AtomicInteger(0);

			ResultSetHandler<String> rsHandler = new ResultSetHandler<String>()
			{
				@Override
				public String handle(ResultSet rs) throws SQLException
				{
					int rowIdx = 0;
					ExpectedRow row = null;
					String actualVal = null, expectedVal = null;
					Map<String, String> actualRow = new HashMap<>();

					while(rs.next())
					{
						recCount.incrementAndGet();
						
						if(expectedRows.size() <= rowIdx)
						{
							exeLogger.error("Actual rows are more than expected row count: {}", expectedRows.size());
							return String.format("Actual rows are more than expected row count: %s", expectedRows.size());
						}

						row = expectedRows.get(rowIdx);
						actualRow.clear();
						
						exeLogger.debug("Validating row {} values are: {}", rowIdx, row.columnToValue);

						for(String column : row.columnToValue.keySet())
						{
							actualVal = rs.getString(column);
							expectedVal = row.columnToValue.get(column);

							if(!expectedVal.equals(actualVal))
							{
								exeLogger.error("At row {} for column {} expected value '{}' is not matching with actual value: {}", rowIdx, column, expectedVal, actualVal);
								return String.format("At row %s for column %s expected value '%s' is not matching with actual value: %s", rowIdx, column, expectedVal, actualVal);
							}
						}
						
						exeLogger.debug("Found row {} to be as per expected values", rowIdx);
						
						rowIdx ++;
					}

					return null;
				}
			};
			
			String errMssg = QueryUtils.getQueryRunner().query(connection, processedQuery, rsHandler);

			if(recCount.get() < expectedRows.size())
			{
				exeLogger.error("Actual rows {} are less than expected row count: {}", recCount.get(), expectedRows.size());
				throw new AutoxValidationException(this, "Actual rows {} are less than expected row count: {}", recCount.get(), expectedRows.size());
			}
			
			if(errMssg != null)
			{
				throw new AutoxValidationException(this, errMssg);
			}
		} catch(SQLException ex)
		{
			//exeLogger.error(ex, "An error occurred while executing sql validation with query - {}", query);
			throw new AutoxValidationException(this, "An erorr occurred while executing sql validation with query - {}", query, ex);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Query: ").append(query);

		builder.append("]");
		return builder.toString();
	}

	@Override
	public IValidation clone()
	{
		return AutomationUtils.deepClone(this);
	}
}
