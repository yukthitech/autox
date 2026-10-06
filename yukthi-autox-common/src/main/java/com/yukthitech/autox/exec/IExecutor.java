package com.yukthitech.autox.exec;

import java.util.List;

import com.yukthitech.autox.exec.report.IExecutionLogger;

public interface IExecutor
{
	String getUniqueId();

	Object getExecutable();

	IExecutor getParentExecutor();

	boolean isParentContextShared();

	List<? extends IExecutor> getDependencies();

	IExecutionLogger getActiveExecutionLogger();
}
