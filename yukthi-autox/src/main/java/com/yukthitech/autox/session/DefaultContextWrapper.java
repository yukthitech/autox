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
package com.yukthitech.autox.session;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Caller-thread context access for normal / CLI sessions (plugin pool semantics).
 */
final class DefaultContextWrapper implements IContextWrapper
{
	private static final Logger logger = LogManager.getLogger(DefaultContextWrapper.class);
	
	private final AutomationContext context;
	
	private boolean closed;
	
	DefaultContextWrapper(AutomationContext context)
	{
		this.context = context;
	}
	
	@Override
	public <T> T execute(Function<AutomationContext, T> func)
	{
		ensureOpen();
		return func.apply(context);
	}
	
	@Override
	public boolean isInteractive()
	{
		return false;
	}
	
	@Override
	public synchronized void close()
	{
		if(closed)
		{
			return;
		}
		
		closed = true;
		
		try
		{
			context.reset();
		}catch(Exception ex)
		{
			logger.warn("Error while resetting automation context", ex);
		}
	}
	
	private void ensureOpen()
	{
		if(closed)
		{
			throw new InvalidStateException("AutoxSession is already closed");
		}
	}
}
