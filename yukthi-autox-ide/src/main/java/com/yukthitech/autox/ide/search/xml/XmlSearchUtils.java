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
package com.yukthitech.autox.ide.search.xml;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class XmlSearchUtils
{
	public static Map<String, String> getNameSpaces(Document xmlDocument)
	{
		if(xmlDocument == null)
		{
			return Collections.emptyMap();
		}
		
		Element element = xmlDocument.getDocumentElement();
		NamedNodeMap attrMap = element.getAttributes();
		int count = (attrMap == null) ? 0 : attrMap.getLength();
		
		if(count == 0)
		{
			return Collections.emptyMap();
		}
		
		Map<String, String> namespaceMap = new HashMap<>();
		
		for(int i = 0; i < count; i++)
		{
			Node node = attrMap.item(i);
			
			if("xmlns".equals(node.getPrefix()) || "xmlns".equals(node.getLocalName()))
			{
				namespaceMap.put(node.getLocalName(), node.getNodeValue());
			}
		}
		
		return namespaceMap;
	}

	public static void replaceElement(Document doc, Element elementToReplace, List<Element> newElements)
	{
		Element parent = (Element) elementToReplace.getParentNode();
		Element parentCopy = doc.createElementNS(parent.getNamespaceURI(), parent.getLocalName());
		
		//create parent copy with replaced child elements
		NamedNodeMap attrMap = parent.getAttributes();
		
		if(attrMap != null)
		{
			int attrCount = attrMap.getLength();
			
			for(int i = 0; i < attrCount; i++)
			{
				Node attr = attrMap.item(i);
				parentCopy.setAttributeNS(attr.getNamespaceURI(), attr.getLocalName(), attr.getNodeValue());
			}
		}
		
		NodeList childLst = parent.getChildNodes();
		int childCount = childLst.getLength();
		
		for(int i = 0; i < childCount; i++)
		{
			Node child = childLst.item(i);
			
			if(child == elementToReplace)
			{
				newElements.forEach(elem -> parentCopy.appendChild(elem));
				continue;
			}
			
			parentCopy.appendChild(child);
		}
		
		//replace parent copy in document
		if(doc.getDocumentElement() == parent)
		{
			doc.replaceChild(parent, parentCopy);
		}
		else
		{
			Element grandParent = (Element) parent.getParentNode();
			grandParent.replaceChild(parent, parentCopy);
		}
	}
	
	public static String getIndent(String line)
	{
		StringBuilder builder = new StringBuilder();
		char ch[] = line.toCharArray();
		
		for(int i = 0; i < ch.length; i++)
		{
			if(Character.isWhitespace(ch[i]))
			{
				builder.append(ch[i]);
				continue;
			}
			
			break;
		}

		return builder.toString();
	}
	
	public static String toXmlContent(Document doc)
	{
		try
		{
			DOMSource domSource = new DOMSource(doc);
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
			throw new InvalidStateException(ex.getMessage(), ex);
		}
	}
}
