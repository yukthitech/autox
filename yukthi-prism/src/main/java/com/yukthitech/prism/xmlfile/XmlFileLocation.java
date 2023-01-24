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

/**
 * Represents location in the xml file.
 * @author akiran
 */
public class XmlFileLocation
{
	/**
	 * Type of the xml location.
	 */
	private XmlLocationType type;
	
	/**
	 * Parent element.
	 */
	private Element parentElement;
	
	/**
	 * Namespace prefix used.
	 */
	private String prefix;
	
	/**
	 * Name space of the element or attribute.
	 */
	private String nameSpace;
	
	/**
	 * Current token.
	 */
	private String currentToken;
	
	/**
	 * Name of the element or attribute.
	 */
	private String name;
	
	/**
	 * Attribute/element Text before current position.
	 */
	private String text;
	
	/**
	 * Source xml file.
	 */
	private XmlFile xmlFile;
	
	/**
	 * If current location is specific to element, then this value indicates the indentation
	 * used for the element.
	 */
	private String indentation;
	
	/**
	 * Flag indicating if current location needs full element generation. If the element already contains
	 * attributes or any other text then this flag will be false.
	 */
	private boolean fullElementGeneration;
	
	public XmlFileLocation(XmlFile xmlFile)
	{
		this.xmlFile = xmlFile;
	}
	
	private static void splitNameToken(Element element, String token, XmlFileLocation loc)
	{
		if(token == null)
		{
			return;
		}
		
		loc.currentToken = token;
		
		token = token.trim();
		int idx = 0;
		
		if( (idx = token.indexOf(":")) > 0)
		{
			loc.prefix = token.substring(0, idx);
			loc.name = token.substring(idx + 1);
			
			loc.nameSpace = element.getNamespaceWithPrefix(loc.prefix);
		}
		else
		{
			loc.name = token;
		}
	}
	
	public static XmlFileLocation newAttributeLocation(XmlFile xmlFile, Element currentElement, String currentToken, boolean fullElement)
	{
		XmlFileLocation loc = new XmlFileLocation(xmlFile);
		loc.setType(XmlLocationType.ATTRIBUTE);
		loc.currentToken = currentToken;
		loc.parentElement = currentElement;
		loc.fullElementGeneration = fullElement;
		
		splitNameToken(currentElement, currentToken, loc);
		
		return loc;
	}

	public static XmlFileLocation newAttributeValueLocation(XmlFile xmlFile, Element currentElement, String attrName, String text, String currentToken)
	{
		XmlFileLocation loc = new XmlFileLocation(xmlFile);
		loc.setType(XmlLocationType.ATTRIBUTE_VALUE);
		loc.parentElement = currentElement;
		
		splitNameToken(currentElement, attrName, loc);
		loc.text = text;
		loc.currentToken = currentToken;
		
		return loc;
	}

	public static XmlFileLocation newElementLocation(XmlFile xmlFile, Element parentElement, String currentToken, String indentation, boolean fullElement)
	{
		XmlFileLocation loc = new XmlFileLocation(xmlFile);
		loc.setType(XmlLocationType.CHILD_ELEMENT);
		loc.parentElement = parentElement;
		loc.indentation = indentation;
		loc.fullElementGeneration = fullElement;
		
		splitNameToken(parentElement, currentToken, loc);
		
		return loc;
	}

	public static XmlFileLocation newTextElementLocation(XmlFile xmlFile, Element parentElement, String text, String currentToken)
	{
		XmlFileLocation loc = new XmlFileLocation(xmlFile);
		loc.setType(XmlLocationType.TEXT_ELEMENT);
		loc.parentElement = parentElement;
		
		loc.text = text;
		loc.currentToken = currentToken;
		
		return loc;
	}

	public XmlLocationType getType()
	{
		return type;
	}

	public void setType(XmlLocationType type)
	{
		this.type = type;
	}

	public Element getParentElement()
	{
		return parentElement;
	}

	public void setParentElement(Element parentElement)
	{
		this.parentElement = parentElement;
	}

	public String getCurrentToken()
	{
		return currentToken;
	}

	public void setCurrentToken(String currentToken)
	{
		this.currentToken = currentToken;
	}

	public String getNameSpace()
	{
		return nameSpace;
	}

	public void setNameSpace(String nameSpace)
	{
		this.nameSpace = nameSpace;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getText()
	{
		return text;
	}
	
	public XmlFile getXmlFile()
	{
		return xmlFile;
	}
	
	public String getIndentation()
	{
		return indentation;
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	
	public boolean isFullElementGeneration()
	{
		return fullElementGeneration;
	}
}
