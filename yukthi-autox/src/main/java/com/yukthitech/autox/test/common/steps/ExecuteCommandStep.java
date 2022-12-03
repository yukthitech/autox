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
package com.yukthitech.autox.test.common.steps;

import java.io.PrintStream;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.Command;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.LogLevel;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executes specified OS command.
 * 
 * @author akiran
 */
@Executable(name = "executeCommand", group = Group.Common, message = "Executes specified OS command.")
public class ExecuteCommandStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the command, which is used in generating log file.
	 */
	@Param(description = "Name of the command, which is used in generating log file. This name will be used to set context attribute with generate log file path.", required = true)
	private String name;

	/**
	 * Command to be executed.
	 */
	@Param(description = "Command to be executed.", required = true)
	private String command;

	/**
	 * Directory in which command has to be executed.
	 */
	@Param(description = "Directory in which command has to be executed.", required = false)
	private String workingDirectory;
	
	/**
	 * Expected exit code of command. If specified, exit code will be validated with actual exit code.
	 */
	@Param(description = "Expected exit code of command. If specified, exit code will be validated with actual exit code.", required = false)
	private Integer expectedExitCode;

	public void setName(String name)
	{
		this.name = name;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}

	public void setWorkingDirectory(String workingDirectory)
	{
		this.workingDirectory = workingDirectory;
	}
	
	public void setExpectedExitCode(Integer expectedExitCode)
	{
		this.expectedExitCode = expectedExitCode;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		Command command = new Command(name, this.command, workingDirectory);
		
		ReportLogFile outFile = exeLogger.logFile("Executing command and output is redirected to below file:\n", LogLevel.DEBUG, name, ".log");
		PrintStream out = new PrintStream(outFile.getFile());
		
		try
		{
			int exitCode = command.execute(context, new Command.ICommandLogger()
			{
				@Override
				public void warn(String mssg, Object... args)
				{
					exeLogger.debug("[WARN]" + mssg, args);
				}
				
				@Override
				public void info(String mssg, Object... args)
				{
					exeLogger.debug("[INFO]" + mssg, args);
				}
				
				@Override
				public void debug(String mssg, Object... args)
				{
					exeLogger.debug(mssg, args);
				}
				
				@Override
				public void output(String line)
				{
					out.println(line);
				}
			});
			
			exeLogger.debug("Command is exited with exit code: {}", exitCode);
			
			if(expectedExitCode != null && exitCode != expectedExitCode)
			{
				throw new InvalidStateException("Expected exit-code {} is not matching actual exit code: {}", expectedExitCode, exitCode);
			}
		}finally
		{
			out.close();			
		}
	}
}
