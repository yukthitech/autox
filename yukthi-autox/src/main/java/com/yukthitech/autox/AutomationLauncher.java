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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.ContentStream;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.event.AutomationEventManager;
import com.yukthitech.autox.event.AutomationEventType;
import com.yukthitech.autox.exec.AsyncTryCatchBlock;
import com.yukthitech.autox.exec.Executor;
import com.yukthitech.autox.exec.FunctionExecutor;
import com.yukthitech.autox.exec.TestSuiteGroupExecutor;
import com.yukthitech.autox.exec.report.FinalReport;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.autox.session.AutoxSession;
import com.yukthitech.autox.test.ExecutionSuite;
import com.yukthitech.autox.test.ExecutionSuiteEntry;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.persistence.repository.RepositoryFactory;
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
		AutomationContext context = AutomationContext.getInstance();
		
		if(context != null)
		{
			context.reset();
		}
	}
	
	/**
	 * Initializes the configurations required by current context.
	 * @param context context whose configurations needs to be initialized
	 * @param extendedCommandLineArgs Extended command line arguments
	 */
	private static void validateCommandLineArguments(AutomationContext context, String extendedCommandLineArgs[])
	{
		//fetch the argument configuration types required
		Collection<IPlugin<?, ?>> plugins = AutomationContext.getInstance().getPluginManager().getPlugins();
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
			
			automationEventManager().onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_CMD_ARG, e.getMessage());
			System.exit(-1);
		} catch(Exception ex)
		{
			ex.printStackTrace();
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			automationEventManager().onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_CMD_ARG, ex.getMessage());
			System.exit(-1);
		}
	}
	
	private static AutomationEventManager automationEventManager()
	{
		return (AutomationEventManager) AutomationContext.getInstance().getAutomationEventManager();
	}
	
	public static AutomationContext loadAutomationContext(ContentStream appConfiguration, String extendedCommandLineArgs[]) throws Exception
	{
		return loadAutomationContext(appConfiguration, extendedCommandLineArgs, null);
	}
	
	public static AutomationContext loadAutomationContext(ContentStream appConfiguration, String extendedCommandLineArgs[], AutomationEventManager eventManager) throws Exception
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
			
			if(eventManager != null)
			{
				eventManager.onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_CMD_ARG, ex.getMessage());
			}
			System.exit(-1);
		}catch(Exception ex)
		{
			ex.printStackTrace();

			System.err.println("Error: " + ex.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			if(eventManager != null)
			{
				eventManager.onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_CMD_ARG, ex.getMessage());
			}
			System.exit(-1);
		}
		
		if(basicArguments != null && StringUtils.isNotBlank(basicArguments.getExecutionSuite()))
		{
			if(StringUtils.isNotBlank(basicArguments.getTestSuites()) || StringUtils.isNotBlank(basicArguments.getTestCases()))
			{
				logger.warn("As execution suite is specified, ignoring test-suites and test-cases limits specified in command line arguments.");
				basicArguments.setTestSuites("");
				basicArguments.setTestCases("");
			}
		}

		File reportFolder = new File(basicArguments.getReportsFolder());

		// force delete report folder, on error try for 5 times
		if(reportFolder.exists())
		{
			AutomationUtils.deleteFolder(reportFolder);
		}
		
		// load the configuration
		ApplicationConfiguration appConfig = ApplicationConfiguration.loadApplicationConfiguration(appConfiguration, basicArguments);
		AutomationContext context = new AutomationContext(appConfig);
		
		PrefixExpressionFactory.init(null, appConfig.getBasePackages());

		context.setBasicArguments(basicArguments);
		context.setReportFolder(reportFolder);
		
		return context;
	}
	
	/**
	 * Loads app configuration into a new {@link AutoxSession}, then loads test suites.
	 *
	 * @param args CLI args: app-config file plus extended args
	 * @return started session (caller must {@link AutoxSession#close()})
	 */
	public static AutoxSession loadTestFiles(String args[]) throws Exception
	{
		return loadTestFiles(args, new AutomationEventManager());
	}
	
	public static AutoxSession loadTestFiles(String args[], AutomationEventManager eventManager) throws Exception
	{
		File currentFolder = new File(".");
		
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(AutoxCliArguments.class);

		logger.debug("Executing from folder: " + currentFolder.getCanonicalPath());

		if(args.length < 1)
		{
			System.err.println("Invalid number of arguments specified");
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			eventManager.onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_CMD_ARG, "Invalid number of arguments specified");
			System.exit(-1);
		}

		File appConfigurationFile = new File(args[0]);

		if(!appConfigurationFile.exists())
		{
			System.err.println("Invalid application configuration file - " + appConfigurationFile);
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			eventManager.onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_CONFIG, "Invalid application configuration file - %s", appConfigurationFile);
			System.exit(-1);
		}
		
		//initialize the configurations
		String extendedCommandLineArgs[] = {};
		
		if(args.length > 1)
		{
			extendedCommandLineArgs = Arrays.copyOfRange(args, 1, args.length);
		}
		
		AutoxSession session = AutoxSession.start(ContentStream.fromFile(appConfigurationFile), extendedCommandLineArgs, eventManager);
		
		try
		{
			AutomationContext context = session.getAutomationContext();
			
			logger.debug("Found extended arguments to be: {}", Arrays.toString(extendedCommandLineArgs));
			validateCommandLineArguments(context, extendedCommandLineArgs);
			
			// load test suites via session so embed loadTestSuiteFiles() stays idempotent
			session.loadTestSuiteFiles();
			
			// check and set execution suite if any
			if(StringUtils.isNotBlank(context.getBasicArguments().getExecutionSuite()))
			{
				context.getBasicArguments().setTestSuites(null);
				context.getBasicArguments().setTestCases(null);

				ExecutionSuite exeSuite = context.getExecutionSuite(context.getBasicArguments().getExecutionSuite());
				
				if(exeSuite == null)
				{
					TestSuite testSuite = context.getTestSuiteGroup().getTestSuite(context.getBasicArguments().getExecutionSuite());
					
					if(testSuite == null)
					{
						System.err.println("ERROR: Invalid execution/test suite specified: " + context.getBasicArguments().getExecutionSuite());
						
						automationEventManager().onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_CMD_ARG, 
								"Invalid execution suite specified: %s", context.getBasicArguments().getExecutionSuite());
						System.exit(-1);
					}
				
					exeSuite = new ExecutionSuite();
					exeSuite.setName(testSuite.getName());
					exeSuite.addEntry(new ExecutionSuiteEntry(testSuite.getName()));
				}
				
				try
				{
					exeSuite.validateAndBuild(context.getTestSuiteGroup());
				}catch(InvalidStateException ex)
				{
					System.err.println(ex.getMessage());
					
					automationEventManager().onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_CMD_ARG, 
							"Encountered invalid configuration in execution suite: %s", context.getBasicArguments().getExecutionSuite());
					System.exit(-1);
				}
				
				logger.debug("Limiting execution to execution-suite: {}", exeSuite.getName());
				context.setActiveExecutionSuite(exeSuite);
			}
			
			return session;
		}catch(Exception ex)
		{
			session.close();
			throw ex;
		}
	}
	
	/**
	 * Post-execution housekeeping (report browser, storage, app-end event).
	 * Does not close the {@link AutoxSession} or call {@code System.exit}.
	 */
	private static void automationCompleted(AutomationContext context, Executor executor)
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
		
		automationEventManager().onAppEnd(executor);
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
	
	/**
	 * @return {@code true} if execution was successful
	 */
	private static boolean executeTestSuiteGroup(TestSuiteGroup testSuiteGroup, AutomationContext context) throws InterruptedException
	{
		TestSuiteGroupExecutor executor = new TestSuiteGroupExecutor(testSuiteGroup);
		CountDownLatch latch = new CountDownLatch(1);
		
		automationEventManager().onInitialize(executor);
		
		AsyncTryCatchBlock.doTry(AutomationLauncher.class.getSimpleName(), callback -> 
		{
			executor.execute(null, null, callback);
		}).onComplete(callback -> 
		{
			FinalReport finalReport = ((ReportDataManager) AutomationContext.getInstance().getReportDataManager()).generateReport();
			printSummary(finalReport);
			
			automationCompleted(context, executor);				
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
		return ((ReportDataManager) AutomationContext.getInstance().getReportDataManager()).isSuccessful();
	}
	
	/**
	 * @return {@code true} if execution was successful
	 */
	private static boolean executeFunction(String functionName, AutomationContext context) throws InterruptedException
	{
		Function function = context.getGlobalFunction(functionName);
		
		if(function == null)
		{
			System.err.println("No function found with specified name: " + functionName);
			System.exit(-1);
		}
		
		FunctionExecutor executor = new FunctionExecutor(function);
		CountDownLatch latch = new CountDownLatch(1);
		
		automationEventManager().onInitialize(executor);
		
		AsyncTryCatchBlock.doTry(AutomationLauncher.class.getSimpleName(), callback -> 
		{
			executor.execute(null, null, callback);
		}).onComplete(callback -> 
		{
			FinalReport finalReport = ((ReportDataManager) AutomationContext.getInstance().getReportDataManager()).generateReport();
			printSummary(finalReport);
			
			automationCompleted(context, executor);				
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
		return ((ReportDataManager) AutomationContext.getInstance().getReportDataManager()).isSuccessful();
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
		AutomationEventManager eventManager = new AutomationEventManager();
		eventManager.onAppStart();
		
		AutoxSession session = null;
		boolean success = false;
		
		try
		{
			session = loadTestFiles(args, eventManager);
			AutomationContext context = session.getAutomationContext();
			TestSuiteGroup testSuiteGroup = context.getTestSuiteGroup();
			
			int debugPort = context.getBasicArguments().getDebugPort();
	
			//execute test suites
			if(debugPort > 0)
			{
				((DebugFlowManager) context.getDebugFlowManager()).startDebugServer(debugPort);
			}
			
			String funcToExecute = context.getBasicArguments().getFunction();
			
			if(StringUtils.isNotBlank(funcToExecute))
			{
				success = executeFunction(funcToExecute, context);
			}
			else
			{
				success = executeTestSuiteGroup(testSuiteGroup, context);
			}
		}catch(Exception ex)
		{
			logger.error("An unhandled error occurred during execution. Error: {}", ex.getMessage(), ex);
			
			eventManager.onAppError(AutomationEventType.AUTOMATION_ERR_UNHANDLED, ex.getMessage());
			success = false;
		}finally
		{
			if(session != null)
			{
				session.close();
			}
		}
		
		if(systemExitEnabled)
		{
			System.exit(success ? 0 : -1);
		}
	}
}