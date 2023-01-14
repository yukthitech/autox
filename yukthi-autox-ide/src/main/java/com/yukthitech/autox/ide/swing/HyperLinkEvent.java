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
package com.yukthitech.autox.ide.swing;

/**
 * Hyper link click event object. 
 * @author akiran
 */
public class HyperLinkEvent
{
	/**
	 * Href of the hyperlink.
	 */
	private String href;

	/**
	 * Instantiates a new hyper link event.
	 *
	 * @param href the href
	 */
	public HyperLinkEvent(String href)
	{
		this.href = href;
	}
	
	/**
	 * Gets the href of the hyperlink.
	 *
	 * @return the href of the hyperlink
	 */
	public String getHref()
	{
		return href;
	}
}
