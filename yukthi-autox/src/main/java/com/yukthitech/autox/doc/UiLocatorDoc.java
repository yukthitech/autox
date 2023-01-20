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
package com.yukthitech.autox.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * Ui locator documentation.
 * @author akiran
 */
public class UiLocatorDoc extends AbstractDocInfo
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the locator.
	 */
	private String name;
	
	/**
	 * Description of the locator.
	 */
	private String description;
	
	/**
	 * Examples of the parser.
	 */
	private List<Example> examples = new ArrayList<>();

	/**
	 * Instantiates a new ui locator doc.
	 */
	public UiLocatorDoc()
	{}
	
	/**
	 * Instantiates a new ui locator doc.
	 *
	 * @param name the name
	 * @param description the description
	 */
	public UiLocatorDoc(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	/**
	 * Gets the name of the locator.
	 *
	 * @return the name of the locator
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the locator.
	 *
	 * @param name the new name of the locator
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description of the locator.
	 *
	 * @return the description of the locator
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the locator.
	 *
	 * @param description the new description of the locator
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the examples of the parser.
	 *
	 * @return the examples of the parser
	 */
	public List<Example> getExamples()
	{
		return examples;
	}

	/**
	 * Sets the examples of the parser.
	 *
	 * @param examples the new examples of the parser
	 */
	public void setExamples(List<Example> examples)
	{
		this.examples = examples;
	}
	
	/**
	 * Adds the example.
	 *
	 * @param ex the ex
	 */
	public void addExample(Example ex)
	{
		this.examples.add(ex);
	}

	public boolean hasExamples()
	{
		return CollectionUtils.isNotEmpty(examples);
	}
	
	public void addExamples(Collection<Example> examples)
	{
		if(examples == null)
		{
			return;
		}
		
		this.examples.addAll(examples);
	}
	
	public boolean hasParams()
	{
		return false;
	}
}
