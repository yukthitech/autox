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
import java.util.List;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.event.EventHandler;
import com.yukthitech.autox.event.EventManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents a test data file used to configure test suites and other configurations.
 * @author akiran
 */
public class TestDataFile
{
	/**
	 * Context under which this file is being loaded.
	 */
	private AutomationContext context;
	
	/**
	 * Test suites to be loaded.
	 */
	private List<TestSuite> testSuites = new ArrayList<>();
	
	/**
	 * Setup steps to be executed before executing any test suite.
	 */
	private Setup setup;
	
	/**
	 * Cleanup steps to be executed after executing all test suites.
	 */
	private Cleanup cleanup;
	
	/**
	 * Instantiates a new test data file.
	 *
	 * @param context the context
	 */
	public TestDataFile(AutomationContext context)
	{
		this.context = context;
	}

	/**
	 * Gets the setup steps to be executed before executing any test suite.
	 *
	 * @return the setup steps to be executed before executing any test suite
	 */
	public Setup getSetup()
	{
		return setup;
	}

	/**
	 * Sets the setup steps to be executed before executing any test suite.
	 *
	 * @param setup the new setup steps to be executed before executing any test suite
	 */
	public void setSetup(Setup setup)
	{
		if(this.setup != null)
		{
			throw new InvalidStateException("Multiple setup are specified under single test suite");
		}
		
		this.setup = setup;
	}

	/**
	 * Gets the cleanup steps to be executed after executing all test suites.
	 *
	 * @return the cleanup steps to be executed after executing all test suites
	 */
	public Cleanup getCleanup()
	{
		return cleanup;
	}

	/**
	 * Sets the cleanup steps to be executed after executing all test suites.
	 *
	 * @param cleanup the new cleanup steps to be executed after executing all test suites
	 */
	public void setCleanup(Cleanup cleanup)
	{
		if(this.cleanup != null)
		{
			throw new InvalidStateException("Multiple cleanup are specified under single test suite");
		}
		
		this.cleanup = cleanup;
	}

	/**
	 * Gets the test suites to be loaded.
	 *
	 * @return the test suites to be loaded
	 */
	public List<TestSuite> getTestSuites()
	{
		return testSuites;
	}

	/**
	 * Sets the test suites to be loaded.
	 *
	 * @param testSuites the new test suites to be loaded
	 */
	public void setTestSuites(List<TestSuite> testSuites)
	{
		if(testSuites == null)
		{
			throw new NullPointerException("Test suites can not be null.");
		}
		
		this.testSuites = testSuites;
	}

	/**
	 * Adds value to {@link #testSuites testSuites}
	 *
	 * @param testSuite
	 *            testSuite to be added
	 */
	public void addTestSuite(TestSuite testSuite)
	{
		testSuites.add(testSuite);
	}
	
	/**
	 * Adds specified test group.
	 * @param function group to add.
	 */
	public void addFunction(Function function)
	{
		context.addFunction(function);
	}
	
	public void addEventHandler(EventHandler handler)
	{
		EventManager.getInstance().addEventHandler(handler);
	}

	public void addCustomUiLocator(CustomUiLocator customUiLocator)
	{
		context.addCustomUiLocator(customUiLocator);
	}
	
	@Override
	public String toString()
	{
		return "<global>";
	}
}
