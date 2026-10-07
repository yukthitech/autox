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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.WebError;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;

/**
 * Captures browser console output (console messages and uncaught page errors) of all the
 * pages of the browser context, between start and stop of monitoring.
 */
public class BrowserLogMonitorSession implements ILogMonitorSession
{
	private static Logger logger = LogManager.getLogger(BrowserLogMonitorSession.class);
	
	/**
	 * Captured log entry.
	 */
	private static class LogEntry
	{
		private String level;
		
		private long timestamp;
		
		private String message;

		public LogEntry(String level, long timestamp, String message)
		{
			this.level = level;
			this.timestamp = timestamp;
			this.message = message;
		}
	}

	private BrowserLogMonitor parentMonitor;
	
	/**
	 * Entries captured since monitoring started.
	 */
	private final List<LogEntry> entries = new ArrayList<>();
	
	/**
	 * Browser context on which listeners are registered currently.
	 */
	private BrowserContext monitoredContext;
	
	/**
	 * Page used to pump pending events before draining.
	 */
	private Page monitoredPage;
	
	private Consumer<ConsoleMessage> consoleListener = this::onConsoleMessage;
	
	private Consumer<WebError> errorListener = this::onWebError;

	public BrowserLogMonitorSession(BrowserLogMonitor parentMonitor)
	{
		this.parentMonitor = parentMonitor;
	}

	public BrowserLogMonitor getParentMonitor()
	{
		return parentMonitor;
	}
	
	private void onConsoleMessage(ConsoleMessage message)
	{
		String text = message.text();
		
		if(text == null || text.isBlank())
		{
			return;
		}
		
		synchronized(entries)
		{
			entries.add(new LogEntry(mapConsoleTypeToLevel(message.type()), System.currentTimeMillis(), text));
		}
	}
	
	private void onWebError(WebError webError)
	{
		String text = webError.error();
		
		if(text == null || text.isBlank())
		{
			return;
		}
		
		synchronized(entries)
		{
			entries.add(new LogEntry("SEVERE", System.currentTimeMillis(), text));
		}
	}

	@Override
	public void startMonitoring()
	{
		if(!parentMonitor.isEnabled())
		{
			logger.warn("As this log monitor is not enabled, skipping start-monitor call");
			return;
		}

		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);

		if(session == null)
		{
			logger.warn("As playwright-plugin is not enabled, the request for monitoring browser-log is ignored.");
			return;
		}
		
		//if listeners are registered on some older context, remove them
		unregisterListeners();

		try
		{
			BrowserContext browserContext = session.getBrowserContext(parentMonitor.driverName);
			
			synchronized(entries)
			{
				entries.clear();
			}
			
			browserContext.onConsoleMessage(consoleListener);
			browserContext.onWebError(errorListener);
			
			monitoredContext = browserContext;
			monitoredPage = session.getPage(parentMonitor.driverName);
		}catch(Exception ex)
		{
			logger.error("An error occurred while starting browser console monitoring", ex);
		}
	}
	
	private void unregisterListeners()
	{
		if(monitoredContext == null)
		{
			return;
		}
		
		try
		{
			monitoredContext.offConsoleMessage(consoleListener);
			monitoredContext.offWebError(errorListener);
		}catch(Exception ex)
		{
			//context might have been closed already
			logger.debug("Failed to remove console listeners. Error: {}", "" + ex);
		}
		
		monitoredContext = null;
	}

	@Override
	public List<ReportLogFile> stopMonitoring()
	{
		if(monitoredContext == null)
		{
			logger.warn("As monitoring was not started on any browser context, no log file is being generated.");
			return null;
		}
		
		//playwright events are processed during api calls, so make a dummy call to pump any pending console events
		try
		{
			if(monitoredPage != null && !monitoredPage.isClosed())
			{
				monitoredPage.evaluate("1");
			}
		}catch(Exception ex)
		{
			logger.debug("Failed to pump pending events. Error: {}", "" + ex);
		}
		
		unregisterListeners();
		monitoredPage = null;
		
		List<LogEntry> capturedEntries = null;
		
		synchronized(entries)
		{
			capturedEntries = new ArrayList<>(entries);
			entries.clear();
		}

		if(capturedEntries.isEmpty())
		{
			logger.debug("As there is no content, returning null from this log monitor");
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		SimpleDateFormat dateFormat = new SimpleDateFormat(parentMonitor.dateFormat);
		String template = "%s [%s] - %s";

		for(LogEntry entry : capturedEntries)
		{
			builder.append(String.format(template, entry.level, dateFormat.format(new Date(entry.timestamp)), entry.message)).append("\n");
		}

		return writeLogFile(builder.toString());
	}

	private List<ReportLogFile> writeLogFile(String content)
	{
		try
		{
			AutomationContext context = AutomationContext.getInstance();
			ReportLogFile tempFile = context.newLogFile(parentMonitor.getName(), ".log");
			FileUtils.write(tempFile.getFile(), content, Charset.defaultCharset());
			return Arrays.asList(tempFile);
		}catch(Exception ex)
		{
			logger.error("An error occurred while creating monitoring log.", ex);
			return null;
		}
	}

	private static String mapConsoleTypeToLevel(String type)
	{
		if(type == null)
		{
			return "INFO";
		}

		switch(type.toLowerCase())
		{
			case "error":
			case "assert":
				return "SEVERE";
			case "warning":
			case "warn":
				return "WARNING";
			case "info":
			case "log":
			case "dir":
			case "dirxml":
			case "table":
				return "INFO";
			case "debug":
			case "trace":
				return "FINE";
			default:
				return "INFO";
		}
	}
}
