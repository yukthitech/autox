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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractLocationBased;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents a group of test cases to be executed.
 * 
 * @author akiran
 */
public class TestSuite extends AbstractLocationBased implements Validateable, IEntryPoint
{
	/**
	 * Name of the test suite.
	 */
	private String name;
	
	/**
	 * Author names of this test suite.
	 */
	private String author;
	
	/**
	 * Description about the test suite.
	 */
	private String description;

	/**
	 * List of test cases to be executed in this test suite.
	 */
	private Map<String, TestCase> testCases = new LinkedHashMap<>();
	
	/**
	 * Setup steps to be executed before executing test suite.
	 */
	private Setup setup;
	
	/**
	 * Cleanup steps to be executed after executing test suite.
	 */
	private Cleanup cleanup;
	
	/**
	 * Name to step group mapping.
	 */
	private Map<String, Function> nameToFunction = new HashMap<>();
	
	/**
	 * If enabled, underlying test-cases will be executed in parallel. 
	 */
	private boolean parallelExecutionEnabled = false;
	
	/**
	 * Setup to be executed after every test case.
	 */
	private Setup beforeTestCase;
	
	/**
	 * Cleanup to be executed after every test case.
	 */
	private Cleanup afterTestCase;
	
	public TestSuite()
	{}
	
