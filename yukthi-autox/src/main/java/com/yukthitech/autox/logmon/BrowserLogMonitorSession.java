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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;

/**
 * Captures browser console output for the active testcase window.
 * <p>
 * Prefer an in-page console hook (via CDP {@code Page.addScriptToEvaluateOnNewDocument}
 * + {@link JavascriptExecutor}) so that {@code console.error(Error)} and similar calls
 * retain {@code Error.stack} — something Selenium's classic {@code LogType.BROWSER} API
 * and the simplified CDP {@code ConsoleEvent} mapping both drop.
 * <p>
 * Falls back to classic Selenium browser logs (with JSON-argument decoding) when the
 * hook cannot be installed.
 */
public class BrowserLogMonitorSession implements ILogMonitorSession
{
	private static Logger logger = LogManager.getLogger(BrowserLogMonitorSession.class);

	/**
	 * In-page hook that mirrors DevTools console formatting, including Error stacks.
	 * Stored logs are drained at {@link #stopMonitoring()}.
	 */
	private static final String CONSOLE_HOOK_SCRIPT =
			"(function(){\n"
			+ "  if (window.__autoxConsoleHooked) {\n"
			+ "    return;\n"
			+ "  }\n"
			+ "  window.__autoxConsoleHooked = true;\n"
			+ "  window.__autoxConsoleLogs = window.__autoxConsoleLogs || [];\n"
			+ "  function formatArgs(args) {\n"
			+ "    var out = [];\n"
			+ "    for (var i = 0; i < args.length; i++) {\n"
			+ "      var a = args[i];\n"
			+ "      if (a instanceof Error) {\n"
			+ "        out.push(a.stack || (a.name + ': ' + a.message));\n"
			+ "      } else if (typeof a === 'string') {\n"
			+ "        out.push(a);\n"
			+ "      } else if (a === null || a === undefined) {\n"
			+ "        out.push(String(a));\n"
			+ "      } else {\n"
			+ "        try { out.push(JSON.stringify(a)); } catch (e) { out.push(String(a)); }\n"
			+ "      }\n"
			+ "    }\n"
			+ "    return out.join(' ');\n"
			+ "  }\n"
			+ "  function push(level, args) {\n"
			+ "    try {\n"
			+ "      window.__autoxConsoleLogs.push({\n"
			+ "        level: level,\n"
			+ "        time: Date.now(),\n"
			+ "        message: formatArgs(args)\n"
			+ "      });\n"
			+ "    } catch (e) {}\n"
			+ "  }\n"
			+ "  ['log','info','warn','error','debug'].forEach(function(level) {\n"
			+ "    var original = console[level];\n"
			+ "    if (typeof original !== 'function') {\n"
			+ "      return;\n"
			+ "    }\n"
			+ "    console[level] = function() {\n"
			+ "      push(level, arguments);\n"
			+ "      return original.apply(console, arguments);\n"
			+ "    };\n"
			+ "  });\n"
			+ "  window.addEventListener('error', function(event) {\n"
			+ "    var msg = (event.error && event.error.stack)\n"
			+ "      ? event.error.stack\n"
			+ "      : (event.message || 'Error');\n"
			+ "    try {\n"
			+ "      window.__autoxConsoleLogs.push({ level: 'error', time: Date.now(), message: msg });\n"
			+ "    } catch (e) {}\n"
			+ "  });\n"
			+ "  window.addEventListener('unhandledrejection', function(event) {\n"
			+ "    var reason = event.reason;\n"
			+ "    var msg = (reason && reason.stack) ? reason.stack : String(reason);\n"
			+ "    try {\n"
			+ "      window.__autoxConsoleLogs.push({ level: 'error', time: Date.now(), message: msg });\n"
			+ "    } catch (e) {}\n"
			+ "  });\n"
			+ "})();";

	private static final String CLEAR_LOGS_SCRIPT = "window.__autoxConsoleLogs = [];";

	private static final String DRAIN_LOGS_SCRIPT =
			"var logs = window.__autoxConsoleLogs || [];"
			+ " window.__autoxConsoleLogs = [];"
			+ " return logs;";

	/**
	 * Web driver captured during last start monitoring.
	 */
	private WebDriver currentWebDriver;

	/**
	 * Selenium logs object used for classic fallback.
	 */
	private Logs currentLogs;

	/**
	 * When true, logs are collected via the in-page console hook.
	 */
	private boolean usingJsHook;

	/**
	 * Whether {@link #CONSOLE_HOOK_SCRIPT} was registered for new documents on this driver.
	 */
	private boolean jsHookRegisteredForDriver;

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

		if(currentWebDriver != driver)
		{
			jsHookRegisteredForDriver = false;
			usingJsHook = false;
			currentLogs = null;
			currentWebDriver = driver;
		}

