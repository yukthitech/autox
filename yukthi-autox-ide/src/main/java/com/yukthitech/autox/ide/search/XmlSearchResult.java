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

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlSearchResult extends SearchResult
{
	private Document document;
	private Element element;
	
	public XmlSearchResult(File file, int start, int end, int lineNo, String lineHtml, 
			Document document, Element element)
	{
		super(file, start, end, lineNo, lineHtml);
		this.document = document;
		this.element = element;
	}

	public Document getDocument()
	{
		return document;
	}
	
	public Element getElement()
	{
		return element;
	}
}
