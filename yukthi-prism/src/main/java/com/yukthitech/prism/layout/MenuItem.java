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
package com.yukthitech.prism.layout;

import javax.swing.JMenuItem;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents menu item.
 */
public class MenuItem implements Validateable
{
	/**
	 * Id of this item.
	 */
	private String id;
	
	/**
	 * Label to be used.
	 */
	private String label;
	
	/**
	 * Mnemonic to be used.
	 */
	private char mnemonic;
	
	/**
	 * Short key to be used.
	 */
	private ShortKey shortKey;
	
	/**
	 * Icon to be used.
	 */
	private String icon;
	
	/**
	 * Action to be invoked.
	 */
	private String action;
	
	/**
	 * Group name of this item.
	 */
	private String group;
	
	/**
	 * Flag indicating default enable status of item.
	 */
	private boolean enabled = true;
	
	/**
	 * Gets the id of this item.
	 *
	 * @return the id of this item
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the id of this item.
	 *
	 * @param id the new id of this item
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Gets the label to be used.
	 *
	 * @return the label to be used
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label to be used.
	 *
	 * @param label the new label to be used
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Gets the mnemonic to be used.
	 *
	 * @return the mnemonic to be used
	 */
	public char getMnemonic()
	{
		return mnemonic;
	}

	/**
	 * Sets the mnemonic to be used.
	 *
	 * @param mnemonic the new mnemonic to be used
	 */
	public void setMnemonic(char mnemonic)
	{
		this.mnemonic = mnemonic;
	}

	/**
	 * Gets the short key to be used.
	 *
	 * @return the short key to be used
	 */
	public ShortKey getShortKey()
	{
		return shortKey;
	}

	/**
	 * Sets the short key to be used.
	 *
	 * @param shortKey the new short key to be used
	 */
	public void setShortKey(ShortKey shortKey)
	{
		this.shortKey = shortKey;
	}

	/**
	 * Gets the icon to be used.
	 *
	 * @return the icon to be used
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * Sets the icon to be used.
	 *
	 * @param icon the new icon to be used
	 */
	public void setIcon(String icon)
	{
		this.icon = icon;
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

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group = group;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(label))
		{
			throw new ValidateException("Label can not be blank.");
		}
	}
	
	/**
	 * Converts current menu item to jemenuitem.
	 *
	 * @param actionHandler object on which action method should be invoked.
	 * @return menu item
	 */
	public JMenuItem toJMenuItem(ActionCollection actionCollection, IdePopupMenu idePopup)
	{
		JMenuItem menuItem = new JMenuItem(label);
		
		if(mnemonic > 0)
		{
			menuItem.setMnemonic(mnemonic);
		}
		
		if(shortKey != null)
		{
			menuItem.setAccelerator(shortKey.toKeyStroke());
			
			if(shortKey.isGlobal() && action != null)
			{
				actionCollection.registerGlobalAction(shortKey, action);
			}
		}
		
		if(action != null)
		{
			menuItem.addActionListener(actionCollection.getActionListener(action, idePopup));
		}
		
		if(icon != null)
		{
			menuItem.setIcon(IdeUtils.loadIcon(this.icon, 16));
		}
		
		if(id != null)
		{
			UiIdElementsManager.registerElement(id, null, menuItem);
		}

		if(group != null)
		{
			String groupNames[] = group.trim().split("\\s*\\,\\s*");
			
			for(String grp : groupNames)
			{
				UiIdElementsManager.registerElement(null, grp, menuItem);
			}
		}
		
		menuItem.setEnabled(enabled);
		return menuItem;
	}
}
