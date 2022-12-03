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
package com.yukthitech.autox.test.mongo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.mongojs.IMongoJsCustomizer;
import com.yukthitech.mongojs.MongoJsEngine;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Mongo query related utils.
 * @author akiran
 */
public class MongoQuryUtils
{
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	private static final JavaType QUERY_JAVA_TYPE = TypeFactory.defaultInstance().constructMapType(LinkedHashMap.class, String.class, Object.class);

	/**
	 * Executing specified query and returns the result.
	 * @param context context to be used
	 * @param exeLogger logger to be used
	 * @param mongoResourceName resource on which query needs to be executed
	 * @param query query to be executed
	 * @return result
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> execute(AutomationContext context, IExecutionLogger exeLogger, String mongoResourceName, Object query)
	{
		MongoPluginSession mongoSession = ExecutionContextManager.getInstance().getPluginSession(MongoPlugin.class);
		MongoResource mongoResource = mongoSession.getMongoResource(mongoResourceName);

		if(mongoResource == null)
		{
			throw new InvalidStateException("No Mongo resource found with specified name - {}", mongoResourceName);
		}
		
		exeLogger.debug(false, "On mongo-resource '{}' executing query: \n <code class='JSON'>{}</code>", mongoResourceName, query);

		Map<String, Object> queryMap = null;
		
		if(query instanceof String)
		{
			try
			{
				queryMap = OBJECT_MAPPER.readValue((String) query, QUERY_JAVA_TYPE);
			}catch(Exception ex)
			{
				exeLogger.error("Invalid mongo-query json specified for execution: {}", query);
				throw new InvalidStateException("Invalid mongo-query json specified for execution: {}", query, ex);
			}
		}
		else if(query instanceof Map)
		{
			queryMap = (Map<String, Object>) query;
		}
		else
		{
			throw new InvalidArgumentException("Non-string/non-map value is specified as mongo query. Specified query: %s (Type: %s)", 
					query, query.getClass().getName());
		}
		
		
		queryMap = AutomationUtils.replaceExpressions("mongoQuery", context, queryMap);
		
		MongoClient client = mongoResource.getMongoClient();
		MongoDatabase mongoDatabase = client.getDatabase(mongoResource.getDbName());
		
		Document commandDocument = new Document(queryMap);
		String resultJson = mongoDatabase.runCommand(commandDocument).toJson();
		
		Map<String, Object> result = null;
		
		try
		{
			result = (Map) OBJECT_MAPPER.readValue(resultJson, Object.class);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting result json to object. Result json: {}", resultJson, ex);
		}
		
		return result;
	}

	public static Object executeJs(AutomationContext context, IExecutionLogger exeLogger, String mongoResourceName, String script)
	{
		MongoPluginSession mongoSession = ExecutionContextManager.getInstance().getPluginSession(MongoPlugin.class);
		MongoResource mongoResource = mongoSession.getMongoResource(mongoResourceName);

		if(mongoResource == null)
		{
			throw new InvalidStateException("No Mongo resource found with specified name - {}", mongoResourceName);
		}
		
		exeLogger.debug(false, "On mongo-resource '{}' executing query: \n <code class='JSON'>{}</code>", mongoResourceName, script);

		MongoClient client = mongoResource.getMongoClient();
		MongoDatabase mongoDatabase = client.getDatabase(mongoResource.getDbName());

		MongoJsEngine mongoEngine = new MongoJsEngine(mongoDatabase);
		mongoEngine.setCustomizer(new IMongoJsCustomizer()
		{
			@Override
			public boolean printLog(String mssg)
			{
				exeLogger.debug("[MongoJS] {}", mssg);
				return true;
			}
		});
		
		try
		{
			String finalScript = String.format("function dynMongoJsFunc(){%s} dynMongoJsFunc();", script);
			return mongoEngine.executeScript(finalScript);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing mongo-js script", ex);
		}
	}
}
