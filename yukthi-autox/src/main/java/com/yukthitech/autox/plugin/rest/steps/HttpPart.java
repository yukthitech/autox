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
import com.yukthitech.autox.SourceType;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents part of the http request.
 * @author akiran
 */
public class HttpPart implements Serializable, Validateable
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the part.
	 */
	@Param(name = "name", description = "Name of the part", required = true)
	private String name;
	
	/**
	 * Value for the part.
	 */
	@Param(name = "value", description = "Value for the part. Can be object or resource source.", required = true, sourceType = SourceType.EXPRESSION)
	private Object value;
	
	/**
	 * Content type of the part. By default is is json part.
	 */
	@Param(name = "contentType", description = "Content type of the part. By default is is json part.", required = false)
	private String contentType = IRestConstants.JSON_CONTENT_TYPE;

	/**
	 * Condition for this part. If specified, this part will be included if condition evaluates to true.
	 */
	@Param(name = "condition", description = "Condition for this part. If specified, this part will be included if condition evaluates to true.", required = false)
	private String condition;

	/**
	 * Gets the name of the part.
	 *
	 * @return the name of the part
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the part.
	 *
	 * @param name the new name of the part
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value for the part.
	 *
	 * @return the value for the part
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value for the part.
	 *
	 * @param value the new value for the part
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 * Gets the content type of the part. By default is is json part.
	 *
	 * @return the content type of the part
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * Sets the content type of the part. By default is is json part.
	 *
	 * @param contentType the new content type of the part
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
	
	/**
	 * Gets the condition for this part. If specified, this part will be
	 * included if condition evaluates to true.
	 *
	 * @return the condition for this part
	 */
	public String getCondition()
	{
		return condition;
	}

	/**
	 * Sets the condition for this part. If specified, this part will be
	 * included if condition evaluates to true.
	 *
	 * @param condition
	 *            the new condition for this part
	 */
	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Name can not be null or blank");
		}
		
		if(value == null)
		{
			throw new ValidateException("Value can not be null.");
		}
	}
}
