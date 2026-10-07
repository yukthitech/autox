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

import com.google.gson.JsonObject;
import com.microsoft.playwright.CDPSession;
import com.microsoft.playwright.Page;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;

/**
 * Maximizes the current browser window.
 * 
 * @author akiran
 */
@Executable(name = "uiMaximizeBrowser", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Maximizes the current browser window.")
public class MaximizeBrowserStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
		Page page = session.getPage(driverName);
		
		exeLogger.debug("Maximizing the browser window. Before maximize browser details are: [Position: {}, Size: {}]",
				session.getBrowserPosition(driverName), session.getBrowserSize(driverName));
		
		boolean maximized = false;
		
		//chromium based browsers support maximizing through dev tools protocol
		try
		{
			CDPSession cdpSession = session.getBrowserContext(driverName).newCDPSession(page);
			
			try
			{
				JsonObject windowInfo = cdpSession.send("Browser.getWindowForTarget");
				int windowId = windowInfo.get("windowId").getAsInt();
				
				JsonObject bounds = new JsonObject();
				bounds.addProperty("windowState", "maximized");
				
				JsonObject params = new JsonObject();
				params.addProperty("windowId", windowId);
				params.add("bounds", bounds);
				
				cdpSession.send("Browser.setWindowBounds", params);
				maximized = true;
			}finally
			{
				cdpSession.detach();
			}
		}catch(RuntimeException ex)
		{
			exeLogger.debug("Failed to maximize window using browser protocol. Falling back to viewport resize. Error: {}", "" + ex);
		}
		
		if(!maximized)
		{
			try
			{
				//resize view port to available screen size
				@SuppressWarnings("unchecked")
				java.util.List<Number> screenSize = (java.util.List<Number>) page.evaluate("() => [window.screen.availWidth, window.screen.availHeight]");
				
				page.setViewportSize(screenSize.get(0).intValue(), screenSize.get(1).intValue());
			}catch(RuntimeException ex)
			{
				exeLogger.warn("Failed to maximize the browser window. Error: {}", "" + ex);
			}
		}
		
		exeLogger.debug("Waiting for 5 Sec for maximize to take affect");
		
		try
		{
			Thread.sleep(5000);
		}catch(Exception ex)
		{}
		
		exeLogger.debug("Post maximizing the browser window browser details are: [Position: {}, Size: {}]",
				session.getBrowserPosition(driverName), session.getBrowserSize(driverName));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Maximize Browser");
		return builder.toString();
	}

}
