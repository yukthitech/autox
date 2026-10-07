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
package com.yukthitech.autox.logmon;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats Chrome/Selenium browser console log text into human-readable form.
 * <p>
 * ChromeDriver's classic {@code LogType.BROWSER} API serializes each {@code console.*}
 * argument as a JSON string. Multi-arg calls (common with Vue warnings) therefore appear as:
 * <pre>
 * "[Vue warn]: ..." "\n" " at \u003CYkModelForm" "id=\"...\"" ...
 * </pre>
 * instead of the readable multi-line text shown in DevTools.
 */
public final class BrowserConsoleLogFormatter
{
	/**
	 * Prefix added by ChromeDriver: {@code <source-url> <line>:<col> <message>}.
	 */
	private static final Pattern SOURCE_PREFIX = Pattern.compile("^(\\S+)\\s+(\\d+):(\\d+)\\s+(.*)$", Pattern.DOTALL);

	private BrowserConsoleLogFormatter()
	{
	}

	/**
	 * Formats a raw browser log message from Selenium {@code LogEntry#getMessage()}.
	 *
	 * @param rawMessage raw message from ChromeDriver
	 * @return decoded, human-readable message (with real newlines where applicable)
	 */
	public static String format(String rawMessage)
	{
		if(rawMessage == null || rawMessage.isEmpty())
		{
			return rawMessage;
		}

		String body = rawMessage;
		Matcher matcher = SOURCE_PREFIX.matcher(rawMessage);

		if(matcher.matches())
		{
			body = matcher.group(4);
		}

		return decodeConsoleArguments(body);
	}

	/**
	 * Decodes a ChromeDriver console payload that may be a sequence of JSON-encoded
	 * string arguments joined by spaces.
	 */
	static String decodeConsoleArguments(String body)
	{
		if(body == null || body.isEmpty())
		{
			return body;
		}

		String trimmed = body.trim();

		// Plain exception / network messages are not JSON-quoted argument lists.
		if(!trimmed.startsWith("\""))
		{
			return body;
		}

		List<String> args = parseJsonStringSequence(trimmed);

		if(args.isEmpty())
		{
			return body;
		}

		// Mirror browser console joining of multiple arguments with a single space.
		return String.join(" ", args);
	}

	/**
	 * Parses consecutive JSON string literals (as emitted by ChromeDriver for console args).
	 */
	static List<String> parseJsonStringSequence(String input)
	{
		List<String> args = new ArrayList<>();
		int i = 0;
		int len = input.length();

		while(i < len)
		{
			while(i < len && Character.isWhitespace(input.charAt(i)))
			{
				i++;
			}

			if(i >= len)
			{
				break;
			}

			if(input.charAt(i) != '"')
			{
				// Remaining plain text (should not normally happen for console-api entries).
				args.add(input.substring(i));
				break;
			}

			i++; // opening quote
			StringBuilder arg = new StringBuilder();
			boolean closed = false;

			while(i < len)
			{
				char c = input.charAt(i++);

				if(c == '\\')
				{
					if(i >= len)
					{
						arg.append('\\');
						break;
					}

					char esc = input.charAt(i++);

					switch(esc)
					{
						case 'n':
							arg.append('\n');
							break;
						case 'r':
							arg.append('\r');
							break;
						case 't':
							arg.append('\t');
							break;
						case '"':
						case '\\':
						case '/':
							arg.append(esc);
							break;
						case 'u':
							if(i + 4 <= len)
							{
								arg.append((char) Integer.parseInt(input.substring(i, i + 4), 16));
								i += 4;
							}
							else
							{
								arg.append("\\u");
							}
							break;
						default:
							arg.append(esc);
							break;
					}
				}
				else if(c == '"')
				{
					closed = true;
					break;
				}
				else
				{
					arg.append(c);
				}
			}

			if(!closed && args.isEmpty() && arg.length() == 0)
			{
				return args;
			}

			args.add(arg.toString());
		}

		return args;
	}
}
