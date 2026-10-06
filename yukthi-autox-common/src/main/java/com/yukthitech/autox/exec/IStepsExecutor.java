package com.yukthitech.autox.exec;

import java.util.List;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepListener;
import com.yukthitech.utils.ObjectWrapper;

public interface IStepsExecutor
{
	void execute(List<IStep> steps, ObjectWrapper<IStep> currentStep, String parentFrameId) throws Exception;

	void addStepListener(IStepListener listener);

	void removeStepListener(IStepListener listener);
}
