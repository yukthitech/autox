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
package com.yukthitech.autox.exec;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.config.ErrorDetails;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.plugin.IPluginSession;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.utils.ObjectWrapper;

public class ExecutorUtils
{
	private static Logger logger = LogManager.getLogger(ExecutorUtils.class);
	
	public static void invokeErrorHandling(Executable executable, ErrorDetails errorDetails)
	{
		logger.debug( "Invoking plugin error handling for executable: {}", executable.name() );
		
		AutomationContext context = AutomationContext.getInstance();
		Collection<IPluginSession> pluginSessions = ExecutionContextManager.getInstance().getExecutionContextStack().getPluginSessions();
		
		if(pluginSessions == null)
		{
			logger.debug( "No associated plugins found in current context.");
			return;
		}
		
		for(IPluginSession  pluginSession : pluginSessions)
		{
			if(pluginSession == null)
			{
				continue;
			}
			
			logger.debug("Invoking error handling of plugin - {}", pluginSession.getClass().getName());
			
			try
			{
				pluginSession.handleError(context, errorDetails);
			}catch(Exception ex)
			{
				logger.error("An error occurred during plugin-error-handling with plugin: {}", pluginSession, ex);
			}
		}
	}
	
	/**
	 * Creates executable proxy annotation for specified step-group.
	 * @param function
	 * @return
	 */
	public static Executable createExecutable(final Function function)
	{
		Executable executable = (Executable) Proxy.newProxyInstance(ExecutorUtils.class.getClassLoader(), new Class[] {Executable.class}, new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				String methodName = method.getName();
				
				if("name".equals(methodName) || "message".equals(methodName))
				{
					return "Function-" + function.getName();
				}
				
				return null;
			}
		});

		return executable;
	}

	public static boolean executeSetup(Setup setup, String mode, Executor executor)
	{
		if(setup == null)
		{
			return true;
		}
		
		ReportDataManager reportManager = ReportDataManager.getInstance();
		executor.activeExecutionLogger = reportManager.getSetupExecutionLogger(executor);
		executor.activeExecutionLogger.setMode(mode);
		
		ObjectWrapper<IStep> currentStep = new ObjectWrapper<>();
		
		try
		{
			reportManager.executionStarted(ExecutionType.SETUP, executor);
			
			StepsExecutor.execute(setup.getSteps(), currentStep);
			reportManager.executionCompleted(ExecutionType.SETUP, executor);
			
			return true;
		}catch(Exception ex)
		{
			executor.setStatus(TestStatus.ERRORED, "Setup failed with error: " + ex.getMessage());
			
			Executable executable = currentStep.getValue().getClass().getAnnotation(Executable.class);
			invokeErrorHandling(executable, new ErrorDetails(executor.activeExecutionLogger, currentStep.getValue(), ex));

			//logger.error("An error occurred during setup execution", ex);
			reportManager.executionErrored(ExecutionType.SETUP, executor, "Setup failed with error: " + ex.getMessage());
			//reportManager.executionErrored(ExecutionType.MAIN, executor, "Setup failed with error: " + ex.getMessage());
			
			return false;
		}finally
		{
			executor.activeExecutionLogger.clearMode();
		}
	}
	
	public static boolean executeCleanup(Cleanup cleanup, String mode, Executor executor)
	{
		if(cleanup == null)
		{
			return true;
		}

		ReportDataManager reportManager = ReportDataManager.getInstance();
		executor.activeExecutionLogger = reportManager.getCleanupExecutionLogger(executor);
		executor.activeExecutionLogger.setMode(mode);
		
		ObjectWrapper<IStep> currentStep = new ObjectWrapper<>();
		
		try
		{
			reportManager.executionStarted(ExecutionType.CLEANUP, executor);
			
			StepsExecutor.execute(cleanup.getSteps(), currentStep);
			reportManager.executionCompleted(ExecutionType.CLEANUP, executor);
			
			return true;
		}catch(Exception ex)
		{
			executor.setStatus(TestStatus.ERRORED, "Cleanup failed with error: " + ex.getMessage());
			
			Executable executable = currentStep.getValue().getClass().getAnnotation(Executable.class);
			invokeErrorHandling(executable, new ErrorDetails(executor.activeExecutionLogger, currentStep.getValue(), ex));

			//logger.error("An error occurred during cleanup execution", ex);
			reportManager.executionErrored(ExecutionType.CLEANUP, executor, "Cleanup failed with error: " + ex.getMessage());
			//reportManager.executionErrored(ExecutionType.MAIN, executor, "Cleanup failed with error: " + ex.getMessage());

			return false;
		}finally
		{
			executor.activeExecutionLogger.clearMode();
		}
	}

}
