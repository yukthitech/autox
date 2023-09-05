package com.yukthitech.autox.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.exec.Executor;
import com.yukthitech.autox.exec.report.ExecutionStatusReport;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.test.TestStatus;

/**
 * Represents execution info along with child and parent execution details linking.
 */
public class ExecutionInfo
{
	/**
	 * Current executor.
	 */
	private Executor executor;
	
	/**
	 * Parent event manager.
	 */
	private AutomationEventManager eventManager;

	/**
	 * Instantiates a new execution info.
	 *
	 * @param executor the executor
	 * @param eventManager the event manager
	 */
	ExecutionInfo(Executor executor, AutomationEventManager eventManager)
	{
		this.executor = executor;
		this.eventManager = eventManager;
	}
	
	/**
	 * Checks if setup is present as part of this execution.
	 *
	 * @return true, if setup is present as part of this execution
	 */
	public boolean hasSetup()
	{
		return executor.hasSetup();
	}
	
	/**
	 * Checks if cleanup is present as part of this execution.
	 *
	 * @return true, if cleanup is present as part of this execution
	 */
	public boolean hasCleanup()
	{
		return executor.hasCleanup();
	}
	
	/**
	 * Checks if before-child is present as part of this execution.
	 *
	 * @return true, if if before-child is present as part of this execution
	 */
	public boolean hasBeforeChild()
	{
		return executor.hasBeforeChild();
	}
	
	/**
	 * Checks if after-child is present as part of this execution.
	 *
	 * @return true, if after-child is present as part of this execution.
	 */
	public boolean hasAfterChild()
	{
		return executor.hasAfterChild();
	}
	
	/**
	 * Gets the current/live status of this execution.
	 *
	 * @return the status
	 */
	public TestStatus getStatus()
	{
		return executor.getStatus();
	}
	
	/**
	 * Gets the status message.
	 *
	 * @return the status message
	 */
	public String getStatusMessage()
	{
		return executor.getStatusMessage();
	}

	/**
	 * Gets unique representation of this execution. Generally this will name of the test-suite or test-case. For
	 * data provider based test-cases this will have data-provider name along with test case name.
	 *
	 * @return the representation name of this execution.
	 */
	public String getRepresentation()
	{
		return executor.getUniqueId();
	}
	
	/**
	 * Gets the child executions.
	 *
	 * @return the child executions
	 */
	public List<ExecutionInfo> getChildExecutions()
	{
		List<Executor> childExecutors = executor.getChildExecutors();
		
		if(CollectionUtils.isEmpty(childExecutors))
		{
			return Collections.emptyList();
		}
		
		List<ExecutionInfo> childInfoLst = childExecutors.stream()
				.map(executor -> eventManager.toExecutionInfo(executor))
				.collect(Collectors.toList());
		
		return new ArrayList<>(childInfoLst);
	}
	
	/**
	 * Gets the parent execution.
	 *
	 * @return the parent execution
	 */
	public ExecutionInfo getParentExecution()
	{
		Executor parent = executor.getParentExecutor();
		
		if(parent == null)
		{
			return null;
		}
		
		return eventManager.toExecutionInfo(parent);
	}
	
	/**
	 * Gets the status report, which hold status, time information of this execution.
	 *
	 * @return the status report
	 */
	public ExecutionStatusReport getStatusReport()
	{
		return ReportDataManager.getInstance().getExecutionStatusReport(executor);
	}
}
