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
 * Represent cdata text content of the node.
 * @author akiran
 */
public class CdataNode implements INode
{
	/**
	 * Content of the text node.
	 */
	private String content;
	
	/**
	 * Start location of cdata section.
	 */
	private LocationRange location;
	
	/**
	 * Value location enclosed by this cdata section.
	 */
	private LocationRange valueLocation;

	/**
	 * Instantiates a new cdata node.
	 *
	 * @param content the content
	 * @param location the location
	 */
	public CdataNode(String content, LocationRange location, LocationRange valueLocation)
	{
		this.content = content;
		this.location = location;
		this.valueLocation = valueLocation;
	}

	/**
	 * Gets the content of the text node.
	 *
	 * @return the content of the text node
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Gets the start location of cdata section.
	 *
	 * @return the start location of cdata section
	 */
	public LocationRange getLocation()
	{
		return location;
	}
	
	public LocationRange getValueLocation()
	{
		return valueLocation;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.prism.xmlfile.INode#toText(java.lang.String, java.lang.StringBuilder)
	 */
	@Override
	public void toText(String indent, StringBuilder builder)
	{
		builder.append("<![CDATA[");
		String lines[] = content.split("\\n");
		
		for(String line : lines)
		{
			builder.append("\n").append(indent).append(line.trim());
		}
		
		builder.append("\n").append(indent).append("]]>");
	}
}
