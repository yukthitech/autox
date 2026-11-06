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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.autox.common.ContentStream;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.event.AutomationEventManager;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
import com.yukthitech.autox.session.AutoxSession;
import com.yukthitech.autox.session.IPluginSessionHandle;
import com.yukthitech.autox.test.Function;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Embed session API: interactive start → loadXml / runFunction / executeSteps / plugin handle.
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
		
		try (AutoxSession session = AutoxSession.startInteractive(ContentStream.fromFile(APP_CONFIG), sessionArgs("./output/session-api-1")))
		{
			session.loadTestSuiteFiles();
			
			Assert.assertNull(session.runFunction("sumWithoutReturn", params));
			
			params.put("a", 5);
			params.put("b", 7);
			Assert.assertNull(session.runFunction("sumWithoutReturn", params));
			
			session.loadTestSuiteFiles();
			
			Assert.assertNotNull(session.getAutomationContext());
			Assert.assertSame(session.getAutomationContext(), AutomationContext.getInstance());
			Assert.assertNotNull(session.getAutomationContext().getGlobalFunction("sumWithoutReturn"));
			Assert.assertTrue(session.isInteractive());
		}
		
		Assert.assertNull(AutomationContext.getInstance());
		
		try (AutoxSession session = AutoxSession.startInteractive(ContentStream.fromFile(APP_CONFIG), sessionArgs("./output/session-api-2")))
		{
			session.loadTestSuiteFiles();
			Assert.assertNull(session.runFunction("sumWithoutReturn", params));
		}
		
		Assert.assertNull(AutomationContext.getInstance());
	}
	
	@Test
	public void testUnknownFunctionAndClosedSession() throws Exception
	{
		AutoxSession session = AutoxSession.startInteractive(ContentStream.fromFile(APP_CONFIG), sessionArgs("./output/session-api-err"));
		
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
		AutoxSession session = AutoxSession.startInteractive(ContentStream.fromFile(APP_CONFIG), sessionArgs("./output/session-api-guard"));
		
		try
		{
			try
			{
				AutoxSession.startInteractive(ContentStream.fromFile(APP_CONFIG), sessionArgs("./output/session-api-guard-2"));
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
		
		try (AutoxSession session = AutoxSession.startInteractive(ContentStream.fromFile(APP_CONFIG), sessionArgs("./output/session-load-xml")))
		{
			session.loadXml(ContentStream.fromString(xml, sourceName));
			
			Function function = session.getAutomationContext().getGlobalFunction("sessionLoadXmlSum");
			Assert.assertNotNull(function);
			Assert.assertTrue(function.getLocation().getPath().contains("session-load-xml.xml"));
			Assert.assertTrue(function.getLocation().getName().contains("session-load-xml.xml"));
			
			Map<String, Object> params = new HashMap<>();
			params.put("a", 3);
			params.put("b", 4);
			Assert.assertNull(session.runFunction("sessionLoadXmlSum", params));
			
			try
			{
				ContentStream.fromString(xml, "  ");
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
		try (AutoxSession session = AutoxSession.startInteractive(ContentStream.fromFile(APP_CONFIG), sessionArgs("./output/session-execute-steps")))
		{
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
	
	@Test
	public void testDefaultSessionRejectsInteractiveApis() throws Exception
	{
		try (AutoxSession session = AutoxSession.start(ContentStream.fromFile(APP_CONFIG),
				sessionArgs("./output/session-default-guard"), new AutomationEventManager()))
		{
			Assert.assertFalse(session.isInteractive());
			
			try
			{
				session.executeSteps("<s:log message=\"nope\"/>");
				Assert.fail("Expected InvalidStateException");
			}catch(InvalidStateException ex)
			{
				Assert.assertTrue(ex.getMessage().contains("interactive"));
			}
			
			try
			{
				session.runFunction("anything");
				Assert.fail("Expected InvalidStateException");
			}catch(InvalidStateException ex)
			{
				Assert.assertTrue(ex.getMessage().contains("interactive"));
			}
			
			try
			{
				session.getPluginSession(PlaywrightPlugin.class);
				Assert.fail("Expected InvalidStateException");
			}catch(InvalidStateException ex)
			{
				Assert.assertTrue(ex.getMessage().contains("interactive"));
			}
		}
	}
	
	@Test
	public void testGetPluginSessionClosedInteractiveSession() throws Exception
	{
		AutoxSession session = AutoxSession.startInteractive(ContentStream.fromFile(APP_CONFIG),
				sessionArgs("./output/session-plugin-session"));
		session.close();
		
		try
		{
			session.getPluginSession(PlaywrightPlugin.class);
			Assert.fail("Expected InvalidStateException for closed session");
		}catch(InvalidStateException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("closed"));
		}
	}
	
	/**
	 * Interactive APIs from child threads: navigate, HTML snapshot via handle, UI click, runFunction.
	 */
	@Test
	public void testCrossThreadUiSnapshotAndOperations() throws Exception
	{
		AutoxSession session = AutoxSession.startInteractive(ContentStream.fromFile(APP_CONFIG),
				sessionArgs("./output/session-cross-thread-ui"));
		
		try
		{
			runOnChild("goto-index", () ->
			{
				session.executeSteps("<s:ui-goto-page uri=\"/index.html\"/>");
			});
			
			AtomicReference<String> mhtmlRef = new AtomicReference<>();
			runOnChild("html-snapshot", () ->
			{
				IPluginSessionHandle<PlaywrightPluginSession> handle = session.getPluginSession(PlaywrightPlugin.class);
				String mhtml = handle.execute(PlaywrightPluginSession::captureHtmlSnapshot);
				Assert.assertTrue(StringUtils.isNotBlank(mhtml), "Expected non-blank MHTML snapshot");
				mhtmlRef.set(mhtml);
			});
			Assert.assertTrue(StringUtils.isNotBlank(mhtmlRef.get()));
			
			runOnChild("button-click", () ->
			{
				session.executeSteps(
						"<s:ui-click locator=\"id: button\"/>"
						+ "<s:sleep time=\"1000\"/>"
						+ "<s:ui-assert-value locator=\"xpath: //input[@name='statusFld']\" value=\"button Clicked\"/>");
			});
			
			runOnChild("snapshot-after-click", () ->
			{
				String mhtml = session.getPluginSession(PlaywrightPlugin.class)
						.execute(PlaywrightPluginSession::captureHtmlSnapshot);
				Assert.assertTrue(StringUtils.isNotBlank(mhtml));
			});
			
			runOnChild("run-function", () ->
			{
				session.executeSteps(
						"<function name=\"crossThreadFn\">"
						+ "<s:return value=\"string: from-child\"/>"
						+ "</function>");
				Assert.assertEquals(session.runFunction("crossThreadFn"), "from-child");
			});
		}finally
		{
			session.close();
		}
	}
	
	private static void runOnChild(String name, ChildWork work) throws Exception
	{
		CountDownLatch done = new CountDownLatch(1);
		AtomicReference<Throwable> error = new AtomicReference<>();
		
		Thread thread = new Thread(() ->
		{
			try
			{
				work.run();
			}catch(Throwable ex)
			{
				error.set(ex);
			}finally
			{
				done.countDown();
			}
		}, "t-autox-session-" + name);
		
		thread.start();
		Assert.assertTrue(done.await(120, TimeUnit.SECONDS), "Child thread timed out: " + name);
		
		if(error.get() != null)
		{
			if(error.get() instanceof Exception)
			{
				throw (Exception) error.get();
			}
			
			if(error.get() instanceof Error)
			{
				throw (Error) error.get();
			}
			
			throw new RuntimeException(error.get());
		}
	}
	
	@FunctionalInterface
	private interface ChildWork
	{
		void run() throws Exception;
	}
}
