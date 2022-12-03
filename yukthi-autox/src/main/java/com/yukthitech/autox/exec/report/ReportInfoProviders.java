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

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.exec.Executor;
import com.yukthitech.autox.exec.TestCaseExecutor;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

public class ReportInfoProviders
{
	private Map<Class<?>, ReportInfoProvider<Object>> providers = new HashMap<>();
	
	public ReportInfoProviders()
	{
		addProvider(TestSuiteGroup.class, new ReportInfoProvider<>(false, grp -> "", grp -> "global", grp -> "global", grp -> ""));
		addProvider(TestSuite.class, new ReportInfoProvider<>(false, ts -> "ts", ts -> ts.getName(), ts -> ts.getDescription(), ts -> ts.getAuthor()));
		addProvider(TestCase.class, new ReportInfoProvider<>(true, tc -> "tc", tc -> tc.getName(), tc -> tc.getDescription(), tc -> tc.getAuthor()));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> void addProvider(Class<T> type, ReportInfoProvider<T> provider)
	{
		this.providers.put(type, (ReportInfoProvider) provider);
	}
	
	private ReportInfoProvider<Object> getProvider(Object executor)
	{
		ReportInfoProvider<Object> provider = providers.get(executor.getClass());
		
		if(provider == null)
		{
			throw new InvalidArgumentException("Invalid executor type encouentered: {}", executor.getClass().getName());
		}
		
		return provider;
	}
	
	public String getCode(Executor executor)
	{
		return getProvider(executor.getExecutable()).getCode(executor.getExecutable());
	}
	
	public String getAuthor(Executor executor)
	{
		return getProvider(executor.getExecutable()).getAuthor(executor.getExecutable());
	}
	
	public String getName(Executor executor)
	{
		String name = getProvider(executor.getExecutable()).getName(executor.getExecutable());
		
		if(executor instanceof TestCaseExecutor)
		{
			TestCaseExecutor tcExecutor = (TestCaseExecutor) executor;
			TestCaseData testCaseData = tcExecutor.getTestCaseData();
			
			if(testCaseData != null)
			{
				name += " [" + testCaseData.getName() + "]";
			}
		}
		
		return name;
	}

	/**
	 * Name to be used for log files.
	 * @param executor
	 * @return
	 */
	public String getLogName(Executor executor)
	{
		String name = getProvider(executor.getExecutable()).getName(executor.getExecutable());
		
		if(executor instanceof TestCaseExecutor)
		{
			TestCaseExecutor tcExecutor = (TestCaseExecutor) executor;
			TestCase testCase = tcExecutor.getTestCase();
			
			name = testCase.getParentTestSuite().getName() + "_" + name;
					
			TestCaseData testCaseData = tcExecutor.getTestCaseData();
			
			if(testCaseData != null)
			{
				name += " [" + testCaseData.getName() + "]";
			}
		}
		
		return name;
	}

	public String getDescription(Executor executor)
	{
		if(executor instanceof TestCaseExecutor)
		{
			TestCaseExecutor tcExecutor = (TestCaseExecutor) executor;
			TestCaseData testCaseData = tcExecutor.getTestCaseData();
			
			if(testCaseData != null)
			{
				return testCaseData.getDescription();
			}
		}

		return getProvider(executor.getExecutable()).getDescription(executor.getExecutable());
	}
	
	public boolean isSingleLogger(Executor executor)
	{
		return getProvider(executor.getExecutable()).isSingleLogger();
	}
}
