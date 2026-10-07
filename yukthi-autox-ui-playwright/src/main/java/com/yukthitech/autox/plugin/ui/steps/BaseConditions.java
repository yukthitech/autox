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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;
import com.yukthitech.autox.plugin.ui.common.UiAutomationUtils;
import com.yukthitech.autox.plugin.ui.common.UiElement;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Base or wrapper object to hold ui conditions. Conditions are evaluated using playwright waits 
 * (like page.waitForURL) where possible, other conditions are polled.
 * 
 * @author akiran
 */
public abstract class BaseConditions extends AbstractParentUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Check to be performed for a condition.
	 */
	public static abstract class ConditionCheck
	{
		/**
		 * Description of the check, to be used in error messages.
		 */
		private String description;
		
		public ConditionCheck(String description)
		{
			this.description = description;
		}
		
		/**
		 * Checks if the condition is satisfied currently.
		 * @param session plugin session
		 * @param driverName driver name
		 * @param parentElement parent element to be used while searching for elements
		 * @return true if condition is satisfied
		 */
		protected abstract boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement);
		
		/**
		 * Waits for the condition to be satisfied. Default implementation polls the condition.
		 * 
		 * @param session plugin session
		 * @param driverName driver name
		 * @param parentElement parent element to be used while searching for elements
		 * @param timeoutMillis max time to wait
		 * @param gapMillis gap between polls
		 */
		protected void await(PlaywrightPluginSession session, String driverName, Object parentElement, long timeoutMillis, long gapMillis)
		{
			long endTime = System.currentTimeMillis() + timeoutMillis;
			Exception lastError = null;
			
			while(true)
			{
				try
				{
					if(isSatisfied(session, driverName, parentElement))
					{
						return;
					}
					
					lastError = null;
				}catch(PlaywrightException ex)
				{
					//element not yet available, etc. 
					lastError = ex;
				}
				
				if(System.currentTimeMillis() >= endTime)
				{
					break;
				}
				
				try
				{
					//wait using page, so that playwright events (dialogs) can be processed during wait
					session.getPage(driverName).waitForTimeout(gapMillis);
				}catch(PlaywrightException ex)
				{
					UiAutomationUtils.waitFor(gapMillis);
				}
			}
			
			throw new InvalidStateException("Timed out waiting for condition - {}{}", description, 
					lastError == null ? "" : (". Last error: " + lastError.getMessage()));
		}
		
		@Override
		public String toString()
		{
			return description;
		}
	}
	
	public static class BaseCondition
	{
		ConditionCheck condition;

		@Param(description = "Time out for this condition in seconds.")
		int timeOutInSec = 60;

		@Param(description = "Time gap between condition checks.")
		int timeGapMillis = 100;
		
		@Override
		public String toString()
		{
			return String.valueOf(condition);
		}
	}

	public static class ValueCondition extends BaseCondition
	{
		@Param(description = "Value to be used.", sourceType = SourceType.EXPRESSION)
		Object value;
	}

	public static class LocatorCondition extends BaseCondition
	{
		@Param(description = "Locator of the element on which condition to be checked.")
		String locator;
	}

	public static class LocatorValueCondition extends LocatorCondition
	{
		@Param(description = "Value to be checked.", sourceType = SourceType.EXPRESSION)
		Object value;
	}

	public static class AttributeCondition extends LocatorCondition
	{
		@Param(description = "Name of the attribute", sourceType = SourceType.EXPRESSION)
		Object name;
		
		@Param(description = "Value of the attribute", sourceType = SourceType.EXPRESSION)
		Object value;
	}

	protected List<BaseCondition> conditions = new ArrayList<BaseCondition>();
	
	/**
	 * Check which waits for page url, using playwright url wait.
	 */
	private static class UrlCheck extends ConditionCheck
	{
		private Predicate<String> urlPredicate;
		
		UrlCheck(String description, Predicate<String> urlPredicate)
		{
			super(description);
			this.urlPredicate = urlPredicate;
		}

		@Override
		protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
		{
			return urlPredicate.test(session.getPage(driverName).url());
		}
		
		@Override
		protected void await(PlaywrightPluginSession session, String driverName, Object parentElement, long timeoutMillis, long gapMillis)
		{
			try
			{
				session.getPage(driverName).waitForURL(urlPredicate, new Page.WaitForURLOptions().setTimeout(timeoutMillis));
			}catch(PlaywrightException ex)
			{
				throw new InvalidStateException("Timed out waiting for condition - {}. Error: {}", toString(), ex.getMessage());
			}
		}
	}
	
	/**
	 * Check which operates on the elements found by specified locator.
	 */
	private static abstract class ElementsCheck extends ConditionCheck
	{
		protected String locator;
		
		ElementsCheck(String description, String locator)
		{
			super(description);
			this.locator = locator;
		}
		
		protected List<UiElement> findElements(String driverName, Object parentElement)
		{
			return UiAutomationUtils.findElements(driverName, parentElement, locator);
		}
		
		protected UiElement findFirst(String driverName, Object parentElement)
		{
			List<UiElement> elements = findElements(driverName, parentElement);
			return (elements == null || elements.isEmpty()) ? null : elements.get(0);
		}
	}

	/**
	 * An expectation for checking the title of a page.
	 * 
	 * @param wrapper
	 */
	@ChildElement(description = "Condition to check page title")
	public void addTitleIs(ValueCondition wrapper)
	{
		String expected = String.valueOf(wrapper.value);
		
		wrapper.condition = new ConditionCheck("Title is '" + expected + "'")
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				return expected.equals(session.getPage(driverName).title());
			}
		};
		
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking that the title contains a case-sensitive
	 * substring
	 * 
	 * @param wrapper
	 */
	public void addTitleContains(ValueCondition wrapper)
	{
		String expected = String.valueOf(wrapper.value);
		
		wrapper.condition = new ConditionCheck("Title contains '" + expected + "'")
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				String title = session.getPage(driverName).title();
				return title != null && title.contains(expected);
			}
		};
		
		conditions.add(wrapper);
	}

	/**
	 * An expectation for the URL of the current page to be specific value.
	 * 
	 * @param wrapper
	 */
	public void addUrlToBe(ValueCondition wrapper)
	{
		String expected = String.valueOf(wrapper.value);
		wrapper.condition = new UrlCheck("Url is '" + expected + "'", url -> expected.equals(url));
		conditions.add(wrapper);
	}

	/**
	 * Expectation for the URL to match a specific regular expression
	 * 
	 * @param wrapper
	 */
	public void addUrlMatchesRegex(ValueCondition wrapper)
	{
		Pattern pattern = Pattern.compile(String.valueOf(wrapper.value));
		wrapper.condition = new UrlCheck("Url matches regex '" + pattern.pattern() + "'", url -> url != null && pattern.matcher(url).find());
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking that an element, known to be present on the
	 * DOM of a page, is visible. Visibility means that the element is not only
	 * displayed but also has a height and width that is greater than 0.
	 * 
	 * @param wrapper
	 */
	public void addIsVisible(LocatorCondition wrapper)
	{
		wrapper.condition = new ElementsCheck("Visible: " + wrapper.locator, wrapper.locator)
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				List<UiElement> elements = findElements(driverName, parentElement);
				
				if(elements == null || elements.isEmpty())
				{
					return false;
				}
				
				for(UiElement element : elements)
				{
					if(!element.isVisible())
					{
						return false;
					}
				}
				
				return true;
			}
		};
		
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking that an element is either invisible or not present on the DOM.
	 * @param wrapper
	 */
	public void addIsInvisible(LocatorCondition wrapper)
	{
		wrapper.condition = new ElementsCheck("Invisible: " + wrapper.locator, wrapper.locator)
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				List<UiElement> elements = findElements(driverName, parentElement);
				
				if(elements == null)
				{
					return true;
				}
				
				for(UiElement element : elements)
				{
					if(element.isVisible())
					{
						return false;
					}
				}
				
				return true;
			}
		};
		
		conditions.add(wrapper);
	}

	public void addIsClickable(LocatorCondition wrapper)
	{
		wrapper.condition = new ElementsCheck("Clickable: " + wrapper.locator, wrapper.locator)
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				UiElement element = findFirst(driverName, parentElement);
				return element != null && element.isVisible() && element.isEnabled();
			}
		};
		
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking if the given element is selected.
	 * @param wrapper
	 */
	public void addIsSelected(LocatorCondition wrapper)
	{
		wrapper.condition = new ElementsCheck("Selected: " + wrapper.locator, wrapper.locator)
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				UiElement element = findFirst(driverName, parentElement);
				return element != null && element.isSelected();
			}
		};
		
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking if the given element is not selected.
	 * @param wrapper
	 */
	public void addIsNotSelected(LocatorCondition wrapper)
	{
		wrapper.condition = new ElementsCheck("Not selected: " + wrapper.locator, wrapper.locator)
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				UiElement element = findFirst(driverName, parentElement);
				return element != null && !element.isSelected();
			}
		};
		
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking if the given text is present in the specified
	 * element.
	 * 
	 * @param wrapper
	 */
	public void addTextPresentIn(LocatorValueCondition wrapper)
	{
		String expected = String.valueOf(wrapper.value);
		
		wrapper.condition = new ElementsCheck("Text '" + expected + "' present in: " + wrapper.locator, wrapper.locator)
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				UiElement element = findFirst(driverName, parentElement);
				return element != null && element.getText().contains(expected);
			}
		};
		
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking if the given text is present in the specified
	 * elements value attribute.
	 * 
	 * @param wrapper
	 */
	public void addTextPresentInElementValue(LocatorValueCondition wrapper)
	{
		String expected = String.valueOf(wrapper.value);
		
		wrapper.condition = new ElementsCheck("Text '" + expected + "' present in value of: " + wrapper.locator, wrapper.locator)
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				UiElement element = findFirst(driverName, parentElement);
				
				if(element == null)
				{
					return false;
				}
				
				String value = element.getAttribute("value");
				return value != null && value.contains(expected);
			}
		};
		
		conditions.add(wrapper);
	}
	
	public void addAttributeValueIs(AttributeCondition wrapper)
	{
		String attrName = String.valueOf(wrapper.name);
		String expected = String.valueOf(wrapper.value);
		
		wrapper.condition = new ElementsCheck("Attribute '" + attrName + "' of " + wrapper.locator + " is '" + expected + "'", wrapper.locator)
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				UiElement element = findFirst(driverName, parentElement);
				return element != null && expected.equals(element.getAttribute(attrName));
			}
		};
		
		conditions.add(wrapper);
	}

	public void addAlertIsPresent(BaseCondition wrapper)
	{
		wrapper.condition = new ConditionCheck("Alert/dialog is present")
		{
			@Override
			protected boolean isSatisfied(PlaywrightPluginSession session, String driverName, Object parentElement)
			{
				return session.hasQueuedDialog(driverName);
			}
		};
		
		conditions.add(wrapper);
	}
	
	/**
	 * Fetches plugin session, to be used by sub classes.
	 */
	protected PlaywrightPluginSession getSession()
	{
		return ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
	}
}
