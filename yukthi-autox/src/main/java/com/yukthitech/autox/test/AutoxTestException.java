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
package com.yukthitech.autox.test;

import com.yukthitech.autox.IStep;
import com.yukthitech.utils.exceptions.UtilsException;

public class AutoxTestException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Step which is throwing this exception.
	 */
	private IStep sourceStep;

	/**
	 * Instantiates a new autox exception.
	 *
	 * @param sourceStep the source step
	 * @param message the message
	 * @param args the args
	 */
	public AutoxTestException(IStep sourceStep, String message, Object... args)
	{
		super(message, args);
		this.sourceStep = sourceStep;
	}
	
	/**
	 * Gets the step which is throwing this exception.
	 *
	 * @return the step which is throwing this exception
	 */
	public IStep getSourceStep()
	{
		return sourceStep;
	}
}
