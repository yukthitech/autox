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

import org.apache.commons.beanutils.PropertyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Utility to compare 2 objects deeply.
 * @author akiran
 */
public class DeepEqualsProcessor
{
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private boolean ignoreExtraProperties;
	
	private String pathFailed;
	
	private IExecutionLogger logger;
	
	/**
	 * In case specified, in case of errors, list path will
	 * include any of these available keys.
	 */
	private List<String> listKeys;
	
	private DeepEqualsProcessor(boolean ignoreExtraProperties, AutomationContext context)
	{
		this.ignoreExtraProperties = ignoreExtraProperties;
		this.logger = context.getExecutionLogger();
	}
	
	public DeepEqualsProcessor setListKeys(List<String> listKeys)
	{
		this.listKeys = listKeys;
		return this;
	}

	public static DeepEqualsProcessor newProcessor(boolean ignoreExtraProperties, AutomationContext context)
	{
		/*
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
		*/
		

		DeepEqualsProcessor instance = new DeepEqualsProcessor(ignoreExtraProperties, context);
		//boolean compareRes = instance.deepCompare(actualJson, expectedJson, "$");
		
		//return compareRes ? null : instance.pathFailed;
		return instance;
	}
	
	public String deepCompare(Object actual, Object expected)
	{
		Object actualJson = toJsonObject(actual);
		Object expectedJson = toJsonObject(expected);
		
		boolean compareRes = deepCompare(actualJson, expectedJson, "$");
		
		return compareRes ? null : this.pathFailed;
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
	
	private boolean deepCompare(Map<String, Object> actual, Map<String, Object> expected, String propPath)
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
			
			if(!deepCompare(actualVal, expectedVal, propPath + "." + key))
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
	
	private String listIndex(int index, Object val)
	{
		if(listKeys == null || !(val instanceof Map))
		{
			return "[" + index + "]";
		}
		
		for(String key : listKeys)
		{
			Object lstVal = null;
			
			try
			{
				lstVal = PropertyUtils.getProperty(val, key);
			}catch(Exception ex)
			{
				//assume the property that does not exist
			}
			
			if(lstVal != null)
			{
				return String.format("[%s@%s=%s]", index, key, lstVal);
			}
		}
		
		return "[" + index + "]";
	}

	private boolean deepCompare(List<Object> actual, List<Object> expected, String propPath)
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
			
			if(!deepCompare(actualVal, expectedVal, propPath + listIndex(i, expectedVal)))
			{
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean deepCompare(Object actual, Object expected, String propPath)
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
			return deepCompare((Map<String, Object>) actual, (Map<String, Object>) expected, propPath);
		}
		
		if(actual instanceof List)
		{
			return deepCompare((List<Object>) actual, (List<Object>) expected, propPath);
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
