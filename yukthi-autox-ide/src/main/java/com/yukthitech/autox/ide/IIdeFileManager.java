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

import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.IIdeCompletionProvider;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.XmlFileLocation;

/**
 * File manager specific to file type.
 * @author akiran
 */
public interface IIdeFileManager
{
	/**
	 * Used to check if specified file is supported by this manager.
	 * @param file file to check
	 * @return true if supported
	 */
	public boolean isSuppored(Project project, File file);
	
	/**
	 * Fetches style to be used for specified extension.
	 * @param extension extension for which style to be fetched.
	 * @return matching style.
	 */
	public String getSyntaxEditingStyle(String extension);
	
	/**
	 * Should return completion provider for current file editor.
	 * @param fileEditor File editor for which completion provider needs to be created.
	 * @return matching completion provider.
	 */
	public IIdeCompletionProvider getCompletionProvider(FileEditor fileEditor);
	
	/**
	 * Parses the file and adds the errors/warnings if any to messages.
	 * @param project Project in which file is present.
	 * @param file file whose content needs to be parsed.
	 * @param content content to be parsed
	 * @param collector collector to collect messages.
	 * 
	 * @return parsed file content object.
	 */
	public Object parseFile(Project project, File file, FileParseCollector collector);
	
	/**
	 * Parses the specified content and add the errors/warnings messages to messages.
	 * @param project Project in which file is present.
	 * @param name name of the file which holds this content.
	 * @param content content to be parsed
	 * @param collector collector to collect messages.
	 * 
	 * @return parsed file content object.
	 */
	public Object parseContent(Project project, String name, String content, FileParseCollector collector);
	
	/**
	 * Fetches the tooltip at the specified offset.
	 * @param fileEditor file editor in used
	 * @param parsedFile currently parsed file content.
	 * @param offset offset at which tooltip needs to be fetched.
	 * @return tooltip, if any
	 */
	public String getToolTip(FileEditor fileEditor, Object parsedFile, int offset);
	
	/**
	 * Fetches the active element name of specified node type.
	 * @param fileEditor file editor in use.
	 * @param nodeType node type to be fetched.
	 * @return matching node type at current position.
	 */
	public String getActiveElement(FileEditor fileEditor, String nodeType);
	
	/**
	 * Fetches the active element text of specified node type.
	 * @param fileEditor file editor in use.
	 * @param nodeType node type to be fetched.
	 * @return matching node type at current position.
	 */
	public default String getActiveElementText(FileEditor fileEditor, String nodeType)
	{
		return null;
	}
	
	/**
	 * Fetches line number of the active element.
	 * @param fileEditor file editor in use.
	 * @param nodeType node type to be fetched.
	 * @return line number of active element.
	 */
	public default int getActiveElementLineNumber(FileEditor fileEditor, String nodeType)
	{
		return -1;
	}
	
	/**
	 * Checks whether steps can be inserted at current position. 
	 * @param fileEditor file editor in use.
	 * @return flag indicating insertable position or not.
	 */
	public default boolean isStepInsertablePosition(FileEditor fileEditor)
	{
		return false;
	}
	
	/**
	 * Fetches current xml file location if any.
	 * @param fileEditor file editor in use
	 * @return xml file location if any.
	 */
	public default XmlFileLocation getXmlFileLocation(FileEditor fileEditor)
	{
		return null;
	}
	
	/**
	 * Checks if the execution/debug is supported by this file manager.
	 * @return
	 */
	public default boolean isExecutionSupported()
	{
		return false;
	}
}
