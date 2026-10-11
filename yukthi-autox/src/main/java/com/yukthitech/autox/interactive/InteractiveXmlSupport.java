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
package com.yukthitech.autox.interactive;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.TestSuiteParserHandler;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.autox.test.CustomPrefixExpression;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Shared parse / reload support for interactive step XML (Prism execute-steps and
 * {@code AutoxSession.executeSteps}).
 */
public class InteractiveXmlSupport
{
	private static final Logger logger = LogManager.getLogger(InteractiveXmlSupport.class);
	
	private static final AtomicInteger DYNAMIC_SOURCE_SEQ = new AtomicInteger(0);
	
	private static final String stepHolderTemplate;
	
	static
	{
		try
		{
			stepHolderTemplate = IOUtils.toString(
					InteractiveXmlSupport.class.getResourceAsStream("/step-holder-template.xml"),
					Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading resource: /step-holder-template.xml", ex);
		}
	}
	
	private InteractiveXmlSupport()
	{}
	
	/**
	 * Next synthetic source file name for dynamic {@code executeSteps} (no caller source).
	 */
	public static File nextDynamicSourceFile()
	{
		return new File("interactive-steps-" + DYNAMIC_SOURCE_SEQ.incrementAndGet() + ".xml");
	}
	
	/**
	 * Ensures a {@link TestSuiteParserHandler} is registered on the context.
	 */
	public static TestSuiteParserHandler ensureParserHandler(AutomationContext context)
	{
		DefaultParserHandler existing = context.getTestSuiteParserHandler();
		
		if(existing instanceof TestSuiteParserHandler)
		{
			return (TestSuiteParserHandler) existing;
		}
		
		TestSuiteParserHandler handler = new TestSuiteParserHandler(context);
		context.setTestSuiteParserHandler(handler);
		return handler;
	}
	
	/**
	 * Parses interactive step XML (wrapped in step-holder template) into a {@link StepHolder}.
	 * Uses a dynamic source file name during parse so steps get non-null locations (same pattern
	 * as interactive execution today); callers may then {@link StepHolder#updateLocations}.
	 */
	public static StepHolder parseSteps(AutomationContext context, String stepXml)
	{
		if(StringUtils.isBlank(stepXml))
		{
			throw new InvalidArgumentException("Steps XML cannot be empty");
		}
		
		TestSuiteParserHandler parserHandler = ensureParserHandler(context);
		File previousFile = parserHandler.getFileBeingParsed();
		File parseSource = nextDynamicSourceFile();
		parserHandler.setFileBeingParsed(parseSource);
		
		StepHolder stepHolder = new StepHolder();
		
		String wrappedXml = CommonUtils.replaceExpressions(
				CommonUtils.toMap("steps", stepXml),
				stepHolderTemplate, null);
		
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(wrappedXml.getBytes(Charset.defaultCharset()));
			XMLBeanParser.parse(bis, stepHolder, parserHandler);
			return stepHolder;
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to parse interactive step xml", ex);
		}finally
		{
			parserHandler.setFileBeingParsed(previousFile);
		}
	}
	
	/**
	 * Applies custom UI locators, prefix expressions, and functions from the holder into the
	 * live context (and optional target test suite).
	 *
	 * @return {@code true} if any reload was applied
	 */
	public static boolean applyReloads(AutomationContext context, StepHolder stepHolder, String targetTestSuiteName)
	{
		boolean reloadingDone = false;
		
		if(CollectionUtils.isNotEmpty(stepHolder.getCustomUiLocators()))
		{
			for(CustomPrefixExpression customUiLocator : stepHolder.getCustomUiLocators())
			{
				logger.debug("Reloading custom-ui-locator: {}", customUiLocator.getName());
				PrefixExpressionFactory.getExpressionFactory().addOrReplaceUiLocator(customUiLocator);
			}
			
			reloadingDone = true;
		}
		
		if(CollectionUtils.isNotEmpty(stepHolder.getCustomPrefixExpressions()))
		{
			for(CustomPrefixExpression customPrefixExpr : stepHolder.getCustomPrefixExpressions())
			{
				logger.debug("Reloading custom-prefix-expression: {}", customPrefixExpr.getName());
				PrefixExpressionFactory.getExpressionFactory().addOrReplacePrefixExpression(customPrefixExpr);
			}
			
			reloadingDone = true;
		}
		
		if(CollectionUtils.isNotEmpty(stepHolder.getFunctions()))
		{
			TestSuite targetTestSuite = null;
			
			if(StringUtils.isNotBlank(targetTestSuiteName))
			{
				TestSuiteGroup group = context.getTestSuiteGroup();
				targetTestSuite = (group != null) ? group.getTestSuite(targetTestSuiteName) : null;
				
				if(targetTestSuite == null)
				{
					throw new InvalidArgumentException(
							"Invalid test suite name specified for function reload: {}", targetTestSuiteName);
				}
			}
			
			for(Function func : stepHolder.getFunctions())
			{
				logger.debug("Reloading function: {}", func.getName());
				
				if(targetTestSuite != null)
				{
					targetTestSuite.addOrReplaceFunction(func);
				}
				else
				{
					context.addOrReplaceFunction(func);
				}
			}
			
			reloadingDone = true;
		}
		
		return reloadingDone;
	}
	
	/**
	 * Parse, apply optional location update, and apply reloads. Used by Prism execute-steps.
	 *
	 * @param sourceFile location file for steps (may be {@code null} to skip location update)
	 * @param startLineNumber start line in source (used with location update)
	 * @param targetTestSuiteName optional suite for function reload
	 */
	public static StepHolder parseAndApply(AutomationContext context, String stepXml,
			File sourceFile, int startLineNumber, String targetTestSuiteName)
	{
		StepHolder stepHolder = parseSteps(context, stepXml);
		
		if(sourceFile != null)
		{
			stepHolder.updateLocations(sourceFile, startLineNumber);
		}
		
		applyReloads(context, stepHolder, targetTestSuiteName);
		return stepHolder;
	}
}
