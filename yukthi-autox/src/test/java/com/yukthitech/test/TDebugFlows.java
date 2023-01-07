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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
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
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.test.DebugFlowTestData.Case;
import com.yukthitech.utils.ObjectWrapper;

public class TDebugFlows extends BaseTestCases
{
	private static Logger logger = LogManager.getLogger(TDebugFlows.class);
	
	private static class DebugClientHandler implements IDebugClientHandler
	{
		private DebugClient debugClient;
		
		private List<Integer> pausedLocations = new ArrayList<>();
		private Map<Integer, String> pausedStrackTraces = new LinkedHashMap<>();
		
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
				
				pausedStrackTraces.put(
						mssg.getLineNumber(), 
						mssg.getStackTrace()
							.stream()
							.map(elem -> new File(elem.getFile()).getName() + ":" + elem.getLineNumber())
							.collect(Collectors.joining(", ")
						));
				
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
			List<Integer> expectedPauses,
			Map<Integer, String> expectedStackTraces) throws Exception
	{
		ObjectWrapper<DebugClientHandler> clientHandler = new ObjectWrapper<>();
		
		Thread clientThread = new Thread() 
		{
			public void run()
			{
				try
				{
					String filePath = testCase.startsWith("debug2_") ?
							"./src/test/resources/new-test-suites/debug-flow/debug-flow-suite-2.xml" :
							"./src/test/resources/new-test-suites/debug-flow/debug-flow-suite.xml";
					
					String debugFile = new File(filePath).getCanonicalPath();
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
		System.out.println("Stack Traces: \n\t" + clientHandler.getValue()
			.pausedStrackTraces
			.entrySet()
			.stream()
			.map(entry -> entry.getKey() + " => " + entry.getValue())
			.collect(Collectors.joining("\n\t")));
		
		
		Assert.assertEquals(clientHandler.getValue().pausedLocations, expectedPauses);
		
		if(expectedStackTraces != null)
		{
			Assert.assertEquals(clientHandler.getValue().pausedStrackTraces, expectedStackTraces);
		}
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/debug-flow/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 1, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 1, "Found one more test cases.");
		Assert.assertEquals(exeResult.getTestCaseSuccessCount(), 1, "Found one more test cases errored.");
	}
	
	@DataProvider(name = "testDataProvider")
	public Object[][] testDataProvider()
	{
		DebugFlowTestData testData = new DebugFlowTestData();
		XMLBeanParser.parse(TDebugFlows.class.getResourceAsStream("/data/debug/debug-test-data.xml"), testData);
		
		List<Object[]> res = new ArrayList<>();
		
		for(DebugFlowTestData.Case caseObj : testData.getCases())
		{
			if(!"stepReturn".equals(caseObj.getName()))
			{
				continue;
			}

			res.add(new Object[] {caseObj.getName(), caseObj});
		}
		
		return res.toArray(new Object[0][]);
	}
	
	@Test(dataProvider =  "testDataProvider")
	public void testBasicFlows(String name, Case tcase) throws Exception
	{
		testDebugFlow(
				tcase.getOp(), 
				tcase.getDebugPoints(), 
				null, 
				tcase.getTestCase(), 
				tcase.getPausePoints(), 
				tcase.getStackTraces());
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
			
			handler.debugClient.sendDataToServer(new ClientMssgExecuteSteps(mssg.getExecutionId(), steps, null));
			
			//wait for 5 seconds, so that step execution is completed
			AutomationUtils.sleep(5000);
		};
		
		testDebugFlow(DebugOp.RESUME, Arrays.asList(42), onPause, "debugExprTest", Arrays.asList(42), null);
		Assert.assertTrue(consumerExectued.getValue());
	}

	@Test
	public void testFunctionReload() throws Exception
	{
		ObjectWrapper<Boolean> consumerExectued = new ObjectWrapper<>(false);
		
		BiConsumer<ServerMssgExecutionPaused, DebugClientHandler> onPause = (mssg, handler) -> 
		{
			consumerExectued.setValue(true);
			
			//using step evaluation set attr to 100
			String steps = "<function name=\"testOp\"><s:return value=\"expr: (param.param1 * param.param2)\"/></function>";
			
			handler.debugClient.sendDataToServer(new ClientMssgExecuteSteps(mssg.getExecutionId(), steps, "debug-flow"));
			
			//wait for 5 seconds, so that step execution is completed
			AutomationUtils.sleep(5000);
		};
		
		testDebugFlow(DebugOp.RESUME, Arrays.asList(59), onPause, "functionReload", Arrays.asList(59), null);
		Assert.assertTrue(consumerExectued.getValue());
	}
}
