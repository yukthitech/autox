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
package com.yukthitech.autox.ide.layout;

import javax.swing.JComponent;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.swing.IconButton;

/**
 * Toolbar icon.
 * @author akiran
 */
public class ToolBarIcon implements Validateable
{
	/**
	 * Used to access this icon directly.
	 */
	private String id;
	
	/**
	 * Icon to be displayed.
	 */
	protected String icon;
	
	/**
	 * Tooltip for the button.
	 */
	protected String tooltip;
	
	/**
	 * Action to be invoked.
	 */
	protected String action;
	
	/**
	 * Gets the used to access this icon directly.
	 *
	 * @return the used to access this icon directly
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the used to access this icon directly.
	 *
	 * @param id the new used to access this icon directly
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Gets the icon to be displayed.
	 *
	 * @return the icon to be displayed
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * Sets the icon to be displayed.
	 *
	 * @param icon the new icon to be displayed
	 */
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	/**
	 * Gets the tooltip for the button.
	 *
	 * @return the tooltip for the button
	 */
	public String getTooltip()
	{
		return tooltip;
	}

	/**
	 * Sets the tooltip for the button.
	 *
	 * @param tooltip the new tooltip for the button
	 */
	public void setTooltip(String tooltip)
	{
		this.tooltip = tooltip;
	}

	/**
	 * Gets the action to be invoked.
	 *
	 * @return the action to be invoked
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * Sets the action to be invoked.
	 *
	 * @param action the new action to be invoked
	 */
	public void setAction(String action)
	{
		this.action = action;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isEmpty(icon))
		{
			throw new ValidateException("Icon can not be empty.");
		}
	}
	
	/**
	 * To button.
	 *
	 * @param actionCollection the action collection
	 * @return the j component
	 */
	public JComponent toButton(ActionCollection actionCollection)
	{
		IconButton button = new IconButton();
		button.setIcon(IdeUtils.loadIconWithoutBorder(icon, 20));
		button.setToolTipText(tooltip);

		if(action != null)
		{
			button.addActionListener(actionCollection.getActionListener(action, null));
		}
		
		if(id != null)
		{
			UiIdElementsManager.registerElement(id, button);
		}
		
		return button;
	}
}
