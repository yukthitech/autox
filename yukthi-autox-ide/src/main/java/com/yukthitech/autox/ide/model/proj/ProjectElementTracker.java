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
package com.yukthitech.autox.ide.model.proj;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.doc.ParamInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.ide.xmlfile.MessageType;
import com.yukthitech.autox.ide.xmlfile.ValueWithLocation;

public class ProjectElementTracker
{
	private static Logger logger = LogManager.getLogger(ProjectElementTracker.class);
	
	private static final Pattern ATTR_NAME_PATTERN = Pattern.compile("\\w+");
	
	private static final Pattern HASH_PATTERN = Pattern.compile("\\#\\{(.+?)\\}");
	
	private static final Pattern DOLLAR_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
	
	private static final Pattern ATTR_REF_PATTERN = Pattern.compile("attr\\.(\\w+)");
	
	private static final Pattern FMARKER_PATTERN = Pattern.compile("\\<\\#\\w+\\s+(.+?)\\>");
	
	private static final Pattern ATTR_EXPR_PATH1 = Pattern.compile("\\s*attr\\s*\\:\\s*([^\\|]+?)\\s*");
	
	private static final Pattern ATTR_EXPR_PATH2 = Pattern.compile("\\s*attr\\(.*?\\)\\s*\\:\\s*([^\\|]+?)\\s*");
	
	private static final Pattern ATTR_REF_EXPR_PATH1 = Pattern.compile("\\s*attr\\s*\\:\\s*(\\w+)\\s*");
	
	private static final Pattern ATTR_REF_EXPR_PATH2 = Pattern.compile("\\s*attr\\(.*?\\)\\s*\\:\\s*(\\w+)\\s*");

	private ProjectElementTree projectElementTree;
	
	private Stack<CodeElementContainer> elementStack = new Stack<>();
	
	private Map<String, Integer> appProp = new HashMap<>();
	
	public ProjectElementTracker(Project project)
	{
		elementStack.push(projectElementTree);
		
		File appPropFile = new File(project.getBaseFolder(), project.getAppPropertyFilePath());
		loadAppProperties(appPropFile);
		
		this.projectElementTree = new ProjectElementTree(appProp);
	}
	
	private void loadAppProperties(File propFile)
	{
		try
		{
			FileInputStream fis = new FileInputStream(propFile);
			String propContent = IOUtils.toString(fis);
			fis.close();
			
			Properties prop = new Properties();
			prop.load(new ByteArrayInputStream(propContent.getBytes()));
			
			prop.keySet()
				.stream()
				.map(obj -> (String) obj)
				.forEach(key -> 
				{
					int idx = propContent.lastIndexOf("\n" + key);
					appProp.put(key, idx);
				}
			);
		}catch(Exception ex)
		{
			logger.error("An error occurred while loading project app properties: {}", propFile.getPath(), ex);
		}
	}
	
	public void elementStarted(File file, Element element, FileParseCollector collector)
	{
		Object peekElem = elementStack.peek();
		String elemName = element.getNormalizedName();
		
		if("testSuite".equalsIgnoreCase(elemName))
		{
			if(!(peekElem instanceof ProjectElementTree) )
			{
				collector.addMessage(new FileParseMessage(MessageType.ERROR, "Testsuite can be defined within test-file only", 
						element.getStartLocation().getStartLineNumber(), 
						element.getStartLocation().getStartOffset(), element.getStartLocation().getEndOffset()));
				return;
			}

			TestSuiteElement testSuite = new TestSuiteElement(file, element.getStartLocation().getStartOffset(), element);
			projectElementTree.addTestSuite(testSuite);
			
			elementStack.push(testSuite);
			return;
		}

		if("testCase".equalsIgnoreCase(elemName))
		{
			if(!(peekElem instanceof TestSuiteElement) )
			{
				collector.addMessage(new FileParseMessage(MessageType.ERROR, "Testcase can be defined within testsuite only", 
						element.getStartLocation().getStartLineNumber(), 
						element.getStartLocation().getStartOffset(), element.getStartLocation().getEndOffset()));
				return;
			}

			TestSuiteElement testSuite = (TestSuiteElement) peekElem;
			TestCaseElement testCase = new TestCaseElement(file, element.getStartLocation().getStartOffset(), element);
			testSuite.addTestCaseElement(testCase);
			
			elementStack.push(testCase);
			return;
		}
		
		if("function".equalsIgnoreCase(elemName))
		{
			if(!(peekElem instanceof ProjectElementTree) && !(peekElem instanceof TestSuiteElement) )
			{
				collector.addMessage(new FileParseMessage(MessageType.ERROR, "Invalid location for function declaration. It should be under test suite or test-data", 
						element.getStartLocation().getStartLineNumber(), 
						element.getStartLocation().getStartOffset(), element.getStartLocation().getEndOffset()));
				return;
			}
			
			FunctionDefElement funcElem = new FunctionDefElement(file, element);
			
			if(peekElem instanceof TestSuiteElement)
			{
				TestSuiteElement testSuite = (TestSuiteElement) peekElem;
				testSuite.addFunction(funcElem);
			}
			else
			{
				projectElementTree.addFunction(funcElem);
			}

			elementStack.push(funcElem);
			
			return;
		}
		
		processTextValues(file, element, collector);
	}
	
