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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents example collection file.
 */
public class ExampleCollectionFile
{
	/**
	 * Represent collection of examples for a step/validation etc.
	 */
	public static class ExampleCollection
	{
		/**
		 * Name of the collection which should match with the target step/validation.
		 */
		private String name;
		
		/**
		 * List of examples of the document.
		 */
		private List<Example> examples = new ArrayList<Example>();

		/**
		 * Gets the name of the collection which should match with the target step/validation.
		 *
		 * @return the name of the collection which should match with the target step/validation
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the collection which should match with the target step/validation.
		 *
		 * @param name the new name of the collection which should match with the target step/validation
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Gets the list of examples of the document.
		 *
		 * @return the list of examples of the document
		 */
		public List<Example> getExamples()
		{
			return examples;
		}

		/**
		 * Sets the list of examples of the document.
		 *
		 * @param examples the new list of examples of the document
		 */
		public void setExamples(List<Example> examples)
		{
			this.examples = examples;
		}
		
		/**
		 * Adds the example.
		 *
		 * @param example the example
		 */
		public void addExample(Example example)
		{
			this.examples.add(example);
		}
		
		/**
		 * Adds the examples of specified collection into this collection.
		 * @param collection
		 */
		public void merge(ExampleCollection collection)
		{
			this.examples.addAll(collection.examples);
		}
	}
	
	/**
	 * Example collectsion from this file.
	 */
	private Map<String, ExampleCollection> collections = new HashMap<>();
	
	/**
	 * Gets the example collectsion from this file.
	 *
	 * @return the example collectsion from this file
	 */
	public Map<String, ExampleCollection> getCollections()
	{
		return collections;
	}
	
	/**
	 * Adds the collection to this file.
	 * @param collection
	 */
	public void addCollection(ExampleCollection collection)
	{
		this.collections.put(collection.getName(), collection);
	}
	
	/**
	 * Merges the collections and their examples from specified file into this file.
	 * @param file
	 */
	public void merge(ExampleCollectionFile file)
	{
		ExampleCollection existing = null;
		
		for(String name : file.collections.keySet())
		{
			existing = this.collections.get(name);
			
			if(existing != null)
			{
				existing.merge(file.collections.get(name));
			}
			else
			{
				this.collections.put(name, file.collections.get(name));
			}
		}
	}
	
	public List<Example> getExamples(String collection)
	{
		ExampleCollection exCollection = collections.get(collection);
		
		if(exCollection == null)
		{
			return null;
		}
		
		return exCollection.getExamples();
	}
}
