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
package com.yukthitech.autox.test.ui.assertion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.ui.common.FieldOption;
import com.yukthitech.autox.test.ui.common.FormFieldType;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;

/**
 * Validates specified form has specified fields with specified field details.
 */
@Executable(name = "uiAssertFormFields", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Validates specified form fields are present")
public class AssertFormFields extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Represents field details to validate.
	 * 
	 * @author akiran
	 */
	public static class FormField implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * locator of the field.
		 */
		@Param(description = "Locator of the field")
		private String locator;

		/**
		 * Indicates if multiple fields are expected with same name.
		 */
		@Param(description = "Should be true, if multiple fields are expected with same locator")
		private boolean multiple;

		/**
		 * Expected form field type.
		 */
		@Param(description = "Expected form field type.")
		private FormFieldType type;

		/**
		 * List of field options to be validated.
		 */
		private List<FieldOption> fieldOptions;

		/**
		 * Expected value of the field.
		 */
		@Param(description = "Expected value of the field.")
		private String value;

		/**
		 * Gets the locator of the field.
		 *
		 * @return the locator of the field
		 */
		public String getLocator()
		{
			return locator;
		}

		/**
		 * Sets the locator of the field.
		 *
		 * @param locator
		 *            the new locator of the field
		 */
		public void setLocator(String locator)
		{
			this.locator = locator;
		}

		/**
		 * Checks if is indicates if multiple fields are expected with same
		 * name.
		 *
		 * @return the indicates if multiple fields are expected with same name
		 */
		public boolean isMultiple()
		{
			return multiple;
		}

		/**
		 * Sets the indicates if multiple fields are expected with same name.
		 *
		 * @param multiple
		 *            the new indicates if multiple fields are expected with
		 *            same name
		 */
		public void setMultiple(boolean multiple)
		{
			this.multiple = multiple;
		}

		/**
		 * Gets the expected form field type.
		 *
		 * @return the expected form field type
		 */
		public FormFieldType getType()
		{
			return type;
		}

		/**
		 * Sets the expected form field type.
		 *
		 * @param type
		 *            the new expected form field type
		 */
		public void setType(FormFieldType type)
		{
			this.type = type;
		}

		/**
		 * Gets the list of field options to be validated.
		 *
		 * @return the list of field options to be validated
		 */
		public List<FieldOption> getFieldOptions()
		{
			return fieldOptions;
		}

		/**
		 * Sets the list of field options to be validated.
		 *
		 * @param fieldOptions
		 *            the new list of field options to be validated
		 */
		public void setFieldOptions(List<FieldOption> fieldOptions)
		{
			this.fieldOptions = fieldOptions;
		}

		/**
		 * Adds value to {@link #fieldOptions fieldOptions}.
		 *
		 * @param fieldOption
		 *            fieldOption to be added
		 */
		@ChildElement(description = "Adds expected drop down option of the field.")
		public void addFieldOption(FieldOption fieldOption)
		{
			if(fieldOptions == null)
			{
				fieldOptions = new ArrayList<FieldOption>();
			}

			fieldOptions.add(fieldOption);
		}

		/**
		 * Gets the expected value of the field.
		 *
		 * @return the expected value of the field
		 */
		public String getValue()
		{
			return value;
		}

		/**
		 * Sets the expected value of the field.
		 *
		 * @param value
		 *            the new expected value of the field
		 */
		public void setValue(String value)
		{
			this.value = value;
		}
	}

	@Param(description = "Name of the driver to be used for the step. Defaults to default driver.", required = false)
	protected String driverName;

	/**
	 * Locator for the form.
	 */
	@Param(description = "Locator of the form to be validated.", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	/**
	 * Field details to validate.
	 */
	private List<FormField> fields;

	public void setDriverName(String driverName)
	{
		this.driverName = driverName;
	}
	
	/**
	 * Sets the locator for the form.
	 *
	 * @param locator
	 *            the new locator for the form
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Adds value to {@link #fields fields}.
	 *
	 * @param field
	 *            field to be added
	 */
	@ChildElement(description = "Form field to validate")
	public void addField(FormField field)
	{
		if(fields == null)
		{
			fields = new ArrayList<FormField>();
		}

		fields.add(field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IValidation#execute(com.yukthitech.ui.automation.
	 * AutomationContext, com.yukthitech.ui.automation.IExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		if(!"true".equals(enabled))
		{
			exeLogger.debug("Current validation is disabled. Skipping validation execution.");
			return;
		}
		
		exeLogger.debug("Validating form  - {}", locator);
		
		WebElement formElement = UiAutomationUtils.findElement(driverName, (WebElement) null, locator);
		List<WebElement> fieldElements = null;
		FormFieldType fieldType = null;

		for(FormField field : this.fields)
		{
			fieldElements = UiAutomationUtils.findElements(driverName, formElement, field.locator);

			if(fieldElements == null || fieldElements.isEmpty())
			{
				exeLogger.error("No field found with locator: {}", field.getLocator());
				throw new AutoxValidationException(this, "No field found with locator: {}", field.getLocator());
			}

			if(fieldElements.size() > 1 && !field.isMultiple())
			{
				exeLogger.error("Multiple fields found when single field is expected for locator: {}", field.getLocator());
				throw new AutoxValidationException(this, "Multiple fields found when single field is expected for locator: {}", field.getLocator());
			}

			if(field.getType() != null)
			{
				for(WebElement element : fieldElements)
				{
					fieldType = UiAutomationUtils.getFormFieldType(element);

					if(fieldType == null)
					{
						exeLogger.error("Failed to find field type of field locator: {}", field.getLocator());
						throw new AutoxValidationException(this, "Failed to find field type of field locator: {}", field.getLocator());
					}

					if(field.getType() != fieldType)
					{
						exeLogger.error(String.format("Expected field type '{}' is not matching with actual field type '{}' for locator: {}", field.getType(), fieldType, field.locator));
						throw new AutoxValidationException(this, "Expected field type '{}' is not matching with actual field type '{}' for locator: {}", 
								field.getType(), fieldType, field.locator);
					}
				}
			}

			if(field.getFieldOptions() != null)
			{
				List<FieldOption> expectedOptions = field.getFieldOptions();
				List<FieldOption> actualOptions = fieldType.getFieldAccessor().getOptions(context, fieldElements.get(0));
				FieldOption expectedOption = null, actualOption = null;

				for(int i = 0; i < expectedOptions.size(); i++)
				{
					expectedOption = expectedOptions.get(i);
					actualOption = actualOptions.get(i);

					if(expectedOption.getLabel() != null && !expectedOption.getLabel().equals(actualOption.getLabel()))
					{
						exeLogger.error("At index {} expected field option label '{}' is not matching with actual field option label '{}' for locator: {}", i, expectedOption.getLabel(), actualOption.getLabel(), field.locator);
						throw new AutoxValidationException(this, "At index {} expected field option label '{}' is not matching with actual field option label '{}' for locator: {}", 
								i, expectedOption.getLabel(), actualOption.getLabel(), field.locator);
					}

					if(expectedOption.getValue() != null && !expectedOption.getValue().equals(actualOption.getValue()))
					{
						exeLogger.error("At index {} expected field option value '{}' is not matching with actual field option value '{}' for locator: {}", i, expectedOption.getValue(), actualOption.getValue(), field.locator);
						throw new AutoxValidationException(this, "At index {} expected field option value '{}' is not matching with actual field option value '{}' for locator: {}", 
								i, expectedOption.getLabel(), actualOption.getLabel(), field.locator);
					}
				}
			}

			if(field.getValue() != null)
			{
				String actValue = fieldType.getFieldAccessor().getValue(context, fieldElements.get(0));

				if(field.getValue().equals(actValue))
				{
					exeLogger.error("Expected field value '{}' is not matching with actual field value'{}' for locator: {}", field.getValue(), actValue, field.locator);
					throw new AutoxValidationException(this, "Expected field value '{}' is not matching with actual field value'{}' for locator: {}", 
							field.getValue(), actValue, field.locator);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Assert Form [");

		builder.append("Locator: ").append(locator);

		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public IValidation clone()
	{
		return AutomationUtils.deepClone(this);
	}
}
