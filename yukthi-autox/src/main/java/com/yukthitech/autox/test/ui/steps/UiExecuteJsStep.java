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

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * Can be used to execute js code.
 * @author akiran
 */
@Executable(name = "uiExecuteJs", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, 
	message = "Can be used to execute js code. If the result needs to be set on context, from js code 'return' should be used to return approp value.")
public class UiExecuteJsStep extends AbstractParentUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Script to execute.
	 */
	@Param(description = "Script to execute")
	private String script;
	
	/**
	 * If specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context. Default: null.
	 */
	@Param(description = "If specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context. Default: null", required = false,
			attrName = true)
	private String resultAttribute;

	/**
	 * Sets the script to execute.
	 *
	 * @param script the new script to execute
	 */
	public void setScript(String script)
	{
		if(StringUtils.isBlank(script))
		{
			throw new InvalidArgumentException("Script can not be empty");
		}
		
		this.script = script;
	}
	
	/**
	 * Sets the if specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context. Default: null.
	 *
	 * @param resultAttribute the new if specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context
	 */
	public void setResultAttribute(String resultAttribute)
	{
		this.resultAttribute = resultAttribute;
	}
	
	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Executing JS script - {}", script);
		
		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);
		
		Object res = ((JavascriptExecutor)driver).executeScript(script);
		
		if(resultAttribute != null)
		{
			exeLogger.debug("Setting the result of JS execution as context attribute with name '{}'. Result: {}", resultAttribute, res);
			context.setAttribute(resultAttribute, res);
		}
	}
	
	@Override
	public void validate() throws ValidateException
	{
		super.validate();
		
		if(StringUtils.isBlank(script))
		{
			throw new ValidateException("Script cannot be empty.");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Execute Js Step [");

		builder.append("Script: ").append(script);

		builder.append("]");
		return builder.toString();
	}
}
