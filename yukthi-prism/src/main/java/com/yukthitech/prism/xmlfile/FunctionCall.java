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
package com.yukthitech.prism.xmlfile;

/**
 * Represents a function call.
 * @author akiran
 */
public class FunctionCall
{
	/**
	 * Name of function being invoked.
	 */
	private String name;

	/**
	 * Instantiates a new function call.
	 *
	 * @param name the name
	 */
	public FunctionCall(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the name of function being invoked.
	 *
	 * @return the name of function being invoked
	 */
	public String getName()
	{
		return name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}

}
