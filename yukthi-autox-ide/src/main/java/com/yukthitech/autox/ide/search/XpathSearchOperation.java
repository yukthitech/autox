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
package com.yukthitech.autox.ide.search;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.JOptionPane;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.search.xml.XmlElement;
import com.yukthitech.autox.ide.search.xml.XmlSearchUtils;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents file search operation using xpath.
 * @author akranthikiran
 */
public class XpathSearchOperation extends AbstractSearchOperation
{
	private static Logger logger = LogManager.getLogger(XpathSearchOperation.class);
	
	private static final String LOCATION_KEY = "XpathSearchOperation.location";
	
	private static String REPLACE_TEMPLATE;
	
	static
	{
		try
		{
			REPLACE_TEMPLATE = IOUtils.resourceToString("/templates/search/xpath-replace-template.js", Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading replace template", ex);
		}
	}
	
	private static class ElementLocation
	{
		private int lineNumber;
		
		@SuppressWarnings("unused")
		private int columnNumber;

		public ElementLocation(int lineNumber, int columnNumber)
		{
			this.lineNumber = lineNumber;
			this.columnNumber = columnNumber;
		}
	}
	
	private static class LocationAnnotator extends XMLFilterImpl
	{
		private Locator locator;
		private Element lastAddedElement;
		private UserDataHandler dataHandler = new UserDataHandler()
		{
			@Override
			public void handle(short operation, String key, Object data, Node src, Node dst)
			{
				ElementLocation loc = (src != null) ? (ElementLocation) src.getUserData(LOCATION_KEY) : null;
				
				if(loc != null && dst != null)
				{
					dst.setUserData(LOCATION_KEY, loc, this);
				}
			}
		};

		LocationAnnotator(XMLReader xmlReader, Document dom)
		{
			super(xmlReader);

			// Add listener to DOM, so we know which node was added.
			EventListener modListener = new EventListener()
			{
				@Override
				public void handleEvent(Event e)
				{
					EventTarget target = ((MutationEvent) e).getTarget();
					lastAddedElement = (Element) target;
				}
			};
			
			((EventTarget) dom).addEventListener("DOMNodeInserted", modListener, true);
		}

		@Override
		public void setDocumentLocator(Locator locator)
		{
			super.setDocumentLocator(locator);
			this.locator = locator;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
		{
			super.startElement(uri, localName, qName, atts);

			// Keep snapshot of start location,
			// for later when end of element is found.
			//locatorStack.push(new ElementLocation(locator.getLineNumber(), locator.getColumnNumber()));
			
			ElementLocation loc = new ElementLocation(locator.getLineNumber(), locator.getColumnNumber());
			lastAddedElement.setUserData(LOCATION_KEY, loc, dataHandler);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			// Mutation event fired by the adding of element end,
			// and so lastAddedElement will be set.
			super.endElement(uri, localName, qName);
			
			/*
			if(locatorStack.size() > 0)
			{
				ElementLocation startLocator = locatorStack.pop();
				
				System.out.println("===========> Last added element: " + lastAddedElement.getNodeName());
				lastAddedElement.setUserData(LOCATION_KEY, startLocator, dataHandler);
			}
			*/
		}
	}
	
	private static class SimpleNamespaceContext implements NamespaceContext
	{
		private Map<String, String> namespaces;

		public SimpleNamespaceContext(Map<String, String> namespaces)
		{
			this.namespaces = namespaces;
		}

		@Override
		public String getNamespaceURI(String prefix)
		{
			if(prefix == null)
			{
				return namespaces.get("xmlns");
			}
			
			return namespaces.get(prefix);
		}

		@Override
		public String getPrefix(String namespaceURI)
		{
			return null;
		}

		@Override
		public Iterator<?> getPrefixes(String namespaceURI)
		{
			return null;
		}
	}
	
	public XpathSearchOperation(FileSearchQuery fileSearchQuery, List<File> searchFiles, boolean replaceOp)
	{
		super(
				fileSearchQuery.setFileNamePatterns(Arrays.asList("*.xml")), 
				searchFiles, 
				replaceOp);
	}
	
	private Document parseXml(DocumentBuilder docBuilder, Transformer transformer, String content) 
			throws SAXException, ParserConfigurationException, TransformerException
	{
		Document doc = docBuilder.newDocument();
		DOMResult domResult = new DOMResult(doc);
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);
		saxParserFactory.setValidating(false);
		SAXParser saxParser = saxParserFactory.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		
		LocationAnnotator locationAnnotator = new LocationAnnotator(xmlReader, doc);
		InputSource inputSource = new InputSource(new StringReader(content));
		SAXSource saxSource = new SAXSource(locationAnnotator, inputSource);
		
		transformer.transform(saxSource, domResult);
		
		return doc;
	}
	
	public static XPathExpression parseXpath(String strExpression, Document xmlDocument)
	{
		XPath xPath = XPathFactory.newInstance().newXPath();
		Map<String, String> namespaceMap = XmlSearchUtils.getNameSpaces(xmlDocument);
		
		if(!namespaceMap.isEmpty())
		{
			xPath.setNamespaceContext(new SimpleNamespaceContext(namespaceMap));
		}
		
		try
		{
			return xPath.compile(strExpression);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex.getMessage(), ex);
		}
	}
	
