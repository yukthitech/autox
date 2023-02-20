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
package com.yukthitech.prism;

import java.awt.Color;
import java.awt.Font;

import org.apache.commons.text.WordUtils;

import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public interface IIdeConstants
{
	Font DEFAULT_FONT = new Font("Consolas", Font.PLAIN, 13);
	
	public String ELEMENT_TYPE_STEP = "$";
	
	public FreeMarkerEngine FREE_MARKER_ENGINE = new FreeMarkerEngine();
	
	/**
	 * Delay used during typing in file editor to reparse the content.
	 */
	public int FILE_EDITOR_PARSE_DELAY = 1000;
	
	public Color DEBUG_BG_COLOR = new Color(198, 219, 174);
	
	public Color DEBUG_ERR_BG_COLOR = new Color(235, 106, 106);
	
	public int MSSG_WRAP_LENGTH = 105;
	
	public static String wrap(String text)
	{
		return WordUtils.wrap(text, MSSG_WRAP_LENGTH, "\n   ", false);
	}
}
