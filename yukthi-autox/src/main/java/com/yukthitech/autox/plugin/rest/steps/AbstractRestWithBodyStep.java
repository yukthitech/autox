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
package com.yukthitech.autox.plugin.rest.steps;

import com.yukthitech.autox.Attributable;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.rest.RestPlugin;
import com.yukthitech.utils.rest.RestRequestWithBody;

/**
 * Used to invoke POST REST API.
 * @author akiran
 */
@Executable(name = "restInvokePost", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke POST api.")
public abstract class AbstractRestWithBodyStep<T extends RestRequestWithBody<T>> extends AbstractRestStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.
	 */
	@Param(description = "Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.", 
			required = false, sourceType = SourceType.EXPRESSION, attributable = Attributable.FALSE)
	private Object body;
	
	/**
	 * Content char set.
	 */
	@Param(description = "Content char set to be used for body.", required = false)
	private String contentCharset;
	
	/**
	 * Sets the body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.
	 *
	 * @param body the new body to be set
	 */
	@ChildElement(description = "Body of the request")
	public void setBody(Object body)
	{
		this.body = body;
	}
	
	/**
	 * Sets the content char set.
	 *
	 * @param contentCharset
	 *            the new content char set
	 */
	public void setContentCharset(String contentCharset)
	{
		this.contentCharset = contentCharset;
	}
	
	protected abstract T newRequest(String uri);
	
	@Override
	public void executeRestStep(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		T postRestRequest = newRequest(uri);
		
		if(body instanceof String)
		{
			postRestRequest.setBody((String) body);
		}
		else if(body != null)
		{
			postRestRequest.setJsonBody(body);
		}
		
		if(contentCharset != null)
		{
			postRestRequest.setContentCharset(contentCharset);
		}
		
		super.populate(context, postRestRequest, logger);
		super.invoke(context, postRestRequest, logger);
	}

}
