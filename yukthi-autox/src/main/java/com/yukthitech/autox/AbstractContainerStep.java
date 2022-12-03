/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.autox;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.common.SkipParsing;

public abstract class AbstractContainerStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Steps for the test case.
	 */
	@SkipParsing
	protected List<IStep> steps = new ArrayList<IStep>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IStepContainer#addStep(com.yukthitech.ui.automation.
	 * IStep)
	 */
	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}
	
	@Override
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	public void setSteps(List<IStep> steps)
	{
		this.steps = steps;
	}
}
