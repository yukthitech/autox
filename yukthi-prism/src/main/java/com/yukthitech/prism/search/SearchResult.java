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
package com.yukthitech.prism.search;

import java.io.File;
import java.util.Objects;

/**
 * Result of search.
 * @author akranthikiran
 */
public class SearchResult
{
	private File file;
	
	private int start;
	
	private int end;
	
	private int lineNo;
	
	private String lineHtml;

	public SearchResult(File file, int start, int end, int lineNo, String lineHtml)
	{
		this.file = file;
		this.start = start;
		this.end = end;
		this.lineNo = lineNo;
		this.lineHtml = lineHtml;
	}

	public File getFile()
	{
		return file;
	}

	public int getStart()
	{
		return start;
	}

	public int getEnd()
	{
		return end;
	}

	public int getLineNo()
	{
		return lineNo;
	}

	public String getLineHtml()
	{
		return lineHtml;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof SearchResult))
		{
			return false;
		}

		SearchResult other = (SearchResult) obj;
		return file.equals(other.file) 
				&& start == other.start
				&& end == other.end;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return Objects.hash(file, start, end);
	}
}
