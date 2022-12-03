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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * The Class SimpleDataSource.
 */
public class SimpleDataSource implements DataSource
{
	/**
	 * Jdc driver class name.
	 */
	private String driverClassName;
	
	/**
	 * jdbc url.
	 */
	private String url;
	
	/**
	 * Db user name.
	 */
	private String userName;
	
	/**
	 * Db password.
	 */
	private String password;
	
	/**
	 * Flag indicating if this class was initialized before.
	 */
	private boolean initialized;
	
	/**
	 * Gets the jdc driver class name.
	 *
	 * @return the jdc driver class name
	 */
	public String getDriverClassName()
	{
		return driverClassName;
	}

	/**
	 * Sets the jdc driver class name.
	 *
	 * @param driverClassName the new jdc driver class name
	 */
	public void setDriverClassName(String driverClassName)
	{
		this.driverClassName = driverClassName;
	}

	/**
	 * Gets the jdbc url.
	 *
	 * @return the jdbc url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Sets the jdbc url.
	 *
	 * @param url the new jdbc url
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * Gets the db user name.
	 *
	 * @return the db user name
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the db user name.
	 *
	 * @param userName the new db user name
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the db password.
	 *
	 * @return the db password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the db password.
	 *
	 * @param password the new db password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException
	{
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException
	{
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException
	{
	}

	@Override
	public int getLoginTimeout() throws SQLException
	{
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException
	{
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		if(!initialized)
		{
			try
			{
				Class.forName(driverClassName);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException("Invalid driver class specified: {}", driverClassName, ex);
			}
			
			initialized = true;
		}
		
		if(StringUtils.isNotEmpty(userName) && StringUtils.isNoneEmpty(password))
		{
			return DriverManager.getConnection(url, userName, password);
		}
		
		return DriverManager.getConnection(url);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException
	{
		throw new UnsupportedOperationException("This function is not supported.");
	}
}
