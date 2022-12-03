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
package com.yukthitech.autox.ide.dialog;

/**
 * Command object to be used for find and replace operations.
 * @author akiran
 */
public class FindCommand
{
	/**
	 * String to be searched.
	 */
	private String searchString;
	
	/**
	 * Replace string;
	 */
	private String replaceWith;
	
	/**
	 * Flag indicating if search needs to be case sensitive;
	 */
	private boolean caseSensitive;
	
	/**
	 * Flag indicating if search needs to wrap the content.
	 */
	private boolean wrapSearch;
	
	/**
	 * Flag indicating if specified search string is regular expression.
	 */
	private boolean regularExpression;
	
	/**
	 * Flag indicating if search needs to be forward direction or reverse.
	 */
	private boolean reverseDirection;
	
	/**
	 * Options for regular expression.
	 */
	private int regexOptions;

	/**
	 * Gets the string to be searched.
	 *
	 * @return the string to be searched
	 */
	public String getSearchString()
	{
		return searchString;
	}

	/**
	 * Sets the string to be searched.
	 *
	 * @param searchString the new string to be searched
	 */
	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}

	/**
	 * Gets the replace string;.
	 *
	 * @return the replace string;
	 */
	public String getReplaceWith()
	{
		return replaceWith;
	}

	/**
	 * Sets the replace string;.
	 *
	 * @param replaceWith the new replace string;
	 */
	public void setReplaceWith(String replaceWith)
	{
		this.replaceWith = replaceWith;
	}

	/**
	 * Gets the flag indicating if search needs to be case sensitive;.
	 *
	 * @return the flag indicating if search needs to be case sensitive;
	 */
	public boolean isCaseSensitive()
	{
		return caseSensitive;
	}

	/**
	 * Sets the flag indicating if search needs to be case sensitive;.
	 *
	 * @param caseSensitive the new flag indicating if search needs to be case sensitive;
	 */
	public void setCaseSensitive(boolean caseSensitive)
	{
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Gets the flag indicating if search needs to wrap the content.
	 *
	 * @return the flag indicating if search needs to wrap the content
	 */
	public boolean isWrapSearch()
	{
		return wrapSearch;
	}

	/**
	 * Sets the flag indicating if search needs to wrap the content.
	 *
	 * @param wrapSearch the new flag indicating if search needs to wrap the content
	 */
	public void setWrapSearch(boolean wrapSearch)
	{
		this.wrapSearch = wrapSearch;
	}

	/**
	 * Gets the flag indicating if specified search string is regular expression.
	 *
	 * @return the flag indicating if specified search string is regular expression
	 */
	public boolean isRegularExpression()
	{
		return regularExpression;
	}

	/**
	 * Sets the flag indicating if specified search string is regular expression.
	 *
	 * @param regularExpression the new flag indicating if specified search string is regular expression
	 */
	public void setRegularExpression(boolean regularExpression)
	{
		this.regularExpression = regularExpression;
	}

	/**
	 * Gets the flag indicating if search needs to be forward direction or reverse.
	 *
	 * @return the flag indicating if search needs to be forward direction or reverse
	 */
	public boolean isReverseDirection()
	{
		return reverseDirection;
	}

	/**
	 * Sets the flag indicating if search needs to be forward direction or reverse.
	 *
	 * @param reverseDirection the new flag indicating if search needs to be forward direction or reverse
	 */
	public void setReverseDirection(boolean reverseDirection)
	{
		this.reverseDirection = reverseDirection;
	}

	/**
	 * Gets the options for regular expression.
	 *
	 * @return the options for regular expression
	 */
	public int getRegexOptions()
	{
		return regexOptions;
	}

	/**
	 * Sets the options for regular expression.
	 *
	 * @param regexOptions the new options for regular expression
	 */
	public void setRegexOptions(int regexOptions)
	{
		this.regexOptions = regexOptions;
	}
}
