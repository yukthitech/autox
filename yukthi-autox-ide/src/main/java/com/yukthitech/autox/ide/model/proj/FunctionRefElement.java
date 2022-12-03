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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.ide.xmlfile.LocationRange;
import com.yukthitech.autox.ide.xmlfile.MessageType;
import com.yukthitech.autox.ide.xmlfile.ValueWithLocation;
import com.yukthitech.autox.test.FunctionParamDef;

/**
 * The Class FunctionRefElement.
 */
public class FunctionRefElement extends ReferenceElement
{
	/**
	 * The name.
	 */
	private String name;
	
	/**
	 * The func ref element.
	 */
	private Element funcRefElement;
	
	/**
	 * The parameter set.
	 */
	private Map<String, List<ValueWithLocation>> parameterSet = new HashMap<>();

	/**
	 * Instantiates a new function ref element.
	 *
	 * @param file the file
	 * @param funcRef the func ref
	 */
	public FunctionRefElement(File file, Element funcRef)
	{
		super(file, funcRef.getStartLocation().getStartLineNumber(), funcRef.getStartLocation().getStartOffset(), funcRef.getStartLocation().getEndOffset());
		
		this.funcRefElement = funcRef;
		this.parameterSet.putAll(funcRef.getTextChildValueMap());
		
		name = funcRef.getNormalizedName();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the func ref element.
	 *
	 * @return the func ref element
	 */
	public Element getFuncRefElement()
	{
		return funcRefElement;
	}

	@Override
	public void compile(FileParseCollector collector)
	{
		Set<FunctionDefElement> funcDefSet = new HashSet<>();
		super.parent.getFunctionDef(name, funcDefSet);
		
		if(funcDefSet.isEmpty())
		{
			collector.addMessage(new FileParseMessage(MessageType.ERROR, 
					"No function-def found with refered name: " + name, super.lineNo, super.position, super.end));
			return;
		}
		
		Map<String, FunctionParamDef> paramDefMap = funcDefSet.iterator().next().getParamMap();
		
		for(String paramName : this.parameterSet.keySet())
		{
			//ignore valid params
			if(paramDefMap.containsKey(paramName))
			{
				continue;
			}
			
			//if invalid param is found
			List<ValueWithLocation> locations = this.parameterSet.get(paramName);
			LocationRange location = locations.get(0).getNameLocation();
			
			collector.addMessage(new FileParseMessage(MessageType.ERROR, 
					String.format("Parameter '%s' is not matching with function parameters", paramName), 
					location.getStartLineNumber(), location.getStartOffset(), location.getEndOffset()));
		}
		
	}
}
