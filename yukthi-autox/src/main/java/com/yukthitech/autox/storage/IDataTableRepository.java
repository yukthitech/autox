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

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.AggregateFunction;
import com.yukthitech.persistence.repository.annotations.AggregateFunctionType;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;

/**
 * Repository for data table.
 * @author akiran
 */
public interface IDataTableRepository extends ICrudRepository<DataTableEntity>
{
	/**
	 * Checks if the specified key already exists or not.
	 * @param key
	 * @return
	 */
	@AggregateFunction(type = AggregateFunctionType.COUNT)
	public boolean isExistingKey(@Condition("key") String key);
	
	/**
	 * Updates value for specified key with specified value.
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean updateValue(@Condition("key") String key, @Field("value") Object value);
	
	/**
	 * Deletes the entry with specified key.
	 * @param key key to be deleted
	 * @return true if deleted.
	 */
	public boolean deleteByKey(@Condition("key") String key);
	
	/**
	 * Fetches value with specified key.
	 * @param key
	 * @return
	 */
	@Field("value")
	public Object fetchByKey(String key);
}
