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
package com.yukthitech.autox.config.selenium;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import com.yukthitech.autox.plugin.ui.SeleniumDriverConfig;

public class AutoxChromeDriver extends ChromeDriver
{
	private static Logger logger = LogManager.getLogger(AutoxChromeDriver.class);
	
	public AutoxChromeDriver(SeleniumDriverConfig config)
	{
		super(buildOptions(config));
	}
	
	private static ChromeOptions buildOptions(SeleniumDriverConfig config)
	{
		ChromeOptions options = new ChromeOptions();
		options.setAcceptInsecureCerts(true);
		
		if(StringUtils.isNotBlank(config.getDownloadFolder()))
		{
			Map<String, Object> expPrefs = new HashMap<String, Object>();
			expPrefs.put("download.default_directory", config.getDownloadFolder());
			expPrefs.put("profile.default_content_settings.popups", 0);
			expPrefs.put("download.prompt_for_download", "false");
			
			options.setExperimentalOption("prefs", expPrefs);
		}
		
		if(StringUtils.isNotBlank(config.getUserDataDir()) && StringUtils.isNotBlank(config.getProfileFolder()))
		{
			options.addArguments("--user-data-dir=" + config.getUserDataDir());
			options.addArguments("--profile-directory=" + config.getProfileFolder());
		}
		
		if(StringUtils.isNoneBlank(config.getExtraArguments()))
		{
			String argLst[] = config.getExtraArguments().trim().split("\\s*\\,\\s*");
			
			for(String arg : argLst)
			{
				options.addArguments(arg);				
			}
		}
		
		logger.debug("Creating chrome options with profile-options: {}", config.getProfileOptions());
		
		if("true".equals(config.getProfileOptions().get("headless.execution")))
		{
			options.addArguments("--headless");
		}
		
		if("true".equals( config.getProfileOptions().get("enable.console.logs") ))
		{
			LoggingPreferences logPrefs = new LoggingPreferences();
	        logPrefs.enable(LogType.BROWSER, Level.ALL);
	        options.setCapability("goog:loggingPrefs", logPrefs);
		}
		
		return options;
	}
}
