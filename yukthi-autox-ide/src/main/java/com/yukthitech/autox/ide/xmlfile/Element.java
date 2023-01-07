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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.ElementInfo;
import com.yukthitech.autox.doc.ParamInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.ide.IIdeConstants;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.index.FileParseCollector;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestDataFile;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLConstants;
import com.yukthitech.ccg.xml.XMLUtil;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.beans.BeanProperty;
import com.yukthitech.utils.beans.BeanPropertyInfoFactory;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class Element implements INode
{
	private static Logger logger = LogManager.getLogger(Element.class);
	
	private static final Pattern DOLLAR_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
	
	private static DefaultParserHandler defaultParserHandler = new DefaultParserHandler();
	
	private Element parentElement;
	
	private String prefix;
	
	private String namespace;
	
	private String name;
	
	private List<INode> nodes = new ArrayList<>();
	
	private Map<String, Attribute> attributes = new HashMap<>();

	private LocationRange startLocation;
	
	private LocationRange endLocation;
	
	private Class<?> elementType;
	
	/**
	 * Step info corresponding to this element.
	 */
	private StepInfo stepInfo;
	
	/**
	 * Function call represented by this function.
	 */
	private FunctionCall functionCall;
	
	/**
	 * Prefix to namespace mapping.
	 */
	private Map<String, String> prefixToNamespace;
	
	/**
	 * Prefix to namespace mapping.
	 */
	private Map<String, String> namespaceToPrefix;
	
	private BeanProperty beanProperty;
	
	/**
	 * Range where this element name is defined.
	 */
	private IndexRange nameIndex;

	public Element()
	{}

	public Element(Element parentElement, String prefix, String namespace, String name, LocationRange startLocation, IndexRange nameIndex)
	{
		this.prefix = prefix;
		this.parentElement = parentElement;
		this.namespace = namespace;
		this.name = name;
		this.startLocation = startLocation;
		this.nameIndex = nameIndex;
	}

	public void addNameSpaceMapping(String prefix, String namespace)
	{
		if(this.prefixToNamespace == null)
		{
			this.prefixToNamespace = new HashMap<>();
			this.namespaceToPrefix = new HashMap<>();
		}
		
		this.prefixToNamespace.put(prefix, namespace);
		this.namespaceToPrefix.put(namespace, prefix);
	}
	
	public IndexRange getNameIndex()
	{
		return nameIndex;
	}
	
	public LocationRange getStartLocation()
	{
		return startLocation;
	}

	public void setStartLocation(LocationRange startLocation)
	{
		this.startLocation = startLocation;
	}

	public LocationRange getEndLocation()
	{
		return endLocation;
	}

	public void setEndLocation(LocationRange endLocation)
	{
		this.endLocation = endLocation;
	}
	
	public Element getParentOfType(List<Class<?>> types)
	{
		Element curParent = this.parentElement;
		
		while(curParent != null)
		{
			if(types.contains(curParent.elementType))
			{
				return curParent;
			}
			
			curParent = curParent.parentElement;
		}
		
		return null;
	}

	public Element getParentElement()
	{
		return parentElement;
	}

	public void setParentElement(Element parentElement)
	{
		this.parentElement = parentElement;
	}

	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	public String getName()
	{
		return name;
	}
	
	public String getNormalizedName()
	{
		return name.toLowerCase().replaceAll("\\W+", "");
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<INode> getNodes()
	{
		return nodes;
	}

	public void setNodes(List<INode> nodes)
	{
		this.nodes = nodes;
	}

	public void addNode(INode element)
	{
		this.nodes.add(element);
	}

	public void addAttribute(Attribute attr)
	{
		this.attributes.put(attr.getName(), attr);
	}
	
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	
	public Attribute getAttribute(String name)
	{
		return this.attributes.get(name);
	}
	
	public String getAttributeValue(String name)
	{
		Attribute attr = this.attributes.get(name);
		
		if(attr == null)
		{
			return null;
		}
		
		return attr.getValue();
	}
	
	public Class<?> getElementType()
	{
		return elementType;
	}
	
	public StepInfo getStepInfo()
	{
		return stepInfo;
	}
	
	public Set<String> getChildNames()
	{
		Set<String> names = new HashSet<>();
		
		for(Attribute attr : attributes.values())
		{
			names.add(attr.getName());
			names.add( attr.getName().replaceAll("([A-Z])", "-$1").toLowerCase() );
		}
		
		for(INode node : nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element elem = (Element) node;
			names.add(elem.getName());
			names.add( elem.getName().replaceAll("([A-Z])", "-$1").toLowerCase() );
		}
		
		return names;
	}
	
	public void toText(String indent, StringBuilder builder)
	{
		builder.append("\n").append(indent)
			.append(startLocation).append(" - ").append(endLocation).append(" ")
			.append(name).append(attributes);
		
		for(INode element : this.nodes)
		{
			element.toText(indent + "\t", builder);
		}
	}
	
	public boolean hasOffset(int offset)
	{
		if(offset < startLocation.getStartOffset())
		{
			return false;
		}
		
		if(endLocation == null || offset <= endLocation.getEndOffset())
		{
			return true;
		}
		
		return false;
	}
	
	public boolean hasLine(int line)
	{
		if(line < startLocation.getStartLineNumber())
		{
			return false;
		}
		
		if(endLocation == null || line <= endLocation.getEndLineNumber())
		{
			return true;
		}
		
		return false;
	}

	public boolean isSameName(String name)
	{
		String elemName = this.name.toLowerCase().replaceAll("\\W+", "");
		name = name.toLowerCase().replaceAll("\\W+", "");
		
		return elemName.equals(name);
	}
	
	private Element getMatchingChildElement(String withName, int curLineNo)
	{
		for(INode node : this.nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element elem = (Element) node;
			
			if(elem.hasLine(curLineNo))
			{
				return elem.getElement(withName, curLineNo);
			}
			
			if(elem.getEndLocation() == null || curLineNo <= elem.getEndLocation().getEndLineNumber())
			{
				break;
			}
		}
		
		return null;
	}
	
	public Element getElement(String withName, int curLineNo)
	{
		//$ indicates any step 
		
		//check if there is any subchild which matches required criteria
		Element matchingChild = getMatchingChildElement(withName, curLineNo);
		
		if(matchingChild != null)
		{
			return matchingChild;
		}
		
		//if no child is matching check for current step
		
		//if non-step is being searched
		if(!IIdeConstants.ELEMENT_TYPE_STEP.equals(withName))
		{
			String elemName = name.toLowerCase().replaceAll("\\W+", "");
			
			if(elemName.equals(withName))
			{
				return this;
			}
			
			return null;
		}
		
		//if step is being searched
		if(stepInfo != null && stepInfo.isExecutable())
		{
			return this;
		}
		
		if(functionCall != null)
		{
			return this;
		}
		
		String elemName = name.toLowerCase().replaceAll("\\W+", "");
		
		if(("customuilocator".equals(elemName) || "function".equals(elemName)) && this.startLocation.hasLine(curLineNo))
		{
			return this;
		}

		return null;
	}
	
	/**
	 * Fetches all attributes and child text nodes in a map.
	 * @return
	 */
	public Map<String, List<ValueWithLocation>> getTextChildValueMap()
	{
		Map<String, List<ValueWithLocation>> resMap = new HashMap<>();
		
		BiConsumer<String, Object> addToMap = new BiConsumer<String, Object>()
		{
			@Override
			public void accept(String name, Object valueParent)
			{
				List<ValueWithLocation> valLst = resMap.get(name);
				
				if(valLst == null)
				{
					valLst = new ArrayList<>();
					resMap.put(name, valLst);
				}
				
				String value = null;
				LocationRange loc = null;
				LocationRange nameLoc = null;
				
				if(valueParent instanceof Attribute)
				{
					value = ((Attribute) valueParent).getValue();
					loc = ((Attribute) valueParent).getValueLocation();
					nameLoc = ((Attribute) valueParent).getNameLocation();
				}
				else
				{
					TextNode textNode = (TextNode) ((Element) valueParent).nodes.get(0); 
					value =  textNode.getContent();
					loc = textNode.getLocation();
					nameLoc = ((Element) valueParent).getStartLocation();
				}
				
				valLst.add(new ValueWithLocation(value, nameLoc, loc));
			}
		};
		
		attributes.entrySet()
			.stream()
			//skip reserved attributes
			.filter(entry -> !AutomationUtils.isReserveNamespace(entry.getValue().getNamespace()) )
			.forEach( entry -> addToMap.accept(entry.getKey(), entry.getValue()));

		this.nodes
			.stream()
			//skip non-element nodes
			.filter(node -> (node instanceof Element))
			//type cast
			.map(node -> (Element) node)
			//filter only nodes which has single text node as child
			.filter(elem -> elem.nodes.size() == 1 && (elem.nodes.get(0) instanceof TextNode))
			.forEach( elem -> addToMap.accept(elem.getNormalizedName(), elem));

		return resMap;
	}
	
	/**
	 * Fetches values for all specified names (both from attributes and from sub-elements).
	 * @param names
	 * @return
	 */
	public Map<String, String> getChildValues(Set<String> names)
	{
		Map<String, String> valueMap = new HashMap<>();
		
		for(String name : names)
		{
			Attribute attr = attributes.get(name);
			
			if(attr != null)
			{
				valueMap.put(name, attr.getValue());
				continue;
			}
		}
		
		//if all names are found in attr, simply return
		if(valueMap.size() == names.size())
		{
			return valueMap;
		}

		for(INode node : this.nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element elem = (Element) node;
			String elemName = elem.getNormalizedName();
			
			if(!names.contains(elemName))
			{
				continue;
			}
			
			if(elem.nodes.size() == 1 && (elem.nodes.get(0) instanceof TextNode))
			{
				valueMap.put(elemName, ((TextNode) elem.nodes.get(0)).getContent() );

				//if all names are found, simply return
				if(valueMap.size() == names.size())
				{
					return valueMap;
				}
			}
		}
		
		return valueMap;
	}
	
	/**
	 * Fetches child elements with specified name and ensures only 'limit' number of elements are fetched to max.
	 * @param withName
	 * @param limit
	 * @return
	 */
	private List<Element> getElementsWithName(String withName, int limit)
	{
		List<Element> resLst = new ArrayList<>();
		String elemName = withName.toLowerCase().replaceAll("\\W+", "");
		
		for(INode node : this.nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element elem = (Element) node;

			if(elemName.equals(elem.getNormalizedName()))
			{
				resLst.add(elem);
				
				if(resLst.size() >= limit)
				{
					break;
				}
			}
		}
		
		return resLst;
	}
	
	/**
	 * Fetches all elements with specified name.
	 * @param withName
	 * @return
	 */
	public List<Element> getElementsWithName(String withName)
	{
		return getElementsWithName(withName, Integer.MAX_VALUE);
	}
	
	/**
	 * Fetches first child element with specified name.
	 * @param withName
	 * @return
	 */
	public Element getElementWithName(String withName)
	{
		List<Element> elements = getElementsWithName(withName, 1);
		
		if(elements.isEmpty())
		{
			return null;
		}
		
		return elements.get(0);
	}

	public Element getLastElement(int offset)
	{
		if(!hasOffset(offset))
		{
			return null;
		}
		
		Element finalElem = null;
		
		for(INode node : this.nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element celem = (Element) node;
			
			finalElem = celem.getLastElement(offset);
			
			if(finalElem != null)
			{
				return finalElem;
			}
			
			if(celem.getEndLocation() == null || celem.getEndLocation().getEndOffset() >= offset)
			{
				break;
			}
		}
		
		return this;
	}
	
	public String getReservedAttribute(String name)
	{
		Attribute attr = this.attributes.get(name);
		
		if(attr == null)
		{
			return null;
		}
		
		if(!AutomationUtils.isReserveNamespace(attr.getNamespace()))
		{
			return null;
		}
		
		return attr.getValue();
	}
	
	private void populateTypesForReserved(Project project, FileParseCollector collector, boolean recursive)
	{
		String beanTypeStr = getReservedAttribute(DefaultParserHandler.ATTR_BEAN_TYPE);
		DocInformation docInformation = project.getDocInformation();
		
		if(beanTypeStr != null)
		{
			try
			{
				this.elementType = Class.forName(beanTypeStr, false, project.getProjectClassLoader());
				
				if(recursive)
				{
					populateChildren(project, collector);
				}
			}catch(Exception ex)
			{
				Attribute attr = getAttribute(DefaultParserHandler.ATTR_BEAN_TYPE);
				
				collector.addMessage(
						new FileParseMessage(MessageType.ERROR, "Invalid bean type specified: " + beanTypeStr, 
								startLocation.getStartLineNumber(), 
								attr.getNameLocation().getStartOffset(), attr.getValueLocation().getEndOffset()
							)
						);
			}
			
			return;
		}
		
		StepInfo stepInfo = docInformation.getStep(name);
		
		if(stepInfo != null)
		{
			try
			{
				this.stepInfo = stepInfo;
				this.elementType = Class.forName(stepInfo.getJavaType(), false, project.getProjectClassLoader());
				
				if(recursive)
				{
					populateChildren(project, collector);
				}
			}catch(Exception ex)
			{
				logger.error("An error occurred while loading step type class: " + stepInfo.getJavaType(), ex);
				
				collector.addMessage(
						new FileParseMessage(
								MessageType.ERROR, 
								"Failed to load step type class: " + stepInfo.getJavaType(), 
								startLocation.getStartLineNumber(),
								this.startLocation.getStartOffset(),
								this.startLocation.getEndOffset()
								)
						);
			}
			
			return;
		}
		
		ValidationInfo validationInfo = docInformation.getValidation(name);
		
		if(validationInfo != null)
		{
			try
			{
				this.stepInfo = validationInfo;
				this.elementType = Class.forName(validationInfo.getJavaType(), false, project.getProjectClassLoader());

				if(recursive)
				{
					populateChildren(project, collector);
				}
			}catch(Exception ex)
			{
				collector.addMessage(
						new FileParseMessage(
								MessageType.ERROR, 
								"Failed to load validation type class: " + validationInfo.getJavaType(), 
								startLocation.getStartLineNumber(),
								this.startLocation.getStartOffset(),
								this.startLocation.getEndOffset()
								)
						);
			}
			
			return;
		}
		
		//if reserve node is supported by default handler, ignore
		if(defaultParserHandler.isValidReserverNode(this.name))
		{
			return;
		}

		collector.addMessage(
				new FileParseMessage(MessageType.ERROR, 
						"No matching step or validation found with name: " + name, 
						startLocation.getStartLineNumber(),
						this.startLocation.getStartOffset(),
						this.startLocation.getEndOffset()
						)
				);
	}
	
	public void populateTestFileTypes(Project project, FileParseCollector collector)
	{
		this.populateTypes(null, project, collector, true);
	}
	
	public void populateTypes(Class<?> parentElementType, Project project, FileParseCollector collector, boolean recursive)
	{
		BeanPropertyInfoFactory beanInfoFactory = project.getBeanPropertyInfoFactory();
		
		if(XMLConstants.CCG_WRAP_URI.equals(namespace) || XMLConstants.NEW_CCG_WRAP_URI.equals(namespace))
		{
			this.elementType = parentElementType;
		}
		else if(IAutomationConstants.FUNC_NAME_SPACE.equals(namespace))
		{
			this.elementType = null;
			this.functionCall = new FunctionCall(name);
			
			collector.addFunctionRef(this);
		}
		else if(AutomationUtils.isReserveNamespace(namespace))
		{
			populateTypesForReserved(project, collector, recursive);
			return;
		}
		else if(parentElementType == null)
		{
			this.elementType = TestDataFile.class;
		}
		else
		{
			StepInfo parentStepInfo = parentElement.getStepInfo();
			ElementInfo childElementInfo = (parentStepInfo != null) ? parentStepInfo.getChildElement(name) : null;
			
			if(childElementInfo != null)
			{
				this.stepInfo = childElementInfo;
				
				try
				{
					this.elementType = Class.forName(childElementInfo.getType());
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while loading class of type: {}", childElementInfo.getType(), ex);
				}
			}
			
			if(this.elementType == null)
			{
				String name = IdeUtils.removeHyphens(this.name);
				BeanProperty propInfo = beanInfoFactory.getBeanPropertyInfo(parentElementType).getProperty(name);
				
				if(propInfo == null)
				{
					collector.addMessage(
							new FileParseMessage(
									MessageType.ERROR, 
									String.format("No matching property '%s' under parent-element bean type: %s", this.name, parentElementType.getName()), 
									startLocation.getStartLineNumber(),
									this.startLocation.getStartOffset(),
									this.startLocation.getEndOffset()
									)
							);
					return;
				}
				
				if(propInfo.getWriteMethod() == null)
				{
					collector.addMessage(
							new FileParseMessage(
									MessageType.ERROR, 
									String.format("No writeable property '%s' under parent-element bean type: %s", this.name, parentElementType.getName()),
									startLocation.getStartLineNumber(),
									this.startLocation.getStartOffset(),
									this.startLocation.getEndOffset()
									)
							);
					return;
				}
				
				this.elementType = propInfo.getType();
				this.beanProperty = propInfo;

				String beanTypeStr = getReservedAttribute(DefaultParserHandler.ATTR_BEAN_TYPE);
				
				if(StringUtils.isNotBlank(beanTypeStr))
				{
					try
					{
						this.elementType = Class.forName(beanTypeStr, false, project.getProjectClassLoader());
					}catch(Exception ex)
					{
						collector.addMessage(
								new FileParseMessage(
										MessageType.ERROR, 
										String.format("An error occurred while fetching dynamic type: %s", beanTypeStr),
										startLocation.getStartLineNumber(),
										this.startLocation.getStartOffset(),
										this.startLocation.getEndOffset()
										)
								);
						return;
					}
				}
			}
		}
		
		//if failed to determine the current element type
		// move to next element
		if(this.elementType == null)
		{
			return;
		}

		//TODO: handle bean copy property properly
		String beanCopy = getReservedAttribute("beanCopy");
		
		//if bean copy is present, for temp fix, dont process children or attributes
		if(StringUtils.isNotBlank(beanCopy))
		{
			return;
		}
		
		if(Function.class.equals(this.elementType))
		{
			collector.addFuncton(this);
		}

		if(recursive)
		{
			populateChildren(project, collector);
		}
	}
	
	private void populateChildren(Project project, FileParseCollector collector)
	{
		collector.elementStarted(this);
		
		try
		{
			for(INode node : this.nodes)
			{
				if(node instanceof TextNode)
				{
					TextNode textNode = (TextNode) node;
					parentElement.parseTextContent(this.name, textNode.getLocation(), textNode.getContent(), collector, 
							AutomationUtils.isReserveNamespace(namespace), elementType);
					continue;
				}
				
				if(node instanceof CdataNode)
				{
					CdataNode textNode = (CdataNode) node;
					parentElement.parseTextContent(this.name, textNode.getValueLocation(), textNode.getContent(), collector, 
							AutomationUtils.isReserveNamespace(namespace), elementType);
					continue;
				}

				if(!(node instanceof Element))
				{
					continue;
				}
				
				Element selem = (Element) node;
				selem.populateTypes(elementType, project, collector, true);
			}
			
			BeanPropertyInfoFactory beanInfoFactory = project.getBeanPropertyInfoFactory();
	
			for(Attribute attr : this.attributes.values())
			{
				if(AutomationUtils.isReserveNamespace(attr.getNamespace()))
				{
					continue;
				}
				
				String name = attr.getName();
				BeanProperty propInfo = beanInfoFactory.getBeanPropertyInfo(elementType).getProperty(name);
				
				if(propInfo == null && name.contains("-"))
				{
					name = IdeUtils.removeHyphens(attr.getName());
					propInfo =  beanInfoFactory.getBeanPropertyInfo(elementType).getProperty(name);
				}
				
				if(propInfo == null)
				{
					//if this id based node ignore it
					if(this.beanProperty != null && this.beanProperty.isKeyProperty())
					{
						continue;
					}
					
					//if step info is present and it is of key based, ignore missing attribute prop
					if(this.stepInfo != null && (this.stepInfo instanceof ElementInfo))
					{
						ElementInfo elemInfo = (ElementInfo) this.stepInfo;
						
						if(StringUtils.isNotBlank(elemInfo.getKeyName()))
						{
							continue;
						}
					}
					
					collector.addMessage(
							new FileParseMessage(
									MessageType.ERROR, 
									String.format("No matching property found for attribute '%s' under bean-type: %s'", attr.getName(), elementType.getName()), 
									startLocation.getStartLineNumber(),
									attr.getNameLocation().getStartOffset(), attr.getValueLocation().getEndOffset()
									)
							);
					continue;
				}
				
				if(propInfo.getWriteMethod() == null)
				{
					collector.addMessage(new FileParseMessage(
								MessageType.ERROR, "No writeable property found for attribute with name: " + attr.getName(), 
								startLocation.getStartLineNumber(),
								attr.getNameLocation().getStartOffset(), attr.getValueLocation().getEndOffset()
								)
							);
					continue;
				}
				
				attr.setAttributeType(propInfo.getType());
				parseTextContent(name, attr.getValueLocation(), attr.getValue(), collector, false, attr.getAttributeType());
			}
		}finally
		{
			collector.elementEnded(this);
		}
	}
	
	private boolean isSupportedTextType(Class<?> type)
	{
		//object is used by diff prop to support expressions
		if(type == null || Object.class.equals(type))
		{
			return true;
		}
		
		return XMLUtil.isSupportedAttributeClass(type);
	}
	
	private void parseTextContent(String propName, LocationRange location, String text, FileParseCollector collector, 
			boolean reservedElementText, Class<?> propType)
	{
		if(reservedElementText || !isSupportedTextType(propType))
		{
			collector.addMessage(new FileParseMessage(MessageType.ERROR, "Encountered text in unsupported element.", 
					location.getStartLineNumber(), 
					location.getStartOffset(), 
					location.getEndOffset()));
			return;
		}
		
		if(stepInfo == null)
		{
			return;
		}
		
		ParamInfo paramInfo = stepInfo.getParam(propName);
		
		if(paramInfo != null && paramInfo.getSourceType() == SourceType.CONDITION)
		{
			parseConditionText(propName, location, new TextContent(text), collector);
		}
		
		collectReferences(location, text, collector);
	}
	
	private void parseConditionText(String propName, LocationRange location, TextContent text, FileParseCollector collector)
	{
		Map<String, String> tokenToError = CommonUtils.toMap(
			">", "Greater than (>) symbol is used in condition, which may not work. Use 'gt' or 'gte' instead",
			"<", "Lesser than (>) symbol is used in condition, which may not work. Use 'lt' or 'lte' instead",
			"&gt;", "Greater than (&gt;) symbol is used in condition, which may not work. Use 'gt' or 'gte' instead",
			"&lt;", "Lesser than (&lt;) symbol is used in condition, which may not work. Use 'lt' or 'lte' instead"
		);
		
		for(Map.Entry<String, String> tokenEntry : tokenToError.entrySet())
		{
			int idx = text.indexOf(tokenEntry.getKey());

			if(idx >= 0)
			{
				int stIdx = location.getStartOffset() + idx;
				collector.addMessage(new FileParseMessage(MessageType.WARNING, 
						tokenEntry.getValue(), 
						location.getStartLineNumber() + text.lineOf(idx),
						stIdx, 
						stIdx + tokenEntry.getKey().length()));
			}
		}

		Matcher matcher = DOLLAR_PATTERN.matcher(text.getText());
		
		while(matcher.find())
		{
			collector.addMessage(new FileParseMessage(MessageType.WARNING, "Dollar expression is being used in condition expression. Context can be accessed directly.", 
					location.getStartLineNumber() + text.lineOf(matcher.start()), 
					location.getStartOffset() + matcher.start(), 
					location.getStartOffset() + matcher.end()));
		}
		
	}
	
	private void collectReferences(LocationRange location, String content, FileParseCollector collector)
	{
		TestDataFileTokenizer.parse(content, location.getStartOffset(), token -> 
		{
			collector.addAppPropRef(token.value, token.startOffset, token.endOffset);
		});
	}
	
	/**
	 * Finds the element type by recursively finding parent element types as needed.
	 * @param project
	 * @param messages
	 * @return
	 */
	public Class<?> findElementType(Project project, FileParseCollector collector)
	{
		if(elementType != null)
		{
			return elementType;
		}
		
		if(parentElement == null)
		{
			return TestDataFile.class;
		}
		
		Class<?> parentElemType = parentElement.findElementType(project, collector);
		
		if(parentElemType == null)
		{
			return null;
		}
		
		populateTypes(parentElemType, project, collector, false);
		
		return this.elementType;
	}
	
	public String getFullElementName()
	{
		if(prefix == null)
		{
			return name;
		}
		
		return prefix + ":" + name;
	}

	public String getNamespaceWithPrefix(String prefix)
	{
		if(prefixToNamespace == null || !prefixToNamespace.containsKey(prefix))
		{
			return parentElement != null ? parentElement.getNamespaceWithPrefix(prefix) : null;
		}
		
		return prefixToNamespace.get(prefix);
	}
	
	public String getPrefixForNamespace(String namespace)
	{
		if(namespaceToPrefix == null || !namespaceToPrefix.containsKey(namespace))
		{
			return parentElement != null ? parentElement.getPrefixForNamespace(namespace) : null;
		}
		
		return namespaceToPrefix.get(namespace);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("\n");
		toText("\t", builder);
		
		return builder.toString();
	}

}
