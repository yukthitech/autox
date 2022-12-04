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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.filter.ExpressionFactory;

/**
 * Common query utility methods.
 * @author akiran
 */
public class QueryUtils
{
	/**
	 * Query param pattern.
	 */
	public static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("\\?\\{(.+)\\}");
	
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
			
			if(IAutomationConstants.EXPRESSION_PATTERN.matcher(expression).find() ||
					IAutomationConstants.EXPRESSION_WITH_PARAMS_PATTERN.matcher(expression).find()
					)
			{
				value = ExpressionFactory.getExpressionFactory().parseExpression(context, expression);
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
		return ExpressionFactory.getExpressionFactory().parseExpression(context, expression);
	}
}
