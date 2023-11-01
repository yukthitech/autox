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
package com.yukthitech.test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.FinalReport;

/**
 * Ensure the flow of execution and context attribute scope is proper.
 * @author akranthikiran
 */
public class TAutomationFlows extends BaseTestCases
{
	private List<String> loadFlows(String resource) throws Exception
	{
		String content = IOUtils.resourceToString(resource, Charset.defaultCharset());
		String lines[] = content.split("\\n");
		List<String> list = new ArrayList<String>();
		
		for(String line : lines)
		{
			line = line.trim();
			
			if(line.length() <= 0)
			{
				continue;
			}
			
			list.add(line);
		}
		
		return list;
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testFlowOrder() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-tsf", "./src/test/resources/new-test-suites/test-suite-flows",
				"-rf", "./output/flows", 
				"-prop", "./src/test/resources/app.properties",
				"--report-opening-disabled", "true",
				//"-ts", "jobj-test-suites"
				//"-tc", "dataProviderOnFetchIndependentCtx"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		List<String> flowPoints = (List<String>) AutomationContext.getInstance().getGlobalAttribute("flowCapture");
		System.out.println(flowPoints);
		
		Assert.assertEquals(flowPoints, loadFlows("/data/test-flow-order.txt"));
	}
	
	@SuppressWarnings("unchecked")
	private void testFlowByIds(String tcIds, List<String> expectedFlowPoints, String rfFolder) throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-tsf", "./src/test/resources/new-test-suites/dup-name-flows",
				"-rf", rfFolder, 
				"-prop", "./src/test/resources/app.properties",
				"--report-opening-disabled", "true",
				"-tc", tcIds
			});
		
		List<String> flowPoints = (List<String>) AutomationContext.getInstance().getGlobalAttribute("flowCapture");
		System.out.println(flowPoints);
		
		Assert.assertEquals(flowPoints, expectedFlowPoints);
	}

	@Test
	public void testFlowByIds_onlyBase() throws Exception
	{
		testFlowByIds("baseTestCase", 
				Arrays.asList("a-test-suites#baseTestCase", "b-test-suites#baseTestCase"), 
				"./output/dup-flows-onlyBase");
	}

	@Test
	public void testFlowByIds_onlyDep() throws Exception
	{
		testFlowByIds("depTestCase1", 
				Arrays.asList("a-test-suites#baseTestCase", "a-test-suites#depTestCase1", "b-test-suites#baseTestCase", "b-test-suites#depTestCase1"), 
				"./output/dup-flows-onlyDep");
	}

	@Test
	public void testFlowByIds_deepDepByUid() throws Exception
	{
		testFlowByIds("b-test-suites#depTestCase3", 
				Arrays.asList("b-test-suites#baseTestCase", "b-test-suites#depTestCase1", "b-test-suites#depTestCase2", "b-test-suites#depTestCase3"), 
				"./output/dup-flows-byUid");
	}

	@Test
	public void testMultiThread_flow() throws Exception
	{
		System.setProperty("autox.testSuites.parallelExecutionEnabled", "true");
		System.setProperty("autox.parallelExecution.poolSize", "10");
		
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-tsf", "./src/test/resources/new-test-suites/multi-thread-flows",
				"-rf", "./output/multi-thread-flows", 
				"-prop", "./src/test/resources/app.properties", 
				"--report-opening-disabled", "true",
				//"-ts", "jobj-test-suites"
				//"-tc", "dataProviderOnFetchIndependentCtx"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/multi-thread-flows/test-results.json"), FinalReport.class);
		Assert.assertEquals(exeResult.getTestSuiteCount(), 7, "Found wrong number of test suites");
		Assert.assertEquals(exeResult.getTestSuiteSuccessCount(), 3, "Found wrong number of SUCCESS test suites");
		Assert.assertEquals(exeResult.getTestSuiteErrorCount(), 4, "Found wrong number of ERROR test suites");
		
		Assert.assertEquals(exeResult.getTestCaseCount(), 35, "Found wrong number of test cases");
		Assert.assertEquals(exeResult.getTestCaseSuccessCount(), 27, "Found wrong number of SUCCESS test cases");
		Assert.assertEquals(exeResult.getTestCaseFailureCount(), 4, "Found wrong number of FAIL test cases");
		Assert.assertEquals(exeResult.getTestCaseErroredCount(), 4, "Found wrong number of ERROR test cases");
	}

	@Test
	public void testMultiThread_sessions() throws Exception
	{
		System.setProperty("autox.testSuites.parallelExecutionEnabled", "true");
		System.setProperty("autox.parallelExecution.poolSize", "10");
		
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-tsf", "./src/test/resources/new-test-suites/multi-thread-sessions",
				"-rf", "./output/multi-thread-sessions", 
				"-prop", "./src/test/resources/app.properties", 
				"--report-opening-disabled", "true",
				//"-ts", "ml-rest-sessions"
				//"-tc", "dataProviderOnFetchIndependentCtx"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/multi-thread-sessions/test-results.json"), FinalReport.class);
		Assert.assertEquals(exeResult.getTestSuiteCount(), 4, "Found wrong number of test suites");
		Assert.assertEquals(exeResult.getTestSuiteSuccessCount(), 4, "Found wrong number of SUCCESS test suites");
		
		Assert.assertEquals(exeResult.getTestCaseCount(), 29, "Found wrong number of test cases");
		Assert.assertEquals(exeResult.getTestCaseSuccessCount(), 29, "Found wrong number of SUCCESS test cases");
		Assert.assertEquals(exeResult.getTestCaseFailureCount(), 0, "Found wrong number of FAIL test cases");
		Assert.assertEquals(exeResult.getTestCaseErroredCount(), 0, "Found wrong number of ERROR test cases");
	}

	@Test
	public void testSessionEvents() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-tsf", "./src/test/resources/new-test-suites/session-events",
				"-rf", "./output/session-events", 
				"-prop", "./src/test/resources/app.properties", 
				"--report-opening-disabled", "true",
				//"-ts", "ml-rest-sessions"
				//"-tc", "dataProviderOnFetchIndependentCtx"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/session-events/test-results.json"), FinalReport.class);
		Assert.assertEquals(exeResult.getTestSuiteCount(), 1, "Found wrong number of test suites");
		Assert.assertEquals(exeResult.getTestSuiteSuccessCount(), 1, "Found wrong number of SUCCESS test suites");
		
		Assert.assertEquals(exeResult.getTestCaseCount(), 1, "Found wrong number of test cases");
		Assert.assertEquals(exeResult.getTestCaseSuccessCount(), 1, "Found wrong number of SUCCESS test cases");
	}
}
