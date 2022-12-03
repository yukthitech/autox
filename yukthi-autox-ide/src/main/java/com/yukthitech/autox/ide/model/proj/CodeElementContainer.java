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
import java.util.Map;

import com.yukthitech.autox.ide.FileParseCollector;

public abstract class CodeElementContainer extends CodeElement
{
	private Map<String, AttrDefElement> attributes = new HashMap<>();

	private Map<String, AttrRefElement> attributeReferences = new HashMap<>();
	
	private Map<String, HashRefElement> hashReferences = new HashMap<>();

	private Map<String, FunctionRefElement> functionReferences = new HashMap<>();

	public CodeElementContainer(File file, int position)
	{
		super(file, position);
	}

	public void addAttribute(AttrDefElement element)
	{
		this.attributes.put(element.getName(), element);
		addFileElement(element);
		element.setParent(this);
	}

	public void addAttributeRef(AttrRefElement element)
	{
		this.attributeReferences.put(element.getName(), element);
		addFileElement(element);
		element.setParent(this);
	}
	
	public void addHashRef(HashRefElement element)
	{
		this.hashReferences.put(element.getName(), element);
		addFileElement(element);
		element.setParent(this);
	}

	public void addFunctionRef(FunctionRefElement element)
	{
		this.functionReferences.put(element.getName(), element);
		addFileElement(element);
		element.setParent(this);
	}
	
	@Override
	public void compile(FileParseCollector collector)
	{
		attributeReferences.values().forEach(ref -> ref.compile(collector));
		functionReferences.values().forEach(ref -> ref.compile(collector));
		hashReferences.values().forEach(ref -> ref.compile(collector));
	}
}
