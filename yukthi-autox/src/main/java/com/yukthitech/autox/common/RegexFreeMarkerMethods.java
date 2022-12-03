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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

public class RegexFreeMarkerMethods
{
	/**
	 * Pattern to extract group names.
	 */
	private static final Pattern GRP_NAME_EXTRACTOR = Pattern.compile("\\(\\?\\<(\\w+)\\>.*?\\)");

	/**
	 * Fetch group names.
	 *
	 * @return the sets the
	 */
	private static Set<String> fetchGroupNames(String regex)
	{
		Matcher matcher = GRP_NAME_EXTRACTOR.matcher(regex);
		Set<String> names = new HashSet<>();
		
		while(matcher.find())
		{
			names.add(matcher.group(1));
		}
		
		return names;
	}
	
	private static Map<String, String> extractGroups(Matcher matcher, Set<String> groups)
	{
		Map<String, String> resMap = new HashMap<>();
		
		for(String grp : groups)
		{
			resMap.put(grp, matcher.group(grp));
		}
		
		return resMap;
	}
	
	private static Matcher getMatcher(String content, String regex)
	{
		Pattern pattern = null;
		
		try
		{
			pattern = Pattern.compile(regex);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid regex specified: {}", regex, ex);
		}
		
		return pattern.matcher(content);
	}
	
	@FreeMarkerMethod(
			description = "Using specified regex tries to find fist match in given content, from that extracts the groups in specified regex.",
			returnDescription = "From the first match map using group name in regex as key and group value from the match as value"
			)
	public static Map<String, String> regexParse(
			@FmParam(name = "content", description = "String in which specified regex match needs to be found") String content, 
			@FmParam(name = "regex", description = "Regex with group names to be extracted from match") String regex)
	{
		IExecutionLogger exeLogger = AutomationContext.getInstance().getExecutionLogger();
		Matcher matcher = getMatcher(content, regex);
		
		if(!matcher.find())
		{
			exeLogger.debug("No match is found with regex {} in content: {}", regex, content);
			return null;
		}

		Set<String> grpNames = fetchGroupNames(regex);
		exeLogger.debug("Found the group names in regex {} as: {}", regex, grpNames);
		
		Map<String, String> res = extractGroups(matcher, grpNames);
		return res;
	}
	
	@FreeMarkerMethod(
			description = "Using specified regex tries to check if content is matched, from that extracts the groups in specified regex.",
			returnDescription = "From the match map using group name in regex as key and group value from the match as value"
			)
	public static Map<String, String> regexParseMatch(
			@FmParam(name = "content", description = "String in which specified regex match needs to be found") String content, 
			@FmParam(name = "regex", description = "Regex with group names to be extracted from match") String regex)
	{
		IExecutionLogger exeLogger = AutomationContext.getInstance().getExecutionLogger();
		Matcher matcher = getMatcher(content, regex);
		
		if(!matcher.matches())
		{
			exeLogger.debug("Regex {} is not matching with specified content: {}", regex, content);
			return null;
		}

		Set<String> grpNames = fetchGroupNames(regex);
		exeLogger.debug("Found the group names in regex {} as: {}", regex, grpNames);
		
		Map<String, String> res = extractGroups(matcher, grpNames);
		return res;
	}

	@FreeMarkerMethod(
			description = "Using specified regex finds all matches for given content, from that extracts the groups in specified regex.",
			returnDescription = "From all matches list of maps are returned. Map uses group name in regex as key and group value from the match as value"
			)
	public static List<Map<String, String>> regexParseAll(
			@FmParam(name = "content", description = "String in which specified regex match needs to be found") String content, 
			@FmParam(name = "regex", description = "Regex with group names to be extracted from match") String regex)
	{
		IExecutionLogger exeLogger = AutomationContext.getInstance().getExecutionLogger();
		Matcher matcher = getMatcher(content, regex);
		
		Set<String> grpNames = fetchGroupNames(regex);
		exeLogger.debug("Found the group names in regex {} as: {}", regex, grpNames);
		
		List<Map<String, String>> resLst = new ArrayList<>();
		
		while(matcher.find())
		{
			resLst.add( extractGroups(matcher, grpNames) );
		}
		
		return resLst;
	}

	@FreeMarkerMethod(
			description = "Checks wether specified content is matching with specified regex.",
			returnDescription = "True if specified content is matching with specified regex"
			)
	public static boolean regexMatches(
			@FmParam(name = "content", description = "String which needs to be evaluated aganist regex") String content, 
			@FmParam(name = "regex", description = "Regex to be used") String regex)
	{
		Matcher matcher = getMatcher(content, regex);
		return matcher.matches();
	}
}
