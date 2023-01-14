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
package com.yukthitech.autox.ide.xmlfile;

import org.fife.ui.rsyntaxtextarea.TokenImpl;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

public class IdeTokenImpl extends TokenImpl
{
	public IdeTokenImpl(char[] line, int beg, int end, int startOffset, int type, int languageIndex) 
	{
		if(beg < 0 || beg >= line.length)
		{
			throw new InvalidArgumentException("Invalid begin value specfied: " + beg);
		}
		
		if(end < 0 || end >= line.length)
		{
			throw new InvalidArgumentException("Invalid end value specfied: " + end);
		}
		
		if(end < beg)
		{
			throw new InvalidArgumentException("End value should be greater than begin value: [Beg: {}, End: {}]", beg, end);
		}

		super.set(line, beg, end, startOffset, type);
		super.setLanguageIndex(languageIndex);
	}
}
