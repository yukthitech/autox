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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Mock server wrapping the jetty server. This maintains the responses being mocked
 * and requests received (till reset() is called).
 * @author akiran
 */
public class MockServer 
{
	/**
	 * Request handler for mock requests.
	 * @author akiran
	 */
	class MockRequestHandler extends AbstractHandler
	{
		@Override
		public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException 
		{
			handleRequest(arg0, arg1, request, response);
		}
	}

	/**
	 * Port at which server is running.
	 */
	private int port;
	
	/**
	 * Instance of jetty server.
	 */
	private Server server = null;
	
	/**
	 * Url to response mapping.
	 */
	private List<MockResponse> mockResponses = new LinkedList<>();
	
	/**
	 * Mock requests received on this server.
	 */
	private List<MockRequest> mockRequests = new LinkedList<>();
	
	/**
	 * Instantiates a new mock server.
	 */
	MockServer(int port)
	{
		if(port < 0)
		{
			throw new IllegalStateException("Invalid port number specified: " + port);
		}
		
		this.port = port;

		server = new Server(port);
		server.getConnectors()[0].getConnectionFactory(HttpConnectionFactory.class);
		server.setHandler(new MockRequestHandler());
		
		try 
		{
			server.start();
		} catch (Exception ex) 
		{
			throw new InvalidStateException("An error occurred while starting mock-server at port: {}", port, ex);
		}
	}
	
	/**
	 * Gets the port at which server is running.
	 *
	 * @return the port at which server is running
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 * Stop the jetty server.
	 */
	void stop()
	{
		try 
		{
			server.stop();
		} catch (Exception e) 
		{
			throw new InvalidStateException("An error occurred while stopping the server", e);
		}
	}
	
	/**
	 * Adds the specified mock response to the start of the queue.
	 * @param response
	 */
	public synchronized void addMockResponse(MockResponse response)
	{
		mockResponses.add(0, response);
	}
	
	/**
	 * Fetches requests which are filtered by specified filter.
	 * @param filter
	 * @return
	 */
	public synchronized List<MockRequest> fetchRequests(MockRequestFilter filter)
	{
		List<MockRequest> filteredRequests = new ArrayList<>();
		
		for(MockRequest request : this.mockRequests)
		{
			if(filter.isMatching(request))
			{
				filteredRequests.add(request);
			}
		}
		
		return filteredRequests;
	}
	
	public synchronized void reset()
	{
		mockResponses.forEach(resp -> resp.stop());
		
		mockRequests.clear();
		mockResponses.clear();
	}
	
	private synchronized void handleRequest(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException 
	{
		MockRequest mockRequest = new MockRequest(request);
		Iterator<MockResponse> responseIt = mockResponses.iterator();
		
		while(responseIt.hasNext())
		{
			MockResponse mockResponse = responseIt.next();
			
			if(!mockResponse.isMatchingRequest(mockRequest))
			{
				continue;
			}
			
			//if response is not served successfully
			if(!mockResponse.writeTo(response))
			{
				return;
			}
			
			if(!mockResponse.canServeMore())
			{
				responseIt.remove();
			}
			
			mockRequest.setMockResponse(mockResponse);
			mockRequests.add(mockRequest);
			
			return;
		}
		
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "No mock response found for specified request. Time Stamp: " + System.currentTimeMillis());
	}
}
