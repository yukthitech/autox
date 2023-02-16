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
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * A simple internal logger to consolidate execution messages in test result.
 * 
 * @author akiran
 */
public class ExecutionLogger implements IExecutionLogger
{
	private static Logger logger = LogManager.getLogger(ExecutionLogger.class);
	
	private static ObjectMapper JSON_MAPPER = new ObjectMapper();
	
	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\d*)\\}");
	
	/**
	 * Flag indicating if logging is disabled or not.
	 */
	private boolean disabled = false;
	
	/**
	 * Mode to be prepended for every log message.
	 */
	private String mode;
	
	/**
	 * Maximum parameter length to be allowed in log messages.
	 */
	private int maxParamLength = 1000;
	
	private PrintWriter logWriter;
	
	private File logsFolder;
	
	private File logFile;
	
	private int logIndex = 0;
	
	private Date startTime;

	public ExecutionLogger(String fileName, String executorName, String executorDescription)
	{
		AutomationContext automationContext = AutomationContext.getInstance();
		
		this.logsFolder = new File(automationContext.getReportFolder(), IAutomationConstants.LOGS_FOLDER_NAME);
		logFile = new File(logsFolder, fileName);
		
		try
		{
			if(!logsFolder.exists())
			{
				FileUtils.forceMkdir(logsFolder);
			}
			
			//open file in append mode, so that if logger is created for same resource, the logs
			// gets appended
			this.logWriter = new PrintWriter(new FileOutputStream(logFile, true));
			this.logWriter.println("var logs = [];");
			this.logWriter.flush();
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to open print writer for log file: {}", logFile.getPath(), ex);
		}
		
		if(automationContext.getAppConfiguration() != null && automationContext.getAppConfiguration().getApplicationProperties() != null)
		{
			String logMaxParamLenStr = automationContext.getAppConfiguration().getApplicationProperties().get(IAutomationConstants.PROP_LOG_MAX_PROP_LEN);
			
			if(logMaxParamLenStr != null)
			{
				try
				{
					int maxLen = Integer.parseInt(logMaxParamLenStr);
					
					//max length should be min of 100
					if(maxLen > 100)
					{
						this.maxParamLength = maxLen;
					}
					else
					{
						logger.warn("IGNORED: As the value specified by {} property is less than 100, it is ignored. Value specified: {}", 
								IAutomationConstants.PROP_LOG_MAX_PROP_LEN, maxLen);
					}
				}catch(Exception ex)
				{
					logger.warn("IGNORED ERROR: Invalid numerical value specified for {} property", IAutomationConstants.PROP_LOG_MAX_PROP_LEN, ex);
				}
			}
		}
	
		addMessage(new ExecutionLogData.Header(executorName, executorDescription));
		startTime = new Date();
	}
	
	/**
	 * Sets the mode to be prepended for every log message.
	 *
	 * @param mode the new mode to be prepended for every log message
	 */
	@Override
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	
	/**
	 * Clears the mode from the logger.
	 */
	@Override
	public void clearMode()
	{
		this.mode = null;
	}
	
	/**
	 * Sets the flag indicating if logging is disabled or not.
	 *
	 * @param disabled the new flag indicating if logging is disabled or not
	 */
	@Override
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
	
	/**
	 * Gets the flag indicating if logging is disabled or not.
	 *
	 * @return the flag indicating if logging is disabled or not
	 */
	@Override
	public boolean isDisabled()
	{
		return disabled;
	}
	
	private String getSource(StackTraceElement stackTrace[])
	{
		StackTraceElement finalElem = null;
		String curClsName = ExecutionLogger.class.getName();
		
		for(StackTraceElement elem  : stackTrace)
		{
			if(elem.getClassName().equals(curClsName) || elem.getClassName().startsWith("java"))
			{
				continue;
			}
			
			finalElem = elem;
			break;
		}
		
		return finalElem.getFileName() + ":" + finalElem.getLineNumber();
	}
	
	/**
	 * Fetches the location of the element.
	 * @param locationBased
	 * @return
	 */
	private String getSourceLocation()
	{
		return ExecutionContextManager.getInstance()
				.getExecutionStack()
				.getCurrentLocation();
	}
	
	/**
	 * Replaces the args values in "message" using patterns mentioned below and same will be returned. 
	 * 
	 * {} will match with the current index argument. If index is greater than provided values then &lt;undefined&gt; string will be used.
	 * {&lt;idx&gt;} can be used to refer to argument at particular index. Helpful in building messages which uses same argument multiple times.
	 * 
	 * @param message Message string with expressions
	 * @param args Values for expression
	 * @return Formatted string
	 */
	private String format(String message, Object... args)
	{
		//when message is null, return null
		if(message == null)
		{
			return null;
		}
		
		//when args is null, assume empty values
		if(args == null)
		{
			args = new Object[0];
		}
		
		Matcher matcher = PARAM_PATTERN.matcher(message);
		StringBuffer buffer = new StringBuffer();
		
		int loopIndex = 0;
		int argIndex = 0;
		Object arg = null;
		
		//loop through pattern matches
		while(matcher.find())
		{
			//if index is mentioned in pattern
			if(org.apache.commons.lang3.StringUtils.isNotBlank(matcher.group(1)))
			{
				argIndex = Integer.parseInt(matcher.group(1));
			}
			//if index is not specified, use current loop index
			else
			{
				argIndex = loopIndex;
			}
			
			//if the index is within provided arguments length
			if(argIndex < args.length)
			{
				arg = args[argIndex];
			}
			//if the index is greater than available values
			else
			{
				arg = "<undefined>";
			}
			
			String argStr = null;
			
			//if argument value is null
			if(arg == null)
			{
				argStr = "null";
			}
			else
			{
				argStr = arg.toString();
				argStr = (argStr.length() > maxParamLength) ? (argStr.substring(0, maxParamLength) + "...") : argStr;
			}

			argStr = Matcher.quoteReplacement(argStr);
			
			matcher.appendReplacement(buffer, argStr);
			loopIndex++;
		}
		
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private String buildMessage(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		String finalMssg = format(mssgTemplate, args);
		
		if(escapeHtml)
		{
			finalMssg = StringEscapeUtils.escapeHtml4(finalMssg);
		}

		if(mode != null)
		{
			finalMssg = "<b>[" + mode + "]</b> " + finalMssg;
		}
		
		return finalMssg;
	}
	
	private synchronized void addMessage(Object mssg)
	{
		if(logWriter == null)
		{
			throw new InvalidStateException("Logger [File: {}] is already closed", logFile.getName());
		}
		
		try
		{
			logWriter.println("logs[" + logIndex + "] = " + JSON_MAPPER.writeValueAsString(mssg) + ";");
			logWriter.flush();
			
			logIndex++;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while logging mssg", ex);
		}
	}

	/**
	 * Used to log error messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void error(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		buildAndAddMssg(LogLevel.ERROR, escapeHtml, mssgTemplate, args);
	}

	/**
	 * Used to log error messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void error(String mssgTemplate, Object... args)
	{
		error(true, mssgTemplate, args);
	}

	private void buildAndAddMssg(LogLevel logLevel, boolean escapeHtml, String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = buildMessage(escapeHtml, mssgTemplate, args);

		logger.log(logLevel.getLog4jLevel(), finalMssg);
		addMessage(new ExecutionLogData.Message( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), logLevel, finalMssg, new Date()));
	}
	
	/**
	 * Used to log debug messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void debug(String mssgTemplate, Object... args)
	{
		buildAndAddMssg(LogLevel.DEBUG, true, mssgTemplate, args);
	}

	/**
	 * Used to log debug messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void debug(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		buildAndAddMssg(LogLevel.DEBUG, escapeHtml, mssgTemplate, args);
	}
	
	/**
	 * Used to log info messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void info(String mssgTemplate, Object... args)
	{
		buildAndAddMssg(LogLevel.INFO, true, mssgTemplate, args);
	}

	/**
	 * Used to log info messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void info(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		buildAndAddMssg(LogLevel.INFO, escapeHtml, mssgTemplate, args);
	}

	/**
	 * Used to log warn messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void warn(String mssgTemplate, Object... args)
	{
		buildAndAddMssg(LogLevel.WARN, true, mssgTemplate, args);
	}

	/**
	 * Used to log warn messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void warn(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		buildAndAddMssg(LogLevel.WARN, escapeHtml, mssgTemplate, args);
	}

	/**
	 * Used to log trace messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void trace(String mssgTemplate, Object... args)
	{
		buildAndAddMssg(LogLevel.TRACE, true, mssgTemplate, args);
	}
	
	/**
	 * Used to log trace messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	@Override
	public void trace(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		buildAndAddMssg(LogLevel.TRACE, escapeHtml, mssgTemplate, args);
	}

	/**
	 * Logs the message at specified level.
	 * @param source location from where logging is being done
	 * @param logLevel level of log
	 * @param mssgTemplate msg template
	 * @param args arguments for message
	 */
	@Override
	public void log(LogLevel logLevel, String mssgTemplate, Object... args)
	{
		log(true, logLevel, mssgTemplate, args);
	}
	
	/**
	 * Logs the message at specified level.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param logLevel level of log
	 * @param mssgTemplate msg template
	 * @param args arguments for message
	 */
	@Override
	public void log(boolean escapeHtml, LogLevel logLevel, String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = buildMessage(escapeHtml, mssgTemplate, args);

		logger.debug(finalMssg);
		addMessage(new ExecutionLogData.Message( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), logLevel, finalMssg, new Date()));
	}

	/**
	 * Adds the specified image file to the debug log.
	 * @param source location from where logging is being done
	 * @param name Name of the image file for easy identification
	 * @param message Message to be logged along with image
	 * @param imageFile Image to be logged
	 * @param logLevel level to be used.
	 */
	@Override
	public void logImage(String message, ReportLogFile imageFile, LogLevel logLevel)
	{
		if(disabled)
		{
			return;
		}
		
		if(logLevel == null)
		{
			logLevel = LogLevel.DEBUG;
		}
		
		if(mode != null)
		{
			message = (message != null) ? "" : message;
			message = "<b>[" + mode + "]</b> " + message;
		}
		
		addMessage(new ExecutionLogData.ImageMessage( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), logLevel, message, new Date(), 
				imageFile.getFile().getName(), imageFile));
	}
	
	@Override
	public ReportLogFile createFile(String filePrefix, String fileSuffix)
	{
		AutomationContext automationContext = AutomationContext.getInstance();
		return automationContext.newLogFile(filePrefix, fileSuffix);
	}
	
	@Override
	public void logFile(String message, LogLevel logLevel, ReportLogFile file)
	{
		if(disabled)
		{
			return;
		}
		
		if(logLevel == null)
		{
			logLevel = LogLevel.DEBUG;
		}
		
		if(mode != null)
		{
			message = (message != null) ? "" : message;
			message = "<b>[" + mode + "]</b> " + message;
		}
		
		addMessage(new ExecutionLogData.FileMessage( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), logLevel, message, new Date(), file));
	}

	@Override
	public ReportLogFile logFile(String message, LogLevel logLevel, String filePrefix, String fileSuffix)
	{
		if(disabled)
		{
			return null;
		}
		
		if(logLevel == null)
		{
			logLevel = LogLevel.DEBUG;
		}
		
		if(mode != null)
		{
			message = (message != null) ? "" : message;
			message = "<b>[" + mode + "]</b> " + message;
		}
		
		AutomationContext automationContext = AutomationContext.getInstance();
		ReportLogFile tempFile = automationContext.newLogFile(filePrefix, fileSuffix);
		
		addMessage(new ExecutionLogData.FileMessage( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), logLevel, message, new Date(), tempFile));
		return tempFile;
	}
	
	@Override
	public void close(TestStatus status, Date endTime)
	{
		logger.debug("Closing exuection-logger [File: {}, Status: {}]", logFile.getName(), status);
		
		addMessage(new ExecutionLogData.Footer(status, startTime, endTime));
		logWriter.close();
		logWriter = null;
	}
}
