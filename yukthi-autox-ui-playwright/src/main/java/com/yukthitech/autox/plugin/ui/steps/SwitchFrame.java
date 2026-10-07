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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.PlaywrightException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;
import com.yukthitech.autox.plugin.ui.common.UiElement;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Helps in switching the frames.
 * 
 * @author akiran
 */
@Executable(name = "uiSwitchFrame", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Helps in switching the frames")
public class SwitchFrame extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the frame. Either locator or index is mandatory.
	 */
	@Param(description = "Locator (name/id or ui locator) of the frame. Either locator or index is mandatory.", required = false)
	private String locator;
	
	/**
	 * Index of the frame. Either locator or index is mandatory.
	 */
	@Param(description = "Index of the frame (among the child frames of current frame). Either locator or index is mandatory.", required = false)
	private Integer index;
	
	/**
	 * If true, switches back to the main frame of the page.
	 */
	@Param(description = "If true, switches back to the main frame (default content) of the page. Default: false", required = false)
	private boolean defaultContent = false;

	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}
	
	public void setDefaultContent(boolean defaultContent)
	{
		this.defaultContent = defaultContent;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
		
		if(defaultContent)
		{
			exeLogger.trace("Switching to default content (main frame)");
			session.setCurrentFrame(driverName, null);
			return;
		}
		
		Frame currentFrame = session.getCurrentFrame(driverName);
		Frame targetFrame = null;
		
		if(index != null)
		{
			exeLogger.trace("Switching to frame with index: {}", index);
			
			List<Frame> childFrames = currentFrame.childFrames();
			
			if(index < 0 || index >= childFrames.size())
			{
				throw new InvalidStateException("No frame found with index {} (available child frames: {})", index, childFrames.size());
			}
			
			targetFrame = childFrames.get(index);
		}
		else
		{
			exeLogger.trace("Switching to frame with locator: {}", locator);
			
			targetFrame = findFrame(currentFrame, exeLogger);
			
			if(targetFrame == null)
			{
				throw new InvalidStateException("No frame found with name/id/locator: {}", locator);
			}
		}
		
		session.setCurrentFrame(driverName, targetFrame);
	}
	
	/**
	 * Finds frame by name, id or ui-locator (in that order).
	 */
	private Frame findFrame(Frame currentFrame, IExecutionLogger exeLogger)
	{
		//check by name
		for(Frame child : currentFrame.childFrames())
		{
			if(locator.equals(child.name()))
			{
				return child;
			}
		}
		
		//check by id
		for(Frame child : currentFrame.childFrames())
		{
			try
			{
				ElementHandle frameElement = child.frameElement();
				
				if(locator.equals(frameElement.getAttribute("id")))
				{
					return child;
				}
			}catch(PlaywrightException ex)
			{
				exeLogger.debug("Failed to fetch frame element. Error: {}", "" + ex);
			}
		}
		
		//check by ui locator
		try
		{
			UiElement element = UiAutomationUtils.findElement(driverName, (UiElement) null, locator);
			
			if(element != null)
			{
				ElementHandle handle = element.getLocator().elementHandle();
				return handle.contentFrame();
			}
		}catch(RuntimeException ex)
		{
			exeLogger.debug("Failed to find frame using locator '{}'. Error: {}", locator, "" + ex);
		}
		
		return null;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		super.validate();
		
		if(!defaultContent && StringUtils.isBlank(locator) && index == null)
		{
			throw new ValidateException("Either of locator or index is mandatory (or defaultContent should be set to true).");
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
		builder.append(", Default Content: ").append(defaultContent);

		builder.append("]");
		return builder.toString();
	}
}
