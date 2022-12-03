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
package com.yukthitech.autox.config;

import com.yukthitech.utils.cli.CliArgument;

/**
 * Command line arguments expected by selenium plugin.
 * @author akiran
 */
public class SeleniumPluginArgs
{
	/**
	 * Name of the web driver to be used.
	 */
	@CliArgument(name = "wd", longName = "webdriver", description = "Webdriver to be used by selenium based test cases", required = false)
	private String webDriver;

	/**
	 * Gets the name of the web driver to be used.
	 *
	 * @return the name of the web driver to be used
	 */
	public String getWebDriver()
	{
		return webDriver;
	}

	/**
	 * Sets the name of the web driver to be used.
	 *
	 * @param webDriver the new name of the web driver to be used
	 */
	public void setWebDriver(String webDriver)
	{
		this.webDriver = webDriver;
	}
}
