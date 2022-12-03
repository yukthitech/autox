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
package com.yukthitech.autox.ide.model;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class ProjectClassLoader extends URLClassLoader
{
	public ProjectClassLoader(Set<String> projectClassPath)
	{
		super(getProjectClassPath(projectClassPath), null);
	}

	/**
	 * Gets the urls of classpath of system classloader.
	 * @return
	 */
	public static URL[] getProjectClassPath(Set<String> projectClassPath)
	{
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		List<URL> resUrls = new ArrayList<>();
		
		if(CollectionUtils.isNotEmpty(projectClassPath))
		{
			for(String entry : projectClassPath)
			{
				File file = new File(entry);
				
				try
				{
					resUrls.add(file.toURI().toURL());
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurrred while converting file into url: {}", file.getPath());
				}
			}
		}
		
		resUrls.addAll(Arrays.asList( ((URLClassLoader)cl).getURLs() ));
		
        return resUrls.toArray(new URL[0]);
	}
	
	public String toClassPath()
	{
		StringBuilder builder = new StringBuilder();
		URL urls[] = super.getURLs();
		
		for(URL url : urls)
		{
			File file = FileUtils.toFile(url);
			builder.append(file.getPath());
			builder.append(File.pathSeparator);
		}
		
		return builder.toString();
	}
}
