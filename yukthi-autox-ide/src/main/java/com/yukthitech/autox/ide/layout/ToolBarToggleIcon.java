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
import javax.swing.JToggleButton;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.swing.ToggleIconButton;

/**
 * Toolbar icon.
 * @author akiran
 */
public class ToolBarToggleIcon extends ToolBarIcon
{
	/**
	 * To button.
	 *
	 * @param actionCollection the action collection
	 * @return the j component
	 */
	public JComponent toButton(ActionCollection actionCollection)
	{
		JToggleButton button = new ToggleIconButton();
		button.setIcon(IdeUtils.loadIconWithoutBorder(icon, 20));
		button.setToolTipText(tooltip);

		if(action != null)
		{
			button.addActionListener(actionCollection.getActionListener(action, null));
		}
		
		String id = super.getId();
		
		if(id != null)
		{
			UiIdElementsManager.registerElement(id, getGroup(), button);
		}
		
		return button;
	}
}
