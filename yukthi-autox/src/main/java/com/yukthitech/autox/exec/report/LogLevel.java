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

/**
 * Logging levels.
 * @author akiran
 */
public enum LogLevel
{
	TRACE("  TRACE"), 
	DEBUG("  DEBUG"), 
	WARN("   WARN"),
	ERROR("  ERROR"),
	INFO("  INFO"),
	
	/**
	 * Log level which would add messages to summary report.
	 */
	SUMMARY("SUMMARY");
	
	/**
	 * Padding string for this level.
	 */
	private String paddedString;

	private LogLevel(String paddedString)
	{
		this.paddedString = paddedString;
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
}
