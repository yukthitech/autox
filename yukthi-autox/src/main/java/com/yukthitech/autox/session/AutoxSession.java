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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationFileLoader;
import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.common.ContentStream;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.event.AutomationEventManager;
import com.yukthitech.autox.exec.ExecutionPool;
import com.yukthitech.autox.exec.FunctionExecutor;
import com.yukthitech.autox.exec.InteractiveStepsExecutor;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.interactive.InteractiveXmlSupport;
import com.yukthitech.autox.interactive.StepHolder;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.IPluginSession;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Owns {@link AutomationContext} and plugin lifecycle for a single automation run.
 * Plugin sessions may return to the plugin cache when executors finish; browsers and
 * plugins are closed only via {@link #close()}.
 * <p>
 * <b>Single active session per classloader:</b> {@link AutomationContext} is a process-level
 * singleton ({@link AutomationContext#getInstance()}). Only one session should be open at a
 * time. Call {@link #close()} before {@link #start} again. {@link #start} fails if a context
 * is already active.
 * </p>
 * <p>
 * Typical embed usage (Mode 1 — load suites / XML, run multiple functions, same plugins/browser):
 * <pre>
 * try (AutoxSession session = AutoxSession.start(ContentStream.fromFile(appConfigFile), args))
 * {
 *     session.loadXml(ContentStream.fromString(suiteXml, "host:checkout-flow.xml"));
 *     session.runFunction("login");
 *     Object result = session.runFunction("checkout", params);
 * }
 * </pre>
 * Mode 2 — dynamic steps (no source name; locations use generated {@code interactive-steps-N.xml}):
 * <pre>
 * session.executeSteps("&lt;s:set expression=\"x\" value=\"int: 1\"/&gt;");
 * </pre>
 * For breakpoints / pause / step / paused {@code eval}, use {@link AutoxDebugSession}.
 * </p>
 * <p>
 * Session work is driven on a single execution thread (CLI main path).
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
	
	private boolean testSuiteFilesLoaded;
	
	protected AutoxSession(AutomationContext context)
	{
		this.context = context;
	}
	
	/**
	 * Starts a new session for the given app configuration content (file, string, or stream).
	 *
	 * @param appConfiguration application configuration content
	 * @param extendedCommandLineArgs extended CLI args (may be empty, not null)
	 * @return the new session (caller must {@link #close()} it)
	 */
	public static AutoxSession start(ContentStream appConfiguration, String[] extendedCommandLineArgs) throws Exception
	{
		return start(appConfiguration, extendedCommandLineArgs, new AutomationEventManager());
	}
	
	/**
	 * Starts a new session, attaching the given automation event manager.
	 */
	public static AutoxSession start(ContentStream appConfiguration, String[] extendedCommandLineArgs, AutomationEventManager automationEventManager) throws Exception
	{
		return new AutoxSession(createContext(appConfiguration, extendedCommandLineArgs, automationEventManager));
	}
	
	/**
	 * Creates and wires a new {@link AutomationContext} for a session. Used by
	 * {@link #start} and {@link AutoxDebugSession}.
	 */
	protected static AutomationContext createContext(ContentStream appConfiguration, String[] extendedCommandLineArgs,
			AutomationEventManager automationEventManager) throws Exception
	{
		if(AutomationContext.getInstance() != null)
		{
			throw new InvalidStateException(
					"An AutomationContext is already active. Close the current AutoxSession before starting another "
					+ "(one active session per classloader).");
		}
		
		if(appConfiguration == null)
		{
			throw new InvalidArgumentException("Application configuration content cannot be null");
		}
		
		if(extendedCommandLineArgs == null)
		{
			extendedCommandLineArgs = new String[0];
		}
		
		if(automationEventManager == null)
		{
			automationEventManager = new AutomationEventManager();
		}
		
		AutomationContext context = AutomationLauncher.loadAutomationContext(appConfiguration, extendedCommandLineArgs, automationEventManager);
		context.setAutomationEventManager(automationEventManager);
		context.setStepsExecutor(new StepsExecutor());
		context.setDebugFlowManager(new DebugFlowManager());
		context.setReportDataManager(new ReportDataManager());
		context.setExecutionPool(new ExecutionPool());
		return context;
	}
	
	protected void ensureOpen()
	{
		if(closed)
		{
			throw new InvalidStateException("AutoxSession is already closed");
		}
	}
	
	public AutomationContext getAutomationContext()
	{
		return context;
	}
	
	/**
	 * Returns the live plugin session for the given plugin type (same path steps use).
	 * Must be called from an execution thread (e.g. during {@link #runFunction} /
	 * {@link #executeSteps}, or another executor context).
	 *
	 * @param pluginType plugin class (e.g. PlaywrightPlugin.class)
	 * @return active plugin session for the current execution thread
	 */
	public <S extends IPluginSession> S getPluginSession(Class<? extends IPlugin<?, S>> pluginType)
	{
		ensureOpen();
		return context.getExecutionContextManager().getPluginSession(pluginType);
	}
	
	public boolean isClosed()
	{
		return closed;
	}
	
	/**
	 * Loads test suites (and global functions) from suite files under folders configured in the
	 * application configuration / CLI args. Idempotent for a given session.
	 */
	public void loadTestSuiteFiles()
	{
		ensureOpen();
		
		if(testSuiteFilesLoaded)
		{
			return;
		}
		
		AutomationFileLoader.loadTestSuites(context, true);
		testSuiteFilesLoaded = true;
	}
	
	/**
	 * Loads suite/function XML from a {@link ContentStream}. The stream's source name is used
	 * for location attribution (logs / debugging of loaded functions).
	 */
	public void loadXml(ContentStream content)
	{
		ensureOpen();
		
		if(content == null)
		{
			throw new InvalidArgumentException("Content stream cannot be null");
		}
		
		Set<String> errors = AutomationFileLoader.loadTestXml(content, true);
		throwIfLoadErrors(content.getSourceName(), errors);
	}
	
	/**
	 * Parses and validates dynamic step XML without executing it. Checks step and
	 * attribute names, required parameters, and related structure. Does not run steps,
	 * and does not register functions, prefix expressions, or UI locators from the snippet.
	 *
	 * @param stepXml step XML snippet (same form as {@link #executeSteps})
	 */
	public void compileSteps(String stepXml)
	{
		ensureOpen();
		
		if(StringUtils.isBlank(stepXml))
		{
			throw new InvalidArgumentException("Steps XML cannot be empty");
		}
		
		InteractiveXmlSupport.parseSteps(context, stepXml);
	}
	
	/**
	 * Parses and executes dynamic step XML (Mode 2). Locations use a generated
	 * {@code interactive-steps-N.xml} name (no caller source name). Also applies any
	 * functions / custom prefix expressions / UI locators present in the snippet.
	 */
	public void executeSteps(String stepXml)
	{
		ensureOpen();
		
		if(StringUtils.isBlank(stepXml))
		{
			throw new InvalidArgumentException("Steps XML cannot be empty");
		}
		
		File dynamicSource = InteractiveXmlSupport.nextDynamicSourceFile();
		StepHolder stepHolder = InteractiveXmlSupport.parseSteps(context, stepXml);
		// Align locations to one dynamic source name (parse already used a generated name)
		stepHolder.updateLocations(dynamicSource, 1);
		boolean reloadingDone = InteractiveXmlSupport.applyReloads(context, stepHolder, null);
		
		if(CollectionUtils.isEmpty(stepHolder.getSteps()))
		{
			if(reloadingDone)
			{
				return;
			}
			
			throw new InvalidArgumentException("No steps found to execute");
		}
		
		InteractiveStepsExecutor executor = new InteractiveStepsExecutor(stepHolder.getSteps());
		executor.executeSteps();
		
		TestStatus status = executor.getStatus();
		
		if(status != null && status.isErrored())
		{
			throw new InvalidStateException("executeSteps ended with status {}: {}",
					status, executor.getStatusMessage());
		}
	}
	
	/**
	 * Runs a loaded global function with no parameters.
	 *
	 * @param name global function name
	 * @return function return value, or {@code null} if the function does not return
	 */
	public Object runFunction(String name)
	{
		return runFunction(name, null);
	}
	
	/**
	 * Runs a loaded global function. Multiple calls without {@link #close()} reuse the same
	 * session-owned plugins/browsers (Phase 1 ownership). Per-invocation execution attributes
	 * are not automatically shared across calls; use {@link AutomationContext#setGlobalAttribute}
	 * when cross-function attribute sharing is required.
	 *
	 * @param name global function name
	 * @param params function parameters (may be {@code null})
	 * @return function return value, or {@code null} if the function does not return
	 */
	public Object runFunction(String name, Map<String, Object> params)
	{
		ensureOpen();
		
		Function function = context.getGlobalFunction(name);
		
		if(function == null)
		{
			throw new InvalidArgumentException("No function found with specified name: {}", name);
		}
		
		FunctionExecutor executor = new FunctionExecutor(function, params);
		Object result = executor.executeFunction();
		
		TestStatus status = executor.getStatus();
		
		if(status != null && status.isErrored())
		{
			throw new InvalidStateException("Function '{}' ended with status {}: {}",
					name, status, executor.getStatusMessage());
		}
		
		return result;
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
	
	private static void throwIfLoadErrors(String sourceName, Set<String> errors)
	{
		if(errors == null || errors.isEmpty())
		{
			return;
		}
		
		String errorStr = errors.stream().collect(Collectors.joining("\n"));
		throw new InvalidStateException("Failed to load XML from source '{}':\n{}", sourceName, errorStr);
	}
}
