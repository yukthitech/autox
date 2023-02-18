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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.debug.common.ClientMssgEvalExpression;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.DebugServer;
import com.yukthitech.autox.debug.server.LiveDebugPoint;

public class EvalExpressionHandler extends AbstractServerDataHandler<ClientMssgEvalExpression>
{
	private static Logger logger = LogManager.getLogger(EvalExpressionHandler.class);

	public EvalExpressionHandler()
	{
		super(ClientMssgEvalExpression.class);
	}

	@Override
	public void processData(ClientMssgEvalExpression evalExpr)
	{
		LiveDebugPoint livepoint = DebugFlowManager.getInstance().getLiveDebugPoint(evalExpr.getExecutionId());
		
		if(livepoint == null)
		{
			logger.warn("Invalid live point id specified: " + evalExpr.getExecutionId());
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(evalExpr.getRequestId(), false, "Invalid live point id specified: %s", evalExpr.getExecutionId()));
			return;
		}
		
		livepoint.requestEvalExpression(evalExpr.getRequestId(), evalExpr.getExpression());
	}
}
