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
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yukthitech.autox.AutoxVersion;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.doc.DocGenerator;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.beans.BeanPropertyInfoFactory;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents project and its details.
 * 
 * @author akiran
 */
public class Project implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(Project.class);

	/**
	 * Name of the project file.
	 */
	public static final String PROJECT_FILE_NAME = "autox-project.json";

	private String name;
	private String projectFilePath;
	private String appConfigFilePath;
	private String appPropertyFilePath;
	private LinkedHashSet<String> testSuitesFoldersList;
	private LinkedHashSet<String> resourceFoldersList;
	private LinkedHashSet<String> classPathEntriesList;
	
	private transient ProjectClassLoader projectClassLoader;
	
	private transient DocInformation docInformation;
	
	private transient BeanPropertyInfoFactory beanPropertyInfoFactory;
	
	private transient File baseFolder;
	
	private transient Set<File> reservedFiles;
	
	private transient Set<String> finalClassPathEntries = null; 
	
	public Project()
	{
		name = "";
		appConfigFilePath = "src/main/config/app-configuration.xml";
		appPropertyFilePath = "src/main/config/app.properties";
		testSuitesFoldersList = new LinkedHashSet<>(CommonUtils.toSet("src/main/test-suites"));
		resourceFoldersList = new LinkedHashSet<>(CommonUtils.toSet("src/main/resources"));
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		if(resourceFoldersList == null)
		{
			resourceFoldersList = new LinkedHashSet<>();
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String projectName)
	{
		this.name = projectName;
	}

	@JsonIgnore
	public void setProjectFilePath(String projectFilePath)
	{
		File projectFile = new File(projectFilePath);

		if(!projectFile.getName().equals(PROJECT_FILE_NAME))
		{
			throw new InvalidArgumentException("Invalid project file specified: " + projectFilePath);
		}

		this.projectFilePath = projectFilePath;
	}

	public String getProjectFilePath()
	{
		return projectFilePath;
	}

	@JsonIgnore
	public String getBaseFolderPath()
	{
		if(projectFilePath == null)
		{
			return null;
		}

		try
		{
			return new File(projectFilePath).getParentFile().getCanonicalPath();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while loading project file cannonical path: " + projectFilePath, ex);
		}
	}
	
	@JsonIgnore
	public File getBaseFolder()
	{
		if(baseFolder != null)
		{
			return baseFolder;
		}
		
		baseFolder = new File(projectFilePath).getParentFile();
		return baseFolder;
	}

	public String getAppConfigFilePath()
	{
		return appConfigFilePath;
	}

	public void setAppConfigFilePath(String appConfigFilePath)
	{
		this.appConfigFilePath = appConfigFilePath;
	}

	public String getAppPropertyFilePath()
	{
		return appPropertyFilePath;
	}

	public void setAppPropertyFilePath(String appPropertyFilePath)
	{
		this.appPropertyFilePath = appPropertyFilePath;
	}

	public Set<String> getTestSuitesFoldersList()
	{
		return testSuitesFoldersList;
	}
	
	public LinkedHashSet<String> getResourceFoldersList()
	{
		return resourceFoldersList;
	}
	
	public boolean isTestSuiteFolderFile(File file)
	{
		String baseFolderPath = getBaseFolderPath();
				
		for(String path : testSuitesFoldersList)
		{
			File folder = new File(baseFolderPath, path);
			
			if(IdeFileUtils.getRelativePath(folder, file) != null)
			{
				return true;
			}
		}
		
		return false;
	}

	public boolean isTestSuiteFolder(File file)
	{
		String relPath = IdeFileUtils.getRelativePath(getBaseFolder(), file);
		return testSuitesFoldersList.contains(relPath);
	}

	public boolean isResourceFolder(File file)
	{
		String relPath = IdeFileUtils.getRelativePath(getBaseFolder(), file);
		return resourceFoldersList.contains(relPath);
	}

	public void setTestSuiteFoldersList(Set<String> testSuitesFoldersList)
	{
		this.testSuitesFoldersList = new LinkedHashSet<String>(testSuitesFoldersList);
	}
	
	public void setResourceFoldersList(Set<String> resourceFoldersList)
	{
		this.resourceFoldersList = new LinkedHashSet<String>(resourceFoldersList);
	}

	@JsonIgnore
	public Set<String> getFullClassPathEntriesList()
	{
		if(finalClassPathEntries == null)
		{
			addDefaultClasspathEntries();
		}
		
		return finalClassPathEntries;
	}
	
	private synchronized void addDefaultClasspathEntries()
	{
		LinkedHashSet<String> pathEntries = new LinkedHashSet<>();
		
		File baseFolder = getBaseFolder();

		//add main resources and config folders
		pathEntries.add(new File(baseFolder, "src" + File.separator + "main" + File.separator + "config").getPath());
		pathEntries.add(new File(baseFolder, "src" + File.separator + "main" + File.separator + "resources").getPath());
		pathEntries.add(new File(baseFolder, "target" + File.separator + "classes").getPath());

		//add lib folder of project
		File libFolder = new File(baseFolder, "lib");
		
		try
		{
			String libJarPath = libFolder.getCanonicalPath() + File.separator + "*";
			pathEntries.add(libJarPath);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while getting canonical path of lib folder: " + libFolder.getPath(), ex);
		}
		
		if(classPathEntriesList != null)
		{
			pathEntries.addAll(classPathEntriesList);
		}
		
		this.finalClassPathEntries = pathEntries;
	}
	
	/**
	 * Sets the class path entries list.
	 * Added for backward compatibility.
	 *
	 * @param classPathEntriesList the new class path entries list
	 */
	public void setClassPathEntriesList(LinkedHashSet<String> classPathEntriesList)
	{
		this.setClassPathEntries(classPathEntriesList);
	}

	public void setClassPathEntries(LinkedHashSet<String> classPathEntriesList)
	{
		this.classPathEntriesList = classPathEntriesList;
		finalClassPathEntries = null;
		projectClassLoader = null;
	}
	
	public Set<String> getClassPathEntries()
	{
		return classPathEntriesList;
	}
	
	public void reset()
	{
		finalClassPathEntries = null;
		projectClassLoader = null;
	}

	/**
	 * Creates all the folder structure and files required by current project.
	 * 
	 * @throws IOException
	 */
	public void createProject() throws IOException
	{
		File projFile = new File(projectFilePath);
		File baseFolder = projFile.getParentFile();

		if(!baseFolder.exists())
		{
			FileUtils.forceMkdir(baseFolder);
		}

		logger.trace("Creating project with path: {}", testSuitesFoldersList);
		
		for(String testsuiteFolderPath : testSuitesFoldersList)
		{
			File testSuiteFolder = new File(baseFolder, testsuiteFolderPath);
	
			if(!testSuiteFolder.exists())
			{
				FileUtils.forceMkdir(testSuiteFolder);
			}
		}
		
		for(String resourcesFolderPath : testSuitesFoldersList)
		{
			File resourcesFolder = new File(baseFolder, resourcesFolderPath);
	
			if(!resourcesFolder.exists())
			{
				FileUtils.forceMkdir(resourcesFolder);
			}
		}

		Map<String, String> pathToTemp = CommonUtils.toMap(
				appPropertyFilePath, "/templates/new-project/app-prop-template.properties",
				appConfigFilePath, "/templates/new-project/app-config-template.xml",
				"/pom.xml", "/templates/new-project/pom.xml",
				"/zip-pom.xml", "/templates/new-project/zip-pom.xml"
			);
		
		Map<String, String> context = CommonUtils.toMap(
				"testSuitesFolder", testSuitesFoldersList.iterator().next(),
				"projectName", this.name,
				"autoxVersion", AutoxVersion.getVersion()
			);
		
		for(Map.Entry<String, String> entry : pathToTemp.entrySet())
		{
			String path = entry.getKey();
			String temp = entry.getValue();
			
			File entryFile = new File(baseFolder, path);
			
			if(entryFile.getParentFile().exists())
			{
				FileUtils.forceMkdir(entryFile.getParentFile());
			}

			if(!entryFile.exists())
			{
				createFile(entryFile, temp, context);
				entryFile.createNewFile();
			}
		}

		save();
	}
	
	private void createFile(File file, String templateFile, Map<String, String> context) throws IOException
	{
		InputStream is = Project.class.getResourceAsStream(templateFile);
		String content = IOUtils.toString(is, Charset.defaultCharset());
		is.close();
		
		content = FreeMarkerMethodManager.replaceExpressions(templateFile, context, content);
		
		FileUtils.write(file, content, Charset.defaultCharset());
	}

	/**
	 * Loads the project from specified base folder path.
	 * 
	 * @param path
	 *            base folder path from which project needs to be loaded.
	 * @return loaded project
	 */
	public static Project load(String path)
	{
		File projectFile = new File(path);

		if(!projectFile.getName().equals(PROJECT_FILE_NAME))
		{
			throw new InvalidArgumentException("Invalid project file specified: " + path);
		}

		Project proj = IdeUtils.load(projectFile, Project.class);

		if(proj == null)
		{
			return null;
		}

		proj.setProjectFilePath(path);
		return proj;
	}

	/**
	 * Saves the current project and its state.
	 */
	public void save()
	{
		IdeUtils.save(this, new File(projectFilePath));
	}
	
	@JsonIgnore
	public ProjectClassLoader getProjectClassLoader()
	{
		if(projectClassLoader == null)
		{
			projectClassLoader = new ProjectClassLoader(getFullClassPathEntriesList());
		}
		
		return projectClassLoader;
	}
	
	@JsonIgnore
	public DocInformation getDocInformation()
	{
		if(docInformation != null)
		{
			return docInformation;
		}

		String[] basepackage = { "com.yukthitech" };

		try
		{
			docInformation = DocGenerator.buildDocInformation(basepackage);
		} catch(Exception e)
		{
			throw new IllegalStateException("An error occured while loading document Information", e);
		}
		
		return docInformation;
	}
	
	@JsonIgnore
	public BeanPropertyInfoFactory getBeanPropertyInfoFactory()
	{
		if(beanPropertyInfoFactory != null)
		{
			return beanPropertyInfoFactory;
		}
		
		beanPropertyInfoFactory = new BeanPropertyInfoFactory();
		return beanPropertyInfoFactory;
	}
	
	@JsonIgnore
	public synchronized boolean isReservedFile(File file)
	{
		if(file.getName().startsWith("."))
		{
			return true;
		}
		
		if(reservedFiles == null)
		{
			reservedFiles = new HashSet<>();
			File baseFolder = getBaseFolder();
			
			try
			{
				//exclude maven target folder
				reservedFiles.add( new File(baseFolder, "target").getCanonicalFile() );
				
				//exclude autox files
				reservedFiles.add( new File(baseFolder, appConfigFilePath).getCanonicalFile() );
				reservedFiles.add( new File(baseFolder, appPropertyFilePath).getCanonicalFile() );
				reservedFiles.add( new File(projectFilePath).getCanonicalFile() );
				
				for(String testSuiteFolder : this.testSuitesFoldersList)
				{
					reservedFiles.add( new File(baseFolder, testSuiteFolder).getCanonicalFile() );
				}

				for(String resFolder : this.resourceFoldersList)
				{
					reservedFiles.add( new File(baseFolder, resFolder).getCanonicalFile() );
				}
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while creating reserved folder list", ex);
			}
		}
		
		return reservedFiles.contains(file);
	}
	
	public void deleteProjectContents() throws IOException
	{
		File baseFolder = getBaseFolder();
		logger.debug("Deleting project contents from base folder: {}", baseFolder);
		FileUtils.forceDelete(baseFolder);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof Project))
		{
			return false;
		}

		Project other = (Project) obj;
		return name.equals(other.name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
