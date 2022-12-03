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
package com.yukthitech.test;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class TestServer
{
	private static Server server;
	
	public static Server start(String[] args) throws Exception
	{
		server = new Server(8080);
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase("./src/test/resources/web");

		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("./src/test/resources/app");
		webapp1.setContextPath("/app");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { webapp1, resource_handler, new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
		System.out.println("Server started at 8080...");
		return server;
	}
	
	public static void stop() throws Exception
	{
		server.stop();
	}

	public static void main(String[] args) throws Exception
	{
		Server server = start(args);
		server.join();
	}
}
