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
package com.yukthitech.autox.ide;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.ide.xmlfile.MessageType;

/**
 * Collector to collect the file parsing results.
 * @author akiran
 */
public class FileParseCollector
{
	private List<LinkWithLocation> links = new LinkedList<>();
	
	private List<FileParseMessage> messages = new LinkedList<>();
	
	private int errorCount;
	
	private int warningCount;
	
	/**
	 * Project under which parsing is being done.
	 */
	private Project project;
	
	/**
	 * File being parsed.
	 */
	private File file;
	
	public FileParseCollector(Project project, File file)
	{
		this.project = project;
		this.file = file;
	}

	public void load(FileParseCollector collector)
	{
		for(FileParseMessage mssg : collector.messages)
		{
			addMessage(mssg);
		}
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
	
	public void addLink(LinkWithLocation link)
	{
		this.links.add(link);
	}
	
	public List<LinkWithLocation> getLinks()
	{
		return links;
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
		//project.getProjectElementTracker().addFunctionRef(file, element, this);
	}
}
