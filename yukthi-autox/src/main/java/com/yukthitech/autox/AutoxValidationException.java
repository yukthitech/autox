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
package com.yukthitech.autox;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Exception to be thrown when validation fails.
 * @author akranthikiran
 */
public class AutoxValidationException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public AutoxValidationException(IStep step, String message, Object... args)
	{
		super(buildValidationErrorMssg(step, message), args);
	}
	
	private static String buildValidationErrorMssg(IStep step, String mssgTemp)
	{
		return getStepName(step) + ": " + mssgTemp;
	}
	
	public static String getStepName(IStep step)
	{
		Executable executable = step.getClass().getAnnotation(Executable.class);
		String stepName = null;
		
		if(executable == null)
		{
			stepName = step.getClass().getSimpleName();
			stepName = stepName.replaceAll("Step$", "");
		}
		else
		{
			stepName = executable.name();
		}

		stepName = stepName.replaceAll("([A-Z])", "-$1").toLowerCase();
		
		if(stepName.startsWith("-"))
		{
			stepName = stepName.substring(1);
		}
		
		return stepName;
	}
}
