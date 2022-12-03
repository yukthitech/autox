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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.Param;

public abstract class AbstractPlugin<AT, S extends IPluginSession> implements IPlugin<AT, S>
{
	private static Logger logger = LogManager.getLogger(AbstractPlugin.class);
	
	@Param(description = "Maximum number of sessions that can be opened simultaneously. Defaults to 10.")
	private int maxSessions = 10;
	
	private PluginCache<S> sessionCache;
	
	public void setMaxSessions(int maxSessions)
	{
		if(maxSessions < 1)
		{
			throw new InvalidArgumentException("Invalid number of max sessions specified: " + maxSessions);
		}
		
		this.maxSessions = maxSessions;
	}

	@Override
	public void initialize(AT args)
	{
		logger.debug("Creating cache with size: {}", maxSessions);
		sessionCache = new PluginCache<>(this::createSession, maxSessions);
	}

	@Override
	public S newSession()
	{
		return sessionCache.getSession();
	}
	
	@Override
	public void releaseSession(S session)
	{
		sessionCache.release(session);
	}
	
	@Override
	public void close()
	{
		if(sessionCache != null)
		{
			sessionCache.close();
		}
	}
	
	protected abstract S createSession();
}
