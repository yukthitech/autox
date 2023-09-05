package com.yukthitech.autox.event;

import com.yukthitech.autox.exec.ExecutionType;

/**
 * Represents an automation event with all corresponding information.
 * @author Kranthi
 */
public class AutomationEvent
{
	/**
	 * Type of event.
	 */
	private AutomationEventType type;
	
	/**
	 * In case of error this will hold the error message.
	 */
	private String message;
	
	/**
	 * Populated only for {@link AutomationEventType#AUTOMATION_ENDED} event. And this flag indicates if overall
	 * execution was successful or failure. Further information can be obtained from current {@link #executionInfo}.
	 */
	private boolean successful;
	
	/**
	 * From {@link AutomationEventType#AUTOMATION_INITIALIZED} this field will be populated. And this will hold
	 * the executor info which resulted in this info. For {@link AutomationEventType#AUTOMATION_INITIALIZED} and 
	 * {@link AutomationEventType#AUTOMATION_ENDED} root executor info is populated.
	 */
	private ExecutionInfo executionInfo;
	
	/**
	 * This is populated only for {@link AutomationEventType#EXECUTION_STARTED} and {@link AutomationEventType#EXECUTION_ENDED} events.
	 * And this will hold info like what type of execution is being performed on corresponding executor - setup, main or cleanup.
	 */
	private ExecutionType executionType;

	public AutomationEvent(AutomationEventType type)
	{
		this.type = type;
	}
	
	AutomationEvent setMessage(String message, Object... args)
	{
		this.message = String.format(message, args);
		return this;
	}
	
	AutomationEvent setSuccessful(boolean successful)
	{
		this.successful = successful;
		return this;
	}
	
	AutomationEvent setExecutionInfo(ExecutionInfo executionInfo)
	{
		this.executionInfo = executionInfo;
		return this;
	}
	
	AutomationEvent setExecutionType(ExecutionType executionType)
	{
		this.executionType = executionType;
		return this;
	}

	/**
	 * Gets the type of event.
	 *
	 * @return the type of event
	 */
	public AutomationEventType getType()
	{
		return type;
	}

	/**
	 * Gets the in case of error this will hold the error message.
	 *
	 * @return the in case of error this will hold the error message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Checks if is populated only for
	 * {@link AutomationEventType#AUTOMATION_ENDED} event. And this flag
	 * indicates if overall execution was successful or failure. Further
	 * information can be obtained from current {@link #executionInfo}.
	 *
	 * @return the populated only for
	 *         {@link AutomationEventType#AUTOMATION_ENDED} event
	 */
	public boolean isSuccessful()
	{
		return successful;
	}

	/**
	 * Gets the from {@link AutomationEventType#AUTOMATION_INITIALIZED} this
	 * field will be populated. And this will hold the executor info which
	 * resulted in this info. For
	 * {@link AutomationEventType#AUTOMATION_INITIALIZED} and
	 * {@link AutomationEventType#AUTOMATION_ENDED} root executor info is
	 * populated.
	 *
	 * @return the from {@link AutomationEventType#AUTOMATION_INITIALIZED} this
	 *         field will be populated
	 */
	public ExecutionInfo getExecutionInfo()
	{
		return executionInfo;
	}
	
	/**
	 * Gets the this is populated only for
	 * {@link AutomationEventType#EXECUTION_STARTED} and
	 * {@link AutomationEventType#EXECUTION_ENDED} events. And this will hold
	 * info like what type of execution is being performed on corresponding
	 * executor - setup, main or cleanup.
	 *
	 * @return the this is populated only for
	 *         {@link AutomationEventType#EXECUTION_STARTED} and
	 *         {@link AutomationEventType#EXECUTION_ENDED} events
	 */
	public ExecutionType getExecutionType()
	{
		return executionType;
	}
}
