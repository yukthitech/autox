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
package com.yukthitech.autox.logmon;

/**
 * Interface representing log monitors. Used to fetch logs from different sources for test cases.
 * 
 * Before starting test case, {@link #startMonitoring()} method would be invoked which is expected to mark start location on target log.
 * 
 * After test case execution is completed {@link #stopMonitoring()} method would be invoked which should fetch the log from start location till current point.
 * 
 * @author akiran
 */
public interface ILogMonitor
{
	/**
	 * Fetches the name of the log monitor.
	 * @return name
	 */
	public String getName();
	
	/**
	 * Checks if is if this flag is set, then only on error, the log will be fetched and added to report. Defaults to true.
	 *
	 * @return the if this flag is set, then only on error, the log will be fetched and added to report
	 */
	public boolean isOnErrorOnly();
	
	/**
	 * Checks if is flag indicating whether this log monitor is enabled or not.
	 *
	 * @return the flag indicating whether this log monitor is enabled or not
	 */
	public boolean isEnabled();
	
	public ILogMonitorSession newSession();
}
