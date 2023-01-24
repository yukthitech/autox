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
package com.yukthitech.prism.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattingRule
{
	private Pattern pattern;
	
	private IndentAction indentAction;
	
	private Pattern excludePattern;

	public FormattingRule(String pattern, IndentAction indentAction)
	{
		this(pattern, indentAction, null);
	}
	
	public FormattingRule(String pattern, IndentAction indentAction, String excludePattern)
	{
		this.pattern = Pattern.compile(pattern);
		this.indentAction = indentAction;
		
		if(excludePattern != null)
		{
			this.excludePattern = Pattern.compile(excludePattern);
		}
	}

	public IndentAction getIndentAction()
	{
		return indentAction;
	}
	
	public boolean isMatching(String line)
	{
		Matcher matcher = pattern.matcher(line);
		boolean matched = matcher.find();
		
		if(excludePattern == null)
		{
			return matched;
		}
		
		if(!matched)
		{
			return false;
		}
		
		matcher = excludePattern.matcher(line);
		return matcher.find();
	}
	
}
