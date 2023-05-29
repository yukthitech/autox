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
package com.yukthitech.prism.exeenv;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.yukthitech.autox.debug.client.DebugClient;
import com.yukthitech.autox.debug.client.IMessageCallback;
import com.yukthitech.autox.debug.common.ClientMessage;
import com.yukthitech.autox.debug.common.ClientMssgDebugPoints;
import com.yukthitech.autox.debug.common.ClientMssgDebuggerInit;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.common.KeyValue;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.debug.common.ServerMssgExecutionReleased;
import com.yukthitech.autox.debug.common.ServerMssgStepExecuted;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.exeenv.debug.DebugExecutionPausedEvent;
import com.yukthitech.prism.exeenv.debug.DebugExecutionReleasedEvent;
import com.yukthitech.prism.exeenv.debug.DebugPointManager;
import com.yukthitech.prism.exeenv.debug.DebugPointsChangedEvent;
import com.yukthitech.prism.exeenv.debug.DebugStepsExecutedEvent;
import com.yukthitech.prism.exeenv.debug.IdeDebugPoint;
import com.yukthitech.prism.layout.ConsoleLinePattern;
import com.yukthitech.prism.layout.UiLayout;
import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.services.IdeEventManager;
import com.yukthitech.prism.services.SpringServiceProvider;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * @author admin
 */
public class ExecutionEnvironment
{
	private static Logger logger = LogManager.getLogger(ExecutionEnvironment.class);
	
	private static AtomicInteger idTracker = new AtomicInteger(0);
	
	private int id = idTracker.incrementAndGet();
	
	@Autowired
	private UiLayout uiLayout;
	
	@Value("${console.lines.maxCount}")
	private int maxLines;
	
	@Value("${console.lines.truncateSize}")
	private int truncateLength;

	private ExecutionType executionType;

	private String name;

	private Process process;

	private LinkedList<String> consoleHtml = new LinkedList<>();
	
	private long lastConsoleChangeTime = 0;

	private boolean terminated = false;

	private DebugClient debugClient;
	
	private boolean debugEnv;

	private File reportFolder;

	private File reportFile;
	
	private Project project;
	
	private String extraArgs[];
	
	private IdeEventManager ideEventManager;
	
	private Map<String, ServerMssgExecutionPaused> pausedThreads = new LinkedHashMap<>();
	
	private String activeThreadId;
	
	/**
	 * Pattern used for last line in console.
	 */
	private ConsoleLinePattern lastLinePattern;
	
	private int debugPort;
	
	private ExecuteCommand command;
	
	private String initialMessage;
	
	ExecutionEnvironment(ExecuteCommand command, String envName, Process process, int debugPort, File reportFolder, 
			String initialMessage, String extraArgs[])
	{
		this.executionType = command.getExecutionType();
		this.project = command.getProject();
		this.debugPort = debugPort;
		this.command = command;
		this.initialMessage = initialMessage;
		
		this.name = envName;
		this.process = process;
		this.reportFolder = reportFolder;
		this.extraArgs = extraArgs;
		this.ideEventManager = SpringServiceProvider.getService(IdeEventManager.class);
		this.debugEnv = (debugPort > 0);
	}
	
	@PostConstruct
	private void init()
	{
		logOnConsole(initialMessage);

		Thread outputThread = new Thread()
		{
			public void run()
			{
				readConsoleStream(process.getInputStream());
			}
		};

		outputThread.start();

		if(debugPort > 0)
		{
			IdeUtils.execute(() -> 
			{
				DebugPointManager debugManager = SpringServiceProvider.getService(DebugPointManager.class);
				List<IdeDebugPoint> ideDebugPoints = debugManager.getDebugPoints(project.getName());
				List<DebugPoint> debugPoints = ideDebugPoints.stream()
						.map(idePoint -> new DebugPoint(idePoint.getFile().getPath(), idePoint.getLineNo() + 1, idePoint.getCondition()))
						.collect(Collectors.toList());
				
				if(command.getRunToLinePoint() != null)
				{
					IdeDebugPoint runToPoint = command.getRunToLinePoint();
					
					debugPoints = new ArrayList<>(debugPoints);
					
					DebugPoint newDebPoint = new DebugPoint(runToPoint.getFile().getPath(), runToPoint.getLineNo() + 1, null);
					newDebPoint.setSinglePauseOnly(true);
					debugPoints.add(newDebPoint);
				}
				
				ClientMssgDebuggerInit initMssg = new ClientMssgDebuggerInit(debugPoints);
				
				debugClient = DebugClient.newClient("localhost", debugPort, initMssg).start();
				debugClient.addDataHandler(this::onServerMessage);
			}, 1);
		}
	}
	
	public int getId()
	{
		return id;
	}
	
