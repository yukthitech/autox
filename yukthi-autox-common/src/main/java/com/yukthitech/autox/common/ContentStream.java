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
package com.yukthitech.autox.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Named content source that can be opened as an {@link InputStream}.
 * Used for application configuration and suite/test XML (file, string, or stream).
 * <p>
 * {@link #getSourceName()} is mandatory for location / log attribution (same role as
 * the former {@code sourceName} parameter on {@code loadXml}).
 * </p>
 *
 * @author akranthikiran
 */
public final class ContentStream
{
	@FunctionalInterface
	private interface StreamOpener
	{
		InputStream open() throws IOException;
	}
	
	private final String sourceName;
	
	private final StreamOpener opener;
	
	private ContentStream(String sourceName, StreamOpener opener)
	{
		this.sourceName = sourceName;
		this.opener = opener;
	}
	
	/**
	 * Logical or filesystem name used for locations and error messages.
	 */
	public String getSourceName()
	{
		return sourceName;
	}
	
	/**
	 * Opens a fresh stream over the content. Caller must close the returned stream.
	 * File and string sources may be opened repeatedly; stream-backed sources are buffered
	 * at construction so they may also be reopened.
	 */
	public InputStream openStream() throws IOException
	{
		return opener.open();
	}
	
	/**
	 * Content from a filesystem file. Source name is {@link File#getPath()}.
	 */
	public static ContentStream fromFile(File file)
	{
		if(file == null)
		{
			throw new InvalidArgumentException("File cannot be null");
		}
		
		return new ContentStream(file.getPath(), () -> new FileInputStream(file));
	}
	
	/**
	 * Content from a string (UTF-8). {@code sourceName} is required.
	 */
	public static ContentStream fromString(String content, String sourceName)
	{
		requireSourceName(sourceName);
		
		if(content == null)
		{
			throw new InvalidArgumentException("Content cannot be null");
		}
		
		byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
		return new ContentStream(sourceName, () -> new ByteArrayInputStream(bytes));
	}
	
	/**
	 * Content from an input stream. The stream is fully read and buffered; it is
	 * <b>not</b> closed. {@code sourceName} is required.
	 */
	public static ContentStream fromStream(InputStream inputStream, String sourceName)
	{
		requireSourceName(sourceName);
		
		if(inputStream == null)
		{
			throw new InvalidArgumentException("Input stream cannot be null");
		}
		
		byte[] bytes;
		
		try
		{
			bytes = IOUtils.toByteArray(inputStream);
		}catch(IOException ex)
		{
			throw new InvalidStateException("Failed to read content stream for source: {}", sourceName, ex);
		}
		
		return new ContentStream(sourceName, () -> new ByteArrayInputStream(bytes));
	}
	
	private static void requireSourceName(String sourceName)
	{
		if(StringUtils.isBlank(sourceName))
		{
			throw new InvalidArgumentException("sourceName is required (used for location / debug attribution)");
		}
	}
	
	@Override
	public String toString()
	{
		return "ContentStream[" + sourceName + "]";
	}
}
