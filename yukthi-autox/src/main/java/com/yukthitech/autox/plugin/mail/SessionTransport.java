package com.yukthitech.autox.plugin.mail;

import javax.mail.Session;
import javax.mail.Transport;

public class SessionTransport
{
	private Session session;
	
	private Transport transport;

	public SessionTransport(Session session, Transport transport)
	{
		this.session = session;
		this.transport = transport;
	}

	public Session getSession()
	{
		return session;
	}

	public Transport getTransport()
	{
		return transport;
	}
}
