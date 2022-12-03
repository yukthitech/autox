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
package com.yukthitech.autox.test;

/**
 * Abstract base data provider for test cases.
 * @author akiran
 */
public abstract class AbstractDataProvider implements IDataProvider
{
	/**
	 * Name of the data provider.
	 */
	private String name;
	
	/**
	 * Flag to enable expression parsing in data.
	 */
	private boolean parsingEnabled = false;

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.test.IDataProvider#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name of the data provider.
	 *
	 * @param name the new name of the data provider
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.test.IDataProvider#isParsingDisabled()
	 */
	public boolean isParsingEnabled()
	{
		return parsingEnabled;
	}
	
	/**
	 * Sets the flag to enable expression parsing in data.
	 *
	 * @param parsingEnabled the new flag to enable expression parsing in data
	 */
	public void setParsingEnabled(boolean parsingEnabled)
	{
		this.parsingEnabled = parsingEnabled;
	}
}
