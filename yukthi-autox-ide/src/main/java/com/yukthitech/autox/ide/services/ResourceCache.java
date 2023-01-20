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
package com.yukthitech.autox.ide.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class ResourceCache
{
	private static Logger logger = LogManager.getLogger(ResourceCache.class);
	
	public static interface SupplierWithException<T>
	{
		public T get() throws Exception;
	}
	
	private static ResourceCache instance = new ResourceCache(); 
	
	private HashMap<String, Serializable> cacheMap = new HashMap<>();
	
	private File cacheFile = new File(".autox-ide.cache");
	
	private boolean modified = false;
	
	private ResourceCache()
	{
		if(!cacheFile.exists())
		{
			logger.info("Ide cache file not found. Resources will be loaded on the fly.");
			return;
		}
		
		try
		{
			FileInputStream fis = new FileInputStream(cacheFile);
			cacheMap = SerializationUtils.deserialize(fis);
			fis.close();
		}catch(Exception ex)
		{
			logger.warn("Failed to load ide cache file. Error: " + ex);
			cacheMap = new HashMap<>();
		}
	}
	
	public static ResourceCache getInstance()
	{
		return instance;
	}
	
	public synchronized void saveCache()
	{
		if(!modified)
		{
			logger.debug("As there is no change in cache, skipping ide cache file persisting.");
			return;
		}
		
		try
		{
			logger.debug("Persisting ide cache file..");
			FileOutputStream fos = new FileOutputStream(cacheFile);
			SerializationUtils.serialize(cacheMap, fos);
			fos.close();
		}catch(Exception ex)
		{
			logger.warn("Failed to persist ide cache file. Error: " + ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getFromCache(SupplierWithException<T> supplier, String keyFormat, Object... keyParams)
	{
		String key = String.format(keyFormat, keyParams);
		T cached = (T) get(key);
		
		if(cached != null)
		{
			return cached;
		}
		
		try
		{
			cached = supplier.get();
		}catch(Exception ex)
		{
			if(ex instanceof RuntimeException)
			{
				throw (RuntimeException) ex;
			}
			
			throw new InvalidStateException("An error occurred while building the object for cache", ex);
		}
		
		addToCache(key, cached);
		return cached;
	}
	
	public synchronized void addToCache(String key, Serializable value)
	{
		cacheMap.put(key, value);
		modified = true;
	}
	
	public synchronized Serializable get(String key)
	{
		return cacheMap.get(key);
	}
}
