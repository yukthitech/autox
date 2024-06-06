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
package com.yukthitech.autox.exec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.test.ExecutionSuite;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executor for test suites.
 * @author akranthikiran
 */
public class TestSuiteExecutor extends Executor
{
	private static Logger logger = LogManager.getLogger(TestSuiteExecutor.class);
	
	private TestSuite testSuite;
	
	public TestSuiteExecutor(TestSuite testSuite)
	{
		super(testSuite, "Test-Case");
		
		this.testSuite = testSuite;
		super.setup = testSuite.getSetup();
		super.cleanup = testSuite.getCleanup();
		super.beforeChild = testSuite.getBeforeTestCase();
		super.afterChild = testSuite.getAfterTestCase();
		super.parallelExecutionEnabled = testSuite.isParallelExecutionEnabled();
		
		//fetch restricted test-cases along with dependencies
		Set<String> restrictedTestCases = getRestrictedTestCases();
		
		//add test case executors in order of dependencies
		List<TestCase> orderedTestCases = testSuite.fetchOrderedTestCases();
		Map<String, TestCaseExecutor> executorMap = new HashMap<>();
		Set<String> reqTestCases = fetchRequiredTestCases(orderedTestCases, restrictedTestCases);
		
		logger.debug("For test-suite '{}', got executeable testcases as: {}", testSuite.getName(), reqTestCases);

		//create executor and child dependencies
		for(TestCase testCase : orderedTestCases)
		{
			if(!reqTestCases.contains(testCase.getName()))
			{
				continue;
			}
			
			TestCaseExecutor executor = new TestCaseExecutor(testCase);
			executorMap.put(testCase.getName(), executor);
			super.addChildExector(executor);
			
			Set<String> depLst = testCase.getDependenciesSet();
			
			//if no dependencies are present
			if(CollectionUtils.isEmpty(depLst))
			{
				continue;
			}
			
			//for each dependency
			for(String dep : depLst)
			{
				TestCaseExecutor depExecutor = executorMap.get(dep);
				
				//check if dependency is not present
				if(depExecutor == null)
				{
					throw new InvalidStateException("For test-case '{}' required dependency test-case '{}' is not found or excluded", testCase.getName(), dep);	
				}
				
				executor.addDependency(depExecutor);
			}
		}
		
		logger.debug("Got child executors as: {}", super.getChildExecutors());
	}
	
	private Set<String> fetchRequiredTestCases(List<TestCase> orderedTestCases, Set<String> restrictedTestCases)
	{
		Map<String, TestCase> testCaseMap = orderedTestCases.stream().collect(Collectors.toMap(tc -> tc.getName(), tc -> tc));
		Set<String> reqTestCases = new LinkedHashSet<>();
		ObjectWrapper<String> excludedGroup = new ObjectWrapper<>();
		
		for(TestCase testCase : orderedTestCases)
		{
			String tcUqId = testCase.getUqId();
			
			if(restrictedTestCases != null 
					&& !restrictedTestCases.contains(testCase.getName())
					&& !restrictedTestCases.contains(tcUqId))
			{
				logger.debug("Excluding test-case '{}' [{}] as it is NOT part of restricted test cases", testCase.getName(), tcUqId);
				continue;
			}
			
			if(!testCase.isExecutable(excludedGroup))
			{
				if(StringUtils.isBlank(excludedGroup.getValue()))
				{
					logger.debug("Excluding test-case '{}' as it is not part of executable groups.", testCase.getName());
				}
				else
				{
					logger.debug("Excluding test-case '{}' as it is part of excluded group: {}", testCase.getName(), excludedGroup.getValue());
				}
				
				continue;
			}
			
			markRequired(testCase, testCaseMap, reqTestCases);
		}
		
		return reqTestCases;
	}
	
	private void markRequired(TestCase testCase, Map<String, TestCase> testCaseMap, Set<String> reqTestCases)
	{
		Set<String> depLst = testCase.getDependenciesSet();
		
		//if no dependencies are present
		if(CollectionUtils.isEmpty(depLst))
		{
			reqTestCases.add(testCase.getName());
			return;
		}

		for(String dep : depLst)
		{
			TestCase depTestCase = testCaseMap.get(dep);
			
			if(depTestCase == null)
			{
				throw new InvalidStateException("For test-case '{}' required dependency test-case '{}' is not found or excluded", testCase.getName(), dep);
			}
			
			markRequired(depTestCase, testCaseMap, reqTestCases);
		}
		
		//add current testcase after dependencies are added
		reqTestCases.add(testCase.getName());
	}
	
	private Set<String> getRestrictedTestCases()
	{
		AutomationContext context = AutomationContext.getInstance();
		ExecutionSuite executionSuite = context.getActiveExecutionSuite();
		
		Set<String> restrictedTestCases = executionSuite != null ?
				executionSuite.getTestSuiteMap().get(testSuite.getName()) :
				context.getBasicArguments().getTestCasesSet();
		
		if(CollectionUtils.isEmpty(restrictedTestCases))
		{
			return null;
		}
		
		Set<String> tcWithDep = new HashSet<>();
		
		for(String tc : restrictedTestCases)
		{
			TestCase testCase = testSuite.getTestCase(tc);
			
			if(testCase == null)
			{
				throw new InvalidStateException("Invalid test case name specified for execution: {}", tc);
			}
			
			tcWithDep.add(tc);
			
			Set<String> depLst = testCase.getDependenciesSet();
			
			if(CollectionUtils.isNotEmpty(depLst))
			{
				tcWithDep.addAll(depLst);
			}
		}
		
		return tcWithDep;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(testSuite.getName());

		builder.append("]");
		return builder.toString();
	}
}
