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
package com.yukthitech.autox.plugin.ui.steps;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * Helps in switching the frames.
 * 
 * @author akiran
 */
@Executable(name = "uiSwitchFrame", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Helps in switching the frames")
public class SwitchFrame extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the frame. Either locator or index is mandatory.
	 */
	@Param(description = "Locator of the frame. Either locator or index is mandatory.", required = false)
	private String locator;
	
	/**
	 * Index of the frame. Either locator or index is mandatory.
	 */
	@Param(description = "Index of the frame. Either locator or index is mandatory.", required = false)
	private Integer index;

	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		if(index != null)
		{
			exeLogger.trace("Switching to frame with index: {}", index);
		}
		else
		{
			exeLogger.trace("Switching to frame with locator: {}", locator);
		}

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);
		
		if(index != null)
		{
			driver.switchTo().frame(index);
		}
		else
		{
			driver.switchTo().frame(locator);
		}
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(locator) && index == 0)
		{
			throw new ValidateException("Either of locator or index is mandatory.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Switch Frame [");

		builder.append("Locator: ").append(locator);
		builder.append(", Index: ").append(index);

		builder.append("]");
		return builder.toString();
	}
}