	public void addFunctionRef(File file, Element element, FileParseCollector collector)
	{
		CodeElementContainer peekElem = elementStack.peek();
		
		FunctionRefElement funcRef = new FunctionRefElement(file, element);
		peekElem.addFunctionRef(funcRef);
		
		processTextValues(file, element, collector);
	}
	
	public void elementEnded(File file, Element element, FileParseCollector collector)
	{
		Object peekElem = elementStack.peek();
		String elemName = element.getNormalizedName();

		if("testSuite".equalsIgnoreCase(elemName))
		{
			if(!(peekElem instanceof TestSuiteElement))
			{
				return;
			}
			
			elementStack.pop();
			return;
		}

		if("testCase".equalsIgnoreCase(elemName))
		{
			if(!(peekElem instanceof TestCaseElement))
			{
				return;
			}

			elementStack.pop();
			return;
		}
		
		if("function".equalsIgnoreCase(elemName))
		{
			if(!(peekElem instanceof FunctionDefElement))
			{
				return;
			}

			elementStack.pop();
			return;
		}
	}
	
	private void processTextValues(File file, Element element, FileParseCollector collector)
	{
		CodeElementContainer peekElem = elementStack.peek();
		
		StepInfo stepInfo = element.getStepInfo();
		Map<String, List<ValueWithLocation>> valMap = element.getTextChildValueMap();
		
		for(Map.Entry<String, List<ValueWithLocation>> entry : valMap.entrySet())
		{
			String name = entry.getKey();
			List<ValueWithLocation> values = entry.getValue();
			ParamInfo paramInfo = (stepInfo != null) ? stepInfo.getParam(name) : null;
			
			//if parameter represents attribute name to be added
			if(paramInfo != null && paramInfo.isAttrName())
			{
				ValueWithLocation lastOne = values.get(values.size() - 1); 
				String attrName = lastOne.getValue();

				//if the attribute is expression path (As in <set> step) and is using expression syntax
				if(paramInfo.getSourceType() == SourceType.EXPRESSION_PATH && IAutomationConstants.EXPRESSION_PATTERN.matcher(attrName).find())
				{
					Matcher matcher = ATTR_EXPR_PATH1.matcher(attrName);
					
					//check if this is attribute path without params
					if(!matcher.matches())
					{
						//if not, match with params
						matcher = ATTR_EXPR_PATH2.matcher(attrName);
					}
					
					//if this is not attribute path (not with params nor without params), simply return
					if(!matcher.matches())
					{
						continue;
					}
					
					//if attr path, extract the attr name for def
					attrName = matcher.group(1);
				}
				
				if(!ATTR_NAME_PATTERN.matcher(attrName).matches())
				{
					collector.addMessage(new FileParseMessage(MessageType.ERROR, "Attribute name can have only alphabetic character.", 
							lastOne.getLocation().getStartLineNumber(), 
							lastOne.getLocation().getStartOffset(),
							lastOne.getLocation().getEndOffset()));
					continue;
				}
				
				int pos = lastOne.getLocation().getStartOffset();
				//peekElem.addAttribute(new AttrDefElement(file, pos, lastOne.getLocation().getStartLineNumber(), attrName));
				continue;
			}
			
			values.forEach(valueWithLoc -> processTextForReferences(
					file, 
					valueWithLoc.getValue(), 
					valueWithLoc.getLocation().getStartLineNumber(), 
					valueWithLoc.getLocation().getStartOffset(), 
					peekElem,
					paramInfo,
					collector));
		}
	}
	
