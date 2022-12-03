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
package com.yukthitech.autox.test.ssh.steps;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.JSchException;
import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Starts a new session with specified details. The session can be accessed in other ssh steps with specified name.
 * 
 * @author akiran
 */
@Executable(name = "sshStartSession", group = Group.Ssh, message = "Starts a new session with specified details. The session can be accessed in other ssh steps with specified name.")
public class StartSessionStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of this session.
	 */
	@Param(description = "Name for the session being started.")
	private String name;

	/**
	 * Host on which remote file is located.
	 */
	@Param(description = "Remote host to be connected.")
	private String host;

	/**
	 * SSH port, defaults to 22.
	 */
	@Param(description = "Remote host's ssh port. Default: 22", required = false)
	private int port = 22;

	/**
	 * User name to be used.
	 */
	@Param(description = "User name for login.")
	private String user;

	/**
	 * Password to be used. Either of password or privateKeyPath is mandatory.
	 */
	@Param(description = "Password for login. Either of password or private-key is mandatory. If both are provided, password will be given higher preference.", required = false)
	private String password;

	/**
	 * Private key path. Either of password or privateKeyPath is mandatory.
	 */
	@Param(description = "Private key to be used for login. Either of password or private-key is mandatory. If both are provided, password will be given higher preference.", required = false)
	private String privateKeyPath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.
	 * AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		RemoteSession remoteSession = new RemoteSession();
		remoteSession.setHost(host);
		remoteSession.setPort(port);
		remoteSession.setUser(user);

		if(StringUtils.isNoneBlank(password))
		{
			exeLogger.debug("Starting SSH session with name '{}' [Host: {}, Port: {}, User: {}, using password]", name, host, port, user);
			remoteSession.setPassword(password);
		}
		else
		{
			exeLogger.debug("Starting SSH session with name '{}' [Host: {}, Port: {}, User: {}, using private-key: {}]", name, host, port, user, privateKeyPath);
			remoteSession.setPrivateKeyPath(privateKeyPath);
		}

		try
		{
			remoteSession.getSession();
		} catch(JSchException e)
		{
			throw new InvalidStateException("An error occurred while estabilising session with name: {}", name, e);
		}
		
		context.setInternalAttribute(name, remoteSession);
		exeLogger.debug("Remote session is started successfully..");
	}

	/**
	 * Sets the name of this session.
	 *
	 * @param name
	 *            the new name of this session
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the host on which remote file is located.
	 *
	 * @param host
	 *            the new host on which remote file is located
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * Sets the sSH port, defaults to 22.
	 *
	 * @param port
	 *            the new sSH port, defaults to 22
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * Sets the user name to be used.
	 *
	 * @param user
	 *            the new user name to be used
	 */
	public void setUser(String user)
	{
		this.user = user;
	}

	/**
	 * Sets the password to be used. Either of password or privateKeyPath is
	 * mandatory.
	 *
	 * @param password
	 *            the new password to be used
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Sets the private key path. Either of password or privateKeyPath is
	 * mandatory.
	 *
	 * @param privateKeyPath
	 *            the new private key path
	 */
	public void setPrivateKeyPath(String privateKeyPath)
	{
		this.privateKeyPath = privateKeyPath;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		super.validate();
		
		if(StringUtils.isBlank(password) && StringUtils.isBlank(privateKeyPath))
		{
			throw new ValidateException("Both password and private-key-path cannot be empty");
		}
		
		if(!StringUtils.isNotBlank(privateKeyPath))
		{
			File file = new File(privateKeyPath);
			
			if(!file.exists())
			{
				throw new ValidateException("Invalid private key file specified: " + privateKeyPath);
			}
		}
	}
}
