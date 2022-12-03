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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.LogLevel;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Logs the specified message using execution logger.
 * @author akiran
 */
@Executable(name = "log", group = Group.Common, message = "Logs specified message. Multiple messages can be specified in single log statement. "
		+ "If non-string or non-primitive values are specified they are converted to json before printing.")
public class LogStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Object mapper to print json from objects.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Logs specified message in ui.
	 */
	@Param(name = "message", description = "Message(s)/object(s) to log", sourceType = SourceType.EXPRESSION)
	private List<Object> messages = new ArrayList<>();
	
	/**
	 * Logging level.
	 */
	@Param(description = "Logging level. Default Value: DEBUG", required = false)
	private LogLevel level = LogLevel.DEBUG;
	
	/**
	 * Sets the logs specified message in ui.
	 *
	 * @param message the new logs specified message in ui
	 */
	public void addMessage(Object message)
	{
		this.messages.add(message);
	}
	
	/**
	 * Sets the logging level.
	 *
	 * @param level the new logging level
	 */
	public void setLevel(LogLevel level)
	{
		this.level = level;
	}
	
	/**
	 * Adds the message to specified builder.
	 * @param message message to be added
	 * @param builder Final result builder
	 */
	private void appendMessage(Object message, StringBuilder builder)
	{
		if(builder.length() > 0)
		{
			builder.append("\n");
		}
		
		if(message == null)
		{
			builder.append("null");
			return;
		}
		
		if(message instanceof String)
		{
			builder.append((String) message);
			return;
		}
		
		if(CommonUtils.isWrapperClass(message.getClass()))
		{
			builder.append("" + message);
			return;
		}
		
		try
		{
			String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
			builder.append("<code class='JSON'>").append(jsonContent).append("</code>");
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting object into json. Object: {}", message, ex);
		}
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		StringBuilder finalMssg = new StringBuilder();
		
		for(Object message : messages)
		{
			appendMessage(message, finalMssg);
		}
		
		if(level == LogLevel.DEBUG)
		{
			exeLogger.debug(false, finalMssg.toString());
		}
		else if(level == LogLevel.ERROR)
		{
			exeLogger.error(false, finalMssg.toString());
		}
		else if(level == LogLevel.TRACE)
		{
			exeLogger.trace(false, finalMssg.toString());
		}
		else
		{
			exeLogger.log(false, level, finalMssg.toString());
		}
	}
}
