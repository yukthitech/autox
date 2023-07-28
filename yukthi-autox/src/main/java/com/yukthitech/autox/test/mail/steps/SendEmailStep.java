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
package com.yukthitech.autox.test.mail.steps;

import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.plugin.mail.EmailPlugin;
import com.yukthitech.autox.plugin.mail.MailSession;
import com.yukthitech.autox.plugin.mail.SessionTransport;

/**
 * Step to send mail, useful to send specific notifications from test cases.
 * 
 * @author akiran
 */
@Executable(name = "sendEmail", group = Group.Mail, 
	message = "Step to send mail, useful to send specific notifications from test cases.",
	requiredPluginTypes = EmailPlugin.class)
public class SendEmailStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Email server from which mail has to be sent.
	 */
	@Param(description = "Email server from which mail has to be sent. If not specified, default server will be used.", required = false)
	private String emailServerName;

	/**
	 * Id from which notification should be marked as sent.
	 */
	@Param(description = "Email-Id from which notification should be marked as sent.", required = false, sourceType = SourceType.EXPRESSION)
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
	
	public void setEmailServerName(String emailServerName)
	{
		this.emailServerName = emailServerName;
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
		exeLogger.debug("Sending email with [From: {}, Subject: {}]", fromAddress, subject);
		MailSession mailSession = ExecutionContextManager.getInstance().getPluginSession(EmailPlugin.class);
		
		SessionTransport session = mailSession.getSessionForSend(emailServerName);
		MimeMessage msg = new MimeMessage(session.getSession());
	
		// set message headers
		msg.addHeader("Content-type", "text/HTML; charset=UTF-8");

		if(fromAddress != null)
		{
			msg.setFrom(new InternetAddress(fromAddress));
		}
		
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
		
		
		//msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddressList, false));
		
		session.getTransport().sendMessage(msg, InternetAddress.parse(toAddressList, false));
		exeLogger.debug("Mail is sent successfully");
	}
}
