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
package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractLocationBased;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents a group of test-suite and test-cases to be executed.
 * 
 * @author akiran
 */
public class ExecutionSuite extends AbstractLocationBased implements Validateable
{
	/**
	 * Name of the execution suite.
	 */
	private String name;

	/**
	 * Description about the execution suite.
	 */
	private String description;

	/**
	 * Execution suite entries.
	 */
	private List<ExecutionSuiteEntry> entries = new ArrayList<>();
	
	private Map<String, Set<String>> testSuiteMap;

	public ExecutionSuite()
	{}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<ExecutionSuiteEntry> getEntries()
	{
		return entries;
	}

	public void setEntries(List<ExecutionSuiteEntry> entries)
	{
		this.entries = entries;
	}
	
	public void addEntry(ExecutionSuiteEntry entry)
	{
		entries.add(entry);
	}
	
	public void validateAndBuild(TestSuiteGroup testSuiteGroup)
	{
		for(ExecutionSuiteEntry entry : entries)
		{
			TestSuite testSuite = testSuiteGroup.getTestSuite(entry.getTestSuite());
			
			if(testSuite == null)
			{
				throw new InvalidStateException("Invalid test suite name '{}' specified in execution suite: {}", entry.getTestSuite(), name);
			}
			
			// testcases will be null if full test suite has to be executed
			if(entry.getTestCases() == null)
			{
				// populate current entry with all available test case names
				Set<String> testCases = testSuite.getTestCases()
						.stream()
						.map(tc -> tc.getName())
						.collect(Collectors.toSet());
				
				entry.setTestCases(testCases);
				continue;
			}
			
			for(String testCase : entry.getTestCases())
			{
				if(testSuite.getTestCase(testCase) == null)
				{
					throw new InvalidStateException("Invalid test case name '{}' specified in test-suite '{}' under execution suite: {}", 
							testCase, entry.getTestSuite(), name);
				}
			}
		}
	}
	
	public Map<String, Set<String>> getTestSuiteMap()
	{
		if(testSuiteMap != null)
		{
			return testSuiteMap;
		}
		
		Map<String, Set<String>> map = new LinkedHashMap<String, Set<String>>();
		
		for(ExecutionSuiteEntry entry : entries)
		{
			if(map.containsKey(entry.getTestSuite()))
			{
				map.get(entry.getTestSuite()).addAll(entry.getTestCases());				
			}
			else
			{
				map.put(entry.getTestSuite(), new LinkedHashSet<String>(entry.getTestCases()));
			}
		}
		
		this.testSuiteMap = map;
		return testSuiteMap;
	}
	
	
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Name can not be null or blank");
		}
		
		if(CollectionUtils.isEmpty(entries))
		{
			throw new ValidateException("No entries specified for execution suite - " + name);
		}
	}

	@Override
	public String toString()
	{
		return "[ES: " + name + "]";
	}
}
