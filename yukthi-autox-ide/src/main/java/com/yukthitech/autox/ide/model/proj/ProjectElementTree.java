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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.autox.ide.FileParseCollector;

public class ProjectElementTree extends CodeElementContainer
{
	private Map<String, FunctionDefElement> functions = new HashMap<>();
	
	private Map<String, TestSuiteElement> testSuites = new HashMap<>();
	
	private Map<File, List<CodeElement>> fileToElements = new HashMap<>();
	
	private Map<String, Integer> appProp;

	public ProjectElementTree(Map<String, Integer> appProp)
	{
		super(null, -1);
		this.appProp = appProp;
	}
	
	@Override
	public boolean isValidAppProperty(String name)
	{
		return appProp.containsKey(name);
	}
	
	@Override
	protected void addFileElement(CodeElement element)
	{
		List<CodeElement> elements = fileToElements.get(element.getFile());
		
		if(elements == null)
		{
			elements = new ArrayList<>();
			fileToElements.put(element.getFile(), elements);
		}
		
		elements.add(element);
	}

	public void addFunction(FunctionDefElement element)
	{
		this.functions.put(element.getName(), element);
		addFileElement(element);
		element.setParent(this);
	}
	
	public void addTestSuite(TestSuiteElement testSuite)
	{
		if(testSuites.containsKey(testSuite.getName()))
		{
			return;
		}
		
		testSuites.put(testSuite.getName(), testSuite);
		//Note: As test suite spans multiple files, this will not be added as file-element
	}
	
	@Override
	public void compile(FileParseCollector collector)
	{
		super.compile(collector);
		
		testSuites.values().forEach(ts -> ts.compile(collector));
		functions.values().forEach(ts -> ts.compile(collector));
	}
}
