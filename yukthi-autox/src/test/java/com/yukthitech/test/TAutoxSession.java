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
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.session.AutoxSession;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Phase 2 embed API: start → load suites → runFunction (multiple) → close → start again.
 */
public class TAutoxSession extends BaseTestCases
{
	private static final File APP_CONFIG = new File("./src/test/resources/app-configuration.xml");
	
	private static String[] sessionArgs(String reportFolder)
	{
		return new String[] {
				"-rf", reportFolder,
				"-prop", "./src/test/resources/app.properties",
				"-rod", "true",
				"-flmt", "./src/test/resources/test-suites"
		};
	}
	
	@Test
	public void testRunFunctionMultipleTimesAndRestart() throws Exception
	{
		Map<String, Object> params = new HashMap<>();
		params.put("a", 10);
		params.put("b", 20);
		
		try (AutoxSession session = AutoxSession.start(APP_CONFIG, sessionArgs("./output/session-api-1")))
		{
			session.loadTestSuiteFiles();
			
			Assert.assertNull(session.runFunction("sumWithoutReturn", params));
			
			params.put("a", 5);
			params.put("b", 7);
			Assert.assertNull(session.runFunction("sumWithoutReturn", params));
			
			// second load is idempotent
			session.loadTestSuiteFiles();
			
			Assert.assertNotNull(session.getAutomationContext());
			Assert.assertSame(session.getAutomationContext(), AutomationContext.getInstance());
			Assert.assertNotNull(session.getAutomationContext().getGlobalFunction("sumWithoutReturn"));
		}
		
		Assert.assertNull(AutomationContext.getInstance());
		
		try (AutoxSession session = AutoxSession.start(APP_CONFIG, sessionArgs("./output/session-api-2")))
		{
			session.loadTestSuiteFiles();
			Assert.assertNull(session.runFunction("sumWithoutReturn", params));
		}
		
		Assert.assertNull(AutomationContext.getInstance());
	}
	
	@Test
	public void testUnknownFunctionAndClosedSession() throws Exception
	{
		AutoxSession session = AutoxSession.start(APP_CONFIG, sessionArgs("./output/session-api-err"));
		
		try
		{
			session.loadTestSuiteFiles();
			
			try
			{
				session.runFunction("noSuchFunctionExists");
				Assert.fail("Expected InvalidArgumentException");
			}catch(InvalidArgumentException ex)
			{
				Assert.assertTrue(ex.getMessage().contains("noSuchFunctionExists"));
			}
		}finally
		{
			session.close();
		}
		
		try
		{
			session.runFunction("sumWithoutReturn");
			Assert.fail("Expected InvalidStateException for closed session");
		}catch(InvalidStateException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("closed"));
		}
	}
	
	@Test
	public void testSingleActiveSessionGuard() throws Exception
	{
		AutoxSession session = AutoxSession.start(APP_CONFIG, sessionArgs("./output/session-api-guard"));
		
		try
		{
			try
			{
				AutoxSession.start(APP_CONFIG, sessionArgs("./output/session-api-guard-2"));
				Assert.fail("Expected InvalidStateException for second start");
			}catch(InvalidStateException ex)
			{
				Assert.assertTrue(ex.getMessage().contains("already active"));
			}
		}finally
		{
			session.close();
		}
	}
}
