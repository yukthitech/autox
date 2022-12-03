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
package com.yukthitech.autox.ide.monitor;

import java.io.Serializable;

import com.yukthitech.autox.debug.client.IDebugClientHandler;
import com.yukthitech.autox.debug.common.MonitorLogServerMssg;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;

public class ReportMessageDataHandler implements IDebugClientHandler
{
	/**
	 * Environment whose events are being listened.
	 */
	private ExecutionEnvironment environment;
	
	public ReportMessageDataHandler(ExecutionEnvironment environment)
	{
		this.environment = environment;
	}

	@Override
	public void processData(Serializable data)
	{
		if(!(data instanceof MonitorLogServerMssg))
		{
			return;
		}
		
		MonitorLogServerMssg mssg = (MonitorLogServerMssg) data;
		environment.addReportMessage(mssg);
	}
}
