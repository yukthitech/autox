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
package com.yukthitech.prism.services;

import java.awt.EventQueue;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.RuntimeInterruptedException;
import com.yukthitech.utils.exceptions.InvalidStateException;

@Service
public class IdeEventManager
{
	private static Logger logger = LogManager.getLogger(IdeEventManager.class);
	
	/**
	 * Event method along with service.
	 * @author akiran
	 */
	private static class EventMethod
	{
		private Object service;
		
		private Method method;

		public EventMethod(Object service, Method method)
		{
			this.service = service;
			this.method = method;
		}

		public void invoke(IIdeEvent event)
		{
			try
			{
				method.invoke(service, event);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while invoking event method: {}.{}()", 
						method.getDeclaringClass().getName(), method.getName(), ex);
			}
		}
	}
	
	@Autowired
	private ClassScannerService classScannerService;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private Map<Class<?>, List<EventMethod>> typeToMethods = new HashMap<>();
	
	private Thread eventProcessor;
	
	private LinkedList<IIdeEvent> eventQueue = new LinkedList<>();
	
	public void init()
	{
		Set<Method> eventMethods = classScannerService.getMethodsWithAnnotation(IdeEventHandler.class);
		Class<?> enclosingClass = null;
		
		for(Method met : eventMethods)
		{
			if(met.getParameterCount() != 1)
			{
				throw new InvalidStateException("Invalid event method encountered {}.{}(). It should have single parameter which should be of type IdeEvent or its child class", 
						met.getDeclaringClass().getName(), met.getName());
			}
			
			Class<?> evtType = met.getParameterTypes()[0];
			
			if(!IIdeEvent.class.isAssignableFrom(evtType))
			{
				throw new InvalidStateException("Invalid event method encountered {}.{}(). It should have single parameter which should be of type IdeEvent or its child class", 
						met.getDeclaringClass().getName(), met.getName());
			}
			
			enclosingClass = met.getDeclaringClass();
			
			Object service = null;
			
			try
			{
				service = applicationContext.getBean(enclosingClass);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Event method encountered {}.{}() is declared in non-spring managed service", 
						met.getDeclaringClass().getName(), met.getName());
			}
			
			List<EventMethod> eventMethodLst = typeToMethods.get(evtType);
			
			if(eventMethodLst == null)
			{
				eventMethodLst = new ArrayList<>();
				typeToMethods.put(evtType, eventMethodLst);
			}
			
			met.setAccessible(true);
			eventMethodLst.add(new EventMethod(service, met));
		}
		
		eventProcessor = new Thread(this::processEvents, "Ide Event Processor");
		eventProcessor.start();
	}
	
	private void processEvents()
	{
		List<IIdeEvent> events = new ArrayList<>();
		
		while(true)
		{
			synchronized(this.eventQueue)
			{
				events.addAll(this.eventQueue);
				this.eventQueue.clear();
			}

			for(IIdeEvent event : events)
			{
				processEvent(event);
			}
			
			events.clear();
			
			synchronized(this.eventQueue)
			{
				try
				{
					if(eventQueue.isEmpty())
					{
						//wait for 1 sec to check next set of events
						eventQueue.wait(1000);
					}
				}catch(InterruptedException ex)
				{
					//ignore
				}
			}
		}
	}
	
	private void processInEventThread(Runnable runnable)
	{
		CountDownLatch latch = new CountDownLatch(1);
		
		//invoke the handlers in event queue, so that ui updates
		//  will not create dead locks
		EventQueue.invokeLater(() -> 
		{
			try
			{
				runnable.run();
			} finally
			{
				latch.countDown();
			}
		});
		
		//wait for AWT event thread to process current event handlers
		try
		{
			latch.await();
		} catch(InterruptedException ex)
		{
			throw new RuntimeInterruptedException(ex);
		}
	}
	
	private void processEvent(IIdeEvent event)
	{
		Runnable executeHandlers = () -> 
		{
			List<EventMethod> methods = typeToMethods.get(event.getClass());
			
			if(methods == null)
			{
				return;
			}
			
			for(EventMethod met : methods)
			{
				logger.trace("Invoking event method {}.{}() for event of type: {}", 
						met.method.getDeclaringClass().getName(), met.method.getName(), event.getClass().getName());
				
				try
				{
					met.invoke(event);
				}catch(Exception ex)
				{
					logger.debug("An error occurred while process event method", ex);
				}
			}
		};
		
		//if current thread is event dispatcher thread
		if(EventQueue.isDispatchThread())
		{
			//execute handlers in same thread (to avoid dead lock)
			executeHandlers.run();
		}
		else
		{
			//execute handlers in event thread and wait for handlers
			// to complete execution
			processInEventThread(executeHandlers);
		}
	}
	
	public void raiseAsyncEvent(IIdeEvent event)
	{
		synchronized(this.eventQueue)
		{
			this.eventQueue.addLast(event);
			this.eventQueue.notifyAll();
		}
	}

	public void raiseSyncEvent(IIdeEvent event)
	{
		processEvent(event);
	}
}
