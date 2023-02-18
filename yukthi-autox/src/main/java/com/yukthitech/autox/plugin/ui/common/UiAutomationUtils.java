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
package com.yukthitech.autox.plugin.ui.common;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.plugin.ui.SeleniumPlugin;
import com.yukthitech.autox.plugin.ui.SeleniumPluginSession;
import com.yukthitech.autox.prefix.PrefixEpression;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.autox.test.CustomExpressionFailedException;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.exceptions.UnsupportedOperationException;

/**
 * Common utils used by automation.
 * 
 * @author akiran
 */
public class UiAutomationUtils
{
	private static Logger logger = LogManager.getLogger(UiAutomationUtils.class);

	/**
	 * Pattern expected to be used by locator strings.
	 */
	private static Pattern LOCATOR_PATTERN = Pattern.compile("(\\w+)\\s*\\:\\s*(.*)");

	/**
	 * Fetches input form field type of specified element.
	 * 
	 * @param element
	 *            Element for which form field type has to be determined.
	 * @return Matching form field type
	 */
	public static FormFieldType getFormFieldType(WebElement element)
	{
		String tagName = element.getTagName().toLowerCase();

		if("textarea".equals(tagName))
		{
			return FormFieldType.MULTI_LINE_TEXT;
		}

		if("select".equals(tagName))
		{
			return FormFieldType.DROP_DOWN;
		}

		if("input".equals(tagName))
		{
			String type = "" + element.getAttribute("type");
			type = type.toLowerCase();

			switch (type)
			{
				case "number":
					return FormFieldType.INT;
				case "password":
					return FormFieldType.PASSWORD;
				case "radio":
					return FormFieldType.RADIO_BUTTON;
				case "checkbox":
					return FormFieldType.CHECK_BOX;
				case "date":
					return FormFieldType.DATE;
				case "hidden":
					return FormFieldType.HIDDEN_FIELD;
				default:
					return FormFieldType.TEXT;
			}
		}

		return null;
	}

	/**
	 * Populates specified field with specified value.
	 * 
	 * @param context
	 *            Automation context
	 * @param parentElement
	 *            Parent attribute name under which target element can be found
	 * @param locator
	 *            Locator of the target field. If locator pattern is not used, this will be assumed as name.
	 * @param value
	 *            Value to be populated
	 * @return True, if population was successful.
	 */
	public static boolean populateField(String driverName, Object parentElement, String locator, Object value)
	{
		WebElement parent = getParentElement(driverName, parentElement);
		return populateField(driverName, parent, locator, value);
	}
	
	public static PrefixEpression getCustomUiLocator(String locator)
	{
		return PrefixExpressionFactory.getExpressionFactory().parseCustomUiLocator(locator);
	}
	
	/**
	 * Populates specified field with specified value.
	 * 
	 * @param context
	 *            Automation context
	 * @param parent
	 *            Parent under which target element can be found
	 * @param locator
	 *            Locator of the target field. If locator pattern is not used, this will be assumed as name.
	 * @param value
	 *            Value to be populated
	 * @return True, if population was successful.
	 */
	public static boolean populateField(String driverName, WebElement parent, String locator, Object value)
	{
		logger.trace("For field {} under parent {} setting value - {}", locator, parent, value);
		
		AutomationContext context = AutomationContext.getInstance();
		PrefixEpression customUiLocator = getCustomUiLocator(locator);
		
		if(customUiLocator != null)
		{
			try
			{
				customUiLocator.setValue(value);
				return true;
			}catch(CustomExpressionFailedException ex)
			{
				logger.error("Setting value by custom locator failed. Error: " + ex.getMessage());
				return false;
			} catch(Exception ex)
			{
				throw new InvalidStateException("Custom locator operation failed. Locator: " + locator, ex);
			}
		}

		Matcher matcher = LOCATOR_PATTERN.matcher(locator); 
		
		if(!matcher.matches())
		{
			locator = LocatorType.NAME.getKey() + ":" + locator;
		}

		List<WebElement> elements = findElements(driverName, parent, locator, true);

		// if no elements found with specified name
		if(elements == null || elements.isEmpty())
		{
			context.getExecutionLogger().debug("No element found with final locator: {}", locator);
			return false;
		}

		WebElement element = elements.get(0);
		String tagName = element.getTagName().toLowerCase();

		FormFieldType type = getFormFieldType(element);

		if(type != null)
		{
			if(type.isMultiFieldAccessor())
			{
				type.getFieldAccessor().setValue(driverName, elements, value);
			}
			else
			{
				if(elements.size() > 1)
				{
					logger.warn("Multiple elements found for locator '{}'. Choosing the first element for population", locator);
				}
				
				type.getFieldAccessor().setValue(driverName, element, value);
			}
		}
		else
		{
			throw new UnsupportedOperationException("Encountered unsupported input tag '{}' for data population", tagName);
		}

		// find the parent element enclosing input elements
		return true;
	}

