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
package com.yukthitech.autox.plugin.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Playwright driver configuration.
 * @author akiran
 */
public class PlaywrightDriverConfig implements Validateable
{
	/**
	 * Supported browser types.
	 */
	public static final String BROWSER_CHROMIUM = "chromium";
	
	public static final String BROWSER_FIREFOX = "firefox";
	
	public static final String BROWSER_WEBKIT = "webkit";
	
	/**
	 * Name of the driver.
	 */
	private String name;
	
	/**
	 * Browser type to be used. Supported values: chromium, firefox, webkit. Default: chromium.
	 */
	private String browserType = BROWSER_CHROMIUM;
	
	/**
	 * Flag indicating if this driver is the default driver.
	 */
	private boolean isDefault;
	
	/**
	 * Default page to be opened to ensure driver is in active use.
	 */
	private String defaultPage;
	
	/**
	 * Folder in which downloaded files can be expected.
	 */
	private String downloadFolder;
	
	/**
	 * Flag indicating if browser should be launched in headless mode. Default: false.
	 */
	private boolean headless = false;
	
	/**
	 * Browser distribution channel to be used (like chrome, msedge, chrome-beta). Applicable for chromium.
	 */
	private String channel;
	
	/**
	 * Profile user-data-folder full path to be set for current driver instance. If specified, persistent context will be used.
	 */
	private String userDataDir;
	
	/**
	 * Extra comma separated arguments to be passed to the browser.
	 */
	private String extraArguments;
	
	/**
	 * Slows down playwright operations by specified milliseconds.
	 */
	private double slowMo = 0;

	/**
	 * Gets the name of the driver.
	 *
	 * @return the name of the driver
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the driver.
	 *
	 * @param name the new name of the driver
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public String getBrowserType()
	{
		return browserType;
	}

	public void setBrowserType(String browserType)
	{
		this.browserType = StringUtils.isBlank(browserType) ? BROWSER_CHROMIUM : browserType.trim().toLowerCase();
	}

	/**
	 * Checks if is default.
	 *
	 * @return true, if is default
	 */
	public boolean isDefault()
	{
		return isDefault;
	}

	/**
	 * Sets the default.
	 *
	 * @param isDefault the new default
	 */
	public void setDefault(boolean isDefault)
	{
		this.isDefault = isDefault;
	}

	public String getDefaultPage()
	{
		return defaultPage;
	}

	public void setDefaultPage(String defaultPage)
	{
		this.defaultPage = defaultPage;
	}

	public String getDownloadFolder()
	{
		return downloadFolder;
	}

	public void setDownloadFolder(String downloadFolder)
	{
		File file = new File(downloadFolder);
		
		try
		{
			this.downloadFolder = file.getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while getting cannoical path of download folder: {}", downloadFolder, ex);
		}
	}

	public boolean isHeadless()
	{
		return headless;
	}

	public void setHeadless(boolean headless)
	{
		this.headless = headless;
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel;
	}

	public String getUserDataDir()
	{
		return userDataDir;
	}

	public void setUserDataDir(String userDataDir)
	{
		this.userDataDir = userDataDir;
	}

	public String getExtraArguments()
	{
		return extraArguments;
	}

	public void setExtraArguments(String extraArguments)
	{
		this.extraArguments = extraArguments;
	}
	
	/**
	 * Fetches extra arguments as list, by splitting the comma separated extra-arguments string.
	 * @return extra arguments, which can be empty.
	 */
	public List<String> getExtraArgumentList()
	{
		List<String> args = new ArrayList<>();
		
		if(StringUtils.isBlank(extraArguments))
		{
			return args;
		}
		
		for(String arg : extraArguments.split("\\s*\\,\\s*"))
		{
			if(StringUtils.isNotBlank(arg))
			{
				args.add(arg.trim());
			}
		}
		
		return args;
	}

	public double getSlowMo()
	{
		return slowMo;
	}

	public void setSlowMo(double slowMo)
	{
		this.slowMo = slowMo;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Name can not be null or empty.");
		}
		
		if(!BROWSER_CHROMIUM.equals(browserType) && !BROWSER_FIREFOX.equals(browserType) && !BROWSER_WEBKIT.equals(browserType))
		{
			throw new ValidateException("Invalid browser-type specified: " + browserType + ". Supported types: chromium, firefox, webkit");
		}
	}
}
