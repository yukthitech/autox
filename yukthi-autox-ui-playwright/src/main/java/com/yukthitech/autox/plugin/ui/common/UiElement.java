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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.plugin.ui.PlaywrightPlugin;
import com.yukthitech.autox.plugin.ui.PlaywrightPluginSession;

/**
 * Wrapper over playwright {@link Locator}, which represents a single ui element. Instead of exposing
 * playwright types to automation scripts (free-marker expressions, context attributes), this wrapper is
 * returned by all the find operations.
 * <p>
 * Note: playwright locators are lazy, which means the underlying dom element is resolved on every operation.
 * Hence this wrapper never goes stale; but if the target element is removed from dom, operations on this
 * wrapper will fail (similar to stale-element behavior of selenium).
 * 
 * @author akiran
 */
public class UiElement
{
	private static Logger logger = LogManager.getLogger(UiElement.class);

	/**
	 * Timeout in millis used for actions like click, fill, etc. This is intentionally smaller than
	 * playwright default (30 sec), as the callers (steps) have their own retry mechanism.
	 */
	public static final double DEFAULT_ACTION_TIMEOUT_MS = 10000;

	/**
	 * Name of the driver to which this element belongs.
	 */
	private final String driverName;

	/**
	 * Underlying playwright locator, which points to single element.
	 */
	private final Locator locator;

	/**
	 * Optional parent element under which this element was found.
	 */
	private final UiElement parent;

	/**
	 * Playwright selector (or description) used to find this element.
	 */
	private final String selector;

	public UiElement(String driverName, Locator locator, UiElement parent, String selector)
	{
		this.driverName = driverName;
		this.locator = locator;
		this.parent = parent;
		this.selector = selector;
	}

	public String getDriverName()
	{
		return driverName;
	}

	/**
	 * Underlying playwright locator. Should be used only by playwright specific code.
	 * @return playwright locator
	 */
	public Locator getLocator()
	{
		return locator;
	}

	public UiElement getParent()
	{
		return parent;
	}

	public String getSelector()
	{
		return selector;
	}

	/**
	 * Checks if there is a modal dialog (confirm/prompt) which is waiting to be handled. Modal dialogs block
	 * playwright actions (like click) which triggered them, so actions are timed out in such cases and
	 * are considered as successful.
	 */
	private boolean isDialogPending()
	{
		try
		{
			PlaywrightPluginSession session = ExecutionContextManager.getInstance().getPluginSession(PlaywrightPlugin.class);
			return session != null && session.hasPendingDialog(driverName);
		}catch(Exception ex)
		{
			return false;
		}
	}

	private void performAction(String actionName, Runnable action)
	{
		try
		{
			action.run();
		}catch(TimeoutError ex)
		{
			if(isDialogPending())
			{
				logger.debug("Action '{}' on element {} timed out, but a dialog is waiting to be handled. Assuming action has triggered the dialog.", actionName, this);
				return;
			}

			throw ex;
		}
	}

	// ---------------- Mouse actions ----------------

