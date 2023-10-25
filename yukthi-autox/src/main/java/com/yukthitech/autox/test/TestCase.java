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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AbstractLocationBasedStepContainer;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.dataprovider.DefaultDataProvider;
import com.yukthitech.autox.dataprovider.IDataProvider;
import com.yukthitech.autox.dataprovider.ListDataProvider;
import com.yukthitech.autox.dataprovider.RangeDataProvider;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.ObjectWrapper;

/**
 * Test case with validations to be executed.
 */
public class TestCase extends AbstractLocationBasedStepContainer implements IStepContainer, Validateable, IEntryPoint
{
	private static Logger logger = LogManager.getLogger(TestCase.class);
	
	/**
	 * Name of the test case.
	 */
	private String name;
	
	/**
	 * Author of the test cases.
	 */
	private String author;

	/**
	 * Description about test case.
	 */
	private String description;
	
	/**
	 * Groups to which this test case belong.
	 */
	private Set<String> groups = new HashSet<>();
	
	/**
	 * Extra tags to this testcase, which will be accessible via meta data.
	 */
	private Map<String, String> tags = new HashMap<>();
	
	/**
	 * Dependency test cases within the current test suite. Dependencies are considered valid only if they occur
	 * in the same test suite and occurs before current test suite. If a valid dependency test case
	 * was not executed successfully then current test case will be skipped.
	 */
	private String dependencies;

	/**
	 * Details of the exception expected from this test case.
	 */
	private ExpectedException expectedException;
	
	/**
	 * Data provider to be used for this test case.
	 */
	private IDataProvider dataProvider;
	
	/**
	 * Used only when data provider is specified. If enabled data provider test cases executes in parallel.
	 */
	private boolean parallelExecutionEnabled = false;
	
	/**
	 * Flag indicating context has to be shared between data-provider executions.
	 * In parallel execution, this flag will be ignored.
	 */
	private boolean sharedContext = false;

	/**
	 * List of failure actions to be invoked when test case is failed.
	 * This should include diagnosis of different components playing role
	 * in this test case.
	 */
	private List<TestCaseFailureAction> failureActions;
	
	/**
	 * Steps for setup for this test case.
	 */
	private Setup setup;
	
	/**
	 * Steps for cleanup for this test case.
	 */
	private Cleanup cleanup;
	
	/**
	 * Data setup to be used. This will be invoked only when data-provider is specified
	 * and will be executed before any data-test-case is executed.
	 */
	private Setup dataSetup;
	
	/**
	 * Data cleanup to be used. This will be invoked only when data-provider is specified
	 * and will be executed after all data-test-cases are executed.
	 */
	private Cleanup dataCleanup;
	
	/**
	 * The data.
	 */
	private TestCaseData data;
	
	/**
	 * Parent test suite.
	 */
	private TestSuite parentTestSuite;
	
	/**
	 * Instantiates a new test case.
	 */
	public TestCase()
	{}
	
	/**
	 * Instantiates a new test case.
	 *
	 * @param name the name
	 */
	public TestCase(String name)
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

	/**
	 * Sets the parent test suite.
	 *
	 * @param parentTestSuite the new parent test suite
	 */
	public void setParentTestSuite(TestSuite parentTestSuite)
	{
		this.parentTestSuite = parentTestSuite;
	}
	
	/**
	 * Gets the parent test suite.
	 *
	 * @return the parent test suite
	 */
	public TestSuite getParentTestSuite()
	{
		return parentTestSuite;
	}
	
	/**
	 * Adds the group.
	 *
	 * @param name the name
	 */
	public void addGroups(String groups)
	{
		if(StringUtils.isBlank(groups))
		{
			return;
		}
		
		String names[] = groups.trim().split("\\s*\\,\\s*");
		this.groups.addAll(Arrays.asList(names));
	}
	
	public void addTag(String name, String value)
	{
		this.tags.put(name, value);
	}
	
	public Map<String, String> getTags()
	{
		return tags;
	}
	
