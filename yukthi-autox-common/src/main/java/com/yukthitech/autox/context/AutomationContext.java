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
package com.yukthitech.autox.context;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutoxCliArguments;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.debug.IDebugFlowManager;
import com.yukthitech.autox.event.EventManager;
import com.yukthitech.autox.event.IAutomationEventManager;
import com.yukthitech.autox.exec.IExecutionPool;
import com.yukthitech.autox.exec.IReportDataManager;
import com.yukthitech.autox.exec.IStepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.Log4jExecutionLogger;
import com.yukthitech.autox.logmon.LogMonitorManager;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.IPluginSession;
import com.yukthitech.autox.plugin.PluginManager;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.autox.storage.PersistenceStorage;
import com.yukthitech.autox.test.ExecutionSuite;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Automation Context information. 
 * @author akiran
 */
public class AutomationContext
{
	private static Logger logger = LogManager.getLogger(AutomationContext.class);
	
	/**
	 * Automation context that can be accessed anywhere needed.
	 */
	private static AutomationContext instance;
	
	/**
	 * Basic arguments specified from command line context.
	 */
	private AutoxCliArguments basicArguments;
	
	/**
	 * Application configuration.
	 */
	private ApplicationConfiguration appConfiguration;
	
	/**
	 * Work directory to be used for this context.
	 */
	private File workDirectory;
	
	/**
	 * Name to step group mapping.
	 */
	private Map<String, Function> nameToFunction = new HashMap<>();
	
	/**
	 * Name to execution suite mapping.
	 */
	private Map<String, ExecutionSuite> nameToExecutionSuite = new HashMap<>();

	/**
	 * Persistence storage to persist data across the executions.
	 */
	private PersistenceStorage persistenceStorage;
	
	/**
	 * Maintains list of extended command line argument which will be used
	 * during plugin initialization.
	 */
	private String extendedCommandLineArgs[];
	
	/**
	 * Report folder where reports should be generated.
	 */
	private File reportFolder;
	
	/**
	 * The test suite parser handler.
	 */
	private DefaultParserHandler testSuiteParserHandler;
	
	/**
	 * Test suite group being executed.
	 */
	private TestSuiteGroup testSuiteGroup = new TestSuiteGroup();
	
	/**
	 * Executable group names.
	 */
	private Set<String> executableGroups;
	
	/**
	 * Execution suite being executed, if any.
	 */
	private ExecutionSuite activeExecutionSuite;
	
	/**
	 * Steps executor for this context/session.
	 */
	private IStepsExecutor stepsExecutor;
	
	/**
	 * Debug flow manager for this context/session.
	 */
	private IDebugFlowManager debugFlowManager;
	
	/**
	 * Execution context manager for this context/session.
	 */
	private final ExecutionContextManager executionContextManager = new ExecutionContextManager();
	
	/**
	 * Plugin manager for this context/session.
	 */
	private final PluginManager pluginManager = new PluginManager();
	
	/**
	 * Report data manager for this context/session (set by session start).
	 */
	private IReportDataManager reportDataManager;
	
	/**
	 * Execution pool for this context/session (set by session start).
	 */
	private IExecutionPool executionPool;
	
	/**
	 * Plugin event manager for this context/session.
	 */
	private final EventManager eventManager;
	
	/**
	 * Log monitor manager for this context/session.
	 */
	private final LogMonitorManager logMonitorManager;
	
	/**
	 * Automation lifecycle event manager (set by session start).
	 */
	private IAutomationEventManager automationEventManager;
	
	/**
	 * Dynamically registered session services (e.g. mock servers), keyed by unique name.
	 */
	private final Map<String, IDynAutomationService> dynAutomationServices = new HashMap<>();
	
	/**
	 * Prefix expression factory for this session (customs are session-scoped; Java parsers are process-level).
	 */
	private final PrefixExpressionFactory prefixExpressionFactory = new PrefixExpressionFactory();
	
	/**
	 * Object currently being loaded (e.g. test-case-data map during dynamic data provider evaluation).
	 * Accessible via prop: _this.* expressions and FreeMarker ${ _this.* } in templates.
	 */
	private Object _this;
	
	/**
	 * Constructor.
	 * @param appConfiguration Application configuration
	 */
	public AutomationContext(ApplicationConfiguration appConfiguration)
	{
		this.appConfiguration = appConfiguration;
		this.eventManager = new EventManager(appConfiguration);
		this.logMonitorManager = new LogMonitorManager(appConfiguration.getLogMonitors());
		
		this.workDirectory = new File(appConfiguration.getWorkDirectory());
		
		try
		{
			if(workDirectory.exists())
			{
				AutomationUtils.deleteFolder(workDirectory);
			}
			
			FileUtils.forceMkdir(workDirectory);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while cleaning up work directory", ex);
		}
		
		AutomationContext.instance = this;
		
		this.persistenceStorage = new PersistenceStorage(appConfiguration);
	}
	
