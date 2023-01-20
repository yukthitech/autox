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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.plugin.IPlugin;
import com.yukthitech.autox.plugin.PluginEvent;
import com.yukthitech.autox.plugin.PluginEvents;
import com.yukthitech.utils.cli.CliArgument;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Information about a plugin.
 * @author akiran
 */
public class PluginInfo implements Comparable<PluginInfo>, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the step.
	 */
	private String name;
	
	/**
	 * Description about the step.
	 */
	private String description;
	
	/**
	 * Java class representing this step.
	 */
	private String javaType;
	
	/**
	 * List of params accepted by this plugin.
	 */
	private Set<ParamInfo> params = new TreeSet<>();
	
	/**
	 * Command line arguments supported by this plugin.
	 */
	private Set<CommandLineArgInfo> cliArguments = new TreeSet<>();
	
	/**
	 * Events supported by this plugin.
	 */
	private Set<PluginEventInfo> events = new TreeSet<>();

	/**
	 * Instantiates a new plugin info.
	 *
	 * @param pluginClass the plugin class
	 * @param executablAnnot the executabl annot
	 */
	public PluginInfo(Class<? extends IPlugin<?, ?>> pluginClass, Executable executablAnnot)
	{
		this.name = Arrays.asList( executablAnnot.name() ).stream().collect(Collectors.joining(","));
		this.description = executablAnnot.message();
		this.javaType = pluginClass.getName();
		
		Class<?> curType = pluginClass;
		Param param = null;
		
		while(curType != null)
		{
			if(curType.getName().startsWith("java"))
			{
				break;
			}
			
			for(Field field : curType.getDeclaredFields())
			{
				if(Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				
				param = field.getAnnotation(Param.class);
				
				if(param == null)
				{
					continue;
				}
				
				this.params.add( new ParamInfo(field, param) );
			}
			
			curType = curType.getSuperclass();
		}

		try
		{
			fetchCliArguments(pluginClass);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetch cli argument details", ex);
		}
		
		fetchEventInfo(pluginClass);
	}
	
	private void fetchEventInfo(Class<? extends IPlugin<?, ?>> pluginClass)
	{
		PluginEvents eventsAnnot = pluginClass.getAnnotation(PluginEvents.class);
		
		if(eventsAnnot == null || eventsAnnot.value().length == 0)
		{
			return;
		}
		
		PluginEvent eventAnnots[] = eventsAnnot.value();
		
		for(PluginEvent eventAnnot : eventAnnots)
		{
			this.events.add(new PluginEventInfo(eventAnnot));
		}
	}
	
	/**
	 * Fetches cli arguments and details for specified plugin type.
	 * @param pluginClass Plugin type for which cli arguments has to be fetched.
	 */
	private void fetchCliArguments(Class<? extends IPlugin<?, ?>> pluginClass) throws Exception
	{
		IPlugin<?, ?> pluginInst = pluginClass.newInstance();
		Class<?> cliArgHolderType = pluginInst.getArgumentBeanType();
		
		if(cliArgHolderType == null)
		{
			return;
		}
		
		Class<?> curType = cliArgHolderType;
		CliArgument argAnnot = null;
		
		while(curType != null)
		{
			if(curType.getName().startsWith("java"))
			{
				break;
			}
			
			for(Field field : curType.getDeclaredFields())
			{
				if(Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				
				argAnnot = field.getAnnotation(CliArgument.class);
				
				if(argAnnot == null)
				{
					continue;
				}
				
				this.cliArguments.add( new CommandLineArgInfo(field, argAnnot) );
			}
			
			curType = curType.getSuperclass();
		}
	}
	
	/**
	 * Gets the name of the step.
	 *
	 * @return the name of the step
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the description about the step.
	 *
	 * @return the description about the step
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Gets the java class representing this step.
	 *
	 * @return the java class representing this step
	 */
	public String getJavaType()
	{
		return javaType;
	}

	/**
	 * Gets the list of params accepted by this step.
	 *
	 * @return the list of params accepted by this step
	 */
	public Set<ParamInfo> getParams()
	{
		return params;
	}

	/**
	 * Gets the command line arguments supported by this plugin.
	 *
	 * @return the command line arguments supported by this plugin
	 */
	public Set<CommandLineArgInfo> getCliArguments()
	{
		return cliArguments;
	}
	
	public Set<PluginEventInfo> getEvents()
	{
		return events;
	}
	
	public boolean hasExamples()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PluginInfo o)
	{
		return name.compareTo(o.name);
	}
}
