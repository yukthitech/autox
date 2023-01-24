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
package com.yukthitech.prism.index;

import java.util.List;

/**
 * Represents an element which is referring other element.
 * @author akranthikiran
 */
public class ReferenceElement
{
	/**
	 * Type of this referable element.
	 */
	private ReferenceType type;
	
	/**
	 * Name of the element.
	 */
	private String name;
	
	/**
	 * Scope in which referred element can be present.
	 */
	private List<String> scopes;
	
	/**
	 * Start position of the reference.
	 */
	private int startPostion;
	
	/**
	 * End position of the reference.
	 */
	private int endPosition;

	public ReferenceElement(ReferenceType type, String name, List<String> scopes, int startPostion, int endPosition)
	{
		this.type = type;
		this.name = name;
		this.scopes = scopes;
		this.startPostion = startPostion;
		this.endPosition = endPosition;
	}

	public ReferenceType getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}
	
	public List<String> getScopes()
	{
		return scopes;
	}

	public int getStartPostion()
	{
		return startPostion;
	}

	public int getEndPosition()
	{
		return endPosition;
	}
	
	public boolean includes(int idx)
	{
		return (idx >= startPostion && idx < endPosition);
	}
}
