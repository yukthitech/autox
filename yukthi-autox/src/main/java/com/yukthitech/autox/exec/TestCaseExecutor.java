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
package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.dataprovider.IDataProvider;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.TestStatus;

public class TestCaseExecutor extends Executor
{
	private TestCase testCase;
	
	private TestCaseData testCaseData;
	
	private List<TestCaseExecutor> dependencies;
	
	private Setup dataSetup;
	
	private Cleanup dataCleanup;
	
	/**
	 * Flag indicating if {@link #init()} method is executed successfully.
	 */
	private boolean initialized = false;
	
	public TestCaseExecutor(TestCase testCase)
	{
		super(testCase, "Test-Case");
		
		this.testCase = testCase;
		super.includeChildStauts = true;
		
		if(testCase.getDataProvider() != null)
		{
			this.dataSetup = testCase.getDataSetup();
			this.dataCleanup = testCase.getDataCleanup();
			
			super.parallelExecutionEnabled = testCase.isParallelExecutionEnabled();
		}
		else
		{
			super.childSteps = testCase.getSteps();
			super.expectedException = testCase.getExpectedException();
			
			//setup and cleanup should be assigned for normal test cases
			// without data proovider
			super.setup = testCase.getSetup();
			super.cleanup = testCase.getCleanup();
		}
	}
	
	private TestCaseExecutor(TestCase testCase, TestCaseData testCaseData)
	{
		super(testCase, null);
		
		this.testCase = testCase;
		this.testCaseData = testCaseData;
		super.childSteps = testCase.getSteps();
		super.expectedException = testCase.getExpectedException();
		
		//setup and cleanup should be executed with every data based test cases
		super.setup = testCase.getSetup();
		super.cleanup = testCase.getCleanup();
		
		super.uniqueId = testCase.getName() + "[" + testCaseData.getName() + "]";
	}
	
	public TestCase getTestCase()
	{
		return testCase;
	}
	
	public boolean isDataProviderType()
	{
		return (testCaseData == null && testCase.getDataProvider() != null);
	}
	
	public TestCaseData getTestCaseData()
	{
		return testCaseData;
	}
	
	public void addDependency(TestCaseExecutor executor)
	{
		if(executor == null)
		{
			throw new InvalidArgumentException("Executor cannot be null");
		}
		
		if(dependencies == null)
		{
			this.dependencies = new ArrayList<>();
		}
		
		this.dependencies.add(executor);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Executor> getDependencies()
	{
		return (List) dependencies;
	}
	
	@Override
	public boolean isReadyToExecute()
	{
		if(CollectionUtils.isEmpty(dependencies))
		{
			return true;
		}
		
		for(TestCaseExecutor dep : this.dependencies)
		{
			if(dep.status == null || dep.status == TestStatus.IN_PROGRESS)
			{
				return false;
			}
			
			if(dep.status == TestStatus.SKIPPED || dep.status.isErrored())
			{
				ReportDataManager.getInstance().executionSkipped(ExecutionType.MAIN, this, 
						String.format("Skipped as required dependency test-case '%s' is found with status: %s", 
								dep.testCase.getName(), dep.status));
				return false;
			}
		}
		
		return true;
	}
	
	private List<TestCaseData> executeDataProvider(IDataProvider dataProvider)
	{
		super.activeExecutionLogger = ReportDataManager.getInstance().getSetupExecutionLogger(this);
		super.activeExecutionLogger.setMode("Data-Provider");
		
		List<TestCaseData> data = dataProvider.getStepData();
		
		if(CollectionUtils.isEmpty(data))
		{
			super.activeExecutionLogger.error("Data provider resulted in null or empty data list");
		}
		
		return data;
	}
	
	@Override
	protected boolean init()
	{
		//dont consider data provider, for already data-based executors
		if(testCaseData != null)
		{
			return true;
		}
		
		IDataProvider dataProvider = testCase.getDataProvider();
		
		if(dataProvider == null)
		{
			return true;
		}
		
		if(!ExecutorUtils.executeSetup(dataSetup, "Data-Setup", this))
		{
			return false;
		}
		
		List<TestCaseData> dataLst = executeDataProvider(dataProvider);
		
		if(CollectionUtils.isEmpty(dataLst))
		{
			super.setStatus(TestStatus.ERRORED, String.format("No data provided by data provider: %s", dataProvider.getName()));
			return false;
		}
		
		boolean shareContext = (testCase.isSharedContext() && !testCase.isParallelExecutionEnabled());
		
		for(TestCaseData data : dataLst)
		{
			TestCaseExecutor newExecutor = new TestCaseExecutor(testCase, data);
			newExecutor.parentContextShared = shareContext;
			
			super.addChildExector(newExecutor);
		}
		
		initialized = true;
		return true;
	}
	
	@Override
	protected void preCleanup()
	{
		//if data init is not done or failed, no need to do cleaup
		if(!initialized)
		{
			return;
		}
		
		ExecutorUtils.executeCleanup(dataCleanup, "Data-Cleanup", this);
	}
	
	@Override
	protected void preexecute()
	{
		if(testCaseData != null)
		{
			AutomationContext.getInstance().setAttribute(testCase.getDataProvider().getName(), testCaseData.getValue());
			AutomationContext.getInstance().setAttribute(testCase.getDataProvider().getName() + "$name", testCaseData.getName());
		}

		AutomationContext.getInstance().startLogMonitoring();
	}
	
	@Override
	protected void postExecute()
	{
		boolean isErrored = (super.status != null && super.status.isErrored());
		Map<String, ReportLogFile> monitorLogs = AutomationContext.getInstance().stopLogMonitoring(isErrored);
		ReportDataManager.getInstance().setMonitoringLogs(this, monitorLogs);
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(testCase.getName());
		
		if(testCaseData != null)
		{
			builder.append(", ").append("Data: ").append(testCaseData.getName());
		}

		builder.append("]");
		return builder.toString();
	}
}
