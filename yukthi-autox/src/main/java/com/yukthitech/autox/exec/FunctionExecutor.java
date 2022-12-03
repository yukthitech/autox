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
package com.yukthitech.autox.exec;

import java.util.Arrays;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.FunctionRef;

/**
 * Function executor to execute specified function.
 * @author akiran
 */
public class FunctionExecutor extends Executor
{
	private static final String RETURN_ATTR = "functionExecutor.returnAttr";
	
	public FunctionExecutor(Function function)
	{
		super(function, null);
		super.childSteps = Arrays.asList(new FunctionRef(function.getName(), RETURN_ATTR));
	}

	public Object executeFunction()
	{
		super.execute(null, null, null);
		return AutomationContext.getInstance().getAttribute(RETURN_ATTR);
	}
}
