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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.index.FileParseCollector;
import com.yukthitech.autox.ide.xmlfile.PatternScanner.ScannerMatch;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class XmlFileParser
{
	public static Pattern PATTERN_VER_TAG_OPENER = Pattern.compile("\\<\\?");
	
	public static Pattern PATTERN_VER_TAG_CLOSER = Pattern.compile("\\?\\>");
	
	public static Pattern PATTERN_COMMENT_TAG_OPENER = Pattern.compile("\\<\\!\\-\\-");
	
	public static Pattern PATTERN_COMMENT_TAG_CLOSER = Pattern.compile("\\-\\-\\>");
	
	public static Pattern PATTERN_DOCTYPE_TAG_OPENER = Pattern.compile("\\<\\!\\s*\\w+");
	
	public static Pattern PATTERN_DOCTYPE_TAG_CLOSER = Pattern.compile("\\>");

	public static Pattern PATTERN_TAG_OPENER = Pattern.compile("\\<");
	
	public static Pattern PATTERN_TAG_CLOSER = Pattern.compile("\\s*\\>");
	
	public static Pattern PATTERN_CDATA_OPENER = Pattern.compile("\\<\\!\\[CDATA\\[");
	
	public static Pattern PATTERN_CDATA_CLOSER = Pattern.compile("\\]\\]\\>");

	public static Pattern PATTERN_START_TAG = Pattern.compile("\\<([\\w\\-\\:\\.]+)");
	
	public static Pattern PATTERN_NODE_SELF_CLOSE = Pattern.compile("\\s*\\/\\>");
	
	public static Pattern PATTERN_CLOSE_TAG = Pattern.compile("\\<\\/([\\w\\-\\:\\.]+)\\s*\\>");
	
	public static Pattern PATTERN_PREFIX_NAME = Pattern.compile("([\\w-]+)\\:([\\w\\-\\.]+)");
	
	public static Pattern PATTERN_WHITE_SPACE = Pattern.compile("\\s*");
	
	public static Pattern PATTERN_NAME = Pattern.compile("([\\w\\-\\:\\.]+)");
	
	public static Pattern PATTERN_ATTR = Pattern.compile("\\s*([\\w\\-\\:\\.]+)\\s*\\=\\s*\"(.*?)\"", Pattern.DOTALL);
	
	private static enum EventType
	{
		START_TAG,
		
		CLOSE_TAG,
		
		CDATA_TAG,
		
		COMMENT
	}
	
	public static class Range
	{
		int line;
		
		int start;
		
		int end;

		public Range(int line, int start, int end)
		{
			this.line = line;
			this.start = start;
			this.end = end;
		}
		
		public int getColumn(int pos)
		{
			return (pos - start) + 1;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder(super.toString());
			builder.append("[");

			builder.append("Start: ").append(start);
			builder.append(",").append("End: ").append(end);
			builder.append(",").append("Line: ").append(line);

			builder.append("]");
			return builder.toString();
		}

	}
	
	private Range lineRanges[];
	
	private int rangeIndex = 0;
	
	private char chArr[];
	
	private PatternScanner scanner;
	
	private Element currentElement = null;
	
	private Element rootElement = null;
	
	private String content;
	
	private FileParseCollector collector;
	
	private XmlFileParser(String content, FileParseCollector collector)
	{
		this.content = content;
		this.collector = collector;
		this.chArr = content.toCharArray();
		this.lineRanges = fetchLineRanges(chArr);
		this.scanner = new PatternScanner(content);
	}
	
	private boolean isXmlChar(char ch)
	{
		if(ch == '\n' || ch == '\r' || ch == '\t')
		{
			return true;
		}
		
		return (ch >= 32 && ch <= 126);
	}
	
	private Range[] fetchLineRanges(char content[])
	{
		int line = 1;
		int start = 0;
		List<Range> ranges = new ArrayList<>();
		
		for(int i = 0; i < content.length; i++)
		{
			if(content[i] == '\n')
			{
				ranges.add(new Range(line, start, i));
				start = i + 1;
				line++;
				continue;
			}
			
			if(collector != null && !isXmlChar(content[i]))
			{
				collector.addMessage(new FileParseMessage(MessageType.WARNING, "Non ASCII or non-printable character is used.", line, i, i + 1));
			}
		}
		
		if(start < content.length)
		{
			ranges.add(new Range(line, start, content.length - 1));
		}
		
		return ranges.toArray(new Range[0]);
	}
	
	private Range moveToPositon(int pos)
	{
		for(int i = rangeIndex; i < lineRanges.length; i++)
		{
			if(lineRanges[i].start > pos)
			{
				throw new InvalidStateException("Current position {} is older than current range: {}", pos, lineRanges[i]);
			}
			
			if(lineRanges[i].end >= pos)
			{
				rangeIndex = i;
				return lineRanges[i];
			}
		}
		
		throw new InvalidStateException("Failed to find range for position {}\nCurrent Ranges: {}", pos, Arrays.toString(lineRanges));
	}
	
	private Range getLineRange(int pos)
	{
		for(int i = 0; i < lineRanges.length; i++)
		{
			if(lineRanges[i].end >= pos)
			{
				rangeIndex = i;
				return lineRanges[i];
			}
		}

		return null;
	}

	private String[] split(String name, int offset, int lineNo, int colNo)
	{
		int idx = name.indexOf(":");
		
		if(idx >= 0)
		{
			Matcher matcher = PATTERN_PREFIX_NAME.matcher(name);
			
			if(!matcher.matches())
			{
				throw new XmlParseException(
						new XmlFile(rootElement), 
						offset, offset + name.length(), 
						lineNo, colNo,
						"Invalid node name format encountered: {}", name);
			}
			
			return new String[] {matcher.group(1), matcher.group(2)};
		}
		
		return new String[] {null, name};
	}
	
	private void parseStartTag()
	{
		if(scanner.next(PATTERN_START_TAG) == null)
		{
			throw new InvalidStateException("Failed to find start of tag at current position");
		}
		
		ScannerMatch match = scanner.getLastMatch();
		Range curRange = moveToPositon(match.start());
		int col = curRange.getColumn(match.start());
		
		String nameTokens[] = split(match.group(1), match.start(), curRange.line, col);
		String prefix = nameTokens[0];
		String name = nameTokens[1];

		String namespace = (currentElement != null) ? currentElement.getNamespaceWithPrefix(prefix) : null;
		Element newElement = new Element(currentElement, prefix, namespace, name, new LocationRange(match.start(), curRange.line, col),
				new IndexRange(match.start(1), match.end(1)));
		
		if(currentElement != null)
		{
			currentElement.addNode(newElement);
		}

		if(rootElement == null)
		{
			rootElement = newElement;
		}
		
		while(scanner.next(PATTERN_ATTR) != null)
		{
			match = scanner.getLastMatch();
			curRange = moveToPositon(match.start());
			col = curRange.getColumn(match.start());
			
			nameTokens = split(match.group(1), match.start(), curRange.line, col);
			prefix = nameTokens[0];
			name = nameTokens[1];
			
			if("xmlns".equals(prefix))
			{
				newElement.addNameSpaceMapping(name, match.group(2));
				continue;
			}
			
			//get name location details
			col = curRange.getColumn(match.start(1));
			LocationRange nameLoc = new LocationRange(
					match.start(1), curRange.line, col, match.end(1), 
					curRange.line, col + match.group(1).length() - 1);
			
			//get value location details
			Range valRange = moveToPositon(match.start(2));
			LocationRange valueLoc = new LocationRange(match.start(2), valRange.line, valRange.getColumn(match.start(2)));

			valRange = moveToPositon(match.end(2));
			int valEndCol = curRange.getColumn(match.end(2)) - 1;
			valueLoc.setEndLocation(match.end(2), valRange.line, valEndCol);
			
			Attribute attr = new Attribute(prefix, newElement.getNamespaceWithPrefix(prefix), name, match.group(2), nameLoc, valueLoc);
			newElement.addAttribute(attr);
		}
		
		if(scanner.next(PATTERN_NODE_SELF_CLOSE) != null)
		{
			setCurrentPositionAsEnd(newElement.getStartLocation(), false);
			newElement.setEndLocation(newElement.getStartLocation());
			return;
		}
		
		if(scanner.next(PATTERN_TAG_CLOSER) == null)
		{
			throw new XmlParseException(
					new XmlFile(rootElement), 
					newElement.getStartLocation().getStartOffset(), match.end(), 
					curRange.line, match.end() + 1, 
					"Unable to find closing bracket (>) for tag: {}", newElement.getName());
		}

		setCurrentPositionAsEnd(newElement.getStartLocation(), false);
		this.currentElement = newElement;
		
		while(true)
		{
			EventType eventType = parseNextContent();
			
			if(eventType == EventType.CLOSE_TAG)
			{
				break;
			}
		}
	}
	
	private LocationRange setCurrentPositionAsStart(LocationRange locationRange, boolean starting)
	{
		ScannerMatch match = scanner.getLastMatch();
		int pos = starting ? match.start() : match.end();
		
		Range curRange = moveToPositon(pos);
		int col = curRange.getColumn(pos);
		
		locationRange.setStartLocation(pos, curRange.line, col);
		return locationRange;
	}
	
	private LocationRange setCurrentPositionAsEnd(LocationRange locationRange, boolean starting)
	{
		ScannerMatch match = scanner.getLastMatch();
		int pos = starting ? (match.start() - 1) : (match.end() - 1);
		
		Range curRange = moveToPositon(pos);
		int col = curRange.getColumn(pos);
		
		locationRange.setEndLocation(pos, curRange.line, col);
		return locationRange;
	}

	private void parseCloseTag()
	{
		if(scanner.next(PATTERN_CLOSE_TAG) == null)
		{
			throw new InvalidStateException("Failed to find close of tag at current position");
		}
		
		ScannerMatch match = scanner.getLastMatch();
		Range curRange = moveToPositon(match.start());
		int col = curRange.getColumn(match.start());
		
		String nameTokens[] = split(match.group(1), match.start(), curRange.line, col);
		String prefix = nameTokens[0];
		String name = nameTokens[1];
		
		if(!Objects.equals(prefix, currentElement.getPrefix()) || !Objects.equals(name, currentElement.getName()))
		{
			throw new XmlParseException(
					new XmlFile(rootElement), 
					match.start(), match.end(), 
					curRange.line, col, 
					"When expecting close tag </{}> found close tag </{}>", currentElement.getFullElementName(), match.group(1));
		}
		
		currentElement.setEndLocation(setCurrentPositionAsStart(new LocationRange(), true));
		setCurrentPositionAsEnd(currentElement.getEndLocation(), false);
		currentElement = currentElement.getParentElement();
	}
	
	private void parseCdataContent()
	{
		if(scanner.next(PATTERN_CDATA_OPENER) == null)
		{
			throw new InvalidStateException("Failed to find opening of cdata tag at current position");
		}
		
		LocationRange locRange = new LocationRange();
		setCurrentPositionAsStart(locRange, true);
		
		LocationRange valRange = new LocationRange();
		setCurrentPositionAsStart(valRange, false);

		ScannerMatch match = scanner.getLastMatch();
		Range curRange = moveToPositon(match.start());
		int col = curRange.getColumn(match.start());

		String text = scanner.skipTill(PATTERN_CDATA_CLOSER);
		
		//if end of ctag is not found
		if(text == null)
		{
			throw new XmlParseException(
					new XmlFile(rootElement), 
					match.start(), content.length() - 1, 
					curRange.line, col, 
					"CDATA ending is not found");
		}
		
		scanner.next(PATTERN_CDATA_CLOSER);
		setCurrentPositionAsEnd(locRange, false);
		setCurrentPositionAsEnd(valRange, true);

		currentElement.addNode(new CdataNode(text, locRange, valRange));
	}
	
	private EventType parseNextContent()
	{
		int curPos = scanner.getPosition();
		Range range = moveToPositon(curPos);
		int col = range.getColumn(curPos);

		String textContent = scanner.skipTill(PATTERN_TAG_OPENER);
		
		if(scanner.hasNext(PATTERN_VER_TAG_OPENER))
		{
			scanner.skipTill(PATTERN_VER_TAG_CLOSER);
			scanner.skip(PATTERN_VER_TAG_CLOSER);
			return parseNextContent();
		}
		
		if(scanner.hasNext(PATTERN_COMMENT_TAG_OPENER))
		{
			scanner.skipTill(PATTERN_COMMENT_TAG_CLOSER);
			scanner.skip(PATTERN_COMMENT_TAG_CLOSER);
			return parseNextContent();
		}

		if(scanner.hasNext(PATTERN_DOCTYPE_TAG_OPENER))
		{
			scanner.skipTill(PATTERN_DOCTYPE_TAG_CLOSER);
			scanner.skip(PATTERN_DOCTYPE_TAG_CLOSER);
			return parseNextContent();
		}

		if(textContent == null)
		{
			curPos = content.length() - 1;
			range = moveToPositon(curPos);
			col = range.getColumn(curPos);
			
			throw new XmlParseException(new XmlFile(rootElement), 
					curPos, content.length() - 1,
					range.line, col, 
					"EOF encountered when expecting next tag");
		}
		
		if(textContent.trim().length() > 0)
		{
			int idx = textContent.indexOf(">");
			
			if(idx >= 0)
			{
				int errPos = curPos + idx;
				Range errLine = getLineRange(errPos);
				int errCol = errPos - errLine.start;
				
				throw new XmlParseException(new XmlFile(rootElement), 
						errPos, errPos + 1,
						errLine.line, errCol, 
						"Text content cannot have '>' character");
			}
			
			LocationRange loc = new LocationRange(curPos, range.line, col);
			setCurrentPositionAsEnd(loc, true);
			
			currentElement.addNode(new TextNode(textContent, loc));
		}

		if(scanner.hasNext(PATTERN_START_TAG))
		{
			parseStartTag();
			return EventType.START_TAG;
		}
		else if(scanner.hasNext(PATTERN_CLOSE_TAG))
		{
			parseCloseTag();
			return EventType.CLOSE_TAG;
		}
		else if(scanner.hasNext(PATTERN_CDATA_OPENER))
		{
			parseCdataContent();
			return EventType.CDATA_TAG;
		}
		
		range = moveToPositon(scanner.getPosition());
		col = range.getColumn(scanner.getPosition());
		throw new XmlParseException(
				new XmlFile(rootElement), 
				scanner.getPosition(), content.length() - 1, 
				range.line, col, "Unsupported content found.");
	}
	
	private Element parseElement()
	{
		scanner.skip(PATTERN_WHITE_SPACE);
		parseNextContent();
		
		return rootElement;
	}
	
	public static XmlFile parse(String content, FileParseCollector collector)
	{
		if(StringUtils.isBlank(content))
		{
			return null;
		}
		
		XmlFileParser parser = new XmlFileParser(content, collector);
		Element rootElement = parser.parseElement();
		return new XmlFile(rootElement);
	}
}
