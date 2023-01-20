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

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.plugin.sql.steps.QueryUtils;
import com.yukthitech.autox.prefix.PrefixEpression;
import com.yukthitech.autox.prefix.PrefixExprParam;
import com.yukthitech.autox.prefix.PrefixExpressionAnnot;
import com.yukthitech.autox.prefix.PrefixExpressionContext;

/**
 * Sql prefix expressions.
 * @author akranthikiran
 */
public class SqlPrefixExpressions
{
	@PrefixExpressionAnnot(type = "sqlVal", description = "Used to fetch single value using sql query", 
			example = "sqlVal(dataSource=default): select count(*) from emp",
			params = {
				@PrefixExprParam(name = "dataSource", type = "String", defaultValue = "Default data-source", 
					description = "Data-source on which specified query has to be executed.")
			})
	public PrefixEpression sqlVal(PrefixExpressionContext parserContext, String expression)
	{
		return new PrefixEpression()
		{
			@Override
			public Object getValue() throws Exception
			{
				return QueryUtils.fetchSingleValue(parserContext.getAutomationContext(), parserContext.getParameter("dataSource"), 
						AutomationUtils.getStringValue(parserContext, expression));
			}
		};
	}

	@PrefixExpressionAnnot(type = "sqlColumnList", description = "Used to fetch first column values as list.", 
			example = "sqlColumnList: select NAME from emp",
			params = {
				@PrefixExprParam(name = "dataSource", type = "String", defaultValue = "Default data-source", 
						description = "Data-source on which specified query has to be executed.")
			})
	public PrefixEpression sqlColumnList(PrefixExpressionContext parserContext, String expression)
	{
		return new PrefixEpression()
		{
			@Override
			public Object getValue() throws Exception
			{
				return QueryUtils.fetchColumnList(parserContext.getAutomationContext(), parserContext.getParameter("dataSource"), 
						AutomationUtils.getStringValue(parserContext, expression));
			}
		};
	}

	@PrefixExpressionAnnot(type = "sqlMap", description = "Used to fetch map from specified key column and value column (each row makes entry into result map).", 
			example = "sqlMap(keyColumn=ID, valueColumn=NAME): select ID, NAME from emp",
			params = {
				@PrefixExprParam(name = "dataSource", type = "String", defaultValue = "Default data-source", 
						description = "Data-source on which specified query has to be executed."),
				@PrefixExprParam(name = "keyColumn", type = "String", required = true,
					description = "Mandatory Param. Key column to be used."),
				@PrefixExprParam(name = "valueColumn", type = "String", required = true,
					description = "Mandatory Param. Value column to be used.")
			})
	public PrefixEpression sqlMap(PrefixExpressionContext parserContext, String expression)
	{
		return new PrefixEpression()
		{
			@Override
			public Object getValue() throws Exception
			{
				String keyCol = parserContext.getParameter("keyColumn");
				String valCol = parserContext.getParameter("valueColumn");
				
				if(StringUtils.isBlank(keyCol))
				{
					throw new InvalidArgumentException("No key column provided for 'sqlMap' filter-expression.");
				}
				
				if(StringUtils.isBlank(valCol))
				{
					throw new InvalidArgumentException("No value column provided for 'sqlMap' filter-expression.");
				}

				return QueryUtils.fetchQueryMap(parserContext.getAutomationContext(), parserContext.getParameter("dataSource"), 
						expression, keyCol, valCol);
			}
		};
	}

	@PrefixExpressionAnnot(type = "sqlRowMaps", description = "Used to fetch rows as maps (Column name will be used as key). Result will be list of maps.", 
			example = "sqlRowMaps: select ID, NAME, ADDRESS, MANAGER from emp",
			params = {
				@PrefixExprParam(name = "dataSource", type = "String", defaultValue = "Default data-source", 
						description = "Data-source on which specified query has to be executed."),
				@PrefixExprParam(name = "processAllRows", type = "boolean", defaultValue = "true",
					description = "Defaults to true, which means all rows will be processed. If made false, then "
							+ "only first row will be processed.")
			})
	public PrefixEpression sqlRowMaps(PrefixExpressionContext parserContext, String expression)
	{
		return new PrefixEpression()
		{
			@Override
			public Object getValue() throws Exception
			{
				boolean processAllRows = "false".equalsIgnoreCase(parserContext.getParameter("processAllRows")) ? false : true;

				return QueryUtils.fetchRowMaps(parserContext.getAutomationContext(), parserContext.getParameter("dataSource"), 
						AutomationUtils.getStringValue(parserContext, expression),
						processAllRows);
			}
		};
	}
}
