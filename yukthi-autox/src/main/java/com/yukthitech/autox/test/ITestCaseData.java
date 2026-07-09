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

/**
 * Represents data for a single test-case iteration from a data provider.
 * @author akiran
 */
public interface ITestCaseData
{
	/**
	 * String representation of the data which will be appended to test case name.
	 * @return iteration name
	 */
	String getName();
	
	/**
	 * Description of what this test case data will do.
	 * @return description
	 */
	String getDescription();
	
	/**
	 * Value of this test case data.
	 * @return iteration value
	 */
	Object getValue();
}
