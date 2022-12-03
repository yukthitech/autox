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
package com.yukthitech.autox.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Used to track functions and their references, which later is used to validate
 * the function-ref for params.
 * @author akiran
 */
public class FunctionAndRefTracker
{
	private Map<String, Function> globalFunctions = new HashMap<>();
	
	private Map<String, Map<String, Function>> testSuiteFunctions = new HashMap<>();
	
	private List<FunctionRef> functionReferences = new LinkedList<>();
	
	public void addGlobalFunction(Function function)
	{
		this.globalFunctions.put(function.getName(), function);
	}
	
	public void addTestSuiteFunction(String testSuite, Function function)
	{
		
	}
	
	public void addFunctionRef(FunctionRef funcRef)
	{
		
	}
}
