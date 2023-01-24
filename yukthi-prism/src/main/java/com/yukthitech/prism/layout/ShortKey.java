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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Short key for menu action.
 * @author akiran
 */
public class ShortKey implements Validateable
{
	/**
	 * true if control should be down.
	 */
	public boolean ctrl;
	
	/**
	 * true if alt should be down.
	 */
	private boolean alt;
	
	/**
	 * true if shift should be down.
	 */
	private boolean shift;
	
	/**
	 * Key to be used.
	 */
	private String key;
	
	/**
	 * Flag indicating the keystroke has to be registered as global.
	 */
	private boolean global;

	/**
	 * Gets the true if control should be down.
	 *
	 * @return the true if control should be down
	 */
	public boolean isCtrl()
	{
		return ctrl;
	}

	/**
	 * Sets the true if control should be down.
	 *
	 * @param ctrl the new true if control should be down
	 */
	public void setCtrl(boolean ctrl)
	{
		this.ctrl = ctrl;
	}

	/**
	 * Gets the true if alt should be down.
	 *
	 * @return the true if alt should be down
	 */
	public boolean isAlt()
	{
		return alt;
	}

	/**
	 * Sets the true if alt should be down.
	 *
	 * @param alt the new true if alt should be down
	 */
	public void setAlt(boolean alt)
	{
		this.alt = alt;
	}

	/**
	 * Gets the true if shift should be down.
	 *
	 * @return the true if shift should be down
	 */
	public boolean isShift()
	{
		return shift;
	}

	/**
	 * Sets the true if shift should be down.
	 *
	 * @param shift the new true if shift should be down
	 */
	public void setShift(boolean shift)
	{
		this.shift = shift;
	}

	/**
	 * Gets the key to be used.
	 *
	 * @return the key to be used
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Sets the key to be used.
	 *
	 * @param key the new key to be used
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	public boolean isGlobal()
	{
		return global;
	}

	public void setGlobal(boolean global)
	{
		this.global = global;
	}

	public int getKeyCode()
	{
		String fieldName = "VK_" + key.toUpperCase();
		
		try
		{
			return KeyEvent.class.getField(fieldName).getInt(null);
		}catch(NoSuchFieldException | IllegalAccessException ex)
		{
			throw new InvalidArgumentException("Invalid key code specified: " + key);
		}
	}
	
	public int getModifiers()
	{
		int modifiers = 0;
		
		if(ctrl)
		{
			modifiers |= InputEvent.CTRL_DOWN_MASK;
		}
		
		if(alt)
		{
			modifiers |= InputEvent.ALT_DOWN_MASK;
		}
		
		if(shift)
		{
			modifiers |= InputEvent.SHIFT_DOWN_MASK;
		}

		return modifiers;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		getKeyCode();
	}
	
	/**
	 * Converts current object to stroke.
	 * @return
	 */
	public KeyStroke toKeyStroke()
	{
		KeyStroke stroke = KeyStroke.getKeyStroke(getKeyCode(), getModifiers());
		return stroke;
	}
}
