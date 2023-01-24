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
package com.yukthitech.prism.exeenv.debug;

import com.yukthitech.autox.debug.common.ServerMssgStepExecuted;
import com.yukthitech.prism.exeenv.ExecutionEnvironment;
import com.yukthitech.prism.services.IIdeEvent;

/**
 * This event is raised when Steps execution is completed.
 * @author akranthikiran
 */
public class DebugStepsExecutedEvent implements IIdeEvent
{
	private ExecutionEnvironment executionEnvironment;

	private ServerMssgStepExecuted stepExecuted;

	public DebugStepsExecutedEvent(ExecutionEnvironment executionEnvironment, ServerMssgStepExecuted stepExecuted)
	{
		this.executionEnvironment = executionEnvironment;
		this.stepExecuted = stepExecuted;
	}

	public ExecutionEnvironment getExecutionEnvironment()
	{
		return executionEnvironment;
	}

	public ServerMssgStepExecuted getStepExecuted()
	{
		return stepExecuted;
	}
}
