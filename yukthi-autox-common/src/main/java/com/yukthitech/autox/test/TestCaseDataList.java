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

import java.util.LinkedList;

/**
 * List of test case data.
 * @author akiran
 */
public class TestCaseDataList extends LinkedList<TestCaseData>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Adds specified test case data to the list.
	 * @param data
	 */
	public void addTestCaseData(TestCaseData data)
	{
		super.add(data);
	}
}
