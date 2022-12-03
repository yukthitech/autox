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
package com.yukthitech.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.common.PropertyAccessor;
import com.yukthitech.utils.CommonUtils;

public class TestPropertyAccessor
{
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	public void testGetProperty() throws Exception
	{
		Object data = objectMapper.readValue(TestPropertyAccessor.class.getResourceAsStream("/data/prop-access-test-data.json"), Object.class);
		
		assertEquals(PropertyAccessor.getProperty(data, "name"), "testTemplate");
		assertEquals(PropertyAccessor.getProperty(data, "versionLong"), 1000000);
		assertEquals(PropertyAccessor.getProperty(data, "audit.createdBy"), "svcjenkins");
		assertEquals(PropertyAccessor.getProperty(data, "audit(updatedBy)"), "svcupdate");
		assertEquals(PropertyAccessor.getProperty(data, "audit"), CommonUtils.toMap(
				"createdBy", "svcjenkins", 
				"updatedBy", "svcupdate"));
		
		assertEquals(PropertyAccessor.getProperty(data, "system.containers[0].name"), "insurance");
		assertEquals(PropertyAccessor.getProperty(data, "system.containers[0](name)"), "insurance");
		assertEquals(PropertyAccessor.getProperty(data, "system.products[0].supportedContainers[0]"), "bank");
		
		assertEquals(PropertyAccessor.getProperty(data, "keys[name = PERCENTAGE_FAILURE_FOR_BLOCKING].enumOptions[1].value"), "70");
		assertEquals(PropertyAccessor.getProperty(data, "keys[name = USER_THRESHOLD_LIMIT].enumOptions[value = 5].label"), "5 failed logins to one site");
		
		//test nested condition property
		assertEquals(PropertyAccessor.getProperty(data, "keys[groupNames[0] = REFRESH-group].name"), "REFRESH");
		assertEquals(PropertyAccessor.getProperty(data, "keys[cobMapProperty(processExpr) = thisKey.REFRESH].name"), "REFRESH");
		assertEquals(PropertyAccessor.getProperty(data, "keys[cobMapProperty.processExpr = thisKey.REFRESH].name"), "REFRESH");

		assertEquals(PropertyAccessor.getProperty(data, "system.containers[accountTypes[@this = CMA] = CMA].name"), "investment");
	}
	
	private void checkNegativeCase(Runnable runnable, String message)
	{
		try
		{
			runnable.run();
			Assert.fail();
		}catch(Exception ex)
		{
			Assert.assertEquals(ex.getMessage(), message);
		}
	}

	@Test
	public void testSetProperty() throws Exception
	{
		Object data = objectMapper.readValue(TestPropertyAccessor.class.getResourceAsStream("/data/prop-access-test-data.json"), Object.class);
		
		//test simple properties
		PropertyAccessor.setProperty(data, "name", "testTemplate1");
		assertEquals(PropertyAccessor.getProperty(data, "name"), "testTemplate1");
		
		PropertyAccessor.setProperty(data, "versionLong", 1000002);
		assertEquals(PropertyAccessor.getProperty(data, "versionLong"), 1000002);
		
		PropertyAccessor.setProperty(data, "audit.createdBy", "svcjenkins1");
		assertEquals(PropertyAccessor.getProperty(data, "audit.createdBy"), "svcjenkins1");
		
		PropertyAccessor.setProperty(data, "audit(updatedBy)", "svcjenkins2");
		assertEquals(PropertyAccessor.getProperty(data, "audit(updatedBy)"), "svcjenkins2");
		
		PropertyAccessor.setProperty(data, "audit(testedBy)", "svcjenkins3");
		assertEquals(PropertyAccessor.getProperty(data, "audit"), CommonUtils.toMap(
				"createdBy", "svcjenkins1", 
				"updatedBy", "svcjenkins2",
				"testedBy", "svcjenkins3"
				));
		
		//test properties with index
		PropertyAccessor.setProperty(data, "system.containers[0].name", "insurance1");
		assertEquals(PropertyAccessor.getProperty(data, "system.containers[0].name"), "insurance1");
		
		PropertyAccessor.setProperty(data, "system.products[1].supportedContainers[0]", "bankTest");
		assertEquals(PropertyAccessor.getProperty(data, "system.products[1].supportedContainers"), Arrays.asList("bankTest", "investment"));

		//test add element
		PropertyAccessor.addProperty(data, "system.products[1].supportedContainers[0]", "bank");
		assertEquals(PropertyAccessor.getProperty(data, "system.products[1].supportedContainers"), Arrays.asList("bank", "bankTest", "investment"));
		
		//test append element
		PropertyAccessor.addProperty(data, "system.products[1].supportedContainers[-1]", "credit");
		assertEquals(PropertyAccessor.getProperty(data, "system.products[1].supportedContainers"), Arrays.asList("bank", "bankTest", "investment", "credit"));
		
		//test properties with conditions
		PropertyAccessor.setProperty(data, "keys[name = USER_THRESHOLD_LIMIT].enumOptions[value = 5].label", "5 failed tests to one site");
		assertEquals(PropertyAccessor.getProperty(data, "keys[name = USER_THRESHOLD_LIMIT].enumOptions[value = 5].label"), "5 failed tests to one site");
	}

