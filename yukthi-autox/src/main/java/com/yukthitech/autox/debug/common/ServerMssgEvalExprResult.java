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

import org.apache.commons.lang3.StringUtils;

/**
 * Response message for eval expression along with result.
 * @author akranthikiran
 */
public class ServerMssgEvalExprResult extends ServerMssgConfirmation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Result value.
	 */
	private byte[] result;
	
	public ServerMssgEvalExprResult(String requestId, boolean successful, Object result, String errorMssg, Object... mssgArgs)
	{
		super(requestId, successful, errorMssg, mssgArgs);
		this.result = DebugUtils.serialize(result);
	}

	public byte[] getResult()
	{
		return result;
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
		builder.append(",").append("Successful: ").append(super.isSuccessful());
		builder.append(",").append("Error: ").append(super.getError());
		builder.append(",").append("Result: ").append(StringUtils.left("" + result, 100));

		builder.append("]");
		return builder.toString();
	}
}
