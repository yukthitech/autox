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
package com.yukthitech.autox.test.lang.steps;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.Function;

/**
 * Evaluates specified condition and if evaluates to true execute 'then'
 * otherwise execute 'else'. For ease 'if' supports direct addition of steps which would be added to then block.
 * 
 * @author akiran
 */
@Executable(name = "pollAndCheck", group = Group.Lang, message = "Used to execute polling steps till check condition is met with specified interval gap. "
		+ "Validation will fail if required condition is not met or exceeds timeout.")
public class PollAndCheckStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd/MM HH:mm:ss");
	
	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Check Freemarker condition to be evaluated.", required = true, sourceType = SourceType.CONDITION)
	private String checkCondition;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@Param(description = "Group of steps/validations to be executed as part of polling.", required = true)
	@SkipParsing
	private List<IStep> poll;

	/**
	 * Polling interval duration.
	 */
	@Param(description = "Polling interval duration.", required = true, expectedType = Long.class, sourceType = SourceType.EXPRESSION)
	private Object pollingInterval;
	
	/**
	 * Polling interval time unit. Defaults to millis.
	 */
	@Param(description = "Polling interval time unit. Defaults to millis.", required = false)
	private TimeUnit pollingIntervalUnit = TimeUnit.MILLISECONDS;
	
	/**
	 * Timeout till which check condition will be tried. After this time, this validation will fail.
	 */
	@Param(description = "Timout till which check condition will be tried. After this time, this validation will fail.", required = true, expectedType = Long.class, sourceType = SourceType.EXPRESSION)
	private Object timeOut;

	/**
	 * Time out time unit. Defaults to millis.
	 */
	@Param(description = "Time out time unit. Defaults to millis.", required = false)
	private TimeUnit timeOutUnit = TimeUnit.MILLISECONDS;

	/**
	 * Sets the freemarker condition to be evaluated.
	 *
	 * @param checkCondition the new freemarker condition to be evaluated
	 */
	public void setCheckCondition(String checkCondition)
	{
		this.checkCondition = checkCondition;
	}

	/**
	 * Sets the group of steps/validations to be executed when condition evaluated to be true.
	 *
	 * @param poll the new group of steps/validations to be executed when condition evaluated to be true
	 */
	@ChildElement(description = "Used to specify polling steps.", required = true)
	public void setPoll(Function poll)
	{
		this.poll = new ArrayList<IStep>( poll.getSteps() );
	}
	
	/**
	 * Sets the polling interval duration in millis.
	 *
	 * @param pollingInterval the new polling interval duration in millis
	 */
	public void setPollingInterval(Object pollingInterval)
	{
		this.pollingInterval = pollingInterval;
	}
	
	/**
	 * Sets the polling interval time unit. Defaults to millis.
	 *
	 * @param pollingIntervalUnit the new polling interval time unit
	 */
	public void setPollingIntervalUnit(TimeUnit pollingIntervalUnit)
	{
		this.pollingIntervalUnit = pollingIntervalUnit;
	}

	/**
	 * Sets the timout in millis till which check condition will be tried. After this time, this validation will fail.
	 *
	 * @param timeOut the new timout in millis till which check condition will be tried
	 */
	public void setTimeOut(Object timeOut)
	{
		this.timeOut = timeOut;
	}
	
	/**
	 * Sets the time out time unit. Defaults to millis.
	 *
	 * @param timeOutUnit the new time out time unit
	 */
	public void setTimeOutUnit(TimeUnit timeOutUnit)
	{
		this.timeOutUnit = timeOutUnit;
	}
	
	private boolean checkCondition(AutomationContext context, IExecutionLogger exeLogger)
	{
		boolean res = AutomationUtils.evaluateCondition(context, checkCondition);
		
		if(res)
		{
			exeLogger.debug("Check condition was successful. Finishing polling step");
		}
		
		return res;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		Date startTime = new Date();
		exeLogger.debug("Starting time: {}. Check Condition: {}", TIME_FORMAT.format(startTime), checkCondition);
		
		int index = -1;
		
		while(true)
		{
			index++;
			
			//from second time check for debug point for current step
			if(index > 0)
			{
				DebugFlowManager.getInstance().checkForDebugPoint(this);
			}
			
			StepsExecutor.execute(poll, null);
			
			boolean conditionSuccessful = checkCondition(context, exeLogger);
			
			if(conditionSuccessful)
			{
				return;
			}
			
			long diff = System.currentTimeMillis() - startTime.getTime();
			
			if(diff > timeOutUnit.toMillis((Long) timeOut))
			{
				exeLogger.error("Check condition '{}' is not met till timeout of {} {}. Error Time: {}", checkCondition, timeOut, timeOutUnit, TIME_FORMAT.format(new Date()));
				throw new AutoxValidationException(this, "Check condition '{}' is not met till timeout of {} {}", checkCondition, timeOut, timeOutUnit);
			}
			
			exeLogger.trace("Check condition was not met. Process will wait for {} {} before re-executing polling steps", pollingInterval, pollingIntervalUnit);
			
			AutomationUtils.sleep(pollingIntervalUnit.toMillis((Long) pollingInterval));
		}
	}
}
