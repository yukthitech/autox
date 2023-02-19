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
package com.yukthitech.autox.debug.common;

import java.io.File;
import java.util.UUID;

import org.openqa.selenium.InvalidArgumentException;

/**
 * Used in debug environment when a changed file is save and needs a reload.
 * @author akiran
 */
public class ClientMssgReloadFile extends ClientMessage
{
	private static final long serialVersionUID = 1L;

	/**
	 * File which is modified.
	 */
	private File file;

	public ClientMssgReloadFile(File file)
	{
		super(UUID.randomUUID().toString());
		
		if(file == null || !file.exists() || !file.isFile())
		{
			throw new InvalidArgumentException("No/invalid file is specified: " + file.getPath());
		}

		this.file = file;
	}

	public File getFile()
	{
		return file;
	}
}