	/**
	 * Fetches the element with specified locator.
	 * 
	 * @param context
	 *            Context to be used
	 * @param parentElement
	 *            Parent attribute name under which element need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @return Matching element
	 */
	public static WebElement findElement(String driverName, Object parentElement, String locator)
	{
		List<WebElement> elements = findElements(driverName, parentElement, locator);

		if(elements == null || elements.size() == 0)
		{
			return null;
		}

		return elements.get(0);
	}

	/**
	 * Fetches the element with specified locator.
	 * 
	 * @param context
	 *            Context to be used
	 * @param parent
	 *            Parent under which element need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @return Matching element
	 */
	public static WebElement findElement(String driverName, WebElement parent, String locator)
	{
		List<WebElement> elements = findElements(driverName, parent, locator, true);

		if(elements == null || elements.size() == 0)
		{
			return null;
		}

		return elements.get(0);
	}
	
	/**
	 * Fetches parent element from context with specified name.
	 * @param context
	 * @param parentElement
	 * @return
	 */
	private static WebElement getParentElement(String driverName, Object parentElement)
	{
		if(parentElement == null)
		{
			return null;
		}
		
		if(parentElement instanceof WebElement)
		{
			return (WebElement) parentElement;
		}
		
		if(!(parentElement instanceof String))
		{
			throw new InvalidArgumentException("Invalid parent element type encountered: {}", parentElement.getClass().getName());
		}
		
		String parentLocator = (String) parentElement;
		Matcher matcher = LOCATOR_PATTERN.matcher(parentLocator);
		
		//if locator is of locator pattern
		if(matcher.matches())
		{
			return findElement(driverName, null, parentLocator);
		}
		
		WebElement parent = null;
		
		AutomationContext context = AutomationContext.getInstance();
		Object parentObj = context.getAttribute(parentLocator);
		
		if(parentObj == null)
		{
			throw new InvalidArgumentException("Failed to find parent element with name: {}", parentLocator);
		}
		
		if(!(parentObj instanceof WebElement))
		{
			throw new InvalidArgumentException("Non web-element found as parent with name: {}", parentLocator);
		}
		
		parent = (WebElement) parentObj;
		return parent;
	}

	/**
	 * Fetches the elements with specified locator.
	 * 
	 * @param context
	 *            Context to be used
	 * @param parentElement
	 *            Parent attributed name under which elements need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @return Matching elements
	 */
	public static List<WebElement> findElements(String driverName, Object parentElement, String locator)
	{
		WebElement parent = getParentElement(driverName, parentElement);
		return findElements(driverName, parent, locator, false);
	}
	
	public static By getLocator(String locator)
	{
		Matcher matcher = LOCATOR_PATTERN.matcher(locator);
		LocatorType locatorType = LocatorType.JS;
		String query = null;

		// if the locator string matches required pattern
		if(matcher.matches())
		{
			locatorType = LocatorType.getLocatorType(matcher.group(1));
			query = matcher.group(2);

			// if invalid locator is specified
			if(locatorType == null)
			{
				throw new InvalidArgumentException("Invalid key '{}' encountered in locator - {}", matcher.group(1), locator);
			}
		}
		else
		{
			query = locator;
		}

		By locatorBy = null;

		// find the locator based on prefix
		switch (locatorType)
		{
			case ID:
				locatorBy = By.id(query);
				break;
			case CSS:
				locatorBy = By.cssSelector(query);
				break;
			case CLASS:
				locatorBy = By.className(query);
				break;
			case NAME:
				locatorBy = By.name(query);
				break;
			case TAG:
				locatorBy = By.tagName(query);
				break;
			case XPATH:
				locatorBy = By.xpath(query);
				break;
			default:
				locatorBy = null;
		}
		
		return locatorBy;
	}
	
	/**
	 * Fetches the elements with specified locator.
	 * 
	 * @param context
	 *            Context to be used
	 * @param parent
	 *            Parent under which elements need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @return Matching elements
	 */
	public static List<WebElement> findElements(String driverName, WebElement parent, String locator, boolean singleElementExpected)
	{
		logger.trace("Trying to find element with location '{}' under parent - {}", locator, parent);

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);

