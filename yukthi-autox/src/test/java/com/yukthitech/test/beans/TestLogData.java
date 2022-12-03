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
package com.yukthitech.test.beans;

import java.util.List;

import com.yukthitech.autox.exec.report.ExecutionLogData;

public class TestLogData
{
	public static class ImgMessage extends  ExecutionLogData.Message
	{
		private static final long serialVersionUID = 1L;
		
		private String imageFileName;

		public String getImageFileName()
		{
			return imageFileName;
		}

		public void setImageFileName(String imageFileName)
		{
			this.imageFileName = imageFileName;
		}
	}
	
	private ExecutionLogData.Header header;
	
	private List<ImgMessage> messages;
	
	private ExecutionLogData.Footer footer;

	public ExecutionLogData.Header getHeader()
	{
		return header;
	}

	public void setHeader(ExecutionLogData.Header header)
	{
		this.header = header;
	}

	public List<ImgMessage> getMessages()
	{
		return messages;
	}

	public void setMessages(List<ImgMessage> messages)
	{
		this.messages = messages;
	}

	public ExecutionLogData.Footer getFooter()
	{
		return footer;
	}

	public void setFooter(ExecutionLogData.Footer footer)
	{
		this.footer = footer;
	}
}
