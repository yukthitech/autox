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
package com.yukthitech.autox.common;

/**
 * Thrown when a resource request for loading is not found.
 * @author akranthikiran
 */
public class ResourceNotFoundException extends AutoxInfoException
{
	private static final long serialVersionUID = 1L;
	
	public static enum ResourceType
	{
		FILE("file"),
		
		RESOURCE("resource");
		
		private String rep;

		private ResourceType(String rep)
		{
			this.rep = rep;
		}
		
		public String getRep()
		{
			return rep;
		}
	}
	
	private String resourcePath;
	
	private ResourceType resourceType;

	public ResourceNotFoundException(ResourceType resType, String resourcePath)
	{
		super("Specified {} is not found: {}", resType.rep, resourcePath);
		
		this.resourcePath = resourcePath;
		this.resourceType = resType;
	}
	
	public String getResourcePath()
	{
		return resourcePath;
	}
	
	public ResourceType getResourceType()
	{
		return resourceType;
	}
}
