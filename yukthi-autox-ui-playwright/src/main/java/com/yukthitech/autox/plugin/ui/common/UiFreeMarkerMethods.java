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

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.PlaywrightException;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
import com.yukthitech.autox.prefix.PrefixEpression;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Free marker methdos related to ui functionality.
 * @author akiran
 */
public class UiFreeMarkerMethods
{
	/**
	 * Used to convert strings into json strings.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Fetches the element based on specified locator. This methods
	 * helps other method to accept locator or element directly.
	 * @param locator
	 * @param parent
	 * @return
	 */
	private static UiElement getElementByLocator(String driverName, Object locator, Object parent)
	{
		UiElement uiElement = null;
		
		if(locator instanceof UiElement)
		{
			uiElement = (UiElement) locator;
		}
		else if(locator instanceof String)
		{
			uiElement =  UiAutomationUtils.findElement(driverName, parent, (String)locator);
		}
		else
		{
			throw new InvalidStateException("Invalid locator/element type specified. Specified locator: {}", locator);
		}
		
		return uiElement;
	}
	
	@FreeMarkerMethod(
			description = "Fetches first element of specified locator.",
			returnDescription = "Matching ui element"
			)
	public static UiElement uiGetElement(
			@FmParam(name = "locator", description = "Locator of the ui element whose element needs to be fetched.") String locator, 
			@FmParam(name = "parent", description = "Optional. Ui-element or ui-locator or attr-name of parent ui-element.") Object parent,
			@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName
			) throws Exception
	{
		return getElementByLocator(driverName, locator, parent);
	}
	
	@FreeMarkerMethod(
			description = "Fetches all elements matching specified locator.",
			returnDescription = "Matching ui elements"
			)
	public static List<UiElement> uiGetElements(
			@FmParam(name = "locator", description = "Locator of the ui element whose element needs to be fetched.") String locator, 
			@FmParam(name = "parent", description = "Optional. Ui-element or ui-locator or attr-name of parent ui-element.") Object parent,
			@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName
			) throws Exception
	{
		return UiAutomationUtils.findElements(driverName, parent, locator);
	}
	
	/**
	 * Fetches value of specified locator.
	 * @param locator locator whose value needs to be fetched
	 * @param parent parent under which locator should be searched
	 * @return locator value
	 */
	@FreeMarkerMethod(
			description = "Fetches value of specified locator. If element is text/textarea, its ui value will be fetched.",
			returnDescription = "Value of the ui element"
			)
	public static String uiValue(
			@FmParam(name = "locator", description = "Locator/Ui-element of the ui element whose element needs to be fetched.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Ui-element or ui-locator or attr-name of parent ui-element.") Object parent,
			@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName
			) throws Exception
	{
		PrefixEpression customUiLocator = UiAutomationUtils.getCustomUiLocator(locator.toString());
		
		if(customUiLocator != null)
		{
			Object res = customUiLocator.getValue();
			return (res == null) ? null : res.toString();
		}

		UiElement element = getElementByLocator(driverName, locator, parent);

		if(element == null)
		{
			return null;
		}
		
		FormFieldType fieldType = UiAutomationUtils.getFormFieldType(element);
		
		if(fieldType != null)
		{
			return fieldType.getFieldAccessor().getValue(AutomationContext.getInstance(), element);
		}
		
		return element.getText().trim();
	}
	
	@FreeMarkerMethod(
			description = "Fetches inner-html of specified locator.",
			returnDescription = "Value of the ui element"
			)
	public static String uiInnerHtml(
			@FmParam(name = "locator", description = "Locator/Ui-element of the ui element whose element needs to be fetched.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Ui-element or ui-locator or attr-name of parent ui-element.") Object parent,
			@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName
			) throws Exception
	{
		PrefixEpression customUiLocator = UiAutomationUtils.getCustomUiLocator(locator.toString());
		
		if(customUiLocator != null)
		{
			Object res = customUiLocator.getValue();
			return (res == null) ? null : res.toString();
		}

		UiElement element = getElementByLocator(driverName, locator, parent);

		if(element == null)
		{
			return null;
		}
		
		return element.getInnerHtml();
	}

