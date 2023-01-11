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
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;
import com.yukthitech.autox.plugin.ui.common.IUiConstants;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Stores the current session cookies into specified file. Helpful in storing the web stored cookies to retain for following
 * sessions.
 * 
 * @author akiran
 */
@Executable(name = "uiStoreCookies", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Stores the current session cookies into specified file.")
public class StoreCookiesStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Path of the file where cookies should be persisted.
	 */
	@Param(description = "Path of the file where cookies should be persisted. Default: " + IUiConstants.COOKIE_FILE, required = false)
	private String path = IUiConstants.COOKIE_FILE;

	/**
	 * Sets the path of the file where cookies should be persisted.
	 *
	 * @param path the new path of the file where cookies should be persisted
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Stroring current cookies into file: {}", path);

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);

		Set<Cookie> cookies = driver.manage().getCookies();
		
		if(cookies == null || cookies.isEmpty())
		{
			exeLogger.debug("No cookies found for persisting.");
			return;
		}
		
		File cookieFile = new File(path);

		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cookieFile));
			oos.writeObject(cookies);
			oos.flush();
			oos.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while storing cookie file: {}", cookieFile.getPath(), ex);
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
		builder.append("Store Cookies [");

		builder.append("Path: ").append(path);

		builder.append("]");
		return builder.toString();
	}
}
