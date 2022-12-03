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

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.filter.ExpressionConfig;
import com.yukthitech.autox.filter.ExpressionFactory;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Data provider that can be configured to provide simple static data list.
 * @author akiran
 */
public class ListDataProvider extends AbstractDataProvider implements Validateable
{
	/**
	 * List of objects to be passed for test case.
	 */
	private List<TestCaseData> dataLst = new ArrayList<>();
	
	/**
	 * Adds new data object for this data provider.
	 * @param data data to add
	 */
	public void addData(Object data)
	{
		this.dataLst.add(new TestCaseData("" + data, data));
	}
	
	/**
	 * Adds new data object for this data provider.
	 * @param data data to add
	 */
	public void addTestCaseData(TestCaseData data)
	{
		this.dataLst.add(data);
	}
	
	/**
	 * Sets the step data list to this data provider.
	 * @param data data to be set
	 */
	@SuppressWarnings("rawtypes")
	public void addStepDataList(Object data)
	{
		ExpressionFactory expressionFactory = ExpressionFactory.getExpressionFactory();
		Object result = expressionFactory.parseExpression(AutomationContext.getInstance(), data, new ExpressionConfig(null, TestCaseDataList.class));
		
		if(!(result instanceof List))
		{
			throw new InvalidArgumentException("Non list object was specified as data list - {}", result.getClass().getName());
		}
		
		for(Object obj : ((List) result) )
		{
			if(obj instanceof TestCaseData)
			{
				addTestCaseData( (TestCaseData) obj);
			}
			else
			{
				addData(obj);
			}
		}
	}
	
	@Override
	public List<TestCaseData> getStepData()
	{
		return dataLst;
	}

	@Override
	public void validate() throws ValidateException
	{
		if(dataLst.isEmpty())
		{
			throw new ValidateException("No data specified for list data provider.");
		}
	}
}
