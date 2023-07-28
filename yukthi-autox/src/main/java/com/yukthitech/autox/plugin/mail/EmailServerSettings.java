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
package com.yukthitech.autox.plugin.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Settings or information required for sending and reading the mails.
 * @author akiran
 */
public class EmailServerSettings implements Validateable
{
	public static class RefreshSettings implements Validateable
	{
		private String url;
		
		private String responseTokenProp;
		
		private Map<String, String> params = new HashMap<>();

		public String getUrl()
		{
			return url;
		}

		public void setUrl(String url)
		{
			this.url = url;
		}
		
		public void addParam(String name, String value)
		{
			this.params.put(name, value);
		}

		public Map<String, String> getParams()
		{
			return params;
		}

		public String getResponseTokenProp()
		{
			return responseTokenProp;
		}

		public void setResponseTokenProp(String responseTokenProp)
		{
			this.responseTokenProp = responseTokenProp;
		}

		@Override
		public void validate() throws ValidateException
		{
			if(StringUtils.isBlank(url))
			{
				throw new ValidateException("No url specified.");
			}

			if(StringUtils.isBlank(responseTokenProp))
			{
				throw new ValidateException("No response-token property specified.");
			}

			if(params.isEmpty())
			{
				throw new ValidateException("No params specified.");
			}
		}
	}
	
	/**
	 * Property name for setting smtp host.
	 */
	public static final String PROP_SMTP_HOST = "mail.smtp.host";
	
	/**
	 * Property for setting smtp port.
	 */
	public static final String PROP_SMTP_PORT = "mail.smtp.port";
	
	/**
	 * Property indicating if auth is enabled or not.
	 */
	public static final String PROP_USE_AUTH = "mail.smtp.auth";
	
	/**
	 * Property indicating if TTLS should be used.
	 */
	public static final String PROP_ENABLE_TTLS = "mail.smtp.starttls.enable";
	
	public static final String PROP_ENABLE_SSL = "mail.smtp.ssl.enable";

	/**
	* Smtp host.
	*/
	private String smtpHost;
	
	/**
	 * Smtp port.
	 */
	private Integer smtpPort;

	/**
	 * User name for authentication.
	 */
	private String userName;
	
	/**
	 * Password for authentication.
	 */
	private String password;

	/**
	 * Flag indicating if ttls should be enabled.
	 */
	private boolean enableTtls = false;
	
	/**
	 * Flag to enable ssl.
	 */
	private boolean enableSsl = false;
	
	/**
	 * Protocol to be used for reading mails.
	 */
	private MailReadProtocol readProtocol;
	
	/**
	 * Host address from where mail can be read or deleted.
	 */
	private String readHost;
	
	/**
	 * Port to be used for reading mails.
	 */
	private int readPort;
	
	private String oauthClientId;
	
	private String oauthSecretKey;
	
	private String oauthToken;
	
	private RefreshSettings oauthRefreshSettings;
	
	/**
	 * Gets the smtp host.
	 *
	 * @return the smtp host
	 */
	public String getSmtpHost()
	{
		return smtpHost;
	}

