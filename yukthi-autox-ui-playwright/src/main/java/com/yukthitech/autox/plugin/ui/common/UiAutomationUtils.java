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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
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
	private static Pattern LOCATOR_PATTERN = Pattern.compile("(\\w+)\\s*\\:\\s*(.*)", Pattern.DOTALL);

	/**
	 * Pattern which can be used directly as css id selector.
	 */
	private static Pattern SIMPLE_CSS_IDENTIFIER = Pattern.compile("-?[_a-zA-Z][_a-zA-Z0-9-]*");

	/**
	 * Fetches input form field type of specified element.
	 * 
	 * @param element
	 *            Element for which form field type has to be determined.
	 * @return Matching form field type
	 */
	public static FormFieldType getFormFieldType(UiElement element)
	{
		String tagName = element.getTagName();
		tagName = (tagName == null) ? "" : tagName.toLowerCase();

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
	 * @param driverName
	 *            Name of the driver to use
	 * @param parentElement
	 *            Parent element (or parent locator or parent attribute name) under which target element can be found
	 * @param locator
	 *            Locator of the target field. If locator pattern is not used, this will be assumed as name.
	 * @param value
	 *            Value to be populated
	 * @return True, if population was successful.
	 */
	public static boolean populateField(String driverName, Object parentElement, String locator, Object value)
	{
		UiElement parent = getParentElement(driverName, parentElement);
		return populateField(driverName, parent, locator, value);
	}

	public static PrefixEpression getCustomUiLocator(String locator)
	{
		return PrefixExpressionFactory.getExpressionFactory().parseCustomUiLocator(locator);
	}

	/**
	 * Populates specified field with specified value.
	 * 
	 * @param driverName
	 *            Name of the driver to use
	 * @param parent
	 *            Parent under which target element can be found
	 * @param locator
	 *            Locator of the target field. If locator pattern is not used, this will be assumed as name.
	 * @param value
	 *            Value to be populated
	 * @return True, if population was successful.
	 */
	public static boolean populateField(String driverName, UiElement parent, String locator, Object value)
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

		List<UiElement> elements = findElements(driverName, parent, locator, true);

		// if no elements found with specified name
		if(elements == null || elements.isEmpty())
		{
			context.getExecutionLogger().debug("No element found with final locator: {}", locator);
			return false;
		}

		UiElement element = elements.get(0);
		String tagName = element.getTagName();

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

		return true;
	}

	/**
	 * Fetches the element with specified locator.
	 * 
	 * @param driverName
	 *            Name of the driver to use
	 * @param parentElement
	 *            Parent element (or parent locator or parent attribute name) under which element need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @return Matching element
	 */
	public static UiElement findElement(String driverName, Object parentElement, String locator)
	{
		UiElement parent = getParentElement(driverName, parentElement);
		List<UiElement> elements = findElements(driverName, parent, locator, true);

		if(elements == null || elements.size() == 0)
		{
			return null;
		}

		return elements.get(0);
	}

	/**
	 * Fetches the element with specified locator.
	 * 
	 * @param driverName
	 *            Name of the driver to use
	 * @param parent
	 *            Parent under which element need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @return Matching element
	 */
	public static UiElement findElement(String driverName, UiElement parent, String locator)
	{
		List<UiElement> elements = findElements(driverName, parent, locator, true);

		if(elements == null || elements.size() == 0)
		{
			return null;
		}

		return elements.get(0);
	}

	/**
	 * Fetches parent element from specified object. The parent can be an {@link UiElement}, a ui-locator
	 * or name of context attribute holding {@link UiElement}.
	 * 
	 * @param driverName
	 *            driver to be used
	 * @param parentElement
	 *            parent element object
	 * @return resolved parent element
	 */
	private static UiElement getParentElement(String driverName, Object parentElement)
	{
		if(parentElement == null)
		{
			return null;
		}

		if(parentElement instanceof UiElement)
		{
			return (UiElement) parentElement;
		}

		if(!(parentElement instanceof String))
		{
			throw new InvalidArgumentException("Invalid parent element type encountered: {}", parentElement.getClass().getName());
		}

		String parentLocator = (String) parentElement;
		Matcher matcher = LOCATOR_PATTERN.matcher(parentLocator);

		// if locator is of locator pattern
		if(matcher.matches())
		{
			return findElement(driverName, (UiElement) null, parentLocator);
		}

		AutomationContext context = AutomationContext.getInstance();
		Object parentObj = context.getAttribute(parentLocator);

		if(parentObj == null)
		{
			throw new InvalidArgumentException("Failed to find parent element with name: {}", parentLocator);
		}

		if(!(parentObj instanceof UiElement))
		{
			throw new InvalidArgumentException("Non ui-element found as parent with name: {}", parentLocator);
		}

		return (UiElement) parentObj;
	}

	/**
	 * Fetches the elements with specified locator.
	 * 
	 * @param driverName
	 *            Name of the driver to use
	 * @param parentElement
	 *            Parent element (or parent locator or parent attribute name) under which elements need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @return Matching elements
	 */
	public static List<UiElement> findElements(String driverName, Object parentElement, String locator)
	{
		UiElement parent = getParentElement(driverName, parentElement);
		return findElements(driverName, parent, locator, false);
	}

	public static String getLocatorType(String locator)
	{
		Matcher matcher = LOCATOR_PATTERN.matcher(locator);

		if(matcher.matches())
		{
			return matcher.group(1);
		}

		return LocatorType.NAME.getKey();
	}

	private static String quote(String value)
	{
		return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

	private static String cssEscape(String value)
	{
		StringBuilder builder = new StringBuilder();

		for(int i = 0; i < value.length(); i++)
		{
			char ch = value.charAt(i);

			if(Character.isLetterOrDigit(ch) || ch == '-' || ch == '_')
			{
				builder.append(ch);
			}
			else
			{
				builder.append('\\').append(ch);
			}
		}

		return builder.toString();
	}

	/**
	 * Converts the specified autox ui-locator (like "xpath: //div") into playwright selector.
	 * If locator does not use "type: query" format, it is assumed to be a name locator.
	 * 
	 * @param locator
	 *            autox locator
	 * @return playwright selector
	 */
	public static String getSelector(String locator)
	{
		Matcher matcher = LOCATOR_PATTERN.matcher(locator);
		LocatorType locatorType = LocatorType.NAME;
		String query = null;

		// if the locator string matches required pattern
		if(matcher.matches())
		{
			locatorType = LocatorType.getLocatorType(matcher.group(1));
			query = matcher.group(2).trim();

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

		switch (locatorType)
		{
			case ID:
				return SIMPLE_CSS_IDENTIFIER.matcher(query).matches() ? ("css=#" + query) : ("css=[id=" + quote(query) + "]");
			case CSS:
				return "css=" + query;
			case CLASS:
				return "css=." + cssEscape(query);
			case NAME:
				return "css=[name=" + quote(query) + "]";
			case TAG:
				return "css=" + query;
			case XPATH:
				return "xpath=" + query;
			default:
				throw new UnsupportedOperationException("Locator type '{}' is not supported by playwright ui plugin. Locator: {}", locatorType.getKey(), locator);
		}
	}

	/**
	 * Fetches the elements with specified locator.
	 * 
	 * @param driverName
	 *            Name of the driver to use
	 * @param parent
	 *            Parent under which elements need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @param singleElementExpected
	 *            If true, warning will be logged when multiple elements are found
	 * @return Matching elements
	 */
	public static List<UiElement> findElements(String driverName, UiElement parent, String locator, boolean singleElementExpected)
	{
		logger.trace("Trying to find element with location '{}' under parent - {}", locator, parent);

		String selector = getSelector(locator);
		logger.trace("For locator '{}' using selector - {}", locator, selector);

		Locator targetLocator = null;

		if(parent != null)
		{
			targetLocator = parent.getLocator().locator(selector);
		}
		else
		{
			PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
			targetLocator = session.newLocator(driverName, selector);
		}

		int count = targetLocator.count();
		List<UiElement> result = new ArrayList<>(count);

		for(int i = 0; i < count; i++)
		{
			result.add(new UiElement(driverName, targetLocator.nth(i), parent, selector));
		}

		if(logger.isTraceEnabled())
		{
			logger.trace("For locator '{}' found elements as - {}", locator, toString(result));
		}

		if(singleElementExpected && result.size() > 1)
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
	 * @return true if check function became true within specified time
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
	 * @param elements
	 *            Elements to be converted
	 * @return Converted string.
	 */
	private static String toString(Collection<UiElement> elements)
	{
		return elements
					.stream()
					.map(elem -> elem.describe())
					.collect(Collectors.joining(", "));
	}

	/**
	 * Checks if specified exception represents the case where element is not yet available/interactable (so that
	 * the operation can be retried).
	 * 
	 * @param ex
	 *            exception to check
	 * @return true if the operation can be retried
	 */
	public static boolean isElementNotAvailableException(Exception ex)
	{
		if(ex instanceof TimeoutError)
		{
			return true;
		}

		if(!(ex instanceof PlaywrightException))
		{
			return false;
		}

		String message = ex.getMessage();

		if(message == null)
		{
			return false;
		}

		message = message.toLowerCase();

		return message.contains("not visible")
				|| message.contains("not enabled")
				|| message.contains("not attached")
				|| message.contains("detached")
				|| message.contains("not stable")
				|| message.contains("not clickable")
				|| message.contains("intercepts pointer events")
				|| message.contains("outside of the viewport")
				|| message.contains("not editable")
				|| message.contains("waiting for element")
				|| message.contains("element is not");
	}

	/**
	 * Finds element and checks if it matches the expected availability (visible or hidden).
	 * Returns true when the condition is met, false when the caller should retry.
	 *
	 * @param driverName driver name to use
	 * @param parentElement parent element or parent locator
	 * @param locator locator of the target element
	 * @param expectVisible when true, element must be present and visible; when false, element must be absent or hidden
	 * @param exeLogger optional execution logger
	 * @return true if availability condition is met
	 */
	public static boolean isElementAvailable(String driverName, Object parentElement, String locator, boolean expectVisible, IExecutionLogger exeLogger)
	{
		UiElement element = findElementSafely(driverName, parentElement, locator, exeLogger);

		if(expectVisible)
		{
			if(element != null && isElementDisplayed(element, locator, exeLogger))
			{
				logDebug(exeLogger, "Found locator '{}' to be visible.", locator);
				return true;
			}
		}
		else if(element == null || !isElementDisplayed(element, locator, exeLogger))
		{
			logDebug(exeLogger, "Found locator '{}' to be hidden", locator);
			return true;
		}

		return false;
	}

	/**
	 * Finds a visible element. Returns null when the element is not found, not visible, or detached,
	 * indicating the caller should retry.
	 *
	 * @param driverName driver name to use
	 * @param parentElement parent element or parent locator
	 * @param locator locator of the target element
	 * @param exeLogger optional execution logger
	 * @return visible element, or null if not yet available
	 */
	public static UiElement findVisibleElement(String driverName, Object parentElement, String locator, IExecutionLogger exeLogger)
	{
		UiElement element = findElementSafely(driverName, parentElement, locator, exeLogger);

		if(element != null && isElementDisplayed(element, locator, exeLogger))
		{
			logDebug(exeLogger, "Found locator '{}' to be visible.", locator);
			return element;
		}

		return null;
	}

	private static UiElement findElementSafely(String driverName, Object parentElement, String locator, IExecutionLogger exeLogger)
	{
		try
		{
			return findElement(driverName, parentElement, locator);
		}
		catch(Exception ex)
		{
			if(isElementNotAvailableException(ex))
			{
				logDebug(exeLogger, "Found locator '{}' to be not accessible or available. Hence assuming element is not available. Error: {}", locator, "" + ex);
				return null;
			}

			logError(exeLogger, "An error occurred while trying to find element with locator - {}. Error: {}", locator, "" + ex);
			return null;
		}
	}

	private static boolean isElementDisplayed(UiElement element, String locator, IExecutionLogger exeLogger)
	{
		try
		{
			return element.isVisible();
		}
		catch(PlaywrightException ex)
		{
			logDebug(exeLogger, "Locator '{}' check resulted in an error (element might have got detached), which is going to be ignored. Error: {}", locator, "" + ex);
			return false;
		}
	}

	private static void logDebug(IExecutionLogger exeLogger, String message, Object... args)
	{
		if(exeLogger != null)
		{
			exeLogger.debug(message, args);
		}
	}

	private static void logError(IExecutionLogger exeLogger, String message, Object... args)
	{
		if(exeLogger != null)
		{
			exeLogger.error(message, args);
		}
	}
}
