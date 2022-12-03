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
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.AbstractIdeFileManager;
import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.editor.IIdeCompletionProvider;
import com.yukthitech.autox.ide.model.Project;

/**
 * Ide file manager for xml files.
 * @author akiran
 */
@Service
public class XmlIdeFileManager extends AbstractIdeFileManager
{
	private static Logger logger = LogManager.getLogger(XmlIdeFileManager.class);
	
	@Override
	public boolean isSuppored(Project project, File file)
	{
		if(!file.getName().toLowerCase().endsWith(".xml"))
		{
			return false;
		}
		
		Set<String> tsFolders = project.getTestSuitesFoldersList();
		
		for(String path : tsFolders)
		{
			File folder = new File(project.getBaseFolderPath(), path);
			
			if(IdeFileUtils.getRelativePath(folder, file) != null)
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public IIdeCompletionProvider getCompletionProvider(FileEditor fileEditor)
	{
		return null;
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
		return null;
	}
	
	@Override
	public String getActiveElement(FileEditor fileEditor, String nodeType)
	{
		return null;
	}

	@Override
	public boolean isStepInsertablePosition(FileEditor fileEditor)
	{
		return false;
	}

	@Override
	public XmlFileLocation getXmlFileLocation(FileEditor fileEditor)
	{
		return null;
	}
}