	@Override
	public List<XmlSearchResult> findAll()
	{
		List<XmlSearchResult> res = new LinkedList<>();
		File file = null;
		
		DocumentBuilder docBuilder = null;
		Transformer transformer = null;
		
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = builderFactory.newDocumentBuilder();
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating builders", ex);
		}
		
		while((file = nextFile()) != null)
		{
			String content = null;
			
			try
			{
				content = FileUtils.readFileToString(file, Charset.defaultCharset());
			}catch(Exception ex)
			{
				logger.error("An error occurred while loading content of file: {}", file.getPath(), ex);
				
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
						String.format("An error occurred while loading content of file: %s\nError: %s", file.getPath(), ex.getMessage()));
				return null;
			}
			
			TreeMap<Integer, FileLine> lineMaping = null;
			
			Document xmlDocument = null;
			NodeList nodeList = null;

			try
			{
				xmlDocument = parseXml(docBuilder, transformer, content);
				
				XPathExpression expression = parseXpath(fileSearchQuery.getSearchString(), xmlDocument);
				nodeList = (NodeList) expression.evaluate(xmlDocument, XPathConstants.NODESET);
			}catch(Exception ex)
			{
				logger.error("An error occurred while evaluating xpath expression for file: {}", file.getPath(), ex);
				
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
						String.format("An error occurred while evaluating xpath expression for file: %s\nError: %s", file.getPath(), ex.getMessage()));
				return null;
			}
				
			int nodeCount = nodeList.getLength();
			
			for(int i = 0; i < nodeCount; i++)
			{
				Element element = (Element) nodeList.item(i);
				
				if(lineMaping == null)
				{
					lineMaping = loadLineNumberMapping(content);
				}
				
				ElementLocation elemLoc = (ElementLocation) element.getUserData(LOCATION_KEY);
				FileLine fileLine = lineMaping.get(elemLoc.lineNumber);
				
				int matchSt = fileLine.content.indexOf(element.getLocalName());
				int matchEnd = matchSt + element.getLocalName().length();

				StringBuilder builder = new StringBuilder("<html><body style=\"white-space: nowrap;\">");
				
				builder.append("<b>").append(file.getName()).append(":").append(fileLine.lineNo).append("</b> - ");
				builder.append(escapeHtml(fileLine.content.substring(0, matchSt)));
				builder.append("<span style=\"background-color: yellow;\">")
					.append(escapeHtml(fileLine.content.substring(matchSt, matchEnd)))
					.append("</span>");
				builder.append((matchEnd < fileLine.content.length() ? escapeHtml(fileLine.content.substring(matchEnd)) : "") );
				builder.append("</body></html>");
				
				res.add(new XmlSearchResult(file, fileLine.index + matchSt, fileLine.index + matchEnd, fileLine.lineNo, builder.toString(),
						xmlDocument, element));
			}
		}
		
		return res;
	}
	
	@Override
	public void replace(FileEditorTabbedPane fileEditorTabbedPane, List<SearchResult> matches)
	{
		/*
		//Group the results by file, in descending order
		Map<File, TreeSet<SearchResult>> fileResults = groupResults(matches);
		
		//replace the results with replacement string
		Pattern searchPattern = buildSearchPattern();
		
		for(Map.Entry<File, TreeSet<SearchResult>> entry : fileResults.entrySet())
		{
			File file = entry.getKey();
			String content = readContent(fileEditorTabbedPane, file);
			
			if(content == null)
			{
				return;
			}
			
			for(SearchResult res : entry.getValue())
			{
				String matchedContent = content.substring(res.getStart(), res.getEnd());
				Matcher matcher = searchPattern.matcher(matchedContent);
				matchedContent = matcher.replaceFirst(fileSearchQuery.getReplaceWith());
				
				content = content.substring(0, res.getStart()) + matchedContent + content.substring(res.getEnd());
			}
			
			if(!writeContent(fileEditorTabbedPane, file, content))
			{
				return;
			}
		}
		*/
	}
	
	@Override
	public void replaceAll(FileEditorTabbedPane fileEditorTabbedPane)
	{
		File file = null;
		
		DocumentBuilder docBuilder = null;
		Transformer transformer = null;
		
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = builderFactory.newDocumentBuilder();
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating builders", ex);
		}
		
		while((file = nextFile()) != null)
		{
			String content = null;
			
			try
			{
				content = FileUtils.readFileToString(file, Charset.defaultCharset());
			}catch(Exception ex)
			{
				logger.error("An error occurred while loading content of file: {}", file.getPath(), ex);
				
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
						String.format("An error occurred while loading content of file: %s\nError: %s", file.getPath(), ex.getMessage()));
				return;
			}
			
			TreeMap<Integer, FileLine> lineMaping = null;
			
			Document xmlDocument = null;
			NodeList nodeList = null;

			try
			{
				xmlDocument = parseXml(docBuilder, transformer, content);
				
				XPathExpression expression = parseXpath(fileSearchQuery.getSearchString(), xmlDocument);
				nodeList = (NodeList) expression.evaluate(xmlDocument, XPathConstants.NODESET);
			}catch(Exception ex)
			{
				logger.error("An error occurred while evaluating xpath expression for file: {}", file.getPath(), ex);
				
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
						String.format("An error occurred while evaluating xpath expression for file: %s\nError: %s", file.getPath(), ex.getMessage()));
				return;
			}
				
			int nodeCount = nodeList.getLength();
			
			for(int i = nodeCount - 1; i >= 0; i--)
			{
				Element element = (Element) nodeList.item(i);
				
				if(lineMaping == null)
				{
					lineMaping = loadLineNumberMapping(content);
				}
				
				ElementLocation elemLoc = (ElementLocation) element.getUserData(LOCATION_KEY);
				FileLine fileLine = lineMaping.get(elemLoc.lineNumber);
				
				List<Element> replacementElem = null;
				
				try
				{
					replacementElem = executeReplacementScript(xmlDocument, element, XmlSearchUtils.getIndent(fileLine.content));
				}catch(Exception ex)
				{
					JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
							String.format("An error occurred while evaluating xpath expression for file: %s\nError: %s", file.getPath(), ex.getMessage()));
					return;
				}

				XmlSearchUtils.replaceElement(xmlDocument, element, replacementElem);
			}
			
			String finalContent = XmlSearchUtils.toXmlContent(xmlDocument);
			super.writeContent(fileEditorTabbedPane, file, finalContent);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> executeReplacementScript(Document doc, Element element, String elementIndent)
	{
		Map<String, Object> defaultBindings = CommonUtils.toMap("document", doc, "element", element);
				
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		Object res = null;
		
		try
		{
			String finalScript = REPLACE_TEMPLATE.replace("${script}", fileSearchQuery.getReplacementScript());
			
			Bindings bindings = engine.createBindings();
			bindings.putAll(defaultBindings);
			
			engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
			res = engine.eval(finalScript);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing replace-js script\nError: " + ex.getMessage(), ex);
		}
		
		List<XmlElement> resLst = null;

		if(res instanceof XmlElement)
		{
			res = Arrays.asList((XmlElement) res);
		}
		else if(res instanceof List)
		{
			resLst = new ArrayList<>();
			List<Object> resObjLst = (List<Object>) res;
			
			for(Object obj : resObjLst)
			{
				if(!(obj instanceof XmlElement))
				{
					throw new InvalidStateException("Result list returned by script contains non-xml-element. Error element: {}", obj);
				}
				
				resLst.add((XmlElement) obj);
			}
		}
		else
		{
			throw new InvalidStateException("Result returned by script contains neither xml-element nor list of xml-element. Result was: {}", res);
		}
		
		return resLst.stream()
				.map(xmlElem -> xmlElem.toDomElement(doc, elementIndent, "\t"))
				.collect(Collectors.toList());
	}
}
