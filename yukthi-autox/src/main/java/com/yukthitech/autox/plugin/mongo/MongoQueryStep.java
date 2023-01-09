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
@Executable(name = "mongoQuery", group = Group.Mongodb, requiredPluginTypes = MongoPlugin.class, message = "Executes specified mongo Query on specified mongo resource. "
		+ "Syntax of queries  can be found at https://docs.mongodb.com/manual/reference/command.")
public class MongoQueryStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute. Can be json string or a map object.", sourceType = SourceType.EXPRESSION)
	private Object query;

	/**
	 * Mongo Resource to be used for query execution.
	 */
	@Param(description = "Mongo Resource to be used for query execution.")
	private String mongoResourceName;

	/**
	 * Name of the attribute to be used to set the result.
	 */
	@Param(description = "Name of the attribute to be used to set the result.", required = false, attrName = true, defaultValue = "result")
	private String resultAttribute = "result";
	
	@Param(description = "If true, fmarker expression should be processed in the query post json parsing.", required = false, attrName = true, defaultValue = "true")
	private boolean replaceExpressions = true;
	
	public void setQuery(Object query)
	{
		this.query = query;
	}

	public void setMongoResourceName(String mongoResourceName)
	{
		this.mongoResourceName = mongoResourceName;
	}

	public void setResultAttribute(String resultAttribute)
	{
		this.resultAttribute = resultAttribute;
	}
	
	public void setReplaceExpressions(boolean replaceExpressions)
	{
		this.replaceExpressions = replaceExpressions;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		Map<String, Object> result = MongoQueryUtils.execute(context, mongoResourceName, query, replaceExpressions);
		
		exeLogger.debug("Using attribute '{}' setting the obtained result. Result: {}", resultAttribute, result);
		context.setAttribute(resultAttribute, result);
	}
}
