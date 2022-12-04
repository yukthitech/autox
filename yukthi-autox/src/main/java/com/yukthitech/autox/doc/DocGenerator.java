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
package com.yukthitech.autox.doc;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.filter.ExpressionFactory;
import com.yukthitech.autox.filter.ExpressionParserDetails;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.ui.common.LocatorType;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.fmarker.FreeMarkerMethodDoc;
import com.yukthitech.utils.fmarker.FreeMarkerMethodExampleDoc;
import com.yukthitech.utils.fmarker.ParamDoc;

/**
 * Tool to generate documentation for all plugins, steps and validations.
 * @author akiran
 */
public class DocGenerator
{
	/**
	 * Loads the examples from all the autox example resources.
	 */
	private static ExampleCollectionFile loadExamples()
	{
		try
		{
			ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();   
			Resource [] mappingLocations = patternResolver.getResources("classpath*:/autox-examples/*.xml");
			
			if(mappingLocations == null || mappingLocations.length == 0)
			{
				return null;
			}
			
			InputStream is = null;
			ExampleCollectionFile finalFile = new ExampleCollectionFile(), curFile = null;
			
			for(Resource res : mappingLocations)
			{
				is = res.getInputStream();
				
				curFile = new ExampleCollectionFile();
				XMLBeanParser.parse(is, curFile);
				is.close();
				
				finalFile.merge(curFile);
			}
			
			return finalFile;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while loading example resources", ex);
		}
	}
	
	/**
	 * Loads the specified step types into specified doc info.
	 * @param docInformation target doc info
	 * @param stepTypes step types to be loaded
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void loadSteps(DocInformation docInformation, Set<Class<?>> stepTypes, ExampleCollectionFile exampleCollections)
	{
		Executable executableAnnot = null;

		for(Class<?> stepType : stepTypes)
		{
			if(stepType.isInterface() || Modifier.isAbstract(stepType.getModifiers()))
			{
				continue;
			}
			
			executableAnnot = stepType.getAnnotation(Executable.class);
			
			if(executableAnnot == null)
			{
				continue;
			}
			
			//ignore validation steps
			if(IValidation.class.isAssignableFrom(stepType))
			{
				continue;
			}
			
			//System.out.println("Found step of type: " + stepType.getName());
			docInformation.addStep(new StepInfo( (Class) stepType, executableAnnot, exampleCollections.getExamples(stepType.getName()) ));
		}
	}
	
	/**
	 * Loads specified validation types into target doc info.
	 * @param docInformation target doc info.
	 * @param validationTypes Validation types to be loaded
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void loadValidations(DocInformation docInformation, Set<Class<?>> validationTypes, ExampleCollectionFile exampleCollections)
	{
		Executable executableAnnot = null;

		for(Class<?> validationType : validationTypes)
		{
			if(validationType.isInterface() || Modifier.isAbstract(validationType.getModifiers()))
			{
				continue;
			}
			
			executableAnnot = validationType.getAnnotation(Executable.class);
			
			if(executableAnnot == null)
			{
				continue;
			}
			
			//System.out.println("Found validation of type: " + validationType.getName());
			docInformation.addValidation(
					new ValidationInfo( 
							(Class) validationType, 
							executableAnnot,
							exampleCollections.getExamples(validationType.getName())
							)
					);
		}
	}

	/**
	 * Loads specified plugins into target doc info.
	 * @param docInformation target doc info
	 * @param pluginTypes plugins to be loaded
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void loadPlugins(DocInformation docInformation, Set<Class<?>> pluginTypes)
	{
		Executable executableAnnot = null;

		for(Class<?> pluginType : pluginTypes)
		{
			if(pluginType.isInterface() || Modifier.isAbstract(pluginType.getModifiers()))
			{
				continue;
			}
			
			executableAnnot = pluginType.getAnnotation(Executable.class);
			
			if(executableAnnot == null)
			{
				continue;
			}
			
			System.out.println("Found plugin of type: " + pluginType.getName());
			docInformation.addPlugin(new PluginInfo((Class) pluginType, executableAnnot));
		}
	}
	
	private static void loadUiLocators(DocInformation docInfo, ExampleCollectionFile exampleCollections)
	{
		for(LocatorType loc : LocatorType.values())
		{
			UiLocatorDoc locDoc = new UiLocatorDoc(loc.getKey(), loc.getDescription());
			
			locDoc.addExamples(exampleCollections.getExamples("$uilocators." + loc.getKey()));
			
			docInfo.addUiLocator(locDoc);
		}
	}
	
	private static void loadExpressionParsers(DocInformation docInfo, ExampleCollectionFile exampleCollections)
	{
		ExpressionFactory.init(null, null);
		
		for(ExpressionParserDetails parser : ExpressionFactory.getExpressionFactory().getParserDetails())
		{
			ExpressionParserDoc parserDoc = new ExpressionParserDoc(parser.getType(), parser.getDescription(), parser.getContentType());
			parserDoc.addExample(new Example("Default", parser.getExample()));
			
			parserDoc.addExamples(exampleCollections.getExamples("$parsers." + parser.getType()));
			
			if(parser.getParams() != null)
			{
				for(ExpressionParserDetails.Param param : parser.getParams())
				{
					parserDoc.addParam(new ExpressionParserDoc.Param(param.getName(), param.getType(), param.getDefaultValue(), param.getDescription()));
				}
			}
			
			parserDoc.setAdditionalInfo(parser.getAdditionalInfo());
			
			docInfo.addParser(parserDoc);
		}
	}
	
	private static void loadFreeMarmerMethodDocs(DocInformation docInfo, String basePackages[], ExampleCollectionFile exampleCollections)
	{
		FreeMarkerMethodManager.reload(null, new HashSet<>(Arrays.asList(basePackages)), true);
		Collection<FreeMarkerMethodDoc> methodDocs = FreeMarkerMethodManager.getRegisterMethodDocuments();

		for(FreeMarkerMethodDoc doc : methodDocs)
		{
			FreeMarkerMethodDocInfo metInfo = new FreeMarkerMethodDocInfo();
			metInfo.setDescription(doc.getDescription());
			metInfo.setName(doc.getName());
			metInfo.setReturnDescription(doc.getReturnDescription());
			metInfo.setReturnType(doc.getReturnType());
			
			if(doc.getParameters() != null)
			{
				for(ParamDoc pdoc : doc.getParameters())
				{
					metInfo.addParameter(new ParamInfo(pdoc.getName(), pdoc.getDescription(), pdoc.getType()));
				}
			}
			
			if(doc.getExamples() != null)
			{
				for(FreeMarkerMethodExampleDoc exDoc : doc.getExamples())
				{
					metInfo.addExample(new Example(exDoc.getUsage(), exDoc.getResult()));
				}
			}
			
			metInfo.addExamples(exampleCollections.getExamples("$fmethod." + doc.getName()));
			
			docInfo.addFreeMarkerMethod(metInfo);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static DocInformation buildDocInformation(String basePackages[]) throws Exception
	{
		DocInformation docInformation = new DocInformation();
		Reflections reflections = null;
		
		ExampleCollectionFile exampleCollections = loadExamples();
		
		for(String pack : basePackages)
		{
			reflections = new Reflections(pack, Scanners.SubTypes);
			
			loadSteps(docInformation, (Set) reflections.getSubTypesOf(IStep.class), exampleCollections );

			loadValidations(docInformation, (Set) reflections.getSubTypesOf(IValidation.class), exampleCollections );
			
			loadPlugins(docInformation, (Set) reflections.getSubTypesOf(IPlugin.class) );
		}

		loadFreeMarmerMethodDocs(docInformation, basePackages, exampleCollections);
		
		loadUiLocators(docInformation, exampleCollections);
		
		loadExpressionParsers(docInformation, exampleCollections);
		
		//convert data into json
		return docInformation;
	}
	
	private static void copyDocResources(File outFolder, DocInformation docInformation) throws Exception
	{
		PathMatchingResourcePatternResolver loader = new PathMatchingResourcePatternResolver();
		Resource[] resources = loader.getResources("classpath:/docs/**/*.*");
		int prefixLen = "/docs/".length();
		
