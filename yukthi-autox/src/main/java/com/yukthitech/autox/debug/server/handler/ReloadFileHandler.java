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
package com.yukthitech.autox.debug.server.handler;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationFileLoader;
import com.yukthitech.autox.debug.common.ClientMssgReloadFile;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.DebugServer;

/**
 * Used to handle reload file request.
 * @author akiran
 */
public class ReloadFileHandler extends AbstractServerDataHandler<ClientMssgReloadFile>
{
	private static Logger logger = LogManager.getLogger(ReloadFileHandler.class);
	
	public ReloadFileHandler()
	{
		super(ClientMssgReloadFile.class);
	}
	
	@Override
	public void processData(ClientMssgReloadFile data)
	{
		logger.debug("Executing reload of file: {}", data.getFile().getPath());
		
		Set<String> errors = new HashSet<>();
		AutomationFileLoader.loadTestFile(data.getFile(), errors, true);
		
		if(errors.isEmpty())
		{
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(data.getRequestId(), true, null));
		}
		else
		{
			String errStr = errors.stream().collect(Collectors.joining("\n\t"));
			
			logger.error("Loading of file '{}' failed with below errors: \\n\\t{}", data.getFile().getName(), errStr);
			
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(data.getRequestId(), false, "Loading of file '%s' failed with below errors: \n\t%s", 
					data.getFile().getName(), errStr));
		}
	}
}
