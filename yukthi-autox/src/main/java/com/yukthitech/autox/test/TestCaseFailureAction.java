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
package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents group of steps that get executed when testcase fails.
 * @author akiran
 */
public class TestCaseFailureAction implements IStepContainer, Validateable
{
	/**
	 * Steps added for execution.
	 */
	private List<IStep> steps = new ArrayList<>();
	
	/**
	 * Title to be used for this failure action.
	 */
	private String title;
	
	/**
	 * Title to be used when action is failed.
	 */
	private String failureTitle;
	
	/**
	 * Title to be used action is errored.
	 */
	private String errorTitle;

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
		if(step == null)
		{
			throw new NullPointerException("Step can not be null");
		}
		
		steps.add(step);
	}

	/**
	 * Gets the steps added for execution.
	 *
	 * @return the steps added for execution
	 */
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	/**
	 * Gets the title to be used for this failure action.
	 *
	 * @return the title to be used for this failure action
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title to be used for this failure action.
	 *
	 * @param title the new title to be used for this failure action
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Gets the title to be used when action is failed.
	 *
	 * @return the title to be used when action is failed
	 */
	public String getFailureTitle()
	{
		return failureTitle;
	}

	/**
	 * Sets the title to be used when action is failed.
	 *
	 * @param failureTitle the new title to be used when action is failed
	 */
	public void setFailureTitle(String failureTitle)
	{
		this.failureTitle = failureTitle;
	}

	/**
	 * Gets the title to be used action is errored.
	 *
	 * @return the title to be used action is errored
	 */
	public String getErrorTitle()
	{
		return errorTitle;
	}

	/**
	 * Sets the title to be used action is errored.
	 *
	 * @param errorTitle the new title to be used action is errored
	 */
	public void setErrorTitle(String errorTitle)
	{
		this.errorTitle = errorTitle;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(title))
		{
			throw new ValidateException("Empty title specified.");
		}
		
		if(steps.isEmpty())
		{
			throw new ValidateException("No steps are specified.");
		}
		
		if(StringUtils.isBlank(errorTitle))
		{
			errorTitle = "ERROR: " + title;
		}

		if(StringUtils.isBlank(failureTitle))
		{
			errorTitle = "FAILED: " + title;
		}
	}
}