		for (Resource resource : resources) 
		{
			String resPath = resource.getURL().toString();
			
			if(resPath.endsWith("/"))
			{
				continue;
			}
			
			int idx = resPath.indexOf("/docs/");
			
			resPath = resPath.substring(idx + prefixLen);
			
			System.out.println("Copying doc resource: " + resPath);
			
			File destFile = new File(outFolder, resPath);
			FileUtils.forceMkdir(destFile.getParentFile());
			
			InputStream resIs = resource.getInputStream();
			FileUtils.copyInputStreamToFile(resIs, destFile);
			resIs.close();

			if("doc.php".equals(destFile.getName()))
			{
				String content = FileUtils.readFileToString(destFile, Charset.defaultCharset());
				content = FreeMarkerMethodManager.replaceExpressions(resPath, docInformation, content);
				FileUtils.write(destFile, content, Charset.defaultCharset());
			}
		}		
	}

	public static void main(String[] args) throws Exception
	{
		if(args.length != 2)
		{
			System.err.println("Invalid number of arguments specified.");
			System.err.println("Syntax: java " + DocGenerator.class.getName() + " <comma-separated-packages-to-scan> <out-folder>");
		}
		
		String packStr = args[0];
		String outFolder = args[1];
		
		String basePackages[] = packStr.split("\\s*\\,\\s*");
		
		DocInformation docInformation = buildDocInformation(basePackages);
		//docInformation.setBasicDocuments(loadBasicDocs());

		//convert data into json
		File outFolderFile = new File(outFolder);
		copyDocResources(outFolderFile, docInformation);
		
		
		System.out.println("Files are generate to folder: " + outFolder);
	}
}
