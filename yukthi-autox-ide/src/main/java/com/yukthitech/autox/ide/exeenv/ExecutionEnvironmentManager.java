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
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.debug.server.DebugServer;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.model.Project;
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
	private IdeContext ideContext;
	
	@Autowired
	private UiLayout uiLayout;
	
	/**
	 * Mapping from project to execution environment.
	 */
	private Map<String, ExecutionEnvironment> interactiveEnvironments = new HashMap<>();
	
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
	
	private ExecutionEnvironment startAutoxEnvironment(ExecutionType executionType, String envName, Project project, String... extraArgs)
	{
		String classpath = project.getProjectClassLoader().toClassPath();
		String javaCmd = "java";
		String outputDir = "autox-report";
		File reportFolder = new File(project.getBaseFolderPath(), outputDir);
		
		int monitorPort = fetchNextAvailablePort();
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
		
		command.addAll(Arrays.asList(extraArgs));
		
		StringBuilder initMssg = new StringBuilder();
		initMssg.append(String.format("Executing command: %s", command.stream().collect(Collectors.joining(" "))));
		
		logger.debug(initMssg);
		initMssg.append("\n\n");
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory( new File(project.getBaseFolderPath()) );
		
		//TODO: Temp workaround to avoid swap lines between out and err streams. Need proper fix.
		builder.redirectErrorStream(true);
		
		try
		{
			ExecutionEnvironment env = new ExecutionEnvironment(executionType, project, envName, builder.start(), ideContext.getProxy(), monitorPort, 
					reportFolder, initMssg.toString(), uiLayout, extraArgs);
			
			ideContext.getProxy().newEnvironmentStarted(env);
			
			return env;
		}catch(IOException ex)
		{
			throw new InvalidStateException("An error occurred while starting autox process", ex);
		}
	}
	
	private ExecutionEnvironment executeTestSuite(Project project, String testSuite)
	{
		return startAutoxEnvironment(ExecutionType.TEST_SUITE, "ts-" + testSuite, project, "-ts", testSuite);
	}
	
	private ExecutionEnvironment executeTestCase(Project project, String testCase)
	{
		return startAutoxEnvironment(ExecutionType.TEST_CASE, "tc-" + testCase, project, "-tc", testCase);
	}
	
	private ExecutionEnvironment executeFolder(Project project, List<File> testSuiteFolder)
	{
		String foldersPath = testSuiteFolder.stream()
			.map(file -> file.getPath())
			.collect(Collectors.joining(","));
		
		String firstFolderName = testSuiteFolder.get(0).getName();
		return startAutoxEnvironment(ExecutionType.FOLDER, "dir-" + firstFolderName, project, "-flmt", foldersPath);
	}

	private ExecutionEnvironment executeSourceFolder(Project project, File sourceFolder)
	{
		return startAutoxEnvironment(ExecutionType.SOURCE_FOLDER, "dir-" + sourceFolder.getName(), project, "-tsf", sourceFolder.getPath());
	}

	private ExecutionEnvironment executeProject(Project project)
	{
		return startAutoxEnvironment(ExecutionType.PROJECT, project.getName(), project);
	}

	synchronized ExecutionEnvironment getInteractiveEnvironment(Project project)
	{
		ExecutionEnvironment env = interactiveEnvironments.get(project.getProjectFilePath());
		
		if(env != null && !env.isTerminated())
		{
			return env;
		}
		
		return null;
	}

	private synchronized ExecutionEnvironment startInteractiveEnvironment(Project project, boolean executeGlobalSetup)
	{
		ExecutionEnvironment env = interactiveEnvironments.get(project.getProjectFilePath());
		
		if(env != null && !env.isTerminated())
		{
			throw new InvalidStateException("For project '{}' interactive environment is already running", project.getName());
		}
		
		env = startAutoxEnvironment(ExecutionType.INTERACTIVE, "*Interactive-" + project.getName(), project, 
				"--interactive-environment", "true", 
				"--interactive-execution-global", "" + executeGlobalSetup);
		
		env.setInteractive(true);
		interactiveEnvironments.put(project.getProjectFilePath(), env);
		
		return env;
	}
	
	ExecutionEnvironment execute(ExecutionType executionType, Project project, String name)
	{
		switch (executionType)
		{
			case TEST_CASE:
			{
				return executeTestCase(project, name);
			}
			case TEST_SUITE:
			{
				return executeTestSuite(project, name);
			}
			case FOLDER:
			{
				return executeFolder(project, Arrays.asList(new File(name)));
			}
			case SOURCE_FOLDER:
			{
				return executeSourceFolder(project, new File(name));
			}
			case PROJECT:
			{
				return executeProject(project);
			}
			case INTERACTIVE:
			{
				return startInteractiveEnvironment(project, true);
			}
			default:
			{
				throw new InvalidArgumentException("Invalid execution type specified: {}", executionType);
			}
		}
	}
}
