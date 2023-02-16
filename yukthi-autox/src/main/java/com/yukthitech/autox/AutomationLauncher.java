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
package com.yukthitech.autox;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.AutoxInfoException;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.DebugServer;
import com.yukthitech.autox.exec.AsyncTryCatchBlock;
import com.yukthitech.autox.exec.ExecutionPool;
import com.yukthitech.autox.exec.FunctionExecutor;
import com.yukthitech.autox.exec.TestSuiteGroupExecutor;
import com.yukthitech.autox.exec.report.FinalReport;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.PluginManager;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestDataFile;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.ccg.xml.XMLLoadException;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.cli.CommandLineOptions;
import com.yukthitech.utils.cli.MissingArgumentException;
import com.yukthitech.utils.cli.OptionsFactory;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Main class which executes the test suites of tha application.
 * 
 * @author akiran
 */
public class AutomationLauncher
{
	private static Logger logger = LogManager.getLogger(AutomationLauncher.class);
	
	private static final String COMMAND_SYNTAX = String.format("java %s <app-config-file> extended-args...", AutomationLauncher.class.getName());
	
	public static boolean systemExitEnabled = true;
	
	public static void resetState() throws Exception
	{
		AutomationContext.reset();
		ExecutionContextManager.reset();
		ReportDataManager.reset();
		ExecutionPool.reset();
		
		DebugFlowManager.getInstance().reset();
		
		if(DebugServer.getInstance() != null)
		{
			DebugServer.getInstance().reset();
		}
	}
	
	private static void handleLoadError(File xmlFile, Set<String> errors, Exception ex)
	{
		String error = null;
		
		if(ex instanceof XMLLoadException)
		{
			XMLLoadException xmlLoadException = (XMLLoadException) ex;
			String mainError = null;
			
			if(xmlLoadException.getCause() instanceof AutoxInfoException)
			{
				mainError = xmlLoadException.getCause().getMessage();
			}
			else if(xmlLoadException.getCause() == null)
			{
				mainError = xmlLoadException.getMessage();
			}
			else
			{
				mainError = CommonUtils.getRootCauseMessages(ex.getCause());
			}
			
			if("true".equalsIgnoreCase(System.getProperty("logLoadErrors")))
			{
				logger.error("An error occurred while loading file: {}", xmlFile.getPath(), ex);
			}
			
			error = String.format("File: %s\n"
					+ "Location: [Line: %s, Column: %s]\n"
					+ "Error: %s", xmlFile.getPath(), 
						xmlLoadException.hasLocation() ? xmlLoadException.getLineNumber() : "<unknown>", 
						xmlLoadException.hasLocation() ? xmlLoadException.getColumn() : "<unknown>",
						mainError);
		}
		else
		{
			//only non-load exceptions log it. Loading exceptions should be displayed in proper format
			logger.error("An error occurred while loading file: {}", xmlFile.getPath(), ex);
			
			error = String.format("File: %s\n"
					+ "Error: %s", xmlFile.getPath(), CommonUtils.getRootCauseMessages(ex.getCause()));
		}
		
		errors.add(error);
	}

