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

import com.yukthitech.utils.exceptions.UtilsException;

public class XmlParseException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	private XmlFile xmlFile;
	
	private int offset;
	
	private int endOffset;
	
	private int lineNumber;
	
	private int columnNumber;
	
	public XmlParseException(XmlFile xmlFile, int offset, int endOffset, int lineNo, int colNo, String mssg, Object... params)
	{
		super(getPositionString(lineNo, colNo) + mssg, params);
		
		this.offset = offset;
		this.endOffset = endOffset;
		this.xmlFile = xmlFile;
		this.lineNumber = lineNo;
		this.columnNumber = colNo;
	}
	
	private static String getPositionString(int lineNo, int colNo)
	{
		return "[Line: " + lineNo + ", Column: " + colNo + "] "; 
	}
	
	public int getOffset()
	{
		return offset;
	}
	
	public XmlFile getXmlFile()
	{
		return xmlFile;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public int getColumnNumber()
	{
		return columnNumber;
	}
	
	public int getEndOffset()
	{
		return endOffset;
	}
}
