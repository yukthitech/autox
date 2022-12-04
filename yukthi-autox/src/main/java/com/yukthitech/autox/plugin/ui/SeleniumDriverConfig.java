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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Selenium driver configuration.
 * @author akiran
 */
public class SeleniumDriverConfig implements Validateable
{
	/**
	 * Name of the driver.
	 */
	private String name;
	
	/**
	 * Class name of the driver.
	 */
	private String className;
	
	/**
	 * Flag indicating if this driver is the default driver.
	 */
	private boolean isDefault;
	
	/**
	 * Default page to be opened to ensure driver is in active use. This is mainly required
	 * by headless browser (jbrowser).
	 */
	private String defaultPage;
	
	/**
	 * Folder in which downloaded files can be expected.
	 */
	private String downloadFolder;
	
	/**
	 * Profile options to be used by drivers.
	 */
	private LinkedHashMap<String, Object> profileOptions = new LinkedHashMap<>();
	
	/**
	 * Profile user-data-folder full path to be set for current driver instance.
	 */
	private String userDataDir;
	
	/**
	 * Profile folder to be used within data folder.
	 */
	private String profileFolder;

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

	/**
	 * Gets the class name of the driver.
	 *
	 * @return the class name of the driver
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * Sets the class name of the driver.
	 *
	 * @param className the new class name of the driver
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}

	/**
	 * Adds specified system property for this driver.
	 * @param name Name of the property.
	 * @param value value of the property.
	 */
	public void setSystemProperty(String name, String value)
	{
		System.setProperty(name, value);
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

	/**
	 * Gets the default page to be opened to ensure driver is in active use. This is mainly required by headless browser (jbrowser).
	 *
	 * @return the default page to be opened to ensure driver is in active use
	 */
	public String getDefaultPage()
	{
		return defaultPage;
	}

	/**
	 * Sets the default page to be opened to ensure driver is in active use. This is mainly required by headless browser (jbrowser).
	 *
	 * @param defaultPage the new default page to be opened to ensure driver is in active use
	 */
	public void setDefaultPage(String defaultPage)
	{
		this.defaultPage = defaultPage;
	}

	/**
	 * Gets the folder in which downloaded files can be expected.
	 *
	 * @return the folder in which downloaded files can be expected
	 */
	public String getDownloadFolder()
	{
		return downloadFolder;
	}

	/**
	 * Sets the folder in which downloaded files can be expected.
	 *
	 * @param downloadFolder the new folder in which downloaded files can be expected
	 */
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
	
	public void addBooleanProfileOption(String name, boolean value)
	{
		this.profileOptions.put(name, value);
	}
	
	public void addIntProfileOption(String name, Integer value)
	{
		this.profileOptions.put(name, value);
	}

	public void addProfileOption(String name, String value)
	{
		this.profileOptions.put(name, value);
	}
	
	public Map<String, Object> getProfileOptions()
	{
		return profileOptions;
	}
	
	public String getProfileFolder()
	{
		return profileFolder;
	}

	public void setProfileFolder(String profileFolder)
	{
		this.profileFolder = profileFolder;
	}
	
	public String getUserDataDir()
	{
		return userDataDir;
	}

	public void setUserDataDir(String userDataDir)
	{
		this.userDataDir = userDataDir;
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
		
		if(StringUtils.isBlank(className))
		{
			throw new ValidateException("Class-name can not be null or empty.");
		}
	}
}
