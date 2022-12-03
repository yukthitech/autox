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
package com.yukthitech.autox.ide.model;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.ide.rest.MultiPart;

public class RestInvocationModel
{
	String uri;
	List<Header> headers= new ArrayList<>();
	List<Param> paramList = new ArrayList<>();
	String rawBody;
	List<PathVariable> pathVariables= new ArrayList<>();
	List<MultiPart> multiPartlist= new ArrayList<>();
	public String getUri()
	{
		return uri;
	}
	public void setUri(String uri)
	{
		this.uri = uri;
	}
	public List<Header> getHeaders()
	{
		return headers;
	}
	public void setHeaders(List<Header> headers)
	{
		this.headers = headers;
	}
	
	public List<Param> getParamList()
	{
		return paramList;
	}
	public void setParamList(List<Param> paramList)
	{
		this.paramList = paramList;
	}
	public String getRawBody()
	{
		return rawBody;
	}
	public void setRawBody(String rawBody)
	{
		this.rawBody = rawBody;
	}
	public List<PathVariable> getPathVariables()
	{
		return pathVariables;
	}
	public void setPathVariables(List<PathVariable> pathVariables)
	{
		this.pathVariables = pathVariables;
	}
	public List<MultiPart> getMultiPartlist()
	{
		return multiPartlist;
	}
	public void setMultiPartlist(List<MultiPart> multiPartlist)
	{
		this.multiPartlist = multiPartlist;
	}
	
		
}
