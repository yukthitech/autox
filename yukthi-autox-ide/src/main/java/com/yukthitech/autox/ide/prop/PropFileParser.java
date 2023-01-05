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
package com.yukthitech.autox.ide.prop;

import com.yukthitech.autox.ide.prop.PropertyFile.PropertyEntry;
import com.yukthitech.autox.ide.xmlfile.LocationRange;

public class PropFileParser
{
	private static class Tracker
	{
		char ch[];
		int lineNo;
		int column;
		
		int index;

		public Tracker(char[] ch)
		{
			this.ch = ch;
		}
		
		public boolean hasNextChar()
		{
			return (index < ch.length);
		}
		
		public char peekChar()
		{
			return this.ch[index];
		}
		
		public char nextChar()
		{
			char ch = this.ch[index];
			
			if(ch == '\n')
			{
				lineNo++;
				column = 0;
			}
			else
			{
				column++;
			}
			
			index++;
			return ch;
		}
	}
	
	public static PropertyFile parse(String content)
	{
		char ch[] = content.replace("\r", "").toCharArray();
		Tracker tracker = new Tracker(ch);
		
		PropertyFile file = new PropertyFile();
		PropertyEntry entry = null;
		
		while((entry = nextEntry(tracker)) != null)
		{
			file.addPropertyEntry(entry);
		}
		
		return file;
	}
	
	private static PropertyEntry nextEntry(Tracker tracker)
	{
		PropertyEntry entry = new PropertyEntry();
		parseKey(tracker, entry);
		
		if(entry.key == null || entry.key.length() == 0)
		{
			return null;
		}
		
		parseValue(tracker, entry);
		
		return entry;
	}
	
	private static void parseValue(Tracker tracker, PropertyEntry entry)
	{
		//skip whitespaces but not new line
		skipWhiteSpaces(tracker, true);
		
		StringBuilder value = new StringBuilder();

		LocationRange loc = new LocationRange();
		
		boolean start = true;
		
		while(tracker.hasNextChar())
		{
			char ch = tracker.peekChar();
			
			if(ch == '\\')
			{
				tracker.nextChar();
				
				if(tracker.hasNextChar())
				{
					ch = tracker.peekChar();
					
					if(ch == '\n')
					{
						tracker.nextChar();
						skipWhiteSpaces(tracker, true);
						continue;
					}
					
					if(start)
					{
						start = false;
						loc.setStartLocation(tracker.index, tracker.lineNo, tracker.column);
					}
					
					value.append(ch);
					tracker.nextChar();
				}
				
				continue;
			}
			
			if(start)
			{
				// this would be the case when the there are whitespaces
				/// before key-value separator
				if(ch == '=' || ch == ':')
				{
					tracker.nextChar();
					skipWhiteSpaces(tracker, true);
					continue;
				}
				
				loc.setStartLocation(tracker.index, tracker.lineNo, tracker.column);
				start = false;
			}

			if(ch == '\n')
			{
				entry.value = value.toString();
				entry.valueLocationRange = loc;
				
				tracker.nextChar();
				return;
			}
			
			value.append(ch);
			loc.setEndLocation(tracker.index, tracker.lineNo, tracker.column);
			tracker.nextChar();
		}
		
		//when EOF is reached before \n
		entry.value = value.toString();
		entry.valueLocationRange = loc;
	}
	
	private static void parseKey(Tracker tracker, PropertyEntry entry)
	{
		skipWhiteSpaces(tracker, false);
		
		StringBuilder key = new StringBuilder();
		LocationRange loc = new LocationRange();
		
		boolean start = true;
		
		while(tracker.hasNextChar())
		{
			char ch = tracker.peekChar();
			
			if(ch == '\\')
			{
				//skip \ char
				tracker.nextChar();
				
				if(tracker.hasNextChar())
				{
					ch = tracker.peekChar();
					
					if(ch == '\n')
					{
						tracker.nextChar();
						skipWhiteSpaces(tracker, true);
						continue;
					}
					
					if(start)
					{
						start = false;
						loc.setStartLocation(tracker.index, tracker.lineNo, tracker.column);
					}
					
					key.append(ch);
					tracker.nextChar();
				}
				
				continue;
			}
			
			if(start)
			{
				if(ch == '#' || ch == '!')
				{
					tracker.nextChar();
					skipComment(tracker);
					continue;
				}
				
				start = false;
				loc.setStartLocation(tracker.index, tracker.lineNo, tracker.column);
			}

			if(Character.isWhitespace(ch) || ch == '=' || ch == ':')
			{
				loc.setEndLocation(tracker.index, tracker.lineNo, tracker.column);
				entry.key = key.toString().trim();
				entry.keyLocationRange = loc;
				
				tracker.nextChar();
				return;
			}
			
			if(ch == '\n')
			{
				break;
			}
			
			key.append(ch);
			loc.setEndLocation(tracker.index, tracker.lineNo, tracker.column);
			tracker.nextChar();
		}
		
		if(key.length() > 0)
		{
			entry.key = key.toString().trim();
			entry.keyLocationRange = loc;
		}
	}
	
	private static void skipComment(Tracker tracker)
	{
		while(tracker.hasNextChar())
		{
			char ch = tracker.nextChar();
			
			if(ch == '\n')
			{
				skipWhiteSpaces(tracker, false);
				return;
			}
		}
	}

	private static void skipWhiteSpaces(Tracker tracker, boolean ignoreNewLine)
	{
		while(tracker.hasNextChar())
		{
			char ch = tracker.peekChar();
			
			if(ignoreNewLine && ch == '\n')
			{
				return;
			}
			
			if(!Character.isWhitespace(ch))
			{
				return;
			}
			
			//move to next char
			tracker.nextChar();
		}
	}
	
	/*
	private static void display(Map<String, String> map)
	{
		map.entrySet().forEach(entry -> {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		});
	}
	
	public static void main(String[] args) throws Exception
	{
		String content = FileUtils.readFileToString(new File("app.properties"), Charset.defaultCharset());
		
		Properties prop = new Properties();
		prop.load(new StringReader(content));
		
		display((Map) prop);
		
		System.out.println("\n\n=======================================\n");
		PropertyFile file = parse(content);
		System.out.println(file);
		
	}
	*/
}
