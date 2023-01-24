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
package com.yukthitech.prism.help;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

public class HelpNodeData
{
	private String id;
	
	private String label;
	
	private String documentation;
	
	private List<HelpNodeData> childNodes;
	
	private boolean filtered = true;
	
	private Object nodeValue;
	
	public HelpNodeData(String id, String label, String documentation, Object nodeValue)
	{
		this.id = id;
		this.label = label;
		this.documentation = documentation;
		this.nodeValue = nodeValue;
	}

	public String getLabel()
	{
		return label;
	}

	public List<HelpNodeData> getChildNodes()
	{
		return childNodes;
	}

	public boolean isFiltered()
	{
		return filtered;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getDocumentation()
	{
		return documentation;
	}
	
	public Object getNodeValue()
	{
		return nodeValue;
	}
	
	public void addHelpNode(HelpNodeData child)
	{
		if(this.childNodes == null)
		{
			this.childNodes = new ArrayList<>();
		}
		
		this.childNodes.add(child);
	}
	
	public void index(IndexWriter writer) throws IOException
	{
		if(nodeValue != null)
		{
			String labelTokens = label
					.replace("-", " ")
					.replaceAll("([A-Z])", " $1")
					.replace("  ", " ")
					.toLowerCase();
			
			writer.addDocument(Arrays.asList(
				new StringField("id", id, Store.YES),
				new TextField("doc", documentation + " " + labelTokens + " " + label, Store.NO)
			));
		}
		
		if(this.childNodes != null)
		{
			for(HelpNodeData node : this.childNodes)
			{
				node.index(writer);
			}
		}
	}
	
	public boolean filter(Set<String> filteredIds)
	{
		boolean res = false;
		
		if(childNodes != null)
		{
			for(HelpNodeData child: childNodes)
			{
				if(child.filter(filteredIds))
				{
					res = true;
				}
			}
		}
		
		if(res || filteredIds == null)
		{
			filtered = true;
			return true;
		}
		
		filtered = filteredIds.contains(id);
		return filtered;
	}
	
}
