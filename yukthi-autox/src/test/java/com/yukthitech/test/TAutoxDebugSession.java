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
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.common.ContentStream;
import com.yukthitech.autox.debug.InProcessDebugEventSink;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.common.ServerMssgEvalExprResult;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.debug.common.ServerMssgExecutionReleased;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.session.AutoxDebugSession;
import com.yukthitech.autox.test.Function;

/**
 * In-process debug session: pause via sink, resume / eval from dispatcher thread.
 */
public class TAutoxDebugSession extends BaseTestCases
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
	public void testInProcessPauseAndResume() throws Exception
	{
		InProcessDebugEventSink sink = new InProcessDebugEventSink();
		CountDownLatch pausedLatch = new CountDownLatch(1);
		CountDownLatch releasedLatch = new CountDownLatch(1);
		AtomicReference<String> pausedExecutionId = new AtomicReference<>();
		AtomicInteger pauseCount = new AtomicInteger(0);
		AtomicReference<AutoxDebugSession> sessionRef = new AtomicReference<>();
		
		sink.addListener((Serializable event) ->
		{
			if(event instanceof ServerMssgExecutionPaused)
			{
				ServerMssgExecutionPaused paused = (ServerMssgExecutionPaused) event;
				pauseCount.incrementAndGet();
				pausedExecutionId.set(paused.getExecutionId());
				pausedLatch.countDown();
				
				AutoxDebugSession session = sessionRef.get();
				Assert.assertNotNull(session);
				Assert.assertTrue(session.resume(paused.getExecutionId()));
			}
			else if(event instanceof ServerMssgExecutionReleased)
			{
				releasedLatch.countDown();
			}
		});
		
		String xml =
				"<testData xmlns:s=\"http://autox.yukthitech.com/steps\">\n"
				+ "  <function name=\"dbgPauseFn\">\n"
				+ "    <s:log message=\"inprocess-debug-line-a\"/>\n"
				+ "    <s:log message=\"inprocess-debug-line-b\"/>\n"
				+ "    <s:return value=\"string: done\"/>\n"
				+ "  </function>\n"
				+ "</testData>";
		
		try (AutoxDebugSession session = AutoxDebugSession.start(ContentStream.fromFile(APP_CONFIG), sessionArgs("./output/session-debug-1"), sink))
		{
			sessionRef.set(session);
			
			DebugFlowManager flowManager = (DebugFlowManager) session.getAutomationContext().getDebugFlowManager();
			Assert.assertTrue(flowManager.isDebugMode());
			Assert.assertSame(flowManager.getEventSink(), sink);
			
			session.loadXml(ContentStream.fromString(xml, "host:inprocess-debug.xml"));
			
			Function function = session.getAutomationContext().getGlobalFunction("dbgPauseFn");
			Assert.assertNotNull(function);
			Assert.assertFalse(function.getSteps().isEmpty());
			
			IStep firstStep = function.getSteps().get(0);
			session.addDebugPoint(new DebugPoint(firstStep.getLocation().getPath(), firstStep.getLineNumber(), null));
			
			Object result = session.runFunction("dbgPauseFn");
			Assert.assertEquals(result, "done");
			
			Assert.assertTrue(pausedLatch.await(5, TimeUnit.SECONDS), "Expected pause event");
			Assert.assertTrue(releasedLatch.await(5, TimeUnit.SECONDS), "Expected release event");
			Assert.assertEquals(pauseCount.get(), 1);
			Assert.assertNotNull(pausedExecutionId.get());
		}
	}
	
	@Test
	public void testEvalWhilePaused() throws Exception
	{
		InProcessDebugEventSink sink = new InProcessDebugEventSink();
		CountDownLatch done = new CountDownLatch(1);
		AtomicReference<Object> evalValue = new AtomicReference<>();
		AtomicReference<String> executionId = new AtomicReference<>();
		AtomicReference<String> evalRequestId = new AtomicReference<>();
		AtomicReference<AutoxDebugSession> sessionRef = new AtomicReference<>();
		
		sink.addListener((Serializable event) ->
		{
			if(event instanceof ServerMssgExecutionPaused)
			{
				ServerMssgExecutionPaused paused = (ServerMssgExecutionPaused) event;
				executionId.set(paused.getExecutionId());
				evalRequestId.set(sessionRef.get().evalWhilePaused(paused.getExecutionId(), "int: 21"));
			}
			else if(event instanceof ServerMssgEvalExprResult)
			{
				ServerMssgEvalExprResult result = (ServerMssgEvalExprResult) event;
				
				if(evalRequestId.get() != null && evalRequestId.get().equals(result.getRequestId()))
				{
					Assert.assertTrue(result.isSuccessful(), result.getError());
					evalValue.set(SerializationUtils.deserialize(result.getResult()));
					Assert.assertTrue(sessionRef.get().resume(executionId.get()));
					done.countDown();
				}
			}
		});
		
		String xml =
				"<testData xmlns:s=\"http://autox.yukthitech.com/steps\">\n"
				+ "  <function name=\"dbgEvalFn\">\n"
				+ "    <s:log message=\"eval-pause-here\"/>\n"
				+ "    <s:return value=\"string: ok\"/>\n"
				+ "  </function>\n"
				+ "</testData>";
		
		try (AutoxDebugSession session = AutoxDebugSession.start(ContentStream.fromFile(APP_CONFIG), sessionArgs("./output/session-debug-eval"), sink))
		{
			sessionRef.set(session);
			session.loadXml(ContentStream.fromString(xml, "host:inprocess-debug-eval.xml"));
			
			IStep step = session.getAutomationContext().getGlobalFunction("dbgEvalFn").getSteps().get(0);
			session.addDebugPoint(new DebugPoint(step.getLocation().getPath(), step.getLineNumber(), null));
			
			Assert.assertEquals(session.runFunction("dbgEvalFn"), "ok");
			Assert.assertTrue(done.await(10, TimeUnit.SECONDS), "Expected eval + resume");
			Assert.assertEquals(evalValue.get(), 21);
		}
	}
}