	public void click()
	{
		performAction("click", () -> locator.click(new Locator.ClickOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS)));
	}

	public void dblClick()
	{
		performAction("dblclick", () -> locator.dblclick(new Locator.DblclickOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS)));
	}

	public void rightClick()
	{
		performAction("rightclick", () -> locator.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT).setTimeout(DEFAULT_ACTION_TIMEOUT_MS)));
	}

	/**
	 * Clicks the element using js (element.click()), instead of simulating mouse events.
	 */
	public void clickByJs()
	{
		performAction("js-click", () -> locator.evaluate("e => e.click()", null, new Locator.EvaluateOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS)));
	}

	public void hover()
	{
		performAction("hover", () -> locator.hover(new Locator.HoverOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS)));
	}

	public void dragTo(UiElement target)
	{
		performAction("dragTo", () -> locator.dragTo(target.locator, new Locator.DragToOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS)));
	}

	/**
	 * Scrolls the element into view only if needed.
	 */
	public void scrollIntoView()
	{
		locator.scrollIntoViewIfNeeded(new Locator.ScrollIntoViewIfNeededOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS));
	}

	/**
	 * Scrolls the element into view, by aligning to top or bottom of the view port.
	 * @param alignToTop if true, element will be aligned to top
	 */
	public void scrollIntoView(boolean alignToTop)
	{
		locator.evaluate("(e, top) => e.scrollIntoView(top)", alignToTop);
	}

	/**
	 * Fetches the center point of the element, relative to the main frame viewport.
	 * @return bounding box of the element or null if element is not visible
	 */
	public BoundingBox getBounds()
	{
		return locator.boundingBox();
	}

	// ---------------- Keyboard / value actions ----------------

	/**
	 * Fills the field with specified value (clears existing value and sets new value, input event will be fired).
	 */
	public void fill(String value)
	{
		locator.fill(value, new Locator.FillOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS));
	}

	public void clear()
	{
		locator.clear(new Locator.ClearOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS));
	}

	/**
	 * Types the specified text key by key (without clearing existing value).
	 */
	public void type(String text)
	{
		locator.pressSequentially(text, new Locator.PressSequentiallyOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS));
	}

	/**
	 * Presses the specified key (like Enter, Tab, Control+A) on this element.
	 */
	public void press(String key)
	{
		locator.press(key, new Locator.PressOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS));
	}

	public void setChecked(boolean checked)
	{
		locator.setChecked(checked, new Locator.SetCheckedOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS));
	}

	// ---------------- Select options ----------------

	public void selectByValue(String value)
	{
		locator.selectOption(new SelectOption().setValue(value), new Locator.SelectOptionOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS));
	}

	public void selectByLabel(String label)
	{
		locator.selectOption(new SelectOption().setLabel(label), new Locator.SelectOptionOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS));
	}

	public void selectByIndex(int index)
	{
		locator.selectOption(new SelectOption().setIndex(index), new Locator.SelectOptionOptions().setTimeout(DEFAULT_ACTION_TIMEOUT_MS));
	}

	/**
	 * Checks if this (select) element has an option with specified value.
	 */
	public boolean hasOptionWithValue(String value)
	{
		Object res = locator.evaluate("(e, v) => !!e.options && Array.from(e.options).some(o => o.value === v)", value);
		return Boolean.TRUE.equals(res);
	}

	/**
	 * Checks if this (select) element has an option with specified label.
	 */
	public boolean hasOptionWithLabel(String label)
	{
		Object res = locator.evaluate("(e, v) => !!e.options && Array.from(e.options).some(o => o.label === v || o.text.trim() === v)", label);
		return Boolean.TRUE.equals(res);
	}

	// ---------------- Read operations ----------------

	/**
	 * Fetches the attribute/property value of the element. Like selenium, property value (like value, checked, innerHTML, etc)
	 * is preferred over attribute value.
	 * @param name name of the attribute
	 * @return value of the attribute or null.
	 */
	public String getAttribute(String name)
	{
		Object res = locator.evaluate(
				"(e, n) => { var p = e[n]; if(p !== undefined && p !== null && typeof p !== 'object' && typeof p !== 'function') { return String(p); } return e.getAttribute(n); }", 
				name);
		return (res == null) ? null : String.valueOf(res);
	}

	/**
	 * Fetches the visible text of the element.
	 */
	public String getText()
	{
		String text = locator.innerText();
		return (text == null) ? "" : text;
	}

	public String getTextContent()
	{
		return locator.textContent();
	}

	public String getInnerHtml()
	{
		return locator.innerHTML();
	}

	/**
	 * Current value of input/textarea/select.
	 */
	public String getInputValue()
	{
		return locator.inputValue();
	}

	/**
	 * Fetches the lower case tag name of the element.
	 */
	public String getTagName()
	{
		Object tag = locator.evaluate("e => e.tagName");
		return (tag == null) ? null : String.valueOf(tag).toLowerCase();
	}

	/**
	 * Checks if the element is visible. Returns false if element is not available.
	 */
	public boolean isVisible()
	{
		return locator.isVisible();
	}

	public boolean isEnabled()
	{
		return locator.isEnabled();
	}

	/**
	 * Checks if the element (check-box, radio button, option) is checked/selected.
	 */
	public boolean isSelected()
	{
		Object res = locator.evaluate("e => !!(e.checked || e.selected)");
		return Boolean.TRUE.equals(res);
	}

	/**
	 * Checks if the element is present in dom (need not be visible).
	 */
	public boolean isPresent()
	{
		return locator.count() > 0;
	}

	/**
	 * Evaluates the specified js function/expression on this element. The first argument of the function will be the
	 * dom element, second (optional) argument will be specified arg. Example: "(e, v) => e.value = v".
	 * 
	 * @param script js function or expression
	 * @param arg optional argument
	 * @return result of the evaluation
	 */
	public Object evaluate(String script, Object arg)
	{
		if(arg == null)
		{
			return locator.evaluate(script);
		}

		return locator.evaluate(script, arg);
	}

	public Object evaluate(String script)
	{
		return locator.evaluate(script);
	}

	/**
	 * Waits for this element to reach specified state.
	 * @param state state to wait for
	 * @param timeoutMillis max time to wait
	 */
	public void waitFor(WaitForSelectorState state, double timeoutMillis)
	{
		locator.waitFor(new Locator.WaitForOptions().setState(state).setTimeout(timeoutMillis));
	}

	// ---------------- Child / sibling operations ----------------

	/**
	 * Number of elements matched by the underlying locator.
	 */
	public int count()
	{
		return locator.count();
	}

	/**
	 * Fetches the nth (zero based) element matching underlying locator.
	 */
	public UiElement nth(int index)
	{
		return new UiElement(driverName, locator.nth(index), parent, selector + "[" + index + "]");
	}

	public UiElement first()
	{
		return nth(0);
	}

	/**
	 * Finds all the descendant elements matching the specified playwright selector.
	 * @param playwrightSelector playwright selector (like css=..., xpath=...)
	 * @return matching elements, which can be empty
	 */
	public List<UiElement> findAll(String playwrightSelector)
	{
		Locator childLocator = locator.locator(playwrightSelector);
		int count = childLocator.count();
		List<UiElement> res = new ArrayList<>(count);

		for(int i = 0; i < count; i++)
		{
			res.add(new UiElement(driverName, childLocator.nth(i), this, playwrightSelector + "[" + i + "]"));
		}

		return res;
	}

	/**
	 * Fetches the dom parent of this element.
	 */
	public UiElement domParent()
	{
		return new UiElement(driverName, locator.locator("xpath=.."), null, "xpath=..");
	}

	/**
	 * Fetches short description of the element, to be used in logs.
	 */
	public String describe()
	{
		StringBuilder builder = new StringBuilder("[");
		builder.append("Selector: ").append(selector);

		try
		{
			builder.append(", Tag: ").append(getTagName());

			BoundingBox bounds = locator.boundingBox();

			if(bounds == null)
			{
				builder.append(", Bounds: null");
			}
			else
			{
				builder.append(String.format(", Bounds: (x: %s, y: %s, width: %s, height: %s)", bounds.x, bounds.y, bounds.width, bounds.height));
			}

			builder.append(", Visible: ").append(locator.isVisible());
			builder.append(", Enabled: ").append(locator.isEnabled());
		}catch(PlaywrightException ex)
		{
			builder.append(", Details-Unavailable: ").append(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage().split("\n")[0]);
		}

		builder.append("]");
		return builder.toString();
	}

	@Override
	public String toString()
	{
		if(parent != null)
		{
			return String.format("UiElement[Selector: %s, Parent: %s]", selector, parent);
		}

		return String.format("UiElement[Selector: %s]", selector);
	}
}
