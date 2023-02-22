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
package com.yukthitech.prism.actions;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.debug.common.ClientMssgDebugOp;
import com.yukthitech.autox.debug.common.DebugOp;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.exeenv.ExecutionEnvironment;
import com.yukthitech.prism.exeenv.ExecutionEnvironmentManager;
import com.yukthitech.prism.layout.Action;
import com.yukthitech.prism.layout.ActionHolder;

@ActionHolder
public class DebugActions
{
	@Autowired
	private ExecutionEnvironmentManager executionEnvManager;
	
	private void executeDebugOp(DebugOp debugOp, boolean ignoreError)
	{
		ExecutionEnvironment activeEnv = executionEnvManager.getActiveEnvironment();
		
		if(activeEnv == null || !activeEnv.isDebugEnv() || activeEnv.getActiveThreadId() == null)
		{
			return;
		}
		
		ServerMssgExecutionPaused execDet = activeEnv.getActiveThreadDetails();
		
		if(execDet.isErrorPoint() && !ignoreError)
		{
			int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), "Current operation will resume the flow without ignoring the error.\n"
							+ "This may stop/error current execution. Are you sure you want to continue?", "Resume on Error", JOptionPane.YES_NO_OPTION);
			
			if(res != JOptionPane.YES_OPTION)
			{
				return;
			}
		}
		
		activeEnv.sendDataToServer(new ClientMssgDebugOp(activeEnv.getActiveThreadId(), debugOp, ignoreError));
	}
	
	/*
	@Action
	public void ignoreDebugError()
	{
		ExecutionEnvironment activeEnv = executionEnvManager.getActiveEnvironment();
		
		if(activeEnv == null || !activeEnv.isDebugEnv() || activeEnv.getActiveThreadId() == null)
		{
			return;
		}
		
		ServerMssgExecutionPaused mssg = activeEnv.getActiveThreadDetails();
		
		if(!mssg.isErrorPoint())
		{
			return;
		}
		
		activeEnv.sendDataToServer(new ClientMssgIgnoreError(activeEnv.getActiveThreadId()));
	}
	*/
	
	@Action
	public void debugStepInto()
	{
		executeDebugOp(DebugOp.STEP_INTO, false);
	}
	
	@Action
	public void debugStepOver()
	{
		executeDebugOp(DebugOp.STEP_OVER, false);
	}
	
	@Action
	public void debugStepReturn()
	{
		executeDebugOp(DebugOp.STEP_RETURN, false);
	}

	@Action
	public void debugResume()
	{
		executeDebugOp(DebugOp.RESUME, false);
	}

	@Action
	public void errDebugStepOver()
	{
		executeDebugOp(DebugOp.STEP_OVER, true);
	}
	
	@Action
	public void errDebugStepReturn()
	{
		executeDebugOp(DebugOp.STEP_RETURN, true);
	}

	@Action
	public void errDebugResume()
	{
		executeDebugOp(DebugOp.RESUME, true);
	}
}
