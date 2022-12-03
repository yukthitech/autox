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

/**
 * Default ide file manager that will be used with files which dont have file manager configured.
 * @author akiran
 */
public class DefaultIdeFileManager implements IIdeFileManager
{
	@Override
	public boolean isSuppored(Project project, File file)
	{
		return false;
	}

	@Override
	public String getSyntaxEditingStyle(String extension)
	{
		return null;
	}

	@Override
	public IIdeCompletionProvider getCompletionProvider(FileEditor fileEditor)
	{
		return null;
	}

	@Override
	public Object parseFile(Project project, File file, FileParseCollector collector)
	{
		return null;
	}

	@Override
	public Object parseContent(Project project, String name, String content, FileParseCollector collector)
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
}
