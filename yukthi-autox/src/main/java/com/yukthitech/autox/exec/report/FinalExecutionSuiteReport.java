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

import org.apache.commons.beanutils.PropertyUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.test.ExecutionSuite;
import com.yukthitech.utils.exceptions.InvalidStateException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FinalExecutionSuiteReport extends FinalReport
{
	private TestSuiteResult executionResult;
	
	public FinalExecutionSuiteReport()
	{}
	
	public FinalExecutionSuiteReport(ExecutionSuite executionSuite, FinalReport report)
	{
		try
		{
			PropertyUtils.copyProperties(this, report);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while copying properties", ex);
		}
		
		super.setTestSuiteResults(null);

		ExecutionStatusReport executionStatusReport = new ExecutionStatusReport(executionSuite.getName(), null, null, ExecutionStatusReportType.STANDARD);
		
		for(TestSuiteResult suiteResult : report.getTestSuiteResults())
		{
			if(suiteResult.getTestCaseResults() == null)
			{
				continue;
			}
			
			if(suiteResult.getReport() == null || suiteResult.getReport().getChildReports() == null)
			{
				continue;
			}
			
			suiteResult
				.getReport()
				.getChildReports()
				.stream()
				.map(rep -> 
				{
					ExecutionStatusReport clone = rep.clone();
					clone.setName(suiteResult.getReport().getName() + "#" + clone.getName());
					return clone;
				})
				.forEach(executionStatusReport::addChidReport);
		}
		
		this.executionResult = new TestSuiteResult(executionStatusReport);
	}

	public TestSuiteResult getExecutionResult()
	{
		return executionResult;
	}

	public void setExecutionResult(TestSuiteResult executionResult)
	{
		this.executionResult = executionResult;
	}
}
