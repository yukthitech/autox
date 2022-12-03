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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Executes specified mongo Query on specified mongo resource defined in https://docs.mongodb.com/manual/reference/command.
 * 
 * @author akiran
 */
@Executable(name = "mongoMultiQuery", group = Group.Mongodb, requiredPluginTypes = MongoPlugin.class, message = "Executes specified multiple mongo Query on specified mongo resource. "
		+ "Syntax of queries  can be found at https://docs.mongodb.com/manual/reference/command.")
public class MongoMultiQueryStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Query to execute.
	 */
	@Param(description = "Query(ies) to execute. Query can be json-string or a map object.", sourceType = SourceType.EXPRESSION)
	private List<Object> queries = new ArrayList<>();

	/**
	 * Mongo Resource to be used for query execution.
	 */
	@Param(description = "Mongo Resource to be used for query execution.")
	private String mongoResourceName;

	public void addQuery(Object query)
	{
		this.queries.add(query);
	}

	public void setMongoResourceName(String mongoResourceName)
	{
		this.mongoResourceName = mongoResourceName;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		for(Object query : this.queries)
		{
			exeLogger.debug("Executing query: {}", query);
			Map<String, Object> result = MongoQuryUtils.execute(context, exeLogger, mongoResourceName, query);
		
			exeLogger.debug("Got result of query as: {}", result);
		}
	}
}
