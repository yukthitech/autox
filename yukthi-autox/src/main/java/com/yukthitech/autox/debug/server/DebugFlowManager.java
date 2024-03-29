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
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;

/**
 * Execution debug manager to control flow execution.
 * @author akranthikiran
 */
public class DebugFlowManager
{
	private static Logger logger = LogManager.getLogger(DebugFlowManager.class);
	
	private static DebugFlowManager instance = new DebugFlowManager();
	
	/**
	 * Currently available debug points.
	 */
	private Map<String, DebugPoint> debugPoints = new HashMap<>();
	
	/**
	 * Maintains list of live debug points (points where execution has stopped) along with callbacks.
	 */
	private Map<String, LiveDebugPoint> livePoints = new HashMap<>();
	
	private DebugFlowManager()
	{}
	
	public static DebugFlowManager getInstance()
	{
		return instance;
	}
	
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
	}
	
	public DebugFlowManager setDebugPoints(Collection<DebugPoint> points)
	{
		synchronized(debugPoints)
		{
			this.debugPoints.clear();
			
			if(CollectionUtils.isNotEmpty(points))
			{
				points.forEach(point -> debugPoints.put(point.getFilePath() + ":" + point.getLineNumber(), point));
			}
		}
		
		return this;
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
		if(!DebugServer.isRunningInDebugMode())
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

				DebugServer.getInstance().sendClientMessage(
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
		if(!DebugServer.isRunningInDebugMode())
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
}
