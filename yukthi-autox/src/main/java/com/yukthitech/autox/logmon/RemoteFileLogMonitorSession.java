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

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpProgressMonitor;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.test.ssh.steps.RemoteSession;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class RemoteFileLogMonitorSession implements ILogMonitorSession
{
	private static Logger logger = LogManager.getLogger(RemoteFileLogMonitorSession.class);
	
	private static class RemoteSessionWithPosition extends RemoteSession
	{
		private long position;
		
		public RemoteSessionWithPosition(String host, int port, String user, String password, String privateKeyPath)
		{
			super(host, port, user, password, privateKeyPath);
		}
	}

	private RemoteFileLogMonitor parentMonitor;
	
	/**
	 * Host to session mapping.
	 */
	private Map<String, RemoteSessionWithPosition> pathToSession = new HashMap<>();

	/**
	 * Number of times this logger failed to fetch the log.
	 */
	private int failureCount = 0;

	public RemoteFileLogMonitorSession(RemoteFileLogMonitor parentMonitor)
	{
		this.parentMonitor = parentMonitor;
	}
	
	@Override
	public RemoteFileLogMonitor getParentMonitor()
	{
		return parentMonitor;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.automation.logmon.ILogMonitor#startMonitoring()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void startMonitoring()
	{
		if(!parentMonitor.isEnabled())
		{
			logger.warn("As this log monitor is not enabled, skipping start-monitor call");
			return;
		}
		
		if(failureCount >= parentMonitor.maxFailureCount)
		{
			logger.warn("As this log monitor is failed more than max-failure-count, skipping start-monitor call");
			return;
		}

		String paths[] = parentMonitor.remoteFilePaths.split("\\s*\\,\\s*");
		
		for(String remotePath : paths)
		{
			try
			{
				Matcher matcher = RemoteFileLogMonitor.REMOTE_FILE_PATTERN.matcher(remotePath);
				
				if(!matcher.matches())
				{
					logger.error("Invalid remote file path specified. It should be of pattern '/remoteFilePath@host:port'. Ignoring errored file path: {}", remotePath);
					continue;
				}
				
				RemoteSessionWithPosition remoteSession = new RemoteSessionWithPosition(matcher.group("host"), Integer.parseInt(matcher.group("port")), 
						parentMonitor.user, parentMonitor.password, parentMonitor.privateKeyPath);
				
				String remoteFilePath = matcher.group("path");
				ChannelSftp sftp = remoteSession.getChannelSftp();
				
				logger.debug("Getting position of remote file: {}", remoteFilePath);
				
				Vector<ChannelSftp.LsEntry> lsEntries = sftp.ls(remoteFilePath);
				
				pathToSession.put(remoteFilePath, remoteSession);
				
				if(lsEntries.size() == 0)
				{
					logger.warn("No remote file found under specified path at start of monitoring. Assuming the file will be created. Path: " + remoteFilePath);
					return;
				}
				
				ChannelSftp.LsEntry lsEntry = lsEntries.get(0);
				remoteSession.position = lsEntry.getAttrs().getSize();
			}catch(Exception ex)
			{
				logger.error("An error occurred while getting remote file size. Remote file - {} [User: {}, Using Password: {}]", 
						remotePath, parentMonitor.user, (parentMonitor.password != null), ex);
				failureCount++;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.logmon.ILogMonitor#stopMonitoring()
	 */
	@Override
	public List<ReportLogFile> stopMonitoring()
	{
		if(!parentMonitor.isEnabled())
		{
			logger.warn("As this log monitor is not enabled, skipping stop-monitor call");
			return Collections.emptyList();
		}
		
		if(failureCount >= parentMonitor.maxFailureCount)
		{
			logger.warn("As this log monitor is failed more than max-failure-count, skipping stop-monitor call");
			return Collections.emptyList();
		}

		if(parentMonitor.fetchDelayInSec > 0)
		{
			logger.debug("Waiting for {} Sec before fetching the logs.", parentMonitor.fetchDelayInSec);
			
			try
			{
				Thread.sleep(parentMonitor.fetchDelayInSec * 1000);
			} catch(Exception ex)
			{
				//ignore
			}
		}
		
		List<ReportLogFile> logFiles = new ArrayList<>(pathToSession.size());
		
		for(String remotePath : pathToSession.keySet())
		{
			RemoteSessionWithPosition remoteSession = pathToSession.get(remotePath);
			ReportLogFile logFile = stopMonitoringSession(remotePath, remoteSession);
			
			if(logFile != null)
			{
				logFiles.add(logFile);
			}
		}
		
		return logFiles;
	}

	/**
	 * Stops monitoring remote session and fetches corresponding log file.
	 * @param remoteSession remote session to stop monitoring
	 * @return corresponding log file
	 */
	@SuppressWarnings("unchecked")
	private ReportLogFile stopMonitoringSession(String remoteFilePath, RemoteSessionWithPosition remoteSession)
	{
		try
		{
			ChannelSftp sftp = remoteSession.getChannelSftp();
			
			logger.debug("Getting content from remote file: {}", remoteFilePath);
			
			Vector<ChannelSftp.LsEntry> lsEntries = sftp.ls(remoteFilePath);
			
			if(lsEntries.size() == 0)
			{
				logger.warn("No file found under specified remote path at end of monitoring. Ignoring monitoring request. Path: " + remoteFilePath);
				return null;
			}
			
			ReportLogFile tempFile = AutomationContext.getInstance().newLogFile(parentMonitor.getName(), ".log");
			
			ChannelSftp.LsEntry lsEntry = lsEntries.get(0);
			long currentSize = lsEntry.getAttrs().getSize();
			
			//if there is no content simply return empty file.
			if(currentSize == 0)
			{
				return tempFile;
			}

			//if current size is less than start size
			//	which can happen in rolling logs, read the current file from starting
			if(currentSize < remoteSession.position)
			{
				remoteSession.position = 0;
			}

			//calculate amount of log to be read
			final long dataToRead = currentSize - remoteSession.position;
			
			try
			{
				//monitor which forces to stop getting data once 
				//	required bytes are read
				SftpProgressMonitor progressMonitor = new SftpProgressMonitor()
				{
					@Override
					public void init(int arg0, String arg1, String arg2, long arg3)
					{
					}
					
					@Override
					public void end()
					{
					}
					
					@Override
					public boolean count(long read)
					{
						return read >= dataToRead;
					}
				};
				
				FileOutputStream fos = new FileOutputStream(tempFile.getFile());
				
				sftp.get(remoteFilePath, fos, progressMonitor, ChannelSftp.RESUME, remoteSession.position);
				
				fos.close();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while creating monitoring log.", ex);
			}
			
			remoteSession.close();
			return tempFile;
		}catch(Exception ex)
		{
			logger.error("An error occurred while getting remote file size. Remote file - {}", remoteFilePath, ex);
			failureCount++;
			return null;
		}
	}
}
