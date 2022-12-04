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

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.DeepEqualsUtil;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Executes specified mongo Query on specified mongo resource defined in https://docs.mongodb.com/manual/reference/command. 
 * And the result is deep-compared with specified expecte object. Extra properties from result are ignored.
 * 
 * @author akiran
 */
@Executable(name = "assertMongo", group = Group.Mongodb, requiredPluginTypes = MongoPlugin.class, message = "Executes specified mongo Query on specified mongo resource. "
		+ "Syntax of queries can be found at https://docs.mongodb.com/manual/reference/command. And the result is deep-compared with specified expecte object. Extra properties from result are ignored.")
public class AssertMongoStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute.")
	private String query;

	/**
	 * Mongo Resource to be used for query execution.
	 */
	@Param(description = "Mongo Resource to be used for query execution.")
	private String mongoResourceName;
	
	/**
	 * Expected value in comparison.
	 */
	@Param(description = "Expected value in comparison.", sourceType = SourceType.EXPRESSION)
	private Object expected;
	
	public void setQuery(String query)
	{
		this.query = query;
	}

	public void setMongoResourceName(String mongoResourceName)
	{
		this.mongoResourceName = mongoResourceName;
	}

	public void setExpected(Object expected)
	{
		this.expected = expected;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		Map<String, Object> result = MongoQuryUtils.execute(context, exeLogger, mongoResourceName, query);
		
		exeLogger.debug("Got result of the query as: {}", result);

		String diffPath = DeepEqualsUtil.deepCompare(result, this.expected, true, context, exeLogger);
		boolean res = (diffPath == null);
		
		if(!res)
		{
			throw new AutoxValidationException(this, "Found the expected value is different from result value at path: {}", diffPath);
		}

		exeLogger.debug("Found the query result and expected values are same");
	}
}
