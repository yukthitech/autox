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

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;

import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ReportLogFile;

public class BrowserLogMonitorSession implements ILogMonitorSession
{
	private static Logger logger = LogManager.getLogger(BrowserLogMonitorSession.class);
	
	/**
	 * Web driver captured during last start monitoring.
	 */
	private WebDriver currentWebDriver;
	
	/**
	 * Selenium logs object that is captured during last invocation.
	 */
	private Logs currentLogs;

	private BrowserLogMonitor parentMonitor;

	public BrowserLogMonitorSession(BrowserLogMonitor parentMonitor)
	{
		this.parentMonitor = parentMonitor;
	}
	
	public BrowserLogMonitor getParentMonitor()
	{
		return parentMonitor;
	}
	
	@Override
	public void startMonitoring()
	{
		if(!parentMonitor.isEnabled())
		{
			logger.warn("As this log monitor is not enabled, skipping start-monitor call");
			return;
		}

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class); 
		
		if(seleniumSession == null)
		{
			logger.warn("As selenium-plugin is not enabled, the request for monitoring browser-log is ignored.");
			return;
		}
		
		WebDriver driver = seleniumSession.getWebDriver(parentMonitor.driverName);
		
		if(currentWebDriver != driver || currentLogs == null)
		{
			try
			{
				currentLogs = driver.manage().logs();
			}catch(Exception ex)
			{
				logger.error("An error occurred while fetching logs object from web-driver", ex);
				return;
			}
			
			if(currentLogs == null)
			{
				logger.warn("As no logs object could be obtained from webdriver, request for monitoring browser logs ignored");
				return;
			}
			
			currentWebDriver = driver;
		}
		
		//this will clean the browser logs available till now
		try
		{
			currentLogs.get(LogType.BROWSER);
		}catch(Exception ex)
		{
			logger.debug("Ignoring error that occurred, while trying to cleanup browser logs, during start of log monitor");
		}
	}

	@Override
	public List<ReportLogFile> stopMonitoring()
	{
		if(currentLogs == null)
		{
			logger.warn("As current logs object is not available for webdriver, no log file is being generated.");
			return null;
		}
		
		AutomationContext context = AutomationContext.getInstance();
		ReportLogFile tempFile = context.newLogFile(parentMonitor.getName(), ".log");
		
		try
		{
			LogEntries logEntries = currentLogs.get(LogType.BROWSER);
			
			if(logEntries == null)
			{
				logger.debug("As there is no log entries, returning null from this log monitor");
				return null;
			}

			StringBuilder builder = new StringBuilder();
			String template = "%s [%s] - %s";
			String mssg = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat(parentMonitor.dateFormat);

			for (LogEntry entry : logEntries) 
			{
				mssg = String.format(template, entry.getLevel().getName(), dateFormat.format(new Date(entry.getTimestamp())), entry.getMessage());
				builder.append(mssg).append("\n");
			}
			
			if(builder.length() == 0)
			{
				logger.debug("As there is no content, returning null from this log monitor");
				return null;
			}

			FileUtils.write(tempFile.getFile(), mssg, Charset.defaultCharset());
		}catch(Exception ex)
		{
			logger.error("An error occurred while creating monitoring log.", ex);
			return null;
		}
		
		return Arrays.asList(tempFile);
	}
}
