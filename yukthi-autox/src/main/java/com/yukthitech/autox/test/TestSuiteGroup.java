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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class TestSuiteGroup.
 */
public class TestSuiteGroup implements IEntryPoint
{
	/**
	 * Setup suites to be executed before any test suite.
	 */
	private Setup setup;

	/**
	 * Cleanup steps to be executed before all test suites.
	 */
	private Cleanup cleanup;

	/**
	 * Test suites to be executed. By default ordered by name.
	 */
	private Map<String, TestSuite> testSuitesMap = new TreeMap<>();

	/**
	 * Gets the setup suites to be executed before any test suite.
	 *
	 * @return the setup suites to be executed before any test suite
	 */
	public Setup getSetup()
	{
		return setup;
	}
	
	/**
	 * Sets the setup suites to be executed before any test suite.
	 *
	 * @param setup the new setup suites to be executed before any test suite
	 */
	public void setSetup(Setup setup)
	{
		this.setup = setup;
	}

	/**
	 * Gets the cleanup steps to be executed before all test suites.
	 *
	 * @return the cleanup steps to be executed before all test suites
	 */
	public Cleanup getCleanup()
	{
		return cleanup;
	}

	/**
	 * Sets the cleanup steps to be executed before all test suites.
	 *
	 * @param cleanup the new cleanup steps to be executed before all test suites
	 */
	public void setCleanup(Cleanup cleanup)
	{
		this.cleanup = cleanup;
	}

	/**
	 * Returns test suites of this group.
	 * @return test suites
	 */
	public Collection<TestSuite> getTestSuites()
	{
		return Collections.unmodifiableCollection( testSuitesMap.values() );
	}
	
	/**
	 * Adds the specified test suite.
	 * @param testSuite test suite to add.
	 */
	public void addTestSuite(TestSuite testSuite, boolean forReload)
	{
		TestSuite existingTestSuite = this.testSuitesMap.get(testSuite.getName());
		
		if(existingTestSuite == null)
		{
			this.testSuitesMap.put(testSuite.getName(), testSuite);
		}
		else
		{
			existingTestSuite.merge(testSuite, forReload);
		}
	}
	
	/**
	 * Checks if the specified name is valid test suite name.
	 * @param name name to check
	 * @return true if valid
	 */
	public boolean isValidTestSuiteName(String name)
	{
		return this.testSuitesMap.containsKey(name);
	}
	
	/**
	 * Returns test suite with specified name.
	 * @param name Name of test suite to fetch
	 * @return Matching test suite.
	 */
	public TestSuite getTestSuite(String name)
	{
		return testSuitesMap.get(name);
	}
	
	@Override
	public String toText()
	{
		return "<Test-Suite-Group>";
	}
}