		if(tryStartJsHookMonitoring(driver))
		{
			return;
		}

		startClassicMonitoring(driver);
	}

	private boolean tryStartJsHookMonitoring(WebDriver driver)
	{
		if(!(driver instanceof JavascriptExecutor))
		{
			return false;
		}

		JavascriptExecutor js = (JavascriptExecutor) driver;

		try
		{
			if(!jsHookRegisteredForDriver && driver instanceof HasCdp)
			{
				Map<String, Object> params = new HashMap<>();
				params.put("source", CONSOLE_HOOK_SCRIPT);
				((HasCdp) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", params);
				jsHookRegisteredForDriver = true;
				logger.debug("Registered browser console capture hook for new documents via CDP");
			}

			// Install / ensure hook on the current document and clear prior entries.
			js.executeScript(CONSOLE_HOOK_SCRIPT);
			js.executeScript(CLEAR_LOGS_SCRIPT);

			usingJsHook = true;
			return true;
		}catch(Exception ex)
		{
			logger.warn("Unable to install in-page browser console hook, falling back to classic browser logs", ex);
			usingJsHook = false;
			return false;
		}
	}

	private void startClassicMonitoring(WebDriver driver)
	{
		usingJsHook = false;

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
		if(usingJsHook)
		{
			return stopJsHookMonitoring();
		}

		return stopClassicMonitoring();
	}

	@SuppressWarnings("unchecked")
	private List<ReportLogFile> stopJsHookMonitoring()
	{
		if(!(currentWebDriver instanceof JavascriptExecutor))
		{
			logger.warn("JavascriptExecutor not available while stopping browser console hook monitoring");
			return null;
		}

		try
		{
			Object raw = ((JavascriptExecutor) currentWebDriver).executeScript(DRAIN_LOGS_SCRIPT);
			List<Map<String, Object>> entries = toLogMaps(raw);

			if(entries.isEmpty())
			{
				logger.debug("As there is no content, returning null from this log monitor");
				return null;
			}

			StringBuilder builder = new StringBuilder();
			SimpleDateFormat dateFormat = new SimpleDateFormat(parentMonitor.dateFormat);
			String template = "%s [%s] - %s";

			for(Map<String, Object> entry : entries)
			{
				String level = mapConsoleTypeToLevel(stringVal(entry.get("level")));
				long timestamp = toEpochMilli(entry.get("time"));
				String message = stringVal(entry.get("message"));

				if(message == null || message.isBlank())
				{
					continue;
				}

				builder.append(String.format(template, level, dateFormat.format(new Date(timestamp)), message)).append("\n");
			}

			if(builder.length() == 0)
			{
				logger.debug("As there is no content, returning null from this log monitor");
				return null;
			}

			return writeLogFile(builder.toString());
		}catch(Exception ex)
		{
			logger.error("An error occurred while draining in-page browser console logs", ex);
			return null;
		}
	}

	private List<ReportLogFile> stopClassicMonitoring()
	{
		if(currentLogs == null)
		{
			logger.warn("As current logs object is not available for webdriver, no log file is being generated.");
			return null;
		}

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
			SimpleDateFormat dateFormat = new SimpleDateFormat(parentMonitor.dateFormat);

			for(LogEntry entry : logEntries)
			{
				String formattedMessage = BrowserConsoleLogFormatter.format(entry.getMessage());
				String mssg = String.format(template, entry.getLevel().getName(), dateFormat.format(new Date(entry.getTimestamp())), formattedMessage);
				builder.append(mssg).append("\n");
			}

			if(builder.length() == 0)
			{
				logger.debug("As there is no content, returning null from this log monitor");
				return null;
			}

			return writeLogFile(builder.toString());
		}catch(Exception ex)
		{
			logger.error("An error occurred while creating monitoring log.", ex);
			return null;
		}
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

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> toLogMaps(Object raw)
	{
		if(!(raw instanceof List))
		{
			return Collections.emptyList();
		}

		List<?> list = (List<?>) raw;
		List<Map<String, Object>> result = new ArrayList<>(list.size());

		for(Object item : list)
		{
			if(item instanceof Map)
			{
				result.add((Map<String, Object>) item);
			}
		}

		return result;
	}

	private static String stringVal(Object value)
	{
		return value == null ? null : String.valueOf(value);
	}

	private static long toEpochMilli(Object value)
	{
		if(value instanceof Number)
		{
			return ((Number) value).longValue();
		}

		if(value instanceof String)
		{
			try
			{
				return Long.parseLong((String) value);
			}catch(NumberFormatException ex)
			{
				// fall through
			}
		}

		return System.currentTimeMillis();
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