	/**
	 * Loads test suites from the test suite folder specified by app
	 * configuration.
	 * 
	 * @param context current context
	 * @param appConfig
	 *            Application config to be used
	 * @return Test suites mapped by name.
	 */
	private static TestSuiteGroup loadTestSuites(AutomationContext context, ApplicationConfiguration appConfig, boolean loadTestSuites)
	{
		TestSuiteParserHandler defaultParserHandler = new TestSuiteParserHandler(context);
		context.setTestSuiteParserHandler(defaultParserHandler);
		
		if(!loadTestSuites)
		{
			return null;
		}
		
		logger.debug("Loading test suites from folders - {}", appConfig.getTestSuiteFolders());
		
		List<File> xmlFiles = new ArrayList<>();

		for(String folder : appConfig.getTestSuiteFolders())
		{
			File testCaseFolder = new File(folder);
	
			if(!testCaseFolder.exists() && !testCaseFolder.isDirectory())
			{
				System.err.println("Invalid test suite folder specified - " + testCaseFolder);
				System.exit(-1);
			}
	
			// load the test suites recursively
			xmlFiles.addAll( AutomationUtils.loadXmlFiles(testCaseFolder) );
		}
		
		TestDataFile testDataFile = null;
		FileInputStream fis = null;
		String filePath = null;
		
		TestSuiteGroup testSuiteGroup = new TestSuiteGroup();
		
		File setupFile = null, cleanupFile = null;
		List<File> limitFolders = context.getBasicArguments().getFolderLimitFiles();
		
		if(limitFolders != null)
		{
			logger.debug("Limiting the xml file loading to folders: {}", limitFolders);
		}

		Set<String> errors = new HashSet<>();

		for(File xmlFile : xmlFiles)
		{
			if(limitFolders != null)
			{
				boolean found = false;
				
				for(File folder : limitFolders)
				{
					if(AutomationUtils.isChild(folder, xmlFile))
					{
						found = true;
						break;
					}
				}
				
				if(!found)
				{
					logger.trace("Skiping file as it is not present in specified folder limits. File: {}", xmlFile.getPath());
					continue;
				}
			}
			
			testDataFile = new TestDataFile(context);
			defaultParserHandler.setFileBeingParsed(xmlFile);

			try
			{
				filePath = xmlFile.getPath();
				fis = new FileInputStream(xmlFile);

				logger.debug("Loading test suite file: {}", filePath);
				
				XMLBeanParser.parse(fis, testDataFile, defaultParserHandler);
				fis.close();
				
				for(TestSuite testSuite : testDataFile.getTestSuites())
				{
					logger.debug("Loading test suite '{}' from file: {}", testSuite.getName(), filePath);
					
					testSuite.setFile(xmlFile);
					testSuiteGroup.addTestSuite(testSuite);
					
					if(testSuite.getTestCases() != null)
					{
						testSuite.getTestCases().forEach(tc -> tc.setFile(xmlFile));
					}
				}
				
				if(testDataFile.getSetup() != null)
				{
					if(setupFile != null)
					{
						throw new InvalidStateException("Duplicate global setups specified. Files: [{}, {}]", setupFile.getPath(), xmlFile.getPath());
					}
					
					if(testDataFile.getSetup().getLocation() == null)
					{
						testDataFile.getSetup().setLocation(xmlFile, -1);
					}
					
					testSuiteGroup.setSetup(testDataFile.getSetup());
					setupFile = xmlFile;
				}
				
				if(testDataFile.getCleanup() != null)
				{
					if(cleanupFile != null)
					{
						throw new InvalidStateException("Duplicate global cleaups specified. Files: [{}, {}]", cleanupFile.getPath(), xmlFile.getPath());
					}
					
					if(testDataFile.getCleanup().getLocation() == null)
					{
						testDataFile.getCleanup().setLocation(xmlFile, -1);
					}
					
					testSuiteGroup.setCleanup(testDataFile.getCleanup());
					cleanupFile = xmlFile;
				}
			} catch(Exception ex)
			{
				handleLoadError(xmlFile, errors, ex);
			}
		}
		
		if(!errors.isEmpty())
		{
			String errorStr = errors.stream().collect(Collectors.joining("\n"));
			
			System.err.println("\n\n\n==============================================================\n");
			System.err.println("Failed to load test files because of below errors: \n");
			System.err.println(errorStr);
			System.err.println("\n\n==============================================================");
			
			System.exit(-1);
		}

		logger.debug("Found required plugins by this context to be: {}", PluginManager.getInstance().getPlugins());
		
		return testSuiteGroup;
	}
	
