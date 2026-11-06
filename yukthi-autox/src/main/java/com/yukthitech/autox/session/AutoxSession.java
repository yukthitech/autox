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
import java.util.Arrays;
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
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.interactive.InteractiveXmlSupport;
import com.yukthitech.autox.interactive.StepHolder;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.IPluginSession;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.FunctionParam;
import com.yukthitech.autox.test.FunctionRef;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Owns {@link AutomationContext} and plugin lifecycle for a single automation run.
 * <p>
 * <b>Normal / CLI</b> ({@link #start}): {@link DefaultContextWrapper} — suite executors use the
 * plugin session pool. {@link #executeSteps}, {@link #runFunction}, and {@link #getPluginSession}
 * are not available.
 * </p>
 * <p>
 * <b>Interactive embed</b> ({@link #startInteractive}): {@link InteractiveContextWrapper} keeps a
 * live executor on one worker thread so the same plugin/browser session stays checked out until
 * {@link #close()} (debug-style). Host threads may call interactive APIs; work is marshaled onto
 * that worker. Use {@link IPluginSessionHandle#execute} for Playwright capture/ops.
 * </p>
 * <p>
 * <b>Single active session per classloader:</b> {@link AutomationContext} is a process-level
 * singleton ({@link AutomationContext#getInstance()}). Only one session should be open at a
 * time. Call {@link #close()} before starting another.
 * </p>
 * <pre>
 * try (AutoxSession session = AutoxSession.startInteractive(ContentStream.fromFile(appConfigFile), args))
 * {
 *     session.loadXml(ContentStream.fromString(suiteXml, "host:checkout-flow.xml"));
 *     session.runFunction("login");
 *     String mhtml = session.getPluginSession(PlaywrightPlugin.class)
 *             .execute(ui -&gt; ui.captureHtmlSnapshot());
 * }
 * </pre>
 * For breakpoints / pause / step / paused {@code eval}, use {@link AutoxDebugSession}.
 *
 * @author akranthikiran
 */
public class AutoxSession implements AutoCloseable
{
	private static final Logger logger = LogManager.getLogger(AutoxSession.class);
	
	private static final String FUNCTION_RETURN_ATTR = "autoxSession.functionReturn";
	
	final IContextWrapper contextWrapper;
	
	private boolean closed;
	
	private boolean testSuiteFilesLoaded;
	
	protected AutoxSession(IContextWrapper contextWrapper)
	{
		this.contextWrapper = contextWrapper;
	}
	
	/**
	 * Starts an interactive session: dedicated worker, live executor, same browser across calls.
	 * Supports {@link #executeSteps}, {@link #runFunction}, and {@link #getPluginSession}.
	 */
	public static AutoxSession startInteractive(ContentStream appConfiguration, String[] extendedCommandLineArgs) throws Exception
	{
		return startInteractive(appConfiguration, extendedCommandLineArgs, new AutomationEventManager());
	}
	
	/**
	 * Starts an interactive session with a custom event manager.
	 */
	public static AutoxSession startInteractive(ContentStream appConfiguration, String[] extendedCommandLineArgs,
			AutomationEventManager automationEventManager) throws Exception
	{
		return new AutoxSession(InteractiveContextWrapper.create(appConfiguration, extendedCommandLineArgs, automationEventManager));
	}
	
	/**
	 * Starts a normal / CLI session (plugin pool; no interactive APIs).
	 */
	public static AutoxSession start(ContentStream appConfiguration, String[] extendedCommandLineArgs,
			AutomationEventManager automationEventManager) throws Exception
	{
		return new AutoxSession(new DefaultContextWrapper(
				createContext(appConfiguration, extendedCommandLineArgs, automationEventManager)));
	}
	
	/**
	 * Creates and wires a new {@link AutomationContext} for a session.
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
	
	void ensureInteractive()
	{
		if(!contextWrapper.isInteractive())
		{
			throw new InvalidStateException(
					"This operation is only available on an interactive AutoxSession (use startInteractive)");
		}
	}
	
	public AutomationContext getAutomationContext()
	{
		ensureOpen();
		return contextWrapper.execute(ctx -> ctx);
	}
	
	/**
	 * Returns a handle that runs plugin-session work on the interactive live-executor thread.
	 * Interactive sessions only.
	 *
	 * @param pluginType plugin class (e.g. PlaywrightPlugin.class)
	 */
	public <S extends IPluginSession> IPluginSessionHandle<S> getPluginSession(Class<? extends IPlugin<?, S>> pluginType)
	{
		ensureOpen();
		ensureInteractive();
		
		if(pluginType == null)
		{
			throw new InvalidArgumentException("Plugin type cannot be null");
		}
		
		return new PluginSessionHandle<>(this, pluginType);
	}
	
	public boolean isClosed()
	{
		return closed;
	}
	
	public boolean isInteractive()
	{
		return contextWrapper.isInteractive();
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
		
		contextWrapper.execute(ctx ->
		{
			AutomationFileLoader.loadTestSuites(ctx, true);
			return null;
		});
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
		
		Set<String> errors = contextWrapper.execute(ctx -> AutomationFileLoader.loadTestXml(content, true));
		throwIfLoadErrors(content.getSourceName(), errors);
	}
	
	/**
	 * Parses and validates dynamic step XML without executing it.
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
		
		contextWrapper.execute(ctx ->
		{
			InteractiveXmlSupport.parseSteps(ctx, stepXml);
			return null;
		});
	}
	
	/**
	 * Parses and executes dynamic step XML under the live interactive executor.
	 * Interactive sessions only.
	 */
	public void executeSteps(String stepXml)
	{
		ensureOpen();
		ensureInteractive();
		
		if(StringUtils.isBlank(stepXml))
		{
			throw new InvalidArgumentException("Steps XML cannot be empty");
		}
		
		contextWrapper.execute(ctx ->
		{
			File dynamicSource = InteractiveXmlSupport.nextDynamicSourceFile();
			StepHolder stepHolder = InteractiveXmlSupport.parseSteps(ctx, stepXml);
			stepHolder.updateLocations(dynamicSource, 1);
			boolean reloadingDone = InteractiveXmlSupport.applyReloads(ctx, stepHolder, null);
			
			if(CollectionUtils.isEmpty(stepHolder.getSteps()))
			{
				if(reloadingDone)
				{
					return null;
				}
				
				throw new InvalidArgumentException("No steps found to execute");
			}
			
			try
			{
				ctx.getStepsExecutor().execute(stepHolder.getSteps(), null, null);
			}catch(RuntimeException ex)
			{
				throw ex;
			}catch(Exception ex)
			{
				throw new InvalidStateException("executeSteps failed: {}", ex.getMessage(), ex);
			}
			
			return null;
		});
	}
	
	/**
	 * Runs a loaded global function with no parameters. Interactive sessions only.
	 */
	public Object runFunction(String name)
	{
		return runFunction(name, null);
	}
	
	/**
	 * Runs a loaded global function under the live interactive executor (same browser across calls).
	 * Interactive sessions only.
	 */
	public Object runFunction(String name, Map<String, Object> params)
	{
		ensureOpen();
		ensureInteractive();
		
		return contextWrapper.execute(ctx ->
		{
			Function function = ctx.getGlobalFunction(name);
			
			if(function == null)
			{
				throw new InvalidArgumentException("No function found with specified name: {}", name);
			}
			
			FunctionRef funcRef = new FunctionRef(function.getName(), FUNCTION_RETURN_ATTR);
			funcRef.setLocation(function.getLocation(), function.getLineNumber());
			
			if(params != null)
			{
				for(Map.Entry<String, Object> entry : params.entrySet())
				{
					funcRef.addParam(new FunctionParam(entry.getKey(), entry.getValue()));
				}
			}
			
			try
			{
				ctx.getStepsExecutor().execute(Arrays.asList(funcRef), null, null);
			}catch(RuntimeException ex)
			{
				throw ex;
			}catch(Exception ex)
			{
				throw new InvalidStateException("Function '{}' failed: {}", name, ex.getMessage(), ex);
			}
			
			Object result = ctx.getAttribute(FUNCTION_RETURN_ATTR);
			ctx.removeAttribute(FUNCTION_RETURN_ATTR);
			return result;
		});
	}
	
	/**
	 * Closes session-owned resources. Idempotent. Does not call {@code System.exit}.
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
			contextWrapper.close();
		}catch(Exception ex)
		{
			logger.warn("Error while closing context wrapper", ex);
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
