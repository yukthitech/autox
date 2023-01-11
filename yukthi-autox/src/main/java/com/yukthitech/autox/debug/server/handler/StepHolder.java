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
package com.yukthitech.autox.debug.server.handler;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.test.CustomPrefixExpression;
import com.yukthitech.autox.test.Function;

public class StepHolder implements IStepContainer
{
	/**
	 * Steps added for execution.
	 */
	private List<IStep> steps = new ArrayList<>();
	
	/**
	 * Custom ui locators to reload.
	 */
	private List<CustomPrefixExpression> customUiLocators = new ArrayList<>();
	
	private List<CustomPrefixExpression> customPrefixExpressions = new ArrayList<>();
	
	/**
	 * Functions to reload.
	 */
	private List<Function> functions = new ArrayList<>();

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

	/**
	 * Gets the steps added for execution.
	 *
	 * @return the steps added for execution
	 */
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	public void addCustomUiLocator(CustomPrefixExpression locator)
	{
		this.customUiLocators.add(locator);
	}
	
	public List<CustomPrefixExpression> getCustomUiLocators()
	{
		return customUiLocators;
	}
	
	public void addCustomPrefixExpression(CustomPrefixExpression prefixExpr)
	{
		this.customPrefixExpressions.add(prefixExpr);
	}
	
	public List<CustomPrefixExpression> getCustomPrefixExpressions()
	{
		return customPrefixExpressions;
	}

	public void addFunction(Function function)
	{
		this.functions.add(function);
	}
	
	public List<Function> getFunctions()
	{
		return functions;
	}
}
