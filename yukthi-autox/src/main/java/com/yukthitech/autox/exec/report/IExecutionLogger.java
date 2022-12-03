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

import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.test.TestStatus;

public interface IExecutionLogger
{

	/**
	 * Sets the mode to be prepended for every log message.
	 *
	 * @param mode the new mode to be prepended for every log message
	 */
	void setMode(String mode);

	/**
	 * Clears the mode from the logger.
	 */
	void clearMode();

	/**
	 * Sets the flag indicating if logging is disabled or not.
	 *
	 * @param disabled the new flag indicating if logging is disabled or not
	 */
	void setDisabled(boolean disabled);

	/**
	 * Gets the flag indicating if logging is disabled or not.
	 *
	 * @return the flag indicating if logging is disabled or not
	 */
	boolean isDisabled();

	/**
	 * Used to log error messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void error(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Used to log error messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void error(String mssgTemplate, Object... args);

	/**
	 * Used to log debug messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void debug(String mssgTemplate, Object... args);

	/**
	 * Used to log debug messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void debug(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Used to log info messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void info(String mssgTemplate, Object... args);

	/**
	 * Used to log info messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void info(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Used to log warn messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void warn(String mssgTemplate, Object... args);

	/**
	 * Used to log warn messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void warn(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Used to log trace messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void trace(String mssgTemplate, Object... args);

	/**
	 * Used to log trace messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void trace(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Logs the message at specified level.
	 * @param source location from where logging is being done
	 * @param logLevel level of log
	 * @param mssgTemplate msg template
	 * @param args arguments for message
	 */
	void log(LogLevel logLevel, String mssgTemplate, Object... args);

	/**
	 * Logs the message at specified level.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param logLevel level of log
	 * @param mssgTemplate msg template
	 * @param args arguments for message
	 */
	void log(boolean escapeHtml, LogLevel logLevel, String mssgTemplate, Object... args);

	/**
	 * Adds the specified image file to the debug log.
	 * @param source location from where logging is being done
	 * @param message Message to be logged along with image
	 * @param imageFile Image to be logged
	 * @param logLevel level to be used.
	 */
	void logImage(String message, ReportLogFile imageFile, LogLevel logLevel);

	ReportLogFile createFile(String filePrefix, String fileSuffix);

	void logFile(String message, LogLevel logLevel, ReportLogFile file);

	ReportLogFile logFile(String message, LogLevel logLevel, String filePrefix, String fileSuffix);

	void close(TestStatus status, Date endTime);

}