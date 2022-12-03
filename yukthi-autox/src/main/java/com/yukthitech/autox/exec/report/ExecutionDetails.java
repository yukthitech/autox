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
package com.yukthitech.autox.exec.report;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.test.TestStatus;

/**
 * Represents duration of execution.
 * @author akranthikiran
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionDetails
{
	/**
	 * Start time.
	 */
	private Date startTime = new Date();
	
	/**
	 * End time.
	 */
	private Date endTime;
	
	/**
	 * Status of execution.
	 */
	private TestStatus status = TestStatus.IN_PROGRESS;
	
	/**
	 * Status message.
	 */
	private String statusMessage;
	
	public Date setEndDetails(TestStatus status, String mssg)
	{
		this.endTime = new Date();
		this.status = status;
		this.statusMessage = mssg;
		
		return endTime;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}

	public TestStatus getStatus()
	{
		return status;
	}

	public String getStatusStr()
	{
		return status != null ? status.name() : "";
	}

	public void setStatus(TestStatus status)
	{
		this.status = status;
	}

	public String getTimeTaken()
	{
		return AutomationUtils.getTimeTaken(startTime, endTime);
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}
	
	public String getStartTimeStr()
	{
		return ApplicationConfiguration.getInstance().getTimeFormatObject().format(startTime);
	}

	public String getEndTimeStr()
	{
		if(endTime == null)
		{
			return "";
		}
		
		return ApplicationConfiguration.getInstance().getTimeFormatObject().format(endTime);
	}
}
