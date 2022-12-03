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

import java.util.List;

import com.yukthitech.autox.context.ReportLogFile;

/**
 * Session of log monitor. Framework will ensure only one thread will access one session.
 * @author akranthikiran
 */
public interface ILogMonitorSession
{
	public ILogMonitor getParentMonitor();
	
	/**
	 * Before starting test case, {@link #startMonitoring()} method would be invoked which is expected to mark start location on target log.
	 */
	public void startMonitoring();
	
	/**
	 * After test case execution is completed {@link #stopMonitoring()} method would be invoked which should fetch the log from start location 
	 * till current point.
	 * @param context current context
	 * @return Log files content from start location till current point.
	 */
	public List<ReportLogFile> stopMonitoring();
}
