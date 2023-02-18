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

import com.yukthitech.autox.debug.common.ClientMssgDebugOp;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.DebugServer;
import com.yukthitech.autox.debug.server.LiveDebugPoint;

/**
 * Used to update debug points.
 * @author akiran
 */
public class DebugOpHandler extends AbstractServerDataHandler<ClientMssgDebugOp>
{
	private static Logger logger = LogManager.getLogger(DebugOpHandler.class);
	
	public DebugOpHandler()
	{
		super(ClientMssgDebugOp.class);
	}
	
	@Override
	public void processData(ClientMssgDebugOp data)
	{
		logger.debug("Executing debug OP command: {}", data);
		
		LiveDebugPoint livePoint = DebugFlowManager.getInstance().getLiveDebugPoint(data.getExecutionId());
		
		if(livePoint == null)
		{
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(data.getRequestId(), false, "No live-debug-point found with execution id: " + data.getExecutionId()));
			return;
		}
		
		boolean res = livePoint.requestRelease(data.getExecutionId(), data.getDebugOp());
		
		if(res)
		{
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(data.getRequestId(), true, null));
		}
	}
}
