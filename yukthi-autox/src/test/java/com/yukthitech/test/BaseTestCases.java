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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationLauncher;

/**
 * Base classes for test classes.
 * @author akranthikiran
 */
public class BaseTestCases
{
	protected ObjectMapper objectMapper = new ObjectMapper(); 
	
	@BeforeClass
	public void setup() throws Exception
	{
		AutomationLauncher.systemExitEnabled = false;
		TestServer.start(null);
	}
	
	@AfterClass
	public void close() throws Exception
	{
		TestServer.stop();
	}
	
	@BeforeMethod
	public void reset() throws Exception
	{
		AutomationLauncher.resetState();
	}
}
