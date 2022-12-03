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
package com.yukthitech.autox.ide.exeenv.debug;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.services.IdeClosingEvent;
import com.yukthitech.autox.ide.services.IdeEventHandler;
import com.yukthitech.autox.ide.services.IdePreStateLoadEvent;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manages debug related information.
 * @author akranthikiran
 */
@Service
public class DebugManager
{
	private static final String DEBUG_POINT_ATTR_NAME = DebugManager.class.getName() + ".debugPoints";
	
	private Map<File, List<DebugPoint>> debugPoints = new HashMap<>();
	
	public synchronized DebugPoint addDebugPoint(String project, File file, int lineNo)
	{
		DebugPoint newPoint = new DebugPoint(project, file, lineNo);
		addDebugPoint(newPoint);
		return newPoint;
	}
	
	private void addDebugPoint(DebugPoint newPoint)
	{
		List<DebugPoint> points = debugPoints.get(newPoint.getFile());
		
		if(points == null)
		{
			points = new ArrayList<>();
			debugPoints.put(newPoint.getFile(), points);
		}
		else
		{
			int idx = points.indexOf(newPoint);
			
			if(idx >= 0)
			{
				throw new InvalidStateException("Multiple debug points are getting added at same location {}:{}", newPoint.getFile().getName(), newPoint.getLineNo());
			}
		}
		
		points.add(newPoint);
	}
	
	public synchronized void removeBreakPoint(DebugPoint debugPoint)
	{
		List<DebugPoint> points = debugPoints.get(debugPoint.getFile());
		
		if(points == null)
		{
			return;
		}
		
		points.remove(debugPoint);
	}
	
	public synchronized List<DebugPoint> getDebugPoints(File file)
	{
		List<DebugPoint> points = this.debugPoints.get(file);
		
		if(CollectionUtils.isEmpty(points))
		{
			return null;
		}
		
		return new ArrayList<>(points);
	}
	
	@SuppressWarnings("unchecked")
	@IdeEventHandler
	public void ideOpening(IdePreStateLoadEvent event)
	{
		IdeState state = event.getIdeState();
		List<DebugPoint> points = (List<DebugPoint>) state.getAttribute(DEBUG_POINT_ATTR_NAME);
		
		if(CollectionUtils.isEmpty(points))
		{
			return;
		}
		
		for(DebugPoint point : points)
		{
			//ignore points which corresponds to deleted file
			if(!point.getFile().exists())
			{
				continue;
			}
			
			try
			{
				addDebugPoint(point);
			}catch(Exception ex)
			{
				//on error ignore the point
			}
		}
	}
	
	@IdeEventHandler
	public void ideClosing(IdeClosingEvent event)
	{
		List<DebugPoint> points = new ArrayList<>();
		this.debugPoints.values().forEach(pointLst -> points.addAll(pointLst));

		IdeState state = event.getIdeState();
		state.setAtribute(DEBUG_POINT_ATTR_NAME, points);
	}
}
