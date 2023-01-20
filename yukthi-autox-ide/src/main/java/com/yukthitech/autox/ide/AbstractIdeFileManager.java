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
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.editor.IIdeCompletionProvider;
import com.yukthitech.autox.ide.index.FileParseCollector;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.MessageType;

/**
 * Abstract base class for ide file managers. This provides caching ability during parsing files.
 * Data from the cache will be used till the timestamp of the file is same.
 * 
 * @author akiran
 */
public abstract class AbstractIdeFileManager implements IIdeFileManager
{
	private static Logger logger = LogManager.getLogger(AbstractIdeFileManager.class);
	
	protected static class XmlParseDetails
	{
		private FileParseCollector collector;
		
		private Object result;
		
		private long lastModifiedTime;

		public XmlParseDetails(FileParseCollector collector, Object result, long lastModifiedTime)
		{
			this.collector = collector;
			this.result = result;
			this.lastModifiedTime = lastModifiedTime;
		}
	}

	private Map<File, XmlParseDetails> parseCacheMap = new HashMap<>();

	@Override
	public synchronized final Object parseFile(Project project, File file, FileParseCollector collector)
	{
		XmlParseDetails parseDetails = parseCacheMap.get(file);
		
		if(parseDetails != null && parseDetails.lastModifiedTime == file.lastModified())
		{
			collector.load(parseDetails.collector);
			return parseDetails.result;
		}
		
		FileParseCollector newCollector = new FileParseCollector(project, file);
		Object result = null;
		
		try
		{
			result = parseContent(project, file, FileUtils.readFileToString(file, Charset.defaultCharset()), newCollector);
			collector.load(newCollector);
		}catch(Exception ex)
		{
			logger.debug("Failed to parse xml file: " + file.getPath(), ex);
			collector.addMessage(new FileParseMessage(MessageType.ERROR, "Failed to parse file with error: " + ex, 1));
		}
		
		parseCacheMap.put(file, new XmlParseDetails(newCollector, result, file.lastModified()));
		return result;
	}

	@Override
	public IIdeCompletionProvider getCompletionProvider(FileEditor fileEditor)
	{
		return null;
	}

	@Override
	public Object parseContent(Project project, File file, String content, FileParseCollector collector)
	{
		return null;
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
	
	public synchronized void clearCache()
	{
		parseCacheMap.clear();
	}
}
