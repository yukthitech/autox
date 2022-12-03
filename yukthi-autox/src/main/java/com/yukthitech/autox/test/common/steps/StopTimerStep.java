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
package com.yukthitech.autox.test.common.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Stops the timer and keeps the elapsed time on context.
 * @author akiran
 */
@Executable(name = "stopTimer", group = Group.Common, message = "Stops the timer and keeps the elapsed time on context.")
public class StopTimerStep extends AbstractStep 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the timer.
	 */
	@Param(description = "Name of the timer.")
	private String name;
	
	/**
	 * Sets the name of the timer.
	 *
	 * @param name the new name of the timer
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		logger.debug("Stopped timer with name: {}", name);
		
		Long startTime = (Long) context.getAttribute(name + ".startTime");
		
		if(startTime == null)
		{
			throw new IllegalStateException("No timer started with specified name: " + name);
		}

		long currentTime = System.currentTimeMillis();
		
		long diffInMillis = currentTime - startTime;
		long diffInSecs = diffInMillis / 1000;
		long diffInMins = diffInSecs / 60;
		long diffInHours = diffInMins / 60;
		
		diffInSecs = (diffInMins > 0) ? (diffInSecs % 60) : diffInMins;
		diffInMins = (diffInHours > 0) ? (diffInMins % 60) : diffInMins;
		
		StringBuilder timeTakenStr = new StringBuilder();
		
		if(diffInHours > 0)
		{
			timeTakenStr.append(diffInHours).append("Hr ");
		}
		
		if(diffInMins > 0)
		{
			timeTakenStr.append(diffInMins).append("Min ");
		}
		
		timeTakenStr.append(diffInSecs).append("Sec");
		
		context.setAttribute(name, timeTakenStr.toString());
	}
}
