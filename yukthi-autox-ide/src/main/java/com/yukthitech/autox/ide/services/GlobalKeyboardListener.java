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

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.layout.ShortKey;

/**
 * Used to handle global key board events which will not be possible
 * with normal short keys.
 * @author akranthikiran
 */
@Service
public class GlobalKeyboardListener
{
	private class KeyStroke
	{
		private int modifiers;
		
		private int keyCode;
		
		private ActionListener actionListener;

		public KeyStroke(int modifiers, int keyCode, ActionListener actionListener)
		{
			this.modifiers = modifiers;
			this.keyCode = keyCode;
			this.actionListener = actionListener;
		}
	}
	
	private Map<Integer, List<KeyStroke>> keyToStrokes = new HashMap<>();
	
	@PostConstruct
	private void init()
	{
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this::handleKeyEvent);
	}
	
	public void addGlobalKeyListener(ShortKey shortKey, ActionListener listener)
	{
		KeyStroke keyStroke = new KeyStroke(shortKey.getModifiers(), shortKey.getKeyCode(), listener);
		List<KeyStroke> strokes = keyToStrokes.get(keyStroke.keyCode);
		
		if(strokes == null)
		{
			strokes = new ArrayList<>();
			keyToStrokes.put(keyStroke.keyCode, strokes);
		}
		
		strokes.add(keyStroke);
	}
	
	private boolean handleKeyEvent(KeyEvent e)
	{
		if(e.getID() != KeyEvent.KEY_RELEASED)
		{
			return false;
		}
		
		int keyCode = e.getKeyCode();
		List<KeyStroke> strokes = keyToStrokes.get(keyCode);
		
		if(CollectionUtils.isEmpty(strokes))
		{
			return false;
		}
		
		int modifiers = 0;
		
		if(e.isControlDown())
		{
			modifiers |= InputEvent.CTRL_DOWN_MASK;
		}
		
		if(e.isAltDown())
		{
			modifiers |= InputEvent.ALT_DOWN_MASK;
		}
		
		if(e.isShiftDown())
		{
			modifiers |= InputEvent.SHIFT_DOWN_MASK;
		}
		
		for(KeyStroke stroke : strokes)
		{
			if(stroke.modifiers == modifiers)
			{
				stroke.actionListener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), null));
				return false;
			}
		}

		return false;
	}
}
