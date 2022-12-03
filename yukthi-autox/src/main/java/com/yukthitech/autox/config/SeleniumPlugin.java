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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Plugin needed by selenium based steps or validators.
 * @author akiran
 */
@Executable(name = "SeleniumPlugin", group = Group.NONE, message = "Plugin needed by selenium/ui-automation based steps or validators.")
public class SeleniumPlugin extends AbstractPlugin<SeleniumPluginArgs, SeleniumPluginSession> implements Validateable
{
	private static Logger logger = LogManager.getLogger(SeleniumPlugin.class);
	
	/**
	 * Selenium drivers to use for automation.
	 */
	@Param(description = "Name to basic configuration to be used for different drivers. Like - name, class-name and default system properties to set.", required = true)
	private Map<String, SeleniumDriverConfig> drivers = new LinkedHashMap<>();
	
	/**
	 * Active driver name, useful during driver reset.
	 */
	private String defaultDriverName;
	
	/**
	 * Base url of the application.
	 */
	@Param(description = "Base url to be used for ui automation", required = true)
	private String baseUrl;
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.config.IPlugin#getArgumentBeanType()
	 */
	@Override
	public Class<SeleniumPluginArgs> getArgumentBeanType()
	{
		return SeleniumPluginArgs.class;
	}

	/**
	 * Adds specified driver configuration.
	 * @param driverConfig configuration to be added
	 */
	public void addDriver(SeleniumDriverConfig driverConfig) throws ValidateException
	{
		driverConfig.validate();
		
		drivers.put(driverConfig.getName(), driverConfig);
	}
	
	/**
	 * Fetches the first default driver name. If no default driver
	 * is specified, first driver will be used as default one. 
	 * @return Default driver name
	 */
	private String findDefaultDriverName()
	{
		String firstName = null;
		
		for(String name : drivers.keySet())
		{
			firstName = firstName == null ? name : firstName;
			
			if(drivers.get(name).isDefault())
			{
				return name;
			}
		}
		
		return firstName;
	}
	
	public SeleniumDriverConfig getDriverConfig(String name)
	{
		SeleniumDriverConfig driverConfig = drivers.get(name);

		if(driverConfig == null)
		{
			throw new InvalidArgumentException("No driver-config found with name: " + name);
		}
		
		return driverConfig;
	}
	
	/**
	 * Sets the base url of the application.
	 *
	 * @param baseUrl the new base url of the application
	 */
	public void setBaseUrl(String baseUrl)
	{
		if(baseUrl.endsWith("/"))
		{
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}
		
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Gets the base url of the application.
	 *
	 * @return the base url of the application
	 */
	String getBaseUrl()
	{
		return baseUrl;
	}
	
	String getDefaultDriverName()
	{
		return defaultDriverName;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.automation.config.IConfiguration#initialize(com.yukthitech.automation.AutomationContext, java.lang.Object)
	 */
	@Override
	public void initialize(SeleniumPluginArgs args)
	{
		String driverName = args.getWebDriver();
		
		if(driverName == null)
		{
			driverName = findDefaultDriverName();
			
			logger.debug("As no web driver is specified in command line arguments, using default driver - " + driverName);
		}
		else
		{
			logger.debug("Using driver specified in command line argument - " + driverName);
		}
		
		SeleniumDriverConfig driverConfig = drivers.get(driverName);
		
		if(driverConfig == null)
		{
			throw new InvalidStateException("No web driver is defined with name - {}", driverName);
		}
		
		defaultDriverName = driverName;
		
		super.initialize(args);
	}
	
	@Override
	protected SeleniumPluginSession createSession()
	{
		return new SeleniumPluginSession(this, defaultDriverName);
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(drivers.isEmpty())
		{
			throw new ValidateException("No drivers are specified.");
		}
		
		if(StringUtils.isBlank(baseUrl))
		{
			throw new ValidateException("No base url is specified.");
		}
	}
}
