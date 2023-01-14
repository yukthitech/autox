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
package com.yukthitech.autox.ide.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Function;

import javax.swing.JList;

public class SearchableList<E> extends JList<E>
{
	private static final long serialVersionUID = 1L;
	
	private static final long ONE_SEC = 1000;
	
	private class SearchListener extends KeyAdapter
	{
		@Override
		public void keyReleased(KeyEvent e)
		{
			long curTime = System.currentTimeMillis();
			
			long diff = (curTime - lastKeyTime);
			lastKeyTime = curTime;
			
			if(diff > ONE_SEC)
			{
				searchString.setLength(0);
			}
			
			searchString.append(e.getKeyChar());
			searchAndSelect();
		}
	}
	
	/**
	 * Current search string.
	 */
	private StringBuilder searchString = new StringBuilder();
	
	/**
	 * Time when last key was pressed.
	 */
	private long lastKeyTime = 0;
	
	/**
	 * Function to be used for filtering. Should accept search string as input
	 * and should return index to be selected.
	 */
	private Function<String, E> searchFunc;
	
	public SearchableList(Function<String, E> searchFunc)
	{
		this.searchFunc = searchFunc;
		super.addKeyListener(new SearchListener());
	}
	
	private void searchAndSelect()
	{
		String srchStr = searchString.toString();
		E selection = searchFunc.apply(srchStr);
		
		if(selection != null)
		{
			super.setSelectedValue(selection, true);
		}
	}

}
