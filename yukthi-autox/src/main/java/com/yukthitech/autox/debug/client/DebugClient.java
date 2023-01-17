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
package com.yukthitech.autox.debug.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.debug.common.ClientMessage;
import com.yukthitech.autox.debug.common.ClientMssgDebuggerInit;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.utils.event.EventListenerManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Client to get monitor data.
 * @author akiran
 */
public class DebugClient
{
	private static Logger logger = LogManager.getLogger(DebugClient.class);
	
	private static AtomicInteger counter = new AtomicInteger(); 
	
	/**
	 * Gap time at which server connection should be checked.
	 */
	private static final int GAP_TIME = 500;
	
	/**
	 * Maximum time for which client should wait for server.
	 */
	private static final int MAX_WAIT_TIME = 60000;
	
	/**
	 * Server host on which server is running.
	 */
	private String serverHost;
	
	/**
	 * Port where server is expected to run.
	 */
	private int serverPort;
	
	/**
	 * Socket connected to server.
	 */
	private Socket clientSocket;
	
	/**
	 * Thread to read data from server.
	 */
	private Thread readerThread;

	/**
	 * Stream for which server data can be read.
	 */
	private ObjectInputStream readerStream;
	
	/**
	 * Stream to write data to server.
	 */
	private ObjectOutputStream writerStream;
	
	/**
	 * Buffer to maintain read data. Till it is sent to listeners.
	 */
	private LinkedList<Serializable> readBuffer = new LinkedList<>();
	
	/**
	 * Manager to manage listeners.
	 */
	private EventListenerManager<IDebugClientHandler> listenerManager = EventListenerManager.newEventListenerManager(IDebugClientHandler.class, false);
	
	/**
	 * Thread to invoke listeners.
	 */
	private Thread listenerThread;
	
	/**
	 * Callback methods for messages.
	 */
	private Map<String, IMessageCallback> idToCallback = new HashMap<>();
	
	/**
	 * Lock to be used for callbacks.
	 */
	private ReentrantLock callbackLock = new ReentrantLock();
	
	private ClientMssgDebuggerInit initMssg;
	
	private DebugClient(String serverHost, int serverPort)
	{
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		
		readerThread = new Thread(this::readDataFromServer, "Debug Client Reader - " + counter.incrementAndGet());
		listenerThread = new Thread(this::invokeListeners, "Debug Client Listeners - " + counter.incrementAndGet());
	}
	
	public static DebugClient newClient(String serverHost, int serverPort, ClientMssgDebuggerInit initMssg)
	{
		DebugClient client = new DebugClient(serverHost, serverPort);
		client.initMssg = initMssg;

		return client;
	}

