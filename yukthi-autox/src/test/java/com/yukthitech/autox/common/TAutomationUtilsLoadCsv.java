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
package com.yukthitech.autox.common;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TAutomationUtilsLoadCsv
{
	public static class CsvPerson
	{
		private String name;
		private String age;
		private String city;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getAge()
		{
			return age;
		}

		public void setAge(String age)
		{
			this.age = age;
		}

		public String getCity()
		{
			return city;
		}

		public void setCity(String city)
		{
			this.city = city;
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testLoadCsvAsMaps() throws Exception
	{
		String csv = "name,age,city\nJohn,30,New York\nJane,25,London\n";

		Object result = AutomationUtils.loadObjectContent(csv, "people.csv", null, null);

		Assert.assertTrue(result instanceof List);
		List<Map<String, Object>> rows = (List<Map<String, Object>>) result;
		Assert.assertEquals(rows.size(), 2);
		Assert.assertEquals(rows.get(0).get("name"), "John");
		Assert.assertEquals(rows.get(0).get("age"), "30");
		Assert.assertEquals(rows.get(0).get("city"), "New York");
		Assert.assertEquals(rows.get(1).get("name"), "Jane");
		Assert.assertEquals(rows.get(1).get("city"), "London");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testLoadCsvAsBeans() throws Exception
	{
		String csv = "name,age,city\nJohn,30,New York\nJane,25,London\n";

		Object result = AutomationUtils.loadObjectContent(csv, "people.csv", CsvPerson.class, null);

		Assert.assertTrue(result instanceof List);
		List<CsvPerson> rows = (List<CsvPerson>) result;
		Assert.assertEquals(rows.size(), 2);
		Assert.assertEquals(rows.get(0).getName(), "John");
		Assert.assertEquals(rows.get(0).getAge(), "30");
		Assert.assertEquals(rows.get(0).getCity(), "New York");
		Assert.assertEquals(rows.get(1).getName(), "Jane");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testLoadCsvWithQuotedValues() throws Exception
	{
		String csv = "name,city\n\"Doe, John\",\"New York, NY\"\n";

		Object result = AutomationUtils.loadObjectContent(csv, "people.csv", null, null);

		List<Map<String, Object>> rows = (List<Map<String, Object>>) result;
		Assert.assertEquals(rows.size(), 1);
		Assert.assertEquals(rows.get(0).get("name"), "Doe, John");
		Assert.assertEquals(rows.get(0).get("city"), "New York, NY");
	}
}
