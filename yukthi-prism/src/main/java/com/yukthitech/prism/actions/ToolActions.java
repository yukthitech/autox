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
package com.yukthitech.prism.actions;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.dialog.RegexSandboxDialog;
import com.yukthitech.prism.dialog.XpathSandboxDialog;
import com.yukthitech.prism.layout.Action;
import com.yukthitech.prism.layout.ActionHolder;

@ActionHolder
public class ToolActions
{
	/**
	 * Used to display about autox info.
	 */
	@Autowired
	private XpathSandboxDialog xpathSandboxDialog;
	
	@Autowired
	private RegexSandboxDialog regexSandboxDialog;

	@Action
	public void displayXpathSandbox()
	{
		IdeUtils.maximize(xpathSandboxDialog, 50);
		IdeUtils.centerOnScreen(xpathSandboxDialog);
		xpathSandboxDialog.display();
	}

	@Action
	public void displayRegexSandbox()
	{
		IdeUtils.centerOnScreen(regexSandboxDialog);
		regexSandboxDialog.display();
	}
}
