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

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents file resource.
 * @author akiran
 */
public class FileResource implements IResource
{
	/**
	 * file path.
	 */
	private String resource;

	/**
	 * File input stream representing the resource.
	 */
	private FileInputStream fis;
	
	/**
	 * Flag indicating if this is raw type resource.
	 */
	private boolean rawType;
	
	public FileResource(String resource, boolean rawType)
	{
		this.resource = resource.trim();
		this.rawType = rawType;
		
		try
		{
			this.fis = new FileInputStream(this.resource);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to load file resource - {}", this.resource, ex);
		}
	}
	
	@Override
	public InputStream getInputStream()
	{
		return fis;
	}

	
	@Override
	public String toText()
	{
		try
		{
			String content = IOUtils.toString(fis, (String) null);
			fis.close();
			
			return content;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading string content from undelying file-resource - {}", resource, ex);
		}
	}

	@Override
	public void close()
	{
		try
		{
			fis.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to close file resource", ex);
		}
	}

	@Override
	public boolean isRawType()
	{
		return rawType;
	}

	@Override
	public String getName()
	{
		return resource;
	}
}
