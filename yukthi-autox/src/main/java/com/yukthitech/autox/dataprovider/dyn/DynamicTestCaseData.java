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
package com.yukthitech.autox.dataprovider.dyn;

import java.util.LinkedHashMap;
import java.util.Map;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.autox.test.ITestCaseData;

/**
 * Test case data entry with deferred runtime evaluation for dynamic data provider.
 */
public class DynamicTestCaseData implements ITestCaseData
{
	private String name;
	
	private String description;
	
	private TestCaseDataValue value;
	
	private Map<String, DataProviderBean> beanRegistry;
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@Override
	public String getDescription()
	{
		return description;
	}
	
	public void setValue(TestCaseDataValue value)
	{
		this.value = value;
	}
	
	public TestCaseDataValue getValueDefinition()
	{
		return value;
	}
	
	public void setBeanRegistry(Map<String, DataProviderBean> beanRegistry)
	{
		this.beanRegistry = beanRegistry;
	}
	
	@Override
	public Object getValue()
	{
		AutomationContext context = AutomationContext.getInstance();
		PrefixExpressionFactory exprFactory = PrefixExpressionFactory.getExpressionFactory();
		LinkedHashMap<String, Object> result = new LinkedHashMap<>();
		
		context.set_this(result);
		
		try
		{
			for(IDataValueInstruction instruction : value.getInstructions())
			{
				instruction.execute(context, exprFactory, beanRegistry, result);
				context.set_this(result);
			}
		}
		finally
		{
			context.set_this(null);
		}
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[" + name + "]";
	}
}
