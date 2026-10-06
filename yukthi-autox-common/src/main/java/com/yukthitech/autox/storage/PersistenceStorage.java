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
package com.yukthitech.autox.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.config.ApplicationConfiguration;

/**
 * Persistence storage used to store automation related data.
 * @author akiran
 */
public class PersistenceStorage
{
	private static Logger logger = LogManager.getLogger(PersistenceStorage.class);
	
	/**
	 * Data table repository.
	 */
	private IDataTableRepository dataTableRepository;
	
	public PersistenceStorage(ApplicationConfiguration config)
	{
		if(config.getStorageRepositoryFactory() != null)
		{
			this.dataTableRepository = config.getStorageRepositoryFactory().getRepository(IDataTableRepository.class);
		}
	}
	
	/**
	 * Sets the specified key-value into persistence storage.
	 * @param key Key to be used
	 * @param value value to set
	 */
	public synchronized void set(String key, Object value)
	{
		if(dataTableRepository == null)
		{
			throw new IllegalStateException("No storage repository specified for storage.");
		}
		
		boolean existingKey = dataTableRepository.isExistingKey(key);
		
		if(existingKey)
		{
			logger.debug("Updating entry with key: {}", key);
			dataTableRepository.updateValue(key, value);
		}
		else
		{
			logger.debug("Inserting new entry with key: {}", key);
			dataTableRepository.save(new DataTableEntity(key, value));
		}
	}
	
	/**
	 * Used to fetch value for specified key.
	 * @param key
	 * @return
	 */
	public synchronized Object get(String key)
	{
		if(dataTableRepository == null)
		{
			throw new IllegalStateException("No storage repository specified for storage.");
		}
		
		return dataTableRepository.fetchByKey(key);
	}
	
	/**
	 * Deletes entry with specified key.
	 * @param key key to be delete.
	 * @return true if deleted.
	 */
	public synchronized boolean remove(String key)
	{
		if(dataTableRepository == null)
		{
			throw new IllegalStateException("No storage repository specified for storage.");
		}
		
		return dataTableRepository.deleteByKey(key);
	}
}
