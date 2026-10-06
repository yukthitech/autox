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

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.test.TestStatus;

public class Log4jExecutionLogger implements IExecutionLogger
{
	private static Logger logger = LogManager.getLogger(Log4jExecutionLogger.class);
	
	private static Log4jExecutionLogger instance = new Log4jExecutionLogger();
	
	public static Log4jExecutionLogger getInstance()
	{
		return instance;
	}
	
	@Override
	public void setMode(String mode)
	{
	}

	@Override
	public void clearMode()
	{
	}

	@Override
	public void setDisabled(boolean disabled)
	{
	}

	@Override
	public boolean isDisabled()
	{
		return false;
	}

	@Override
	public void error(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.error(mssgTemplate, args);
	}

	@Override
	public void error(String mssgTemplate, Object... args)
	{
		logger.error(mssgTemplate, args);
	}

	@Override
	public void debug(String mssgTemplate, Object... args)
	{
		logger.debug(mssgTemplate, args);
	}

	@Override
	public void debug(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.debug(mssgTemplate, args);
	}

	@Override
	public void info(String mssgTemplate, Object... args)
	{
		logger.info(mssgTemplate, args);
	}

	@Override
	public void info(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.info(mssgTemplate, args);
	}

	@Override
	public void warn(String mssgTemplate, Object... args)
	{
		logger.warn(mssgTemplate, args);
	}

	@Override
	public void warn(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.warn(mssgTemplate, args);
	}

	@Override
	public void trace(String mssgTemplate, Object... args)
	{
		logger.trace(mssgTemplate, args);
	}

	@Override
	public void trace(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.trace(mssgTemplate, args);
	}

	@Override
	public void log(LogLevel logLevel, String mssgTemplate, Object... args)
	{
		logger.debug(mssgTemplate, args);
	}

	@Override
	public void log(boolean escapeHtml, LogLevel logLevel, String mssgTemplate, Object... args)
	{
		logger.debug(mssgTemplate, args);
	}

	@Override
	public void logImage(String message, ReportLogFile imageFile, LogLevel logLevel)
	{
		logger.debug(message);
	}

	@Override
	public ReportLogFile createFile(String filePrefix, String fileSuffix)
	{
		return null;
	}

	@Override
	public void logFile(String message, LogLevel logLevel, ReportLogFile file)
	{
		logger.debug(message);
	}

	@Override
	public ReportLogFile logFile(String message, LogLevel logLevel, String filePrefix, String fileSuffix)
	{
		logger.debug(message);
		return null;
	}

	@Override
	public void close(TestStatus status, Date endTime)
	{
	}
}
