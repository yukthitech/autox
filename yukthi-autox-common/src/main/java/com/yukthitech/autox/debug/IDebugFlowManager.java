package com.yukthitech.autox.debug;

import java.io.File;

import com.yukthitech.autox.ILocationBased;

public interface IDebugFlowManager
{
	void checkForDebugPoint(ILocationBased step);

	void addSinglePauseDebugPoint(File sourceFile, int lineNumber);

	void pauseAtErrorPoint(ILocationBased step);

	/**
	 * Releases live debug points and clears configured breakpoints for this manager instance.
	 */
	void reset();
}
