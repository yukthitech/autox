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
package com.yukthitech.autox.ide.search;

import java.util.List;

/**
 * Represents a file search query.
 * @author akranthikiran
 */
public class FileSearchQuery
{
	/**
	 * Search query type.
	 * @author akranthikiran
	 */
	public static enum QueryType
	{
		TEXT, XML;
	}
	
	public static enum Scope
	{
		ALL_PROJECTS, SELECTED_FOLDERS;
	}
	
	/**
	 * Search query type.
	 */
	private QueryType queryType;
	
	/**
	 * String to be searched.
	 */
	private String searchString;
	
	/**
	 * Whether search string should be search in case sensitive way. 
	 */
	private boolean caseSensitive;
	
	/**
	 * Whether search string should be used as regular expression.
	 */
	private boolean regularExpression;
	
	/**
	 * Regex option. If true, dot will span multiple lines.
	 */
	private boolean spanMultipleLines;
	
	/**
	 * String used for replacement.
	 */
	private String replaceWith;

	/**
	 * File name pattern.
	 */
	private List<String> fileNamePatterns;

	/**
	 * Scope of search.
	 */
	private Scope scope;
	
	/**
	 * Replacement script to be used. Used only by xml search.
	 */
	private String replacementScript;
	
	private FileSearchQuery()
	{}

	public static FileSearchQuery newTextQuery(String searchString, boolean caseSensitive, boolean regularExpression, boolean spanMultipleLines,
			String replaceWith, List<String> fileNamePatterns, Scope scope)
	{
		FileSearchQuery qry = new FileSearchQuery();
		
		qry.queryType = QueryType.TEXT;
		qry.searchString = searchString;
		qry.caseSensitive = caseSensitive;
		qry.regularExpression = regularExpression;
		qry.spanMultipleLines = spanMultipleLines;
		qry.replaceWith = replaceWith;
		qry.fileNamePatterns = fileNamePatterns;
		qry.scope = scope;
		
		return qry;
	}

	public static FileSearchQuery newXmlQuery(String searchString, Scope scope, String replacementScript)
	{
		FileSearchQuery qry = new FileSearchQuery();
		
		qry.queryType = QueryType.XML;
		qry.searchString = searchString;
		qry.scope = scope;
		qry.replacementScript = replacementScript;
		
		return qry;
	}

	public QueryType getQueryType()
	{
		return queryType;
	}

	public String getSearchString()
	{
		return searchString;
	}

	public boolean isCaseSensitive()
	{
		return caseSensitive;
	}

	public boolean isRegularExpression()
	{
		return regularExpression;
	}

	public String getReplaceWith()
	{
		return replaceWith;
	}

	public List<String> getFileNamePatterns()
	{
		return fileNamePatterns;
	}

	public Scope getScope()
	{
		return scope;
	}

	public String getReplacementScript()
	{
		return replacementScript;
	}

	public boolean isSpanMultipleLines()
	{
		return spanMultipleLines;
	}
}
