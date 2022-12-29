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
package com.yukthitech.autox.ide.actions;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.debug.common.ClientMssgDebugOp;
import com.yukthitech.autox.debug.common.DebugOp;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironmentManager;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;

@ActionHolder
public class DebugActions
{
	@Autowired
	private ExecutionEnvironmentManager executionEnvManager;
	
	private void executeDebugOp(DebugOp debugOp)
	{
		ExecutionEnvironment activeEnv = executionEnvManager.getActiveEnvironment();
		
		if(activeEnv == null || !activeEnv.isDebugEnv() || activeEnv.getActiveThreadId() == null)
		{
			return;
		}
		
		activeEnv.sendDataToServer(new ClientMssgDebugOp(activeEnv.getActiveThreadId(), debugOp));
	}
	
	@Action
	public void debugStepInto()
	{
		executeDebugOp(DebugOp.STEP_INTO);
	}
	
	@Action
	public void debugStepOver()
	{
		executeDebugOp(DebugOp.STEP_OVER);
	}
	
	@Action
	public void debugStepReturn()
	{
		executeDebugOp(DebugOp.STEP_RETURN);
	}
}
