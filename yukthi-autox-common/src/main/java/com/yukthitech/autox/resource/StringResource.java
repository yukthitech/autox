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
package com.yukthitech.autox.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Represents string resource.
 * @author akiran
 */
public class StringResource implements IResource
{
	/**
	 * Name of the source resource.
	 */
	private String name;

	/**
	 * String input representing the resource.
	 */
	private String content;
	
	/**
	 * Flag indicating if this is raw type resource.
	 */
	private boolean rawType;

	public StringResource(String name, String content, boolean rawType)
	{
		this.name = name;
		this.content = content;
		this.rawType = rawType;
	}

	public StringResource(String content, boolean rawType)
	{
		this.content = content;
		this.rawType = rawType;
	}
	
	@Override
	public InputStream getInputStream()
	{
		return new ByteArrayInputStream(content.getBytes());
	}

	@Override
	public String toText()
	{
		return content;
	}

	@Override
	public void close()
	{
	}

	@Override
	public boolean isRawType()
	{
		return rawType;
	}

	@Override
	public String getName()
	{
		return name;
	}
}
