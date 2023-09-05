package com.yukthitech.autox.event;

/**
 * Enumeration of automation event types.
 * @author Kranthi
 */
public enum AutomationEventType
{
	/**
	 * Occurs when automation is started (even before parsing anything like cmd args, files). 
	 */
	AUTOMATION_STARTED,
	
	/**
	 * Raised when invalid command line arguments are specified.
	 */
	AUTOMATION_ERR_INVALID_CMD_ARG,
	
	/**
	 * Raised when invalid app configuration file is specified.
	 */
	AUTOMATION_ERR_INVALID_CONFIG,
	
	/**
	 * Raised when source file parsing fails.
	 */
	AUTOMATION_ERR_INVALID_SOURCE,
	
	/**
	 * Raised when unhandled error occurs.
	 */
	AUTOMATION_ERR_UNHANDLED,
	
	/**
	 * Invoked when all the configs and source is parsed and loaded. 
	 * Handlers can be used to estimate the work/time involved. The execution-info provides
	 * full hierarchy tree which is going to be executed (excluding dynamically/data-provider test cases).
	 */
	AUTOMATION_INITIALIZED,
	
	/**
	 * Invoked when an executable part (setup, test-case or cleanup) execution is started.
	 */
	EXECUTION_STARTED,
	
	/**
	 * Invoked when an executable part (setup, test-case or cleanup) execution is completed.
	 */
	EXECUTION_ENDED,
	
	/**
	 * Invoked when full automation execution is completed. Corresponding even holds flag indicating
	 * if automation execution was successful or not.
	 */
	AUTOMATION_ENDED
}
