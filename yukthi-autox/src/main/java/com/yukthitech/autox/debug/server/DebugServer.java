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
package com.yukthitech.autox.debug.server;

import java.io.Closeable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.debug.common.ClientMessage;
import com.yukthitech.autox.debug.common.DebugUtils;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.handler.DebugOpHandler;
import com.yukthitech.autox.debug.server.handler.DebugPointsHandler;
import com.yukthitech.autox.debug.server.handler.DebuggerInitHandler;
import com.yukthitech.autox.debug.server.handler.DropToFrameHandler;
import com.yukthitech.autox.debug.server.handler.EvalExpressionHandler;
import com.yukthitech.autox.debug.server.handler.ExecuteStepsHandler;
import com.yukthitech.autox.debug.server.handler.LoadAppPropHandler;
import com.yukthitech.autox.debug.server.handler.ReloadFileHandler;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Socket transport for Prism remote debugging. Owned by {@link DebugFlowManager}
 * (and later by AutoxDebugSession); not registered on AutomationContext.
 * @author akiran
 */
public class DebugServer
{
	private static Logger logger = LogManager.getLogger(DebugServer.class);
	
	/**
	 * Port on which monitoring manager should run.
	 */
	private int serverPort;
	
	/**
	 * Server socket.
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Client socket.
	 */
	private Socket clientSocket;
	
	/**
	 * Stream to send data to client.
	 */
	private ObjectOutputStream clientOutputStream;
	
	/**
	 * Client stream to read objects from client.
	 */
	private ObjectInputStream clientInputStream;
	
	/**
	 * Data buffer to be sent to the client.
	 */
	private LinkedList<Serializable> clientDataBuffer = new LinkedList<>();
	
	/**
	 * Lock to synchronize on client data buffer.
	 */
	private ReentrantLock clientDataBufferLock = new ReentrantLock();
	
	/**
	 * Thread to send data to client.
	 */
	private Thread writeThread;
	
	/**
	 * Thread to read data from client.
	 */
	private Thread readThread;
	
	/**
	 * Manager to manage listeners.
	 */
	private Map<Class<?>, IServerDataHandler<Serializable>> dataHandlers = new HashMap<>();
	
	private boolean stopped = false;
	
	private boolean started = false;
	
	public DebugServer(int serverPort)
	{
		if(serverPort <= 0)
		{
			throw new InvalidArgumentException("Invalid monitor port specified: " + serverPort);
		}
		
		this.serverPort = serverPort;
		
		writeThread = new Thread(this::sendDataToClient, "Debug Writer");
		readThread = new Thread(this::readDataFromClient, "Debug Reader");
		
		addAsyncServerDataHandler(new ExecuteStepsHandler());
		addAsyncServerDataHandler(new DebuggerInitHandler());
		addAsyncServerDataHandler(new DebugPointsHandler());
		addAsyncServerDataHandler(new EvalExpressionHandler());
		addAsyncServerDataHandler(new DebugOpHandler());
		addAsyncServerDataHandler(new LoadAppPropHandler());
		addAsyncServerDataHandler(new DropToFrameHandler());
		addAsyncServerDataHandler(new ReloadFileHandler());
	}
	
