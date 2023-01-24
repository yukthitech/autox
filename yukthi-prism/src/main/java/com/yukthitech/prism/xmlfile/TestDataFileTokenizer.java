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

import java.util.function.Consumer;
import java.util.regex.Matcher;

import com.yukthitech.autox.config.AppConfigParserHandler;

/**
 * Used to find important patterns of text inside the text of test data files.
 * @author akranthikiran
 */
public class TestDataFileTokenizer
{
	public static final int TOKEN_TYPE_APP_PROP = 1;
	
	public static class Token
	{
		public int type;
		
		public String value;
		
		public int startOffset;
		
		public int endOffset;

		public Token(int type, String value, int startOffset, int endOffset)
		{
			this.type = type;
			this.value = value;
			this.startOffset = startOffset;
			this.endOffset = endOffset;
		}
	}
	
	public static void parse(String content, int startOffset, Consumer<Token> tokenConsumer)
	{
		Matcher matcher = AppConfigParserHandler.EXPR_PATTERN.matcher(content);
		
		while(matcher.find())
		{
			Token token = new Token(TOKEN_TYPE_APP_PROP, matcher.group(1), startOffset + matcher.start(1), startOffset + matcher.end(1));
			tokenConsumer.accept(token);
		}

	}
}
