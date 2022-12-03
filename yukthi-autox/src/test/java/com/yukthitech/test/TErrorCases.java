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

public class TErrorCases extends BaseTestCases
{
	/**
	 * Ensures failures are happening at right places and with right stack trace.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testNegativeCases() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/new-test-suites/neg-test-suites",
				"-rf", "./output/negCases", 
				"-prop", "./src/test/resources/app.properties", 
				//"--report-opening-disabled", "true",
				//"-ts", "data-provider-err"
				//"-tc", "screenShotInCleanupErr"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/negCases/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 8, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 17, "Found one more test cases.");
		Assert.assertEquals(exeResult.getTestCaseSuccessCount(), 7, "Found one more test cases errored.");
		Assert.assertEquals(exeResult.getTestCaseErroredCount(), 5, "Found one more test cases errored.");
		Assert.assertEquals(exeResult.getTestCaseFailureCount(), 4, "Found one more test cases failed.");
		Assert.assertEquals(exeResult.getTestCaseSkippedCount(), 1, "Found one more test cases skipped.");
		
		validateStackTraces(exeResult);

		validateTsSeupCleanupErr(exeResult);

		validateUiErrors(exeResult);
		
		validateSkipFlows(exeResult);

		validateSetupCleanupErrors(exeResult);
		
		validateDataproviderErrors(exeResult);
	}

	private void validateStackTraces(FinalReport exeResult) throws Exception
	{
		TestUtils.validateTestCase("basicAssertFailure", exeResult, TestStatus.FAILED, 
				Arrays.asList("[TC: basicAssertFailure](neg-lang-test-suite.xml:12)"), 
				null, null, "negCases");

		TestUtils.validateTestCase("errorInLoop", exeResult, TestStatus.FAILED, 
				Arrays.asList("[TC: errorInLoop](neg-lang-test-suite.xml:25)"), 
				null, null, "negCases");

		TestUtils.validateTestCase("deepFail", exeResult, TestStatus.FAILED, 
				Arrays.asList(
					Arrays.asList(
						"[TS: negative-lang-suites].level2(neg-lang-test-suite.xml:37)",
						"[TS: negative-lang-suites].level1(neg-lang-test-suite.xml:42)",
						"[TC: deepFail](neg-lang-test-suite.xml:50)"
					)
				), 
				null, null, "negCases");
	}

	private void validateTsSeupCleanupErr(FinalReport exeResult) throws Exception
	{
		TestUtils.validateTestCase("tcSetupFail", exeResult, TestStatus.ERRORED, 
				Arrays.asList(
					Arrays.asList(
						"[Setup]",
						"[TC: tcSetupFail](neg-lang-test-suite.xml:59)"
					)
				), 
				null, null, "negCases");

		TestUtils.validateTestCase("tcCleanupFail", exeResult, TestStatus.ERRORED, 
				Arrays.asList(
					Arrays.asList(
						"[Cleanup]",
						"[TC: tcCleanupFail](neg-lang-test-suite.xml:74)"
					)
				), 
				Arrays.asList("This is from testcase"), 
				null, "negCases");
	}

	private void validateSetupCleanupErrors(FinalReport exeResult) throws Exception
	{
		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_test-suite-setup-err-setup.js"), 
				"_testSuiteSetup",
				Arrays.asList("[TS: test-suite-setup-err](test-suite-setup-err.xml:8)"), 
				null, null);

		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_test-suite-cleanup-err-setup.js"), 
				"_testSuiteSetup",
				null, 
				Arrays.asList("This is from setup"), null);

		TestUtils.validateTestCase("tsCleanupErr_test", exeResult, TestStatus.SUCCESSFUL, 
				null, 
				Arrays.asList("From testcase"), null, "negCases");
		
		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_test-suite-cleanup-err-cleanup.js"), 
				"_testSuiteCleanup",
				Arrays.asList("[TS: test-suite-cleanup-err](test-suite-cleanup-err.xml:20)"), 
				null, null);
	}

	private void validateSkipFlows(FinalReport exeResult) throws Exception
	{
		TestUtils.validateTestCase("skip_success", exeResult, TestStatus.SUCCESSFUL, 
				null, 
				Arrays.asList("Mssg from skip_success"), null, "negCases");

		TestUtils.validateTestCase("skip_fail", exeResult, TestStatus.FAILED, 
				Arrays.asList("[TC: skip_fail](skip-test-suite.xml:20)"), 
				null, null, "negCases");

		TestUtils.validateTestCase("skip_skip", exeResult, TestStatus.SKIPPED, 
				null, 
				null, null, "negCases");
	}

	private void validateUiErrors(FinalReport exeResult) throws Exception
	{
		TestUtils.validateTestCase("screenShotOnError", exeResult, TestStatus.ERRORED, 
				Arrays.asList(
					"Failed to find element with locator: [Locator: id: invalidId]",
					"Screen shot during error"
				), 
				null, Arrays.asList("error-screenshot"), "negCases");

		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_neg-ui-test-suites-setupErr-setup.js"), 
				"test-suite-setup-err",
				Arrays.asList("[TS: neg-ui-test-suites-setupErr](neg-ui-test-suite.xml:22)"), 
				null,
				Arrays.asList("error-screenshot"));

		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_neg-ui-test-suites-cleanupErr-cleanup.js"), 
				"test-suite-cleanup-err",
				Arrays.asList("[TS: neg-ui-test-suites-cleanupErr](neg-ui-test-suite.xml:48)"), 
				null,
				Arrays.asList("error-screenshot"));
	}

	private void validateDataproviderErrors(FinalReport exeResult) throws Exception
	{
		TestUtils.validateLogFile(new File("./output/negCases/logs/tc_data-provider-err_dataProviderSetupErr.js"), 
				"dataProviderSetupErr",
				Arrays.asList(
					Arrays.asList(
						"[Data-Setup]",
						"fail: Failing the data-setup"
					)
				),
				null,
				null);

		TestUtils.validateTestCase("dataProviderCleanupErr [case1]", exeResult, TestStatus.SUCCESSFUL, null, null, null, "negCases");
		TestUtils.validateTestCase("dataProviderCleanupErr [case2]", exeResult, TestStatus.SUCCESSFUL, null, null, null, "negCases");
		TestUtils.validateTestCase("dataProviderCleanupErr [case3]", exeResult, TestStatus.SUCCESSFUL, null, null, null, "negCases");

		TestUtils.validateLogFile(new File("./output/negCases/logs/tc_data-provider-err_dataProviderCleanupErr.js"), 
				"dataProviderCleanupErr",
				Arrays.asList(
					Arrays.asList(
						"[Data-Cleanup]",
						"fail: Failing the data-setup."
					)
				),
				null,
				null);

		TestUtils.validateLogFile(new File("./output/negCases/logs/tc_data-provider-err_dataProviderEmptyRes.js"), 
				"dataProviderEmptyRes",
				Arrays.asList(
					Arrays.asList(
						"[Data-Provider]",
						"Data provider resulted in null or empty data list"
					)
				),
				null,
				null);
	}
}
