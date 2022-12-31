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

import java.io.File;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class IdeFileUtils
{
	public static String getCanonicalPath(File file)
	{
		try
		{
			return file.getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching canonical path: {}", file.getPath(), ex);
		}
	}
	
	public static File getCanonicalFile(File file)
	{
		try
		{
			return file.getCanonicalFile();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching canonical path: {}", file.getPath(), ex);
		}
	}

	public static String getRelativePath(File parent, File child)
	{
		String parentPath = getCanonicalPath(parent);
		String childPath = getCanonicalPath(child);
		
		if(parentPath.equals(childPath))
		{
			return "";
		}
		
		if(!childPath.startsWith(parentPath))
		{
			return null;
		}
		
		String relativePath = childPath.replace(parentPath, "");
		File relativePathFile = new File(parentPath, relativePath);
		
		relativePath = (childPath.equals(getCanonicalPath(relativePathFile))) ? relativePath : null;
		
		if(relativePath == null)
		{
			return null;
		}
		
		if(relativePath.startsWith(File.separator))
		{
			relativePath = relativePath.substring(1);
		}
		
		return relativePath;
	}
	
	
}
