package com.yukthitech.autox.context;

/**
 * Session-scoped dynamic service registered on {@link AutomationContext} by unique name.
 * Closed automatically when the context is reset.
 */
public interface IDynAutomationService
{
	void close();
}
