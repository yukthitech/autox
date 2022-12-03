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

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class FileLogMonitorSession implements ILogMonitorSession
{
	private static Logger logger = LogManager.getLogger(FileLogMonitorSession.class);
	
	private FileLogMonitor parentMonitor;
	
	/**
	 * Internal field to track start position at start of monitoring.
	 */
	private long startPosition = -1;

	public FileLogMonitorSession(FileLogMonitor parentMonitor)
	{
		this.parentMonitor = parentMonitor;
	}
	
	@Override
	public FileLogMonitor getParentMonitor()
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

		File file = new File(parentMonitor.getPath());
		
		if(!file.exists() || !file.isFile())
		{
			logger.warn("No file found under specified path at start of monitoring. Assuming the file will be created. Path: " + parentMonitor.getPath());
			startPosition = 0;
			return;
		}
		
		startPosition = file.length();
	}

	@Override
	public List<ReportLogFile> stopMonitoring()
	{
		File file = new File(parentMonitor.getPath());
		
		if(!file.exists() || !file.isFile())
		{
			logger.warn("No file found under specified path at end of monitoring. Ignoring monitoring request. Path: " + parentMonitor.getPath());
			return null;
		}
		
		AutomationContext context = AutomationContext.getInstance();
		ReportLogFile tempFile = context.newLogFile(parentMonitor.getName(), ".log");
		
		long currentSize = file.length();
		
		//if there is no content simply return empty file.
		if(currentSize == 0)
		{
			return Arrays.asList(tempFile);
		}
		
		//if current size is less than start size
		//	which can happen in rolling logs, read the current file from starting
		if(currentSize < startPosition)
		{
			startPosition = 0;
		}
		
		//calculate amount of log to be read
		long dataToRead = currentSize - startPosition;
		
		try
		{
			RandomAccessFile inputFile = new RandomAccessFile(file, "r");
			inputFile.seek(startPosition);
			
			byte buff[] = new byte[2048];
			int read = 0;
			FileOutputStream fos = new FileOutputStream(tempFile.getFile());
			long totalRead = 0;
			
			while( (read = inputFile.read(buff)) > 0)
			{
				fos.write(buff, 0, buff.length);
				totalRead += read;
				
				//once the all content is read till stop monitoring was requested
				if(totalRead >= dataToRead)
				{
					//stop reading
					break;
				}
			}
			
			fos.close();
			inputFile.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating monitoring log.");
		}
		
		return Arrays.asList(tempFile);
	}
}
