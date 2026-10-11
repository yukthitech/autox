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

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.debug.common.ClientMssgExecuteSteps;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.LiveDebugPoint;
import com.yukthitech.autox.interactive.InteractiveXmlSupport;
import com.yukthitech.autox.interactive.StepHolder;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Thin adapter: parse/reload via {@link InteractiveXmlSupport}, then run on the live debug point.
 */
public class ExecuteStepsHandler extends AbstractServerDataHandler<ClientMssgExecuteSteps>
{
	private static Logger logger = LogManager.getLogger(ExecuteStepsHandler.class);

	public ExecuteStepsHandler()
	{
		super(ClientMssgExecuteSteps.class);
	}

	@Override
	public void processData(ClientMssgExecuteSteps steps)
	{
		LiveDebugPoint livepoint = debugFlowManager.getLiveDebugPoint(steps.getExecutionId());
		
		if(livepoint == null)
		{
			logger.warn("Invalid execution id specified: " + steps.getExecutionId());
			debugFlowManager.sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), false, "Invalid live point id specified: %s", steps.getExecutionId()));
			return;
		}
		
		StepHolder stepHolder;
		boolean reloadingDone;
		
		try
		{
			stepHolder = InteractiveXmlSupport.parseSteps(automationContext, steps.getStepsToExecute());
			stepHolder.updateLocations(steps.getSourceFile(), steps.getStartLineNumber());
			reloadingDone = InteractiveXmlSupport.applyReloads(automationContext, stepHolder, steps.getTargetTestSuite());
		}catch(InvalidArgumentException ex)
		{
			logger.warn(ex.getMessage());
			debugFlowManager.sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), false, ex.getMessage()));
			return;
		}catch(Exception ex)
		{
			logger.error("Failed to parse/apply interactive step xml", ex);
			debugFlowManager.sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), false,
					"Failed to parse interactive steps: %s", ex.getMessage()));
			return;
		}

		if(CollectionUtils.isEmpty(stepHolder.getSteps()))
		{
			if(reloadingDone)
			{
				debugFlowManager.sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), true, "Reloading completed successfully"));
			}
			else
			{
				debugFlowManager.sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), false, "No steps found to execute: %s", steps.getExecutionId()));
			}
			
			return;
		}
		
		livepoint.requestExecuteSteps(steps.getRequestId(), stepHolder.getSteps());
	}
}
