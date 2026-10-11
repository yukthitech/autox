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

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.test.Function;

/**
 * Runs a dynamic list of steps under a synthetic function name for reporting.
 */
public class InteractiveStepsExecutor extends Executor
{
	private static final AtomicInteger SEQ = new AtomicInteger(0);
	
	public InteractiveStepsExecutor(List<IStep> steps)
	{
		super(newInteractiveFunction(steps), null);
		super.childSteps = steps;
	}
	
	private static Function newInteractiveFunction(List<IStep> steps)
	{
		int id = SEQ.incrementAndGet();
		Function function = new Function();
		function.setName("interactive-steps-" + id);
		function.setDescription("Dynamic interactive steps");
		
		File location = null;
		
		if(steps != null && !steps.isEmpty() && steps.get(0).getLocation() != null)
		{
			location = steps.get(0).getLocation();
		}
		else
		{
			location = new File("interactive-steps-" + id + ".xml");
		}
		
		function.setLocation(location, 1);
		return function;
	}
	
	/**
	 * Executes the steps and returns this executor for status inspection.
	 */
	public InteractiveStepsExecutor executeSteps()
	{
		super.execute(null, null, null);
		return this;
	}
}
