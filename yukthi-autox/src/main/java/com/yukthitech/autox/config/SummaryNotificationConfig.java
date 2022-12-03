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

import org.apache.commons.lang3.StringUtils;

/**
 * Defines the configuration required by notification configuration.
 * @author akiran
 */
public class SummaryNotificationConfig
{
	/**
	 * Smtp server host.
	 */
	private String smptpHost;
	
	/**
	 * Smtp server port.
	 */
	private int smptpPort = 587;
	
	/**
	 * Flag indicating ttls enabled or not.
	 */
	private boolean ttlsEnabled = true;
	
	/**
	 * Flag indicating ssl should be used.
	 */
	private boolean enableSsl = false;
	
	/**
	 * Smtp user name to be used.
	 */
	private String userName;
	
	/**
	 * Smtp password to be used.
	 */
	private String password;
	
	/**
	 * Id from which notification should be marked as sent.
	 */
	private String fromAddress;
	
	/**
	 * Space separated address list to which notification should be sent.
	 */
	private String toAddressList;
	
	/**
	 * Template fore creating subject line.
	 */
	private String subjectTemplate;
	
	/**
	 * Header template file for summary report.
	 */
	private String headerTemplateFile;
	
	/**
	 * Footer template file for summary report.
	 */
	private String footerTemplateFile;
	
	/**
	 * Flag indicating if summary notification config is enabled or not.
	 */
	private boolean enabled = true;
	
	/**
	 * Instantiates a new summary notification config.
	 */
	public SummaryNotificationConfig()
	{}
	
	/**
	 * Instantiates a new summary notification config.
	 *
	 * @param smptpHost the smptp host
	 * @param userName the user name
	 * @param password the password
	 * @param toAddressList the to address list
	 * @param subjectTemplate the subject template
	 */
	public SummaryNotificationConfig(String smptpHost, String userName, String password, String toAddressList, String subjectTemplate)
	{
		this.smptpHost = smptpHost;
		this.userName = userName;
		this.password = password;
		this.toAddressList = toAddressList;
		this.subjectTemplate = subjectTemplate;
	}

	/**
	 * Gets the smtp server host.
	 *
	 * @return the smtp server host
	 */
	public String getSmptpHost()
	{
		return smptpHost;
	}

	/**
	 * Sets the smtp server host.
	 *
	 * @param smptpHost the new smtp server host
	 */
	public void setSmptpHost(String smptpHost)
	{
		this.smptpHost = smptpHost;
	}

	/**
	 * Gets the smtp server port.
	 *
	 * @return the smtp server port
	 */
	public int getSmptpPort()
	{
		return smptpPort;
	}

	/**
	 * Sets the smtp server port.
	 *
	 * @param smptpPort the new smtp server port
	 */
	public void setSmptpPort(int smptpPort)
	{
		this.smptpPort = smptpPort;
	}

	/**
	 * Gets the flag indicating ttls enabled or not.
	 *
	 * @return the flag indicating ttls enabled or not
	 */
	public boolean isTtlsEnabled()
	{
		return ttlsEnabled;
	}

	/**
	 * Sets the flag indicating ttls enabled or not.
	 *
	 * @param ttlsEnabled the new flag indicating ttls enabled or not
	 */
	public void setTtlsEnabled(boolean ttlsEnabled)
	{
		this.ttlsEnabled = ttlsEnabled;
	}

	/**
	 * Gets the smtp user name to be used.
	 *
	 * @return the smtp user name to be used
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the smtp user name to be used.
	 *
	 * @param userName the new smtp user name to be used
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the smtp password to be used.
	 *
	 * @return the smtp password to be used
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the smtp password to be used.
	 *
	 * @param password the new smtp password to be used
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * Fetches flag if auth is enabled or not.
	 * @return
	 */
	public boolean isAuthEnabled()
	{
		return ( StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password) ); 
	}

	/**
	 * Gets the id from which notification should be marked as sent.
	 *
	 * @return the id from which notification should be marked as sent
	 */
	public String getFromAddress()
	{
		if(fromAddress == null)
		{
			return userName;
		}
		
		return fromAddress;
	}

	/**
	 * Sets the id from which notification should be marked as sent.
	 *
	 * @param fromAddress the new id from which notification should be marked as sent
	 */
	public void setFromAddress(String fromAddress)
	{
		this.fromAddress = fromAddress;
	}

	/**
	 * Gets the space separated address list to which notification should be sent.
	 *
	 * @return the space separated address list to which notification should be sent
	 */
	public String getToAddressList()
	{
		return toAddressList;
	}

	/**
	 * Sets the space separated address list to which notification should be sent.
	 *
	 * @param toAddressList the new space separated address list to which notification should be sent
	 */
	public void setToAddressList(String toAddressList)
	{
		this.toAddressList = toAddressList;
	}

	/**
	 * Gets the template fore creating subject line.
	 *
	 * @return the template fore creating subject line
	 */
	public String getSubjectTemplate()
	{
		return subjectTemplate;
	}

	/**
	 * Sets the template fore creating subject line.
	 *
	 * @param subjectTemplate the new template fore creating subject line
	 */
	public void setSubjectTemplate(String subjectTemplate)
	{
		this.subjectTemplate = subjectTemplate;
	}

	/**
	 * Gets the header template file for summary report.
	 *
	 * @return the header template file for summary report
	 */
	public String getHeaderTemplateFile()
	{
		return headerTemplateFile;
	}

	/**
	 * Sets the header template file for summary report.
	 *
	 * @param headerTemplateFile the new header template file for summary report
	 */
	public void setHeaderTemplateFile(String headerTemplateFile)
	{
		this.headerTemplateFile = headerTemplateFile;
	}

	/**
	 * Gets the footer template file for summary report.
	 *
	 * @return the footer template file for summary report
	 */
	public String getFooterTemplateFile()
	{
		return footerTemplateFile;
	}

	/**
	 * Sets the footer template file for summary report.
	 *
	 * @param footerTemplateFile the new footer template file for summary report
	 */
	public void setFooterTemplateFile(String footerTemplateFile)
	{
		this.footerTemplateFile = footerTemplateFile;
	}

	/**
	 * Gets the flag indicating if summary notification config is enabled or not.
	 *
	 * @return the flag indicating if summary notification config is enabled or not
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets the flag indicating if summary notification config is enabled or not.
	 *
	 * @param enabled the new flag indicating if summary notification config is enabled or not
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnableSsl()
	{
		return enableSsl;
	}

	public void setEnableSsl(boolean enableSsl)
	{
		this.enableSsl = enableSsl;
	}
} 