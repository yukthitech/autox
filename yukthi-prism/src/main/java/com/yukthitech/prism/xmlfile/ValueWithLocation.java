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
 * Text value along with location.
 * @author akiran
 */
public class ValueWithLocation
{
	private String value;
	
	/**
	 * Location of name defining this value.
	 */
	private LocationRange nameLocation;
	
	/**
	 * Value location.
	 */
	private LocationRange location;

	public ValueWithLocation(String value, LocationRange nameLocation, LocationRange location)
	{
		this.value = value;
		this.nameLocation = nameLocation;
		this.location = location;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public LocationRange getNameLocation()
	{
		return nameLocation;
	}
	
	public LocationRange getLocation()
	{
		return location;
	}
}
