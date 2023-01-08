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
package com.yukthitech.autox.common;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Utility to compare 2 objects deeply.
 * @author akiran
 */
public class DeepEqualsUtil
{
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private boolean ignoreExtraProperties;
	
	private String pathFailed;
	
	private DeepEqualsUtil(boolean ignoreExtraProperties)
	{
		this.ignoreExtraProperties = ignoreExtraProperties;
	}

	/**
	 * Deep compare actual and expected objects.
	 * @param actual actual object
	 * @param expected expected object
	 * @param ignoreExtraProperties flag indicating if extra properties of 
	 * @param context
	 * @param exeLogger
	 * @return
	 */
	public static String deepCompare(Object actual, Object expected, boolean ignoreExtraProperties, AutomationContext context, IExecutionLogger exeLogger)
	{
		//if both are null
		if(actual == null && expected == null)
		{
			return null;
		}
		
		//if both are not null but one of object is null
		if(actual == null || expected == null)
		{
			return "$";
		}
		
		Object actualJson = toJsonObject(actual);
		Object expectedJson = toJsonObject(expected);

		DeepEqualsUtil instance = new DeepEqualsUtil(ignoreExtraProperties);
		boolean compareRes = instance.deepCompare(actualJson, expectedJson, "$", context, exeLogger);
		
		return compareRes ? null : instance.pathFailed;
	}

	/**
	 * Converts input object into map of maps using json.
	 * @param obj
	 * @return
	 */
	private static Object toJsonObject(Object obj)
	{
		try
		{
			String json = objectMapper.writeValueAsString(obj);
			return objectMapper.readValue(json, Object.class);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting object into json object: {}", obj);
		}
	}
	
	private boolean deepCompare(Map<String, Object> actual, Map<String, Object> expected, String propPath, AutomationContext context, IExecutionLogger logger)
	{
		if(!ignoreExtraProperties)
		{
			if(actual.size() != expected.size())
			{
				pathFailed = propPath;
				
				logger.warn("Comparison failed because of non-matching map-size at path: {} [Actual's size: {}, Expected's size: {}]", 
						propPath, actual.size(), expected.size());
				return false;
			}
		}
		
		Object expectedVal = null, actualVal = null;
		
		for(String key : expected.keySet())
		{
			expectedVal = expected.get(key);
			actualVal = actual.get(key);
			
			if(!deepCompare(actualVal, expectedVal, propPath + "." + key, context, logger))
			{
				return false;
			}
		}
		
		if(ignoreExtraProperties)
		{
			return true;
		}
		
		for(String key : actual.keySet())
		{
			//if a key present in actual is not present in expected
			// Note: common properties are already verified.
			if(!expected.containsKey(key))
			{
				pathFailed = propPath;
				
				logger.warn("Comparison failed because of expected map is not having key: {}", 
						propPath, key);
				return false;
			}
		}

		return true;
	}

	private boolean deepCompare(List<Object> actual, List<Object> expected, String propPath, AutomationContext context, IExecutionLogger logger)
	{
		if(!ignoreExtraProperties)
		{
			if(actual.size() != expected.size())
			{
				pathFailed = propPath;
				
				logger.warn("Comparison failed because of non-matching list-size at path: {} [Actual's size: {}, Expected's size: {}]", 
						propPath, actual.size(), expected.size());
				return false;
			}
		}
		
		Object expectedVal = null, actualVal = null;
		int size = expected.size();
		
		//if the actual size is less than actual size, return false
		if(actual.size() < size)
		{
			pathFailed = propPath;
			
			logger.warn("Comparison failed because of non-matching list-size at path: {} [Actual's size: {}, Expected's size: {}]", 
					propPath, actual.size(), expected.size());
			return false;
		}

		for(int i = 0; i < size; i++)
		{
			expectedVal = expected.get(i);
			actualVal = actual.get(i);
			
			if(!deepCompare(actualVal, expectedVal, propPath + "[" + i + "]", context, logger))
			{
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean deepCompare(Object actual, Object expected, String propPath, AutomationContext context, IExecutionLogger logger)
	{
		//if both are null
		if(actual == null && expected == null)
		{
			return true;
		}
		
		//if both are not null but one of object is null
		if(actual == null || expected == null)
		{
			pathFailed = propPath;
			
			logger.warn("Comparison failed because of null value at path: {} [Actual is Null: {}, Expected is Null: {}]", 
					propPath, (actual == null), (expected == null));
			return false;
		}
		
		if(!actual.getClass().equals(expected.getClass()))
		{
			pathFailed = propPath;
			
			logger.warn("Comparison failed because of incompatible types at path: {} [Actual's type: {}, Expected's type: {}]", 
					propPath, actual.getClass().getName(), expected.getClass().getName());
			return false;
		}
		
		if(actual instanceof Map)
		{
			return deepCompare((Map<String, Object>) actual, (Map<String, Object>) expected, propPath, context, logger);
		}
		
		if(actual instanceof List)
		{
			return deepCompare((List<Object>) actual, (List<Object>) expected, propPath, context, logger);
		}

		boolean res = AutomationUtils.equals(actual, expected);
		
		if(!res)
		{
			pathFailed = propPath;
			logger.warn("Comparison failed because of non-equal values at path: {} [Actual Val: {}, Expected Val: {}]", 
					propPath, actual, expected);
		}
		
		return res;
	}

}
