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
package com.yukthitech.autox.plugin.ui;

import java.util.List;

import com.yukthitech.autox.plugin.ui.common.UiElement;

/**
 * Matches for one locator query: every match, and the subset that is visible.
 * 
 * @author akiran
 */
public class ElementMatches
{
	private final int totalCount;
	
	private final int visibleCount;
	
	private final List<UiElement> visibleElements;
	
	ElementMatches(int totalCount, List<UiElement> visibleElements)
	{
		this.totalCount = totalCount;
		this.visibleCount = visibleElements.size();
		this.visibleElements = visibleElements;
	}
	
	/**
	 * Number of elements matched by the locator, including hidden ones.
	 */
	public int getTotalCount()
	{
		return totalCount;
	}
	
	/**
	 * Number of matched elements that are visible.
	 */
	public int getVisibleCount()
	{
		return visibleCount;
	}
	
	/**
	 * Matched elements that are visible. Hidden or unavailable matches are omitted.
	 */
	public List<UiElement> getVisibleElements()
	{
		return visibleElements;
	}
}
