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

/**
 * Used to send success/failure confirmation for client request.
 * @author akranthikiran
 */
public class ServerMssgConfirmation implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String requestId;
	
	/**
	 * Flag indicating corresponding request processing is successful or not.
	 */
	private boolean successful;
	
	/**
	 * Error details in case request processing failed.
	 */
	private String error;

	public ServerMssgConfirmation(String requestId, boolean successful, String errorMssg, Object... mssgArgs)
	{
		this.requestId = requestId;
		this.successful = successful;
		
		if(errorMssg != null)
		{
			this.error = String.format(errorMssg, mssgArgs);
		}
	}

	public String getRequestId()
	{
		return requestId;
	}

	public boolean isSuccessful()
	{
		return successful;
	}

	public String getError()
	{
		return error;
	}
}
