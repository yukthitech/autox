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
package com.yukthitech.autox.exec.report;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.exec.ExecutionType;
import com.yukthitech.autox.exec.Executor;
import com.yukthitech.autox.exec.FunctionExecutor;
import com.yukthitech.autox.exec.TestCaseExecutor;
import com.yukthitech.autox.exec.report.FinalReport.FunctionResult;
import com.yukthitech.autox.logmon.LogMonitorContext;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manages the report generation.
 * @author akranthikiran
 */
public class ReportDataManager
{
	private static Logger logger = LogManager.getLogger(ReportDataManager.ExecutorDetails.class);
	
	private static ReportDataManager instance = new ReportDataManager();
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static String MONITOR_HTML_TEMPLATE;
	
	static
	{
		try
		{
			MONITOR_HTML_TEMPLATE = IOUtils.resourceToString("/monitor-log-template.html", Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading monitor html template", ex);
		}
	}
	
	private class ExecutorDetails
	{
		private Map<ExecutionType, ExecutionLogger> loggers;
		
		private ExecutionStatusReport statusReport;
		
		private FunctionResult functionResult;
		
		public ExecutorDetails(Executor executor)
		{
			if(executor instanceof FunctionExecutor)
			{
				Function function = (Function) executor.getExecutable();
				functionResult = new FunctionResult(function.getName());
			}
			else
			{
				statusReport = new ExecutionStatusReport();
			}
		}
		
		public IExecutionLogger getLoggerIfPresent(ExecutionType executionType)
		{
			if(loggers == null)
			{
				return null;
			}
			
			return loggers.get(executionType);
		}
		
		public IExecutionLogger getLogger(Executor executor, ExecutionType executionType, String suffix, String mainSuffix)
		{
			//for single logger type, all logs gets into single log file
			if(reportInfoProviders.isSingleLogger(executor))
			{
				suffix = mainSuffix;
				executionType = ExecutionType.MAIN;
			}

			ExecutionLogger logger = null;
			
			if(loggers == null)
			{
				loggers = new HashMap<>();
			}
			else
			{
				logger = loggers.get(executionType);
			}
			
			if(logger == null)
			{
				String fileName = reportInfoProviders.getCode(executor) + "_" + reportInfoProviders.getLogName(executor) + suffix;
				logger = new ExecutionLogger(fileName, reportInfoProviders.getName(executor), reportInfoProviders.getDescription(executor));
				
				loggers.put(executionType, logger);
			}
			
			return logger;
		}
	}
	
	private ReportInfoProviders reportInfoProviders = new ReportInfoProviders();
	
	private IdentityHashMap<Executor, ExecutorDetails> executorDetailsMap = new IdentityHashMap<>();
	
	private ExecutorDetails rootExecutorDetails;
	
	private ReportGenerator reportGenerator = new ReportGenerator();
	
	public static ReportDataManager getInstance()
	{
		return instance;
	}
	
	public static void reset()
	{
		instance.executorDetailsMap.clear();
		instance.rootExecutorDetails = null;
	}
	
	private ExecutorDetails getExecutorDetails(Executor executor)
	{
		ExecutorDetails details = executorDetailsMap.get(executor);
		
		if(details == null)
		{
			details = new ExecutorDetails(executor);
			executorDetailsMap.put(executor, details);
			
			if(rootExecutorDetails == null)
			{
				rootExecutorDetails = details;
			}
			
			if(executor.getParentExecutor() != null)
			{
				ExecutorDetails parentDetails = getExecutorDetails(executor.getParentExecutor());
				parentDetails.statusReport.addChidReport(details.statusReport);
			}
			
			if(executor instanceof TestCaseExecutor)
			{
				TestCaseExecutor testCaseExecutor = (TestCaseExecutor) executor;
				
				if(testCaseExecutor.isDataProviderType())
				{
					details.statusReport.setName(testCaseExecutor.getTestCase().getName());
					details.statusReport.setType(ExecutionStatusReportType.DATA_PROVIDER);
				}
			}
		}
		
		return details;
	}
	
	public synchronized IExecutionLogger getSetupExecutionLogger(Executor executor)
	{
		return getExecutorDetails(executor).getLogger(executor, ExecutionType.SETUP, "-setup.js", ".js");
	}

	public synchronized IExecutionLogger getCleanupExecutionLogger(Executor executor)
	{
		return getExecutorDetails(executor).getLogger(executor, ExecutionType.CLEANUP, "-cleanup.js", ".js");
	}

	public synchronized IExecutionLogger getExecutionLogger(Executor executor)
	{
		return getExecutorDetails(executor).getLogger(executor, ExecutionType.MAIN, ".js", ".js");
	}
	
	public String getRep(Executor executor)
	{
		return reportInfoProviders.getName(executor);
	}
	
	private void generateJsonReport()
	{
		File reportFolder = AutomationContext.getInstance().getReportFolder();
		File reportFile = new File(reportFolder, "test-results.json");
		
		try
		{
			if(!reportFolder.exists())
			{
				FileUtils.forceMkdir(reportFolder);
			}
			
			objectMapper.writeValue(reportFile, rootExecutorDetails.statusReport);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to generate report file: " + reportFile.getPath(), ex);
		}
	}
	
	public synchronized void executionStarted(ExecutionType executionType, Executor executor)
	{
		ExecutorDetails executorDetails = getExecutorDetails(executor);
		
		switch (executionType)
		{
			case SETUP:
			{
				executorDetails.statusReport.setSetupExecutionDetails(new ExecutionDetails());
				break;
			}
			case CLEANUP:
			{
				executorDetails.statusReport.setCleanupExecutionDetails(new ExecutionDetails());
				break;
			}
			case MAIN:
			{
				if(executorDetails.statusReport != null)
				{
					executorDetails.statusReport.setName(reportInfoProviders.getName(executor));
					executorDetails.statusReport.setAuthor(reportInfoProviders.getAuthor(executor));
					
					executorDetails.statusReport.setMainExecutionDetails(new ExecutionDetails());
				}
				else
				{
					executorDetails.functionResult.setExecutionDetails(new ExecutionDetails());
				}
				
				break;
			}
			default:
			{
				throw new InvalidStateException("Unsupported execution type encountered: {}", executionType);
			}
		}
		
		generateJsonReport();
	}
	
	private void setEndDetails(ExecutionType executionType, Executor executor, TestStatus status, String mssg)
	{
		ExecutorDetails executorDetails = getExecutorDetails(executor);
		Date endTime = null;
		IExecutionLogger logger = null;
		
		switch (executionType)
		{
			case SETUP:
			{
				endTime = executorDetails.statusReport.getSetupExecutionDetails().setEndDetails(status, mssg);
				logger = executorDetails.getLoggerIfPresent(ExecutionType.SETUP);
				break;
			}
			case CLEANUP:
			{
				endTime = executorDetails.statusReport.getCleanupExecutionDetails().setEndDetails(status, mssg);
				logger = executorDetails.getLoggerIfPresent(ExecutionType.CLEANUP);
				break;
			}
			case MAIN:
			{
				if(executorDetails.statusReport != null)
				{
					endTime = executorDetails.statusReport.getMainExecutionDetails().setEndDetails(status, mssg);
				}
				else
				{
					endTime = executorDetails.functionResult.getExecutionDetails().setEndDetails(status, mssg);
				}
				
				logger = executorDetails.getLoggerIfPresent(ExecutionType.MAIN);
				break;
			}
			default:
			{
				throw new InvalidStateException("Unsupported execution type encountered: {}", executionType);
			}
		}
		
		if(logger != null)
		{
			logger.close(status, endTime);
		}
		
		generateJsonReport();
	}
	
	public FinalReport generateReport()
	{
		FinalReport finalReport = null;
		
		if(rootExecutorDetails.functionResult != null)
		{
			finalReport = new FinalReport(
					AutomationContext.getInstance().getAppConfiguration().getReportName(), 
					rootExecutorDetails.functionResult);
		}
		else
		{
			finalReport = new FinalReport(
					AutomationContext.getInstance().getAppConfiguration().getReportName(), 
					rootExecutorDetails.statusReport);
		}
		
		reportGenerator.generateReports(finalReport);
		return finalReport;
	}
	
	public synchronized void executionCompleted(ExecutionType executionType, Executor executor)
	{
		setEndDetails(executionType, executor, TestStatus.SUCCESSFUL, null);
	}
	
	public synchronized void executionErrored(ExecutionType executionType, Executor executor, String message)
	{
		setEndDetails(executionType, executor, TestStatus.ERRORED, message);
	}
	
	public synchronized void executionFailed(ExecutionType executionType, Executor executor, String message)
	{
		setEndDetails(executionType, executor, TestStatus.FAILED, message);
	}

	public synchronized void executionSkipped(ExecutionType executionType, Executor executor, String reason)
	{
		//during skip flow, the execution would be skipped without starting
		//  so start the execution before marking it as skipped
		if(executionType == ExecutionType.MAIN)
		{
			executionStarted(executionType, executor);
		}
		
		setEndDetails(executionType, executor, TestStatus.SKIPPED, reason);
	}
	
	public synchronized boolean isSuccessful()
	{
		if(rootExecutorDetails.statusReport != null)
		{
			int totalCount = rootExecutorDetails.statusReport.getTotalCount();
			int successCount = rootExecutorDetails.statusReport.getSuccessCount();
			return (totalCount == successCount);
		}
		
		return rootExecutorDetails.functionResult.getExecutionDetails().getStatus() == TestStatus.SUCCESSFUL;
	}
	
	private ReportLogFile generateMonitorHtml(Executor executor, ExecutorDetails executorDetails, String name, ReportLogFile logFile)
	{
		try
		{
			ReportLogFile logHtmlFile = AutomationContext.getInstance().newLogFile(logFile.getFile().getName(), ".html");
			String logContent = FileUtils.readFileToString(logFile.getFile(), Charset.defaultCharset());
			
			if(StringUtils.isBlank(logContent))
			{
				return null;
			}
			
			logContent = StringEscapeUtils.escapeHtml4(logContent);
			
			ExecutionDetails executionDetails = executorDetails.functionResult != null ? 
					executorDetails.functionResult.getExecutionDetails()
					: executorDetails.statusReport.getMainExecutionDetails();
			
			String processedContent = FreeMarkerMethodManager.replaceExpressions("monitor-log-template.html", 
					new LogMonitorContext(
						reportInfoProviders.getName(executor), 
						name, 
						logContent, 
						executionDetails.getStatus(), 
						reportInfoProviders.getDescription(executor)), 
					MONITOR_HTML_TEMPLATE);
			
			FileUtils.write(logHtmlFile.getFile(), processedContent, Charset.defaultCharset());
			
			return logHtmlFile;
		}catch(Exception ex)
		{
			logger.error("An error occurred while creating monitoring log file - {}. Ignoring log error.", name, ex);
		}

		return null;
	}
	
	public synchronized void setMonitoringLogs(Executor executor, Map<String, ReportLogFile> monitorLogs)
	{
		if(MapUtils.isEmpty(monitorLogs))
		{
			return;
		}

		ExecutorDetails executorDetails = getExecutorDetails(executor);
		
		for(Map.Entry<String, ReportLogFile> entry : monitorLogs.entrySet())
		{
			ReportLogFile htmlLogFile = generateMonitorHtml(executor, executorDetails, entry.getKey(), entry.getValue());
			
			if(htmlLogFile == null)
			{
				continue;
			}
			
			if(executorDetails.statusReport != null)
			{
				executorDetails.statusReport.addMonitorLog(entry.getKey(), htmlLogFile.getFile().getName());
			}
			else
			{
				executorDetails.functionResult.addMonitorLog(entry.getKey(), htmlLogFile.getFile().getName());
			}
		}
	}
}
