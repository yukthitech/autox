package com.yukthitech.autox.plugin.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.plugin.AbstractPluginSession;
import com.yukthitech.autox.plugin.mail.EmailServerSettings.RefreshSettings;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.PostRestRequest;
import com.yukthitech.utils.rest.RestClient;
import com.yukthitech.utils.rest.RestResult;

public class MailSession extends AbstractPluginSession<MailSession, EmailPlugin>
{
	private static Logger logger = LogManager.getLogger(MailSession.class);
	
	private static class SessionDetails
	{
		private Session readSession;
		
		private Session sendSession;
		
		private SessionTransport sendSessionTransport;
		
		private Store store;
		
		private RestClient refreshRestClient;
		
		private PostRestRequest refreshRequest;
	}
	
	private Map<String, SessionDetails> nameToDetails = new HashMap<>();
	
	public MailSession(EmailPlugin parentPlugin)
	{
		super(parentPlugin);
	}
	
	private Session newSession(String name, boolean read)
	{
		EmailServerSettings settings = super.parentPlugin.getSettings(name);
		
		Session mailSession = null;
		Properties configProperties = settings.toProperties(read);
		
		logger.debug("Connecting to mail-server '{}' using properties: {}", name, configProperties);

		// if authentication needs to be done provide user name and password
		if(settings.isPasswordAuthenticated())
		{
			mailSession = Session.getInstance(configProperties, new Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(settings.getUserName(), settings.getPassword());
				}
			});
		}
		else
		{
			mailSession = Session.getInstance(configProperties);
		}
		
		return mailSession;
	}
	
	private Session getSession(String name, boolean read)
	{
		SessionDetails sessionDet = nameToDetails.get(name);
		
		if(sessionDet == null)
		{
			sessionDet = new SessionDetails();
			nameToDetails.put(name, sessionDet);
		}
		
		Session session = read ? sessionDet.readSession : sessionDet.sendSession;
		
		if(session != null)
		{
			return session;
		}
		
		session = newSession(name, read);
		
		if(read)
		{
			sessionDet.readSession = session;
		}
		else
		{
			sessionDet.sendSession = session;
		}
		
		return session;

	}
	
	public SessionTransport getSessionForSend(String name)
	{
		Session session = getSession(name, false);
		SessionDetails sessionDet = nameToDetails.get(name);
		
		try
		{
			if(sessionDet.sendSessionTransport != null)
			{
				if(sessionDet.sendSessionTransport.getTransport().isConnected())
				{
					return sessionDet.sendSessionTransport;
				}
				
				sessionDet.sendSessionTransport.getTransport().close();
				sessionDet.sendSessionTransport = null;
			}
			
			EmailServerSettings settings = super.parentPlugin.getSettings(name);
			Transport transport = session.getTransport();
			
			if(settings.isOauthEnabled())
			{
				transport.connect(settings.getSmtpHost(), settings.getSmtpPort(), settings.getUserName(), generateOauthToken(settings, sessionDet));
			}
			
			sessionDet.sendSessionTransport = new SessionTransport(session, transport);
			return sessionDet.sendSessionTransport;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating mail session", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	private String generateOauthToken(EmailServerSettings settings, SessionDetails sessionDet) throws Exception
	{
		RefreshSettings refreshSettings = settings.getOauthRefreshSettings();
		
		if(refreshSettings == null)
		{
			return settings.getOauthToken();
		}
		
		if(sessionDet.refreshRestClient == null)
		{
			sessionDet.refreshRestClient = new RestClient(refreshSettings.getUrl());
			sessionDet.refreshRequest = new PostRestRequest("/");
			
			for(Map.Entry<String, String> paramEntry : refreshSettings.getParams().entrySet())
			{
				String value = FreeMarkerMethodManager.replaceExpressions("refeshParam", settings, paramEntry.getValue());
				sessionDet.refreshRequest.addParam(paramEntry.getKey(), value);
			}
			
			sessionDet.refreshRequest.setBody("");
		}
		
		RestResult<Object> result = sessionDet.refreshRestClient.invokeJsonRequest(sessionDet.refreshRequest, Object.class);
		
		if(result.getValue() == null)
		{
			throw new InvalidStateException("Failed to invoke refresh url for new token. [Status: {}, Parse Error: {}]", result.getStatusCode(), result.getParseError());
		}
		
		Map<String, Object> responseMap = (Map<String, Object>) result.getValue();
		
		return (String) PropertyUtils.getProperty(responseMap, refreshSettings.getResponseTokenProp());
	}
	
	public Store getStoreForRead(String name)
	{
		//First get session so that details object also gets created
		//  even if it does not exist
		Session session = getSession(name, true);
		SessionDetails sessionDet = nameToDetails.get(name);
		
		if(sessionDet.store != null)
		{
			if(sessionDet.store.isConnected())
			{
				return sessionDet.store;
			}
			
			try
			{
				sessionDet.store.close();
			}catch(Exception ex)
			{
				logger.warn("[Ignored] An error occurred while closing disconnected session. Error: " + ex);
			}
			
			sessionDet.store = null;
		}
		
		EmailServerSettings settings = super.parentPlugin.getSettings(name);
		
		try
		{
			MailReadProtocol readProtocol = settings.getReadProtocol();
			boolean oatuhEnabled = settings.isOauthEnabled();
			String password = settings.getPassword();
			
			if(oatuhEnabled)
			{
				readProtocol = MailReadProtocol.IMAP;
				password = generateOauthToken(settings, sessionDet);
			}
			
			Store store = session.getStore(readProtocol.getName());
			store.connect(settings.getReadHost(), settings.getReadPort(), 
					settings.getUserName(), 
					password);
			
			sessionDet.store = store;
			return store;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching store", ex);
		}
	}
	
	@Override
	public void close()
	{
		try
		{
			for(SessionDetails det : nameToDetails.values())
			{
				if(det.store != null)
				{
					det.store.close();
				}
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while closing mail stores", ex);
		}
	}

}
