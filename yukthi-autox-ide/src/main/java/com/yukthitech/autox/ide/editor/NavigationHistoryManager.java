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
package com.yukthitech.autox.ide.editor;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.common.IndexRange;
import com.yukthitech.autox.ide.actions.FileActions;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.Project;

@ActionHolder
public class NavigationHistoryManager
{
	private static final int MAX_LIMIT = 50;
	
	private static class NavEntry
	{
		private Project project;
		
		private File file;
		
		private IndexRange index;
		
		private NavEntry previous;
		
		private NavEntry next;
		
		private int position = 0;

		public NavEntry(Project project, File file, IndexRange index)
		{
			this.project = project;
			this.file = file;
			this.index = index;
		}
	}
	
	@Autowired
	private FileActions fileActions;
	
	private NavEntry current;
	
	private NavEntry head;
	
	public void push(Project project, File file, IndexRange index)
	{
		NavEntry newEntry = new NavEntry(project, file, index);
		
		if(current == null)
		{
			current = newEntry;
			head = newEntry;
			return;
		}
		
		current.next = newEntry;
		newEntry.previous = current;
		newEntry.position = current.position + 1;
		
		current = newEntry;
		
		//cleanup history if max limit is reached
		int diff = current.position - head.position;
		
		if(diff >= MAX_LIMIT)
		{
			head = head.next;
			head.previous = null;
		}
	}
	
	@Action
	public void goBack()
	{
		if(current == null)
		{
			return;
		}
		
		NavEntry prev = current.previous;

		if(prev == null)
		{
			return;
		}
		
		current = prev;
		fileActions.gotoFilePath(current.project, current.file.getPath(), -1, current.index);
	}
	
	@Action
	public void goForward()
	{
		if(current == null)
		{
			return;
		}
		
		NavEntry fwd = current.next;

		if(fwd == null)
		{
			return;
		}
		
		current = fwd;
		fileActions.gotoFilePath(current.project, current.file.getPath(), -1, current.index);
	}
}
