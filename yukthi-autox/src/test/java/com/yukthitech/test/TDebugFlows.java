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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.debug.client.DebugClient;
import com.yukthitech.autox.debug.client.IDebugClientHandler;
import com.yukthitech.autox.debug.common.ClientMssgDebugOp;
import com.yukthitech.autox.debug.common.ClientMssgDebuggerInit;
import com.yukthitech.autox.debug.common.ClientMssgExecuteSteps;
import com.yukthitech.autox.debug.common.DebugOp;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.debug.common.ServerMssgExecutionReleased;
import com.yukthitech.autox.exec.report.FinalReport;
import com.yukthitech.utils.ObjectWrapper;

public class TDebugFlows extends BaseTestCases
{
	private static Logger logger = LogManager.getLogger(TDebugFlows.class);
	
	private static class DebugClientHandler implements IDebugClientHandler
	{
		private DebugClient debugClient;
		
		private List<Integer> pausedLocations = new ArrayList<>();
		
		private String expectedFile;
		
		private DebugOp debugOp;
		
		private BiConsumer<ServerMssgExecutionPaused, DebugClientHandler> pauseConsumer;
		
		public DebugClientHandler(DebugClient debugClient, String expectedFile, DebugOp debugOp)
		{
			this.debugClient = debugClient;
			this.expectedFile = expectedFile;
			this.debugOp = debugOp;
		}

		@Override
		public void processData(Serializable data)
		{
			if(data instanceof ServerMssgExecutionPaused)
			{
				ServerMssgExecutionPaused mssg = (ServerMssgExecutionPaused) data;
				
				Assert.assertEquals(mssg.getDebugFilePath(), expectedFile);
				pausedLocations.add(mssg.getLineNumber());
				
				logger.debug("Debug with execution-id {} is pasused at {}:{}", 
						mssg.getExecutionId(), mssg.getDebugFilePath(), mssg.getLineNumber());
				
				if(pauseConsumer != null)
				{
					pauseConsumer.accept(mssg, this);
				}
				
				debugClient.sendDataToServer(new ClientMssgDebugOp(mssg.getExecutionId(), debugOp));
			}
			else if(data instanceof ServerMssgExecutionReleased)
			{
				ServerMssgExecutionReleased mssg = (ServerMssgExecutionReleased) data;
				logger.debug("Debug with execution-id {} is released", mssg.getExecutionId());
			}
		}
	}
	
	public void testDebugFlow(DebugOp debugOp, List<Integer> debugPointLines, 
			BiConsumer<ServerMssgExecutionPaused, DebugClientHandler> pauseConsumer, 
			String testCase,
			List<Integer> expectedPauses) throws Exception
	{
		ObjectWrapper<DebugClientHandler> clientHandler = new ObjectWrapper<>();
		
		Thread clientThread = new Thread() 
		{
			public void run()
			{
				try
				{
					String debugFile = new File("./src/test/resources/new-test-suites/debug-flow/debug-flow-suite.xml").getCanonicalPath();
					
					List<DebugPoint> points = new ArrayList<>();
					
					for(int line : debugPointLines)
					{
						points.add(new DebugPoint(debugFile, line, null));
					}
					
					ClientMssgDebuggerInit initDebugPoints = new ClientMssgDebuggerInit(points);
					
					DebugClient debugClient = DebugClient.newClient("localhost", 9876, initDebugPoints);
					DebugClientHandler handler = new DebugClientHandler(debugClient, debugFile, debugOp);
					handler.pauseConsumer = pauseConsumer;
					
					clientHandler.setValue(handler);
					
					debugClient
						.addDataHandler(handler)
						.start();
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		};
		
		clientThread.start();
		
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/new-test-suites/debug-flow",
				"-rf", "./output/debug-flow", 
				"-prop", "./src/test/resources/app.properties", 
				"--debug-port", "9876",
				"--report-opening-disabled", "true",
				//"-ts", "data-provider-err"
				"-tc", testCase
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		
		System.out.println("Halt points: " + clientHandler.getValue().pausedLocations);
		
		Assert.assertEquals(clientHandler.getValue().pausedLocations, expectedPauses);
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/debug-flow/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 1, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 1, "Found one more test cases.");
		Assert.assertEquals(exeResult.getTestCaseSuccessCount(), 1, "Found one more test cases errored.");
	}
	
	@Test
	public void testStepIntoFlow() throws Exception
	{
		testDebugFlow(DebugOp.STEP_INTO, Arrays.asList(8), null, "debugTest1", Arrays.asList(
				//setup
				8, 9,
				
				//testcase
				29, 30, 32,
					//function2
					18, 19, 21,
					//function 1
					13, 14,
					
				//cleanup	
				47, 48
			));
	}

	@Test
	public void testStepOverFlow() throws Exception
	{
		testDebugFlow(DebugOp.STEP_OVER, Arrays.asList(8), null, "debugTest1", Arrays.asList(
				//setup
				8, 9,
				
				//testcase
				29, 30, 32,
					
				//cleanup	
				47, 48
			));
	}
	
	/**
	 * Test the same step over functionality but with a extra break point in middle
	 * which will not be in general covered by first debug point during step-over.
	 * @throws Exception
	 */
	@Test
	public void testStepOverFlow_midPoint() throws Exception
	{
		testDebugFlow(DebugOp.STEP_OVER, Arrays.asList(8, 13), null, "debugTest1", Arrays.asList(
				//setup
				8, 9,
				
				//testcase
				29, 30, 32,
				
				//function2 (because of second debug point)
				13, 14,
					
				//cleanup	
				47, 48
			));
	}

	@Test
	public void testStepReturnFlow() throws Exception
	{
		testDebugFlow(DebugOp.STEP_RETURN, Arrays.asList(8, 13, 48), null, "debugTest1", Arrays.asList(8, 13, 48));
	}

	@Test
	public void testExpressionEvaluation() throws Exception
	{
		ObjectWrapper<Boolean> consumerExectued = new ObjectWrapper<>(false);
		
		BiConsumer<ServerMssgExecutionPaused, DebugClientHandler> onPause = (mssg, handler) -> 
		{
			consumerExectued.setValue(true);
			
			//check whether attr is coming properly
			Object value = SerializationUtils.deserialize(mssg.getContextAttr().get("someAttr"));
			Assert.assertEquals(value, 10);
			
			//using step evaluation set attr to 100
			String steps = "<s:log message=\"This is from dyn step execution..\"/>\n"
					+ "<s:set expression=\"someAttr\" value=\"int: 100\"/>\n";
			
			handler.debugClient.sendDataToServer(new ClientMssgExecuteSteps(mssg.getExecutionId(), steps));
			
			//wait for 5 seconds, so that step execution is completed
			AutomationUtils.sleep(5000);
		};
		
		testDebugFlow(DebugOp.STEP_RETURN, Arrays.asList(41), onPause, "debugExprTest", Arrays.asList(41));
		Assert.assertTrue(consumerExectued.getValue());
	}

}