	public TestSuiteGroup getTestSuiteGroup()
	{
		return testSuiteGroup;
	}

	/**
	 * Resets all session-scoped managers owned by this context, then clears the global instance
	 * when this context is the current one.
	 */
	public void reset() throws Exception
	{
		try
		{
			executionContextManager.close();
			executionContextManager.reset();
		}catch(Exception ex)
		{
			logger.warn("Error while resetting execution context manager", ex);
		}
		
		try
		{
			if(appConfiguration != null)
			{
				Collection<IPlugin<?, ?>> configured = appConfiguration.getAllPlugins();
				
				if(configured != null)
				{
					for(IPlugin<?, ?> plugin : configured)
					{
						try
						{
							plugin.close();
						}catch(Exception ex)
						{
							logger.warn("Error while closing configured plugin: {}", plugin.getClass().getName(), ex);
						}
					}
				}
			}
		}catch(Exception ex)
		{
			logger.warn("Error while closing configured plugins", ex);
		}
		
		try
		{
			pluginManager.reset();
		}catch(Exception ex)
		{
			logger.warn("Error while resetting plugin manager", ex);
		}
		
		try
		{
			if(reportDataManager != null)
			{
				reportDataManager.reset();
			}
		}catch(Exception ex)
		{
			logger.warn("Error while resetting report data manager", ex);
		}
		
		try
		{
			if(executionPool != null)
			{
				executionPool.reset();
			}
		}catch(Exception ex)
		{
			logger.warn("Error while resetting execution pool", ex);
		}
		
		try
		{
			if(debugFlowManager != null)
			{
				debugFlowManager.reset();
			}
		}catch(Exception ex)
		{
			logger.warn("Error while resetting debug flow manager", ex);
		}
		
		try
		{
			eventManager.reset();
		}catch(Exception ex)
		{
			logger.warn("Error while resetting event manager", ex);
		}
		
		try
		{
			logMonitorManager.reset();
		}catch(Exception ex)
		{
			logger.warn("Error while resetting log monitor manager", ex);
		}
		
		try
		{
			if(automationEventManager != null)
			{
				automationEventManager.reset();
			}
		}catch(Exception ex)
		{
			logger.warn("Error while resetting automation event manager", ex);
		}
		
		try
		{
			prefixExpressionFactory.clearCustomExpressions();
		}catch(Exception ex)
		{
			logger.warn("Error while clearing custom prefix expressions", ex);
		}
		
		closeDynAutomationServices();
		
		try
		{
			if(appConfiguration != null)
			{
				appConfiguration.close();
			}
		}catch(Exception ex)
		{
			logger.warn("Error while closing application configuration", ex);
		}
		
		if(instance == this)
		{
			instance = null;
		}
	}
	
	private void closeDynAutomationServices()
	{
		for(Map.Entry<String, IDynAutomationService> entry : new HashMap<>(dynAutomationServices).entrySet())
		{
			try
			{
				entry.getValue().close();
			}catch(Exception ex)
			{
				logger.warn("Error while closing dynamic automation service: {}", entry.getKey(), ex);
			}
		}
		
		dynAutomationServices.clear();
	}
	
	/**
	 * Registers a dynamic session service under a unique name.
	 */
	public synchronized void registerDynAutomationService(String name, IDynAutomationService service)
	{
		if(StringUtils.isBlank(name))
		{
			throw new InvalidArgumentException("Dynamic automation service name cannot be blank");
		}
		
		if(service == null)
		{
			throw new InvalidArgumentException("Dynamic automation service cannot be null");
		}
		
		if(dynAutomationServices.containsKey(name))
		{
			throw new InvalidStateException("Dynamic automation service already registered with name: {}", name);
		}
		
		dynAutomationServices.put(name, service);
	}
	
	/**
	 * Fetches a dynamic session service by name.
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends IDynAutomationService> T getDynAutomationService(String name)
	{
		return (T) dynAutomationServices.get(name);
	}
	
	/**
	 * Unregisters a dynamic session service by name without closing it.
	 * Callers that stop a service themselves should unregister after close.
	 */
	public synchronized IDynAutomationService unregisterDynAutomationService(String name)
	{
		return dynAutomationServices.remove(name);
	}
	
