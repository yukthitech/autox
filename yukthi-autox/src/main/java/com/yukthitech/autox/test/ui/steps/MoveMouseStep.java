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
package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = "uiMoveMouse", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Moves the mouse to specified target and optionally clicks the element.")
public class MoveMouseStep extends AbstractParentUiStep
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Mouse mouse in x-direction by specified amount.
	 */
	@Param(description = "Mouse mouse in x-direction by specified amount.")
	private int xoffset;
	
	/**
	 * Mouse mouse in y-direction by specified amount.
	 */
	@Param(description = "Mouse mouse in y-direction by specified amount.")
	private int yoffset;
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Moving mouse by specified offset: [x: {}, y: {}]", xoffset, yoffset);

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);

		Actions actions = new Actions(driver);
		actions.moveByOffset(xoffset, yoffset);
		actions.build().perform();
	}

	/**
	 * Sets the mouse mouse in x-direction by specified amount.
	 *
	 * @param xoffset the new mouse mouse in x-direction by specified amount
	 */
	public void setXoffset(int xoffset)
	{
		this.xoffset = xoffset;
	}

	/**
	 * Sets the mouse mouse in y-direction by specified amount.
	 *
	 * @param yoffset the new mouse mouse in y-direction by specified amount
	 */
	public void setYoffset(int yoffset)
	{
		this.yoffset = yoffset;
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
		builder.append("Move Mouse [");

		builder.append("x: ").append(xoffset);
		builder.append(", y: ").append(yoffset);

		builder.append("]");
		return builder.toString();
	}
}
