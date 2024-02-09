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
package com.yukthitech.autox.common;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerDirective;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;
import com.yukthitech.utils.fmarker.doc.FreeMarkerMethodDoc;

import freemarker.template.TemplateException;

public class FreeMarkerMethodManager
{
	private static Logger logger = LogManager.getLogger(FreeMarkerMethodManager.class);
	
	/**
	 * Freemarker template config used to parse expressions.
	 */
	private static FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	static
	{
		try
		{
			freeMarkerEngine.getConfiguration().setSetting("number_format", "#");
		} catch(TemplateException ex)
		{
			throw new InvalidStateException("An error occurred while init freemarker context", ex);
		}
		
		reload(null, getBasePackages(), false);
	}
	
	private static Set<String> getBasePackages()
	{
		ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
		Set<String> basePackages = applicationConfiguration != null ? applicationConfiguration.getBasePackages() : null;

		if(basePackages == null)
		{
			basePackages = new HashSet<>();
		}

		basePackages.add("com.yukthitech");

		return basePackages;
	}
	
	public static void reload(ClassLoader classLoader)
	{
		reload(classLoader, getBasePackages(), true);
	}
	
	public static void reload(ClassLoader classLoader, Set<String> basePackages, boolean reset)
	{
		if(reset)
		{
			freeMarkerEngine.reset();
		}
		
		loadFreeMarkerMethods(classLoader, basePackages);
	}
	
	private static void loadFreeMarkerMethods(ClassLoader classLoader, Set<String> basePackages)
	{
		if(classLoader == null)
		{
			classLoader = FreeMarkerMethodManager.class.getClassLoader();
		}

		Reflections reflections = null;
		Set<Method> freeMarkerMethods = null, freeMarkerDirectiveMethods = null;
		
		Set<Class<?>> fmClasses = new HashSet<>();

		for(String pack : basePackages)
		{
			logger.debug("Scanning for free marker methods in package - {}", pack);
			reflections = new Reflections(
					ConfigurationBuilder.build(pack, Scanners.MethodsAnnotated)
				);

			freeMarkerMethods = reflections.getMethodsAnnotatedWith(FreeMarkerMethod.class);
			freeMarkerDirectiveMethods = reflections.getMethodsAnnotatedWith(FreeMarkerDirective.class);
			
			if(freeMarkerMethods != null)
			{
				for(Method method : freeMarkerMethods)
				{
					fmClasses.add(method.getDeclaringClass());
				}
			}

			if(freeMarkerDirectiveMethods != null)
			{
				for(Method method : freeMarkerDirectiveMethods)
				{
					Class<?> cls = method.getDeclaringClass();
					
					if(fmClasses.contains(cls))
					{
						continue;
					}
					
					try
					{
						Class.forName(cls.getName(), true, classLoader);
					}catch(Exception ex)
					{
						throw new InvalidStateException("Failed to load fmarker method holder class: {}", cls.getName(), ex);
					}
								
					fmClasses.add(cls);
				}
			}
		}
		
		logger.debug("Found free marker method container classes to be: {}", fmClasses);
		
		for(Class<?> cls : fmClasses)
		{
			logger.debug("Loading free marker class: {}", cls.getName());
			freeMarkerEngine.loadClass(cls);
		}
	}
	
	/**
	 * Gets the freemarker template config used to parse expressions.
	 *
	 * @return the freemarker template config used to parse expressions
	 */
	static FreeMarkerEngine getFreeMarkerEngine()
	{
		return freeMarkerEngine;
	}

	/**
	 * Treats provided template as freemarker template and processes them. The result will be returned.
	 * @param templateName Name of template to be processed
	 * @param context context which would be used as freemarker context for processing.
	 * @param templateStr Template in which expressions should be replaced
	 * @return Processed string
	 */
	public static String replaceExpressions(String templateName, Object context, String templateStr)
	{
		try
		{
			return freeMarkerEngine.processTemplate(templateName, templateStr, context);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while processing template:\n" + templateStr, ex);
		}
	}

	/**
	 * Fetches method documentations of free marker methods.
	 * @return available free marker methods
	 */
	public static Collection<FreeMarkerMethodDoc> getRegisterMethodDocuments()
	{
		return freeMarkerEngine.getRegisterMethodDocuments();
	}

	/**
	 * Evaluates specified condition in specified context and returns the result.
	 * @param name name of condition template useful for debugging.
	 * @param condition condition to be evaluated.
	 * @param context context to be used for processing.
	 * @return result of condition evaluation.
	 */
	public static boolean evaluateCondition(String name, String condition, Object context)
	{
		return freeMarkerEngine.evaluateCondition(name, condition, context);
	}

	/**
	 * Evaluates specified condition in specified context and returns the result.
	 * @param name name of condition template useful for debugging.
	 * @param valueExpression Value expression whose value needs to be fetched.
	 * @param context context to be used for processing.
	 * @return result fetched value.
	 */
	public static Object fetchValue(String name, String valueExpression, Object context)
	{
		return freeMarkerEngine.fetchValue(name, valueExpression, context);
	}
}
