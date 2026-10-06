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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.test.ITestCaseData;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Root element for dynamic data provider external XML files.
 */
public class DynamicTestDataList implements Validateable
{
	private List<DataProviderBean> beans = new ArrayList<>();
	
	private List<DynamicTestCaseData> testCaseData = new ArrayList<>();
	
	public void addBean(DataProviderBean bean)
	{
		this.beans.add(bean);
	}
	
	public List<DataProviderBean> getBeans()
	{
		return beans;
	}
	
	public void addTestCaseData(DynamicTestCaseData data)
	{
		this.testCaseData.add(data);
	}
	
	public List<ITestCaseData> getTestCaseData()
	{
		return new ArrayList<ITestCaseData>(testCaseData);
	}
	
	/**
	 * Wires bean registry into each test-case-data entry for clone resolution.
	 */
	public void wireBeanRegistry()
	{
		Map<String, DataProviderBean> registry = new HashMap<>();
		
		for(DataProviderBean bean : beans)
		{
			if(bean.getId() != null)
			{
				registry.put(bean.getId(), bean);
			}
		}
		
		for(DynamicTestCaseData data : testCaseData)
		{
			data.setBeanRegistry(registry);
		}
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(CollectionUtils.isEmpty(testCaseData))
		{
			throw new ValidateException("No test-case-data specified in dynamic test-data-list.");
		}
		
		Set<String> beanIds = new HashSet<>();
		Map<String, DataProviderBean> registry = new HashMap<>();
		
		for(DataProviderBean bean : beans)
		{
			if(!beanIds.add(bean.getId()))
			{
				throw new ValidateException("Duplicate bean id encountered in dynamic test-data-list: " + bean.getId());
			}
			
			registry.put(bean.getId(), bean);
		}
		
		for(DynamicTestCaseData data : testCaseData)
		{
			for(IDataValueInstruction instruction : data.getValueDefinition().getInstructions())
			{
				if(instruction instanceof DataCloneInstruction)
				{
					DataCloneInstruction clone = (DataCloneInstruction) instruction;
					
					if(!registry.containsKey(clone.getBeanId()))
					{
						throw new ValidateException("Clone references unknown bean id '" + clone.getBeanId()
								+ "' in test-case-data: " + data.getName());
					}
				}
			}
		}
	}
}
