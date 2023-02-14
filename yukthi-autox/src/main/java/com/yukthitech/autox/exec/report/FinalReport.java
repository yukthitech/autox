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
package com.yukthitech.autox.exec.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.test.TestStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FinalReport
{
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TestSuiteResult
	{
		private ExecutionStatusReport report;
		
		private int totalCount;
		
		private int successCount;
		
		private int failureCount;
		
		private int errorCount;
		
		private int skipCount;
		
		private List<ExecutionStatusReport> testCaseResults = new ArrayList<>();
		
		public TestSuiteResult()
		{}
		
		public TestSuiteResult(ExecutionStatusReport report)
		{
			this.report = report;
			addTestCases(report);
		}
		
		private void addTestCases(ExecutionStatusReport parent)
		{
			if(CollectionUtils.isEmpty(parent.getChildReports()))
			{
				return;
			}
			
			TestStatus status = null;
			
			for(ExecutionStatusReport report : parent.getChildReports())
			{
				if(CollectionUtils.isNotEmpty(report.getChildReports()) || report.getType() == ExecutionStatusReportType.DATA_PROVIDER)
				{
					addTestCases(report);
					
					//add entry of parent test case to expose data-provider logs
					ExecutionStatusReport statusReport = new ExecutionStatusReport(report.getName(), report.getSetupExecutionDetails(), 
							report.getCleanupExecutionDetails(), ExecutionStatusReportType.DATA_PROVIDER);
					
					this.testCaseResults.add(statusReport);
					status = statusReport.getMainExecutionDetails().getStatus();
				}
				else
				{
					testCaseResults.add(report);
					status = report.getMainExecutionDetails().getStatus();
				}
				
				switch(status)
				{
					case ERRORED:
						errorCount++;
						break;
					case FAILED:
						failureCount++;
						break;
					case SKIPPED:
						skipCount++;
						break;
					default:
						successCount++;
				}

				totalCount++;
			}
		}

		public ExecutionStatusReport getReport()
		{
			return report;
		}

		public void setReport(ExecutionStatusReport report)
		{
			this.report = report;
		}

		public int getTotalCount()
		{
			return totalCount;
		}

		public void setTotalCount(int totalCount)
		{
			this.totalCount = totalCount;
		}

		public int getSuccessCount()
		{
			return successCount;
		}

		public void setSuccessCount(int successCount)
		{
			this.successCount = successCount;
		}

		public int getFailureCount()
		{
			return failureCount;
		}

		public void setFailureCount(int failureCount)
		{
			this.failureCount = failureCount;
		}

		public int getErrorCount()
		{
			return errorCount;
		}

		public void setErrorCount(int errorCount)
		{
			this.errorCount = errorCount;
		}

		public int getSkipCount()
		{
			return skipCount;
		}

		public void setSkipCount(int skipCount)
		{
			this.skipCount = skipCount;
		}

		public List<ExecutionStatusReport> getTestCaseResults()
		{
			return testCaseResults;
		}
		
		public ExecutionStatusReport getTestCaseResult(String name)
		{
			return testCaseResults
					.stream()
					.filter(res -> name.equals(res.getName()))
					.findFirst()
					.orElse(null);
		}

		public void setTestCaseResults(List<ExecutionStatusReport> testCaseResults)
		{
			this.testCaseResults = testCaseResults;
		}
	}
	
	public static class FunctionResult
	{
		private String name;
		
		private ExecutionDetails executionDetails;
		
		private Map<String, String> monitorLogs;

		public FunctionResult(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public ExecutionDetails getExecutionDetails()
		{
			return executionDetails;
		}

		public void setExecutionDetails(ExecutionDetails executionDetails)
		{
			this.executionDetails = executionDetails;
		}

		public void addMonitorLog(String name, String fileName)
		{
			if(this.monitorLogs == null)
			{
				this.monitorLogs = new LinkedHashMap<>();
			}
			
			this.monitorLogs.put(name, fileName);
		}

		public Map<String, String> getMonitorLogs()
		{
			return monitorLogs;
		}

		public void setMonitorLogs(Map<String, String> monitorLogs)
		{
			this.monitorLogs = monitorLogs;
		}
	}
	
	private String reportName;
	
	private Date executionDate = new Date();
	
	private int testSuiteCount;
	
	private int testSuiteSuccessCount;
	
	private int testSuiteErrorCount;
	
	private int testSuiteFailureCount;
	
	private int testSuiteSkippedCount;
	
	private int testCaseCount;
	
	private int testCaseSuccessCount;
	
	private int testCaseFailureCount;
	
	private int testCaseErroredCount;
	
	private int testCaseSkippedCount;
	
	private List<TestSuiteResult> testSuiteResults = new ArrayList<>();
	
	/**
	 * Setup execution details.
	 */
	private ExecutionDetails setupExecutionDetails;

	/**
	 * Main execution details.
	 */
	private ExecutionDetails mainExecutionDetails;

	/**
	 * Cleanup execution details.
	 */
	private ExecutionDetails cleanupExecutionDetails;
	
	/**
	 * Result of function execution.
	 */
	private FunctionResult functionResult;
	
	public FinalReport()
	{}
	
	public FinalReport(String reportName, FunctionResult result)
	{
		this.reportName = reportName;
		this.functionResult = result;
	}
	
	public FinalReport(String reportName, ExecutionStatusReport testSuiteGroupReport)
	{
		this.reportName = reportName;
		this.setupExecutionDetails = testSuiteGroupReport.getSetupExecutionDetails();
		this.mainExecutionDetails = testSuiteGroupReport.getMainExecutionDetails();
		this.cleanupExecutionDetails = testSuiteGroupReport.getCleanupExecutionDetails();
		
		if(testSuiteGroupReport.getChildReports() == null)
		{
			return;
		}
		
		for(ExecutionStatusReport testSuiteReport : testSuiteGroupReport.getChildReports())
		{
			TestSuiteResult testSuiteResult = new TestSuiteResult(testSuiteReport);
			
			switch (testSuiteReport.getMainExecutionDetails().getStatus())
			{
				case ERRORED:
					testSuiteErrorCount++;
					break;
				case FAILED:
					testSuiteFailureCount++;
					break;
				case SKIPPED:
					testSuiteSkippedCount++;
					break;
				default:
					testSuiteSuccessCount++;
			}
			
			testSuiteCount++;
			
			testCaseCount += testSuiteResult.totalCount;
			testCaseSuccessCount += testSuiteResult.successCount;
			testCaseFailureCount += testSuiteResult.failureCount;
			testCaseErroredCount += testSuiteResult.errorCount;
			testCaseSkippedCount += testSuiteResult.skipCount;
			
			testSuiteResults.add(testSuiteResult);
		}
	}

	public String getReportName()
	{
		return reportName;
	}

	public void setReportName(String reportName)
	{
		this.reportName = reportName;
	}
	
	public String getExecutionDateStr()
	{
		return ApplicationConfiguration.getInstance().getTimeFormatObject().format(executionDate);
	}

	public Date getExecutionDate()
	{
		return executionDate;
	}

	public void setExecutionDate(Date executionDate)
	{
		this.executionDate = executionDate;
	}

	public int getTestSuiteCount()
	{
		return testSuiteCount;
	}

	public void setTestSuiteCount(int testSuiteCount)
	{
		this.testSuiteCount = testSuiteCount;
	}

	public int getTestSuiteSuccessCount()
	{
		return testSuiteSuccessCount;
	}

	public void setTestSuiteSuccessCount(int testSuiteSuccessCount)
	{
		this.testSuiteSuccessCount = testSuiteSuccessCount;
	}

	public int getTestSuiteErrorCount()
	{
		return testSuiteErrorCount;
	}

	public void setTestSuiteErrorCount(int testSuiteErrorCount)
	{
		this.testSuiteErrorCount = testSuiteErrorCount;
	}

	public int getTestSuiteFailureCount()
	{
		return testSuiteFailureCount;
	}

	public void setTestSuiteFailureCount(int testSuiteFailureCount)
	{
		this.testSuiteFailureCount = testSuiteFailureCount;
	}

	public int getTestSuiteSkippedCount()
	{
		return testSuiteSkippedCount;
	}

	public void setTestSuiteSkippedCount(int testSuiteSkippedCount)
	{
		this.testSuiteSkippedCount = testSuiteSkippedCount;
	}

	public int getTestCaseCount()
	{
		return testCaseCount;
	}

	public void setTestCaseCount(int testCaseCount)
	{
		this.testCaseCount = testCaseCount;
	}

	public int getTestCaseSuccessCount()
	{
		return testCaseSuccessCount;
	}

	public void setTestCaseSuccessCount(int testCaseSuccessCount)
	{
		this.testCaseSuccessCount = testCaseSuccessCount;
	}

	public int getTestCaseFailureCount()
	{
		return testCaseFailureCount;
	}

	public void setTestCaseFailureCount(int testCaseFailureCount)
	{
		this.testCaseFailureCount = testCaseFailureCount;
	}

	public int getTestCaseErroredCount()
	{
		return testCaseErroredCount;
	}

	public void setTestCaseErroredCount(int testCaseErroredCount)
	{
		this.testCaseErroredCount = testCaseErroredCount;
	}

	public int getTestCaseSkippedCount()
	{
		return testCaseSkippedCount;
	}

	public void setTestCaseSkippedCount(int testCaseSkippedCount)
	{
		this.testCaseSkippedCount = testCaseSkippedCount;
	}

	public List<TestSuiteResult> getTestSuiteResults()
	{
		return testSuiteResults;
	}
	
	public TestSuiteResult getTestSuiteResult(String name)
	{
		return this.testSuiteResults
				.stream()
				.filter(res -> res.getReport().getName().equals(name))
				.findFirst()
				.orElse(null);
	}

	public void setTestSuiteResults(List<TestSuiteResult> testSuiteResults)
	{
		this.testSuiteResults = testSuiteResults;
	}

	public ExecutionDetails getSetupExecutionDetails()
	{
		return setupExecutionDetails;
	}

	public void setSetupExecutionDetails(ExecutionDetails setupExecutionDetails)
	{
		this.setupExecutionDetails = setupExecutionDetails;
	}

	public ExecutionDetails getMainExecutionDetails()
	{
		return mainExecutionDetails;
	}

	public void setMainExecutionDetails(ExecutionDetails mainExecutionDetails)
	{
		this.mainExecutionDetails = mainExecutionDetails;
	}

	public ExecutionDetails getCleanupExecutionDetails()
	{
		return cleanupExecutionDetails;
	}

	public void setCleanupExecutionDetails(ExecutionDetails cleanupExecutionDetails)
	{
		this.cleanupExecutionDetails = cleanupExecutionDetails;
	}

	public FunctionResult getFunctionResult()
	{
		return functionResult;
	}

	public void setFunctionResult(FunctionResult functionResult)
	{
		this.functionResult = functionResult;
	}
}
