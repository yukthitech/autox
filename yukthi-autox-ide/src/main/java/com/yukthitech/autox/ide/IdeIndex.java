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
package com.yukthitech.autox.ide;

import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.model.Project;

@Service
public class IdeIndex
{
	private TreeSet<FileDetails> files = new TreeSet<>();
	
	public synchronized void cleanFileIndex()
	{
		files.clear();
	}
	
	public synchronized void addFile(FileDetails file)
	{
		files.add(file);
	}
	
	public synchronized TreeSet<FileDetails> getFiles()
	{
		return files;
	}
	
	public synchronized void removeProjectFiles(Project project)
	{
		TreeSet<FileDetails> updatedIndex = new TreeSet<>();
		
		files.forEach(fileDet -> 
		{
			if(project.equals(fileDet.getProject()))
			{
				return;
			}
			
			updatedIndex.add(fileDet);
		});
		
		this.files = updatedIndex;
	}
}
