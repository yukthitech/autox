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
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = "uiClickAndDownload", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Clicks the specified target and download the result file. If no  file is downloaded, this will throw exception.")
public class ClickAndDownloadStep extends AbstractParentUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator for button.
	 */
	@Param(description = "Locator of the element to be triggered. Out of located elements, first element will be clicked.", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * Attribute name which would be set with the downloaded file path.
	 */
	@Param(description = "Attribute name which would be set with the downloaded file path.")
	private String pathName;
	
	/**
	 * Time to wait for download to complete in millis. Default: 20000.
	 */
	@Param(description = "Time to wait for download to complete in millis. Default: 30000")
	private long downloadWaitTime = 30000;
	
	
	/**
	 * Number of retries to happen. Default: 5
	 */
	@Param(description = "Number of retries to happen. Default: 5", required = false)
	private int retryCount = 5;
	
	/**
	 * Time gap between retries.
	 */
	@Param(description = "Time gap between retries. Default: 1000", required = false)
	private int retryTimeGapMillis = IAutomationConstants.ONE_SECOND;
	
	/**
	 * Comma separated supported extensions. If specified, once file is found with any of these extensions, the wait will end.
	 */
	@Param(description = "Comma separated supported extensions. If specified, once file is found with any of these extensions, the wait will end.", required = false)
	private String extensions;

	/**
	 * Sets the number of retries to happen. Default: 5.
	 *
	 * @param retryCount the new number of retries to happen
	 */
	public void setRetryCount(int retryCount)
	{
		this.retryCount = retryCount;
	}
	
	/**
	 * Sets the time gap between retries.
	 *
	 * @param retryTimeGapMillis the new time gap between retries
	 */
	public void setRetryTimeGapMillis(int retryTimeGapMillis)
	{
		this.retryTimeGapMillis = retryTimeGapMillis;
	}

	/**
	 * Sets the attribute name which would be set with the downloaded file path.
	 *
	 * @param pathName the new attribute name which would be set with the downloaded file path
	 */
	public void setPathName(String pathName)
	{
		this.pathName = pathName;
	}
	
	/**
	 * Sets the time to wait for download to complete in millis. Default: 20000.
	 *
	 * @param downloadWaitTime the new time to wait for download to complete in millis
	 */
	public void setDownloadWaitTime(long downloadWaitTime)
	{
		this.downloadWaitTime = downloadWaitTime;
	}
	
	/**
	 * Sets the locator for button.
	 *
	 * @param locator
	 *            the new locator for button
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}
	
	public void setExtensions(String extensions)
	{
		this.extensions = extensions;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		
		if(!seleniumSession.isDownloadsSupported(driverName))
		{
			throw new InvalidStateException("Current driver does not support download automation"); 
		}
		
		seleniumSession.cleanDownloadFolder(driverName);
		
		exeLogger.trace("For download clicking the element specified by locator: {}", getLocatorWithParent(locator));

		try
		{
			UiAutomationUtils.validateWithWait(() -> 
			{
				WebElement webElement = UiAutomationUtils.findElement(driverName, super.parentElement, locator);

				if(webElement == null)
				{
					exeLogger.error("Failed to find element with locator: {}", getLocatorWithParent(locator));
					throw new NullPointerException("Failed to find element with locator: " + getLocatorWithParent(locator));
				}

				try
				{
					webElement.click();
					return true;
				} catch(RuntimeException ex)
				{
					if(ex.getMessage().toLowerCase().contains("not clickable"))
					{
						return false;
					}
	
					throw ex;
				}
			} , retryCount, retryTimeGapMillis,
					"Waiting for element to be clickable: " + getLocatorWithParent(locator), 
					new InvalidStateException("Failed to click element - " + getLocatorWithParent(locator)));
			
			File file = null;
			
			if(StringUtils.isBlank(extensions))
			{
				file = waitAndDownload(seleniumSession.getDownloadFolder(driverName));
			}
			else
			{
				file = pollAndDownload(seleniumSession.getDownloadFolder(driverName), extensions);
			}
			
			if(file == null)
			{
				throw new InvalidStateException("No file found in download folder: {}", seleniumSession.getDownloadFolder(driverName));
			}

			exeLogger.trace("Setting downloaded file path '{}' on context with name: {}", file.getPath(), pathName);
			context.setAttribute(pathName, file.getPath());
		}catch(InvalidStateException ex)
		{
			//exeLogger.error(ex, "Failed to click element - {}", getLocatorWithParent(locator));
			throw new TestCaseFailedException(this, "Failed to click element - {}", getLocatorWithParent(locator), ex);
		}
	}
	
	private File pollAndDownload(String downloadFolderPath, String extensions)
	{
		Set<String> extSet = CommonUtils.toSet(extensions.trim().toLowerCase().split("\\s*\\,\\s*"));
		ObjectWrapper<File> fileRes = new ObjectWrapper<File>();
		
		UiAutomationUtils.waitWithPoll(() -> 
		{
			File file = checkForFile(downloadFolderPath, extSet);
			fileRes.setValue(file);
			
			return (file != null);
		}, (int) downloadWaitTime, 1000);

		return fileRes.getValue();
	}

	private File waitAndDownload(String downloadFolderPath)
	{
		AutomationUtils.sleep(downloadWaitTime);
		return checkForFile(downloadFolderPath, null);
	}
	
	private File checkForFile(String downloadFolderPath, Set<String> extensions)
	{
		File downloadFolder = new File(downloadFolderPath);
		File files[] = downloadFolder.listFiles();
		
		if(files == null || files.length == 0)
		{
			return null;
		}
		
		if(files.length > 1)
		{
			throw new InvalidStateException("Multiple files found in download folder: {}", downloadFolder.getPath());
		}
		
		if(extensions != null)
		{
			String ext = FilenameUtils.getExtension(files[0].getName()).toLowerCase();
			
			if(!extensions.contains(ext))
			{
				return null;
			}
		}
		
		try
		{
			return files[0].getCanonicalFile();
		}catch(IOException ex)
		{
			throw new InvalidStateException("An error occurred while fetching cannoical path of downloaded file: " + files[0].getPath(), ex);
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
		builder.append("Click and Download [");

		builder.append("Locator: ").append(locator);

		builder.append("]");
		return builder.toString();
	}
}
