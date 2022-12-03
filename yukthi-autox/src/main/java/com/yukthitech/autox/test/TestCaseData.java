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
package com.yukthitech.autox.test;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.ccg.xml.DynamicBean;

/**
 * Represents data for test case execution. 
 * @author akiran
 */
public class TestCaseData
{
	/**
	 * Dynamic map used to evaluate expressions during fetch.
	 * @author akranthikiran
	 */
	public static class OnFetchEvalMap extends AbstractMap<String, Object>
	{
		private Map<String, Object> actualMap;

		public OnFetchEvalMap(Map<String, Object> actualMap)
		{
			this.actualMap = actualMap;
		}
		
		private Object process(Object val)
		{
			if(val == null)
			{
				return null;
			}
			
			AutomationContext context = AutomationContext.getInstance();
			return AutomationUtils.replaceExpressions("val", context, val);
		}
		
		@Override
		public Object get(Object key)
		{
			return process(actualMap.get(key));
		}
		
		@Override
		public int size()
		{
			return actualMap.size();
		}

		@Override
		public Set<Entry<String, Object>> entrySet()
		{
			return new AbstractSet<Map.Entry<String,Object>>()
			{
				@Override
				public Iterator<Entry<String, Object>> iterator()
				{
					final Iterator<Entry<String, Object>> mainIt = actualMap.entrySet().iterator();
					
					Iterator<Entry<String, Object>> it = new Iterator<Map.Entry<String,Object>>()
					{
						@Override
						public boolean hasNext()
						{
							return mainIt.hasNext();
						}

						@Override
						public Entry<String, Object> next()
						{
							final Entry<String, Object> entry = mainIt.next();
							
							return new Map.Entry<String, Object>()
							{
								@Override
								public String getKey()
								{
									return entry.getKey();
								}

								@Override
								public Object getValue()
								{
									return process(entry.getValue());
								}

								@Override
								public Object setValue(Object value)
								{
									throw new UnsupportedOperationException();
								}
							};
						}
						
					};
					
					return it;
				}

				@Override
				public int size()
				{
					return actualMap.size();
				}
			};
		}

		@Override
		public String toString()
		{
			return "TestCaseData$OnFetchEvalMap";
		}
	}
	
	/**
	 * String representation of the data which will be appended to test case name.
	 */
	private String name;
	
	/**
	 * Description of what this test case data will do.
	 */
	private String description;
	
	/**
	 * Value of this test case data.
	 */
	private Object value;
	
	/**
	 * If set to true, fmarker expressions in dynamic values will be evaluated
	 * every time it is used in testcase. 
	 */
	private boolean onFetchEval = false;
	
	/**
	 * Instantiates a new test case data.
	 */
	public TestCaseData()
	{}
	
	/**
	 * Instantiates a new test case data.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public TestCaseData(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Sets the if set to true, fmarker expressions in dynamic values will be
	 * evaluated every time it is used in testcase.
	 *
	 * @param onFetchEval
	 *            the new if set to true, fmarker expressions in dynamic values
	 *            will be evaluated every time it is used in testcase
	 */
	public void setOnFetchEval(boolean onFetchEval)
	{
		this.onFetchEval = onFetchEval;
	}

	/**
	 * Gets the string representation of the data which will be appended to test case name.
	 *
	 * @return the string representation of the data which will be appended to test case name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the string representation of the data which will be appended to test case name.
	 *
	 * @param name the new string representation of the data which will be appended to test case name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value of this test case data.
	 *
	 * @return the value of this test case data
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value of this test case data.
	 *
	 * @param value the new value of this test case data
	 */
	public void setValue(Object value)
	{
		this.value = AutomationUtils.parseObjectSource(AutomationContext.getInstance(), null, value, null);
	}
	
	/**
	 * Convenient method to set dynamic value for test data from xml.
	 * @param value
	 */
	public void setDynamicValue(DynamicBean value)
	{
		if(this.onFetchEval)
		{
			this.value = new OnFetchEvalMap(value.toSimpleMap());
		}
		else
		{
			this.value = value.toSimpleMap();
		}
	}
	
	/**
	 * Gets the description of what this test case data will do.
	 *
	 * @return the description of what this test case data will do
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of what this test case data will do.
	 *
	 * @param description the new description of what this test case data will do
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("Value: ").append(value);

		builder.append("]");
		return builder.toString();
	}

}
