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
package com.yukthitech.autox.ds;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Wrapper on actual data source which gives extra functionality like force closing
 * data source after certain usages.
 * 
 * @author akranthikiran
 */
public class DataSourceWrapper implements DataSource
{
	private static Logger logger = LogManager.getLogger(DataSourceWrapper.class);
	
	private Class<?> dataSourceType;
	
	/**
	 * Data source properties.
	 */
	private Map<String, String> properties = new HashMap<String, String>();
	
	/**
	 * Number of times of data source for which data source can be used.
	 * Post which new data source should be created.
	 */
	private int maxCloseCount = -1;
	
	private int usageCount = 0;
	
	private PrintWriter out;
	
	private DataSource current;
	
	private Integer loginTimeout;
	
	public void setDataSourceType(Class<?> dataSourceType)
	{
		this.dataSourceType = dataSourceType;
	}

	public void setMaxCloseCount(int maxCloseCount)
	{
		this.maxCloseCount = maxCloseCount;
	}
	
	public void addProperty(String name, String value)
	{
		this.properties.put(name, value);
	}
	
	private DataSource createNewDataSource()
	{
		try
		{
			DataSource dataSource = (DataSource) dataSourceType.newInstance();
			
			for(Map.Entry<String, String> prop : this.properties.entrySet())
			{
				BeanUtils.setProperty(dataSource, prop.getKey(), prop.getValue());
			}
			
			if(out != null)
			{
				dataSource.setLogWriter(out);
			}
			
			if(loginTimeout != null)
			{
				dataSource.setLoginTimeout(loginTimeout);
			}
			
			return dataSource;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating data source object", ex);
		}
	}
	
	private synchronized DataSource getDataSource()
	{
		if(current == null)
		{
			current = createNewDataSource();
		}
		
		if(maxCloseCount <= 0)
		{
			return current;
		}
		
		if(usageCount >= maxCloseCount)
		{
			logger.debug("Closing current data-source after {} usages.", usageCount);
			
			if(current instanceof AutoCloseable)
			{
				try
				{
					((AutoCloseable) current).close();
				}catch(Exception ex)
				{
					logger.warn("An error occurred while closing data source", ex);
				}
			}
			
			current = createNewDataSource();
			usageCount = 0;
		}
		
		usageCount++;
		return current;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException
	{
		return out;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException
	{
		if(current != null)
		{
			current.setLogWriter(out);
		}

		this.out = out;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException
	{
		if(current != null)
		{
			current.setLoginTimeout(seconds);
		}

		loginTimeout = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException
	{
		return loginTimeout == null ? 0 : loginTimeout;
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException
	{
		if(current == null)
		{
			return null;
		}
		
		return current.getParentLogger();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		if(current == null)
		{
			return null;
		}
		
		return current.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		if(current == null)
		{
			return false;
		}
		
		return current.isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return getDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException
	{
		return getDataSource().getConnection(username, password);
	}
}
