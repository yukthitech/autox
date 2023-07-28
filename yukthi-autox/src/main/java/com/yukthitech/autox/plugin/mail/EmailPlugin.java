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
package com.yukthitech.autox.plugin.mail;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.plugin.AbstractPlugin;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Plugin related to mail reading or sending.
 * @author akiran
 */
@Executable(name = "EmailPlugin", group = Group.NONE, message = "Plugin related to email reading or sending.")
public class EmailPlugin extends AbstractPlugin<Object, MailSession> implements Validateable
{
	/**
	 * Application data sources.
	 */
	@Param(description = "Name to setting mapping for different email servers.", required = true)
	private Map<String, EmailServerSettings> emailSettingsMap = new HashMap<>();
	
	/**
	 * Name of the default email server.
	 */
	@Param(description = "Name of the default email server.", required = false)
	private String defaultEmailServer;

	public void setDefaultEmailServer(String defaultEmailServer)
	{
		this.defaultEmailServer = defaultEmailServer;
	}
	
	@Override
	protected MailSession createSession()
	{
		return new MailSession(this);
	}
	
	/**
	 * Adds specified email server settings with specified name.
	 * @param name Name of the data source.
	 * @param dataSource Data source to add.
	 */
	public void addEmailServer(String name, EmailServerSettings settings)
	{
		if(StringUtils.isBlank(name))
		{
			throw new NullPointerException("Email server name can not be null or empty.");
		}
		
		if(settings == null)
		{
			throw new NullPointerException("Data source can not be null");
		}
		
		this.emailSettingsMap.put(name, settings);
	}
	
	EmailServerSettings getSettings(String name)
	{
		if(name == null)
		{
			name = defaultEmailServer;
		}
		
		return this.emailSettingsMap.get(name);
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(emailSettingsMap.isEmpty())
		{
			throw new ValidateException("No mail servers are defined.");
		}
	}
}
