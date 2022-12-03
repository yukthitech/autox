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
package com.yukthitech.autox.test.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Represents a mongo db resource.
 * @author akiran
 */
public class MongoResource implements Validateable
{
	/**
	 * The Constant HOST_PORT.
	 */
	public static final Pattern HOST_PORT = Pattern.compile("([\\w\\.\\-]+)\\:(\\d+)");
	
	/**
	 * Replicas of mongo db.
	 */
	private List<ServerAddress> serverAddresses = new ArrayList<>();
	
	/**
	 * Name of this mongo resource.
	 */
	private String name;
	
	/**
	 * Mongo db name.
	 */
	private String dbName;
	
	/**
	 * User name to be used.
	 */
	private String userName;
	
	/**
	 * Password to be used.
	 */
	private String password;

	/**
	 * Internal field. Translated mongo client.
	 */
	private MongoClient mongoClient;

	/**
	 * Gets the name of this mongo resource.
	 *
	 * @return the name of this mongo resource
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this mongo resource.
	 *
	 * @param name the new name of this mongo resource
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the replicas of mongo db.
	 *
	 * @param replicas the new replicas of mongo db
	 */
	public void setReplicas(String replicas)
	{
		String lst[] = replicas.trim().split("\\s*\\,\\s*");
		
		for(String item : lst)
		{
			Matcher matcher = HOST_PORT.matcher(item);
			
			if(!matcher.matches())
			{
				throw new InvalidArgumentException("Invalid mongo host-port combination specified. It should be of format host:port. Specified Replicas: {}", replicas);
			}
			
			this.serverAddresses.add(new ServerAddress(matcher.group(1), Integer.parseInt(matcher.group(2))));
		}
	}

	/**
	 * Gets the mongo db name.
	 *
	 * @return the mongo db name
	 */
	public String getDbName()
	{
		return dbName;
	}

	/**
	 * Sets the mongo db name.
	 *
	 * @param dbName the new mongo db name
	 */
	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}

	/**
	 * Gets the user name to be used.
	 *
	 * @return the user name to be used
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the user name to be used.
	 *
	 * @param userName the new user name to be used
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the password to be used.
	 *
	 * @return the password to be used
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password to be used.
	 *
	 * @param password the new password to be used
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * Gets the internal field. Translated mongo client.
	 *
	 * @return the internal field
	 */
	public MongoClient getMongoClient()
	{
		if(mongoClient != null)
		{
			return mongoClient;
		}
		
		MongoCredential credential = (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) ? MongoCredential.createCredential(userName, dbName, password.toCharArray()) : null;
		MongoClient client = null;
		
		if(credential != null)
		{
			client = new MongoClient(
				serverAddresses, 
				credential,
				MongoClientOptions.builder().writeConcern(WriteConcern.ACKNOWLEDGED).build()
			);
		}
		else
		{
			client = new MongoClient(
				serverAddresses, 
				MongoClientOptions.builder().writeConcern(WriteConcern.ACKNOWLEDGED).build()
			);
		}
		
		this.mongoClient = client;
		return client;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Name can not be empty.");
		}

		if(CollectionUtils.isEmpty(serverAddresses))
		{
			throw new ValidateException("Server-addresses can not be empty.");
		}

		if(StringUtils.isBlank(dbName))
		{
			throw new ValidateException("Db-name can not be empty.");
		}

		if(StringUtils.isBlank(userName) && !StringUtils.isBlank(password))
		{
			throw new ValidateException("Password is specified without username.");
		}
	}
}
