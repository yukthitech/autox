/**
 * Copyright (c) 2023 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
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
package com.yukthitech.autox;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.AutoxInfoException;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.event.AutomationEventManager;
import com.yukthitech.autox.event.AutomationEventType;
import com.yukthitech.autox.interactive.InteractiveXmlSupport;
import com.yukthitech.autox.test.TestDataFile;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.ccg.xml.XMLLoadException;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Helps in loading automation files.
 * @author akranthikiran
 */
public class AutomationFileLoader
{
	private static Logger logger = LogManager.getLogger(AutomationFileLoader.class);
	
	private static void handleLoadError(File xmlFile, Set<String> errors, Exception ex)
	{
		logger.error("An error occurred while loading resources", ex);
		
		String error = null;
		
		if(ex instanceof XMLLoadException)
		{
			XMLLoadException xmlLoadException = (XMLLoadException) ex;
			String mainError = null;
			
			if(xmlLoadException.getCause() instanceof AutoxInfoException)
			{
				mainError = xmlLoadException.getCause().getMessage();
			}
			else if(xmlLoadException.getCause() == null)
			{
				mainError = xmlLoadException.getMessage();
			}
			else
			{
				mainError = CommonUtils.getRootCauseMessages(ex.getCause());
			}
			
			if("true".equalsIgnoreCase(System.getProperty("logLoadErrors")))
			{
				logger.error("An error occurred while loading file: {}", xmlFile.getPath(), ex);
			}
			
			error = String.format("File: %s\n"
					+ "Location: [Line: %s, Column: %s]\n"
					+ "Error: %s", xmlFile.getPath(), 
						xmlLoadException.hasLocation() ? xmlLoadException.getLineNumber() : "<unknown>", 
						xmlLoadException.hasLocation() ? xmlLoadException.getColumn() : "<unknown>",
						mainError);
		}
		else
		{
			//only non-load exceptions log it. Loading exceptions should be displayed in proper format
			logger.error("An error occurred while loading file: {}", xmlFile.getPath(), ex);
			
			error = String.format("File: %s\n"
					+ "Error: %s", xmlFile.getPath(), CommonUtils.getRootCauseMessages(ex.getCause()));
		}
		
		errors.add(error);
	}
	
	public static void loadTestFile(File xmlFile, Set<String> errors, boolean forReload)
	{
		loadTestFile(xmlFile, InteractiveXmlSupport.ensureParserHandler(AutomationContext.getInstance()), errors, forReload);
	}

	private static void loadTestFile(File xmlFile,
			TestSuiteParserHandler defaultParserHandler, Set<String> errors, boolean forReload)
	{
		try(FileInputStream fis = new FileInputStream(xmlFile))
		{
			loadTestXml(fis, xmlFile, defaultParserHandler, errors, forReload);
		}catch(Exception ex)
		{
			handleLoadError(xmlFile, errors, ex);
		}
	}
	
	/**
	 * Loads suite/function XML from a string. {@code sourceName} is mandatory and used as the
	 * synthetic {@link File} for location attribution and load-error messages.
	 *
	 * @return load errors (empty on success)
	 */
	public static Set<String> loadTestXml(String xml, String sourceName, boolean forReload)
	{
		if(StringUtils.isBlank(xml))
		{
			throw new InvalidArgumentException("XML content cannot be empty");
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes(Charset.defaultCharset()));
		return loadTestXml(bis, sourceName, forReload);
	}
	
	/**
	 * Loads suite/function XML from a stream. {@code sourceName} is mandatory and used as the
	 * synthetic {@link File} for location attribution and load-error messages.
	 * Does not close {@code xmlStream}.
	 *
	 * @return load errors (empty on success)
	 */
	public static Set<String> loadTestXml(InputStream xmlStream, String sourceName, boolean forReload)
	{
		if(StringUtils.isBlank(sourceName))
		{
			throw new InvalidArgumentException("sourceName is required for loadTestXml (used for location / debug attribution)");
		}
		
		if(xmlStream == null)
		{
			throw new InvalidArgumentException("XML stream cannot be null");
		}
		
		AutomationContext context = AutomationContext.getInstance();
		TestSuiteParserHandler parserHandler = InteractiveXmlSupport.ensureParserHandler(context);
		Set<String> errors = new HashSet<>();
		loadTestXml(xmlStream, toSourceFile(sourceName), parserHandler, errors, forReload);
		return errors;
	}
	
	/**
	 * Builds a {@link File} used for location attribution. Logical names that are not valid
	 * filesystem paths on the current OS (e.g. {@code host:flow.xml} on Windows) are sanitized
	 * so {@code File#getCanonicalFile()} used by steps still succeeds; the path string remains
	 * recognizable in logs.
	 */
	static File toSourceFile(String sourceName)
	{
		// Drive-letter paths like C:\... are left alone; scheme-like "host:name" is sanitized
		if(sourceName.length() >= 2 && sourceName.charAt(1) == ':'
				&& Character.isLetter(sourceName.charAt(0)))
		{
			return new File(sourceName);
		}
		
		String sanitized = sourceName
				.replace(':', '_')
				.replace('*', '_')
				.replace('?', '_')
				.replace('"', '_')
				.replace('<', '_')
				.replace('>', '_')
				.replace('|', '_');
		
		return new File(sanitized);
	}
	
