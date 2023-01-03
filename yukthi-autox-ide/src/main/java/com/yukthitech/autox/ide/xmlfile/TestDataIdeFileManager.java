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

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.AbstractIdeFileManager;
import com.yukthitech.autox.ide.IdeNotificationPanel;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.editor.IIdeCompletionProvider;
import com.yukthitech.autox.ide.index.FileParseCollector;
import com.yukthitech.autox.ide.model.Project;

/**
 * Ide file manager for test-data files.
 * @author akiran
 */
@Service
public class TestDataIdeFileManager extends AbstractIdeFileManager
{
	private static Logger logger = LogManager.getLogger(TestDataIdeFileManager.class);
	
	@Autowired
	private IdeNotificationPanel ideNotificationPanel;
	
	@Autowired
	private IdeContext ideContext;
	
	@Override
	public boolean isExecutionSupported()
	{
		return true;
	}
	
	@Override
	public boolean isSuppored(Project project, File file)
	{
		if(!file.getName().toLowerCase().endsWith(".xml"))
		{
			return false;
		}

		if(project.isTestSuiteFolderFile(file))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public IIdeCompletionProvider getCompletionProvider(FileEditor fileEditor)
	{
		XmlCompletionProvider xmlCompletionProvider = new XmlCompletionProvider(fileEditor.getProject(), fileEditor, ideContext);
		return xmlCompletionProvider;
	}
	
	@Override
	public Object parseContent(Project project, String name, String content, FileParseCollector collector)
	{
		XmlFile xmlFile = null;
		
		try
		{
			xmlFile = XmlFile.parse(content, -1, collector);
		}catch(XmlParseException ex)
		{
			xmlFile = ex.getXmlFile();
			collector.addMessage(new FileParseMessage(MessageType.ERROR, ex.getMessage(), ex.getLineNumber(), ex.getOffset(), ex.getEndOffset()));
		}catch(Exception ex)
		{
			logger.debug("Failed to parse xml file: " + name, ex);
			collector.addMessage(new FileParseMessage(MessageType.ERROR, "Failed to parse xml file with error: " + ex, 1));
		}
		
		if(xmlFile != null && xmlFile.getRootElement() != null)
		{
			xmlFile.getRootElement().populateTestFileTypes(project, collector);
		}
		
		return xmlFile;
	}

	@Override
	public String getSyntaxEditingStyle(String extension)
	{
		return RSyntaxTextArea.SYNTAX_STYLE_XML;
	}
	
	@Override
	public String getToolTip(FileEditor fileEditor, Object parsedFile, int offset)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getActiveElement(FileEditor fileEditor, String nodeType)
	{
		//TODO: Content reparsing is required only when content is changed
		XmlFile xmlFile = getXmlFile(fileEditor.getProject(), fileEditor.getFile(), fileEditor.getContent());

		if(xmlFile == null)
		{
			return null;
		}
		
		int curLineNo = fileEditor.getCurrentLineNumber();
		Element curElement = xmlFile.getElement(nodeType, curLineNo);

		if(curElement == null)
		{
			return null;
		}

		Attribute attr = curElement.getAttribute("name");

		if(attr == null || StringUtils.isBlank(attr.getValue()))
		{
			return null;
		}

		return attr.getValue();
	}
	
	@Override
	public int getActiveElementLineNumber(FileEditor fileEditor, String nodeType)
	{
		XmlFile xmlFile = getXmlFile(fileEditor.getProject(), fileEditor.getFile(), fileEditor.getContent());

		if(xmlFile == null)
		{
			return -1;
		}
		
		xmlFile.getRootElement().populateTestFileTypes(fileEditor.getProject(), new FileParseCollector(fileEditor.getProject(), fileEditor.getFile()));
		
		int curLineNo = fileEditor.getCurrentLineNumber() + 1;
		Element curElement = xmlFile.getElement(nodeType, curLineNo);

		if(curElement == null)
		{
			return -1;
		}
		
		return curElement.getStartLocation().getStartLineNumber();
	}
	
	@Override
	public String getActiveElementText(FileEditor fileEditor, String nodeType)
	{
		String content = fileEditor.getContent();
		
		XmlFile xmlFile = getXmlFile(fileEditor.getProject(), fileEditor.getFile(), fileEditor.getContent());

		if(xmlFile == null)
		{
			return null;
		}
		
		//TODO: Content reparsing is required only when content is changed
		xmlFile.getRootElement().populateTestFileTypes(fileEditor.getProject(), new FileParseCollector(fileEditor.getProject(), fileEditor.getFile()));

		int curLineNo = fileEditor.getCurrentLineNumber() + 1;
		Element curElement = xmlFile.getElement(nodeType, curLineNo);

		if(curElement == null)
		{
			return null;
		}
		
		return content.substring(curElement.getStartLocation().getStartOffset(), curElement.getEndLocation().getEndOffset() + 1);
	}

	private XmlFile getXmlFile(Project project, File file, String content)
	{
		if(!file.getName().toLowerCase().endsWith(".xml"))
		{
			return null;
		}

		try
		{
			XmlFile xmlFile = XmlFile.parse(content, -1, new FileParseCollector(project, file));
			return xmlFile;
		} catch(Exception ex)
		{
			logger.trace("Failed to parse xml file: " + file.getName() + " Error: " + ex);
			return null;
		}
	}
	
	@Override
	public boolean isStepInsertablePosition(FileEditor fileEditor)
	{
		XmlFileLocation loc = getXmlFileLocation(fileEditor);
		
		if(loc == null)
		{
			return false;
		}
		
		return (loc.getType() == XmlLocationType.CHILD_ELEMENT);
	}

	@Override
	public XmlFileLocation getXmlFileLocation(FileEditor fileEditor)
	{
		try
		{
			XmlFileLocation loc = XmlLoctionAnalyzer.getLocation(fileEditor.getContent(), fileEditor.getCaretPosition());
			return loc;
		}catch(Exception ex)
		{
			ideNotificationPanel.displayWarning("Failed to parse xml till current location. Error: " + ex.getMessage());
			return null;
		}
	}
}
