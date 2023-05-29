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
package com.yukthitech.autox.debug.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

/**
 * Used when execution is paused because of debug point.
 * @author akranthikiran
 */
public class ServerMssgExecutionPaused implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Represent stack element in stack trace.
	 * 
	 * @author akranthikiran
	 */
	public static class StackElement implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private String file;
		
		private int lineNumber;
		
		private String stackElementId;

		public StackElement(String file, int lineNumber, String stackElementId)
		{
			this.file = file;
			this.lineNumber = lineNumber;
			this.stackElementId = stackElementId;
		}

		public String getFile()
		{
			return file;
		}

		public int getLineNumber()
		{
			return lineNumber;
		}
		
		public String getStackElementId()
		{
			return stackElementId;
		}
		
		public void setStackElementId(String stackElementId)
		{
			this.stackElementId = stackElementId;
		}
	}
	
	/**
	 * Unique id representing current step or execution.
	 */
	private String executionId;
	
	/**
	 * Name of this execution.
	 */
	private String name;
	
	/**
	 * Path of debug point file, where execution got paused.
	 */
	private String debugFilePath;
	
	/**
	 * Line number of debug point file, where execution got paused.
	 */
	private int lineNumber;
	
	/**
	 * Stack trace of current execution.
	 */
	private List<StackElement> stackTrace;
	
	private Map<String, KeyValue> contextAttr;
	
	private Map<String, KeyValue> params;
	
	/**
	 * Flag indicating if this live point is created because of the error.
	 */
	private boolean errorPoint;

	public ServerMssgExecutionPaused(String executionId, String name, String debugFilePath, 
			int lineNumber, List<StackElement> stackTrace, Map<String, KeyValue> contextAttr,
			Map<String, KeyValue> paramMap,
			boolean errorPoint)
	{
		this.executionId = executionId;
		this.name = name;
		this.debugFilePath = debugFilePath;
		this.lineNumber = lineNumber;
		this.stackTrace = new ArrayList<>(stackTrace);
		this.contextAttr = contextAttr;
		this.params = paramMap;
		this.errorPoint = errorPoint;
	}
	
	public String getName()
	{
		return name;
	}

	public String getExecutionId()
	{
		return executionId;
	}

	public String getDebugFilePath()
	{
		return debugFilePath;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public List<StackElement> getStackTrace()
	{
		return stackTrace;
	}

	public Map<String, KeyValue> getContextAttr()
	{
		return contextAttr;
	}
	
	public void setContextAttr(Map<String, KeyValue> contextAttr)
	{
		this.contextAttr = contextAttr;
	}
	
	public Map<String, KeyValue> getParams()
	{
		return params;
	}
	
	public boolean isErrorPoint()
	{
		return errorPoint;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Execution Id: ").append(executionId);
		builder.append(",").append("Name: ").append(name);
		builder.append(",").append("File path: ").append(debugFilePath);
		builder.append(",").append("Line# ").append(lineNumber);
		builder.append(",").append("Error Point: ").append(errorPoint);
		builder.append(",").append("Attr Count: ").append(CollectionUtils.size(contextAttr));
		builder.append(",").append("Param Count: ").append(CollectionUtils.size(params));

		builder.append("]");
		return builder.toString();
	}
}
