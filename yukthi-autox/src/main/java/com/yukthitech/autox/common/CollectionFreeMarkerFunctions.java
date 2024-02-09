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
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.annotations.Named;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Collection related free marker functions.
 * @author akiran
 */
@Named("Autox Collection Methods")
public class CollectionFreeMarkerFunctions
{
	@FreeMarkerMethod(
			description = "Creates a new list with specified values",
			returnDescription = "newly created list"
			)
	public static List<Object> newList(
			@FmParam(name = "values", description = "Initial values to be set") Object... values)
	{
		List<Object> lst = new ArrayList<>();
		
		if(values != null && values.length > 0)
		{
			lst.addAll(Arrays.asList(values));
		}
		
		return lst;
	}

	@FreeMarkerMethod(
			description = "Creates a new map with specified values",
			returnDescription = "newly created map"
			)
	public static Map<Object, Object> newMap(
			@FmParam(name = "keyValues", description = "Initial key-value pairs to be set") Object... keyValues)
	{
		return CommonUtils.toMap(keyValues);
	}

	@FreeMarkerMethod(
			description = "Creates a new sorted map with specified values",
			returnDescription = "newly created map"
			)
	public static Map<Object, Object> newSortedMap(
			@FmParam(name = "keyValues", description = "Initial key-value pairs to be set") Object... keyValues)
	{
		return CommonUtils.toSortedMap(keyValues);
	}
	
	@FreeMarkerMethod(
			description = "Adds specified values to the specified collection",
			returnDescription = "Input collection post modification"
			)
	public static Collection<Object> addToCol(
			@FmParam(name = "collection", description = "Collection to be modified") Collection<Object> collection, 
			@FmParam(name = "values", description = "Values to add") Object... values)
	{
		if(values != null)
		{
			collection.addAll(Arrays.asList(values));
		}
		
		return collection;
	}

	@FreeMarkerMethod(
			description = "Removes specified values from the specified collection",
			returnDescription = "Input collection post modification"
			)
	public static Collection<Object> removeFromCol(
			@FmParam(name = "collection", description = "Collection to be modified") Collection<Object> collection, 
			@FmParam(name = "values", description = "Values to remove") Object... values)
	{
		if(values != null)
		{
			collection.removeAll(Arrays.asList(values));
		}
		
		return collection;
	}

	@FreeMarkerMethod(
			description = "Adds specified key-values to the specified map",
			returnDescription = "Input map post modification"
			)
	public static Map<Object, Object> addToMap(
			@FmParam(name = "map", description = "Map to be modified") Map<Object, Object> map, 
			@FmParam(name = "keyValues", description = "Key-value pairs to add") Object... keyValues)
	{
		if(keyValues != null)
		{
			map.putAll(CommonUtils.toMap(keyValues));
		}
		
		return map;
	}

	@FreeMarkerMethod(
			description = "Removes specified keys from the specified map",
			returnDescription = "Input collection post modification"
			)
	public static Map<Object, Object> removeFromMap(
			@FmParam(name = "map", description = "Map to be modified") Map<Object, Object> map, 
			@FmParam(name = "keys", description = "Keys to remove") Object... keys)
	{
		if(keys != null)
		{
			Arrays.asList(keys).forEach(key -> map.remove(key));
		}
		
		return map;
	}
	
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
