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
package com.yukthitech.autox.config;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.exec.report.LogLevel;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class SeleniumPluginSession extends AbstractPluginSession<SeleniumPluginSession, SeleniumPlugin>
{
	private static Logger logger = LogManager.getLogger(SeleniumPluginSession.class);
	
	private Map<String, WebDriver> drivers = new HashMap<>();
	
	/**
	 * Main window handler.
	 */
	private Map<String, String> mainWindowHandles = new HashMap<>();
	
	private String defaultDriverName;
	
	private String sessionId = UUID.randomUUID().toString();
	
	public SeleniumPluginSession(SeleniumPlugin parentPlugin, String defaultDriverName)
	{
		super(parentPlugin);
		this.defaultDriverName = defaultDriverName;
	}
	
	/**
	 * Gets the resource url.
	 *
	 * @param resource the resource
	 * @return the resource url
	 */
	public String getResourceUrl(String resource)
	{
		if(!resource.startsWith("/"))
		{
			resource = "/" + resource;
		}
		
		return parentPlugin.getBaseUrl() + resource;
	}
	
	private WebDriver resetActiveDriver(String name) throws Exception
	{
		SeleniumDriverConfig driverConfig = parentPlugin.getDriverConfig(name);
		
		Class<?> driverClass = Class.forName(driverConfig.getClassName());
		WebDriver newDriver = null;
		String actDownloadFolder = driverConfig.getDownloadFolder();
		
		try
		{
			Constructor<?> configConstr = driverClass.getConstructor(SeleniumDriverConfig.class);
			
			if(actDownloadFolder != null)
			{
				File dwnldFolder = new File(actDownloadFolder, sessionId);
				FileUtils.forceMkdir(dwnldFolder);
				
				driverConfig.setDownloadFolder(dwnldFolder.getPath());
			}
			
			newDriver = (WebDriver) configConstr.newInstance(driverConfig);
		} catch(NoSuchMethodException ex)
		{
			newDriver = (WebDriver) driverClass.newInstance();					
		} finally
		{
			if(actDownloadFolder != null)
			{
				driverConfig.setDownloadFolder(actDownloadFolder);
			}
		}
		
		
		if(driverConfig.getDefaultPage() != null)
		{
			logger.debug("Taking driver to default page: " + driverConfig.getDefaultPage());
			newDriver.get(driverConfig.getDefaultPage());
		}
		
		WebDriver oldDriver = drivers.get(name);
		
		if(oldDriver != null)
		{
			try
			{
				oldDriver.close();
				oldDriver.quit();
			}catch(Exception ex)
			{
				//ignore old driver close errors
			}
		}

		drivers.put(name, newDriver);
		
		String mainWindowHandle = newDriver.getWindowHandle();
		
		if(mainWindowHandle != null)
		{
			this.mainWindowHandles.put(name, mainWindowHandle);
		}
		
		return newDriver;
	}
	
	/**
	 * Gets the main window handler.
	 *
	 * @return the main window handler
	 */
	public String getMainWindowHandle(String name)
	{
		name = (name == null) ? defaultDriverName : name;
		return mainWindowHandles.get(name);
	}
	
	/**
	 * Fetches the current web driver.
	 * @return current web driver.
	 */
	public WebDriver getWebDriver(String name)
	{
		name = (name == null) ? defaultDriverName : name;
		WebDriver driver = this.drivers.get(name);
		
		if(driver != null)
		{
			return driver;
		}
		
		try
		{
			return resetActiveDriver(name);
		} catch(RuntimeException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new InvalidStateException("Failed to initialize driver with name: " + name, ex);
		}
	}
	
	/**
	 * Recreates the driver object. Note this method will not close
	 * the existing driver.
	 */
	public void resetDriver(String name)
	{
		name = (name == null) ? defaultDriverName : name;
		
		try
		{
			resetActiveDriver(name);
		} catch(RuntimeException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new InvalidStateException("Failed to initialize driver with name: " + name, ex);
		}
	}
	
	/**
	 * Returns true if downloads are supported by current driver.
	 * @return true if download automation is supported.
	 */
	public boolean isDownloadsSupported(String name)
	{
		name = (name == null) ? defaultDriverName : name;
		
		SeleniumDriverConfig driverConfig = parentPlugin.getDriverConfig(name);
		return (driverConfig.getDownloadFolder() != null);
	}
	
	/**
	 * Fetches folder path when downloaded files can be expected.
	 * @return
	 */
	public String getDownloadFolder(String name)
	{
		name = (name == null) ? defaultDriverName : name;
		SeleniumDriverConfig driverConfig = parentPlugin.getDriverConfig(name);

		String folder = driverConfig.getDownloadFolder();
		
		if(folder == null)
		{
			return null;
		}
		
		return new File(folder, sessionId).getPath();
	}
	
	/**
	 * Cleans the download folder.
	 */
	public void cleanDownloadFolder(String name)
	{
		String downloadFolder = getDownloadFolder(name); 
				
		if(downloadFolder == null)
		{
			return;
		}
		
		try
		{
			File folder = new File(downloadFolder);
			
			if(folder.exists())
			{
				FileUtils.forceDelete(folder);
			}
			
			FileUtils.forceMkdir(folder);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to clean download folder: {}", downloadFolder, ex);
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.config.IPlugin#handleError(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.config.ErrorDetails)
	 */
	@Override
	public void handleError(AutomationContext context, ErrorDetails errorDetails)
	{
		if(drivers.isEmpty())
		{
			return;
		}

		for(Map.Entry<String, WebDriver> driverEntry : drivers.entrySet())
		{
			try
			{
				WebDriver activeDriver = driverEntry.getValue();
				
				File file = ((TakesScreenshot) activeDriver).getScreenshotAs(OutputType.FILE);
				ReportLogFile reportLogFile = context.newLogFile("error-screenshot", FilenameUtils.getExtension(file.getName()));
				reportLogFile.copyContent(file);
				
				errorDetails.getExecutionLogger().logImage(
						String.format("Screen shot during error. [Driver name: %s]", driverEntry.getKey()), 
						reportLogFile, LogLevel.ERROR);
				
				errorDetails.getExecutionLogger().error("During error browser details are: [Postition: {}, Size: {}]",
						activeDriver.manage().window().getPosition(),
						activeDriver.manage().window().getSize());
			}catch(NoSuchSessionException ex)
			{
				logger.warn("Found the session to be closed, so skipping taking screen shot");
			}
		}
	}
	
	public void closeDriver(String driverName)
	{
		WebDriver driver = this.drivers.remove(driverName);
		
		if(driver == null)
		{
			return;
		}
		
		driver.close();
		driver.quit();
	}
	
	@Override
	public void close()
	{
		for(WebDriver driver : this.drivers.values())
		{
			try
			{
				driver.close();
				driver.quit();
			}catch(NoSuchSessionException ex)
			{
				//ignore if session is already closed
			}
		}
		
		drivers.clear();
	}
}
