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
package com.yukthitech.autox.plugin.rest;

import javax.servlet.http.HttpServletResponse;

import com.yukthitech.autox.event.EventManager;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.rest.IRestClientListener;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.utils.rest.RestResult;

/**
 * Listener to evaluate the response from rest clients.
 * @author akranthikiran
 */
public class AutoxRestClientListener implements IRestClientListener
{
	private String connectionName;
	
	private RestPluginSession session;
	
	public AutoxRestClientListener(String connectionName, RestPluginSession session)
	{
		this.connectionName = connectionName;
		this.session = session;
	}

	@Override
	public void postrequest(RestRequest<?> req, RestResult<?> res)
	{
		if(res.getStatusCode() != HttpServletResponse.SC_UNAUTHORIZED)
		{
			return;
		}
		
		Object handlerRes = EventManager.getInstance().invokePluginEventHandler(session, IRestPluginEvent.EVENT_UNAUTHORIZED, 
				CommonUtils.toMap("request", req, "result", res, "connectionName", connectionName));
		
		if("true".equalsIgnoreCase("" + handlerRes))
		{
			throw new UnauthorizedRequestException("Got unauthorized response");
		}
	}

	@Override
	public void prerequest(RestRequest<?> arg0)
	{
	}
}