	/**
	 * Fetches display value of specified locator.
	 * @param locator locator whose value needs to be fetched
	 * @param parent parent under which locator should be searched
	 * @return locator value
	 */
	@FreeMarkerMethod(
			description = "Fetches display value of specified locator. For select, option label will be fetched. If element is not Select, its ui value will be fetched.",
			returnDescription = "Value of the ui element"
			)
	public static String uiDisplayValue(
			@FmParam(name = "locator", description = "Locator/Ui-element of the ui element whose display value needs to be fetched.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Ui-element or ui-locator or attr-name of parent ui-element.") Object parent,
			@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName
			) throws Exception
	{
		PrefixEpression customUiLocator = UiAutomationUtils.getCustomUiLocator(locator.toString());
		
		if(customUiLocator != null)
		{
			Object res = customUiLocator.getValue();
			return (res == null) ? null : res.toString();
		}

		UiElement element = getElementByLocator(driverName, locator, parent);

		if(element == null)
		{
			return null;
		}
		
		FormFieldType fieldType = UiAutomationUtils.getFormFieldType(element);
		
		if(fieldType != null)
		{
			return fieldType.getFieldAccessor().getDisplayValue(AutomationContext.getInstance(), element);
		}
		
		return element.getText().trim();
	}

	/**
	 * Fetches the specified attribute value of specified element.
	 * @param attrName name of attribute to fetch
	 * @param locator element locator or ui-element whose attribute needs to be fetched
	 * @param parent Parent name under which locator needs to be fetched
	 * @return attribute value, if any. Otherwise null
	 */
	@FreeMarkerMethod(
			description = "Fetches attribute value of specified locator.",
			returnDescription = "Value of the ui element attribute"
			)
	public static String uiElemAttr(
			@FmParam(name = "attrName", description = "Name of the attribute whose value to be fetched.") String attrName, 
			@FmParam(name = "locator", description = "Locator/Ui-element of the ui element whose attribute value needs to be fetched.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Ui-element or ui-locator or attr-name of parent ui-element.") Object parent,
			@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName
			)
	{
		UiElement uiElement = getElementByLocator(driverName, locator, parent);
		
		if(uiElement == null)
		{
			return null;
		}
		
		return uiElement.getAttribute(attrName);
	}

	/**
	 * Checks if specified element is visible or not.
	 * @param locator locator to check.
	 * @param parent parent under which locator should be searched
	 * @return true if available and visible
	 */
	@FreeMarkerMethod(
			description = "Checks if specified element is visible or not.",
			returnDescription = "True if element is visible"
			)
	public static boolean uiIsVisible(
			@FmParam(name = "locator", description = "Locator/Ui-element of the ui element whose visibility needs to be checked.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Ui-element or ui-locator or attr-name of parent ui-element.") Object parent,
			@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName
			)
	{
		try
		{
			UiElement element = getElementByLocator(driverName, locator, parent);
			return (element != null && element.isVisible());
		}catch(PlaywrightException ex)
		{
			//element got detached (similar to stale element) or became unavailable
			return false;
		}
	}

	/**
	 * Checks if specified element is present or not (need not be visible).
	 * @param locator locator to check.
	 * @param parent parent under which locator should be searched
	 * @return true if available (need not be visible)
	 */
	@FreeMarkerMethod(
			description = "Checks if specified element is present or not (need not be visible).",
			returnDescription = "True if element is available (need not be visible)"
			)
	public static boolean uiIsPresent(
			@FmParam(name = "locator", description = "Locator/Ui-element of the ui element whose presence needs to be checked.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Ui-element or ui-locator or attr-name of parent ui-element.") Object parent,
			@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName
			)
	{
		UiElement element = getElementByLocator(driverName, locator, parent);
		return (element != null);
	}

	@FreeMarkerMethod(
			description = "Removes special characters and coverts result into json string (enclosed in double quotes)",
			returnDescription = "Converted string"
			)
	public static String escape(
			@FmParam(name = "str", description = "String to be converted") String str)
	{
		if(str == null)
		{
			return "null";
		}
		
		//remove special characters if any
		char chArr[] = str.toCharArray();
		
		for(int i = 0; i < chArr.length; i++)
		{
			if(Character.isWhitespace(chArr[i]))
			{
				chArr[i] = ' ';
				continue;
			}
			
			if(chArr[i] < 33 || chArr[i] > 126)
			{
				chArr[i] = ' ';
			}
		}
		
		str = new String(chArr);
		str = str.replaceAll("\\ +", " ");
		
		try
		{
			return objectMapper.writeValueAsString(str);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting intput string into json string: {}", str);
		}
	}

	@FreeMarkerMethod(
			description = "Fetches the size of the browser",
			returnDescription = "Size of the browser"
			)
	public static Dimension uiBrowserSize(@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName)
	{
		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
		return session.getBrowserSize(driverName);
	}

	@FreeMarkerMethod(
			description = "Fetches the position of the browser",
			returnDescription = "Position of the browser"
			)
	public static Point uiBrowserPosition(@FmParam(name = "driverName", description = "Optional. Name of ui driver to use.") String driverName)
	{
		PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
		return session.getBrowserPosition(driverName);
	}
}
