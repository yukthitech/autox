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
package com.yukthitech.autox.ide.views.report;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.exec.report.ExecutionLogData;

/**
 * Represents a test suite row.
 * @author akiran
 */
public class TestSuiteRow extends MinimizableRow<TestCaseRow> implements IReportRow
{
	private String name;
	
	private Map<String, TestCaseRow> nameToTestCase = new HashMap<>();

	public TestSuiteRow(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	@Override
	public void addChild(TestCaseRow child)
	{
		super.addChild(child);
		nameToTestCase.put(child.getName(), child);
	}
	
	public void addLog(String testCase, ExecutionLogData.Message message)
	{
		TestCaseRow testCaseRow = nameToTestCase.get(testCase);
		
		if(testCaseRow == null)
		{
			testCaseRow = new TestCaseRow(testCase);
			addChild(testCaseRow);
		}
		
		testCaseRow.addLogEntry(message);
	}
	
	@Override
	public Object getValueAt(int col)
	{
		return (col == 0) ? name : "";
	}
}