	@Test
	public void testRemoveProperty() throws Exception
	{
		Object data = objectMapper.readValue(TestPropertyAccessor.class.getResourceAsStream("/data/prop-access-test-data.json"), Object.class);
		
		//test simple properties
		PropertyAccessor.removeProperty(data, "versionLong");
		assertNull(PropertyAccessor.getProperty(data, "versionLong"));
		
		PropertyAccessor.setProperty(data, "audit(testedBy)", "svcjenkins3");
		PropertyAccessor.removeProperty(data, "audit.createdBy");
		PropertyAccessor.removeProperty(data, "audit(updatedBy)");

		assertEquals(PropertyAccessor.getProperty(data, "audit"), CommonUtils.toMap(
				"testedBy", "svcjenkins3"
				));
		
		//test properties with index
		PropertyAccessor.removeProperty(data, "system.containers[0]");
		assertEquals(PropertyAccessor.getProperty(data, "system.containers[0].name"), "reward");
		
		PropertyAccessor.removeProperty(data, "system.products[1].supportedContainers[0]");
		assertEquals(PropertyAccessor.getProperty(data, "system.products[1].supportedContainers"), Arrays.asList("investment"));

		//test properties with conditions
		PropertyAccessor.removeProperty(data, "keys[name = USER_THRESHOLD_LIMIT].enumOptions[value = 5].label");
		assertNull(PropertyAccessor.getProperty(data, "keys[name = USER_THRESHOLD_LIMIT].enumOptions[value = 5].label"));

		PropertyAccessor.removeProperty(data, "system.products[1].supportedDatasetAttribs[@this = BANK_TRANSFER_CODE]");
		PropertyAccessor.removeProperty(data, "system.products[1].supportedDatasetAttribs[@this = HOLDER_DETAILS]");
		assertEquals(PropertyAccessor.getProperty(data, "system.products[1].supportedDatasetAttribs"), 
				Arrays.asList("FULL_ACCT_NUMBER", "HOLDER_NAME", "PAYMENT_PROFILE"));
	}

	@Test
	public void testGetProperty_neg() throws Exception
	{
		Object data = objectMapper.readValue(TestPropertyAccessor.class.getResourceAsStream("/data/prop-access-test-data.json"), Object.class);
		
		checkNegativeCase(() -> 
			PropertyAccessor.getProperty(data, "keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty[0]"), 
			"Index is used on non-list value at: keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty");
		
		checkNegativeCase(
				() -> PropertyAccessor.getProperty(data, "keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty[processExpr = test]"), 
				"Condition is used on non-collection value at: keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty");

		checkNegativeCase(
				() -> PropertyAccessor.getProperty(data, "keys[name = NON_EXISTING].cobMapProperty", true), 
				"Property path 'keys[name = NON_EXISTING]' resulted in null");
	}

	@Test
	public void testSetProperty_neg() throws Exception
	{
		Object data = objectMapper.readValue(TestPropertyAccessor.class.getResourceAsStream("/data/prop-access-test-data.json"), Object.class);
		
		checkNegativeCase(() -> 
			PropertyAccessor.setProperty(data, "keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty[0]", "xyz"), 
			"Index is used on non-list value at: keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty");
		
		checkNegativeCase(
				() -> PropertyAccessor.setProperty(data, "keys[name = USER_THRESHOLD_LIMIT].enumOptions[value = 5]", "test"), 
				"Condition is used as last property to set property: keys[name = USER_THRESHOLD_LIMIT].enumOptions[value = 5]");
	}

	@Test
	public void testRemoveProperty_neg() throws Exception
	{
		Object data = objectMapper.readValue(TestPropertyAccessor.class.getResourceAsStream("/data/prop-access-test-data.json"), Object.class);
		
		checkNegativeCase(() -> 
			PropertyAccessor.removeProperty(data, "keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty[0]"), 
			"Index is used on non-list value at: keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty");
		
		checkNegativeCase(
				() -> PropertyAccessor.removeProperty(data, "keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty[idx = 0]"), 
				"Condition is used on non-collection value at: keys[name = THRESHOLD_LIMIT_FOR_BLOCKING].cobMapProperty");
	}
}
