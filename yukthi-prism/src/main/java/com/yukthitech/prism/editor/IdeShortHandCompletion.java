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
package com.yukthitech.prism.editor;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;

import com.yukthitech.prism.IdeUtils;

/**
 * Extension of short hand completion along with cursor position in inserted text.
 * @author akiran
 */
public class IdeShortHandCompletion extends ShorthandCompletion
{
	private static String CUR_POS_TEXT = "###CUR###";
	
	/**
	 * Left side movement of the cursor required from the end of inserted text.
	 */
	private int cursorLeftMovement = -1;
	
	public IdeShortHandCompletion(CompletionProvider provider, String inputText, String replacementText, String shortDesc, String summary)
	{
		super(provider, inputText, getFinalText(replacementText), shortDesc, summary);
		
		int pos = replacementText.indexOf(CUR_POS_TEXT);
		
		if(pos < 0)
		{
			return;
		}
		
		this.cursorLeftMovement = replacementText.length() - pos - CUR_POS_TEXT.length();
	}
	
	/**
	 * Gets the left side movement of the cursor required from the end of inserted text.
	 *
	 * @return the left side movement of the cursor required from the end of inserted text
	 */
	public int getCursorLeftMovement()
	{
		return cursorLeftMovement;
	}
	
	private static String getFinalText(String actualText)
	{
		actualText = IdeUtils.removeCarriageReturns(actualText);
		return actualText.replace(CUR_POS_TEXT, "");
	}
}
