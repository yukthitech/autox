/**
 * Copyright (c) 2023 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

public class DebugUtils
{
	public static byte[] serialize(Object data)
	{
		try
		{
			return SerializationUtils.serialize((Serializable) data);
		}catch(Exception ex)
		{
			// exception case is handled post this block
		}
		
		String strRep = null;
		
		try
		{
			if(data instanceof Collection)
			{
				Collection<?> col = (Collection<?>) data;
				strRep = String.format("<Non-serializable> [Type: %s, Size: %s] %s", col.getClass().getName(), col.size(), col);
			}
			else if(data instanceof Map)
			{
				Map<?, ?> map = (Map<?, ?>) data;
				strRep = String.format("<Non-serializable> [Type: %s, Size: %s] %s", map.getClass().getName(), map.size(), map);
			}
			else
			{
				strRep = String.format("<Non-serializable> [Type: %s] %s", data.getClass().getName(), data);
			}
		}catch(Exception ex)
		{
			strRep = String.format("<Non-serializable> <Non-string-convertible> [Type: %s]", data.getClass().getName());
		}
		
		return SerializationUtils.serialize(strRep);
	}
}
