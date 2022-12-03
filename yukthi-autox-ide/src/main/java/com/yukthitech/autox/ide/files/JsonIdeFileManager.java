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
package com.yukthitech.autox.ide.files;

import java.io.File;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.AbstractIdeFileManager;
import com.yukthitech.autox.ide.model.Project;

/**
 * Ide file manager for json files.
 * @author akiran
 */
@Service
public class JsonIdeFileManager extends AbstractIdeFileManager
{
	@Override
	public boolean isSuppored(Project project, File file)
	{
		return file.getName().toLowerCase().endsWith(".json");
	}

	@Override
	public String getSyntaxEditingStyle(String extension)
	{
		return RSyntaxTextArea.SYNTAX_STYLE_JSON;
	}
}
