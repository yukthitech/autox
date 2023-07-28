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

import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;

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

/**
 * Step to delete mails.
 * 
 * @author akiran
 */
@Executable(name = "deleteEmails", group = Group.Mail, message = "Step to delete mails.", requiredPluginTypes = EmailPlugin.class)
public class DeleteEmailsStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	private static final String FOLDER_NAME = "INBOX";
	
	/**
	 * Email server from which mails has to be read.
	 */
	@Param(description = "Email server from which mails has to be deleted. If not specified, default server will be used.", required = false)
	private String emailServerName;
	
	/**
	 * Email-Id pattern of mails to be read.
	 */
	@Param(description = "Email-Id pattern of mails to be deleted.", required = false, sourceType = SourceType.EXPRESSION)
	private String fromAddressPattern;

	/**
	 * Subject to be used for sending mail.
	 */
	@Param(description = "Subject pattern of mails to be deleted.", required = true, sourceType = SourceType.EXPRESSION)
	private String subjectPattern;
	
	@Param(description = "Name of attribute to which delete count should be populated. Default: deleteCount", required = false, sourceType = SourceType.EXPRESSION, attrName = true)
	private String deleteCountAttribute = "deleteCount";
	
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
	
	public void setDeleteCountAttribute(String deleteCountAttribute)
	{
		this.deleteCountAttribute = deleteCountAttribute;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		exeLogger.debug("Deleting mails from folder: INBOX.");
		
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
		
		mailFolder.open(Folder.READ_WRITE);

		Message newMessages[] = mailFolder.getMessages();

		if(newMessages.length <= 0)
		{
			exeLogger.debug("No new messages are found. Total messages: {}", 
					mailFolder.getMessageCount());
			return;
		}
		else
		{
			exeLogger.debug("Number of messages found: {}", newMessages.length);
		}

		int count = 0;
		
		for(Message mssg : newMessages)
		{
			if(subjPattern != null)
			{
				if(!subjPattern.matcher(mssg.getSubject()).matches())
				{
					continue;
				}
			}
			
			if(fromPattern != null)
			{
				Address from[] = mssg.getFrom();
				
				if(from == null || from.length == 0)
				{
					continue;
				}
				
				String nameAndId[] = MailUtil.extractNameAndId(from[0].toString());

				if(!fromPattern.matcher(nameAndId[1]).matches())
				{
					continue;
				}
			}
			
			mssg.setFlag(Flags.Flag.DELETED, true);
			count++;
		}
		
		context.setAttribute(deleteCountAttribute, count);
		exeLogger.debug("Number of messages deleted with required pattern: {}", count);
		mailFolder.close(true);
	}
}
