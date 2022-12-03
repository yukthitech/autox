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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;

/**
 * Base class for client messages.
 * @author akranthikiran
 */
public class ClientMessage implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String requestId;
	
	protected ClientMessage(String requestId)
	{
		if(StringUtils.isEmpty(requestId))
		{
			throw new InvalidArgumentException("Request id cannot be null or empty");
		}
		
		this.requestId = requestId;
	}
	
	public String getRequestId()
	{
		return requestId;
	}
}
