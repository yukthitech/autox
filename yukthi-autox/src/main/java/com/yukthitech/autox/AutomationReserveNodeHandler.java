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
package com.yukthitech.autox;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.xml.sax.Locator;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.PluginManager;
import com.yukthitech.autox.test.FunctionRef;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.reserved.NodeName;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Factory to create new steps and validations.
 * 
 * @author akiran
 */
@NodeName(namePattern = ".*")
public class AutomationReserveNodeHandler implements IReserveNodeHandler
{
	private static Logger logger = LogManager.getLogger(AutomationReserveNodeHandler.class);
	
	/**
	 * Mapping from step name to type.
	 */
	private Map<String, Class<? extends IStep>> nameToStepType = new HashMap<>();

	/**
	 * Current application configuration.
	 */
	private ApplicationConfiguration applicationConfiguration;
	
	/**
	 * Maintains the file being parsed.
	 */
	private File fileBeingParsed;
	
	/**
	 * Base packages to be used for loading.
	 */
	private Set<String> basePackages;

	/**
	 * Instantiates a new executable factory.
	 *
	 * @param appConfiguraion
	 *            the app configuraion
	 */
	public AutomationReserveNodeHandler(AutomationContext context, ApplicationConfiguration appConfiguraion)
	{
		this.applicationConfiguration = appConfiguraion;
		
		Set<String> basePackages = appConfiguraion.getBasePackages();

		if(basePackages == null)
		{
			basePackages = new HashSet<>();
		}

		basePackages.add("com.yukthitech");
		
		this.basePackages = new HashSet<>(basePackages);

		loadStepTypes(null);
	}

	/**
	 * Sets the maintains the file being parsed.
	 *
	 * @param fileBeingParsed the new maintains the file being parsed
	 */
	public void setFileBeingParsed(File fileBeingParsed)
	{
		this.fileBeingParsed = fileBeingParsed;
	}
	
	public File getFileBeingParsed()
	{
		return fileBeingParsed;
	}