	/**
	 * Gets the automation context that can be accessed anywhere needed.
	 *
	 * @return the automation context that can be accessed anywhere needed
	 */
	public static AutomationContext getInstance()
	{
		return instance;
	}
	
	/**
	 * Sets the maintains list of extended command line argument which will be used during plugin initialization.
	 *
	 * @param extendedCommandLineArgs the new maintains list of extended command line argument which will be used during plugin initialization
	 */
	public void setExtendedCommandLineArgs(String[] extendedCommandLineArgs)
	{
		this.extendedCommandLineArgs = extendedCommandLineArgs;
	}
	
	public String[] getExtendedCommandLineArgs()
	{
		return extendedCommandLineArgs;
	}
	
	/**
	 * Sets the basic arguments specified from command line context.
	 *
	 * @param basicArguments the new basic arguments specified from command line context
	 */
	public void setBasicArguments(AutoxCliArguments basicArguments)
	{
		this.basicArguments = basicArguments;
	}
	
	/**
	 * Gets the basic arguments specified from command line context.
	 *
	 * @return the basic arguments specified from command line context
	 */
	public AutoxCliArguments getBasicArguments()
	{
		return basicArguments;
	}
	
	public Object get_this()
	{
		return _this;
	}
	
	/**
	 * Sets the object currently being loaded.
	 * @param _this current object being loaded
	 */
	public void set_this(Object _this)
	{
		this._this = _this;
	}
	
	/**
	 * Sets the specified attribute with specified value.
	 * @param name Name of the attribute
	 * @param value Value of the attribute
	 */
	public void setAttribute(String name, Object value)
	{
		executionContextManager.getExecutionContext().setAttribute(name, value);
	}
	
	public void setGlobalAttribute(String name, Object value)
	{
		executionContextManager.setGlobalAttribute(name, value);
	}
	
	public Object getGlobalAttribute(String name)
	{
		return executionContextManager.getGlobalAttribute(name);
	}
	
	/**
	 * Sets internal context attribute which will be accessible within the java code.
	 * @param name name of the attribute
	 * @param value value to set
	 */
	public void setInternalAttribute(String name, Object value)
	{
		executionContextManager.getExecutionContext().setInternalAttribute(name, value);
	}
	
	/**
	 * Fetches the attribute value with specified name.
	 * @param name Name of attribute to fetch
	 * @return Attribute value
	 */
	public Object getAttribute(String name)
	{
		return executionContextManager.getExecutionContext().getAttribute(name);
	}
	
	/**
	 * Fetches internal attribute value with specified name.
	 * @param name Name of attribute to fetch
	 * @return Attribute value
	 */
	public Object getInternalAttribute(String name)
	{
		return executionContextManager.getExecutionContext().getInternalAttribute(name);
	}
	
	/**
	 * Removes attribute with specified name.
	 * @param name Name of the attribute to remove.
	 * @return Current attribute value.
	 */
	public Object removeAttribute(String name)
	{
		return executionContextManager.getExecutionContext().removeAttribute(name);
	}
	
	/**
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttributeMap()
	{
		return executionContextManager.getExecutionContext().getAttr();
	}
	
	/**
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttr()
	{
		return executionContextManager.getExecutionContext().getAttr();
	}
	
	/**
	 * Fetches the currently active plugin attribute map.
	 * If no plugin is active, this method will throw exception.
	 * 
	 * @return
	 */
	public Map<String, Object> getPluginAttr(String name)
	{
		return executionContextManager.getExecutionContext().getPluginAttr(name);
	}

	/**
	 * Used to push the parameters on stack of the function going to be executed on. Expected to be
	 * called only before fucntion execution by framework itself.
	 * @param parameters parameters to be pushed
	 */
	public void pushParameters(Map<String, Object> parameters)
	{
		executionContextManager.getExecutionContextStack().pushParameters(parameters);
	}
	
	/**
	 * Pops the parameters from the function stack.
	 */
	public void popParameters()
	{
		executionContextManager.getExecutionContextStack().popParameters();
	}
	
	public boolean isParamPresent()
	{
		return executionContextManager.getExecutionContextStack().isParamPresent();
	}
	
	public Map<String, Object> getParam()
	{
		return executionContextManager.getExecutionContextStack().getParam();
	}
	
	public Object getParameter(String name)
	{
		return executionContextManager.getExecutionContextStack().getParameter(name);
	}
	
	/**
	 * Gets the application configuration.
	 *
	 * @return the application configuration
	 */
	public ApplicationConfiguration getAppConfiguration()
	{
		return appConfiguration;
	}
	
