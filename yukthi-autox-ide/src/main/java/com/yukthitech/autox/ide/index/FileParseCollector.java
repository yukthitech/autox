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
package com.yukthitech.autox.ide.index;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.ide.xmlfile.IndexRange;
import com.yukthitech.autox.ide.xmlfile.LocationRange;
import com.yukthitech.autox.ide.xmlfile.MessageType;
import com.yukthitech.autox.test.TestDataFile;
import com.yukthitech.autox.test.TestSuite;

/**
 * Collector to collect the file parsing results.
 * @author akiran
 */
public class FileParseCollector
{
	private List<FileParseMessage> messages = new LinkedList<>();
	
	private int errorCount;
	
	private int warningCount;
	
	private List<Element> functions = new LinkedList<>();
	
	private List<Element> functionReferences = new LinkedList<>();
	
	private File file;
	
	public FileParseCollector(Project project, File file)
	{
		this.file = file;
	}

	public void load(FileParseCollector collector)
	{
		for(FileParseMessage mssg : collector.messages)
		{
			addMessage(mssg);
		}
		
		this.functions.addAll(collector.functions);
		this.functionReferences.addAll(collector.functionReferences);
		this.file = collector.file;
	}
	
	public void addMessage(FileParseMessage mssg)
	{
		this.messages.add(mssg);
		
		if(mssg.getMessageType() == MessageType.ERROR)
		{
			errorCount++;
		}
		
		if(mssg.getMessageType() == MessageType.WARNING)
		{
			warningCount++;
		}
	}
	
	public List<FileParseMessage> getMessages()
	{
		return this.messages;
	}
	
	public int getErrorCount()
	{
		return errorCount;
	}
	
	public int getWarningCount()
	{
		return warningCount;
	}
	
	public void elementStarted(Element element)
	{
		//project.getProjectElementTracker().elementStarted(file, element, this);
	}
	
	public void elementEnded(Element element)
	{
		//project.getProjectElementTracker().elementEnded(file, element, this);
	}
	
	public void addFunctionRef(Element element)
	{
		functionReferences.add(element);
	}
	
	public void addFuncton(Element element)
	{
		functions.add(element);
	}
	
	public List<ReferenceElement> getReferences()
	{
		if(functionReferences.isEmpty())
		{
			return Collections.emptyList();
		}
		
		List<ReferenceElement> res = new LinkedList<>();
		
		for(Element funcRef : this.functionReferences)
		{
			Element parent = funcRef.getParentOfType(Arrays.asList(TestSuite.class, TestDataFile.class));
			
			if(parent == null)
			{
				continue;
			}
			
			String scope = null;
			
			if(TestSuite.class.equals(parent.getElementType()))
			{
				scope = parent.getAttributeValue("name");
			}
			
			IndexRange nameIndex = funcRef.getNameIndex();
			res.add(new ReferenceElement(IIndexConstants.TYPE_FUNCTION, funcRef.getName(), 
					scope != null ? Arrays.asList(scope) : null, 
					nameIndex.getStart(), 
					nameIndex.getEnd()));
		}
		
		return res;
	}
	
	public List<ReferableElement> getReferableElements()
	{
		if(functions.isEmpty())
		{
			return Collections.emptyList();
		}

		List<ReferableElement> res = new LinkedList<>();
		
		for(Element func : this.functions)
		{
			Element parent = func.getParentOfType(Arrays.asList(TestSuite.class, TestDataFile.class));
			
			if(parent == null)
			{
				continue;
			}
			
			String name = func.getAttributeValue("name");

			if(name == null)
			{
				continue;
			}
			
 			LocationRange selLocationRange = func.getAttribute("name").getValueLocation();

			String scope = null;
			
			if(TestSuite.class.equals(parent.getElementType()))
			{
				scope = parent.getAttributeValue("name");
			}
			
			res.add(new ReferableElement(IIndexConstants.TYPE_FUNCTION, name, scope, this.file, func,
					new IndexRange(selLocationRange.getStartOffset(), selLocationRange.getEndOffset())));
		}
		
		return res;
	}
}
