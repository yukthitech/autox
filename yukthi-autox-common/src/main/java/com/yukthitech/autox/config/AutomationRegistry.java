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
package com.yukthitech.autox.config;

import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import com.yukthitech.autox.logmon.ILogMonitor;
import com.yukthitech.autox.plugin.IPlugin;

/**
 * Registry of dynamic app-config types (plugins and log monitors) discovered
 * from the classpath via Reflections.
 *
 * @author akranthikiran
 */
public final class AutomationRegistry
{
	private static final Logger logger = LogManager.getLogger(AutomationRegistry.class);

	private static final String BASE_PACKAGE = "com.yukthitech";

	/**
	 * Single map of camelCase XML/property name to concrete type.
	 */
	private static final Map<String, Class<?>> dynamicTypes = new HashMap<>();

	private static boolean loaded;

	private AutomationRegistry()
	{}

	/**
	 * Registers a plugin type under the given camelCase name.
	 *
	 * @param name camelCase name (e.g. seleniumPlugin)
	 * @param type plugin class
	 */
	public static void registerPlugin(String name, Class<?> type)
	{
		register(name, type);
	}

	/**
	 * Registers a log-monitor type under the given camelCase name.
	 *
	 * @param name camelCase name (e.g. fileLogMonitor)
	 * @param type log-monitor class
	 */
	public static void registerLogMonitor(String name, Class<?> type)
	{
		register(name, type);
	}

	private static void register(String name, Class<?> type)
	{
		if(name == null || type == null)
		{
			return;
		}

		Class<?> existing = dynamicTypes.put(name, type);

		if(existing != null && existing != type)
		{
			logger.warn("Overwriting dynamic type registration for '{}': {} -> {}", name, existing.getName(), type.getName());
		}
		else
		{
			logger.debug("Registered dynamic app-config type '{}' -> {}", name, type.getName());
		}
	}

	/**
	 * Fetches registered type for the given camelCase node/property name.
	 *
	 * @param name camelCase name
	 * @return matching class or null
	 */
	public static Class<?> getType(String name)
	{
		ensureLoaded();
		return dynamicTypes.get(name);
	}

	/**
	 * Discovers concrete {@link IPlugin} and {@link ILogMonitor} subtypes under
	 * {@code com.yukthitech} and registers them. Idempotent and synchronous.
	 */
	public static synchronized void ensureLoaded()
	{
		if(loaded)
		{
			return;
		}

		logger.debug("Scanning classpath for plugins and log-monitors under package: {}", BASE_PACKAGE);

		Reflections reflections = new Reflections(
				ConfigurationBuilder.build(BASE_PACKAGE, Scanners.SubTypes));

		registerConcreteTypes(reflections.getSubTypesOf(IPlugin.class));
		registerConcreteTypes(reflections.getSubTypesOf(ILogMonitor.class));

		loaded = true;
		logger.debug("Dynamic app-config types loaded: {}", dynamicTypes.keySet());
	}

	private static void registerConcreteTypes(Set<? extends Class<?>> types)
	{
		for(Class<?> type : types)
		{
			if(type.isInterface() || Modifier.isAbstract(type.getModifiers()))
			{
				continue;
			}

			String name = Introspector.decapitalize(type.getSimpleName());
			register(name, type);
		}
	}

	/**
	 * Resets registry state (intended for tests).
	 */
	public static synchronized void reset()
	{
		dynamicTypes.clear();
		loaded = false;
	}
}
 