	private static void loadTestXml(InputStream xmlStream, File sourceFile,
			TestSuiteParserHandler defaultParserHandler, Set<String> errors, boolean forReload)
	{
		AutomationContext context = AutomationContext.getInstance();
		TestDataFile testDataFile = new TestDataFile(context, forReload);
		TestSuiteGroup testSuiteGroup = context.getTestSuiteGroup();
		
		defaultParserHandler.setFileBeingParsed(sourceFile);

		try
		{
			String sourcePath = sourceFile.getPath();

			logger.debug("Loading test suite xml from source: {}", sourcePath);
			
			XMLBeanParser.parse(xmlStream, testDataFile, defaultParserHandler);
			
			for(TestSuite testSuite : testDataFile.getTestSuites())
			{
				logger.debug("Loading test suite '{}' from source: {}", testSuite.getName(), sourcePath);
				
				testSuiteGroup.addTestSuite(testSuite, forReload);
			}
			
			if(testDataFile.getSetup() != null)
			{
				if(testSuiteGroup.getSetup() != null)
				{
					throw new InvalidStateException("Duplicate global setups specified. Files: [{}, {}]", testSuiteGroup.getSetup().getLocation().getPath(), sourcePath);
				}
				
				if(testDataFile.getSetup().getLocation() == null)
				{
					testDataFile.getSetup().setLocation(sourceFile, -1);
				}
				
				testSuiteGroup.setSetup(testDataFile.getSetup());
			}
			
			if(testDataFile.getCleanup() != null)
			{
				if(testSuiteGroup.getCleanup() != null)
				{
					throw new InvalidStateException("Duplicate global cleaups specified. Files: [{}, {}]", testSuiteGroup.getCleanup().getLocation().getPath(), sourcePath);
				}
				
				if(testDataFile.getCleanup().getLocation() == null)
				{
					testDataFile.getCleanup().setLocation(sourceFile, -1);
				}
				
				testSuiteGroup.setCleanup(testDataFile.getCleanup());
			}
		} catch(Exception ex)
		{
			handleLoadError(sourceFile, errors, ex);
		}
	}

	/**
	 * Loads test suites from the test suite folder specified by app
	 * configuration.
	 * 
	 * @param context current context
	 * @param appConfig
	 *            Application config to be used
	 * @return Test suites mapped by name.
	 */
	public static void loadTestSuites(AutomationContext context, boolean loadTestSuites)
	{
		TestSuiteParserHandler defaultParserHandler = new TestSuiteParserHandler(context);
		context.setTestSuiteParserHandler(defaultParserHandler);
		
		if(!loadTestSuites)
		{
			return;
		}
		
		ApplicationConfiguration appConfig = context.getAppConfiguration();
		logger.debug("Loading test suites from folders - {}", appConfig.getTestSuiteFolders());
		
		List<File> xmlFiles = new ArrayList<>();

		for(String folder : appConfig.getTestSuiteFolders())
		{
			File testCaseFolder = new File(folder);
	
			if(!testCaseFolder.exists() && !testCaseFolder.isDirectory())
			{
				System.err.println("Invalid test suite folder specified - " + testCaseFolder);
				
				((AutomationEventManager) AutomationContext.getInstance().getAutomationEventManager()).onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_SOURCE, "Invalid test suite folder specified - " + testCaseFolder);
				System.exit(-1);
			}
	
			// load the test suites recursively
			xmlFiles.addAll( AutomationUtils.loadXmlFiles(testCaseFolder) );
		}
		
		List<File> limitFolders = context.getBasicArguments().getFolderLimitFiles();
		
		if(limitFolders != null)
		{
			logger.debug("Limiting the xml file loading to folders: {}", limitFolders);
		}

		Set<String> errors = new HashSet<>();

		for(File xmlFile : xmlFiles)
		{
			if(limitFolders != null)
			{
				boolean found = false;
				
				for(File folder : limitFolders)
				{
					if(AutomationUtils.isChild(folder, xmlFile))
					{
						found = true;
						break;
					}
				}
				
				if(!found)
				{
					logger.trace("Skiping file as it is not present in specified folder limits. File: {}", xmlFile.getPath());
					continue;
				}
			}
			
			loadTestFile(xmlFile, defaultParserHandler, errors, false);
		}
		
		if(!errors.isEmpty())
		{
			String errorStr = errors.stream().collect(Collectors.joining("\n"));
			
			System.err.println("\n\n\n==============================================================\n");
			System.err.println("Failed to load test files because of below errors: \n");
			System.err.println(errorStr);
			System.err.println("\n\n==============================================================");
			
			((AutomationEventManager) AutomationContext.getInstance().getAutomationEventManager()).onAppError(AutomationEventType.AUTOMATION_ERR_INVALID_SOURCE, "Failed to load test files");
			System.exit(-1);
		}

		logger.debug("Found required plugins by this context to be: {}", AutomationContext.getInstance().getPluginManager().getPlugins());
	}

}
