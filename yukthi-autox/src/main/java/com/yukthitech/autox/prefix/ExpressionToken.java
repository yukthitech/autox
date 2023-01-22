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
package com.yukthitech.autox.prefix;

/**
 * Represents an expression token within main expression.
 * @author akranthikiran
 */
public class ExpressionToken
{
	/**
	 * Value of this token.
	 */
	private String value;
	
	/**
	 * Start index of this token.
	 */
	private int startIndex;
	
	/**
	 * End index (inclusive) of this token.
	 */
	private int endIndex;

	public ExpressionToken(String value, int startIndex, int endIndex)
	{
		this.value = value;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public String getValue()
	{
		return value;
	}

	public int getStartIndex()
	{
		return startIndex;
	}

	public int getEndIndex()
	{
		return endIndex;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("Token: ").append(value);
		builder.append(" (").append(startIndex).append(" - ").append(endIndex).append(")");

		return builder.toString();
	}

}