	/**
	 * Initializes the configurations required by current context.
	 * @param context context whose configurations needs to be initialized
	 * @param extendedCommandLineArgs Extended command line arguments
	 */
	private static void validateCommandLineArguments(AutomationContext context, String extendedCommandLineArgs[])
	{
		//fetch the argument configuration types required
		Collection<IPlugin<?, ?>> plugins = PluginManager.getInstance().getPlugins();
 		List<Class<?>> argBeanTypes = plugins.stream()
				.map(config -> config.getArgumentBeanType())
				.filter(type -> (type != null))
				.collect(Collectors.toList());
		
		argBeanTypes = new ArrayList<>(argBeanTypes);
		
		//Add basic arguments type, so that on error its properties are not skipped in error message
		argBeanTypes.add(AutoxCliArguments.class);

		//if any type is required creation command line options and parse command line arguments
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(argBeanTypes.toArray(new Class<?>[0]));
		
		try
		{
			commandLineOptions.parseBeans(extendedCommandLineArgs);
		} catch(MissingArgumentException e)
		{
			System.err.println("Error: " + e.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			System.exit(-1);
		} catch(Exception ex)
		{
			ex.printStackTrace();
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			System.exit(-1);
		}
	}
	
	public static AutomationContext loadAutomationContext(File appConfigurationFile, String extendedCommandLineArgs[]) throws Exception
	{
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(AutoxCliArguments.class);
		AutoxCliArguments basicArguments = null;
		
		try
		{
			basicArguments = (AutoxCliArguments) commandLineOptions.parseBean(extendedCommandLineArgs);
		}catch(MissingArgumentException | UnrecognizedOptionException ex)
		{
			System.err.println("Error: " + ex.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}catch(Exception ex)
		{
			ex.printStackTrace();

			System.err.println("Error: " + ex.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}

		File reportFolder = new File(basicArguments.getReportsFolder());

		// force delete report folder, on error try for 5 times
		if(reportFolder.exists())
		{
			AutomationUtils.deleteFolder(reportFolder);
		}
		
		// load the configuration file
		ApplicationConfiguration appConfig = ApplicationConfiguration.loadApplicationConfiguration(appConfigurationFile, basicArguments);
		AutomationContext context = new AutomationContext(appConfig);
		
		PrefixExpressionFactory.init(null, appConfig.getBasePackages());

		context.setBasicArguments(basicArguments);
		context.setReportFolder(reportFolder);
		
		return context;
	}
	
	public static TestSuiteGroup loadTestFiles(String args[]) throws Exception
	{
		File currentFolder = new File(".");
		
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(AutoxCliArguments.class);

		logger.debug("Executing from folder: " + currentFolder.getCanonicalPath());

		if(args.length < 1)
		{
			System.err.println("Invalid number of arguments specified");
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}

		File appConfigurationFile = new File(args[0]);

		if(!appConfigurationFile.exists())
		{
			System.err.println("Invalid application configuration file - " + appConfigurationFile);
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}
		
		//initialize the configurations
		String extendedCommandLineArgs[] = {};
		
		if(args.length > 1)
		{
			extendedCommandLineArgs = Arrays.copyOfRange(args, 1, args.length);
		}
		
		//load automation context
		AutomationContext context = loadAutomationContext(appConfigurationFile, extendedCommandLineArgs);
		ApplicationConfiguration appConfig = context.getAppConfiguration();
		
		logger.debug("Found extended arguments to be: {}", Arrays.toString(extendedCommandLineArgs));
		validateCommandLineArguments(context, extendedCommandLineArgs);
		
		boolean loadTestSuites = true;
		// load test suites
		TestSuiteGroup testSuiteGroup = loadTestSuites(context, appConfig, loadTestSuites);
		context.setTestSuiteGroup(testSuiteGroup);
		
		return testSuiteGroup;
	}
	
	private static void automationCompleted(boolean res, AutomationContext context)
	{
		if(!context.getBasicArguments().isReportOpeningDisalbed())
		{
			try
			{
				Desktop.getDesktop().open(new File(context.getReportFolder(), "index.html"));
			}catch(Exception ex)
			{
				logger.warn("Failed to open report html in browser. Ignoring the error: " + ex);
			}
		}
		
		//close the open resources
		RepositoryFactory repoFactory = context.getAppConfiguration().getStorageRepositoryFactory();
		
		try
		{
			if(repoFactory != null)
			{
				repoFactory.close();
			}
		}catch(Exception ex)
		{
			logger.error("An error occurred while closing the resources", ex);
		}
		
		if(systemExitEnabled)
		{
			System.exit( res ? 0 : -1 );
		}
	}
	
	private static void printSummary(FinalReport report)
	{
		String summary = String.format(""
				+ "\n\n======================================================================\n"
				+ "    Test Suite Summary ==> Total: %s, Failures: %s, Errors: %s, Skips: %s\n"
				+ "    Test Case Summary ==> Total: %s, Failures: %s, Errors: %s, Skips: %s\n"
				+ "======================================================================\n\n", 
				report.getTestSuiteCount(), report.getTestSuiteFailureCount(), report.getTestSuiteErrorCount(), report.getTestSuiteSkippedCount(),
				report.getTestCaseCount(), report.getTestCaseFailureCount(), report.getTestCaseErroredCount(), report.getTestCaseSkippedCount());
		
		System.out.println(summary);
	}
	
	private static void executeTestSuiteGroup(TestSuiteGroup testSuiteGroup, AutomationContext context) throws InterruptedException
	{
		TestSuiteGroupExecutor executor = new TestSuiteGroupExecutor(testSuiteGroup);
		CountDownLatch latch = new CountDownLatch(1);
		
		AsyncTryCatchBlock.doTry(AutomationLauncher.class.getSimpleName(), callback -> 
		{
			executor.execute(null, null, callback);
		}).onComplete(callback -> 
		{
			FinalReport finalReport = ReportDataManager.getInstance().generateReport();
			printSummary(finalReport);
			
			ExecutionContextManager.getInstance().close();
			
			AutomationContext.getInstance()
				.getAppConfiguration()
				.getAllPlugins()
				.forEach(plugin -> plugin.close());
			
			automationCompleted(ReportDataManager.getInstance().isSuccessful(), context);				
		}).onError((callback, ex) -> 
		{
			logger.error("An error occurred during automation execution", ex);
		})
		.onFinally(callback -> 
		{
			latch.countDown();
		}).execute();
		
		latch.await();
		logger.debug("Automation completed...");
	}
	
	private static void executeFunction(String functionName, AutomationContext context) throws InterruptedException
	{
		Function function = context.getGlobalFunction(functionName);
		
		if(function == null)
		{
			System.err.println("No function found with specified name: " + functionName);
			System.exit(-1);
		}
		
		FunctionExecutor executor = new FunctionExecutor(function);
		
		CountDownLatch latch = new CountDownLatch(1);
		
		AsyncTryCatchBlock.doTry(AutomationLauncher.class.getSimpleName(), callback -> 
		{
			executor.execute(null, null, callback);
		}).onComplete(callback -> 
		{
			FinalReport finalReport = ReportDataManager.getInstance().generateReport();
			printSummary(finalReport);
			
			ExecutionContextManager.getInstance().close();
			
			AutomationContext.getInstance()
				.getAppConfiguration()
				.getAllPlugins()
				.forEach(plugin -> plugin.close());
			
			automationCompleted(ReportDataManager.getInstance().isSuccessful(), context);				
		}).onError((callback, ex) -> 
		{
			logger.error("An error occurred during automation execution", ex);
		})
		.onFinally(callback -> 
		{
			latch.countDown();
		}).execute();
		
		latch.await();
		logger.debug("Automation completed...");
	}

	/**
	 * Automation entry point.
	 * 
	 * @param args
	 *            CMD line arguments.
	 */
	public static void main(String[] args) throws Exception
	{
		System.out.println("Executing main function of automation launcher...");
		
		try
		{
			// load test suites
			TestSuiteGroup testSuiteGroup = loadTestFiles(args);
			AutomationContext context = AutomationContext.getInstance();
			
			int debugPort = context.getBasicArguments().getDebugPort();
	
			//execute test suites
			if(debugPort > 0)
			{
				DebugServer.start(debugPort);
			}
			
			String funcToExecute = context.getBasicArguments().getFunction();
			
			if(StringUtils.isNotBlank(funcToExecute))
			{
				executeFunction(funcToExecute, context);
			}
			else
			{
				executeTestSuiteGroup(testSuiteGroup, context);
			}
		}catch(Exception ex)
		{
			logger.error("An unhandled error occurred during execution. Error: {}", ex.getMessage(), ex);
			System.exit(-1);
		}
	}
}