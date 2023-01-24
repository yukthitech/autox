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
package com.yukthitech.prism.layout;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Console line pattern according to which lines in which text in console will
 * be rendered.
 * 
 * @author akiran
 */
public class ConsoleLinePattern implements Validateable
{
	/**
	 * Line pattern to be matched.
	 */
	private Pattern pattern;

	/**
	 * Color to be used when matched.
	 */
	private String color;

	public Pattern getPattern()
	{
		return pattern;
	}

	public void setPattern(String linePattern)
	{
		this.pattern = Pattern.compile(linePattern);
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	@Override
	public void validate() throws ValidateException
	{
		if(pattern == null)
		{
			throw new ValidateException("No pattern is specified.");
		}
		
		if(StringUtils.isBlank(color))
		{
			throw new ValidateException("No color is specified.");
		}
	}
}
