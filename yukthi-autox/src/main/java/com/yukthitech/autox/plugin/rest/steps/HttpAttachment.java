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
package com.yukthitech.autox.plugin.rest.steps;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.Param;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents the attachment that can be sent along with request.
 * @author akiran
 */
public class HttpAttachment implements Validateable, Serializable
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the attachment.
	 */
	@Param(name = "name", description = "Name of the attachment", required = true)
	private String name;
	
	/**
	 * File of the attachment.
	 */
	@Param(name = "file", description = "Resource to be added as attachment. Should be resource expression.", required = true)
	private String file;
	
	/**
	 * File name to be used for attachment.
	 */
	@Param(name = "fileName", description = "File name to be used for attachment", required = false)
	private String fileName;
	
	/**
	 * Flag indicating if the attachment should be parsed as free marker template.
	 */
	@Param(name = "parseAsTemplate", description = "Flag indicating if the attachment should be parsed as free marker template. Defaults to false", required = false)
	private boolean parseAsTemplate = false;

	/**
	 * Condition for this condition. If specified, this attachement will be included if condition evaluates to true.
	 */
	@Param(name = "condition", description = "Condition for this attachment. If specified, this attachement will be included if condition evaluates to true.", required = false)
	private String condition;

	/**
	 * Instantiates a new http attachment.
	 */
	public HttpAttachment()
	{}


	/**
	 * Gets the name of the attachment.
	 *
	 * @return the name of the attachment
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the attachment.
	 *
	 * @param name the new name of the attachment
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the file of the attachment.
	 *
	 * @return the file of the attachment
	 */
	public String getFile()
	{
		return file;
	}

	/**
	 * Sets the file of the attachment.
	 *
	 * @param file the new file of the attachment
	 */
	public void setFile(String file)
	{
		this.file = file;
	}

	/**
	 * Gets the flag indicating if the attachment should be parsed as free marker template.
	 *
	 * @return the flag indicating if the attachment should be parsed as free marker template
	 */
	public boolean isParseAsTemplate()
	{
		return parseAsTemplate;
	}

	/**
	 * Sets the flag indicating if the attachment should be parsed as free marker template.
	 *
	 * @param parseAsTemplate the new flag indicating if the attachment should be parsed as free marker template
	 */
	public void setParseAsTemplate(boolean parseAsTemplate)
	{
		this.parseAsTemplate = parseAsTemplate;
	}

	/**
	 * Gets the condition for this condition. If specified, this attachement
	 * will be included if condition evaluates to true.
	 *
	 * @return the condition for this condition
	 */
	public String getCondition()
	{
		return condition;
	}

	/**
	 * Sets the condition for this condition. If specified, this attachement
	 * will be included if condition evaluates to true.
	 *
	 * @param condition
	 *            the new condition for this condition
	 */
	public void setCondition(String condition)
	{
		this.condition = condition;
	}
	
	/**
	 * Gets the file name to be used for attachment.
	 *
	 * @return the file name to be used for attachment
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Sets the file name to be used for attachment.
	 *
	 * @param fileName
	 *            the new file name to be used for attachment
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Name can not be null or empty");
		}
		
		if(StringUtils.isBlank(file))
		{
			throw new ValidateException("File can not be null or empty");
		}
	}
}
