package com.yukthitech.autox.test;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entry that can be added to execution suite.
 */
public class ExecutionSuiteEntry
{
	/**
	 * Name of test suite to be executed.
	 */
	private String testSuite;
	
	/**
	 * List of test cases to be executed under specified test suite.
	 */
	private Set<String> testCases;
	
	public ExecutionSuiteEntry()
	{}
	
	public ExecutionSuiteEntry(String testSuite)
	{
		this.testSuite = testSuite;
	}

	/**
	 * Gets the name of test suite to be executed.
	 *
	 * @return the name of test suite to be executed
	 */
	public String getTestSuite()
	{
		return testSuite;
	}

	/**
	 * Sets the name of test suite to be executed.
	 *
	 * @param testSuite the new name of test suite to be executed
	 */
	public void setTestSuite(String testSuite)
	{
		this.testSuite = testSuite;
	}

	/**
	 * Gets the list of test cases to be executed under specified test suite.
	 *
	 * @return the list of test cases to be executed under specified test suite
	 */
	public Set<String> getTestCases()
	{
		return testCases;
	}

	/**
	 * Sets the list of test cases to be executed under specified test suite.
	 *
	 * @param testCases the new list of test cases to be executed under
	 *        specified test suite
	 */
	public void setTestCases(Set<String> testCases)
	{
		this.testCases = testCases;
	}
	
	/**
	 * Adds the test case.
	 *
	 * @param testCase the test case
	 */
	public void addTestCase(String testCase)
	{
		if(this.testCases == null)
		{
			this.testCases = new LinkedHashSet<>();
		}
		
		this.testCases.add(testCase);
	}
}
