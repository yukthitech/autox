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
package com.yukthitech.autox.test.lang.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.autox.common.CommonFreeMarkerMethods;

/**
 * Test cases for common free marker methods.
 * @author akiran
 */
public class TCommonFreeMarkerMethods
{
	private ParentBean parentBean = new ParentBean();
	
	private Set<String> names = new HashSet<>( Arrays.asList("name1", "name2") );
	
	@BeforeClass
	public void setup()
	{
		parentBean.setParentBean(new ParentBean());
		parentBean.setSubbean(new Subbean("name1"));
		parentBean.getParentBean().setSubbean(new Subbean("name2"));
	}
	
	@Test
	public void testGetValueByXpath()
	{
		Assert.assertTrue( names.contains(CommonFreeMarkerMethods.getValueByXpath(parentBean, "/subbean/name")) );
	}

	@Test
	public void testGetValuesByXpath()
	{
		Set<Object> resNames = new HashSet<>( CommonFreeMarkerMethods.getValuesByXpath(parentBean, "//name") );
		Assert.assertTrue( names.equals(resNames) );
	}


	@Test
	public void testCountByXpath()
	{
		Assert.assertEquals(CommonFreeMarkerMethods.countOfXpath(parentBean, "//name"), 2);
		Assert.assertEquals(CommonFreeMarkerMethods.countOfXpath(parentBean, "//name1"), 0);	
	}
}
