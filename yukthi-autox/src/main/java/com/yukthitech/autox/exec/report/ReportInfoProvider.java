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
package com.yukthitech.autox.exec.report;

import java.util.function.Function;

/**
 * Base interface used to extract information from different executables.
 * @author akranthikiran
 */
public class ReportInfoProvider<T>
{
	private boolean singleLogger;
	
	private Function<T, String> codeFunc;
	
	private Function<T, String> nameFunc;
	
	private Function<T, String> descFunc;
	
	private Function<T, String> authorFunc;
	
	public ReportInfoProvider(boolean singleLogger, 
			Function<T, String> codeFunc, 
			Function<T, String> nameFunc, 
			Function<T, String> descFunc,
			Function<T, String> authorFunc)
	{
		this.singleLogger = singleLogger;
		this.codeFunc = codeFunc;
		this.nameFunc = nameFunc;
		this.descFunc = descFunc;
		this.authorFunc = authorFunc;
	}
	
	public boolean isSingleLogger()
	{
		return singleLogger;
	}

	public String getCode(T executor)
	{
		return codeFunc.apply(executor);
	}
	
	public String getName(T executor)
	{
		return nameFunc.apply(executor);
	}
	
	public String getDescription(T executor)
	{
		return descFunc.apply(executor);
	}

	public String getAuthor(T executor)
	{
		return authorFunc.apply(executor);
	}
}
