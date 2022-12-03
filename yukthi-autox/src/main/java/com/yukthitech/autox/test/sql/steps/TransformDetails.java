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
package com.yukthitech.autox.test.sql.steps;

/**
 * Details available during transformation.
 * @author akranthikiran
 */
public class TransformDetails
{
	/**
	 * Name of column whose value is being transformed.
	 */
	private String columnName;
	
	/**
	 * Current column value being transformed.
	 */
	private Object columnValue;
	
	/**
	 * Current row.
	 */
	private Object row;

	/**
	 * Instantiates a new transform details.
	 *
	 * @param columnName
	 *            name of column whose value is being transformed.
	 * @param columnValue
	 *            current column value being transformed.
	 * @param row
	 *            current row.
	 */
	public TransformDetails(String columnName, Object columnValue, Object row)
	{
		this.columnName = columnName;
		this.columnValue = columnValue;
		this.row = row;
	}

	/**
	 * Gets the name of column whose value is being transformed.
	 *
	 * @return the name of column whose value is being transformed
	 */
	public String getColumnName()
	{
		return columnName;
	}

	/**
	 * Gets the current column value being transformed.
	 *
	 * @return the current column value being transformed
	 */
	public Object getColumnValue()
	{
		return columnValue;
	}

	/**
	 * Gets the current row.
	 *
	 * @return the current row
	 */
	public Object getRow()
	{
		return row;
	}
}
