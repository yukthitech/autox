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

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an example with description.
 * @author akiran
 */
public class Example implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final Pattern ESCAPE_INDENT_PATTERN = Pattern.compile("^(\\s*)");
	
	/**
	 * Description of example.
	 */
	private String description;
	
	/**
	 * Example text.
	 */
	private String content;
	
	/**
	 * Instantiates a new example.
	 */
	public Example()
	{}
	
	/**
	 * Instantiates a new example.
	 *
	 * @param description the description
	 * @param content the content
	 */
	public Example(String description, String content)
	{
		this.description = description;
		this.content = content;
	}

	/**
	 * Gets the description of example.
	 *
	 * @return the description of example
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of example.
	 *
	 * @param description the new description of example
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the example text.
	 *
	 * @return the example text
	 */
	public String getContent()
	{
		return content;
	}
	
	private static String escapeIndent(String content)
	{
		String lines[] = content.split("\\n");
		
		if(lines.length <= 1)
		{
			return content;
		}
		
		Matcher matcher = ESCAPE_INDENT_PATTERN.matcher(lines[lines.length - 1]);
		matcher.find();
		
		String escapeIndent = matcher.group(1);
		
		if(escapeIndent.length() <= 0)
		{
			return content;
		}

		StringBuilder res = new StringBuilder();
		
		for(String line: lines)
		{
			if(line.startsWith(escapeIndent))
			{
				line = line.substring(escapeIndent.length());
			}
			
			res.append(line).append("\n");
		}
		
		return res.toString().trim();
	}
	
	public String getEscapedContent()
	{
		if(content == null)
		{
			return "";
		}
		
		return escapeIndent(content)
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\n", "<br/>")
				.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
			;
	}

	/**
	 * Sets the example text.
	 *
	 * @param content the new example text
	 */
	public void setContent(String content)
	{
		this.content = content;
	}
	
	public static void main(String[] args)
	{
		String content = "		<first>\n"
				+ "			<sub>dffdf</sub>\n"
				+ "			<sub>dffdf</sub>\n"
				+ "		</first>";
		
		System.out.println(escapeIndent(content));
	}
}
