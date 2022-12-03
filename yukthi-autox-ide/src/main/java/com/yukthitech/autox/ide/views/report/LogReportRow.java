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
package com.yukthitech.autox.ide.views.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.yukthitech.autox.exec.report.LogLevel;

public class LogReportRow implements IReportRow
{
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss aa");
	
	private LogLevel logLevel;
	private String source;
	private String message;
	private Date time;

	public LogReportRow(LogLevel logLevel, String source, String message, Date time)
	{
		this.logLevel = logLevel;
		this.source = source;
		this.message = message;
		this.time = time;
	}

	public LogLevel getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel)
	{
		this.logLevel = logLevel;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}
	
	@Override
	public Object getValueAt(int col)
	{
		switch (col)
		{
			case 0:
			{
				String style = (logLevel == LogLevel.ERROR) ? "color:red;" : "";
				
				String str = String.format(
						"<span style='%s'>%s</span> %s [%s]", 
						style, 
						logLevel.getPaddedString(), 
						TIME_FORMAT.format(time), 
						source, 
						source);
				
				return str.replace("'", "\"");
			}
			case 1:
				String message = this.message;
				
				if(logLevel == LogLevel.ERROR)
				{
					message = "<div style='color:red;'>" + message + "</div>";
				}
				
				message = message.replace("\n", "<br/>");
				message = message.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
				message = message.replace("<code", "<div style='padding: 10px; margin: 5px 0px 5px 30px; border: 1px solid black;' ");
				message = message.replace("</code>", "</div>");
				
				return message.replace("'", "\"");
		}
		
		return "";
	}
}
