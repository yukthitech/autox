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

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AutoxCliArguments;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.test.TestSuiteGroup;

/**
 * Executor of test suite group.
 * @author akranthikiran
 */
public class TestSuiteGroupExecutor extends Executor
{
	public TestSuiteGroupExecutor(TestSuiteGroup testSuiteGroup)
	{
		super(testSuiteGroup, "Test-Suite");
		
		super.setup = testSuiteGroup.getSetup();
		super.cleanup = testSuiteGroup.getCleanup();
		
		AutomationContext context = AutomationContext.getInstance();
		String parallelExecutionEnabled = context.getOverridableProp(IAutomationConstants.AUTOX_PROP_PARALLEL_EXEC_ENABLED);
		super.parallelExecutionEnabled = "true".equalsIgnoreCase(parallelExecutionEnabled);
		
		AutoxCliArguments basicArguments = context.getBasicArguments();
		Set<String> limitedTestSuites = basicArguments.getTestSuitesSet();
		Set<String> restrictedTestCases = basicArguments.getTestCasesSet();
		
		testSuiteGroup
			.getTestSuites()
			.stream()
			.filter(ts -> 
			{
				if(limitedTestSuites != null && !limitedTestSuites.contains(ts.getName()))
				{
					return false;
				}
				
				if(restrictedTestCases != null && !ts.hasAnyTestCases(restrictedTestCases))
				{
					return false;
				}
				
				return true;
			})
			.map(ts -> new TestSuiteExecutor(ts))
			.filter(tse -> CollectionUtils.isNotEmpty(tse.getChildExecutors()))
			.forEach(exec -> addChildExector(exec));
	}
}
