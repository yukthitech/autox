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

import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Local file monitor to monitor the changes in local file.
 * @author akiran
 */
public class BrowserLogMonitor extends AbstractLogMonitor implements Validateable
{
	/**
	 * Format used to print time in logs.
	 */
	String dateFormat = "dd/MM/yyyy hh:mm:ss aa";
	
	/**
	 * Name of the selenium driver to use.
	 */
	String driverName;
	
	/**
	 * Sets the format used to print time in logs.
	 *
	 * @param dateFormat the new format used to print time in logs
	 */
	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = dateFormat;
	}
	
	public void setDriverName(String driverName)
	{
		this.driverName = driverName;
	}
	
	@Override
	public ILogMonitorSession newSession()
	{
		return new BrowserLogMonitorSession(this);
	}
}
