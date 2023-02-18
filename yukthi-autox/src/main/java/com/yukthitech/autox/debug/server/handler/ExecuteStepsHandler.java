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
package com.yukthitech.autox.debug.server.handler;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.common.ClientMssgExecuteSteps;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.DebugServer;
import com.yukthitech.autox.debug.server.LiveDebugPoint;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.autox.test.CustomPrefixExpression;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class ExecuteStepsHandler extends AbstractServerDataHandler<ClientMssgExecuteSteps>
{
	private static Logger logger = LogManager.getLogger(ExecuteStepsHandler.class);

	private static String stepHolderTemplate;
	
	static
	{
		try
		{
			stepHolderTemplate = IOUtils.toString(ExecuteStepsHandler.class.getResourceAsStream("/step-holder-template.xml"), Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading resource: /step-holder-template.xml", ex);
		}
	}

	public ExecuteStepsHandler()
	{
		super(ClientMssgExecuteSteps.class);
	}

	@Override
	public void processData(ClientMssgExecuteSteps steps)
	{
		LiveDebugPoint livepoint = DebugFlowManager.getInstance().getLiveDebugPoint(steps.getExecutionId());
		
		if(livepoint == null)
		{
			logger.warn("Invalid execution id specified: " + steps.getExecutionId());
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), false, "Invalid live point id specified: %s", steps.getExecutionId()));
			return;
		}
		
		StepHolder stepHolder = parseSteps(steps.getStepsToExecute());
		stepHolder.updateLocations(steps.getSourceFile(), steps.getStartLineNumber());
		
		AutomationContext automationContext = AutomationContext.getInstance();
		boolean reloadingDone = false;
		
		if(CollectionUtils.isNotEmpty(stepHolder.getCustomUiLocators()))
		{
			for(CustomPrefixExpression customUiLocator : stepHolder.getCustomUiLocators())
			{
				logger.debug("Reloading custom-ui-locator: {}", customUiLocator.getName());
				PrefixExpressionFactory.getExpressionFactory().addOrReplaceUiLocator(customUiLocator);
			}
			
			reloadingDone = true;
		}
		
		if(CollectionUtils.isNotEmpty(stepHolder.getCustomPrefixExpressions()))
		{
			for(CustomPrefixExpression customPrefixExpr : stepHolder.getCustomPrefixExpressions())
			{
				logger.debug("Reloading custom-prefix-expression: {}", customPrefixExpr.getName());
				PrefixExpressionFactory.getExpressionFactory().addOrReplacePrefixExpression(customPrefixExpr);
			}
			
			reloadingDone = true;
		}

		if(CollectionUtils.isNotEmpty(stepHolder.getFunctions()))
		{
			String targetTestSuiteName = steps.getTargetTestSuite();
			TestSuite targetTestSuite = null;
			
			if(StringUtils.isNotBlank(targetTestSuiteName))
			{
				TestSuiteGroup group = automationContext.getTestSuiteGroup();
				targetTestSuite = (group != null) ? group.getTestSuite(targetTestSuiteName) : null;
				
				if(targetTestSuite == null)
				{
					logger.warn("Invalid test suite name specified for function reload: {}", targetTestSuiteName);
					DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), false, 
							"Invalid test suite name specified for function reload: %s", targetTestSuiteName));
					return;
				}
			}

			for(Function func : stepHolder.getFunctions())
			{
				logger.debug("Reloading function: {}", func.getName());
				
				if(targetTestSuite != null)
				{
					targetTestSuite.addOrReplaceFunction(func);
				}
				else
				{
					automationContext.addOrReplaceFunction(func);
				}
			}
			
			reloadingDone = true;
		}

		if(CollectionUtils.isEmpty(stepHolder.getSteps()))
		{
			if(reloadingDone)
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), true, "Reloading completed successfully"));
			}
			else
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), false, "No steps found to execute: %s", steps.getExecutionId()));
			}
			
			return;
		}
		
		livepoint.requestExecuteSteps(steps.getRequestId(), stepHolder.getSteps());
	}
	
	private StepHolder parseSteps(String xml)
	{
		StepHolder stepHolder = new StepHolder();
		
		String stepXml = CommonUtils.replaceExpressions(
				CommonUtils.toMap("steps", xml), 
				stepHolderTemplate, null);
		
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(stepXml.getBytes());
			XMLBeanParser.parse(bis, stepHolder, AutomationContext.getInstance().getTestSuiteParserHandler());
			
			return stepHolder;
		} catch(Exception ex)
		{
			logger.error("Failed to parse step list from interactive step xml:\n", xml, ex);
			return null;
		}
	}
}
