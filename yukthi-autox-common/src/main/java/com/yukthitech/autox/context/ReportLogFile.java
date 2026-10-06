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
package com.yukthitech.autox.context;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Immutable Wrapper over the file. 
 * @author akranthikiran
 */
public class ReportLogFile
{
	private File file;

	ReportLogFile(File file)
	{
		this.file = file;
	}
	
	public void copyContent(File source)
	{
		try
		{
			FileUtils.copyURLToFile(source.toURI().toURL(), file);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while copying content", ex);
		}
	}
	
	public File getFile()
	{
		return file;
	}
	
	@Override
	public String toString()
	{
		return file.toString();
	}
}
