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
import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.event.AutomationEventManager;
import com.yukthitech.autox.exec.report.FinalReport;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.test.beans.ExecutionResult;

public class TAutomation extends BaseTestCases
{
	@Test
	public void testSuccessCases() throws Exception
	{
		System.setProperty(IAutomationConstants.AUTOX_SYS_PROP_LISTENERS, TestAutomationListener.class.getName());
		
		try
		{
			AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
					"-rf", "./output/success", 
					"-prop", "./src/test/resources/app.properties", 
					//"-ts", "assert-test-suites"
					//"-tc", "button_Click"
					//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
				});
			
			ExecutionResult exeResult = objectMapper.readValue(new File("./output/success/test-results.json"), ExecutionResult.class);
			Assert.assertEquals(exeResult.getTestCaseErroredCount(), 0, "Found one more test cases errored.");
			Assert.assertEquals(exeResult.getTestCaseFailureCount(), 0, "Found one more test cases failed.");
			Assert.assertEquals(exeResult.getTestCaseSkippedCount(), 0, "Found one more test cases skipped.");
			
			TestAutomationListener listener = (TestAutomationListener) AutomationEventManager.getInstance().getListeners().get(0);
			Assert.assertNotNull(listener.getStartTime());
			Assert.assertNotNull(listener.getEndTime());
			Assert.assertTrue(CollectionUtils.isNotEmpty(listener.getMessages()));
			
			listener.print();
		}finally
		{
			System.setProperty(IAutomationConstants.AUTOX_SYS_PROP_LISTENERS, "");
		}
	}

	@Test
	public void testGroupWiseExecution() throws Exception
	{
		System.setProperty("autox.executable.groups", "group1,group2");
		
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-rf", "./output/groups", 
				"-prop", "./src/test/resources/app.properties",
				//"-ts", "ui-test-suites"
				//"-tc", "fillForm"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/groups/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 2, "Found extra test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 4, "Found extra test cases.");
		
		Assert.assertEquals(exeResult.getTestSuiteSuccessCount(), 2, "Found invalid number of success test suites.");
	}

	/*
	@Test
	public void testMailPlugin() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/new-test-suites/mail-test-suites",
				"-rf", "./output/mail", 
				"-prop", "./src/test/resources/app.properties", 
				//"--report-opening-disabled", "true",
				//"-ts", "rest-test-suites"
				//"-tc", "testGroupRecursion"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/mail/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 1, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 1, "Found one more test cases.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 1, "Found one more test cases.");
	}
	*/

	@Test
	public void testGlobalSetupError() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/new-test-suites/global-setup-err",
				"-rf", "./output/globalSetupErr", 
				"-prop", "./src/test/resources/app.properties", 
				"--report-opening-disabled", "true",
				//"-ts", "rest-test-suites"
				//"-tc", "testGroupRecursion"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/globalSetupErr/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 0, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 0, "Found one more test cases.");
		
		TestUtils.validateLogFile(new File("./output/globalSetupErr/logs/_global-setup.js"), 
				"_globalSetup",
				Arrays.asList("&lt;Test-Suite-Group&gt;(common.xml:4)"), 
				null, null);
	}

	@Test
	public void testGlobalCleanupError() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/new-test-suites/global-cleanup-err",
				"-rf", "./output/globalCleanupErr", 
				"-prop", "./src/test/resources/app.properties", 
				"--report-opening-disabled", "true",
				//"-ts", "rest-test-suites"
				//"-tc", "testGroupRecursion"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/globalCleanupErr/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 1, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 1, "Found one more test cases.");
		
		TestUtils.validateLogFile(new File("./output/globalCleanupErr/logs/_global-setup.js"), 
				"_globalSetup",
				null,
				Arrays.asList("Message from global setup"), null);

		TestUtils.validateTestCase("test", exeResult, TestStatus.SUCCESSFUL, 
				null, 
				Arrays.asList("Message from testcase"), null, "globalCleanupErr");

		TestUtils.validateLogFile(new File("./output/globalCleanupErr/logs/_global-cleanup.js"), 
				"_globalCleanup",
				Arrays.asList("&lt;Test-Suite-Group&gt;(common.xml:8)"),
				null, null);
	}
}
