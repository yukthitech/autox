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

import org.apache.logging.log4j.Level;

/**
 * Logging levels.
 * @author akiran
 */
public enum LogLevel
{
	TRACE("  TRACE", Level.TRACE), 
	DEBUG("  DEBUG", Level.DEBUG), 
	WARN("   WARN", Level.WARN),
	ERROR("  ERROR", Level.ERROR),
	INFO("  INFO", Level.INFO),
	
	/**
	 * Log level which would add messages to summary report.
	 */
	SUMMARY("SUMMARY", Level.INFO);
	
	/**
	 * Padding string for this level.
	 */
	private String paddedString;
	
	private Level log4jLevel;

	private LogLevel(String paddedString, Level log4jLevel)
	{
		this.paddedString = paddedString;
		this.log4jLevel = log4jLevel;
	}
	
	/**
	 * Gets the padding string for this level.
	 *
	 * @return the padding string for this level
	 */
	public String getPaddedString()
	{
		return paddedString;
	}
	
	public Level getLog4jLevel()
	{
		return log4jLevel;
	}
}
