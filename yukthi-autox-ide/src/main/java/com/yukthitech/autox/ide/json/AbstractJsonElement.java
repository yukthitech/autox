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
package com.yukthitech.autox.ide.json;

public class AbstractJsonElement implements IJsonElement
{
	private int startLineNumber;
	
	private int endLineNumber;
	
	private int startColumnNumber;
	
	private int endColumnNumber;

	@Override
	public int getStartLineNumber()
	{
		return startLineNumber;
	}

	public void setStartLineNumber(int startLineNumber)
	{
		this.startLineNumber = startLineNumber;
	}

	@Override
	public int getEndLineNumber()
	{
		return endLineNumber;
	}

	public void setEndLineNumber(int endLineNumber)
	{
		this.endLineNumber = endLineNumber;
	}

	@Override
	public int getStartColumnNumber()
	{
		return startColumnNumber;
	}

	public void setStartColumnNumber(int startColumnNumber)
	{
		this.startColumnNumber = startColumnNumber;
	}

	@Override
	public int getEndColumnNumber()
	{
		return endColumnNumber;
	}

	public void setEndColumnNumber(int endColumnNumber)
	{
		this.endColumnNumber = endColumnNumber;
	}
}
