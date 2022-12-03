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
package com.yukthitech.autox;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Class to fetch current autox version.
 * @author akiran
 */
public class AutoxVersion
{
	/**
	 * Autox version.
	 */
	private static String version;
	
	static
	{
		try
		{
			InputStream is = AutoxVersion.class.getResourceAsStream("/autox-version.txt");
			version = IOUtils.toString(is, Charset.defaultCharset());
			version = version.trim();
			is.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading autox version", ex);
		}
	}
	
	/**
	 * Gets the autox version.
	 *
	 * @return the autox version
	 */
	public static String getVersion()
	{
		return version;
	}
}
