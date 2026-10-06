package com.yukthitech.autox.debug;

import java.io.File;

import com.yukthitech.autox.ILocationBased;

public interface IDebugFlowManager
{
	void checkForDebugPoint(ILocationBased step);

	void addSinglePauseDebugPoint(File sourceFile, int lineNumber);
}
