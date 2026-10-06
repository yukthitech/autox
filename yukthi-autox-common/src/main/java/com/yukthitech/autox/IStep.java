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

import java.io.Serializable;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Represents automation step to be performed.
 * @author akiran
 */
public interface IStep extends Cloneable, Serializable, ILocationBased
{
	/**
	 * Method which should execute current step.
	 * @param context Current automation context
	 * @param logger Logger to log messages.
	 * value will be checked for success.
	 */
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception;
	
	/**
	 * Clones and makes a copy of current step.
	 * @return copy of current step
	 */
	public IStep clone();
	
	/**
	 * Fetches flag indicating if logging has to be disabled for current step.
	 * @return flag indicating if logging has to be disabled for current step.
	 */
	public boolean isLoggingDisabled();
	
	/**
	 * Sets the source step with actual expressions. Which in turn can be used, when access to actual
	 * expressions is required. This is mainly needed by assertions to build error messages in case of
	 * errors.
	 * @param sourceStep
	 */
	public void setSourceStep(IStep sourceStep);
	
	public IStep getSourceStep();
}
