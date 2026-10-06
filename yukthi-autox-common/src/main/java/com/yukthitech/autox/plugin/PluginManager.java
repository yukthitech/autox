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
package com.yukthitech.autox.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.utils.cli.CommandLineOptions;
import com.yukthitech.utils.cli.MissingArgumentException;
import com.yukthitech.utils.cli.OptionsFactory;

/**
 * Manager of plugins.
 * @author akranthikiran
 */
public class PluginManager
{
	private static Logger logger = LogManager.getLogger(PluginManager.class);
	
	private static PluginManager instance = new PluginManager();
	
	/**
	 * Maintains list of required plugins required by loaded test suites.
	 */
	private Map<Class<?>, IPlugin<?, ?>> requiredPlugins = new HashMap<>();

	/**
	 * Maintain list of plugins which are already initialized.
	 */
	private Set<Class<?>> initializedPlugins = new HashSet<>();
	
	private PluginManager()
	{}
	
	public static PluginManager getInstance()
	{
		return instance;
	}
	
	/**
	 * Adds required plugin to the context.
	 * @param plugin plugin to add
	 */
	public synchronized void addRequirePlugin(IPlugin<?, ?> plugin)
	{
		this.requiredPlugins.put(plugin.getClass(), plugin);
	}
	
	/**
	 * Fetches all required plugins required by this context.
	 * @return required plugins.
	 */
	public Collection<IPlugin<?, ?>> getPlugins()
	{
		return Collections.unmodifiableCollection(requiredPlugins.values());
	}

	/**
	 * Initalize plugin.
	 *
	 * @param pluginType the plugin type
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initalizePlugin(Class<?> pluginType)
	{
		IPlugin<Object, IPluginSession> plugin = (IPlugin) requiredPlugins.get(pluginType);
		
		if(plugin == null)
		{
			return;
		}
		
		Class<?> pluginArgType = plugin.getArgumentBeanType();
		
		if(pluginArgType == null || Object.class.equals(pluginArgType))
		{
			plugin.initialize(null);
			initializedPlugins.add(pluginType);
			return;
		}
		
		logger.debug("Initializing plugin: {}", plugin.getClass().getName());
		
		List<Class<?>> argBeanTypes = new ArrayList<>();
		argBeanTypes.add(pluginArgType);

		//if any type is required creation command line options and parse command line arguments
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(argBeanTypes.toArray(new Class<?>[0]));
		Map<Class<?>, Object> argBeans = null;
		
		try
		{
			String[] extendedCommandLineArgs = AutomationContext.getInstance().getExtendedCommandLineArgs();
			argBeans = commandLineOptions.parseBeans(extendedCommandLineArgs);
		} catch(MissingArgumentException e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(-1);
		} catch(Exception ex)
		{
			ex.printStackTrace();
			System.exit(-1);
		}

		Object args = argBeans.get(plugin.getArgumentBeanType());
		plugin.initialize(args);
		
		initializedPlugins.add(pluginType);
	}

	/**
	 * Fetches the plugin of specified plugin type.
	 * @param pluginType plugin type to fetch
	 * @return matching plugin
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends IPlugin<?, ?>> T getPlugin(Class<T> pluginType)
	{
		if(!initializedPlugins.contains(pluginType))
		{
			initalizePlugin(pluginType);
		}
		
		return (T) requiredPlugins.get(pluginType);
	}

}
