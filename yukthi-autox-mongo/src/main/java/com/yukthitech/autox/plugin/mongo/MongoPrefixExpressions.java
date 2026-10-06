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
package com.yukthitech.autox.plugin.mongo;

import java.util.Map;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.ContextMap;
import com.yukthitech.autox.prefix.PrefixEpression;
import com.yukthitech.autox.prefix.PrefixExprParam;
import com.yukthitech.autox.prefix.PrefixExpressionAnnot;
import com.yukthitech.autox.prefix.PrefixExpressionContext;

/**
 * Prefix expressions related to mongo.
 * 
 * @author akranthikiran
 */
public class MongoPrefixExpressions
{
	@PrefixExpressionAnnot(type = "mongo", description = "Used to fetch values using mongo json query.", 
		example = "mongo(resource=default): {\"find\": \"ENVIRONMENT\"}",
		params = {
			@PrefixExprParam(name = "resource", type = "String", defaultValue = "Default mongo-resource", 
				description = "Mongo-resource on which specified query has to be executed."),
			@PrefixExprParam(name = "replaceExpressions", type = "boolean", defaultValue = "true", 
				description = "Flag indicating if free-marker expressions has to be processed post json parsing."),
			@PrefixExprParam(name = "jel", type = "boolean", defaultValue = "false", 
				description = "If set to true, instead of standard json parsing, query will be processed as JEL Json.")
		})
	public PrefixEpression mongo(PrefixExpressionContext parserContext, String expression)
	{
		return new PrefixEpression()
		{
			@Override
			public Object getValue() throws Exception
			{
				boolean replaceExpressions = "false".equalsIgnoreCase(parserContext.getParameter("replaceExpressions")) ? false : true;
				Object query = AutomationUtils.getStringValue(parserContext, expression);
				
				if("true".equalsIgnoreCase(parserContext.getParameter("jel")))
				{
					Map<String, Object> context = new ContextMap(parserContext.getAutomationContext());
					query = IAutomationConstants.JSON_EXPR_ENGINE.processJsonAsObject(expression, context);
				}
				
				return MongoQueryUtils.execute(parserContext.getAutomationContext(), parserContext.getParameter("resource"), query, replaceExpressions);
			}
		};
	}

	@PrefixExpressionAnnot(type = "mongoJs", description = "Used to fetch values using mongo JS query (by yukhi-mongojs).",
		example = "mongoJs: db.ENVIRONMENT.find({})",
		params = {
			@PrefixExprParam(name = "resource", type = "String", defaultValue = "Default mongo-resource", 
				description = "Mongo-resource on which specified query has to be executed.")
		})
	public PrefixEpression mongoJs(PrefixExpressionContext parserContext, String expression)
	{
		return new PrefixEpression()
		{
			@Override
			public Object getValue() throws Exception
			{
				return MongoQueryUtils.executeJs(parserContext.getAutomationContext(), parserContext.getParameter("resource"), 
						AutomationUtils.getStringValue(parserContext, expression));
			}
		};
	}
}
