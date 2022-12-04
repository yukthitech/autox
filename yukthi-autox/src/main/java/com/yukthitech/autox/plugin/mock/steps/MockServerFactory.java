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
package com.yukthitech.autox.plugin.mock.steps;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Factor of mock servers.
 * @author akiran
 */
public class MockServerFactory
{
	/**
	 * Name to server.
	 */
	private static Map<String, MockServer> nameToServer = new HashMap<>();
	
	/**
	 * Starts a server with specified name at specified port.
	 * @param context context to be used
	 * @param name name of server
	 * @param port port at which server needs to be started.
	 */
	public static synchronized void startMockServer(AutomationContext context, String name, int port)
	{
		MockServer server = nameToServer.get(name);
		IExecutionLogger logger = context.getExecutionLogger();
		
		if(server != null)
		{
			if(server.getPort() != port)
			{
				logger.error("Server with name '{}' is already running at port {}. So it can not be started at: {}", name, server.getPort(), port);
				throw new InvalidStateException("Server with name '{}' is already started at port: {}", name, server.getPort());
			}
			
			logger.debug("Server with name '{}' is already running at port: {}. So ignoring to start same server on same port", name, port);
			return;
		}
		
		server = new MockServer(port);
		nameToServer.put(name, server);
		
		logger.debug("Started server with name '{}' at port: {}", name, port);
	}
	
	/**
	 * Stops specified mock server.
	 * @param context
	 * @param name
	 */
	public static synchronized void stopMockServer(AutomationContext context, String name)
	{
		MockServer server = nameToServer.get(name);
		IExecutionLogger logger = context.getExecutionLogger();
		
		if(server == null)
		{
			logger.debug("No server found with name: {}. Ignoring stop request.", name);
			return;
		}
		
		server.stop();
		nameToServer.remove(name);
		logger.debug("Server '{}' is stopped successfully", name);
	}
	
	/**
	 * Fetches the mock server with specified name.
	 * @param name
	 * @return
	 */
	public static synchronized MockServer getMockServer(String name)
	{
		MockServer server = nameToServer.get(name);
		
		if(server == null)
		{
			throw new InvalidArgumentException("No server found with name: {}", name);
		}
		
		return server;
	}
}
