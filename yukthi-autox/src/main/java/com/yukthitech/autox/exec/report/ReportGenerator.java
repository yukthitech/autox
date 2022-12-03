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
package com.yukthitech.autox.exec.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.config.SummaryNotificationConfig;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.test.ResourceManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Responsible for generating output reports.
 * 
 * @author akiran
 */
public class ReportGenerator
{
	private static final String SUMMARY_REPORT_TEMPLATE = "/summary-report-template.html";

	private static Logger logger = LogManager.getLogger(ReportGenerator.class);

	/**
	 * Used to generate json files.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();

	public void generateReports(FinalReport finalReport)
	{
		AutomationContext automationContext = AutomationContext.getInstance();
		File reportFolder = automationContext.getReportFolder();
		
		ApplicationConfiguration applicationConfiguration = automationContext.getAppConfiguration();
		
		/*
		List<String> summaryMessages = automationContext.getSummaryMessages();
		
		if(summaryMessages != null && !summaryMessages.isEmpty())
		{
			//fullExecutionDetails.setSummaryMessages(automationContext.getSummaryMessages());
		}
		*/
		
		// copy the resource files into output folder
		ResourceManager.getInstance().copyReportResources(reportFolder);

		// create final report files
		try
		{
			String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalReport);
			String jsContent = "var reportData = " + jsonContent;

			FileUtils.write(new File(reportFolder, "test-results.json"), jsonContent, Charset.defaultCharset());
			FileUtils.write(new File(reportFolder, "test-results.js"), jsContent, Charset.defaultCharset());
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while generating test result report");
		}

		// generate summary report
		try
		{
			Map<String, Object> context = new HashMap<>();
			context.put("report", finalReport);
			context.put("reportFolder", reportFolder.getPath());
			context.put("reportFolderName", reportFolder.getName());
			context.put("context", automationContext);
			
			SummaryNotificationConfig summaryNotificationConfig = applicationConfiguration.getSummaryNotificationConfig();
			summaryNotificationConfig = (summaryNotificationConfig != null && summaryNotificationConfig.isEnabled()) ? summaryNotificationConfig : null;
			
			//generate header and footer content and place in context which in turn will be used in summary report template
			if(summaryNotificationConfig != null)
			{
				generateHeaderAndFooter(context, summaryNotificationConfig);
			}

			String reportTemplate = IOUtils.toString(ReportGenerator.class.getResourceAsStream(SUMMARY_REPORT_TEMPLATE), Charset.defaultCharset());
			String summaryResult = FreeMarkerMethodManager.replaceExpressions("reportTemplate", context, reportTemplate);

			File summaryHtml = new File(reportFolder, "summary-report.html");
			FileUtils.write(summaryHtml, summaryResult, Charset.defaultCharset());

			//send notification mail
			if(summaryNotificationConfig != null)
			{
				sendSummaryMail(summaryNotificationConfig, summaryHtml, context);
			}
			else
			{
				logger.debug("Summary mail is disabled. Hence skipping sending summary mail..");
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while generating summary report", ex);
		}
		
	}
	
	private void generateHeaderAndFooter(Map<String, Object> context, SummaryNotificationConfig notificationConfig) throws IOException
	{
		File headerFile = notificationConfig.getHeaderTemplateFile() != null ? new File(notificationConfig.getHeaderTemplateFile()) : null;
		File footerFile = notificationConfig.getHeaderTemplateFile() != null ? new File(notificationConfig.getFooterTemplateFile()) : null;
		
		context.put("headerContent", processTemplateFile(context, headerFile, "header"));
		context.put("footerContent", processTemplateFile(context, footerFile, "footer"));
	}
	
	private String processTemplateFile(Map<String, Object> context, File file, String name) throws IOException
	{
		if(file == null)
		{
			return "";
		}
		
		if(!file.exists())
		{
			logger.warn("For summary-report specified {} template file does not exist: {}", name, file.getPath());
			return "";
		}
		
		String content = FileUtils.readFileToString(file, Charset.defaultCharset());
		return FreeMarkerMethodManager.replaceExpressions("summary-" + name + "-template", context, content);
	}

	private void sendSummaryMail(final SummaryNotificationConfig notificationConfig, File summaryHtml, Object freeMarkerContext) throws MessagingException, IOException
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", notificationConfig.getSmptpHost()); // SMTP Host
		props.put("mail.smtp.port", "" + notificationConfig.getSmptpPort()); // TLS Port
		props.put("mail.smtp.auth", "" + notificationConfig.isAuthEnabled()); // enable authentication
		props.put("mail.smtp.starttls.enable", "" + notificationConfig.isTtlsEnabled()); // enable STARTTLS
		props.put("mail.smtp.ssl.enable", "" + notificationConfig.isEnableSsl());

		Authenticator auth = null;

		// create Authenticator object to pass in Session.getInstance argument
		if(notificationConfig.isAuthEnabled())
		{
			auth = new Authenticator()
			{
				// override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(notificationConfig.getUserName(), notificationConfig.getPassword());
				}
			};
		}

		Session session = Session.getInstance(props, auth);
		MimeMessage msg = new MimeMessage(session);
	
		// set message headers
		msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
		//msg.addHeader("format", "flowed");
		//msg.addHeader("Content-Transfer-Encoding", "8bit");

		msg.setFrom(new InternetAddress(notificationConfig.getFromAddress()));
		msg.setReplyTo(InternetAddress.parse(notificationConfig.getToAddressList(), false));
		msg.setSubject(FreeMarkerMethodManager.replaceExpressions("notificaton-subject", freeMarkerContext, notificationConfig.getSubjectTemplate()), "UTF-8");
		msg.setSentDate(new Date());

		// create multi part message
		Multipart multiPart = new MimeMultipart();

		// add body to multi part
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(FileUtils.readFileToString(summaryHtml, Charset.defaultCharset()), "text/html");
		multiPart.addBodyPart(messageBodyPart);
		
		// set the multi part message as content
		msg.setContent(multiPart);
		
		
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(notificationConfig.getToAddressList(), false));
		Transport.send(msg);
		
		logger.debug("Summary mail is sent successfully!");
	}
}
