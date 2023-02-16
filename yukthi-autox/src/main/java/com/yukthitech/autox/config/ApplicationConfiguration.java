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
package com.yukthitech.autox.config;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AutoxCliArguments;
import com.yukthitech.autox.logmon.BrowserLogMonitor;
import com.yukthitech.autox.logmon.FileLogMonitor;
import com.yukthitech.autox.logmon.ILogMonitor;
import com.yukthitech.autox.logmon.RemoteFileLogMonitor;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.mongo.MongoPlugin;
import com.yukthitech.autox.plugin.rest.RestPlugin;
import com.yukthitech.autox.plugin.sql.DbPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Application configuration for applications being automated.
 */
public class ApplicationConfiguration
{
	/**
	 * Static instance of current application configuration.
	 */
	private static ApplicationConfiguration applicationConfiguration;
	
	/**
	 * Name of the report. Length should be less than 30 chars.
	 */
	private String reportName = "Automation";
	
	/**
	 * Folder containing test suite xmls.
	 */
	private List<String> testSuiteFolders = new ArrayList<>();
	
	/**
	 * Base packages to be scanned for steps and validators.
	 */
	private Set<String> basePackages = new HashSet<>();
	
	/**
	 * Plugins configured which would be required by different steps and validators.
	 */
	private Map<Class<?>, IPlugin<?, ?>> plugins = new HashMap<>();
	
	/**
	 * Test data beans that can be used by test cases.
	 */
	private Map<String, Object> dataBeans = new HashMap<>();
	
	/**
	 * List of log monitors to be used.
	 */
	private List<ILogMonitor> logMonitors = new ArrayList<>();
	
	/**
	 * Work directory in which temp files and folders will be created.
	 * The work directory by default will be deleted and recreated during starting.
	 */
	private String workDirectory = "./work";
	
	/**
	 * Date format to be used in reports.
	 */
	private String dateFomat = "dd/MM/YYYY";
	
	/**
	 * Date-time format to be used in reports.
	 */
	private String dateTimeFomat = "dd/MM/YYYY HH:mm:ss";
	
	/**
	 * Time format to be used in reports.
	 */
	private String timeFormat = "hh:mm:ss aa";
	
	private SimpleDateFormat timeFormatObject = new SimpleDateFormat(timeFormat);
	
	/**
	 * Application properties.
	 */
	private Map<String, String> applicationProperties = new HashMap<>();
	
	/**
	 * Defines the summary notification configuration. This is optional.
	 */
	private SummaryNotificationConfig summaryNotificationConfig;
	
	/**
	 * Folder in which automation persistence storage will be done. This would be data
	 * available across the executions.
	 */
	private String dataFolder = "./data";
	
	/**
	 * Storage repository factory.
	 */
	private RepositoryFactory storageRepositoryFactory;
	
	/**
	 * Groups to exclude.
	 */
	private Set<String> excludeGroups = new HashSet<>();
	
	/**
	 * Test suites which should be excluded.
	 */
	private Set<String> excludeTestSuite = new HashSet<>();
	
	/**
	 * List of commands to be executed post automation execution. 
	 * This can include commands like code coverage report generation, sending mail etc.
	 */
	private List<Command> postCommands = new ArrayList<>();
	
	/**
	 * Instantiates a new application configuration.
	 *
	 * @param applicationProperties the application properties
	 */
	public ApplicationConfiguration(Properties applicationProperties)
	{
		this.setApplicationProperties(applicationProperties);
		ApplicationConfiguration.applicationConfiguration = this;
	}
	
	/**
	 * Gets the static instance of current application configuration.
	 *
	 * @return the static instance of current application configuration
	 */
	public static ApplicationConfiguration getInstance()
	{
		return applicationConfiguration;
	}
	
	public static void reset() throws Exception
	{
		if(applicationConfiguration != null && applicationConfiguration.storageRepositoryFactory != null)
		{
			applicationConfiguration.storageRepositoryFactory.close();
		}
		
		applicationConfiguration = null;
	}
	
