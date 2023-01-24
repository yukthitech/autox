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
package com.yukthitech.prism.index;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

/**
 * Maintains referable elements in the project.
 * @author akranthikiran
 */
public class ProjectIndex
{
	/**
	 * Name to referable element.
	 */
	private Map<String, ReferableElement> referables = new HashMap<>();
	
	/**
	 * File to referables defined in that file.
	 */
	private Map<File, List<ReferableElement>> fileReferables = new HashMap<>();
	
	public synchronized void addReferableElements(File file, List<ReferableElement> newElements)
	{
		removeFile(file);
				
		if(CollectionUtils.isEmpty(newElements))
		{
			return;
		}
		
		for(ReferableElement element : newElements)
		{
			String refName = element.getRefName();
			referables.put(refName, element);
		}

		List<ReferableElement> newRefLst = new LinkedList<>(newElements);
		fileReferables.put(file, newRefLst);
	}
	
	public synchronized void removeFile(File file)
	{
		List<ReferableElement> existing = fileReferables.remove(file);
		
		if(existing != null)
		{
			existing.forEach(elem -> referables.remove(elem.getRefName()));
		}
	}
	
	/**
	 * Multiple scopes are defined for future scope for attribute reference which have to refer
	 * 	itself, all dependent test cases and test suites.
	 * @param type
	 * @param name
	 * @param scopes
	 * @return
	 */
	public ReferableElement getReferableElement(ReferenceType type, String name, List<String> scopes)
	{
		scopes = (scopes == null) ? Collections.emptyList() : scopes;
		
		for(String scope : scopes)
		{
			String refName = ReferableElement.getRefName(scope, name, type);
			ReferableElement ref = referables.get(refName);
			
			if(ref != null)
			{
				return ref;
			}
		}

		//check in global scope
		String refName = ReferableElement.getRefName(null, name, type);
		ReferableElement ref = referables.get(refName);
		return ref;
	}
}