	/**
	 * Loads all step types found in specified base packages.
	 * 
	 * @param  customLoader Custom class loader to be used to load step classes.
	 */
	protected void loadStepTypes(ClassLoader customLoader)
	{
		Reflections reflections = null;
		Set<Class<? extends IStep>> stepTypes = null;
		Executable executable = null;
		
		if(customLoader == null)
		{
			logger.debug("Using system class loader for scanning the steps..");
			customLoader = AutomationReserveNodeHandler.class.getClassLoader();
		}
		
		for(String pack : basePackages)
		{
			logger.debug("Scanning for steps in package: {}", pack);
			
			reflections = new Reflections(
					ConfigurationBuilder.build(pack, Scanners.TypesAnnotated, Scanners.SubTypes)
				);

			stepTypes = reflections.getSubTypesOf(IStep.class);
			
			logger.debug("Number of steps found in package {} to be: {}", pack, stepTypes.size());

			for(Class<? extends IStep> stepType : stepTypes)
			{
				if(stepType.isInterface() || Modifier.isAbstract(stepType.getModifiers()))
				{
					continue;
				}

				executable = stepType.getAnnotation(Executable.class);

				if(executable == null)
				{
					logger.debug("Skipping class {} as @Executable annotation is not found on the class", stepType.getName());
					continue;
				}

				nameToStepType.put(executable.name(), stepType);
			}
		}
		
		logger.debug("Steps loaded: {}", nameToStepType.keySet());
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.reserved.IReserveNodeHandler#createCustomNodeBean(com.yukthitech.ccg.xml.IParserHandler, com.yukthitech.ccg.xml.BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public Object createCustomNodeBean(IParserHandler handler, BeanNode beanNode, XMLAttributeMap attrMap, Locator locator)
	{
		Object parent = beanNode.getParent();
		
		if(parent instanceof IStepContainer)
		{
			if(IAutomationConstants.FUNC_NAME_SPACE.equals(beanNode.getNameSpace()))
			{
				FunctionRef funcRef = new FunctionRef();
				funcRef.setName(beanNode.getName());
				funcRef.setLocation(fileBeingParsed, locator.getLineNumber());
				
				return funcRef;
			}
			
			IStep step = newStep(beanNode.getName(), (IStepContainer) parent);

			if(step != null)
			{
				step.setLocation(fileBeingParsed, locator.getLineNumber());
				return step;
			}
			
			//if locator is specified throw exception with location details
			if(locator != null)
			{
				throw new InvalidStateException("[Line: {}, Column: {}] No step/validator found with name: {}.\nAvailable Steps/Validators: {}",
						locator.getLineNumber(), locator.getColumnNumber(),
						beanNode.getName(), nameToStepType.keySet());
			}
			
			throw new InvalidStateException("No step/validator found with name: {}.\nAvailable Steps/Validators: {}", beanNode.getName(), nameToStepType.keySet());
		}

		//if locator is specified throw exception with location details
		if(locator != null)
		{
			throw new InvalidStateException("[Line: {}, Column: {}] Unsupported custom node encountered. If it is step/validator ensure it is defined under right parent. Node name: {}",
					locator.getLineNumber(), locator.getColumnNumber(),
					beanNode.getName());
		}

		throw new InvalidStateException("Unsupported custom node encountered. If it is step/validator ensure it is defined under right parent. Node name: {}", beanNode.getName());
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.reserved.IReserveNodeHandler#handleCustomNodeEnd(com.yukthitech.ccg.xml.IParserHandler, com.yukthitech.ccg.xml.BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public void handleCustomNodeEnd(IParserHandler handler, BeanNode beanNode, XMLAttributeMap attrMap, Locator locator)
	{
		Object parent = beanNode.getParent();
		Object bean = beanNode.getActualBean();

		if((bean instanceof IStep) && (parent instanceof IStepContainer))
		{
			AutomationUtils.validateRequiredParams(bean);
			
			Executable executable = bean.getClass().getAnnotation(Executable.class);
			
			//if current step is expected part of another step
			if(executable.partOf().length > 0)
			{
				//get preceding step of current step
				// Note: required validations were performed as part of step pojo creation.
				List<IStep> steps = ((IStepContainer) parent).getSteps();
				IStep lastStep = steps.get(steps.size() - 1);

				//Note: The parent multi step validation is already done in method newStep()
				//add current step as child step to prev multi step
				((IMultiPartStep) lastStep).addChildStep((IStep) bean);
			}
			else
			{
				((IStepContainer) parent).addStep((IStep) bean);
			}
		}
	}
	
	private String getStepName(Class<?> cls)
	{
		Executable executable = cls.getAnnotation(Executable.class);
		return executable.name();
	}
	
	private void validateForParentStep(Class<?> currentStepType, IStep lastStep, Class<? extends IMultiPartStep>[] expectedTypes)
	{
		if(lastStep != null)
		{
			for(Class<?> typ : expectedTypes)
			{
				if(typ.isAssignableFrom(lastStep.getClass()))
				{
					return;
				}
			}
		}
		
		String expNames = Arrays.asList(expectedTypes)
				.stream()
				.map(step -> getStepName(step))
				.collect(Collectors.joining(", "));
				
		throw new InvalidStateException("Step {} is expected to be preceeded by one of the steps: {}",
				getStepName(currentStepType), expNames);
	}
	
	/**
	 * Creates a new step with specified name. A step can be named using @
	 * {@link Executable}
	 * 
	 * @param stepTypeName
	 *            Step name
	 * @return Matching step instance
	 */
	@SuppressWarnings("rawtypes")
	public IStep newStep(String stepTypeName, IStepContainer parent)
	{
		Class<? extends IStep> stepType = nameToStepType.get(stepTypeName);

		if(stepType == null)
		{
			return null;
		}
		
		Executable executable = stepType.getAnnotation(Executable.class);
		
		//if current step is expected part of another step
		if(executable.partOf().length > 0)
		{
			List<IStep> steps = parent.getSteps();
			IStep lastStep = CollectionUtils.isEmpty(steps) ? null : steps.get(steps.size() - 1);
			
			validateForParentStep(stepType, lastStep, executable.partOf());
		}
		
		IPlugin<?, ?> plugin = null;
		PluginManager pluginManager = PluginManager.getInstance();
		
		for(Class<? extends IPlugin> pluginType : executable.requiredPluginTypes())
		{
			plugin = applicationConfiguration.getPlugin(pluginType);
			
			if(plugin == null)
			{
				throw new InvalidStateException("Plugin-type {} is not specified by application-configuration which is required by step: {}", pluginType.getName(), stepTypeName);
			}
			
			pluginManager.addRequirePlugin(plugin);
		}

		return createStepInstance(stepType);
	}
	
	/**
	 * Creates step instance of specified type. Child classes can override this method
	 * to customize instance creation or created instance.
	 * @param stepType
	 * @return
	 */
	protected IStep createStepInstance(Class<?> stepType)
	{
		try
		{
			return (IStep) stepType.newInstance();
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating step of type - {}", stepType.getName());
		}
	}
}
