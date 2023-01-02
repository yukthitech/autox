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
package com.yukthitech.autox.ide.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

/**
 * Used to collect different ui elements across with a given "id". And access these
 * ui elements based on this "id".
 * @author akiran
 */
public class UiIdElementsManager
{
	private static Map<String, List<Object>> idElements = new HashMap<>();
	
	public static void registerElement(String id, Object element)
	{
		List<Object> elements = idElements.get(id);
		
		if(elements == null)
		{
			elements = new ArrayList<>();
			idElements.put(id, elements);
		}
		
		elements.add(element);
	}
	
	public static Set<String> getUiIds()
	{
		return idElements.keySet();
	}
	
	public static List<Object> getElements(String id)
	{
		List<Object> elements = idElements.get(id);
		
		if(elements == null)
		{
			return null;
		}
		
		return new ArrayList<>(elements);
	}

	public static Object getElement(String id)
	{
		List<Object> elements = idElements.get(id);
		
		if(elements == null)
		{
			return null;
		}
		
		return elements.get(0);
	}

	public static JComponent getComponent(String id)
	{
		List<Object> elements = idElements.get(id);
		
		if(elements == null)
		{
			return null;
		}
		
		return (JComponent) elements.get(0);
	}
}
