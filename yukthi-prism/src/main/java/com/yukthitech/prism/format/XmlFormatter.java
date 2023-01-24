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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlFormatter
{
	private static List<FormattingRule> rules = new ArrayList<>();
	
	static
	{
		rules.add(new FormattingRule("\\<\\#if", IndentAction.POST_INCR_INDENT, "\\<\\/\\#if\\>"));
		rules.add(new FormattingRule("\\<\\/\\#if\\>", IndentAction.PRE_DECR_INDENT));
	}
	
	public static String formatXml(String content)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document oldDocument = builder.parse(new ByteArrayInputStream(content.getBytes()));
			Document newDoc = builder.newDocument();
			
			Element oldElement = oldDocument.getDocumentElement();
	
			Element newElement = formatElement(oldElement, newDoc, "", new AtomicInteger(0));
			newDoc.appendChild(newElement);
			
			//convert to xml text
			DOMSource domSource = new DOMSource(newDoc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(domSource, result);		
			
			String resultContent = writer.toString();
			resultContent = resultContent.replace("xmlns:", "\n\txmlns:");
			
			return resultContent;
		}catch(Exception ex)
		{
			if(ex instanceof RuntimeException)
			{
				throw (RuntimeException) ex;
			}
			
			throw new IllegalStateException("An error occurred while formatting xml", ex);
		}
	}
	
	public static Element formatElement(Element oldElement, Document newDoc, String indentation, AtomicInteger childCount)
	{
		Element element = null;
		
		if(StringUtils.isNotEmpty(oldElement.getPrefix()))
		{
			element = newDoc.createElementNS(oldElement.getNamespaceURI(), oldElement.getNodeName());
		}
		else
		{
			element = newDoc.createElement(oldElement.getNodeName());
		}
		
		//set attributes on the new node
		NamedNodeMap attrMap = oldElement.getAttributes();
		int size = attrMap.getLength();
		
		for(int i = 0; i < size; i++)
		{
			Attr oldAttr = (Attr) attrMap.item(i);
			String name = oldAttr.getName();
			
			Attr attr = null;

			if(StringUtils.isNotEmpty(oldAttr.getPrefix()))
			{
				attr = newDoc.createAttributeNS(oldAttr.getNamespaceURI(), oldAttr.getNodeName());
			}
			else
			{
				attr = newDoc.createAttribute(name);
			}
			
			attr.setValue(oldAttr.getValue());
			
			element.setAttributeNode(attr);
		}
		
		//set the child nodes recursively
		NodeList nodeList = oldElement.getChildNodes();
		size = nodeList.getLength();
		Node node = null, prevNode = null;
		String childIndent = indentation + "\t";
		String nextLineIndentText = "\n" + childIndent;
		String nextLineIndentTextWithGap = nextLineIndentText + nextLineIndentText;
		
		boolean prevElemMultiLined = false;
		AtomicInteger subchildCount = new AtomicInteger(0);
		boolean firstElement = true;
		int elementCount = 0;

		//for root element, before child elements are added, add a line gap
		if("".equals(indentation))
		{
			element.appendChild(newDoc.createTextNode(nextLineIndentText));
		}
		
		for(int i = 0 ; i < size ; i++)
		{
			node = nodeList.item(i);
			
			if(node instanceof Attr)
			{
				continue;
			}
			
			if(node instanceof CDATASection)
			{
				String text = ((CDATASection) node).getNodeValue();
				text = formatText(text, childIndent, indentation);
				
				element.appendChild(newDoc.createCDATASection(text));
				childCount.incrementAndGet();
				
				prevNode = node;
				continue;
			}
			
			if(node instanceof Text)
			{
				String text = ((Text) node).getNodeValue();
				text = formatText(text, childIndent, indentation);
				
				if(StringUtils.isBlank(text))
				{
					continue;
				}
				
				element.appendChild(newDoc.createTextNode(text));
				childCount.incrementAndGet();
				
				prevNode = node;
				continue;
			}
			
			if(node instanceof Comment)
			{
				String text = ((Comment) node).getNodeValue();
				text = formatText(text, childIndent + "\t", indentation + "\t");
				
				if(firstElement)
				{
					element.appendChild(newDoc.createTextNode(nextLineIndentText));
				}
				else
				{
					element.appendChild(newDoc.createTextNode(nextLineIndentTextWithGap));
				}
				
				element.appendChild(newDoc.createComment(text));
				
				childCount.incrementAndGet();
				prevNode = node;
				continue;
			}
			
			if(node instanceof Element)
			{
				//if prev element is multi-lined, add empty line after new element
				if(prevElemMultiLined && !(prevNode instanceof Comment))
				{
					element.appendChild(newDoc.createTextNode("\n" + indentation));
				}
				
				element.appendChild(newDoc.createTextNode(nextLineIndentText));
				
				subchildCount.set(0);
				
				Element newElem = formatElement((Element) node, newDoc, indentation + "\t", subchildCount);
				
				//if new element is multi lined
				if(subchildCount.get() > 0)
				{
					//add if there is no line break or comment before this and also this is not first element
					if(!prevElemMultiLined && !(prevNode instanceof Comment) && !firstElement)
					{
						//add extra line gap
						element.appendChild(newDoc.createTextNode("\n" + indentation + "\t"));
					}
				}
				
				element.appendChild(newElem);
				
				prevElemMultiLined = (subchildCount.get() > 0);
				
				elementCount ++;
				childCount.incrementAndGet();
				firstElement = false;
				prevNode = node;
				continue;
			}
			
			throw new IllegalStateException("Unknow node type encountered in xml conent: " + node.getNodeType() + "[" + node.getClass().getName() + "]");
		}
		
		//if element has children, add extra line with indent for closing tag
		if(elementCount > 0)
		{
			element.appendChild(newDoc.createTextNode("\n" + indentation));
			prevElemMultiLined = true;
		}

		return element;
	}

	private static String formatText(String text, String indentation, String parentIndent)
	{
		String trimText = text.trim();
		
		//if text is not multi line text
		if(!trimText.contains("\n"))
		{
			if(trimText.length() > 30)
			{
				return "\n" + indentation + trimText + "\n" + parentIndent;
			}
			
			return trimText;
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new StringReader(trimText));
			StringBuilder builder = new StringBuilder();
			String line = null;
			String extraIndent = "";
			int bracketCount = 0;
			
			FormattingRule matchedRule = null;
			
			while((line = reader.readLine()) != null)
			{
				line = line.trim();
				matchedRule = null;
				
				for(FormattingRule rule : rules)
				{
					if(!rule.isMatching(line))
					{
						continue;
					}
					
					matchedRule = rule;
					break;
				}
				
				if(matchedRule != null)
				{
					extraIndent = matchedRule.getIndentAction().alterIndet(true, extraIndent);
				}
				
				if(matchedRule != null)
				{
					builder.append("\n").append(indentation).append(extraIndent).append(line);
					extraIndent = matchedRule.getIndentAction().alterIndet(false, extraIndent);
				}
				else
				{
					String prevExtraIndent = extraIndent;
					char chArr[] = line.toCharArray();
					bracketCount = 0;
					
					for(int i = 0; i < chArr.length; i++)
					{
						if(isOpenBracket(chArr[i]))
						{
							bracketCount++;
						}
						
						if(isCloseBracket(chArr[i]))
						{
							bracketCount--;
						}
					}
					
					if(bracketCount > 0)
					{
						extraIndent += "\t";
					}
					else if(bracketCount < 0)
					{
						if(extraIndent.length() > 0)
						{
							extraIndent = extraIndent.substring(1);
						}
					}
					
					if(chArr.length == 1 && isCloseBracket(chArr[0]))
					{
						builder.append("\n").append(indentation).append(extraIndent).append(line);
					}
					else
					{
						builder.append("\n").append(indentation).append(prevExtraIndent).append(line);
					}
				}
			}
			
			text = StringUtils.stripEnd(builder.toString(), "\n\t");
			return text + "\n" + parentIndent;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while formating text content", ex);
		}
	}
	
	private static boolean isOpenBracket(char ch)
	{
		return (ch == '(' || ch == '{' || ch == '[');
	}
	
	private static boolean isCloseBracket(char ch)
	{
		return (ch == ')' || ch == '}' || ch == ']');
	}

	public static void main(String[] args) throws Exception
	{
		File file = new File("C:\\Kranthi\\github\\autox\\yukthi-autox\\src\\test\\resources\\test-suites\\test.xml");
		String content = FileUtils.readFileToString(file, Charset.defaultCharset());
		
		System.out.println(formatXml(content));
	}
}
