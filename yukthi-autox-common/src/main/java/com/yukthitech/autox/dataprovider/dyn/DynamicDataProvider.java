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
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.dataprovider.AbstractDataProvider;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.autox.test.ITestCaseData;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Data provider that loads external test-data-list XML and evaluates test-case-data at runtime.
 * @author akiran
 */
public class DynamicDataProvider extends AbstractDataProvider implements Validateable
{
	private List<ITestCaseData> testCaseData = new ArrayList<>();

	/**
	 * Loads and parses the external test-data-list XML.
	 * @param data resource path or expression
	 */
	public void addStepDataList(String data)
	{
		String expression = data.trim();
		IExecutionLogger logger = AutomationContext.getInstance().getExecutionLogger();
		
		try
		{
			IResource resource = ResourceFactory.getResource(AutomationContext.getInstance(), expression, logger, false);
			String xmlContent = IOUtils.toString(resource.getInputStream(), (String) null);
			
			DynamicTestDataList list = new DynamicTestDataList();
			list = AutomationUtils.loadXmlBean(xmlContent, list, logger);
			list.wireBeanRegistry();
			
			this.testCaseData.addAll(list.getTestCaseData());
		}
		catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading dynamic data provider resource: {}", expression, ex);
		}
	}
	
	@Override
	public List<ITestCaseData> getStepData()
	{
		return testCaseData;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(getName()))
		{
			throw new ValidateException("No name specified for dynamic data provider.");
		}
		
		if(testCaseData.isEmpty())
		{
			throw new ValidateException("No test-case-data specified for dynamic data provider: " + getName());
		}
	}
}