	/**
	 * For ease of access of app properties using context.
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> getProp()
	{
		return (Map) appConfiguration.getApplicationProperties();
	}
	
	public ReportLogFile newLogFile(String name, String extension)
	{
		if(!extension.startsWith("."))
		{
			extension = "." + extension;
		}
		
		if(name.toLowerCase().endsWith(extension.toLowerCase()))
		{
			name = name.substring(0, name.length() - extension.length());
		}
		
		File logFolder = new File(reportFolder, IAutomationConstants.LOGS_FOLDER_NAME);
		String namePrefix = name + "_" + Long.toHexString(System.currentTimeMillis()).toLowerCase();
		File file = new File(logFolder, name + extension);
		int index = 1;
		
		while(file.exists())
		{
			file = new File(logFolder, namePrefix + "_" + index + extension);
			index++;
		}
		
		return new ReportLogFile(file);
	}
	
	public <P, S extends IPluginSession> S newPluginSession(Class<? extends IPlugin<?, S>> pluginType)
	{
		return executionContextManager.getPluginSession(pluginType);
	}

	/**
	 * Starts all registered log monitors.
	 */
	public void startLogMonitoring()
	{
		executionContextManager.getExecutionContext().startLogMonitoring();
	}
	
	/**
	 * Stops the log monitors and collects the log files generated.
	 * @return Collected log files
	 */
	public Map<String, ReportLogFile> stopLogMonitoring(boolean flowErrored)
	{
		return executionContextManager.getExecutionContext().stopMonitoring(flowErrored);
	}
	
	/**
	 * Fetches the data bean map.
	 * @return data bean map.
	 */
	public Map<String, Object> getDataBeans()
	{
		return appConfiguration.getDataBeans();
	}
	
	/**
	 * Ease method to access data beans.
	 * @return
	 */
	public Map<String, Object> getData()
	{
		return appConfiguration.getDataBeans();
	}

	/**
	 * Gets the work directory to be used for this context.
	 *
	 * @return the work directory to be used for this context
	 */
	public File getWorkDirectory()
	{
		return workDirectory;
	}
	
	/**
	 * Adds specified test group.
	 * @param function group to add.
	 */
	public void addFunction(Function function)
	{
		if(StringUtils.isEmpty(function.getName()))
		{
			throw new InvalidArgumentException("Function can not be added without name");
		}
		
		if(nameToFunction.containsKey(function.getName()))
		{
			Function existing = nameToFunction.get(function.getName());
			
			throw new InvalidConfigurationException("Duplicate global-function encountered with name '{}'. [Location1: {}:{}, Location2: {}:{}]", 
					function.getName(), 
					existing.getLocation(), existing.getLineNumber(), 
					function.getLocation(), function.getLineNumber());
		}
		
		nameToFunction.put(function.getName(), function);
	}
	
	public synchronized void addOrReplaceFunction(Function function)
	{
		if(StringUtils.isEmpty(function.getName()))
		{
			throw new InvalidArgumentException("Function can not be added without name");
		}
		
		nameToFunction.put(function.getName(), function);
	}
	
	public void addExecutionSuite(ExecutionSuite executionSuite)
	{
		if(nameToExecutionSuite.containsKey(executionSuite.getName()))
		{
			ExecutionSuite existing = nameToExecutionSuite.get(executionSuite.getName());
			
			throw new InvalidConfigurationException("Duplicate execution suite encountered with name '{}'. [Location1: {}:{}, Location2: {}:{}]", 
					executionSuite.getName(), 
					existing.getLocation(), existing.getLineNumber(), 
					executionSuite.getLocation(), executionSuite.getLineNumber());
		}
		
		nameToExecutionSuite.put(executionSuite.getName(), executionSuite);
	}
	
	public ExecutionSuite getExecutionSuite(String name)
	{
		return nameToExecutionSuite.get(name);
	}

	/**
	 * Clears step groups.
	 */
	public void clearFunctions()
	{
		nameToFunction.clear();
	}
	
	/**
	 * Fetches the step group with specified name.
	 * @param name name of step group.
	 * @return matching group
	 */
	public Function getFunction(String name)
	{
		//check if current test suite has the group, if present give that higher preference
		Function function = executionContextManager.getExecutionContext().getFunction(name);
		
		if(function != null)
		{
			return function;
		}
		
		return nameToFunction.get(name);
	}

	public Function getGlobalFunction(String name)
	{
		return nameToFunction.get(name);
	}

	/**
	 * Gets the persistence storage to persist data across the executions.
	 *
	 * @return the persistence storage to persist data across the executions
	 */
	public PersistenceStorage getPersistenceStorage()
	{
		return persistenceStorage;
	}

