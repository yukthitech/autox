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
package com.yukthitech.prism.model;

import java.awt.Font;
import java.io.Serializable;

/**
 * Setting of ide.
 * @author akiran
 */
public class IdeSettings implements Serializable
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Font to be used for editors.
	 */
	private Font editorFont;
	
	/**
	 * Flag indicating if text wrapping should be enabled.
	 */
	private boolean enableTextWrapping;

	/**
	 * Gets the font to be used for editors.
	 *
	 * @return the font to be used for editors
	 */
	public Font getEditorFont()
	{
		return editorFont;
	}

	/**
	 * Sets the font to be used for editors.
	 *
	 * @param editorFont the new font to be used for editors
	 */
	public void setEditorFont(Font editorFont)
	{
		this.editorFont = editorFont;
	}

	/**
	 * Gets the flag indicating if text wrapping should be enabled.
	 *
	 * @return the flag indicating if text wrapping should be enabled
	 */
	public boolean isEnableTextWrapping()
	{
		return enableTextWrapping;
	}

	/**
	 * Sets the flag indicating if text wrapping should be enabled.
	 *
	 * @param enableTextWrapping the new flag indicating if text wrapping should be enabled
	 */
	public void setEnableTextWrapping(boolean enableTextWrapping)
	{
		this.enableTextWrapping = enableTextWrapping;
	}
}
