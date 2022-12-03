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
package com.yukthitech.autox.test.ui.common;

/**
 * Form field types.
 * @author akiran
 */
public enum FormFieldType
{
	/**
	 * Simple text field.
	 */
	TEXT(new SimpleFieldAccessor()),
	
	/**
	 * An int field.
	 */
	INT(new SimpleFieldAccessor()),
	
	/**
	 * Date field.
	 */
	DATE(new SimpleFieldAccessor()),
	
	/**
	 * Password field.
	 */
	PASSWORD(new SimpleFieldAccessor()),
	
	/**
	 * Multi line text field.
	 */
	MULTI_LINE_TEXT(new SimpleFieldAccessor()),
	
	/**
	 * Drop down field.
	 */
	DROP_DOWN(new SelectFieldAccessor()),
	
	/**
	 * Check box field.
	 */
	CHECK_BOX(new CheckboxFieldAccessor(), true),
	
	/**
	 * Radio button field. 
	 */
	RADIO_BUTTON(new CheckboxFieldAccessor()),
	
	/**
	 * Hidden field.
	 */
	HIDDEN_FIELD(new ValueAttrAccessor())
	
	;
	
	/**
	 * Accessor for current field type.
	 */
	private IFieldAccessor fieldAccessor;
	
	/**
	 * Flag indicating if this type support multiple form fields for single value setting.
	 */
	private boolean multiFieldAccessor = false;

	/**
	 * Instantiates a new form field type.
	 *
	 * @param fieldAccessor the field accessor
	 */
	private FormFieldType(IFieldAccessor fieldAccessor)
	{
		this.fieldAccessor = fieldAccessor;
	}
	
	private FormFieldType(IFieldAccessor fieldAccessor, boolean multiFieldAccessor)
	{
		this.fieldAccessor = fieldAccessor;
		this.multiFieldAccessor = multiFieldAccessor;
	}
	
	/**
	 * Fetches field accessor for current type.
	 * @return Field accessor for current type.
	 */
	public IFieldAccessor getFieldAccessor()
	{
		return fieldAccessor;
	}
	
	/**
	 * Gets the flag indicating if this type support multiple form fields for single value setting.
	 *
	 * @return the flag indicating if this type support multiple form fields for single value setting
	 */
	public boolean isMultiFieldAccessor()
	{
		return multiFieldAccessor;
	}
}
