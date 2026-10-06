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
package com.yukthitech.autox.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;

/**
 * OS command to execute.
 * @author akiran
 */
public class Command
{
	public static interface ICommandLogger
	{
		public void debug(String mssg, Object... args);
		
		public void warn(String mssg, Object... args);
		
		public void info(String mssg, Object... args);
		
		public void output(String line);
	}
	
	/**
	 * Name for this command.
	 */
	private String name;
	
	/**
	 * Command to execute.
	 */
	private String command;
	
	/**
	 * Working directory to be used for command.
	 */
	private String workingDir;
	
	/**
	 * Resource file to load on context with this command name. The resource can be properties file, map file or xml file.
	 */
	private String resourceFile;
	
	public Command()
	{}
	
	public Command(String name, String command, String workingDir)
	{
		this.name = name;
		this.command = command;
		this.workingDir = workingDir;
	}

	/**
	 * Gets the name for this command.
	 *
	 * @return the name for this command
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name for this command.
	 *
	 * @param name the new name for this command
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the command to execute.
	 *
	 * @return the command to execute
	 */
	public String getCommand()
	{
		return command;
	}

	/**
	 * Sets the command to execute.
	 *
	 * @param command the new command to execute
	 */
	public void setCommand(String command)
	{
		this.command = command;
	}

	/**
	 * Gets the working directory to be used for command.
	 *
	 * @return the working directory to be used for command
	 */
	public String getWorkingDir()
	{
		return workingDir;
	}

	/**
	 * Sets the working directory to be used for command.
	 *
	 * @param workingDir the new working directory to be used for command
	 */
	public void setWorkingDir(String workingDir)
	{
		this.workingDir = workingDir;
	}
	
	/**
	 * Gets the resource file to load on context with this command name. The resource can be properties file, map file or xml file.
	 *
	 * @return the resource file to load on context with this command name
	 */
	public String getResourceFile()
	{
		return resourceFile;
	}

	/**
	 * Sets the resource file to load on context with this command name. The resource can be properties file, map file or xml file.
	 *
	 * @param resourceFile the new resource file to load on context with this command name
	 */
	public void setResourceFile(String resourceFile)
	{
		this.resourceFile = resourceFile;
	}

	/**
	 * Split args.
	 *
	 * @param command the command
	 * @return the string[]
	 */
	private static String[] splitArgs(String command)
	{
		List<String> tokens = new ArrayList<String>();
		StringBuilder token = new StringBuilder();
		boolean quoted = false;
		
		for(char ch : command.toCharArray())
		{
			if(!quoted && Character.isWhitespace(ch))
			{
				if(token.length() > 0)
				{
					tokens.add(token.toString());
					token.setLength(0);
				}
				
				continue;
			}

			if(ch == '\"')
			{
				token.append(ch);

				if(quoted)
				{
					if(token.length() > 0)
					{
						tokens.add(token.toString());
						token.setLength(0);
					}
					
					quoted = false;
					continue;
				}
				
				quoted = true;
				continue;
			}
			
			if(ch == '\'')
			{
				if(quoted)
				{
					if(token.length() > 0)
					{
						tokens.add(token.toString());
						token.setLength(0);
					}
					
					quoted = false;
					continue;
				}
				
				quoted = true;
				continue;
			}

			token.append(ch);
		}
		
		if(token.length() > 0)
		{
			tokens.add(token.toString().trim());
		}
		
		return tokens.toArray(new String[0]);
	}
	
	/**
	 * Execute command.
	 *
	 * @param cmd the cmd
	 * @param workDir the work dir
	 */
	private int executeCommand(String cmd, String workDir, ICommandLogger logger)
	{
		logger.info("Executing command with name {} and with command: \n\t{}", name, cmd);
		
		String cmdArgs[] = splitArgs(cmd);
		
		logger.debug("After split the command is: {}", Arrays.toString(cmdArgs));
		
		ProcessBuilder processBuilder = new ProcessBuilder(cmdArgs);
		processBuilder.redirectErrorStream(true);
		
		if(workDir != null)
		{
			processBuilder.directory(new File(workDir));
		}
		
		logger.debug("Executing command in directory: {}", workDir);

		try
		{
			Process process = processBuilder.start();
	
			InputStream is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	
			String line = null;
			
			while((line = reader.readLine()) != null)
			{
				logger.output(line);
			}
			
			is.close();
	
			int exitCode = process.waitFor();
			
			logger.debug("Command {} exited with code: {}", name, exitCode);
			return exitCode;
		}catch(Exception ex)
		{
			logger.warn("IGNORED ERROR: An error occurred while executing command {}", name, ex);
		}
		
		return -1;
	}

	/**
	 * Execute the current command (if any) and post loads the resource file specified, if any.
	 *
	 * @param context the context
	 */
	public int execute(AutomationContext context, ICommandLogger logger)
	{
		String workDir = (workingDir != null) ? AutomationUtils.replaceExpressionsInString("cmd.workDir", context, workingDir) : null;
		String cmd = (command != null) ? AutomationUtils.replaceExpressionsInString("cmd.cmd", context, command) : null;
		int exitCode = 0;
	
		if(cmd != null)
		{
			exitCode = executeCommand(cmd, workDir, logger);
		}
		else
		{
			logger.debug("As no command is specified, no os command execution is taking place as part of command: {}", name);
		}
		
		String res = (resourceFile != null) ? AutomationUtils.replaceExpressionsInString("cmd.res", context, resourceFile) : null;
		
		if(res == null)
		{
			return exitCode;
		}
		
		logger.debug("As part of command '{}' loading resource file: {}", name, res);
		
		File resFile = new File(res);
		
		if(resFile.exists())
		{
			logger.warn("IGNORED ERROR: As part of command '{}' specified resource file is not found for loading: {}", name, res);
			return exitCode;
		}
		
		try
		{
			String data = FileUtils.readFileToString(resFile, Charset.defaultCharset());
			Object resObj = AutomationUtils.loadObjectContent(data, res, null, null);
			
			context.setAttribute(name, resObj);
		}catch(Exception ex)
		{
			logger.warn("IGNORED ERROR: An error occurred in command '{}' while loading resource file: {}", name, res, ex);
		}
		
		return exitCode;
	}
}
