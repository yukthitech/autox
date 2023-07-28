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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.InvalidArgumentException;

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
import com.yukthitech.autox.test.mail.steps.MailMessage.Attachment;
import com.yukthitech.autox.test.mail.steps.MailMessage.NameAndMailId;

/**
 * Step to read mails.
 * 
 * @author akiran
 */
@Executable(name = "readEmails", group = Group.Mail, message = "Step to read mails.", requiredPluginTypes = EmailPlugin.class)
public class ReadEmailsStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	private static final String FOLDER_NAME = "INBOX";
	
	/**
	 * Email server from which mails has to be read.
	 */
	@Param(description = "Email server from which mails has to be read. If not specified, default server will be used.", required = false)
	private String emailServerName;
	
	@Param(description = "Attribute name to which read mails has to be set. Default: readMails", required = false, sourceType = SourceType.EXPRESSION)
	private String mailAttribute = "readMails";

	/**
	 * Email-Id pattern of mails to be read.
	 */
	@Param(description = "Email-Id pattern of mails to be read.", required = false, sourceType = SourceType.EXPRESSION)
	private String fromAddressPattern;

	/**
	 * Subject to be used for sending mail.
	 */
	@Param(description = "Subject pattern of mails to be read.", required = false, sourceType = SourceType.EXPRESSION)
	private String subjectPattern;
	
	public void setEmailServerName(String emailServerName)
	{
		this.emailServerName = emailServerName;
	}

	public void setFromAddressPattern(String fromAddressPattern)
	{
		this.fromAddressPattern = fromAddressPattern;
	}

	public void setSubjectPattern(String subjectPattern)
	{
		this.subjectPattern = subjectPattern;
	}
	
	public void setMailAttribute(String mailAttribute)
	{
		this.mailAttribute = mailAttribute;
	}

	private List<NameAndMailId> parseAddresses(Address addresses[])
	{
		if(addresses == null || addresses.length == 0)
		{
			return null;
		}
		
		List<NameAndMailId> res = new ArrayList<>(addresses.length);
		
		for(Address add : addresses)
		{
			String nameAndId[] = MailUtil.extractNameAndId(add.toString());
			res.add(new NameAndMailId(nameAndId[0], nameAndId[1]));
		}
		
		return res;
	}
	
	private void extractMailContent(MailMessage mailMessage, Object content, String contentType) throws MessagingException, IOException
	{
		if(!contentType.toLowerCase().contains("multipart"))
		{
			mailMessage.setContent(content.toString());
			return;
		}

		Multipart multipart = (Multipart) content;
		int count = multipart.getCount();
		BodyPart part = null;
		File attachmentFile = null;

		for(int i = 0; i < count; i++)
		{
			part = multipart.getBodyPart(i);

			if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
			{
				attachmentFile = File.createTempFile(part.getFileName(), ".attachment");
				((MimeBodyPart) part).saveFile(attachmentFile);

				mailMessage.addAttachment(new Attachment(attachmentFile, part.getFileName()));
			}
			else if(part.getContentType().toLowerCase().contains("text/html"))
			{
				String contentStr = IOUtils.toString(part.getInputStream(), Charset.defaultCharset());
				mailMessage.setContent(contentStr);
			}
			else if(part.getContentType().toLowerCase().contains("text"))
			{
				String contentStr = IOUtils.toString(part.getInputStream(), Charset.defaultCharset());
				mailMessage.setContent(contentStr);
			}
		}
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		exeLogger.debug("Reading mails from folder: INBOX.");
		
		Pattern fromPattern = null;
		Pattern subjPattern = null;
		
		try
		{
			fromPattern = this.fromAddressPattern == null ? null : Pattern.compile(this.fromAddressPattern);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid from address pattern specified: " + fromAddressPattern, ex);
		}
		
		try
		{
			subjPattern = this.subjectPattern == null ? null : Pattern.compile(this.subjectPattern);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid subject pattern specified: " + subjectPattern, ex);
		}

		MailSession session = ExecutionContextManager.getInstance().getPluginSession(EmailPlugin.class);
		Store store = session.getStoreForRead(emailServerName);

		Folder mailFolder = store.getFolder(FOLDER_NAME);
		
		mailFolder.open(Folder.READ_ONLY);

		Message newMessages[] = mailFolder.getMessages();

		if(newMessages.length <= 0)
		{
			exeLogger.debug("No new messages are found. Total messages: {}", 
					mailFolder.getMessageCount());
			return;
		}
		else
		{
			exeLogger.debug("Number of messages read: {}", newMessages.length);
		}

		List<MailMessage> mssgLst = new LinkedList<>();
		
		for(Message mssg : newMessages)
		{
			System.out.println("Process mssg: " + mssg.getSubject());
			
			if(subjPattern != null)
			{
				if(!subjPattern.matcher(mssg.getSubject()).matches())
				{
					continue;
				}
			}
			
			MailMessage convMssg = new MailMessage();
			Address from[] = mssg.getFrom();
				
			if(from != null && from.length > 0)
			{
				String nameAndId[] = MailUtil.extractNameAndId(from[0].toString());
				
				if(fromPattern != null && !fromPattern.matcher(nameAndId[1]).matches())
				{
					continue;
				}
				
				convMssg.setFromId(new NameAndMailId(nameAndId[0], nameAndId[1]));
			}
			
			convMssg.setSubject(mssg.getSubject());
			convMssg.setToIds(parseAddresses(mssg.getRecipients(RecipientType.TO)));
			convMssg.setCcIds(parseAddresses(mssg.getRecipients(RecipientType.CC)));
			convMssg.setBccIds(parseAddresses(mssg.getRecipients(RecipientType.BCC)));
			
			convMssg.setContentType(mssg.getContentType());
			extractMailContent(convMssg, mssg.getContent(), mssg.getContentType());
			
			mssgLst.add(convMssg);
		}
		
		exeLogger.debug("Number of messages read with required pattern: {}", mssgLst.size());
		context.setAttribute(mailAttribute, mssgLst);
		mailFolder.close(false);
	}
}
