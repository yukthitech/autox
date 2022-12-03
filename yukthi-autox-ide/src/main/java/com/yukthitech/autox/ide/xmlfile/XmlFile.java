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

import com.yukthitech.autox.ide.FileParseCollector;

public class XmlFile
{
	private Element rootElement;

	/**
	 * Line number till which parsing is done to generate this xml file. If the full content is parsed this
	 * value will be -1.
	 */
	private int parsedTill = -1;
	
	public XmlFile()
	{
	}
	
	public XmlFile(Element rootElement)
	{
		this.rootElement = rootElement;
	}
	
	public Element getElement(String withName, int curLineNo)
	{
		if(!rootElement.hasLine(curLineNo))
		{
			return null;
		}

		return rootElement.getElement(withName.toLowerCase(), curLineNo);
	}
	
	public static XmlFile parse(String content, int validPosition, FileParseCollector collector) throws Exception
	{
		boolean partial = (validPosition > 0);
		
		try
		{
			return XmlFileParser.parse(content, collector);
		}catch(XmlParseException ex)
		{
			if(!partial)
			{
				throw ex;
			}
			
			int posFromEx = ex.getOffset();
			
			if(posFromEx >= validPosition)
			{
				XmlFile xmlFile = ex.getXmlFile();
				xmlFile.parsedTill = validPosition;
				return xmlFile;
			}
			
			throw ex;
		}
	}
	
	public Element getRootElement()
	{
		return rootElement;
	}
	
	public Element getLastElement(int offset)
	{
		return rootElement.getLastElement(offset);
	}
	
	/**
	 * Returns flag indicating if this file object is created by full or partial parsing.
	 * @return
	 */
	public boolean isPartiallyParsed()
	{
		return parsedTill >= 0;
	}
	
	/**
	 * Gets the line number till which parsing is done to generate this xml file. If the full content is parsed this value will be -1.
	 *
	 * @return the line number till which parsing is done to generate this xml file
	 */
	public int getParsedTill()
	{
		return parsedTill;
	}
	
	public String getNamespaceWithPrefix(String prefix)
	{
		return rootElement.getNamespaceWithPrefix(prefix);
	}
	
	public String getPrefixForNamespace(String... namespaces)
	{
		for(String namespace : namespaces)
		{
			String val = rootElement.getPrefixForNamespace(namespace);
			
			if(val != null)
			{
				return val;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + " - " + rootElement.toString();
	}
}
