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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.sql.DbPlugin;
import com.yukthitech.autox.plugin.sql.DbPluginSession;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Common query utility methods.
 * @author akiran
 */
public class QueryUtils
{
	/**
	 * Query param pattern.
	 */
	public static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("\\?\\{(.+?)\\}");
	
	/**
	 * Query runner for query execution.
	 */
	private static QueryRunner queryRunner = new QueryRunner();
	
	/**
	 * Extract query params which are of pattern defined by {@link #QUERY_PARAM_PATTERN}.
	 *
	 * @param query query to parse
	 * @param context Context to be used to obtain values for expressions
	 * @param paramMap Map that can be useful for logging purpose
	 * @param values Order of values to be passed for query execution.
	 * @return Query after expression replacement
	 */
	public static String extractQueryParams(String query, AutomationContext context, Map<String, Object> paramMap, List<Object> values)
	{
		Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
		Object value = null;
		
		StringBuffer buffer = new StringBuffer();
		String expression = null;
		
		while(matcher.find())
		{
			expression = matcher.group(1);
			
			if(PrefixExpressionFactory.isExpression(expression))
			{
				value = PrefixExpressionFactory.getExpressionFactory().getValueByExpression(context, expression);
			}
			else
			{
				try
				{
					value = PropertyUtils.getProperty(context, expression);
				}catch(Exception ex)
				{
					value = null;
				}
			}
			
			paramMap.put(matcher.group(1), value);
			values.add(value);
			
			matcher.appendReplacement(buffer, "?");
		}
		
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	/**
	 * Executes specified dml query on specified connection.
	 * @param connection Connection to be used
	 * @param query Query to execute.
	 * @param params Params to be used
	 * @return number of rows affected
	 */
	public static int executeDml(Connection connection, String query, List<Object> params) throws SQLException
	{
		int count = queryRunner.update(connection, query, params.toArray());
		
		return count;
	}

	/**
	 * Gets the query runner for query execution.
	 *
	 * @return the query runner for query execution
	 */
	public static QueryRunner getQueryRunner()
	{
		return queryRunner;
	}
	
	/**
	 * Processes specified expression with specified details.
	 *
	 * @param context context to be used
	 * @param expression expression to be processed
	 * @param attrName Attr name to be used to set transform details on context
	 * @param transformDetails transformation info
	 * @return transformation result
	 */
	public static Object transform(AutomationContext context, String expression, String attrName, TransformDetails transformDetails)
	{
		if(StringUtils.isBlank(expression))
		{
			return transformDetails.getColumnValue();
		}
		
		context.setAttribute(attrName, transformDetails);
		return PrefixExpressionFactory.getExpressionFactory().getValueByExpression(context, expression);
	}
	
	private static <T> T executeFetch(AutomationContext context, String dataSourceName, String query, ResultSetHandler<T> handler) throws SQLException
	{
		DbPluginSession dbSession = ExecutionContextManager.getInstance().getPluginSession(DbPlugin.class);
		DataSource dataSource = dbSession.getDataSource(dataSourceName);

		if(dataSource == null)
		{
			throw new InvalidStateException("No data source found with specified name - {}", dataSourceName);
		}

		IExecutionLogger exeLogger = context.getExecutionLogger();
		Connection connection = null;

		try
		{
			connection = dataSource.getConnection();

			Map<String, Object> paramMap = new HashMap<>();
			List<Object> values = new ArrayList<>();

			String processedQuery = extractQueryParams(query, context, paramMap, values);
			
			exeLogger.debug(false, "On data-source '{}' executing query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, query, paramMap);
			
			exeLogger.trace(false, "On data-source '{}' executing processed query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, processedQuery, values);

			Object valueArr[] = values.isEmpty() ? null : values.toArray();
			
			return QueryUtils.getQueryRunner().query(connection, processedQuery, handler, valueArr);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
	
	public static Object fetchSingleValue(AutomationContext context, String dataSourceName, String query) throws SQLException
	{
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

		return executeFetch(context, dataSourceName, query, handler);
	}
	
	public static List<Object> fetchColumnList(AutomationContext context, String dataSourceName, String query) throws SQLException
	{
		ResultSetHandler<List<Object>> handler = new ColumnListHandler<>();
		
		List<Object> res = executeFetch(context, dataSourceName, query, handler);
		return (res == null) ? new ArrayList<>() : res;
	}

	public static Map<Object, Object> fetchQueryMap(AutomationContext context, String dataSourceName, String query, String keyCol, String valCol) throws SQLException
	{
		ResultSetHandler<Map<Object, Object>> handler = new ResultSetHandler<Map<Object, Object>>()
		{
			@Override
			public Map<Object, Object> handle(ResultSet rs) throws SQLException
			{
				Map<Object, Object> resMap = new HashMap<>();
				
				while(rs.next())
				{
					resMap.put(rs.getObject(keyCol), rs.getObject(valCol));
				}
				
				return resMap;
			}
		};
		
		return executeFetch(context, dataSourceName, query, handler);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List<T> asList(T... elem)
	{
		return new ArrayList<>(Arrays.asList(elem));
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> fetchRowMaps(AutomationContext context, String dataSourceName, String query, boolean processAllRows) throws SQLException
	{
		ResultSetHandler<? extends Object> handler = processAllRows ? new MapListHandler() : new MapHandler();
		
		Object res = executeFetch(context, dataSourceName, query, handler);
		
		if(!processAllRows)
		{
			return (res != null) ? asList((Map<String, Object>) res) : asList(new HashMap<>());
		}
		
		List<Map<String, Object>> resLst = (List<Map<String, Object>>) res;
		return CollectionUtils.isNotEmpty(resLst) ? resLst : asList(new HashMap<>());
	}
}