	public MetaInfo getMetaInfo()
	{
		return new MetaInfo()
				.setFilePath(super.getLocation().getPath())
				.setLineNumber(super.getLineNumber())
				.setGroups(groups)
				.setTags(tags);
	}
	
	/**
	 * Checks whether this test case can be executed based on excluded groups.
	 * @param context
	 * @param excludedGroup in case of exclusion, this wrapper will be set with group which is excluded.
	 * @return
	 */
	public boolean isExecutable(ObjectWrapper<String> excludedGroup)
	{
		Set<String> executableGroups = AutomationContext.getInstance().getExecutableGroups();
		boolean executableGroupsSpecified = CollectionUtils.isNotEmpty(executableGroups);
		
		if(groups.isEmpty())
		{
			//if no group is specified at test case level
			//  then return true if no executable groups are specified otherwise false
			return !executableGroupsSpecified;
		}
		
		ApplicationConfiguration appConfig = AutomationContext.getInstance().getAppConfiguration();
		//by default, tc is executable if executable-groups are not specified
		boolean executable = !executableGroupsSpecified;
		
		for(String grp : this.groups)
		{
			//if executable groups is specified and current group is part
			//  of executable groups, then mark test case as executable
			if(executableGroupsSpecified && executableGroups.contains(grp))
			{
				executable = true;
			}
			
			//if current group is excluded in app config
			if(appConfig.isGroupExcluded(grp))
			{
				logger.debug("Test case '{}' is non-executable as its group '{}' is under exclusion list", name, grp);
				
				if(excludedGroup != null)
				{
					excludedGroup.setValue(grp);
				}
				
				return false;
			}
		}
		
		return executable;
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(TestCaseData data)
	{
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.test.IEntryPoint#toText()
	 */
	@Override
	public String toText()
	{
		if(data != null)
		{
			return "[TC: " + name + " - " + data.getName() + "]";	
		}
		
		return "[TC: " + name + "]";
	}

	/**
	 * Gets the name of the test case.
	 *
	 * @return the name of the test case
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the test case.
	 *
	 * @param name
	 *            the new name of the test case
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the dependency test cases within the current test suite. Dependencies are considered valid only if they occur in the same test suite and occurs before current test suite. If a valid dependency test case was not executed successfully then current test case will be skipped.
	 *
	 * @return the dependency test cases within the current test suite
	 */
	public String getDependencies()
	{
		return dependencies;
	}

	/**
	 * Sets the dependency test cases within the current test suite. Dependencies are considered valid only if they occur in the same test suite and occurs before current test suite. If a valid dependency test case was not executed successfully then current test case will be skipped.
	 *
	 * @param dependencies the new dependency test cases within the current test suite
	 */
	public void setDependencies(String dependencies)
	{
		this.dependencies = dependencies;
	}
	
	/**
	 * Sets the data provider to be used for this test case.
	 *
	 * @param dataProvider the new data provider to be used for this test case
	 */
	public void setDataProvider(IDataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}
	
	/**
	 * Gets the data provider to be used for this test case.
	 *
	 * @return the data provider to be used for this test case
	 */
	public IDataProvider getDataProvider()
	{
		return dataProvider;
	}
	
	/**
	 * Sets the specified list data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setListDataProvider(ListDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}
	
	/**
	 * Sets the default data provider for this test case.
	 * @param dataProvider
	 */
	public void setDefaultDataProvider(DefaultDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}

	/**
	 * Sets the specified range data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setRangeDataProvider(RangeDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}

	public boolean isParallelExecutionEnabled()
	{
		return parallelExecutionEnabled;
	}

	public void setParallelExecutionEnabled(boolean parallelExecutionEnabled)
	{
		this.parallelExecutionEnabled = parallelExecutionEnabled;
	}

	public boolean isSharedContext()
	{
		return sharedContext;
	}

	public void setSharedContext(boolean sharedContext)
	{
		this.sharedContext = sharedContext;
	}

	/**
	 * Fetches dependencies as a set.
	 * @return dependencies set
	 */
	public Set<String> getDependenciesSet()
	{
		if(StringUtils.isBlank(dependencies))
		{
			return null;
		}
		
		String depArr[] = dependencies.trim().split("\\s*\\,\\s*");
		return new HashSet<>(Arrays.asList(depArr));
	}

	/**
	 * Gets the description about test case.
	 *
	 * @return the description about test case
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about test case.
	 *
	 * @param description
	 *            the new description about test case
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Sets the steps for setup for this test case.
	 *
	 * @param setup the new steps for setup for this test case
	 */
	public void setSetup(Setup setup)
	{
		this.setup = setup;
	}
	
	public Setup getSetup()
	{
		return setup;
	}
	
	/**
	 * Sets the steps for cleanup for this test case.
	 *
	 * @param cleanup the new steps for cleanup for this test case
	 */
	public void setCleanup(Cleanup cleanup)
	{
		this.cleanup = cleanup;
	}
	
	public Cleanup getCleanup()
	{
		return cleanup;
	}

	/**
	 * Gets the data setup to be used. This will be invoked only when data-provider is specified and will be executed before any data-test-case is executed.
	 *
	 * @return the data setup to be used
	 */
	public Setup getDataSetup()
	{
		return dataSetup;
	}

	/**
	 * Sets the data setup to be used. This will be invoked only when data-provider is specified and will be executed before any data-test-case is executed.
	 *
	 * @param dataSetup the new data setup to be used
	 */
	public void setDataSetup(Setup dataSetup)
	{
		this.dataSetup = dataSetup;
	}

	/**
	 * Gets the data cleanup to be used. This will be invoked only when data-provider is specified and will be executed after all data-test-cases are executed.
	 *
	 * @return the data cleanup to be used
	 */
	public Cleanup getDataCleanup()
	{
		return dataCleanup;
	}

	/**
	 * Sets the data cleanup to be used. This will be invoked only when data-provider is specified and will be executed after all data-test-cases are executed.
	 *
	 * @param dataCleanup the new data cleanup to be used
	 */
	public void setDataCleanup(Cleanup dataCleanup)
	{
		this.dataCleanup = dataCleanup;
	}

	/**
	 * Sets the details of the exception expected from this test case.
	 *
	 * @param expectedException the new details of the exception expected from this test case
	 */
	public void setExpectedException(ExpectedException expectedException)
	{
		this.expectedException = expectedException;
	}
	
	public ExpectedException getExpectedException()
	{
		return expectedException;
	}

	/**
	 * Adds the failure action.
	 *
	 * @param action the action to be executed on failure.
	 */
	public void addFailureAction(TestCaseFailureAction action)
	{
		if(action == null)
		{
			throw new NullPointerException("Action can not be null.");
		}
		
		if(this.failureActions == null)
		{
			failureActions = new ArrayList<>();
		}
		
		failureActions.add(action);
	}
	
	/**
	 * Gets the list of failure actions to be invoked when test case is failed. This should include diagnosis of different components playing role in this test case.
	 *
	 * @return the list of failure actions to be invoked when test case is failed
	 */
	public List<TestCaseFailureAction> getFailureActions()
	{
		return failureActions;
	}

	/**
	 * Sets the list of failure actions to be invoked when test case is failed. This should include diagnosis of different components playing role in this test case.
	 *
	 * @param failureActions the new list of failure actions to be invoked when test case is failed
	 */
	public void setFailureActions(List<TestCaseFailureAction> failureActions)
	{
		this.failureActions = failureActions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ccg.core.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isEmpty(name))
		{
			throw new ValidateException("No name is provided for test case.");
		}

		if(StringUtils.isEmpty(description))
		{
			throw new ValidateException("No description is provided for test case - " + name);
		}
		
		if(CollectionUtils.isEmpty(steps))
		{
			throw new ValidateException("No steps specified for execution of test case - " + name);
		}
	}
	
	@Override
	public String toString()
	{
		String extra = String.format("TC [Name: %s, Location: %s:%s]", name, super.getLocation(), super.getLineNumber());
		return extra;
	}
}
