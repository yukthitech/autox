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
package com.yukthitech.autox.ide.exeenv;

import com.yukthitech.autox.ide.services.IIdeEvent;

/**
 * Raised when content has to be truncated from env console.
 * @author akranthikiran
 */
public class ConsoleContentTruncateEvent implements IIdeEvent
{
	private ExecutionEnvironment executionEnvironment;
	
	/**
	 * Number of lines to be truncated.
	 */
	private int lines;

	public ConsoleContentTruncateEvent(ExecutionEnvironment executionEnvironment, int lines)
	{
		this.executionEnvironment = executionEnvironment;
		this.lines = lines;
	}

	public ExecutionEnvironment getExecutionEnvironment()
	{
		return executionEnvironment;
	}

	public int getLines()
	{
		return lines;
	}
}
