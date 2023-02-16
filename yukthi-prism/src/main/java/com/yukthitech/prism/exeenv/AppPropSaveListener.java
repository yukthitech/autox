/**
 * Copyright (c) 2023 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
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
package com.yukthitech.prism.exeenv;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.debug.common.ClientMssgLoadAppProperties;
import com.yukthitech.prism.events.FileSavedEvent;
import com.yukthitech.prism.services.IdeEventHandler;

/**
 * Component responsible for reloading app properties in debug environments.
 * @author akranthikiran
 */
@Component
public class AppPropSaveListener
{
	private static Logger logger = LogManager.getLogger(AppPropSaveListener.class);
	
	@Autowired
	private ExecutionEnvironmentManager executionEnvironmentManager;
	
	@IdeEventHandler
	private void onFileSave(FileSavedEvent event)
	{
		File appPropFile = event.getProject().getAppPropertyFile();
		
		//if event is not related app prop file, ignore the event
		if(!appPropFile.equals(event.getFile()))
		{
			return;
		}
		
		Properties prop = new Properties();
		
		try
		{
			FileInputStream fis	= new FileInputStream(appPropFile);
			prop.load(fis);
			fis.close();
		}catch(Exception ex)
		{
			logger.warn("Loading change app prop file resulted in error. Hence ignoring the save event: [Project: {}, File: {}, Error: {}]", 
					event.getProject().getName(), appPropFile.getPath(), "" + ex);
			return;
		}
		
		List<ExecutionEnvironment> enviroments = executionEnvironmentManager.getRunningEnvironments();
		
		if(CollectionUtils.isEmpty(enviroments))
		{
			return;
		}
		
		for(ExecutionEnvironment env : enviroments)
		{
			if(!env.isDebugEnv() || env.isTerminated())
			{
				continue;
			}
			
			try
			{
				logger.debug("Sending latest app properties to env: {}", env.getName());
				env.sendDataToServer(new ClientMssgLoadAppProperties(prop));
			}catch(Exception ex)
			{
				logger.error("An error occurred while sending latest app properties to environment: {}", env.getName(), ex);
			}
		}
	}
}
