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
package com.yukthitech.autox.ide.actions;

import java.awt.Component;
import java.awt.Window;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.MaximizableTabbedPane;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;

@ActionHolder
public class WindowActions
{
	@Action
	public void flipMaximization()
	{
		Window window = IdeUtils.getCurrentWindow();
		Component focusOwner = window.getFocusOwner();
		
		if(focusOwner == null)
		{
			return;
		}
		
		Component curComp = focusOwner;
		
		while(curComp != null)
		{
			if(curComp instanceof MaximizableTabbedPane)
			{
				((MaximizableTabbedPane) curComp).flipMaximization();
				break;
			}
			
			curComp = curComp.getParent();
		}

		focusOwner.requestFocus();
	}
}
