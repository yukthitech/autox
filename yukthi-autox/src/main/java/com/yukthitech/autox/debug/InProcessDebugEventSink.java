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
package com.yukthitech.autox.debug;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * In-process sink for host apps. Dispatches events asynchronously on a dedicated
 * thread so listeners can resume / step without deadlocking the paused execution thread.
 *
 * @author akranthikiran
 */
public class InProcessDebugEventSink implements IDebugEventSink
{
	private static final Logger logger = LogManager.getLogger(InProcessDebugEventSink.class);
	
	private static final AtomicInteger THREAD_SEQ = new AtomicInteger(0);
	
	private final List<Consumer<Serializable>> listeners = new CopyOnWriteArrayList<>();
	
	private final ExecutorService dispatcher;
	
	private volatile boolean closed;
	
	public InProcessDebugEventSink()
	{
		this.dispatcher = Executors.newSingleThreadExecutor(new ThreadFactory()
		{
			@Override
			public Thread newThread(Runnable r)
			{
				Thread t = new Thread(r, "Autox-DebugEventSink-" + THREAD_SEQ.incrementAndGet());
				t.setDaemon(true);
				return t;
			}
		});
	}
	
	/**
	 * Registers a listener for debug events. Invoked on the sink dispatcher thread.
	 */
	public void addListener(Consumer<Serializable> listener)
	{
		if(listener == null)
		{
			throw new NullPointerException("listener cannot be null");
		}
		
		listeners.add(listener);
	}
	
	public void removeListener(Consumer<Serializable> listener)
	{
		listeners.remove(listener);
	}
	
	@Override
	public void onEvent(Serializable event)
	{
		if(closed)
		{
			return;
		}
		
		dispatcher.execute(() ->
		{
			for(Consumer<Serializable> listener : listeners)
			{
				try
				{
					listener.accept(event);
				}catch(Exception ex)
				{
					logger.error("Error while delivering debug event to listener: {}", event, ex);
				}
			}
		});
	}
	
	@Override
	public synchronized void close()
	{
		if(closed)
		{
			return;
		}
		
		closed = true;
		listeners.clear();
		dispatcher.shutdownNow();
	}
}
