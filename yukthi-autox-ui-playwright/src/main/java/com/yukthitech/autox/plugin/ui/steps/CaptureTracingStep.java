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
package com.yukthitech.autox.plugin.ui.steps;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Tracing;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.exec.ExecutionServices;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.LogLevel;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;

/**
 * Captures Playwright tracing (timeline) for all child steps and adds the trace zip to the log.
 * 
 * @author akiran
 */
@Executable(name = "uiCaptureTracing", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Captures Playwright tracing timeline for all the steps under current step")
public class CaptureTracingStep extends AbstractUiStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Group of steps/validations to be executed as part of this step.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed while tracing is active.")
	private List<IStep> steps = new ArrayList<IStep>();
	
	/**
	 * Name of the trace zip file to be created.
	 */
	@Param(description = "Name of the trace zip file to be created. .zip extension is appended if needed.", sourceType = SourceType.EXPRESSION)
	private Object name;

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStepContainer#addStep(com.yukthitech.autox.IStep)
	 */
	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}
	
	@Override
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	public void setName(Object name)
	{
		this.name = name;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		String nameStr = String.valueOf(name);
		exeLogger.debug("Tracing capture started with name: {}", nameStr);
		
		ReportLogFile traceFile = exeLogger.createFile(nameStr, ".zip");
		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
		BrowserContext browserContext = session.getBrowserContext(driverName);
		
		boolean tracingStarted = false;
		
		try
		{
			browserContext.tracing().start(new Tracing.StartOptions()
					.setScreenshots(true)
					.setSnapshots(true)
					.setSources(true));
			tracingStarted = true;
			
			ExecutionServices.getStepsExecutor().execute(steps, null, null);
		}finally
		{
			if(tracingStarted)
			{
				try
				{
					browserContext.tracing().stop(new Tracing.StopOptions()
							.setPath(traceFile.getFile().toPath()));
					
					exeLogger.logFile("Trace is generated", LogLevel.INFO, traceFile);
				}catch(Exception ex)
				{
					exeLogger.warn("Tracing could not be finalized. Error: {}", "" + ex);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Capture Tracing [");
		builder.append("Name: ").append(name);
		builder.append("]");
		return builder.toString();
	}
}
