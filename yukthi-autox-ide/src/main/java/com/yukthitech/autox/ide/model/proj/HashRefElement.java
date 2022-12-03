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

import java.io.File;

import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.xmlfile.MessageType;

/**
 * Element where attribute is referred.
 * @author akiran
 */
public class HashRefElement extends ReferenceElement
{
	/**
	 * Hash property being referred.
	 */
	private String name;

	public HashRefElement(File file, int lineNo, int pos, int end, String name)
	{
		super(file, lineNo, pos, end);
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public void compile(FileParseCollector collector)
	{
		if(!super.isValidAppProperty(name))
		{
			collector.addMessage(new FileParseMessage(MessageType.ERROR, 
					"No app-property found with refered name: " + name, super.lineNo, super.position, super.end));
		}
	}
}
