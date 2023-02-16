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
import com.yukthitech.autox.TestSuiteParserHandler;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.Log4jExecutionLogger;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.IPluginSession;
import com.yukthitech.autox.storage.PersistenceStorage;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
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
	private TestSuiteParserHandler testSuiteParserHandler;
	
	/**
	 * Test suite group being executed.
	 */
	private TestSuiteGroup testSuiteGroup;
	
	/**
	 * Executable group names.
	 */
	private Set<String> executableGroups;
	
	/**
	 * Constructor.
	 * @param appConfiguration Application configuration
	 */
	public AutomationContext(ApplicationConfiguration appConfiguration)
	{
		this.appConfiguration = appConfiguration;
		
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

	public void setTestSuiteGroup(TestSuiteGroup testSuiteGroup)
	{
		this.testSuiteGroup = testSuiteGroup;
	}

	public static void reset() throws Exception
	{
		if(instance != null)
		{
			ApplicationConfiguration.reset();
		}
		
		instance = null;
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
	
	/**
	 * Sets the specified attribute with specified value.
	 * @param name Name of the attribute
	 * @param value Value of the attribute
	 */
	public void setAttribute(String name, Object value)
	{
		ExecutionContextManager.getExecutionContext().setAttribute(name, value);
	}
	
	public void setGlobalAttribute(String name, Object value)
	{
		ExecutionContextManager.getInstance().setGlobalAttribute(name, value);
	}
	
	public Object getGlobalAttribute(String name)
	{
		return ExecutionContextManager.getInstance().getGlobalAttribute(name);
	}
	
	/**
	 * Sets internal context attribute which will be accessible within the java code.
	 * @param name name of the attribute
	 * @param value value to set
	 */
	public void setInternalAttribute(String name, Object value)
	{
		ExecutionContextManager.getExecutionContext().setInternalAttribute(name, value);
	}
	
	/**
	 * Fetches the attribute value with specified name.
	 * @param name Name of attribute to fetch
	 * @return Attribute value
	 */
	public Object getAttribute(String name)
	{
		return ExecutionContextManager.getExecutionContext().getAttribute(name);
	}
	
	/**
	 * Fetches internal attribute value with specified name.
	 * @param name Name of attribute to fetch
	 * @return Attribute value
	 */
	public Object getInternalAttribute(String name)
	{
		return ExecutionContextManager.getExecutionContext().getInternalAttribute(name);
	}
	
	/**
	 * Removes attribute with specified name.
	 * @param name Name of the attribute to remove.
	 * @return Current attribute value.
	 */
	public Object removeAttribute(String name)
	{
		return ExecutionContextManager.getExecutionContext().removeAttribute(name);
	}
	
	/**
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttributeMap()
	{
		return ExecutionContextManager.getExecutionContext().getAttr();
	}
	
	/**
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttr()
	{
		return ExecutionContextManager.getExecutionContext().getAttr();
	}
	
	/**
	 * Fetches the currently active plugin attribute map.
	 * If no plugin is active, this method will throw exception.
	 * 
	 * @return
	 */
	public Map<String, Object> getPluginAttr(String name)
	{
		return ExecutionContextManager.getExecutionContext().getPluginAttr(name);
	}

	/**
	 * Used to push the parameters on stack of the function going to be executed on. Expected to be
	 * called only before fucntion execution by framework itself.
	 * @param parameters parameters to be pushed
	 */
	public void pushParameters(Map<String, Object> parameters)
	{
		ExecutionContextManager.getInstance().getExecutionContextStack().pushParameters(parameters);
	}
	
	/**
	 * Pops the parameters from the function stack.
	 */
	public void popParameters()
	{
		ExecutionContextManager.getInstance().getExecutionContextStack().popParameters();
	}
	
	public boolean isParamPresent()
	{
		return ExecutionContextManager.getInstance().getExecutionContextStack().isParamPresent();
	}
	
	public Map<String, Object> getParam()
	{
		return ExecutionContextManager.getInstance().getExecutionContextStack().getParam();
	}
	
	public Object getParameter(String name)
	{
		return ExecutionContextManager.getInstance().getExecutionContextStack().getParameter(name);
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
		return ExecutionContextManager.getInstance().getPluginSession(pluginType);
	}

	/**
	 * Starts all registered log monitors.
	 */
	public void startLogMonitoring()
	{
		ExecutionContextManager.getExecutionContext().startLogMonitoring();
	}
	
	/**
	 * Stops the log monitors and collects the log files generated.
	 * @return Collected log files
	 */
	public Map<String, ReportLogFile> stopLogMonitoring(boolean flowErrored)
	{
		return ExecutionContextManager.getExecutionContext().stopMonitoring(flowErrored);
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
			throw new InvalidStateException("Duplicate function name encountered: {}", function.getName());
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
		Function function = ExecutionContextManager.getExecutionContext().getFunction(name);
		
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
			return ExecutionContextManager.getExecutionContext().getExecutionLogger();
		}catch(NonExecutionThreadException ex)
		{
			return Log4jExecutionLogger.getInstance();
		}
	}

	/**
	 * Gets the test suite parser handler.
	 *
	 * @return the test suite parser handler
	 */
	public TestSuiteParserHandler getTestSuiteParserHandler()
	{
		return testSuiteParserHandler;
	}

	/**
	 * Sets the test suite parser handler.
	 *
	 * @param testSuiteParserHandler the new test suite parser handler
	 */
	public void setTestSuiteParserHandler(TestSuiteParserHandler testSuiteParserHandler)
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
}