	/**
	 * This is expected to be called by {@link ExecutionEnvironmentManager}.
	 * @param event
	 */
	void onDebugPointChange(DebugPointsChangedEvent event)
	{
		boolean isAffected = event.getDebugPoints().stream()
				.filter(point -> project.getName().equals(point.getProject()))
				.findFirst()
				.isPresent();
		
		if(!isAffected)
		{
			return;
		}
		
		IdeUtils.execute(() -> 
		{
			DebugPointManager debugManager = SpringServiceProvider.getService(DebugPointManager.class);
			List<IdeDebugPoint> ideDebugPoints = debugManager.getDebugPoints(project.getName());
			List<DebugPoint> debugPoints = ideDebugPoints.stream()
					.map(idePoint -> new DebugPoint(idePoint.getFile().getPath(), idePoint.getLineNo() + 1, idePoint.getCondition()))
					.collect(Collectors.toList());
			
			debugClient.sendDataToServer(new ClientMssgDebugPoints(debugPoints));
		}, 1);
	}
	
	private void onServerMessage(Serializable data)
	{
		logger.debug("From server received message: {}", data);
		
		if(data instanceof ServerMssgExecutionPaused)
		{
			ServerMssgExecutionPaused mssg = (ServerMssgExecutionPaused) data;
			
			synchronized(pausedThreads)
			{
				pausedThreads.put(mssg.getExecutionId(), mssg);
			}
			
			ideEventManager.raiseAsyncEvent(new DebugExecutionPausedEvent(this, mssg));
			return;
		}
		
		if(data instanceof ServerMssgExecutionReleased)
		{
			ServerMssgExecutionReleased mssg = (ServerMssgExecutionReleased) data;
			ServerMssgExecutionPaused pauseMssg = null;
			
			synchronized(pausedThreads)
			{
				pauseMssg = pausedThreads.remove(mssg.getExecutionId());
			}
			
			ideEventManager.raiseAsyncEvent(new DebugExecutionReleasedEvent(this, pauseMssg));
			return;
		}
		
		if(data instanceof ServerMssgStepExecuted)
		{
			ServerMssgStepExecuted mssg = (ServerMssgStepExecuted) data;

			synchronized(pausedThreads)
			{
				ServerMssgExecutionPaused pauseMssg = pausedThreads.get(mssg.getExecutionId());
						
				if(mssg.getContextAttr() != null && pauseMssg != null)
				{
					pauseMssg.setContextAttr(mssg.getContextAttr());
				}
			}
			
			ideEventManager.raiseAsyncEvent(new DebugStepsExecutedEvent(this, mssg));
			return;
		}

		if(data instanceof ServerMssgConfirmation)
		{
			ServerMssgConfirmation confirmMssg = (ServerMssgConfirmation) data;
			
			if(!confirmMssg.isSuccessful())
			{
				IdeUtils.execute(() -> 
				{
					JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), confirmMssg.getError() + "\n\nPlease check logs for more details.");
				}, 1);
			}
		}
	}
	
	public Map<String, KeyValue> getContextAttributes()
	{
		ServerMssgExecutionPaused pauseMssg = pausedThreads.get(getActiveThreadId());
		
		if(pauseMssg == null)
		{
			return Collections.emptyMap();
		}
		
		return pauseMssg.getContextAttr();
	}
	
	public Map<String, KeyValue> getParamMap()
	{
		ServerMssgExecutionPaused pauseMssg = pausedThreads.get(getActiveThreadId());
		
		if(pauseMssg == null)
		{
			return Collections.emptyMap();
		}
		
		return pauseMssg.getParams();
	}

	public String[] getExtraArgs()
	{
		return extraArgs;
	}
	
	public ExecutionType getExecutionType()
	{
		return executionType;
	}

	public void terminate()
	{
		if(!process.isAlive())
		{
			return;
		}

		process.destroyForcibly();
	}

	private void appenConsoleHtml(String html)
	{
		synchronized(consoleHtml)
		{
			consoleHtml.add(html);
			lastConsoleChangeTime = System.currentTimeMillis();
			
			if(consoleHtml.size() > maxLines)
			{
				for(int i = 0; i < truncateLength; i++)
				{
					consoleHtml.removeFirst();
				}
			
				lastConsoleChangeTime = System.currentTimeMillis();
			}
		}
	}
	
	public long getLastConsoleChangeTime()
	{
		synchronized(consoleHtml)
		{
			return lastConsoleChangeTime;
		}
	}
	
	public String getConsoleHtml()
	{
		List<String> tmpLst = new LinkedList<>();
		
		synchronized(consoleHtml)
		{
			tmpLst.addAll(consoleHtml);
		}
		
		return tmpLst.stream().collect(Collectors.joining());
	}
	
	public void clearConsole()
	{
		synchronized(consoleHtml)
		{
			consoleHtml.clear();
			this.lastConsoleChangeTime = System.currentTimeMillis();
		}
	}


	private void logOnConsole(String lineText)
	{
		logger.debug(">>[{}] {}", name, lineText);

		lineText = lineText.replace("&", "&amp;");
		lineText = lineText.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		lineText = lineText.replace("<", "&lt;");
		lineText = lineText.replace(">", "&gt;");
		lineText = lineText.replace("\n", "<br/>");

		String lineHtml = null;
		String color = null;
		ConsoleLinePattern stdPattern = uiLayout.getStandardConsoleLinePattern();
		
		if(lastLinePattern == null)
		{
			lastLinePattern = stdPattern;
		}
		
		//if line has standard pattern of log line
		if(stdPattern.getPattern().matcher(lineText).find())
		{
			ConsoleLinePattern matchingPattern = null;
			
			//check for specific pattern for line style
			for(ConsoleLinePattern linePattern : uiLayout.getConsoleLinePatterns())
			{
				if(linePattern.getPattern().matcher(lineText).find())
				{
					matchingPattern = linePattern;
					break;
				}
			}
			
			matchingPattern = (matchingPattern == null) ? stdPattern : matchingPattern;
			color = matchingPattern.getColor();
			lastLinePattern = matchingPattern;
		}
		//if standard line pattern is not matched, follow the style of last line
		else
		{
			color = lastLinePattern.getColor();
		}
		
		
		lineHtml = String.format("<div style=\"color:%s;\">%s</div>", color, lineText);
		appenConsoleHtml(lineHtml);
	}
	
	private void readConsoleStream(InputStream input)
	{
		//input can be null, when error is redirected to output stream
		if(input == null)
		{
			return;
		}
		
		InputStreamReader inReader = new InputStreamReader(input);
		final BufferedReader reader = new BufferedReader(inReader);

		String line = null;

		try
		{
			while((line = reader.readLine()) != null)
			{
				logOnConsole(line);
			}

			reader.close();

			int code = process.waitFor();
			
			//client can be null, if process is exited without proper starting itself
			if(debugClient != null)
			{
				debugClient.close();
			}

			logger.debug("Sending env termination event...");
			logOnConsole("Process exited with code: " + code);

			synchronized(this)
			{
				if(!terminated)
				{
					terminated = true;
					
					File repFile = new File(reportFolder, "index.html");
					
					if(repFile.exists())
					{
						this.reportFile = repFile;
					}
					
					ideEventManager.raiseAsyncEvent(new EnvironmentTerminatedEvent(this));
				}
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while reading console stream", ex);
		}
	}

	public String getName()
	{
		return name;
	}

	public void stop()
	{
		process.destroyForcibly();
	}

	public synchronized boolean isTerminated()
	{
		return terminated;
	}

	public File getReportFolder()
	{
		return reportFolder;
	}

	public void sendDataToServer(ClientMessage data)
	{
		this.sendDataToServer(data, null);
	}

	public void sendDataToServer(ClientMessage data, IMessageCallback callback)
	{
		debugClient.sendDataToServer(data, callback);
	}

	public boolean isReportFileAvailable()
	{
		return (reportFile != null && reportFile.exists());
	}
	
	public File getReportFile()
	{
		return reportFile;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	public boolean isDebugEnv()
	{
		return debugEnv;
	}
	
	public String getActiveThreadId()
	{
		synchronized(pausedThreads)
		{
			//if explicit thread id is specified and is still valid, then return the same
			if(activeThreadId != null && pausedThreads.containsKey(activeThreadId))
			{
				return activeThreadId;
			}
			
			//if explicit thread is not specified or specified but not valid
			
			//and if other paused threads are present, then return first thread id
			if(!pausedThreads.isEmpty())
			{
				return pausedThreads.keySet().iterator().next();
			}
			
			return null;
		}
	}

	public void setActiveThreadId(String activeThreadId)
	{
		synchronized(pausedThreads)
		{
			if(!pausedThreads.containsKey(activeThreadId))
			{
				return;
			}
			
			this.activeThreadId = activeThreadId;	
		}
	}
	
	public ServerMssgExecutionPaused getThreadDetails(String threadId)
	{
		synchronized(pausedThreads)
		{
			return pausedThreads.get(threadId);
		}
	}

	public ServerMssgExecutionPaused getActiveThreadDetails()
	{
		return getThreadDetails(getActiveThreadId());
	}
	
	public void visitPausedThreads(Consumer<ServerMssgExecutionPaused> consumer)
	{
		synchronized(pausedThreads)
		{
			pausedThreads.values().forEach(mssg -> consumer.accept(mssg));
		}
	}
	
	public void clean()
	{
		try
		{
			if(reportFolder.exists())
			{
				FileUtils.forceDelete(reportFolder);
			}
		}catch(Exception ex)
		{
			logger.error("An error occurred while cleaning report folder: {}", reportFolder.getPath());
		}
	}

	@Override
	public String toString()
	{
		return name;
	}
}
