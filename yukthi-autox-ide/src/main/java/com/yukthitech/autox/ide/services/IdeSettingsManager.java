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
package com.yukthitech.autox.ide.services;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JToggleButton;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.dialog.FontDialog;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.layout.UiIdElementsManager;
import com.yukthitech.autox.ide.model.IdeSettings;
import com.yukthitech.autox.ide.model.IdeState;

@ActionHolder
public class IdeSettingsManager
{
	public static final Pattern IDE_SETTINGS_ID = Pattern.compile("ideSettings_(\\w+)");
	
	public static final String ID_SETTINGS_PREFIX = "ideSettings_";
			
	@Autowired
	private IdeStateManager ideStateManager;
	
	@Autowired
	private IdeEventManager ideEventManager;
	
	@Autowired
	private FontDialog fontDialog;
	
	/**
	 * Map maintaining 
	 */
	private Map<String, Object> stateToVal = new HashMap<>();
	
	@IdeEventHandler
	public void onIdeStart(IdeOpeningEvent event) throws Exception
	{
		IdeState ideState = ideStateManager.getState();
		IdeSettings ideSettings = ideState.getIdeSettings();

		//fetch the states required by ui layout
		for(String uiId : UiIdElementsManager.getUiIds())
		{
			Matcher matcher = IDE_SETTINGS_ID.matcher(uiId);
			
			if(!matcher.matches())
			{
				continue;
			}
			
			String settingsName = matcher.group(1);
			Object value = PropertyUtils.getProperty(ideSettings, settingsName);
			
			stateToVal.put(settingsName, value);
			
			if(value instanceof Boolean)
			{
				changeUiStatus(settingsName, (Boolean) value);
			}
		}
	}
	
	@IdeEventHandler
	public void onSettingsChanged(IdeSettingChangedEvent event) throws Exception
	{
		IdeSettings settings = event.getIdeSettings();
		
		for(String state : stateToVal.keySet())
		{
			Object oldVal = stateToVal.get(state);
			Object newVal = PropertyUtils.getProperty(settings, state);
			
			if(Objects.equals(oldVal, newVal))
			{
				continue;
			}
			
			if(newVal instanceof Boolean)
			{
				changeUiStatus(state, (Boolean) newVal);
			}
		}
	}
	
	private void changeUiStatus(String name, boolean value)
	{
		List<Object> elements = UiIdElementsManager.getElements(ID_SETTINGS_PREFIX + name);
		
		for(Object uiElem : elements)
		{
			if(uiElem instanceof JToggleButton)
			{
				JToggleButton button = (JToggleButton) uiElem;
				
				if(button.isSelected() == value)
				{
					continue;
				}
				
				((JToggleButton) uiElem).setSelected(value);
			}
		}
	}
	
	@Action
	public void changeEditorFont()
	{
		IdeState ideState = ideStateManager.getState();
		IdeSettings ideSettings = ideState.getIdeSettings();
		
		Font newFont = fontDialog.display(ideSettings.getEditorFont());
		
		if(newFont == null)
		{
			return;
		}

		ideSettings.setEditorFont(newFont);
		ideEventManager.processEvent(new IdeSettingChangedEvent(ideSettings));
		
		ideStateManager.saveState(ideState);
	}
	
	@Action
	public void toogleWordWrap()
	{
		IdeState ideState = ideStateManager.getState();
		IdeSettings ideSettings = ideState.getIdeSettings();

		boolean curState = ideSettings.isEnableTextWrapping();
		boolean newState = !curState;
		
		ideSettings.setEnableTextWrapping(newState);
		ideEventManager.processEvent(new IdeSettingChangedEvent(ideSettings));
	}
}
