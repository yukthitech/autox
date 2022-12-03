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
package com.yukthitech.autox.logmon;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Base abstract for log monitors which provides functionality to manage name.
 */
public abstract class AbstractLogMonitor implements ILogMonitor, Validateable
{
	/**
	 * Name of the log monitor.
	 */
	private String name;
	
	/**
	 * If this flag is set, then only on error, the log will be fetched and added to report.
	 * Defaults to true.
	 */
	private boolean onErrorOnly = true;
	
	/**
	 * Flag indicating whether this log monitor is enabled or not.
	 */
	private boolean enabled = true;

	/**
	 * Gets the name of the log monitor.
	 *
	 * @return the name of the log monitor
	 */
	/* (non-Javadoc)
	 * @see com.yukthitech.automation.logmon.ILogMonitor#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the log monitor.
	 *
	 * @param name the new name of the log monitor
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Checks if is if this flag is set, then only on error, the log will be fetched and added to report. Defaults to true.
	 *
	 * @return the if this flag is set, then only on error, the log will be fetched and added to report
	 */
	public boolean isOnErrorOnly()
	{
		return onErrorOnly;
	}

	/**
	 * Sets the if this flag is set, then only on error, the log will be fetched and added to report. Defaults to true.
	 *
	 * @param onErrorOnly the new if this flag is set, then only on error, the log will be fetched and added to report
	 */
	public void setOnErrorOnly(boolean onErrorOnly)
	{
		this.onErrorOnly = onErrorOnly;
	}
	
	/**
	 * Checks if is flag indicating whether this log monitor is enabled or not.
	 *
	 * @return the flag indicating whether this log monitor is enabled or not
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets the flag indicating whether this log monitor is enabled or not.
	 *
	 * @param enabled the new flag indicating whether this log monitor is enabled or not
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * Validate.
	 *
	 * @throws ValidateException the validate exception
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("No/empty name specified for log monitor.");
		}
	}
}
