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
import com.yukthitech.autox.test.Function;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Embed session API: start → loadXml / load suites → runFunction / executeSteps → close.
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
	
	@Test
	public void testLoadXmlWithSourceNameAndRunFunction() throws Exception
	{
		String xml =
				"<testData xmlns:s=\"http://autox.yukthitech.com/steps\">\n"
				+ "  <function name=\"sessionLoadXmlSum\">\n"
				+ "    <s:set expression=\"sumResult\" value=\"expr: param.a + param.b | int: $\"/>\n"
				+ "  </function>\n"
				+ "</testData>";
		
		String sourceName = "host:session-load-xml.xml";
		
		try (AutoxSession session = AutoxSession.start(APP_CONFIG, sessionArgs("./output/session-load-xml")))
		{
			session.loadXml(xml, sourceName);
			
			Function function = session.getAutomationContext().getGlobalFunction("sessionLoadXmlSum");
			Assert.assertNotNull(function);
			// Logical names with ':' are sanitized for File location on Windows
			Assert.assertTrue(function.getLocation().getPath().contains("session-load-xml.xml"));
			Assert.assertTrue(function.getLocation().getName().contains("session-load-xml.xml"));
			
			Map<String, Object> params = new HashMap<>();
			params.put("a", 3);
			params.put("b", 4);
			Assert.assertNull(session.runFunction("sessionLoadXmlSum", params));
			
			try
			{
				session.loadXml(xml, "  ");
				Assert.fail("Expected InvalidArgumentException for blank sourceName");
			}catch(InvalidArgumentException ex)
			{
				Assert.assertTrue(ex.getMessage().contains("sourceName"));
			}
		}
	}
	
	@Test
	public void testExecuteStepsDynamicSource() throws Exception
	{
		try (AutoxSession session = AutoxSession.start(APP_CONFIG, sessionArgs("./output/session-execute-steps")))
		{
			// Mode 2: no sourceName; locations use generated interactive-steps-N.xml
			session.executeSteps("<s:log message=\"hello from session executeSteps\"/>");
			
			session.executeSteps(
					"<function name=\"sessionDynFunc\">"
					+ "<s:return value=\"string: ok\"/>"
					+ "</function>");
			
			Function dynFunc = session.getAutomationContext().getGlobalFunction("sessionDynFunc");
			Assert.assertNotNull(dynFunc);
			Assert.assertNotNull(dynFunc.getLocation());
			Assert.assertTrue(dynFunc.getLocation().getName().startsWith("interactive-steps-"));
			
			Assert.assertEquals(session.runFunction("sessionDynFunc"), "ok");
		}
	}
}