	/**
	 * Sets the smtp host.
	 *
	 * @param smtpHost the new smtp host
	 */
	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
	}

	/**
	 * Gets the smtp port.
	 *
	 * @return the smtp port
	 */
	public Integer getSmtpPort()
	{
		return smtpPort;
	}

	/**
	 * Sets the smtp port.
	 *
	 * @param smtpPort the new smtp port
	 */
	public void setSmtpPort(Integer smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	/**
	 * Gets the user name for authentication.
	 *
	 * @return the user name for authentication
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the user name for authentication.
	 *
	 * @param userName the new user name for authentication
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the password for authentication.
	 *
	 * @return the password for authentication
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password for authentication.
	 *
	 * @param password the new password for authentication
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Checks if is flag indicating if ttls should be enabled.
	 *
	 * @return the flag indicating if ttls should be enabled
	 */
	public boolean isEnableTtls()
	{
		return enableTtls;
	}

	/**
	 * Sets the flag indicating if ttls should be enabled.
	 *
	 * @param enableTtls the new flag indicating if ttls should be enabled
	 */
	public void setEnableTtls(boolean enableTtls)
	{
		this.enableTtls = enableTtls;
	}
	
	/**
	 * Gets the protocol to be used for reading mails.
	 *
	 * @return the protocol to be used for reading mails
	 */
	public MailReadProtocol getReadProtocol()
	{
		return readProtocol;
	}

	/**
	 * Sets the protocol to be used for reading mails.
	 *
	 * @param readProtocol the new protocol to be used for reading mails
	 */
	public void setReadProtocol(MailReadProtocol readProtocol)
	{
		this.readProtocol = readProtocol;
	}

	/**
	 * Gets the host address from where mail can be read or deleted.
	 *
	 * @return the host address from where mail can be read or deleted
	 */
	public String getReadHost()
	{
		return readHost;
	}

	/**
	 * Sets the host address from where mail can be read or deleted.
	 *
	 * @param readHost the new host address from where mail can be read or deleted
	 */
	public void setReadHost(String readHost)
	{
		this.readHost = readHost;
	}
	
	/**
	 * Gets the port to be used for reading mails.
	 *
	 * @return the port to be used for reading mails
	 */
	public int getReadPort()
	{
		return readPort;
	}

	/**
	 * Sets the port to be used for reading mails.
	 *
	 * @param readPort the new port to be used for reading mails
	 */
	public void setReadPort(int readPort)
	{
		this.readPort = readPort;
	}

	/**
	 * Checks if is flag to enable ssl.
	 *
	 * @return the flag to enable ssl
	 */
	public boolean isEnableSsl()
	{
		return enableSsl;
	}

	/**
	 * Sets the flag to enable ssl.
	 *
	 * @param enableSsl the new flag to enable ssl
	 */
	public void setEnableSsl(boolean enableSsl)
	{
		this.enableSsl = enableSsl;
	}
	
	public String getOauthClientId()
	{
		return oauthClientId;
	}

	public void setOauthClientId(String oauthClientId)
	{
		this.oauthClientId = oauthClientId;
	}

	public String getOauthSecretKey()
	{
		return oauthSecretKey;
	}

	public void setOauthSecretKey(String oauthSecretKey)
	{
		this.oauthSecretKey = oauthSecretKey;
	}

	public String getOauthToken()
	{
		return oauthToken;
	}

	public void setOauthToken(String oauthToken)
	{
		this.oauthToken = oauthToken;
	}

	public RefreshSettings getOauthRefreshSettings()
	{
		return oauthRefreshSettings;
	}

	public void setOauthRefreshSettings(RefreshSettings oauthRefreshSettings)
	{
		this.oauthRefreshSettings = oauthRefreshSettings;
	}

	public boolean isPasswordAuthenticated()
	{
		return StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password);
	}
	
	public boolean isOauthEnabled()
	{
		return StringUtils.isNotBlank(userName) 
				&& StringUtils.isNotBlank(oauthClientId)
				&& StringUtils.isNotBlank(oauthSecretKey)
				&& StringUtils.isNotBlank(oauthToken);
	}

	/**
	* Validates required configuration params are provided.
	*/
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isEmpty(smtpHost))
		{
			throw new ValidateException("No SMTP host is provided");
		}

		if(StringUtils.isEmpty(userName))
		{
			throw new ValidateException("No Username is provided");
		}
	}

	/**
	* Converts this configuration into properties compatible with java-mail.
	*
	* @return Java mail compatible properties.
	*/
	public Properties toProperties(boolean forRead)
	{
		Properties props = new Properties();
		
		if(isOauthEnabled())
		{
			if(forRead)
			{
				 props.put("mail.imap.ssl.enable", "true");
				 props.put("mail.imap.auth.mechanisms", "XOAUTH2");
			}
			else
			{
				 props.put("mail.smtp.ssl.enable", "true");
				 props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
			}
			
			return props;
		}
		
		if(forRead && readProtocol == MailReadProtocol.POP3S)
		{
			props.put("mail.store.protocol", "pop3s");
			props.put("mail.pop3s.host", smtpHost);
			props.put("mail.pop3s.port", "" + smtpPort);
			props.put("mail.pop3s.auth", "true");
			props.put("mail.pop3s.socketFactory.class", SSLSocketFactory.class.getName());
			props.put("mail.pop3s.ssl.trust", "*");
			
			return props;
		}
		
		props.put(PROP_ENABLE_TTLS, "" + enableTtls);
		props.put("mail.smtp.starttls.required", "" + enableTtls);
		props.put(PROP_ENABLE_SSL, "" + enableSsl);
		
		props.put("mail.imaps.ssl.protocols", "TLSv1.2");
		
		props.put(PROP_SMTP_HOST, smtpHost);
		
		if(smtpPort != null)
		{
			props.put(PROP_SMTP_PORT, "" + smtpPort);
		}
		
		return props;
	}
}
