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
package com.yukthitech.autox.test.common.steps;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Step to send mail, useful to send specific notifications from test cases.
 * 
 * @author akiran
 */
@Executable(name = "sendMail", group = Group.Common, message = "Step to send mail, useful to send specific notifications from test cases.")
public class SendMailStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Smtp server host.
	 */
	@Param(description = "Smtp host to be used for sending mails", required = true, sourceType = SourceType.EXPRESSION)
	private String smptpHost;

	/**
	 * Smtp server port.
	 */
	@Param(description = "Smtp port to be used for sending mails.", required = true, defaultValue = "587")
	private Integer smptpPort = 587;

	/**
	 * Flag indicating ttls enabled or not.
	 */
	@Param(description = "Flag indicating ttls enabled or not.", required = false, defaultValue = "false")
	private boolean ttlsEnabled = true;

	/**
	 * Smtp user name to be used.
	 */
	@Param(description = "Smtp user name to be used for authentication. If not specified, authentication will not be done.", required = false)
	private String userName;

	/**
	 * Smtp password to be used.
	 */
	@Param(description = "Smtp password to be used for authentication. If not specified, authentication will not be done.", required = false)
	private String password;

	/**
	 * Id from which notification should be marked as sent.
	 */
	@Param(description = "Email-Id from which notification should be marked as sent.", required = true, sourceType = SourceType.EXPRESSION)
	private String fromAddress;

	/**
	 * Space separated address list to which notification should be sent.
	 */
	@Param(description = "Space separated address list to which notification should be sent.", required = true, sourceType = SourceType.EXPRESSION)
	private String toAddressList;

	/**
	 * Subject to be used for sending mail.
	 */
	@Param(description = "Subject to be used for sending mail.", required = true, sourceType = SourceType.EXPRESSION)
	private String subject;

	/**
	 * Content to be used for sending mail.
	 */
	@Param(description = "Content to be used for sending mail.", required = true, sourceType = SourceType.EXPRESSION)
	private String content;

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
	 * Sets the smtp server port.
	 *
	 * @param smptpPort the new smtp server port
	 */
	public void setSmptpPort(Integer smptpPort)
	{
		this.smptpPort = smptpPort;
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
	 * Sets the smtp user name to be used.
	 *
	 * @param userName the new smtp user name to be used
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
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
	 * Sets the id from which notification should be marked as sent.
	 *
	 * @param fromAddress the new id from which notification should be marked as sent
	 */
	public void setFromAddress(String fromAddress)
	{
		this.fromAddress = fromAddress;
	}

	/**
	 * Sets the comma separated address list to which notification should be sent.
	 *
	 * @param toAddressList the new comma separated address list to which notification should be sent
	 */
	public void setToAddressList(String toAddressList)
	{
		this.toAddressList = toAddressList;
	}

	/**
	 * Sets the subject to be used for sending mail.
	 *
	 * @param subject the new subject to be used for sending mail
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 * Sets the content to be used for sending mail.
	 *
	 * @param content the new content to be used for sending mail
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * Execute.
	 *
	 * @param context the context
	 * @param exeLogger the exe logger
	 * @return true, if successful
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", smptpHost);
		props.put("mail.smtp.port", "" + smptpPort);
		
		boolean authEnabled = StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password);
		props.put("mail.smtp.auth", "" + authEnabled);
		props.put("mail.smtp.starttls.enable", "" + ttlsEnabled);

		Authenticator auth = null;

		// create Authenticator object to pass in Session.getInstance argument
		if(authEnabled)
		{
			auth = new Authenticator()
			{
				// override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(userName, password);
				}
			};
		}

		Session session = Session.getInstance(props, auth);
		MimeMessage msg = new MimeMessage(session);
	
		// set message headers
		msg.addHeader("Content-type", "text/HTML; charset=UTF-8");

		msg.setFrom(new InternetAddress(fromAddress));
		msg.setReplyTo(InternetAddress.parse(toAddressList, false));
		msg.setSubject(subject, "UTF-8");
		msg.setSentDate(new Date());

		// create multi part message
		Multipart multiPart = new MimeMultipart();

		// add body to multi part
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(content, "text/html");
		multiPart.addBodyPart(messageBodyPart);
		
		// set the multi part message as content
		msg.setContent(multiPart);
		
		
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddressList, false));
		Transport.send(msg);
	}
}
