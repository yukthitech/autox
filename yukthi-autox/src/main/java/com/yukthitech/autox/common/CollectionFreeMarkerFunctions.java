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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Collection related free marker functions.
 * @author akiran
 */
public class CollectionFreeMarkerFunctions
{
	@FreeMarkerMethod(
			description = "Converts specified list into set.",
			returnDescription = "Converted set."
			)
	public static Set<Object> lstToSet(
			@FmParam(name = "list", description = "List to be converted") List<Object> lst)
	{
		if(lst == null)
		{
			return null;
		}
		
		return new HashSet<>(lst);
	}
	
	@FreeMarkerMethod(
			description = "Converts specified string into list by splitting it using specified delimiter.",
			returnDescription = "Converted list."
			)
	public static List<String> strToList(
			@FmParam(name = "str", description = "String to be converted") String str,
			@FmParam(name = "delim", description = "Delimiter to be used") String delim
			)
	{
		return new ArrayList<String>( Arrays.asList(str.split(delim)) );
	}

	@FreeMarkerMethod(
			description = "Checks if specified submap is submap of supermap",
			returnDescription = "true if comparison is susccessful."
			)
	public static boolean isSubmap(
			@FmParam(name = "superMap", description = "Super-set map in which submap has to be checked") Map<Object, Object> superMap, 
			@FmParam(name = "superMap", description = "Sub-set map which needs to be checked") Map<Object, Object> submap)
	{
		IExecutionLogger logger = AutomationContext.getInstance().getExecutionLogger();
		
		for(Object key : submap.keySet())
		{
			Object superVal = superMap.get(key);
			Object subVal = submap.get(key);
			
			if(!Objects.equals(superVal, subVal))
			{
				logger.debug("Value from super-map '{}' is not matching value from sub-map '{}' for key: {}", superVal, subVal, key);
				return false;
			}
		}
		
		return true;
	}
	
	@FreeMarkerMethod(
			description = "Evaluates the intersection size of specified collections.",
			returnDescription = "intersection size of collections"
			)
	@SuppressWarnings("unchecked")
	public static int intersectionCount(
			@FmParam(name = "collection1", description = "Collection one to be checked") Collection<Object> collection1, 
			@FmParam(name = "collection2", description = "Collection two to be checked") Collection<Object> collection2
			)
	{
		Collection<Object> intersection = CollectionUtils.intersection(collection1, collection2);
		//return the size
		return intersection.size();
	}

	@FreeMarkerMethod(
			description = "Adds to specified value to specified collection",
			returnDescription = "empty string"
			)
	public static String push(
			@FmParam(name = "collection", description = "Collection to which value should be added") Collection<Object> collection, 
			@FmParam(name = "value", description = "Value to add") Object val
			)
	{
		collection.add(val);
		return "";
	}

	@FreeMarkerMethod(
			description = "Removes the element from the end. If list is empty null will be returned.",
			returnDescription = "Returns the element removed"
			)
	public static Object pop(
			@FmParam(name = "list", description = "Collection to which value should be added") List<Object> collection
			)
	{
		return collection.remove(collection.size() - 1);
	}
}
