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
package com.yukthitech.test.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Beand to parse final results json with limited read.
 * @author akiran
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionResult
{
	private int testCaseErroredCount;
	
	private int testCaseFailureCount;
	
	private int testCaseSkippedCount;

	public int getTestCaseErroredCount()
	{
		return testCaseErroredCount;
	}

	public void setTestCaseErroredCount(int testCaseErroredCount)
	{
		this.testCaseErroredCount = testCaseErroredCount;
	}

	public int getTestCaseFailureCount()
	{
		return testCaseFailureCount;
	}

	public void setTestCaseFailureCount(int testCaseFailureCount)
	{
		this.testCaseFailureCount = testCaseFailureCount;
	}

	public int getTestCaseSkippedCount()
	{
		return testCaseSkippedCount;
	}

	public void setTestCaseSkippedCount(int testCaseSkippedCount)
	{
		this.testCaseSkippedCount = testCaseSkippedCount;
	}
}
