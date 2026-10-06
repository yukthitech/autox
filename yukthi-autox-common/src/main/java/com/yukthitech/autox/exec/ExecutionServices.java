package com.yukthitech.autox.exec;

import com.yukthitech.autox.debug.IDebugFlowManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidStateException;

public final class ExecutionServices
{
	private static IStepsExecutor stepsExecutor;
	private static IDebugFlowManager debugFlowManager;
	private static IExecutionLoggerFactory executionLoggerFactory;

	public interface IExecutionLoggerFactory
	{
		IExecutionLogger create();
	}

	private ExecutionServices()
	{
	}

	public static IStepsExecutor getStepsExecutor()
	{
		if(stepsExecutor == null)
		{
			throw new InvalidStateException("Steps executor is not registered");
		}
		return stepsExecutor;
	}

	public static void setStepsExecutor(IStepsExecutor executor)
	{
		stepsExecutor = executor;
	}

	public static IDebugFlowManager getDebugFlowManager()
	{
		if(debugFlowManager == null)
		{
			throw new InvalidStateException("Debug flow manager is not registered");
		}
		return debugFlowManager;
	}

	public static void setDebugFlowManager(IDebugFlowManager manager)
	{
		debugFlowManager = manager;
	}

	public static IExecutionLogger getExecutionLogger()
	{
		if(executionLoggerFactory != null)
		{
			return executionLoggerFactory.create();
		}
		return com.yukthitech.autox.exec.report.Log4jExecutionLogger.getInstance();
	}

	public static void setExecutionLoggerFactory(IExecutionLoggerFactory factory)
	{
		executionLoggerFactory = factory;
	}

	public static void reset()
	{
		stepsExecutor = null;
		debugFlowManager = null;
		executionLoggerFactory = null;
	}
}
