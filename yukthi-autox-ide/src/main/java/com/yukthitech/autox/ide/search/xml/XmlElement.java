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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.InvalidArgumentException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.google.common.base.Objects;

public class XmlElement
{
	public static class Attribute
	{
		private String prefix;

		private String name;

		private String value;

		public Attribute(String prefix, String name, String value)
		{
			this.prefix = prefix;
			this.name = name;
			this.value = value;
		}
		
		public Attribute()
		{}

		public String getPrefix()
		{
			return prefix;
		}

		public void setPrefix(String prefix)
		{
			this.prefix = prefix;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getValue()
		{
			return value;
		}

		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	public static class CdataValue
	{
		private String value;

		public CdataValue(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}

		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	private Map<String, String> namespaces;
	
	private String prefix;

	private String name;
	
	private List<Attribute> attributes;
	
	private List<Object> childNodes;
	
	private XmlElement(Map<String, String> namespaces, String name)
	{
		this.namespaces = namespaces;
		this.name = name;
	}
	
	public XmlElement(Document doc, String name)
	{
		namespaces = XmlSearchUtils.getNameSpaces(doc);
		this.name = name;
	}
	
	public XmlElement(Element element)
	{
		namespaces = XmlSearchUtils.getNameSpaces(element.getOwnerDocument());
		
		this.prefix = element.getPrefix();
		this.name = element.getLocalName();
		
		NamedNodeMap attrMap = element.getAttributes();
		
		if(attrMap != null && attrMap.getLength() > 0)
		{
			this.attributes = new ArrayList<>();
			int count = 0;
			
			for(int i = 0; i < count; i++)
			{
				Node attr = attrMap.item(i);
				attributes.add(new Attribute(attr.getPrefix(), attr.getLocalName(), attr.getNodeValue()));
			}
		}
		
		NodeList childLst = element.getChildNodes();
		
		if(childLst != null && childLst.getLength() > 0)
		{
			int count = childLst.getLength();
			childNodes = new ArrayList<>();

			for(int i = 0; i < count; i++)
			{
				Node child = childLst.item(i);
				
				if(child instanceof Element)
				{
					childNodes.add(new XmlElement((Element) child));
				}
				else if(child instanceof Text)
				{
					String val = ((Text) child).getNodeValue().trim();
					childNodes.add(val);
				}
				else if(child instanceof CDATASection)
				{
					String val = ((CDATASection) child).getNodeValue().trim();
					childNodes.add(new CdataValue(val));
				}
			}
		}
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public XmlElement newChildElement(String name)
	{
		XmlElement child = new XmlElement(namespaces, name);
		this.childNodes.add(child);
		return child;
	}

	public List<Attribute> getAttributes()
	{
		if(attributes == null)
		{
			return Collections.emptyList();
		}
		
		return Collections.unmodifiableList(attributes);
	}

	public void setAttributes(List<Attribute> attributes)
	{
		if(attributes == null)
		{
			this.attributes = null;
			return;
		}
		
		attributes.forEach(attr -> setAttribute(attr.prefix, attr.name, attr.value));
	}
	
	public void setAttribute(String prefix, String name, String value)
	{
		if(!namespaces.containsKey(prefix))
		{
			throw new InvalidArgumentException("Invalid prefix specified: " + prefix);
		}
		
		if(this.attributes == null)
		{
			this.attributes = new ArrayList<>();
		}
		
		int size = this.attributes.size();
		
		for(int i = 0 ; i < size; i++)
		{
			Attribute attr = this.attributes.get(i);
			
			if(Objects.equal(prefix, attr.prefix) && Objects.equal(name, attr.name))
			{
				attr.value = value;
				return;
			}
		}
		
		this.attributes.add(new Attribute(prefix, name, value));
	}
	
	public Attribute removeAttribute(String prefix, String name)
	{
		if(this.attributes == null)
		{
			return null;
		}
		
		int size = this.attributes.size();
		
		for(int i = 0 ; i < size; i++)
		{
			Attribute attr = this.attributes.get(i);
			
			if(Objects.equal(prefix, attr.prefix) && Objects.equal(name, attr.name))
			{
				this.attributes.remove(i);
				return attr;
			}
		}
		
		return null;
	}

	public List<Object> getChildObjects()
	{
		if(childNodes == null)
		{
			return Collections.emptyList();
		}
		
		return Collections.unmodifiableList(childNodes);
	}
	
	public List<XmlElement> getChildElements()
	{
		if(childNodes == null)
		{
			return Collections.emptyList();
		}

		List<XmlElement> elements = this.childNodes
				.stream()
				.filter(obj -> (obj instanceof XmlElement))
				.map(obj -> (XmlElement) obj)
				.collect(Collectors.toList());
		
		return Collections.unmodifiableList(elements);
	}

	public void setChildElements(List<XmlElement> childElements)
	{
		this.childNodes = null;

		if(childElements == null)
		{
			return;
		}
		
		childElements.forEach(elem -> addChildElement(elem));
	}
	
	private void addChildObject(Object obj)
	{
		if(this.childNodes == null)
		{
			this.childNodes = new ArrayList<>();
		}

		this.childNodes.add(obj);
	}
	
	public void addChildElement(XmlElement elem)
	{
		if(elem == null)
		{
			throw new NullPointerException("Element cannot be null.");
		}
		
		if(elem.prefix != null && !namespaces.containsKey(elem.prefix))
		{
			throw new InvalidArgumentException("Invalid prefix specified: " + prefix);
		}
		
		addChildObject(elem);
	}
	
	public void addChildElements(Collection<XmlElement> elements)
	{
		if(elements == null)
		{
			return;
		}
		
		elements.forEach(elem -> addChildElement(elem));
	}

	public XmlElement removeChildElement(String prefix, String name)
	{
		if(this.childNodes == null)
		{
			return null;
		}
		
		int size = this.childNodes.size();
		
		for(int i = 0 ; i < size; i++)
		{
			Object childObj = this.childNodes.get(i);
			
			if(!(childObj instanceof XmlElement))
			{
				continue;
			}
			
			XmlElement child = (XmlElement) childObj;
			
			if(Objects.equal(prefix, child.prefix) && Objects.equal(name, child.name))
			{
				this.childNodes.remove(i);
				return child;
			}
		}
		
		return null;
	}

	public List<XmlElement> removeAllChildElements(String prefix, String name)
	{
		if(this.childNodes == null)
		{
			return null;
		}
		
		int size = this.childNodes.size();
		List<XmlElement> removeElements = new ArrayList<>();
		
		for(int i = 0 ; i < size; i++)
		{
			Object childObj = this.childNodes.get(i);
			
			if(!(childObj instanceof XmlElement))
			{
				continue;
			}
			
			XmlElement child = (XmlElement) childObj;
			
			if(Objects.equal(prefix, child.prefix) && Objects.equal(name, child.name))
			{
				this.childNodes.remove(i);
				removeElements.add(child);
				
				//reset index and size
				i--;
				size--;
			}
		}
		
		return removeElements;
	}
	
	public void removeChildElement(XmlElement childElement)
	{
		if(this.childNodes == null || childElement == null)
		{
			return;
		}
		
		this.childNodes.remove(childElement);
	}

	public void removeChildElements(List<XmlElement> childElements)
	{
		if(this.childNodes == null || childElements == null)
		{
			return;
		}
		
		this.childNodes.removeAll(childElements);
	}
	
	public void removeAllChildNodes()
	{
		this.childNodes.clear();
	}
	
	public void setValue(String value)
	{
		if(value == null)
		{
			return;
		}

		addChildObject(value);
	}
	
	public void setCdataValue(String value)
	{
		if(value == null)
		{
			return;
		}

		addChildObject(new CdataValue(value));
	}
	
	public Element toDomElement(Document doc, String initIndent, String indentStr)
	{
		Element newElem = doc.createElementNS(namespaces.get(prefix), name);
		
		if(attributes != null)
		{
			attributes.forEach(attr -> newElem.setAttributeNS(namespaces.get(attr.prefix), attr.name, attr.value));
		}
		
		if(childNodes != null)
		{
			boolean hasElements = (childNodes.size() > 1);
			
			for(Object child : this.childNodes)
			{
				//when elements are present, consider empty text as indent strings and ignore
				if((child instanceof String) && ((String)child).length() == 0 && hasElements)
				{
					continue;
				}
				
				if(child instanceof XmlElement)
				{
					Element childElem = toDomElement(doc, initIndent + indentStr, indentStr);
					newElem.appendChild(doc.createTextNode("\n" + initIndent + indentStr));
					newElem.appendChild(childElem);
					continue;
				}
				
				if(child instanceof CdataValue)
				{
					String strVal = ((CdataValue) child).value;
					
					newElem.appendChild(doc.createCDATASection(strVal));
					continue;
				}
				
				if(child instanceof String)
				{
					String strVal = (String) child;
					
					newElem.appendChild(doc.createTextNode(strVal));
					continue;
				}
				
				//add indent for closing node
				newElem.appendChild(doc.createTextNode("\n" + initIndent));
			}
		}
		
		return newElem;
	}
}
