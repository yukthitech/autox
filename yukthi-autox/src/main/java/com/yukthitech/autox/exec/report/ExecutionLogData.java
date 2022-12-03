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

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.test.TestStatus;

/**
 * Log data of the test case.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionLogData
{
	/**
	 * Represent log message information.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Message implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Source location from where logging was done.
		 */
		private String source;

		/**
		 * Java source location where logging was done.
		 */
		private String javaSource;

		/**
		 * Log level at which message is logged.
		 */
		private LogLevel logLevel;

		/**
		 * Log message.
		 */
		private String message;

		/**
		 * Time at which message is logged.
		 */
		private Date time;

		/**
		 * Instantiates a new message.
		 *
		 * @param logLevel the log level
		 * @param message the message
		 * @param time the time
		 */
		public Message(String source, String javaSource, LogLevel logLevel, String message, Date time)
		{
			this.source = source != null ? source : javaSource;
			this.javaSource = javaSource;
			this.logLevel = logLevel;
			this.message = message;
			this.time = time;
		}

		public Message()
		{}

		public String getType()
		{
			return "Message";
		}

		public void setSource(String source)
		{
			this.source = source;
		}

		public void setJavaSource(String javaSource)
		{
			this.javaSource = javaSource;
		}

		public void setLogLevel(LogLevel logLevel)
		{
			this.logLevel = logLevel;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}

		public void setTime(Date time)
		{
			this.time = time;
		}

		/**
		 * Gets the source location from where logging was done.
		 *
		 * @return the source location from where logging was done
		 */
		public String getSource()
		{
			return source;
		}

		/**
		 * Gets the java source location where logging was done.
		 *
		 * @return the java source location where logging was done
		 */
		public String getJavaSource()
		{
			return javaSource;
		}

		/**
		 * Gets the log level at which message is logged.
		 *
		 * @return the log level at which message is logged
		 */
		public LogLevel getLogLevel()
		{
			return logLevel;
		}

		/**
		 * Gets the log message.
		 *
		 * @return the log message
		 */
		public String getMessage()
		{
			return message;
		}

		/**
		 * Gets the time at which message is logged.
		 *
		 * @return the time at which message is logged
		 */
		public Date getTime()
		{
			return time;
		}

		/**
		 * Gets the time at which message is logged.
		 *
		 * @return the time at which message is logged
		 */
		public String getTimeStr()
		{
			return ApplicationConfiguration.getInstance().getTimeFormatObject().format(time);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ImageMessage extends Message
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Name of the image.
		 */
		private String name;

		/**
		 * Image file.
		 */
		private ReportLogFile imageFile;

		/**
		 * Instantiates a new image message.
		 *
		 * @param logLevel the log level
		 * @param message the message
		 * @param time the time
		 * @param imageFile the image file
		 */
		public ImageMessage(String source, String javaSource, LogLevel logLevel, String message, Date time, String name, ReportLogFile imageFile)
		{
			super(source, javaSource, logLevel, message, time);

			this.name = name;
			this.imageFile = imageFile;
		}

		public ImageMessage()
		{}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public void setImageFile(ReportLogFile imageFile)
		{
			this.imageFile = imageFile;
		}

		/**
		 * Gets the image file.
		 *
		 * @return the image file
		 */
		@JsonIgnore
		public ReportLogFile getImageFile()
		{
			return imageFile;
		}

		/**
		 * Fetches the target image file name.
		 * 
		 * @return image file name.
		 */
		public String getImageFileName()
		{
			return imageFile.getFile().getName();
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class FileMessage extends Message
	{
		private static final long serialVersionUID = 1L;

		/**
		 * file.
		 */
		private ReportLogFile file;

		/**
		 * Instantiates a new image message.
		 *
		 * @param logLevel the log level
		 * @param message the message
		 * @param time the time
		 * @param imageFile the image file
		 */
		public FileMessage(String source, String javaSource, LogLevel logLevel, String message, Date time, ReportLogFile file)
		{
			super(source, javaSource, logLevel, message, time);
			this.file = file;
		}

		public FileMessage()
		{}

		/**
		 * Gets the image file.
		 *
		 * @return the image file
		 */
		@JsonIgnore
		public ReportLogFile getFile()
		{
			return file;
		}

		/**
		 * Fetches the target file name.
		 * 
		 * @return file name.
		 */
		public String getFileName()
		{
			return file.getFile().getName();
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Header
	{
		/**
		 * Name of the executor.
		 */
		private String executorName;

		/**
		 * Executor description.
		 */
		private String executorDescription;

		/**
		 * Start time of this execution.
		 */
		private Date startTime = new Date();

		public Header()
		{}

		public Header(String executorName, String executorDescription)
		{
			this.executorName = executorName;
			this.executorDescription = executorDescription;
		}

		public String getExecutorName()
		{
			return executorName;
		}

		public void setExecutorName(String executorName)
		{
			this.executorName = executorName;
		}

		public String getExecutorDescription()
		{
			return executorDescription;
		}

		public void setExecutorDescription(String executorDescription)
		{
			this.executorDescription = executorDescription;
		}

		public Date getStartTime()
		{
			return startTime;
		}

		public void setStartTime(Date startTime)
		{
			this.startTime = startTime;
		}

		public String getStartTimeStr()
		{
			return DateFormatUtils.format(startTime, ApplicationConfiguration.getInstance().getDateTimeFomat());
		}

		public String getType()
		{
			return "Header";
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Footer
	{
		/**
		 * Status of the execution.
		 */
		private TestStatus status;

		/**
		 * Start time of this execution.
		 */
		private Date endTime;
		
		/**
		 * Time taken by execution.
		 */
		private String timeTaken;
		
		public Footer()
		{}

		public Footer(TestStatus status, Date startTime, Date endTime)
		{
			this.status = status;
			this.endTime = endTime;
			
			this.timeTaken = AutomationUtils.getTimeTaken(startTime, endTime);
		}

		public TestStatus getStatus()
		{
			return status;
		}

		public void setStatus(TestStatus status)
		{
			this.status = status;
		}

		public Date getEndTime()
		{
			return endTime;
		}

		public void setEndTime(Date endTime)
		{
			this.endTime = endTime;
		}

		public String getEndTimeStr()
		{
			return ApplicationConfiguration.getInstance().getTimeFormatObject().format(endTime);
		}
		
		public String getTimeTaken()
		{
			return timeTaken;
		}

		public void setTimeTaken(String timeTaken)
		{
			this.timeTaken = timeTaken;
		}

		public String getType()
		{
			return "Footer";
		}
	}
}