	/**
	 * Stops reader/writer threads and closes sockets. Idempotent.
	 */
	public void reset()
	{
		stopped = true;
		
		if(writeThread != null)
		{
			writeThread.interrupt();
		}
		
		if(readThread != null)
		{
			readThread.interrupt();
		}
		
		close(clientOutputStream);
		close(clientInputStream);
		close(clientSocket);
		close(serverSocket);

		try
		{
			if(writeThread != null && writeThread.isAlive())
			{
				writeThread.join();
			}
			
			if(readThread != null && readThread.isAlive())
			{
				readThread.join();
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		started = false;
	}
	
	private void close(Closeable closeable)
	{
		if(closeable == null)
		{
			return;
		}
		
		try
		{
			closeable.close();
		}catch(Exception ex)
		{}
	}
	
	/**
	 * Adds the listener to the server.
	 * @param handler handler to add
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addAsyncServerDataHandler(IServerDataHandler<?> handler)
	{
		this.dataHandlers.put(handler.getSupportedDataType(), (IServerDataHandler) handler);
	}
	
	private void sendDataToClient()
	{
		List<Serializable> dataBuff = null;
		
		while(!stopped)
		{
			clientDataBufferLock.lock();
			
			try
			{
				if(clientDataBuffer.isEmpty())
				{
					continue;
				}
				
				dataBuff = new ArrayList<>(clientDataBuffer);
				clientDataBuffer.clear();
			}finally
			{
				clientDataBufferLock.unlock();
			}
			
			byte rawData[] = null;
			
			try
			{
				for(Serializable mssg : dataBuff)
				{
					rawData = DebugUtils.serialize(mssg);
					
					clientOutputStream.writeObject(rawData);
					clientOutputStream.flush();
				}
			} catch(Exception ex)
			{
				logger.error("An error occurred while sending data to client. There might be some data loss being sent to client", ex);
			}
			
			try
			{
				Thread.sleep(Long.MAX_VALUE);
			}catch(InterruptedException ex)
			{
				//ignore
			}
		}
	}
	
	/**
	 * Reads data from client.
	 */
	private void readDataFromClient()
	{
		//wait till client socket is closed
		while(!stopped && clientSocket != null && !clientSocket.isClosed())
		{
			ClientMessage clientMssg = null;
			
			try
			{
				clientMssg = (ClientMessage) clientInputStream.readObject();
				logger.debug("Received command from client: {}", clientMssg);
				
				Class<?> mssgType = clientMssg.getClass();
				
				IServerDataHandler<Serializable> handler = this.dataHandlers.get(mssgType);
				
				if(handler != null)
				{
					handler.processData(clientMssg);
				}
				else
				{
					logger.warn("Unsupported debug-message received: {}", clientMssg);
					sendClientMessage(new ServerMssgConfirmation(clientMssg.getRequestId(), false, "Unsupported debug-message received: %s", clientMssg));
				}
			} catch(DebugLockException ex)
			{
				sendClientMessage(new ServerMssgConfirmation(clientMssg.getRequestId(), false, ex.getMessage()));
			} catch(Exception ex)
			{
				logger.error("An error occurred while fetching data from client", ex);
				
				//as the exception might be because of client close. So wait for second and check again
				try
				{
					Thread.sleep(1000);
				}catch(Exception e1)
				{}
			}
		}
	}
	
	/**
	 * Starts the manager and waits for clients to get connected.
	 */
	public synchronized void start()
	{
		if(started)
		{
			throw new InvalidStateException("Debug server is already started.");
		}
		
		logger.debug("Starting debug-server on port: {}", serverPort);
		
		try
		{
			serverSocket = new ServerSocket(serverPort);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to start debug server on port: " + serverPort, ex);
		}
		
		try
		{
			logger.info("Waiting for client to connect. Monitor port: {}", serverPort);
			
			clientSocket = serverSocket.accept();
			OutputStream outputStream = clientSocket.getOutputStream();
			
			clientOutputStream = new ObjectOutputStream(outputStream);
			clientInputStream = new ObjectInputStream(clientSocket.getInputStream());
	
			logger.debug("Client got connected...");
			writeThread.start();
			readThread.start();
			
			logger.info("Waiting for client to send init info...");
			
			//wait till init message is received
			while(!DebuggerInitHandler.isInitialized())
			{
				//wait for 100 millis and check again
				AutomationUtils.sleep(100);
			}
			
			started = true;
			
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while waiting for client to connect.", ex);
		}
	}
	
	/**
	 * Used to send monitoring data to the client.
	 * @param data data to be sent.
	 */
	public void sendClientMessage(Serializable data)
	{
		clientDataBufferLock.lock();
		
		try
		{
			clientDataBuffer.add(data);
			writeThread.interrupt();
		}finally
		{
			clientDataBufferLock.unlock();
		}
	}
}
