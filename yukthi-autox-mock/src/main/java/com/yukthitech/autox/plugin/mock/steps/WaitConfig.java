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
package com.yukthitech.autox.plugin.mock.steps;

/**
 * Wait configuration to be used before sending response.
 * @author akiran
 */
public class WaitConfig
{
	/**
	 * Time to wait before sending response.
	 */
	private long time;
	
	/**
	 * Wait for this attribute to be set.
	 */
	private String forAttr;

	/**
	 * Gets the time to wait before sending response.
	 *
	 * @return the time to wait before sending response
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 * Sets the time to wait before sending response.
	 *
	 * @param time the new time to wait before sending response
	 */
	public void setTime(long time)
	{
		this.time = time;
	}

	/**
	 * Gets the wait for this attribute to be set.
	 *
	 * @return the wait for this attribute to be set
	 */
	public String getForAttr()
	{
		return forAttr;
	}

	/**
	 * Sets the wait for this attribute to be set.
	 *
	 * @param forAttr the new wait for this attribute to be set
	 */
	public void setForAttr(String forAttr)
	{
		this.forAttr = forAttr;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.getClass().getSimpleName());
		builder.append("[");

		builder.append("Wait: ").append(time);
		builder.append(",").append("forAttr: ").append(forAttr);

		builder.append("]");
		return builder.toString();
	}

}
