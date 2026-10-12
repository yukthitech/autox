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
package com.yukthitech.autox.debug.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ILocationBased;
import com.yukthitech.autox.debug.IDebugEventSink;
import com.yukthitech.autox.debug.IDebugFlowManager;
import com.yukthitech.autox.debug.SocketDebugEventSink;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Execution debug manager to control flow execution.
 * Debug mode is active when an {@link IDebugEventSink} is registered (socket for Prism
 * via {@link #startDebugServer(int)}, or in-process via {@link #enableDebug(IDebugEventSink)}).
 * @author akranthikiran
 */
public class DebugFlowManager implements IDebugFlowManager
{
	private static Logger logger = LogManager.getLogger(DebugFlowManager.class);
	
	/**
	 * Currently available debug points.
	 */
	private Map<String, DebugPoint> debugPoints = new HashMap<>();
	
	/**
	 * Maintains list of live debug points (points where execution has stopped) along with callbacks.
	 */
	private Map<String, LiveDebugPoint> livePoints = new HashMap<>();
	
	/**
	 * Active outbound event sink. Non-null means debug mode is enabled.
	 */
	private IDebugEventSink eventSink;
	
	/**
	 * Optional Prism socket debug server (when sink is {@link SocketDebugEventSink}).
	 */
	private DebugServer debugServer;
	
	public DebugFlowManager()
	{}
	
	/**
	 * Enables debug mode with the given sink (typically {@code InProcessDebugEventSink} for embed).
	 */
	public synchronized void enableDebug(IDebugEventSink sink)
	{
		if(eventSink != null)
		{
			throw new InvalidStateException("Debug mode is already enabled for this flow manager.");
		}
		
		if(sink == null)
		{
			throw new InvalidStateException("Debug event sink cannot be null");
		}
		
		this.eventSink = sink;
	}
	
	/**
	 * Starts socket debug server on the given port, waits for Prism client init, and
	 * registers a {@link SocketDebugEventSink}.
	 */
	public synchronized void startDebugServer(int port)
	{
		if(eventSink != null)
		{
			throw new InvalidStateException("Debug mode is already enabled for this flow manager.");
		}
		
		DebugServer server = new DebugServer(port);
		server.start();
		this.debugServer = server;
		this.eventSink = new SocketDebugEventSink(server);
	}
	
	/**
	 * Debug mode is active when a sink is registered (not merely when a socket server exists).
	 */
	public boolean isDebugMode()
	{
		return eventSink != null;
	}
	
	public IDebugEventSink getEventSink()
	{
		return eventSink;
	}
	
	public DebugServer getDebugServer()
	{
		return debugServer;
	}
	
	/**
	 * Sends a message to the active debug sink.
	 */
	public void sendClientMessage(Serializable data)
	{
		IDebugEventSink sink = eventSink;
		
		if(sink == null)
		{
			throw new InvalidStateException("Debug mode is not enabled for this session");
		}
		
		sink.onEvent(data);
	}
	
	@Override
	public void reset()
	{
		synchronized(livePoints)
		{
			List<LiveDebugPoint> points = new ArrayList<>(this.livePoints.values());
			
			points.forEach(livePoint -> 
			{
				livePoint.clearThread();
			});
			
			this.livePoints.clear();
		}

		synchronized(debugPoints)
		{
			this.debugPoints.clear();
		}
		
		IDebugEventSink sink = eventSink;
		eventSink = null;
		debugServer = null;
		
		if(sink != null)
		{
			try
			{
				sink.close();
			}catch(Exception ex)
			{
				logger.warn("Error while closing debug event sink", ex);
			}
		}
	}
	
	public DebugFlowManager setDebugPoints(Collection<DebugPoint> points)
	{
		synchronized(debugPoints)
		{
			this.debugPoints.clear();
			
			if(CollectionUtils.isNotEmpty(points))
			{
				points.forEach(point -> debugPoints.put(pointKey(point), point));
			}
		}
		
		return this;
	}
	
	public void addDebugPoint(DebugPoint point)
	{
		if(point == null)
		{
			throw new InvalidStateException("Debug point cannot be null");
		}
		
		synchronized(debugPoints)
		{
			debugPoints.put(pointKey(point), point);
		}
	}
	
	public void removeDebugPoint(String filePath, int lineNumber)
	{
		synchronized(debugPoints)
		{
			debugPoints.remove(filePath + ":" + lineNumber);
		}
	}
	
	public void removeDebugPoint(DebugPoint point)
	{
		if(point == null)
		{
			return;
		}
		
		removeDebugPoint(point.getFilePath(), point.getLineNumber());
	}
	
	public Collection<DebugPoint> getDebugPoints()
	{
		synchronized(debugPoints)
		{
			return new ArrayList<>(debugPoints.values());
		}
	}
	
	public void addSinglePauseDebugPoint(File sourceFile, int lineNumber)
	{
		String key = sourceFile.getPath() + ":" + lineNumber;
		
		synchronized(debugPoints)
		{
			//if normal debug point is already present, skip adding this point
			if(debugPoints.containsKey(key))
			{
				return;
			}
			
			DebugPoint point = new DebugPoint(sourceFile.getPath(), lineNumber, null);
			point.setSinglePauseOnly(true);
			
			debugPoints.put(key, point);
		}
	}
	
	/**
	 * Checks if specified step has debug point, if it does throws debug exception, so that execution can be paused.
	 * 
	 * NOTE: Callback is supported for future use-cases, where multiple threads may participate in execution and only
	 * one thread is getting paused. 
	 * 
	 * @param step step whose location should be checked for debug point 
	 */
	public void checkForDebugPoint(ILocationBased step)
	{
		if(!isDebugMode())
		{
			return;
		}
		
		LiveDebugPoint currentLivePoint = LiveDebugPoint.getLivePoint();
		
		if(currentLivePoint != null)
		{
			//when dynamic steps under execution dont look for debug points
			if(currentLivePoint.isDynamicExecutionInProgress())
			{
				return;
			}
			
			//if live point is able to handle the flow, return 
			if(currentLivePoint.checkForPause(step))
			{
				return;
			}
		}

		//if live point is not present or unable to determine, check for other debug points
		String debugRef = step.getLocation().getPath() + ":" + step.getLineNumber();
		DebugPoint point = null;
		
		synchronized(debugPoints)
		{
			point = debugPoints.get(debugRef);
		}
		
		if(point == null)
		{
			return;
		}
		
		if(StringUtils.isNotBlank(point.getCondition()))
		{
			try
			{
				Object res = PrefixExpressionFactory.getExpressionFactory().getValueByExpressionString(AutomationContext.getInstance(), point.getCondition());
				
				//if condition is not evaluated as true
				if(!"true".equalsIgnoreCase("" + res))
				{
					return;
				}
			}catch(Exception ex)
			{
				String fileName = new File(point.getFilePath()).getName();
				
				logger.error("An error occurred while evaluating debug point condition [Debug point: {}:{}, Condition: {}]", 
						fileName, point.getLineNumber(), point.getCondition(), ex);

				sendClientMessage(
						new ServerMssgConfirmation(null, false, 
								"An error occurred while evaluating debug point condition [Debug point: %s:%s, Condition: %s]\nError: %s", 
								fileName, point.getLineNumber(), point.getCondition(), ex.getMessage()));
			}
		}
		
		//if live point is present, then before pausing at other debug point, release current live point
		if(currentLivePoint != null)
		{
			currentLivePoint.clearThread();
		}
		
		synchronized(debugPoints)
		{
			if(point.isSinglePauseOnly())
			{
				debugPoints.remove(debugRef);
			}
		}

		LiveDebugPoint.pauseAtDebugPoint(step, point, livePoint -> 
		{
			synchronized(livePoints)
			{
				this.livePoints.put(livePoint.getId(), livePoint);	
			}
		});
	}
	
	public void pauseAtErrorPoint(ILocationBased step)
	{
		if(!isDebugMode())
		{
			return;
		}
		
		LiveDebugPoint currentLivePoint = LiveDebugPoint.getLivePoint();
		
		/*
		 * Current thread might be holding already a debug point
		 * This can happen when steps are getting executed as part of error
		 * point and steps again resulted in error.
		 */
		if(currentLivePoint != null)
		{
			//if error came up during dynamic code execution, ignore the error
			if(currentLivePoint.isDynamicExecutionInProgress())
			{
				return;
			}
			else
			{
				//By returning without creating error point
				//  would throw the exception as part of step execution and will return back
				//  to current debug point
				currentLivePoint.clearThread();
			}
		}

		DebugPoint debugPoint = new DebugPoint(step.getLocation().getPath(), step.getLineNumber(), null);
		
		LiveDebugPoint.pauseAtErrorPoint(step, debugPoint, livePoint -> 
		{
			synchronized(livePoints)
			{
				this.livePoints.put(livePoint.getId(), livePoint);	
			}
		});
	}
	
	public LiveDebugPoint getLiveDebugPoint(String id)
	{
		synchronized(livePoints)
		{
			return livePoints.get(id);
		}
	}
	
	public void removeLivePoint(String id)
	{
		synchronized(livePoints)
		{
			livePoints.remove(id);
		}
	}
	
	private static String pointKey(DebugPoint point)
	{
		return point.getFilePath() + ":" + point.getLineNumber();
	}
}
