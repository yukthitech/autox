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

import com.microsoft.playwright.Page;
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
import com.yukthitech.autox.test.TestCaseFailedException;

/**
 * Drag and drop the ui elements.
 * 
 * @author Pritam.
 */
@Executable(name = "uiDragAndDrop", group = Group.Ui, requiredPluginTypes = PlaywrightPlugin.class, message = "Drags the specified element to specified target")
public class DragAndDropStep extends AbstractParentUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Source html element to be dragged.
	 */
	@Param(description = "Locator of element which needs to be dragged")
	private String source;

	/**
	 * Destination html element area to drop.
	 */
	@Param(description = "Locator of element on which source element should be dropped")
	private String destination;

	@Override
	public void execute(AutomationContext context, IExecutionLogger logger)
	{
		logger.debug("Dragging element '{}' to element - {}", source, destination);
		
		UiElement sourceElement = UiAutomationUtils.findElement(driverName, (UiElement) null, source);
		UiElement destinationElement = UiAutomationUtils.findElement(driverName, (UiElement) null, destination);

		dragAndDrop(context, sourceElement, destinationElement, logger);
	}

	/**
	 * Drag and drop ui element.
	 * 
	 * @param sourceElement
	 *            the element to be dragged.
	 * @param destinationElement
	 *            area to be dropped.
	 */
	private void dragAndDrop(AutomationContext context, UiElement sourceElement, UiElement destinationElement, IExecutionLogger logger)
	{
		if(sourceElement == null || !sourceElement.isVisible())
		{
			logger.error("Failed to find source element to be dragged. Locator: {}", source);
			
			throw new TestCaseFailedException(this, "Failed to find drag element - '{}'", source);
		}

		if(destinationElement == null || !destinationElement.isVisible())
		{
			logger.error("Failed to find targer element to be dropped. Locator: {}", destination);
			
			throw new TestCaseFailedException(this, "Failed to find drop area element - '{}'", destination);
		}

		try
		{
			UiAutomationUtils.waitFor(2000);
			
			try
			{
				sourceElement.dragTo(destinationElement);
			}catch(PlaywrightException ex)
			{
				logger.debug("Drag using drag-to failed, trying to drag using mouse actions. Error: {}", "" + ex);
				
				PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
				Page page = session.getPage(driverName);
				
				sourceElement.hover();
				page.mouse().down();
				destinationElement.hover();
				page.mouse().up();
			}
		} catch(PlaywrightException e)
		{
			throw new TestCaseFailedException(this, "Error occurred while performing drag and drop operation between {} and {}. Error: {}", 
					sourceElement, destinationElement, "" + e, e);
		}
	}

	/**
	 * Gets source html element to be dragged.
	 * 
	 * @return source html element to be dragged.
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * Sets the source html element to be dragged.
	 * 
	 * @param source
	 *            the source html element.
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * Gets the destination drop area html element.
	 * 
	 * @return the destination drop area html element.
	 */
	public String getDestination()
	{
		return destination;
	}

	/**
	 * Sets the destination drop area html element.
	 * 
	 * @param destination
	 *            the new destination drop area html element.
	 */
	public void setDestination(String destination)
	{
		this.destination = destination;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Drang and Drop [");

		builder.append("Source: ").append(source);
		builder.append(",").append("Destination: ").append(destination);

		builder.append("]");
		return builder.toString();
	}

}
