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
package com.yukthitech.prism.files;

import java.io.File;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.common.IndexRange;
import com.yukthitech.prism.AbstractIdeFileManager;
import com.yukthitech.prism.index.FileParseCollector;
import com.yukthitech.prism.index.ReferableElement;
import com.yukthitech.prism.index.ReferenceType;
import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.prop.PropFileParser;
import com.yukthitech.prism.prop.PropertyFile;
import com.yukthitech.prism.xmlfile.LocationRange;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Ide file manager for property files.
 * @author akiran
 */
@Service
public class PropertyIdeFileManager extends AbstractIdeFileManager
{
	@Override
	public boolean isSuppored(Project project, File file)
	{
		return file.getName().toLowerCase().endsWith(".properties");
	}
	
	private void loadAppPropFile(String content, File appProperties, FileParseCollector collector)
	{
		try
		{
			PropertyFile propFile = PropFileParser.parse(content);
			
			for(PropertyFile.PropertyEntry entry : propFile.entries())
			{
				LocationRange locRange = entry.getKeyLocationRange();
				
				collector.addReferable(new ReferableElement(ReferenceType.APP_PROPERTY, entry.getKey(), null, 
						appProperties, new IndexRange(locRange.getStartOffset(), locRange.getEndOffset())));
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading prop file", ex);
		}
	}
	
	@Override
	public Object parseContent(Project project, File file, String content, FileParseCollector collector)
	{
		if(file.equals(project.getAppPropertyFile()))
		{
			loadAppPropFile(content, file, collector);
			return null;
		}
		
		return super.parseContent(project, file, content, collector);
	}

	@Override
	public String getSyntaxEditingStyle(String extension)
	{
		return RSyntaxTextArea.SYNTAX_STYLE_PROPERTIES_FILE;
	}
}
