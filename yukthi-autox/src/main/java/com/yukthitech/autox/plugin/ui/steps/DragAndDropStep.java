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

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;
import com.yukthitech.autox.test.TestCaseFailedException;

/**
 * Drag and drop the web elements.
 * 
 * @author Pritam.
 */
@Executable(name = "uiDragAndDrop", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Drags the specified element to specified target")
public class DragAndDropStep extends AbstractUiStep
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
		
		WebElement sourceElement = UiAutomationUtils.findElement(driverName, (WebElement) null, source);
		WebElement destinationElement = UiAutomationUtils.findElement(driverName, (WebElement) null, destination);

		dragAndDrop(context, sourceElement, destinationElement, logger);
	}

	/**
	 * Drag and drop web element.
	 * 
	 * @param sourceElement
	 *            the element to be dragged.
	 * @param destinationElement
	 *            area to be dropped.
	 */
	private void dragAndDrop(AutomationContext context, WebElement sourceElement, WebElement destinationElement, IExecutionLogger logger)
	{
		if(!sourceElement.isDisplayed())
		{
			logger.error("Failed to find source element to be dragged. Locator: {}", source);
			
			throw new TestCaseFailedException(this, "Failed to find drag element - '{}'", source);
		}

		if(!destinationElement.isDisplayed())
		{
			logger.error("Failed to find targer element to be dropped. Locator: {}", destination);
			
			throw new TestCaseFailedException(this, "Failed to find drop area element - '{}'", destination);
		}

		try
		{
			SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
			seleniumSession.getWebDriver(driverName).manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
			
			Thread.sleep(2000);
			
			Actions actions = new Actions(seleniumSession.getWebDriver(driverName));

			//actions.dragAndDrop(sourceElement, destinationElement).build().perform();
			
			Action dragAndDrop = actions.clickAndHold(sourceElement)
					   .moveToElement(destinationElement, 2, 2)
					   .release()
					   .build();

			dragAndDrop.perform();
		} catch(StaleElementReferenceException ex)
		{
			//logger.error(ex, "Element with {} or {} is not attached to the page document", sourceElement, destinationElement);
			throw new TestCaseFailedException(this, "Element with {} or {} is not attached to the page document", sourceElement, destinationElement, ex);
		} catch(NoSuchElementException e)
		{
			//logger.error(e, "Element with {} or {} was not found in DOM ", sourceElement, destinationElement);
			throw new TestCaseFailedException(this, "Element with {} or {} was not found in DOM ", sourceElement, destinationElement, e);
		} catch(Exception e)
		{
			//logger.error(e, "Error occurred while performing drag and drop operation");
			throw new TestCaseFailedException(this, "Error occurred while performing drag and drop operation ", e);
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
