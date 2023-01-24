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
package com.yukthitech.prism;

import java.io.InputStream;
import java.util.Map;

import org.openqa.selenium.InvalidArgumentException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Version
{
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static Map<String, Object> versionMap;
	
	@SuppressWarnings("unchecked")
	private static synchronized Map<String, Object> getVersionMap()
	{
		if(versionMap != null)
		{
			return versionMap;
		}

		try
		{
			versionMap = (Map<String, Object>) objectMapper.readValue(PrismUpdater.class.getResourceAsStream("/version/version.json"), Object.class);
			return versionMap;
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("An error occurred while loading version.json", ex);
		}
	}
	
	public static String getLocalVersion()
	{
		return (String) getVersionMap().get("version");
	}

	public static String getLocalAutoxVersion()
	{
		return (String) getVersionMap().get("autoxVersion");
	}

	@SuppressWarnings("unchecked")
	public static String getVersion(InputStream is)
	{
		try
		{
			Map<String, Object> map = (Map<String, Object>) objectMapper.readValue(is, Object.class);
			return (String) map.get("version");
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("An error occurred while loading version.json", ex);
		}
	}
}