	/**
	 * Loads application configuration from sepcified file.
	 * 
	 * @param appConfigurationFile
	 *            Application config file to load.
	 * @return Loaded application config.
	 */
	public static ApplicationConfiguration loadApplicationConfiguration(File appConfigurationFile, AutoxCliArguments basicArguments) throws Exception
	{
		Properties appProperties = new Properties();
		
		if(basicArguments.getPropertiesFile() != null)
		{
			FileInputStream propInputStream = new FileInputStream(basicArguments.getPropertiesFile());
			appProperties.load(propInputStream);
		}
		
		FileInputStream fis = new FileInputStream(appConfigurationFile);
		
		ApplicationConfiguration appConfig = new ApplicationConfiguration(appProperties);
		
		try
		{
			XMLBeanParser.parse(fis, appConfig, new AppConfigParserHandler(appProperties));
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to load application configuration file: {}", appConfigurationFile.getPath(), ex);
		}
		
		fis.close();
		
		if(StringUtils.isNotBlank(basicArguments.getTestSuiteFolders()))
		{
			String folders[] = basicArguments.getTestSuiteFolders().trim().split("\\s*\\,\\s*");
			appConfig.testSuiteFolders = Arrays.asList(folders);
		}

		return appConfig;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setApplicationProperties(Properties applicationProperties)
	{
		this.applicationProperties.putAll((Map) applicationProperties);
	}
	
	/**
	 * Gets the application properties.
	 *
	 * @return the application properties
	 */
	public Map<String, String> getApplicationProperties()
	{
		return applicationProperties;
	}

	/**
	 * Gets the name of the report. Length should be less than 30 chars.
	 *
	 * @return the name of the report
	 */
	public String getReportName()
	{
		return reportName;
	}

	/**
	 * Sets the name of the report. Length should be less than 30 chars.
	 *
	 * @param reportName the new name of the report
	 */
	public void setReportName(String reportName)
	{
		this.reportName = reportName;
	}

	/**
	 * Gets the folder containing test suite xmls.
	 *
	 * @return the folder containing test suite xmls
	 */
	public List<String> getTestSuiteFolders()
	{
		return testSuiteFolders;
	}

	/**
	 * Sets the folder containing test suite xmls.
	 *
	 * @param testSuiteFolder the new folder containing test suite xmls
	 */
	public void addTestSuiteFolder(String testSuiteFolder)
	{
		this.testSuiteFolders.add(testSuiteFolder);
	}

	/**
	 * Adds specified base package for scanning.
	 * @param basePackage Base package to be added
	 */
	public void addBasePackage(String basePackage)
	{
		this.basePackages.add(basePackage);
	}
	
	/**
	 * Base package names where validations and steps needs to be scanned.
	 * @return Base package names
	 */
	public Set<String> getBasePackages()
	{
		return basePackages;
	}

	/**
	 * Generic adder for adding any type of plugin object. 
	 * @param plugin plugin to be added.
	 */
	public void addPlugin(IPlugin<?, ?> plugin)
	{
		this.plugins.put(plugin.getClass(), plugin);
	}
	
	/**
	 * Sets specified selenium plugin.
	 * @param plugin selenium plugin to set
	 */
	public void setSeleniumPlugin(SeleniumPlugin plugin)
	{
		this.addPlugin(plugin);
	}
	
	/**
	 * Sets specified db plugin.
	 * @param plugin db plugin to set
	 */
	public void setDbPlugin(DbPlugin plugin)
	{
		this.addPlugin(plugin);
	}
	
	/**
	 * Sets the specified mongo plugin.
	 * @param plugin
	 */
	public void setMongoPlugin(MongoPlugin plugin)
	{
		this.addPlugin(plugin);
	}

	/**
	 * Sets the rest plugin.
	 *
	 * @param plugin the new rest plugin
	 */
	public void setRestPlugin(RestPlugin plugin)
	{
		this.addPlugin(plugin);
	}
	
	/**
	 * Gets the plugin from configured plugins of specified type.
	 * @return Matching plugin.
	 */
	@SuppressWarnings("unchecked")
	public <T extends IPlugin<?, ?>> T getPlugin(Class<T> pluginType)
	{
		return (T) plugins.get(pluginType);
	}
	
	/**
	 * Fetches all plugins.
	 * @return all plugins
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<IPlugin<?, ?>> getAllPlugins()
	{
		return (Collection) plugins.values();
	}

	/**
	 * Adds specified data bean to this test suite.
	 * @param name Name of the data bean.
	 * @param bean Bean to be added.
	 */
	public void addDataBean(String name, Object bean)
	{
		dataBeans.put(name, bean);
	}
	
	/**
	 * Gets data bean with specified name.
	 * @param name Name of the data bean
	 * @return matching data bean
	 */
	public Object getDataBean(String name)
	{
		return dataBeans.get(name);
	}
	
	/**
	 * Gets the test data beans that can be used by test cases.
	 *
	 * @return the test data beans that can be used by test cases
	 */
	public Map<String, Object> getDataBeans()
	{
		return dataBeans;
	}
	
	/**
	 * Adds log monitor to the configuration.
	 * @param monitor monitor to add
	 */
	public void addLogMonitor(ILogMonitor monitor)
	{
		if(monitor == null)
		{
			throw new NullPointerException("Monitor can not be null");
		}
		
		this.logMonitors.add(monitor);
	}
	
	/**
	 * Adds file log monitor to this configuration.
	 * @param monitor monitor to add.
	 */
	public void addFileLogMonitor(FileLogMonitor monitor)
	{
		addLogMonitor(monitor);
	}
	
	/**
	 * Adds specified remote log monitor.
	 * @param monitor monitor to add
	 */
	public void addRemoteLogMonitor(RemoteFileLogMonitor monitor)
	{
		addLogMonitor(monitor);
	}
	
	/**
	 * Adds the browser log monitor.
	 *
	 * @param monitor the monitor
	 */
	public void addBrowserLogMonitor(BrowserLogMonitor monitor)
	{
		addLogMonitor(monitor);
	}
	
	/**
	 * Gets the list of log monitors to be used.
	 *
	 * @return the list of log monitors to be used
	 */
	public List<ILogMonitor> getLogMonitors()
	{
		return logMonitors;
	}

	/**
	 * Gets the work directory in which temp files and folders will be created. The work directory by default will be deleted and recreated during starting.
	 *
	 * @return the work directory in which temp files and folders will be created
	 */
	public String getWorkDirectory()
	{
		return workDirectory;
	}

	/**
	 * Sets the work directory in which temp files and folders will be created. The work directory by default will be deleted and recreated during starting.
	 *
	 * @param workDirectory the new work directory in which temp files and folders will be created
	 */
	public void setWorkDirectory(String workDirectory)
	{
		if(StringUtils.isBlank(workDirectory))
		{
			throw new NullPointerException("Work directory can not be null or empty.");
		}
		
		this.workDirectory = workDirectory;
	}

	/**
	 * Gets the date format to be used in reports.
	 *
	 * @return the date format to be used in reports
	 */
	public String getDateFomat()
	{
		return dateFomat;
	}

	/**
	 * Sets the date format to be used in reports.
	 *
	 * @param dateFomat the new date format to be used in reports
	 */
	public void setDateFomat(String dateFomat)
	{
		this.dateFomat = dateFomat;
	}

	/**
	 * Gets the date-time format to be used in reports.
	 *
	 * @return the date-time format to be used in reports
	 */
	public String getDateTimeFomat()
	{
		return dateTimeFomat;
	}

	/**
	 * Sets the date-time format to be used in reports.
	 *
	 * @param dateTimeFomat the new date-time format to be used in reports
	 */
	public void setDateTimeFomat(String dateTimeFomat)
	{
		this.dateTimeFomat = dateTimeFomat;
	}

	/**
	 * Gets the time format to be used in reports.
	 *
	 * @return the time format to be used in reports
	 */
	public String getTimeFormat()
	{
		return timeFormat;
	}

	/**
	 * Sets the time format to be used in reports.
	 *
	 * @param timeFomat the new time format to be used in reports
	 */
	public void setTimeFormat(String timeFomat)
	{
		SimpleDateFormat timeFormatObject = new SimpleDateFormat(timeFomat);
		
		this.timeFormat = timeFomat;
		this.timeFormatObject = timeFormatObject;
	}
	
	public SimpleDateFormat getTimeFormatObject()
	{
		return timeFormatObject;
	}

	/**
	 * Gets the defines the summary notification configuration. This is optional.
	 *
	 * @return the defines the summary notification configuration
	 */
	public SummaryNotificationConfig getSummaryNotificationConfig()
	{
		return summaryNotificationConfig;
	}

	/**
	 * Sets the defines the summary notification configuration. This is optional.
	 *
	 * @param summaryNotificationConfig the new defines the summary notification configuration
	 */
	public void setSummaryNotificationConfig(SummaryNotificationConfig summaryNotificationConfig)
	{
		this.summaryNotificationConfig = summaryNotificationConfig;
	}

	/**
	 * Gets the folder in which automation persistence storage will be done. This would be data available across the executions.
	 *
	 * @return the folder in which automation persistence storage will be done
	 */
	public String getDataFolder()
	{
		return dataFolder;
	}

	/**
	 * Sets the folder in which automation persistence storage will be done. This would be data available across the executions.
	 *
	 * @param dataFolder the new folder in which automation persistence storage will be done
	 */
	public void setDataFolder(String dataFolder)
	{
		this.dataFolder = dataFolder;
	}

	/**
	 * Gets the storage repository factory.
	 *
	 * @return the storage repository factory
	 */
	public RepositoryFactory getStorageRepositoryFactory()
	{
		return storageRepositoryFactory;
	}

	/**
	 * Sets the storage repository factory.
	 *
	 * @param storageRepositoryFactory the new storage repository factory
	 */
	public void setStorageRepositoryFactory(RepositoryFactory storageRepositoryFactory)
	{
		this.storageRepositoryFactory = storageRepositoryFactory;
	}
	
	/**
	 * Adds groups to exclude with comma delimited values.
	 * @param groupsStr
	 */
	public void addExcludedGroups(String groupsStr)
	{
		String groups[] = groupsStr.split("\\s*\\,\\s*");
		
		for(String grp : groups)
		{
			if(grp.trim().length() == 0)
			{
				continue;
			}
			
			this.excludeGroups.add(grp);
		}
	}
	
	public void addExcludedTestSuites(String testSuiteStr)
	{
		String tsLst[] = testSuiteStr.split("\\s*\\,\\s*");
		
		for(String ts : tsLst)
		{
			if(ts.trim().length() == 0)
			{
				continue;
			}
			
			this.excludeTestSuite.add(ts);
		}
	}

	/**
	 * Returns true if specified group name is under exclusion list.
	 * @param name group name to check
	 * @return true if specified group is excluded.
	 */
	public boolean isGroupExcluded(String name)
	{
		return excludeGroups.contains(name);
	}
	
	public boolean isTestSuiteExcluded(String name)
	{
		return excludeTestSuite.contains(name);
	}
	
	/**
	 * Adds the specified post command.
	 * @param command
	 */
	public void addPostCommand(Command command)
	{
		this.postCommands.add(command);
	}
	
	/**
	 * Gets the list of commands to be executed post automation execution. This can include commands like code coverage report generation, sending mail etc.
	 *
	 * @return the list of commands to be executed post automation execution
	 */
	public List<Command> getPostCommands()
	{
		return postCommands;
	}
}