	private void processTextForReferences(File file, String text, int lineNo, int pos, CodeElementContainer peekElem, ParamInfo paramInfo, FileParseCollector collector)
	{
		//add hash expressions if any
		Matcher matcher = HASH_PATTERN.matcher(text);
		Matcher refMatcher = null;
		
		while(matcher.find())
		{
			//peekElem.addHashRef(new HashRefElement(file, lineNo, pos + matcher.start(), pos + matcher.end(), matcher.group(1)));
		}
		
		//process dollar expressions
		matcher = DOLLAR_PATTERN.matcher(text);

		while(matcher.find())
		{
			int valPos = pos + matcher.start(1);
			String expression = matcher.group(1);
			refMatcher = ATTR_REF_PATTERN.matcher(expression);
			
			while(refMatcher.find())
			{
				//peekElem.addAttributeRef(new AttrRefElement(file, valPos + refMatcher.start(1), lineNo, valPos + refMatcher.end(1), refMatcher.group(1)));
			}
		}
		
		//process <#> expressions
		matcher = FMARKER_PATTERN.matcher(text);

		while(matcher.find())
		{
			int valPos = pos + matcher.start(1);
			String expression = matcher.group(1);
			refMatcher = ATTR_REF_PATTERN.matcher(expression);
			
			while(refMatcher.find())
			{
				//peekElem.addAttributeRef(new AttrRefElement(file, valPos + refMatcher.start(1), lineNo, valPos + refMatcher.end(1), refMatcher.group(1)));
			}
		}

		//for expression based values check for colon expressions
		if(paramInfo != null && paramInfo.getSourceType() == SourceType.EXPRESSION)
		{
			processExpressionText(file, text, lineNo, pos, peekElem, paramInfo, collector);
		}

		//process condition based content
		if(paramInfo != null && paramInfo.getSourceType() == SourceType.CONDITION)
		{
			processConditionText(file, text, lineNo, pos, peekElem, paramInfo, collector);
		}
	}
	
	private void processExpressionText(File file, String text, int lineNo, int pos, CodeElementContainer peekElem, ParamInfo paramInfo, FileParseCollector collector)
	{
		Matcher matcher = ATTR_REF_EXPR_PATH1.matcher(text);
		
		//check if this is attribute path without params
		if(!matcher.matches())
		{
			//if not, match with params
			matcher = ATTR_REF_EXPR_PATH2.matcher(text);
		}
		
		//if attr path, extract the attr name and add ref
		if(matcher.matches())
		{
			int valPos = pos + matcher.start(1);
			//peekElem.addAttributeRef(new AttrRefElement(file, valPos, lineNo, valPos + matcher.end(1), matcher.group(1)));
		}
	}
	
	private void processConditionText(File file, String text, int lineNo, int pos, CodeElementContainer peekElem, ParamInfo paramInfo, FileParseCollector collector)
	{
		int idx = text.indexOf(">");
		
		if(idx >= 0)
		{
			collector.addMessage(new FileParseMessage(MessageType.WARNING, "Greater than (>) symbol is used in condition, which may not work. Use 'gt' or 'gte' instead", lineNo, pos + idx, pos + idx + 1));
		}
		
		idx = text.indexOf("&gt;");
		
		if(idx >= 0)
		{
			collector.addMessage(new FileParseMessage(MessageType.WARNING, "Greater than (>) symbol is used in condition, which may not work. Use 'gt' or 'gte' instead", lineNo, pos + idx, pos + idx + 4));
		}
		
		idx = text.indexOf("<");
		
		if(idx >= 0)
		{
			collector.addMessage(new FileParseMessage(MessageType.WARNING, "Lesser than (>) symbol is used in condition, which may not work. Use 'lt' or 'lte' instead", lineNo, pos + idx, pos + idx + 1));
		}

		idx = text.indexOf("&lt;");
		
		if(idx >= 0)
		{
			collector.addMessage(new FileParseMessage(MessageType.WARNING, "Greater than (>) symbol is used in condition, which may not work. Use 'lt' or 'lte'  instead", lineNo, pos + idx, pos + idx + 4));
		}
		
		Matcher matcher = DOLLAR_PATTERN.matcher(text);
		
		while(matcher.find())
		{
			collector.addMessage(new FileParseMessage(MessageType.WARNING, "Dollar expression is being used in condition. Instead access context directly", lineNo, pos + matcher.start(), pos + matcher.end()));
		}
	}

	public void checkForErrors()
	{
		
	}
}
