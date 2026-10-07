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

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import com.microsoft.playwright.BrowserContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
import com.yukthitech.autox.plugin.ui.common.CookieData;
import com.yukthitech.autox.plugin.ui.common.IUiConstants;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Loads cookies from specified file into current session cookies. The file should have been created using {@link StoreCookiesStep}
 * 
 * @author akiran
 */
@Executable(name = "uiLoadCookies", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Loads cookies from specified file into current session cookies.")
public class LoadCookiesStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Path of the file where cookies should be persisted.
	 */
	@Param(description = "Path of the file where cookies should be loaded from. Default: " + IUiConstants.COOKIE_FILE, required = false, sourceType = SourceType.EXPRESSION)
	private Object path = IUiConstants.COOKIE_FILE;

	/**
	 * Sets the path of the file where cookies should be persisted.
	 *
	 * @param path the new path of the file where cookies should be persisted
	 */
	public void setPath(Object path)
	{
		this.path = path;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		String pathStr = String.valueOf(path);
		exeLogger.trace("Loading cookies from file: {}", pathStr);

		File cookieFile = new File(pathStr);

		if(!cookieFile.exists())
		{
			exeLogger.debug("No cookie file exist at path '{}'. Ignoring load request.", pathStr);
			return;
		}
		
		List<CookieData> cookies = null;
		
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cookieFile)))
		{
			cookies = (List<CookieData>) ois.readObject();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading cookie file: {}", cookieFile.getPath(), ex);
		}

		if(cookies == null || cookies.isEmpty())
		{
			exeLogger.debug("No cookies found in file - {}. Ignoring load request.", pathStr);
			return;
		}
		
		exeLogger.debug("Loading {} cookies from file - {}", cookies.size(), pathStr);
		
		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
		BrowserContext browserContext = session.getBrowserContext(driverName);

		for(CookieData cookie : cookies)
		{
			try
			{
				browserContext.addCookies(java.util.Arrays.asList(cookie.toCookie()));
			}catch(Exception ex)
			{
				exeLogger.debug("Failed to load cookie - {}\nError: {}", cookie, "" + ex);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Load Cookies [");

		builder.append("Path: ").append(path);

		builder.append("]");
		return builder.toString();
	}
}
