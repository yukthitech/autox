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
package com.yukthitech.prism.xmlfile;

import java.util.Map;
import java.util.TreeMap;

/**
 * Simple utility wrapper over the text which can be used to find line number based on index.
 * @author akranthikiran
 */
public class TextContent
{
	private String text;
	
	/**
	 * Maps from index to line number.
	 */
	private TreeMap<Integer, Integer> idxToLine;

	public TextContent(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	private void buildIndex()
	{
		if(idxToLine != null)
		{
			return;
		}
		
		TreeMap<Integer, Integer> idxToLine = new TreeMap<>();
		idxToLine.put(0, 0);
		
		int lineNo = 1;
		int idx = text.indexOf('\n', 0);
		
		while(idx >= 0)
		{
			idx = idx + 1;
			
			idxToLine.put(idx, lineNo);
			idx = text.indexOf('\n', idx);
			lineNo++;
		}
		
		this.idxToLine = idxToLine;
	}
	
	/**
	 * Returns zero based line number.
	 * @param index
	 * @return
	 */
	public int lineOf(int index)
	{
		if(index < 0)
		{
			return -1;
		}
		
		buildIndex();
		Map.Entry<Integer, Integer> floorEntry = idxToLine.floorEntry(index);
		return floorEntry.getValue();
	}
	
	public int indexOf(String substr)
	{
		return text.indexOf(substr);
	}
}
