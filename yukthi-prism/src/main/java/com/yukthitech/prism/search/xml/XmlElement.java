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
package com.yukthitech.prism.search.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.openqa.selenium.InvalidArgumentException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.google.common.base.Objects;
import com.yukthitech.prism.format.XmlFormatter;
import com.yukthitech.utils.doc.Doc;

@Doc("Represents an xml element with attributes and sub-elements")
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

		public String getPrefix()
		{
			return prefix;
		}

		public String getName()
		{
			return name;
		}

		public String getValue()
		{
			return value;
		}
	}
	
	public static class CdataValue
	{
		private String value;

		public CdataValue(String value)
		{
			this.value = value;
		}
	}
	
	public static class CommentValue
	{
		private String value;

		public CommentValue(String value)
		{
			this.value = value;
		}
	}

	private Map<String, String> namespaces;
	
	private String prefix;

	private String name;
	
	private List<Attribute> attributes;
	
	private List<Object> childNodes;
	
	private XmlElement(Map<String, String> namespaces, String prefix, String name)
	{
		this.namespaces = namespaces;
		this.name = name;
		this.prefix = prefix;
	}
	
	@Doc("Used to create new Xml element")
	public XmlElement(
			@Doc(name = "document", value = "Current document under which this element will be used") Document doc, 
			@Doc(name = "prefix", value = "Xml namespace prefix. Eg: s for autox steps") String prefix, 
			@Doc(name = "name", value = "Name of the element") String name)
	{
		namespaces = XmlSearchUtils.getNameSpaces(doc);
		this.name = name;
		this.prefix = prefix;
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
			int count = attrMap.getLength();
			
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
				else if(child instanceof Comment)
				{
					String val = ((Comment) child).getNodeValue().trim();
					childNodes.add(new CommentValue(val));
				}
				else if(child instanceof CDATASection)
				{
					String val = ((CDATASection) child).getNodeValue().trim();
					childNodes.add(new CdataValue(val));
				}
				else if(child instanceof Text)
				{
					String val = ((Text) child).getNodeValue().trim();
					childNodes.add(val);
				}
			}
		}
	}

	@Doc("Fetches prefix of current element")
	public String getPrefix()
	{
		return prefix;
	}

	@Doc("Sets the prefix of current element. Eg: s for autox steps")
	public void setPrefix(
			@Doc(name = "prefix", value = "Prefix to set") String prefix
			)
	{
		this.prefix = prefix;
	}

	@Doc("Fetches name of current element")
	public String getName()
	{
		return name;
	}

	@Doc("Sets name of current element")
	public void setName(
			@Doc(name = "name", value = "Name to set") String name)
	{
		this.name = name;
	}
	
	@Doc("Creates a new xml element with specified prefix and name. And adds it to current element as child element")
	public XmlElement newChildElement(
			@Doc(name = "prefix", value = "Prefix for new element") String prefix, 
			@Doc(name = "prefix", value = "Name for new element") String name)
	{
		XmlElement child = new XmlElement(namespaces, prefix, name);
		this.childNodes.add(child);
		return child;
	}

	@Doc("Fetchs all attributes of current element. Each of attribute will have properties - name, prefix, value")
	public List<Attribute> getAttributes()
	{
		if(attributes == null)
		{
			return Collections.emptyList();
		}
		
		return Collections.unmodifiableList(attributes);
	}

	@Doc("Sets the attributes for this element. Generally used to bulk copy attributes from one element to other element")
	public void setAttributes(
			@Doc(name = "attributes", value = "Attributes to set") List<Attribute> attributes)
	{
		this.attributes = null;
		
		if(attributes == null)
		{
			return;
		}
		
		attributes.forEach(attr -> setAttribute(attr.prefix, attr.name, attr.value));
	}
	
	@Doc("Sets specified attribute to current element")
	public void setAttribute(
			@Doc(name = "prefix", value = "Prefix for new attribute") String prefix, 
			@Doc(name = "name", value = "Name for new attribute") String name, 
			@Doc(name = "value", value = "Value for new attribute") String value)
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
	
	@Doc("Removes specified attribute")
	public Attribute removeAttribute(
			@Doc(name = "prefix", value = "Prefix of attribute to remove") String prefix, 
			@Doc(name = "name", value = "Name of attribute to remove") String name)
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

	@Doc("Fetches all child objects including indent text, comments, cdata etc. Expected to be used only during bulk copy of child objects.")
	public List<Object> getChildObjects()
	{
		if(childNodes == null)
		{
			return Collections.emptyList();
		}
		
		return Collections.unmodifiableList(childNodes);
	}
	
	@Doc("Fetches first child element with specified prefix and name.")
	public XmlElement getChildElement(
			@Doc(name = "prefix", value = "Prefix of element to fetch") String prefix, 
			@Doc(name = "name", value = "Name of element to fetch") String name)
	{
		if(this.childNodes == null)
		{
			return null;
		}
		
		XmlElement res = this.childNodes
				.stream()
				.filter(obj -> (obj instanceof XmlElement))
				.map(obj -> (XmlElement) obj)
				.filter(elem -> Objects.equal(prefix, elem.prefix) && Objects.equal(name, elem.name))
				.findFirst()
				.orElse(null);
		
		return res;
	}

	@Doc("Fetches all child elements (text, cdata other non-elements will not be part of this).")
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

	@Doc("Sets specified child objects (which can include element, text, cdata etc). Expected to be used during bulk copy.")
	public void setChildObjects(
			@Doc(name = "childElements", value = "Child elements to be copied.") List<Object> childElements
			)
	{
		this.childNodes = null;

		if(childElements == null)
		{
			return;
		}
		
		childElements.forEach(elem -> addChildObject(elem));
	}
	
	private void addChildObject(Object obj)
	{
		if(!(obj instanceof XmlElement) 
				&& !(obj instanceof CdataValue)
				&& !(obj instanceof CommentValue)
				&& !(obj instanceof String)
			)
		{
			throw new InvalidArgumentException("Invalid chid object specified: " + obj);
		}
		
		if(this.childNodes == null)
		{
			this.childNodes = new ArrayList<>();
		}

		this.childNodes.add(obj);
	}
	
	@Doc("Adds specified element as child element")
	public void addChildElement(
			@Doc(name = "element", value = "Element to add") XmlElement elem)
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
	
	@Doc(value = "Removes first child element with specified prefix and name", returnDesc = "Element which was remove. Null if nothing is removed")
	public XmlElement removeChildElement(
			@Doc(name = "prefix", value = "Prefix of element to remove") String prefix, 
			@Doc(name = "name", value = "Name of element to remove") String name)
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

	@Doc(value = "Removes all child elements with specified prefix and name", returnDesc = "Elements which were removed. Null if nothing is removed")
	public List<XmlElement> removeAllChildElements(
			@Doc(name = "prefix", value = "Prefix of elements to remove") String prefix, 
			@Doc(name = "name", value = "Name of elements to remove") String name)
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
	
	@Doc("Removes specified child element")
	public void removeChildElement(
			@Doc(name = "childElement", value = "Element to remove") XmlElement childElement)
	{
		if(this.childNodes == null || childElement == null)
		{
			return;
		}
		
		this.childNodes.remove(childElement);
	}

	@Doc("Removes specified child elements")
	public void removeChildElements(
			@Doc(name = "childElements", value = "Elements to remove") List<XmlElement> childElements)
	{
		if(this.childNodes == null || childElements == null)
		{
			return;
		}
		
		this.childNodes.removeAll(childElements);
	}
	
	@Doc("Removes all child nodes (elements, text, cdata etc)")
	public void removeAllChildNodes()
	{
		this.childNodes.clear();
	}
	
	@Doc("Adds specified value as element-text")
	public void setValue(
			@Doc(name = "value", value = "Text to set") String value)
	{
		if(value == null)
		{
			return;
		}

		addChildObject(value);
	}
	
	@Doc("Adds specified value as cdata-section under current element")
	public void setCdataValue(
			@Doc(name = "value", value = "Cdata text to set") String value)
	{
		if(value == null)
		{
			return;
		}

		addChildObject(new CdataValue(value));
	}
	
	public Element toDomElement(Document doc, String initIndent)
	{
		String qname = prefix != null ? prefix + ":" + name : name;
		Element newElem = doc.createElementNS(namespaces.get(prefix), qname);
		
		if(attributes != null)
		{
			attributes.forEach(attr -> newElem.setAttributeNS(
					namespaces.get(attr.prefix), 
					attr.prefix != null ? attr.prefix + ":" + attr.name : attr.name, 
					attr.value));
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
					Element childElem = ((XmlElement) child).toDomElement(doc, initIndent + "\t");
					newElem.appendChild(childElem);
					continue;
				}
				
				if(child instanceof CommentValue)
				{
					String strVal = ((CommentValue) child).value;
					newElem.appendChild(doc.createComment(strVal));
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
			}
		}
		
		return XmlFormatter.formatElement(newElem, doc, initIndent, new AtomicInteger(0));
	}
}
