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
import java.util.LinkedList;
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
public class DefaultDataProvider extends AbstractDataProvider implements Validateable
{
	/**
	 * List of objects to be passed for test case.
	 */
	private List<Object> dataLst = new ArrayList<>();
	
	/**
	 * Sets the step data list to this data provider.
	 * @param data data to be set
	 */
	public void addStepDataList(Object data)
	{
		this.dataLst.add(data);
	}
	
	@SuppressWarnings("rawtypes")
	private void processDataList(Object data, List<TestCaseData> testCaseDataLst)
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
				testCaseDataLst.add( (TestCaseData) obj);
			}
			else
			{
				testCaseDataLst.add(new TestCaseData("" + obj, obj));
			}
		}
	}
	
	@Override
	public List<TestCaseData> getStepData()
	{
		List<TestCaseData> testCaseData = new LinkedList<TestCaseData>();
		
		for(Object data : this.dataLst)
		{
			processDataList(data, testCaseData);
		}
		
		return testCaseData;
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
