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
package com.yukthitech.autox.test;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manages report resources and copies to output folder.
 * @author akiran
 */
public class ResourceManager
{
	/**
	 * Classpath resource folder where report resource files are maintained.
	 */
	public static final String REPORT_RES_FOLDER = "/report-resources/";
	
	/**
	 * Classpath resource folder where doc resource files are maintained.
	 */
	public static final String DOC_RES_FOLDER = "/doc-resources/";
	
	private static final ResourceManager instance = new ResourceManager();
	
	static
	{
		try
		{
			XMLBeanParser.parse(ResourceManager.class.getResourceAsStream("/report-resource-list.xml"), instance.reportResources);
			//XMLBeanParser.parse(ResourceManager.class.getResourceAsStream("/doc-resource-list.xml"), instance.docResources);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading report resource list", ex);
		}
	}
	
	/**
	 * Represents resource file.
	 * @author akiran
	 */
	public static class ResourceFile
	{
		/**
		 * Path of the resource file in report resources folder.
		 */
		private String path;
		
		public void setPath(String path)
		{
			this.path = path;
		}
	}
	
	/**
	 * List of resource files.
	 * @author akiran
	 */
	public static class ResourceFileList
	{
		/**
		 * Resource files to be copied with generated report data.
		 */
		private List<ResourceFile> resourceFiles = new ArrayList<>();
		
		/**
		 * Adds a file to be copied.
		 * @param file
		 */
		public void addFile(ResourceFile file)
		{
			this.resourceFiles.add(file);
		}
	}
	
	/**
	 * Document resource files.
	 */
	private ResourceFileList docResources = new ResourceFileList();
	
	/**
	 * Report resource files.
	 */
	private ResourceFileList reportResources = new ResourceFileList();
	
	/**
	 * Gets singleton instance of this class.
	 * @return
	 */
	public static ResourceManager getInstance()
	{
		return instance;
	}
	
	/**
	 * Copies report resources to specified out folder.
	 * @param outFolder folder to where resources to be copied.
	 */
	public void copyReportResources(File outFolder)
	{
		copyResources(outFolder, reportResources, REPORT_RES_FOLDER);
	}
	
	/**
	 * Copies doc resources to specified out folder.
	 * @param outFolder folder to where resources to be copied.
	 */
	public void copyDocResources(File outFolder)
	{
		copyResources(outFolder, docResources, DOC_RES_FOLDER);
	}
	
	/**
	 * Copies specified resource list files to specified out folder from specified source res folder.
	 * @param outFolder out folder to where resources to be copied
	 * @param resourceList list of resources to be copied
	 * @param resFolder source class path resource folder
	 */
	private void copyResources(File outFolder, ResourceFileList resourceList, String resFolder)
	{
		String subpath = null;
		int idx = 0;
		File parentFolder = null, outFile = null;
		
		for(ResourceFile file : resourceList.resourceFiles)
		{
			idx = file.path.lastIndexOf("/");
			parentFolder = outFolder;
			
			//if required create sub folders
			if(idx > 0)
			{
				subpath = file.path.substring(0, idx);
				parentFolder = new File(outFolder, subpath);
				
				if(!parentFolder.exists())
				{
					try
					{
						FileUtils.forceMkdir(parentFolder);
					}catch(Exception ex)
					{
						throw new InvalidStateException("An error occurred while creating output folder path: {}", parentFolder.getPath());
					}
				}
				
				outFile = new File(parentFolder, file.path.substring(idx + 1));
			}
			else
			{
				outFile = new File(parentFolder, file.path);
			}
			
			try
			{
				InputStream is = ResourceManager.class.getResourceAsStream(resFolder + file.path);
				FileUtils.copyInputStreamToFile(is, outFile);
				is.close();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while copying resource '{}' as out file: {}", resFolder + file.path, outFile.getPath(), ex);
			}
		}
	}
}
