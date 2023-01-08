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
package com.yukthitech.autox.plugin.sql.assertion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.filter.ExpressionFactory;
import com.yukthitech.autox.plugin.sql.DbPlugin;
import com.yukthitech.autox.plugin.sql.DbPluginSession;
import com.yukthitech.autox.plugin.sql.steps.QueryUtils;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * SQL based validation.
 */
@Executable(name = "sqlAssertValue", group = Group.Rdbms, requiredPluginTypes = DbPlugin.class, message = "Executes specified query and validates only single value (first row - first column value)")
public class SqlAssertValue extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

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
	 * Expected value. Which will compared with value from db.
	 */
	@Param(description = "Expected value. Which will compared with value from db.", sourceType = SourceType.EXPRESSION)
	private Object expectedValue;
	
	@Param(description = "Expression to be used on query result, before comparison. Default: null", required = false)
	private String convertExpression;

	public void setConvertExpression(String convertExpression)
	{
		this.convertExpression = convertExpression;
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
	 * Sets the expected value. Which will compared with value from db.
	 *
	 * @param expectedValue the new expected value
	 */
	public void setExpectedValue(Object expectedValue)
	{
		this.expectedValue = expectedValue;
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

	/**
	 * Gets the type.
	 *
	 * @param val the val
	 * @return the type
	 */
	private Class<?> getType(Object val)
	{
		if(val == null)
		{
			return null;
		}
		
		return val.getClass();
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
			
			exeLogger.debug(false, "On data-source '{}' executing query: \n<code class='SQL'>{}</code>\nParams: {} \nExpected Value: {}", dataSourceName, query, paramMap, expectedValue);
			
			exeLogger.trace(false, "On data-source '{}' executing processed query: \n<code class='SQL'>{}</code>\nParams: {} \nExpected Value: {}", dataSourceName, processedQuery, values, expectedValue);
			
			ResultSetHandler<Object> rsHandler = new ResultSetHandler<Object>()
			{
				@Override
				public Object handle(ResultSet rs) throws SQLException
				{
					if(!rs.next())
					{
						return null;
					}
					
					Object res = rs.getObject(1);

					if(convertExpression != null)
					{
						res = ExpressionFactory.getExpressionFactory().parseExpression(context, convertExpression, res);
					}
					
					return res;
				}
			};
			
			Object res = QueryUtils.getQueryRunner().query(connection, processedQuery, rsHandler);
			
			if(!AutomationUtils.equals(expectedValue, res))
			{
				exeLogger.error("Expected value {} [Type: {}] is not matching with actual value: {} [Type: {}]", expectedValue, getType(expectedValue), res, getType(res));
				throw new AutoxValidationException(this, "Expected value {} [Type: {}] is not matching with actual value: {} [Type: {}]", expectedValue, getType(expectedValue), res, getType(res));
			}
			
			exeLogger.debug("Expected value and actual value are found to be same: {}", expectedValue);
		} catch(SQLException ex)
		{
			//exeLogger.error(ex, "An error occurred while executing sql validation with query - {}", query);
			throw new TestCaseFailedException(this, "An erorr occurred while executing sql validation with query - {}", query, ex);
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
