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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.exec.report.FinalReport;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.test.beans.ExecutionResult;

public class TAutomation extends BaseTestCases
{
	@Test
	public void testSuccessCases() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-rf", "./output/success", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "ui-test-suites"
				//"-tc", "fillForm"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		ExecutionResult exeResult = objectMapper.readValue(new File("./output/success/test-results.json"), ExecutionResult.class);
		Assert.assertEquals(exeResult.getTestCaseErroredCount(), 0, "Found one more test cases errored.");
		Assert.assertEquals(exeResult.getTestCaseFailureCount(), 0, "Found one more test cases failed.");
		Assert.assertEquals(exeResult.getTestCaseSkippedCount(), 0, "Found one more test cases skipped.");
	}

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
