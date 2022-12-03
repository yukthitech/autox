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
package com.yukthitech.autox.ide.xmlfile;

/**
 * Location information.
 * @author akiran
 */
public class LocationRange
{
	/**
	 * Start Offset of the element.
	 */
	private int startOffset;
	
	/**
	 * start Line in which it occurred.
	 */
	private int startLineNumber;
	
	/**
	 * start Column in which it occurred.
	 */
	private int startColumnNumber;

	/**
	 * Start Offset of the element.
	 */
	private int endOffset;
	
	/**
	 * start Line in which it occurred.
	 */
	private int endLineNumber;
	
	/**
	 * start Column in which it occurred.
	 */
	private int endColumnNumber;
	
	public LocationRange()
	{}
	
	public LocationRange(int startOffset, int startLineNumber, int startColumnNumber)
	{
		this.startOffset = startOffset;
		this.startLineNumber = startLineNumber;
		this.startColumnNumber = startColumnNumber;
	}
	
	public LocationRange(int startOffset, int startLineNumber, int startColumnNumber, int endOffset, int endLineNumber, int endColumnNumber)
	{
		this.startOffset = startOffset;
		this.startLineNumber = startLineNumber;
		this.startColumnNumber = startColumnNumber;
		this.endOffset = endOffset;
		this.endLineNumber = endLineNumber;
		this.endColumnNumber = endColumnNumber;
	}

	/**
	 * Sets the start location.
	 *
	 * @param startOffset the start offset
	 * @param startLineNumber the start line number
	 * @param startColumnNumber the start column number
	 */
	public void setStartLocation(int startOffset, int startLineNumber, int startColumnNumber)
	{
		this.startOffset = startOffset;
		this.startLineNumber = startLineNumber;
		this.startColumnNumber = startColumnNumber;
	}
	
	/**
	 * Sets the end location.
	 *
	 * @param endOffset the end offset
	 * @param endLineNumber the end line number
	 * @param endColumnNumber the end column number
	 */
	public void setEndLocation(int endOffset, int endLineNumber, int endColumnNumber)
	{
		this.endOffset = endOffset;
		this.endLineNumber = endLineNumber;
		this.endColumnNumber = endColumnNumber;
	}

	/**
	 * Gets the start Offset of the element.
	 *
	 * @return the start Offset of the element
	 */
	public int getStartOffset()
	{
		return startOffset;
	}

	/**
	 * Sets the start Offset of the element.
	 *
	 * @param startOffset the new start Offset of the element
	 */
	public void setStartOffset(int startOffset)
	{
		this.startOffset = startOffset;
	}

	/**
	 * Gets the start Line in which it occurred.
	 *
	 * @return the start Line in which it occurred
	 */
	public int getStartLineNumber()
	{
		return startLineNumber;
	}

	/**
	 * Sets the start Line in which it occurred.
	 *
	 * @param startLineNumber the new start Line in which it occurred
	 */
	public void setStartLineNumber(int startLineNumber)
	{
		this.startLineNumber = startLineNumber;
	}

	/**
	 * Gets the start Column in which it occurred.
	 *
	 * @return the start Column in which it occurred
	 */
	public int getStartColumnNumber()
	{
		return startColumnNumber;
	}

	/**
	 * Sets the start Column in which it occurred.
	 *
	 * @param startColumnNumber the new start Column in which it occurred
	 */
	public void setStartColumnNumber(int startColumnNumber)
	{
		this.startColumnNumber = startColumnNumber;
	}

	/**
	 * Gets the start Offset of the element.
	 *
	 * @return the start Offset of the element
	 */
	public int getEndOffset()
	{
		return endOffset;
	}

	/**
	 * Sets the start Offset of the element.
	 *
	 * @param endOffset the new start Offset of the element
	 */
	public void setEndOffset(int endOffset)
	{
		this.endOffset = endOffset;
	}

	/**
	 * Gets the start Line in which it occurred.
	 *
	 * @return the start Line in which it occurred
	 */
	public int getEndLineNumber()
	{
		return endLineNumber;
	}

	/**
	 * Sets the start Line in which it occurred.
	 *
	 * @param endLineNumber the new start Line in which it occurred
	 */
	public void setEndLineNumber(int endLineNumber)
	{
		this.endLineNumber = endLineNumber;
	}

	/**
	 * Gets the start Column in which it occurred.
	 *
	 * @return the start Column in which it occurred
	 */
	public int getEndColumnNumber()
	{
		return endColumnNumber;
	}

	/**
	 * Sets the start Column in which it occurred.
	 *
	 * @param endColumnNumber the new start Column in which it occurred
	 */
	public void setEndColumnNumber(int endColumnNumber)
	{
		this.endColumnNumber = endColumnNumber;
	}
	
	public boolean hasLine(int lineNo)
	{
		return (lineNo >= startLineNumber && lineNo <= endLineNumber);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append(startOffset).append(", ");
		builder.append(startLineNumber).append(", ");
		builder.append(startColumnNumber).append(" => ");

		builder.append(endOffset).append(", ");
		builder.append(endLineNumber).append(", ");
		builder.append(endColumnNumber);

		builder.append("]");
		return builder.toString();
	}

}