	/**
	 * Gets the report folder where reports should be generated.
	 *
	 * @return the report folder where reports should be generated
	 */
	public File getReportFolder()
	{
		return reportFolder;
	}

	/**
	 * Sets the report folder where reports should be generated.
	 *
	 * @param reportFolder the new report folder where reports should be generated
	 */
	public void setReportFolder(File reportFolder)
	{
		this.reportFolder = reportFolder;
	}
	
	/**
	 * Gets the current execution logger. Can be null.
	 *
	 * @return the current execution logger
	 */
	public IExecutionLogger getExecutionLogger()
	{
		try
		{
			return executionContextManager.getExecutionContext().getExecutionLogger();
		}catch(NonExecutionThreadException ex)
		{
			return Log4jExecutionLogger.getInstance();
		}
	}
	
	public ExecutionContextManager getExecutionContextManager()
	{
		return executionContextManager;
	}
	
	public PluginManager getPluginManager()
	{
		return pluginManager;
	}
	
	public IReportDataManager getReportDataManager()
	{
		if(reportDataManager == null)
		{
			throw new InvalidStateException("Report data manager is not registered on automation context");
		}
		
		return reportDataManager;
	}
	
	public void setReportDataManager(IReportDataManager reportDataManager)
	{
		this.reportDataManager = reportDataManager;
	}
	
	public IExecutionPool getExecutionPool()
	{
		if(executionPool == null)
		{
			throw new InvalidStateException("Execution pool is not registered on automation context");
		}
		
		return executionPool;
	}
	
	public void setExecutionPool(IExecutionPool executionPool)
	{
		this.executionPool = executionPool;
	}
	
	public IStepsExecutor getStepsExecutor()
	{
		if(stepsExecutor == null)
		{
			throw new InvalidStateException("Steps executor is not registered on automation context");
		}
		
		return stepsExecutor;
	}
	
	public void setStepsExecutor(IStepsExecutor stepsExecutor)
	{
		this.stepsExecutor = stepsExecutor;
	}
	
	public IDebugFlowManager getDebugFlowManager()
	{
		if(debugFlowManager == null)
		{
			throw new InvalidStateException("Debug flow manager is not registered on automation context");
		}
		
		return debugFlowManager;
	}
	
	public void setDebugFlowManager(IDebugFlowManager debugFlowManager)
	{
		this.debugFlowManager = debugFlowManager;
	}
	
	public EventManager getEventManager()
	{
		return eventManager;
	}
	
	public LogMonitorManager getLogMonitorManager()
	{
		return logMonitorManager;
	}
	
	public IAutomationEventManager getAutomationEventManager()
	{
		if(automationEventManager == null)
		{
			throw new InvalidStateException("Automation event manager is not registered on automation context");
		}
		
		return automationEventManager;
	}
	
	public void setAutomationEventManager(IAutomationEventManager automationEventManager)
	{
		this.automationEventManager = automationEventManager;
	}
	
	public PrefixExpressionFactory getPrefixExpressionFactory()
	{
		return prefixExpressionFactory;
	}

	/**
	 * Gets the test suite parser handler.
	 *
	 * @return the test suite parser handler
	 */
	public DefaultParserHandler getTestSuiteParserHandler()
	{
		return testSuiteParserHandler;
	}

	/**
	 * Sets the test suite parser handler.
	 *
	 * @param testSuiteParserHandler the new test suite parser handler
	 */
	public void setTestSuiteParserHandler(DefaultParserHandler testSuiteParserHandler)
	{
		this.testSuiteParserHandler = testSuiteParserHandler;
	}
	
	public synchronized Set<String> getExecutableGroups()
	{
		if(executableGroups != null)
		{
			return executableGroups;
		}
		
		String propVal = getOverridableProp(IAutomationConstants.AUTOX_PROP_EXECUTABLE_GROUPS);
		
		if(StringUtils.isBlank(propVal))
		{
			executableGroups = Collections.emptySet();
			return executableGroups;
		}
		
		executableGroups = new HashSet<>(Arrays.asList(propVal.trim().split("\\s*\\,\\s*")));
		logger.debug("Limiting test case execution for group: {}", executableGroups);
		return executableGroups;
	}

	public String getOverridableProp(String name)
	{
		String val = System.getProperty(name);
		
		if(val != null)
		{
			return val;
		}
		
		return appConfiguration.getApplicationProperties().get(name);
	}

	public ExecutionSuite getActiveExecutionSuite()
	{
		return activeExecutionSuite;
	}

	public void setActiveExecutionSuite(ExecutionSuite activeExecutionSuite)
	{
		this.activeExecutionSuite = activeExecutionSuite;
	}
}