		By locatorBy = getLocator(locator);

		logger.trace("For locator '{}' using locator-by - {}", locator, locatorBy);
		
		if(locatorBy == null)
		{
			return null;
		}

		List<WebElement> result = null;

		// if locator type is not defined (which would be the case for JS
		// locator type)
		/*
		if(locatorBy == null)
		{
			Object res = ((JavascriptExecutor) driver).executeScript("return $(" + query + ").get()");

			if(res instanceof Collection)
			{
				result = new ArrayList<WebElement>((Collection) res);
			}
			else
			{
				result = Arrays.asList((WebElement) res);
			}
		}
		// if parent is defined
		else if(parent != null)
		*/
		
		if(parent != null)
		{
			result = parent.findElements(locatorBy);
		}
		else
		{
			result = driver.findElements(locatorBy);
		}

		if(logger.isTraceEnabled())
		{
			logger.trace("For locator '{}' found elements as - {}", locator, toString(result));
		}
		
		if(singleElementExpected && result != null && result.size() > 1)
		{
			AutomationContext.getInstance().getExecutionLogger().warn("Given locator '{}' resulted in multiple elements: {}", locator, toString(result));
		}

		return result;
	}

	/**
	 * Waits for specified amount of time.
	 * 
	 * @param millis
	 *            Milli seconds to wait.
	 */
	public static void waitFor(long millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Checks for checkFunction to be true, if not waits for 1 sec and again
	 * tries to check. This process will be repeated for "iterationCount" number
	 * of times. If result is still false, exception "ex" will be thrown.
	 * 
	 * @param checkFunction
	 *            Function to check
	 * @param retryCount
	 *            Total number of retries that should happen.
	 * @param gapTime
	 *            Gap time in seconds to wait between each check.
	 * @param waitMessage
	 *            Wait message to be logged during waiting.
	 * @param ex
	 *            Exception to be thrown if all tries fail.
	 */
	public static void validateWithWait(Supplier<Boolean> checkFunction, int retryCount, long gapTime, String waitMessage, RuntimeException ex)
	{
		logger.trace(waitMessage);
		
		for(int i = 0; i < retryCount; i++)
		{
			if(checkFunction.get())
			{
				return;
			}

			waitFor(gapTime);
		}

		throw ex;
	}

	/**
	 * Waits for specified checkFunction to become true. For specified amount of waitTime. gapTime represents the polling interval.
	 * @param checkFunction
	 * @param waitTimeInMillis
	 * @param gapTimeInMillis
	 * @return
	 */
	public static boolean waitWithPoll(Supplier<Boolean> checkFunction, int waitTimeInMillis, int gapTimeInMillis)
	{
		long iterationCount = waitTimeInMillis / gapTimeInMillis;

		for(int i = 0; i < iterationCount; i++)
		{
			if(checkFunction.get())
			{
				return true;
			}

			waitFor(gapTimeInMillis);
		}
		
		return false;
	}

	/**
	 * Generates html node string from specified elements.
	 * 
	 * @param context
	 *            Automation context
	 * @param elements
	 *            Elements to be converted
	 * @return Converted string.
	 */
	private static String toString(Collection<WebElement> elements)
	{
		return elements
					.stream()
					.map(elem -> toString(elem))
					.collect(Collectors.joining(", "));
	}
	
	private static String toString(WebElement element)
	{
		if(element == null)
		{
			return "null";
		}
		
		StringBuilder builder = new StringBuilder("[");
		
		builder.append("Tag: ").append(element.getTagName()).append(", ");
		
		Rectangle bounds = element.getRect();
		
		if(bounds == null)
		{
			builder.append("Bounds: null").append(", ");
		}
		else
		{
			builder.append("Bounds: ").append(String.format("(x: %s, y: %s, width: %s, height: %s)", bounds.x, bounds.y, bounds.width, bounds.height)).append(", ");
		}
		
		builder.append("Visible: ").append(element.isDisplayed()).append(", ");
		builder.append("Enabled: ").append(element.isEnabled());
		
		builder.append("]");
		
		return builder.toString();
	}

	public static boolean isElementNotAvailableException(Exception ex)
	{
		if(ex instanceof ElementNotInteractableException)
		{
			return true;
		}
		
		if(ex instanceof StaleElementReferenceException)
		{
			return true;
		}

		if(ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not clickable"))
		{
			return true;
		}
		
		return false;
	}
}