	/**
	 * Method to read data from server and add it to read buffer.
	 */
	@SuppressWarnings("unchecked")
	private void readDataFromServer()
	{
		boolean firstError = true;
		
		//wait till client socket is closed
		while(clientSocket != null && !clientSocket.isClosed())
		{
			try
			{
				List<Serializable> dataLst = (List<Serializable>) readerStream.readObject();
				
				synchronized(this)
				{
					this.readBuffer.addAll(dataLst);
					super.notifyAll();
				}
			}catch(Exception ex)
			{
				if(ex instanceof SocketException)
				{
					//ignore first erorr as it may happen during env termination
					if(!firstError)
					{
						logger.error("An error occurred while fetching data from server", ex);	
					}
					else
					{
						firstError = false;
					}
				}
				
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
	 * Adds specified handler to this client listener list.
	 * @param handler
	 */
	public DebugClient addDataHandler(IDebugClientHandler handler)
	{
		this.listenerManager.addListener(handler);
		return this;
	}
	
	/**
	 * Thread method to invoke listeners.
	 */
	private void invokeListeners()
	{
		while(clientSocket != null && !clientSocket.isClosed())
		{
			List<Serializable> data = null;
			
			synchronized(this)
			{
				if(this.readBuffer.isEmpty())
				{
					//wait till data is available
					try
					{
						super.wait();
					}catch(Exception ex)
					{}
				}
				
				data = new ArrayList<>(this.readBuffer);
				this.readBuffer.clear();
			}
			
			for(Serializable obj : data)
			{
				if(obj instanceof ServerMssgConfirmation)
				{
					ServerMssgConfirmation confirm = (ServerMssgConfirmation) obj;
					handleConfirmMessage(confirm);
				}
				
				this.listenerManager.get().processData(obj);
			}
		}
	}
	
	private void handleConfirmMessage(ServerMssgConfirmation mssg)
	{
		logger.debug("For message with id {} got confirmation: {}", mssg.getRequestId(), mssg);
		IMessageCallback callback = null;
		
		callbackLock.lock();
		
		try
		{
			callback = idToCallback.remove(mssg.getRequestId());
		}finally
		{
			callbackLock.unlock();
		}
		
		if(callback == null)
		{
			return;
		}
		
		try
		{
			logger.debug("For message with id {} invoking the callback", mssg.getRequestId());
			callback.onProcess(mssg);
		}catch(Exception ex)
		{
			logger.error("An error occurred while processing confirmation message: {}", mssg, ex);
		}
	}
	
	/**
	 * Starts the client which will wait till is connected to server.
	 */
	public DebugClient start()
	{
		logger.debug("Waiting for server to be up and running...");
		
		long startTime = System.currentTimeMillis();
		long diff = 0;
		Exception lastEx = null;
		
		while(diff < MAX_WAIT_TIME)
		{
			try
			{
				clientSocket = new Socket(serverHost, serverPort);
				
				InputStream is = clientSocket.getInputStream();
				this.readerStream = new ObjectInputStream(is);
				this.writerStream = new ObjectOutputStream(clientSocket.getOutputStream());
				break;
			}catch(Exception ex)
			{
				clientSocket  = null;
				lastEx = ex;
			}
			
			try
			{
				Thread.sleep(GAP_TIME);
			}catch(Exception ex)
			{}

			diff = System.currentTimeMillis() - startTime;
		}

		if(clientSocket == null)
		{
			throw new InvalidStateException("Failed to connect to server. Last exception while connecting to server was: {}", "" + lastEx);
		}
		
		logger.debug("Successfully connected to server...");
		readerThread.start();
		listenerThread.start();
		
		logger.debug("Sending init-mssg to server..");
		sendDataToServer(initMssg);

		return this;
	}
	
	public void sendDataToServer(ClientMessage data)
	{
		sendDataToServer(data, null);
	}
	
	public synchronized void sendDataToServer(ClientMessage data, IMessageCallback callback)
	{
		if(clientSocket == null)
		{
			throw new InvalidStateException("Client process is disconnected");
		}
		
		try
		{
			logger.debug("Sending client message {} with id: {}", data, data.getRequestId());
			
			if(callback != null)
			{
				this.writerStream.writeObject(data);
				
				callbackLock.lock();
				
				try
				{
					idToCallback.put(data.getRequestId(), callback);
				}finally
				{
					callbackLock.unlock();
				}
			}
			else
			{
				this.writerStream.writeObject(data);
			}
			
			this.writerStream.flush();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while sending data to server.", ex);
		}
	}
	
	/**
	 * Called when process is terminated.
	 */
	public void processTerminated()
	{
		callbackLock.lock();
		
		try
		{
			Set<String> callbackIds = new HashSet<>(idToCallback.keySet());
			
			for(String id : callbackIds)
			{
				IMessageCallback callback = idToCallback.remove(id);
				callback.terminated();
			}
		}finally
		{
			callbackLock.unlock();
		}
	}
	
	public void close()
	{
		if(clientSocket == null)
		{
			return;
		}
		
		try
		{
			clientSocket.close();
		}catch(Exception ex)
		{
			logger.error("An error occurred while closing monitoring client. Exception: " + ex);
		}
		
		clientSocket = null;
	}
}
