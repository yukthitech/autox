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
package com.yukthitech.autox.ide.editor;

import com.yukthitech.autox.ide.xmlfile.MessageType;

public class FileParseMessage
{
	private MessageType messageType;
	
	private String message;
	
	private int lineNo;
	
	private int startOffset = -1;
	
	private int endOffset;
	
	public FileParseMessage()
	{}

	public FileParseMessage(MessageType messageType, String message, int lineNo)
	{
		this.messageType = messageType;
		this.message = message;
		this.lineNo = lineNo;
	}
	
	public FileParseMessage(MessageType messageType, String message, int lineNo, int startOffset, int endOffset)
	{
		this.messageType = messageType;
		this.message = message;
		this.lineNo = lineNo;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	public MessageType getMessageType()
	{
		return messageType;
	}

	public void setMessageType(MessageType messageType)
	{
		this.messageType = messageType;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public int getLineNo()
	{
		return lineNo;
	}

	public void setLineNo(int lineNo)
	{
		this.lineNo = lineNo;
	}

	public int getStartOffset()
	{
		return startOffset;
	}

	public void setStartOffset(int startOffset)
	{
		this.startOffset = startOffset;
	}

	public int getEndOffset()
	{
		return endOffset;
	}
	
	public boolean hasValidOffsets()
	{
		if(startOffset < 0)
		{
			return false;
		}
		
		return (endOffset > startOffset);
	}

	public void setEndOffset(int endOffset)
	{
		this.endOffset = endOffset;
	}
}
