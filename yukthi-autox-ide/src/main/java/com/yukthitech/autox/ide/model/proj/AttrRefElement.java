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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.xmlfile.MessageType;

/**
 * Element where attribute is referred.
 * @author akiran
 */
public class AttrRefElement extends ReferenceElement
{
	/**
	 * Name of the attribute referred by this ref.
	 */
	private String name;

	public AttrRefElement(File file, int lineNo, int pos, int end, String name)
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
		Set<AttrDefElement> attrDefSet = new HashSet<>();
		super.parent.getAttrDef(name, attrDefSet);
		
		if(attrDefSet.isEmpty())
		{
			collector.addMessage(new FileParseMessage(MessageType.ERROR, 
					"No attribute-def found with refered name: " + name, super.lineNo, super.position, super.end));
		}
		
		if(attrDefSet.size() > 1)
		{
			String locations = attrDefSet.stream()
					.limit(2)
					.map(def -> def.getFile().getName() + ":" + def.getLineNo())
					.collect(Collectors.joining(", "));
			
			//if more than 2 locations are found, ellipsify the location
			if(attrDefSet.size() > 2)
			{
				locations += " ...";
			}
			
			collector.addMessage(new FileParseMessage(MessageType.ERROR, 
					"Multiple attribute-def found with refered name: " + name + "\n"
							+ "[" + locations + "]", 
					super.lineNo, super.position, super.end));
		}
	}
}
