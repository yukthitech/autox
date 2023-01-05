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
package com.yukthitech.autox.ide.prop;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.autox.ide.xmlfile.LocationRange;

/**
 * Represens a property file with key-value entries along with location.
 * @author akranthikiran
 */
public class PropertyFile
{
	public static class PropertyEntry
	{
		String key;
		
		String value;
		
		LocationRange keyLocationRange;
		
		LocationRange valueLocationRange;

		public String getKey()
		{
			return key;
		}

		public String getValue()
		{
			return value;
		}

		public LocationRange getKeyLocationRange()
		{
			return keyLocationRange;
		}

		public LocationRange getValueLocationRange()
		{
			return valueLocationRange;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder(key);
			builder.append("=").append(value);
			
			builder.append(" {Key Loc: ").append(keyLocationRange).append(", ");
			builder.append("Value Loc: ").append(valueLocationRange).append("}");
			
			return builder.toString();
		}

	}
	
	private Map<String, PropertyEntry> entries = new LinkedHashMap<>();
	
	void addPropertyEntry(PropertyEntry entry)
	{
		//remove property if it already exist
		entries.remove(entry.getKey());
		
		entries.put(entry.getKey(), entry);
	}
	
	public List<PropertyEntry> entries()
	{
		return new ArrayList<>(entries.values());
	}
	
	public PropertyEntry getEntry(String key)
	{
		return entries.get(key);
	}
	
	public Map<String, String> toMap()
	{
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		
		entries.entrySet().forEach(entry -> 
		{
			map.put(entry.getKey(), entry.getValue().value);
		});
		
		return map;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");
		
		entries.entrySet().forEach(entry -> 
		{
			builder.append("\n\t").append(entry.getValue());
		});
		
		builder.append("\n]");
		return builder.toString();
	}

}
