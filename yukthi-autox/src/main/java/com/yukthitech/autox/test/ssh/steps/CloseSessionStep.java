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

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Closes the specified remote session.
 * 
 * @author akiran
 */
@Executable(name = "sshCloseSession", group = Group.Ssh, message = "Closes the specified remote session.")
public class CloseSessionStep extends AbstractStep
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the session to be closed.
	 */
	@Param(description = "Name of the session to be closed.")
	private String session;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.
	 * AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing the session - {}", session);
		
		RemoteSession remoteSession = (RemoteSession) context.getInternalAttribute(session);
		
		if(remoteSession == null)
		{
			throw new InvalidStateException("No ssh-session exists with specified name: {}", session);
		}
		
		remoteSession.close();
	}

	/**
	 * Sets the name of the session to be closed.
	 *
	 * @param session the new name of the session to be closed
	 */
	public void setSession(String session)
	{
		this.session = session;
	}
}
