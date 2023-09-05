package com.yukthitech.autox.event;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.exec.ExecutionType;
import com.yukthitech.autox.exec.Executor;
import com.yukthitech.autox.exec.report.ReportDataManager;


public class AutomationEventManager
{
	private static Logger logger = LogManager.getLogger(AutomationEventManager.class);
	
	private static AutomationEventManager instance;
	
	private List<IAutomationListener> listeners = new ArrayList<>();
	
	private IdentityHashMap<Executor, ExecutionInfo> executionInfoMap = new IdentityHashMap<>();
	
	public static synchronized AutomationEventManager getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		
		instance = new AutomationEventManager();
		
		String listenerNamesStr = System.getProperty(IAutomationConstants.AUTOX_SYS_PROP_LISTENERS);
		
		if(StringUtils.isBlank(listenerNamesStr))
		{
			return instance;
		}
		
		String listenerNames[] = listenerNamesStr.trim().split("\\s*\\,\\s*");
		Object listener = null;
		
		for(String name : listenerNames)
		{
			try
			{
				Class<?> cls = Class.forName(name);
				listener = cls.newInstance();
			}catch(Exception ex)
			{
				logger.error("An error occurred while initializing automation listener of type: " + name, ex);
				
				System.err.println("An error occurred while initializing automation listener of type: " + name);
				System.exit(-1);
			}
			
			if(!(listener instanceof IAutomationListener))
			{
				System.err.println(String.format("Invalid listener is specified in '%s' system property: %s", 
						IAutomationConstants.AUTOX_SYS_PROP_LISTENERS, name));
				System.exit(-1);
			}
			
			instance.listeners.add((IAutomationListener) listener);
		}
		
		return instance;
	}
	
	public List<IAutomationListener> getListeners()
	{
		return listeners;
	}
	
	public synchronized ExecutionInfo toExecutionInfo(Executor executor)
	{
		ExecutionInfo info = executionInfoMap.get(executor);
		
		if(info != null)
		{
			return info;
		}
		
		info = new ExecutionInfo(executor, this);
		executionInfoMap.put(executor, info);
		return info;
	}
	
	private void raiseEvent(AutomationEvent event)
	{
		if(listeners.isEmpty())
		{
			return;
		}
		
		for(IAutomationListener listener : listeners)
		{
			try
			{
				logger.debug("Invoking listener {} for event: {}", listener.getClass().getName(), event);
				listener.handleEvent(event);
			}catch(Exception ex)
			{
				logger.warn("IGNORED: An error occurred while invokig automation event listener: {}", listener.getClass().getName(), ex);
			}
		}
	}
	
	public void onAppStart()
	{
		raiseEvent(new AutomationEvent(AutomationEventType.AUTOMATION_STARTED));
	}
	
	public void onAppError(AutomationEventType errType, String message, Object... args)
	{
		raiseEvent(
			new AutomationEvent(errType)
				.setMessage(message, args)
			);
	}
	
	public void onInitialize(Executor rootExecutor)
	{
		raiseEvent(
				new AutomationEvent(AutomationEventType.AUTOMATION_INITIALIZED)
					.setExecutionInfo(toExecutionInfo(rootExecutor))
				);
	}
	
	public void executionStarted(Executor executor, ExecutionType executionType)
	{
		raiseEvent(
				new AutomationEvent(AutomationEventType.EXECUTION_STARTED)
					.setExecutionInfo(toExecutionInfo(executor))
					.setExecutionType(executionType)
				);
	}
	
	public void executionEnded(Executor executor, ExecutionType executionType)
	{
		raiseEvent(
				new AutomationEvent(AutomationEventType.EXECUTION_ENDED)
					.setExecutionInfo(toExecutionInfo(executor))
					.setExecutionType(executionType)
				);
	}
	
	public void onAppEnd(Executor rootExecutor)
	{
		boolean successful = ReportDataManager.getInstance().isSuccessful();
		raiseEvent(
				new AutomationEvent(AutomationEventType.AUTOMATION_ENDED)
					.setSuccessful(successful)
					.setExecutionInfo(toExecutionInfo(rootExecutor))
				);
	}
}
