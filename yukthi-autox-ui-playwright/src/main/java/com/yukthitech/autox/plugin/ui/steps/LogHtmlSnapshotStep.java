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
package com.yukthitech.autox.plugin.ui.steps;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.google.gson.JsonObject;
import com.microsoft.playwright.CDPSession;
import com.microsoft.playwright.Page;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.LogLevel;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Takes the MHTML snapshot of the current page and adds it to the log.
 * @author akiran
 */
@Executable(name = "uiLogHtmlSnapshot", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Takes current page MHTML snapshot and adds to the log")
public class LogHtmlSnapshotStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the file provided by the user.
	 */
	@Param(description = "Name of the MHTML snapshot file to be created", sourceType = SourceType.EXPRESSION)
	private Object name;
	
	/**
	 * Message to be logged along with the snapshot.
	 */
	@Param(description = "Message to be logged along with the snapshot", required = false, sourceType = SourceType.EXPRESSION)
	private Object message;

	/**
	 * Logging level.
	 */
	@Param(description = "Logging level. Default Value: DEBUG", required = false)
	private LogLevel level = LogLevel.DEBUG;

	/**
	 * Sets the name of the file provided by the user.
	 *
	 * @param fileName the new name of the file provided by the user
	 */
	public void setName(Object fileName) 
	{
		this.name = fileName;
	}
	
	/**
	 * Sets the message to be logged along with the snapshot.
	 *
	 * @param message the new message to be logged along with the snapshot
	 */
	public void setMessage(Object message)
	{
		this.message = message;
	}
	
	/**
	 * Sets the logging level.
	 *
	 * @param level the new logging level
	 */
	public void setLevel(LogLevel level)
	{
		this.level = level;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
		Page page = session.getPage(driverName);
		
		String mhtml = null;
		CDPSession cdpSession = null;
		
		try
		{
			cdpSession = page.context().newCDPSession(page);
			JsonObject result = cdpSession.send("Page.captureSnapshot", null);
			mhtml = result.get("data").getAsString();
		}catch(Exception ex)
		{
			exeLogger.warn("Snapshot could not be taken. CDP based MHTML capture is not supported or failed for the current browser. Error: {}", "" + ex);
			return;
		}finally
		{
			if(cdpSession != null)
			{
				try
				{
					cdpSession.detach();
				}catch(Exception ex)
				{
					// ignore detach failures
				}
			}
		}
	
		String nameStr = String.valueOf(name);
		ReportLogFile reportLogFile = context.newLogFile(nameStr, "mhtml");
		
		try
		{
			Files.writeString(reportLogFile.getFile().toPath(), mhtml, StandardCharsets.UTF_8);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while saving MHTML snapshot to log file: {}", reportLogFile.getFile(), ex);
		}
		
		exeLogger.logFile(message != null ? String.valueOf(message) : null, level, reportLogFile);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Log Html Snapshot [");

		builder.append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}

}
