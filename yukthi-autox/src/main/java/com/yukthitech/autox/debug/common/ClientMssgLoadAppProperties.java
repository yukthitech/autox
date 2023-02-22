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
package com.yukthitech.autox.debug.common;

import java.util.Properties;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.openqa.selenium.InvalidArgumentException;

/**
 * Used to reload app properties.
 * @author akiran
 */
public class ClientMssgLoadAppProperties extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	private Properties properties;
	
	public ClientMssgLoadAppProperties(Properties properties)
	{
		super(UUID.randomUUID().toString());
		
		if(MapUtils.isEmpty(properties))
		{
			throw new InvalidArgumentException("Properties cannot be empty");
		}

		this.properties = properties;
	}

	public Properties getProperties()
	{
		return properties;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Request Id: ").append(super.getRequestId());

		builder.append("]");
		return builder.toString();
	}
}
