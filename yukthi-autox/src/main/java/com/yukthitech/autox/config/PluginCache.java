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

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

import com.yukthitech.utils.RuntimeInterruptedException;

/**
 * Used for caching plugin sessions and also limiting to specified max limit.
 * @author akranthikiran
 * @param <P>
 */
public class PluginCache<P extends IPluginSession>
{
	private LinkedList<P> freeSessions = new LinkedList<>();
	
	private Supplier<P> newSessionCreator;
	
	private Semaphore semaphore;
	
	public PluginCache(Supplier<P> newSessionCreator, int maxSessions)
	{
		this.newSessionCreator = newSessionCreator;
		
		semaphore = new Semaphore(maxSessions);
	}
	
	public P getSession()
	{
		try
		{
			semaphore.acquire();
		}catch(InterruptedException ex)
		{
			throw new RuntimeInterruptedException(ex);
		}
		
		synchronized(this)
		{
			P session = null;
			
			if(!freeSessions.isEmpty())
			{
				session = freeSessions.remove();
			}
			else
			{
				session = newSessionCreator.get();
			}

			return session;
		}
	}
	
	public void release(P session)
	{
		if(session == null)
		{
			throw new NullPointerException("Null session specified.");
		}
		
		synchronized(this)
		{
			freeSessions.add(session);
		}
		
		semaphore.release();
	}
	
	public synchronized void close()
	{
		freeSessions.forEach(session -> session.close());
	}
}
