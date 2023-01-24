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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Maintains references in the file.
 * @author akranthikiran
 */
public class FileIndex
{
	private TreeMap<Integer, ReferenceElement> references = new TreeMap<>();
	
	public synchronized void setReferences(List<ReferenceElement> references)
	{
		this.references.clear();
		
		for(ReferenceElement ref : references)
		{
			int idx = ref.getStartPostion();
			this.references.put(idx, ref);
		}
	}
	
	public synchronized ReferenceElement getReference(int index)
	{
		Map.Entry<Integer, ReferenceElement> entry = references.floorEntry(index);
		
		if(entry == null)
		{
			return null;
		}
		
		return entry.getValue().includes(index) ? entry.getValue() : null;
	}
}
