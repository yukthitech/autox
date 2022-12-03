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
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Remote file monitor to monitor the changes in remote ssh file.
 * @author akiran
 */
public class RemoteFileLogMonitor extends AbstractLogMonitor implements Validateable
{
	static final Pattern REMOTE_FILE_PATTERN = Pattern.compile("(?<path>.*)\\@(?<host>.*)\\:(?<port>\\d+)");
	
	/**
	 * List of remote file paths in format
	 * 		filePath@host:port, filePath@host:port
	 */
	String remoteFilePaths;
	
	/**
	 * User to be used for authentication.
	 */
	String user;
	
	/**
	 * Password for authentication.
	 */
	String password;
	
	/**
	 * Private key path to be used instead of password.
	 */
	String privateKeyPath;
	
	/**
	 * Delay time in seconds after which the log content will be fetched. This
	 * will help to make sure buffered log content is pushed by remote applications
	 * into log file. 
	 */
	long fetchDelayInSec = 5;
	
	/**
	 * Maximum number of failures after this logger will self disable. Defaults to 4.
	 */
	int maxFailureCount = 4;
	
	public RemoteFileLogMonitor()
	{
	}
	
	/**
	 * Adds remote file path.
	 * @param remoteFilePaths path to be added.
	 */
	public void addRemoteFilePaths(String remoteFilePaths)
	{
		this.remoteFilePaths = remoteFilePaths;
	}

	/**
	 * Sets the user name to be used.
	 *
	 * @param user the new user name to be used
	 */
	public void setUser(String user)
	{
		this.user = user;
	}

	/**
	 * Sets the password to be used. Either of password or privateKeyPath is mandatory.
	 *
	 * @param password the new password to be used
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Sets the private key path. Either of password or privateKeyPath is mandatory.
	 *
	 * @param privateKeyPath the new private key path
	 */
	public void setPrivateKeyPath(String privateKeyPath)
	{
		this.privateKeyPath = privateKeyPath;
	}
	
	/**
	 * Sets the delay time in seconds after which the log content will be
	 * fetched. This will help to make sure buffered log content is pushed by
	 * remote applications into log file.
	 *
	 * @param fetchDelayInSec
	 *            the new delay time in seconds after which the log content will
	 *            be fetched
	 */
	public void setFetchDelayInSec(long fetchDelayInSec)
	{
		this.fetchDelayInSec = fetchDelayInSec;
	}
	
	/**
	 * Sets the maximum number of failures after this logger will self disable.
	 *
	 * @param maxFailureCount
	 *            the new maximum number of failures after this logger will self
	 *            disable
	 */
	public void setMaxFailureCount(int maxFailureCount)
	{
		this.maxFailureCount = maxFailureCount;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(!isEnabled())
		{
			return;
		}
		
		super.validate();
		
		if(StringUtils.isEmpty(remoteFilePaths))
		{
			throw new ValidateException("No remote file path specified");
		}
		
		String paths[] = remoteFilePaths.split("\\s*\\,\\s*");
		
		for(String path : paths)
		{
			if(!REMOTE_FILE_PATTERN.matcher(path).matches())
			{
				throw new ValidateException("Invalid remote file path specified: " + path);
			}
		}
		
		if(StringUtils.isBlank(user))
		{
			throw new ValidateException("No user name is specified");
		}
		
		if(StringUtils.isBlank(password) && StringUtils.isBlank(privateKeyPath))
		{
			throw new ValidateException("Either of password or private-key-path is mandatory.");
		}
		
		if(StringUtils.isNotBlank(privateKeyPath))
		{
			File keyFile = new File(privateKeyPath);
			
			if(!keyFile.exists())
			{
				throw new ValidateException("Specified private key file does not exist: " + privateKeyPath);
			}
		}
	}
	
	@Override
	public ILogMonitorSession newSession()
	{
		return new RemoteFileLogMonitorSession(this);
	}
}
