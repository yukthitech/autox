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
package com.yukthitech.autox.ide.exeenv;

import java.io.File;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.exeenv.debug.DebugPointsChangedEvent;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.services.IdeEventHandler;
import com.yukthitech.autox.ide.services.IdeEventManager;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manager to manage current environments.
 * @author akiran
 */
@Service
public class ExecutionEnvironmentManager
{
	private static Logger logger = LogManager.getLogger(ExecutionEnvironmentManager.class);
	
	@Autowired
	private IdeEventManager ideEventManager;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private ExecutionEnvironment activeEnvironment;
	
	private List<ExecutionEnvironment> runningEnvironments = new LinkedList<>();
	
	/**
	 * Fetches next available socket.
	 * @return next available socket.
	 */
	private synchronized int fetchNextAvailablePort()
	{
		try
		{
			ServerSocket testSocket = new ServerSocket(0);
			int nextPort = testSocket.getLocalPort();
			testSocket.close();
			
			return nextPort;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An errror occurred while fetching available socket", ex);
		}
	}
	
	private ExecutionEnvironment startAutoxEnvironment(ExecuteCommand executeCommand, String envName, String... extraArgs)
	{
		ExecutionType executionType = executeCommand.getExecutionType();
		Project project = executeCommand.getProject();
		File testSuiteFolder = executeCommand.getTestSuiteFolder();
		boolean debug = executeCommand.isDebug();
		
		String classpath = project.getProjectClassLoader().toClassPath();
		String javaCmd = "java";
		String outputDir = "autox-report";
		File reportFolder = new File(project.getBaseFolderPath(), outputDir);
		
		int debugPort = -1;
		//Eg: -Dautox.debug.enabled=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
		String debugArg = System.getProperty("autox.debug.enabled");
		
		//if debug args are not specified, use dummy args
		if(StringUtils.isBlank(debugArg))
		{
			debugArg = "-Ddummy=true";
		}
		
		List<String> command = new ArrayList<>( Arrays.asList(
			javaCmd, 
			"-classpath", classpath,
			debugArg,
			//"-D" + DebugServer.SYS_PROP_MONITOR_PORT + "=" + monitorPort,
			AutomationLauncher.class.getName(),
			project.getAppConfigFilePath(),
			"-prop", project.getAppPropertyFilePath(), 
			"-rf", reportFolder.getPath(),
			"--report-opening-disabled", "true"
		) );
		
		if(debug)
		{
			debugPort = fetchNextAvailablePort();
			command.addAll(Arrays.asList("--debug-port", "" + debugPort));
		}
		
		if(executionType != ExecutionType.SOURCE_FOLDER && testSuiteFolder != null)
		{
			command.addAll(Arrays.asList("--test-suite-folders", testSuiteFolder.getPath()));
		}
		
		command.addAll(Arrays.asList(extraArgs));
		
		StringBuilder initMssg = new StringBuilder();
		initMssg.append(String.format("Executing command: %s", command.stream().collect(Collectors.joining(" "))));
		
		logger.debug(initMssg);
		initMssg.append("\n\n");
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory( new File(project.getBaseFolderPath()) );
		builder.redirectErrorStream(true);
		
		try
		{
			ExecutionEnvironment env = new ExecutionEnvironment(executeCommand, envName, builder.start(), debugPort, 
					reportFolder, initMssg.toString(), extraArgs);
			IdeUtils.autowireBean(applicationContext, env);
			
			synchronized(runningEnvironments)
			{
				runningEnvironments.add(env);
			}
			
			ideEventManager.raiseAsyncEvent(new EnvironmentStartedEvent(env));
			
			return env;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while starting autox process", ex);
		}
	}
	
	@IdeEventHandler
	private void onEnviromentTerminate(EnvironmentTerminatedEvent event)
	{
		synchronized(runningEnvironments)
		{
			runningEnvironments.remove(event.getExecutionEnvironment());
		}
	}
	
	@IdeEventHandler
	private void onDebugPointChange(DebugPointsChangedEvent event)
	{
		List<ExecutionEnvironment> runningEnvironments = new ArrayList<>();
		
		synchronized(runningEnvironments)
		{
			runningEnvironments.addAll(this.runningEnvironments);
		}
		
		runningEnvironments.forEach(env -> env.onDebugPointChange(event));
	}
	
	private ExecutionEnvironment executeTestSuite(ExecuteCommand executeCommand)
	{
		return startAutoxEnvironment(executeCommand, "ts-" + executeCommand.getName(), "-ts", executeCommand.getName());
	}
	
	private ExecutionEnvironment executeTestCase(ExecuteCommand executeCommand)
	{
		return startAutoxEnvironment(executeCommand, "tc-" + executeCommand.getName(), "-tc", executeCommand.getName());
	}
	
	private ExecutionEnvironment executeFolder(ExecuteCommand executeCommand, List<File> folders)
	{
		String foldersPath = folders.stream()
			.map(file -> file.getPath())
			.collect(Collectors.joining(","));
		
		String firstFolderName = folders.get(0).getName();
		return startAutoxEnvironment(executeCommand, "dir-" + firstFolderName, "-flmt", foldersPath);
	}

	private ExecutionEnvironment executeSourceFolder(ExecuteCommand executeCommand, File sourceFolder)
	{
		return startAutoxEnvironment(executeCommand, "tsDir-" + sourceFolder.getName(), "--test-suite-folders", sourceFolder.getPath());
	}

	private ExecutionEnvironment executeProject(ExecuteCommand executeCommand)
	{
		return startAutoxEnvironment(executeCommand, "prj-" + executeCommand.getProject().getName());
	}

	ExecutionEnvironment execute(ExecuteCommand executeCommand)
	{
		switch (executeCommand.getExecutionType())
		{
			case TEST_CASE:
			{
				return executeTestCase(executeCommand);
			}
			case TEST_SUITE:
			{
				return executeTestSuite(executeCommand);
			}
			case FOLDER:
			{
				return executeFolder(executeCommand, Arrays.asList(new File(executeCommand.getName())));
			}
			case SOURCE_FOLDER:
			{
				return executeSourceFolder(executeCommand, new File(executeCommand.getName()));
			}
			case PROJECT:
			{
				return executeProject(executeCommand);
			}
			default:
			{
				throw new InvalidArgumentException("Invalid execution type specified: {}", executeCommand.getExecutionType());
			}
		}
	}

	public ExecutionEnvironment getActiveEnvironment()
	{
		return activeEnvironment;
	}

	public void setActiveEnvironment(ExecutionEnvironment activeEnvironment)
	{
		this.activeEnvironment = activeEnvironment;
		
		ideEventManager.raiseAsyncEvent(new EnvironmentActivationEvent(activeEnvironment));
	}
}