	public TestSuite(String name)
	{
		this.name = name;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public void setAuthor(String author)
	{
		if(StringUtils.isNotBlank(author))
		{
			Set<String> authors = new TreeSet<>(Arrays.asList(author.trim().split("\\s*\\,\\s*")));
			this.author = authors.stream().collect(Collectors.joining(", "));
		}
	}

	public boolean isParallelExecutionEnabled()
	{
		return parallelExecutionEnabled;
	}

	public void setParallelExecutionEnabled(boolean parallelExecutionEnabled)
	{
		this.parallelExecutionEnabled = parallelExecutionEnabled;
	}

	public void merge(TestSuite newTestSuite, boolean forReload)
	{
		if(newTestSuite.getSetup() != null)
		{
			if(this.setup != null && !forReload)
			{
				throw new InvalidConfigurationException("For test-suite '{}' duplicate setups are configured. [Location1: {}:{}, Location2: {}:{}]", 
						this.name, 
						this.setup.getLocation(), this.setup.getLineNumber(), 
						newTestSuite.setup.getLocation(), newTestSuite.setup.getLineNumber());
			}
			
			this.setup = newTestSuite.setup;
		}
		
		if(newTestSuite.cleanup != null)
		{
			if(this.cleanup != null && !forReload)
			{
				throw new InvalidConfigurationException("For test-suite '{}' duplicate cleanups are configured. [Location1: {}:{}, Location2: {}:{}]", 
						this.name, 
						this.cleanup.getLocation(), this.cleanup.getLineNumber(), 
						newTestSuite.cleanup.getLocation(), newTestSuite.cleanup.getLineNumber());
			}
			
			this.cleanup = newTestSuite.cleanup;
		}
		
		if(newTestSuite.testCases != null)
		{
			if(this.testCases == null)
			{
				this.testCases = newTestSuite.testCases;
			}
			else
			{
				this.testCases.putAll(newTestSuite.testCases);
			}
			
			this.testCases.values().forEach(tc -> tc.setParentTestSuite(this));
		}
		
		if(StringUtils.isNotBlank(newTestSuite.author))
		{
			Set<String> authors = new TreeSet<>(Arrays.asList(newTestSuite.author.trim().split("\\s*\\,\\s*")));
			
			if(StringUtils.isNotBlank(author))
			{
				authors.addAll(Arrays.asList(author.trim().split("\\s*\\,\\s*")));
			}
			
			this.author = authors.stream().collect(Collectors.joining(", "));
		}
		
		if(newTestSuite.afterTestCase != null)
		{
			if(this.afterTestCase == null || forReload)
			{
				this.afterTestCase = newTestSuite.afterTestCase;
			}
			else
			{
				throw new InvalidStateException("In test-suite '{}', after-test-case is defined more than once.", this.name);
			}
		}
		
		if(newTestSuite.beforeTestCase != null)
		{
			if(this.beforeTestCase == null || forReload)
			{
				this.beforeTestCase = newTestSuite.beforeTestCase;
			}
			else
			{
				throw new InvalidStateException("In test-suite '{}', before-test-case is defined more than once.", this.name);
			}
		}

		this.nameToFunction.putAll(newTestSuite.nameToFunction);
	}

	/**
	 * Gets the name of the test suite.
	 *
	 * @return the name of the test suite
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the test suite.
	 *
	 * @param name
	 *            the new name of the test suite
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the description about the test suite.
	 *
	 * @return the description about the test suite
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about the test suite.
	 *
	 * @param description the new description about the test suite
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Fetches the test case with specified name.
	 * @param name
	 * @return
	 */
	public TestCase getTestCase(String name)
	{
		return testCases.get(name);
	}
	
	public boolean hasAnyTestCases(Set<String> names)
	{
		return CollectionUtils.containsAny(testCases.keySet(), names);
	}

	/**
	 * Gets the list of test cases to be executed in this test suite.
	 *
	 * @return the list of test cases to be executed in this test suite
	 */
	public List<TestCase> getTestCases()
	{
		return new ArrayList<>(testCases.values());
	}

	/**
	 * Adds value to {@link #testCases testCases}.
	 *
	 * @param testCase
	 *            testCase to be added
	 */
	public void addTestCase(TestCase testCase)
	{
		TestCase oldTestCase = this.testCases.get(testCase.getName());
		
		if(oldTestCase != null)
		{
			throw new InvalidArgumentException("Duplicate test case name encountered: " + testCase.getName());
		}
		
		testCases.put(testCase.getName(), testCase);
		testCase.setParentTestSuite(this);
	}

	/**
	 * Adds specified data bean to this application.
	 * @param name Name of the data bean.
	 * @param bean Bean to be added.
	 */
	public void addDataBean(String name, Object bean)
	{
		ApplicationConfiguration.getInstance().addDataBean(name, bean);
	}

	/**
	 * Gets the setup steps to be executed before executing test suite.
	 *
	 * @return the setup steps to be executed before executing test suite
	 */
	public Setup getSetup()
	{
		return setup;
	}

	/**
	 * Sets the setup steps to be executed before executing test suite.
	 *
	 * @param setup the new setup steps to be executed before executing test suite
	 */
	public void setSetup(Setup setup)
	{
		if(this.setup != null)
		{
			throw new InvalidConfigurationException("For test-suite '{}' duplicate setups are configured. [Location1: {}:{}, Location2: {}:{}]", 
					this.name, 
					this.setup.getLocation(), this.setup.getLineNumber(), 
					setup.getLocation(), setup.getLineNumber());
		}
		
		this.setup = setup;
	}

	/**
	 * Gets the cleanup steps to be executed after executing test suite.
	 *
	 * @return the cleanup steps to be executed after executing test suite
	 */
	public Cleanup getCleanup()
	{
		return cleanup;
	}

	/**
	 * Sets the cleanup steps to be executed after executing test suite.
	 *
	 * @param cleanup the new cleanup steps to be executed after executing test suite
	 */
	public void addCleanup(Cleanup cleanup)
	{
		if(this.cleanup != null)
		{
			throw new InvalidConfigurationException("For test-suite '{}' duplicate cleanups are configured. [Location1: {}:{}, Location2: {}:{}]", 
					this.name, 
					this.cleanup.getLocation(), this.cleanup.getLineNumber(), 
					cleanup.getLocation(), cleanup.getLineNumber());
		}
		
		this.cleanup = cleanup;
	}

	/**
	 * Adds specified test group.
	 * @param function group to add.
	 */
	public void addFunction(Function function)
	{
		if(StringUtils.isEmpty(function.getName()))
		{
			throw new InvalidArgumentException("Step group can not be added without name");
		}
		
		if(nameToFunction.containsKey(function.getName()))
		{
			throw new InvalidStateException("Duplicate step group name encountered: {}", function.getName());
		}
		
		nameToFunction.put(function.getName(), function);
	}

	/**
	 * Adds or replaces a function in this test suite.
	 * This is expected to be called from interactive handler on need basis.
	 * @param function
	 */
	public void addOrReplaceFunction(Function function)
	{
		nameToFunction.put(function.getName(), function);
	}
	
	/**
	 * Fetches the step group with specified name.
	 * @param name name of step group.
	 * @return matching group
	 */
	public Function getFunction(String name)
	{
		return nameToFunction.get(name);
	}
	
	public Setup getBeforeTestCase()
	{
		return beforeTestCase;
	}

	public void setBeforeTestCase(Setup beforeTestCase)
	{
		this.beforeTestCase = beforeTestCase;
	}

	public Cleanup getAfterTestCase()
	{
		return afterTestCase;
	}

	public void setAfterTestCase(Cleanup afterTestCase)
	{
		this.afterTestCase = afterTestCase;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(name == null || name.trim().length() == 0)
		{
			throw new ValidateException("No name is provided for test suite.");
		}
	}
	
	/**
	 * Orders testcases in order as per dependencies.
	 */
	private void orderTestCases(TestCase testCase, LinkedHashSet<String> ordered, List<String> inProgress)
	{
		String testCaseName = testCase.getName();
		
		if(inProgress.contains(testCaseName))
		{
			String path = inProgress.stream().collect(Collectors.joining(" => "));
			path += " => " + testCaseName;
			
			throw new InvalidConfigurationException("For testcase '{}' ciruclar dependency occurred at path: {}", testCase.getName(), path);
		}
		
		if(ordered.contains(testCaseName))
		{
			return;
		}
		
		String depStr = testCase.getDependencies();
		
		if(StringUtils.isEmpty(depStr))
		{
			ordered.add(testCase.getName());					
			return;
		}

		String depNames[] = depStr.trim().split("\\s*\\,\\s*");
		
		inProgress.add(testCaseName);
			
		for(String depName : depNames)
		{
			//if required dependency is already added
			if(ordered.contains(depName))
			{
				continue;
			}
			
			if(!this.testCases.containsKey(depName))
			{
				throw new InvalidConfigurationException("For testcase '{}' invalid dependency testcase specified: {}", testCase.getName(), depName);
			}
			
			TestCase depTestCase = this.testCases.get(depName);
			orderTestCases(depTestCase, ordered, inProgress);
		}
		
		inProgress.remove(testCaseName);
		ordered.add(testCaseName);
	}
	
	public List<TestCase> fetchOrderedTestCases()
	{
		LinkedHashSet<String> ordered = new LinkedHashSet<String>();
		List<String> inProgress = new ArrayList<String>();

		for(TestCase testCase : this.testCases.values())
		{
			orderTestCases(testCase, ordered, inProgress);
		}
		
		List<TestCase> orderedTestCases = new ArrayList<TestCase>();
		ordered.forEach(name -> orderedTestCases.add(testCases.get(name)));
		
		return orderedTestCases;
	}

	@Override
	public String toText()
	{
		return toString();
	}

	@Override
	public String toString()
	{
		return "[TS: " + name + "]";
	}
}
