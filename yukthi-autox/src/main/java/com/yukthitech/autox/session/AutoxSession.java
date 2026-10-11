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
package com.yukthitech.autox.session;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.event.AutomationEventManager;
import com.yukthitech.autox.exec.ExecutionPool;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.ReportDataManager;

/**
 * Owns {@link AutomationContext} and plugin lifecycle for a single automation run.
 * Plugin sessions may return to the plugin cache when executors finish; browsers and
 * plugins are closed only via {@link #close()}.
 * <p>
 * Callers that create a session are responsible for holding and closing that instance.
 * Phase 1 assumes session work is driven on a single execution thread (CLI main path).
 * Sequential use from different threads is deferred.
 * </p>
 *
 * @author akranthikiran
 */
public class AutoxSession implements AutoCloseable
{
	private static final Logger logger = LogManager.getLogger(AutoxSession.class);
	
	private final AutomationContext context;
	
	private boolean closed;
	
	private AutoxSession(AutomationContext context)
	{
		this.context = context;
	}
	
	/**
	 * Starts a new session for the given app configuration.
	 *
	 * @param appConfigurationFile application configuration file
	 * @param extendedCommandLineArgs extended CLI args (may be empty, not null)
	 * @return the new session (caller must {@link #close()} it)
	 */
	public static AutoxSession start(File appConfigurationFile, String[] extendedCommandLineArgs) throws Exception
	{
		return start(appConfigurationFile, extendedCommandLineArgs, new AutomationEventManager());
	}
	
	/**
	 * Starts a new session, attaching the given automation event manager.
	 */
	public static AutoxSession start(File appConfigurationFile, String[] extendedCommandLineArgs, AutomationEventManager automationEventManager) throws Exception
	{
		if(extendedCommandLineArgs == null)
		{
			extendedCommandLineArgs = new String[0];
		}
		
		AutomationContext context = AutomationLauncher.loadAutomationContext(appConfigurationFile, extendedCommandLineArgs, automationEventManager);
		context.setAutomationEventManager(automationEventManager);
		context.setStepsExecutor(new StepsExecutor());
		context.setDebugFlowManager(new DebugFlowManager());
		context.setReportDataManager(new ReportDataManager());
		context.setExecutionPool(new ExecutionPool());
		return new AutoxSession(context);
	}
	
	public AutomationContext getAutomationContext()
	{
		return context;
	}
	
	public boolean isClosed()
	{
		return closed;
	}
	
	/**
	 * Closes session-owned resources via {@link AutomationContext#reset()}. Idempotent.
	 * Does not call {@code System.exit}.
	 */
	@Override
	public synchronized void close()
	{
		if(closed)
		{
			return;
		}
		
		closed = true;
		
		try
		{
			context.reset();
		}catch(Exception ex)
		{
			logger.warn("Error while resetting automation context", ex);
		}
	}
}
