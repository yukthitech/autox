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
package com.yukthitech.utils.beans;

/**
 * Information about a property
 * @author akiran
 */
public class PropertyInfo
{
	/**
	 * Actual property
	 */
	private NestedProperty property;
	
	/**
	 * as a destination this property should be ignored
	 */
	private boolean ignoreDestination;

	/**
	 * Instantiates a new property info.
	 *
	 * @param property the property
	 * @param ignoreDestination the ignore destination
	 */
	public PropertyInfo(NestedProperty property, boolean ignoreDestination)
	{
		this.property = property;
		this.ignoreDestination = ignoreDestination;
	}
	
	public String getName()
	{
		return property.getName();
	}

	/**
	 * Gets the actual property.
	 *
	 * @return the actual property
	 */
	public NestedProperty getProperty()
	{
		return property;
	}

	/**
	 * Checks if is as a destination this property should be ignored.
	 *
	 * @return the as a destination this property should be ignored
	 */
	public boolean isIgnoreDestination()
	{
		return ignoreDestination;
	}
	
}
