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
package com.yukthitech.autox.test.lang.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Loops through specified collection or string tokens and for each iteration executed underlying steps.
 * 
 * @author akiran
 */
@Executable(name = "forEach", group = Group.Lang, message = "Loops through specified collection, map or string tokens and for each iteration executed underlying steps")
public class ForEachLoopStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed in loop.")
	private List<IStep> steps = new ArrayList<IStep>();

	/**
	 * Expression which will be evaluated to collection or map or String.
	 */
	@Param(description = "Expression which will be evaluated to collection or map or String", sourceType = SourceType.EXPRESSION, required = true)
	private Object expression;
	
	/**
	 * If expression evaluated to string, delimiter to be used to split the string.
	 */
	@Param(description = "If expression evaluated to string, delimiter to be used to split the string. Default Value: comma (\\s*\\,\\s*)", required = false)
	private String delimiter = "\\s*\\,\\s*";
	
	/**
	 * Loop variable that will be used to set loop iteration object on context. Default: loopVar.
	 */
	@Param(description = "Loop variable that will be used to set loop iteration object on context. Default: loopVar", required = false, 
			attrName = true, defaultValue = "loopVar")
	private String loopVar = "loopVar";
	
	/**
	 * Loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar.
	 */
	@Param(description = "Loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar", required = false,
			attrName = true, defaultValue = "loopIdxVar")
	private String loopIdxVar = "loopIdxVar";
	
	/**
	 * Ignores error during iteration and continues to next iteration.
	 */
	@Param(description = "Ignores error during iteration and continues to next iteration.", required = false)
	private boolean ignoreError = false;

	/**
	 * Sets the expression which will be evaluated to collection or map or String.
	 *
	 * @param expression the new expression which will be evaluated to collection or map or String
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	/**
	 * Sets the if expression evaluated to string, delimiter to be used to split the string.
	 *
	 * @param delimiter the new if expression evaluated to string, delimiter to be used to split the string
	 */
	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStepContainer#addStep(com.yukthitech.autox.IStep)
	 */
	@ChildElement(description = "Steps to be executed")
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
	
	/**
	 * Sets the loop variable that will be used to set loop iteration object on context. Default: loopVar.
	 *
	 * @param loopVar the new loop variable that will be used to set loop iteration object on context
	 */
	public void setLoopVar(String loopVar)
	{
		this.loopVar = loopVar;
	}
	
	/**
	 * Sets the loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar.
	 *
	 * @param loopIdxVar the new loop index variable that will be used to set loop iteration index on context
	 */
	public void setLoopIdxVar(String loopIdxVar)
	{
		this.loopIdxVar = loopIdxVar;
	}
	
	/**
	 * Sets the ignores error during iteration and continues to next iteration.
	 *
	 * @param ignoreError the new ignores error during iteration and continues to next iteration
	 */
	public void setIgnoreError(boolean ignoreError)
	{
		this.ignoreError = ignoreError;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Object> fetchExpressionList(IExecutionLogger exeLogger)
	{
		List<Object> collection = null;
		
		if(expression instanceof String)
		{
			collection = Arrays.asList( (Object[]) ((String) expression).split(delimiter) );
			
			exeLogger.debug("Collection expression '{}' evaluated to string and after split got collection as: {}", expression, collection);
		}
		else if(expression instanceof Collection)
		{
			collection =  new ArrayList<Object>( (Collection<Object>) expression );
			exeLogger.debug("Collection expression '{}' evaluated to collection of size: {}", expression, collection.size());
		}
		else if(expression instanceof Map)
		{
			collection = new ArrayList<Object>( (Collection) ((Map<Object, Object>) expression).entrySet() );
			exeLogger.debug("Collection expression '{}' evaluated to map of size: {}", expression, collection.size());
		}
		else
		{
			throw new InvalidStateException("Collection expression {} evaluated to non-string, non-collection, non-map. Value: {}", expression, expression);
		}
		
		return collection;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		if(expression == null)
		{
			exeLogger.debug("Collection expression evaluated to null");
			return;
		}
		
		List<Object> collection = fetchExpressionList(exeLogger);
		
		if(CollectionUtils.isEmpty(collection))
		{
			ForEachLoopStep parentStep = (ForEachLoopStep) sourceStep;
			exeLogger.debug("Collection expression '{}' evaluated to empty collection", parentStep.expression);
			return;
		}
		
		int index = -1;
		
		for(Object obj : collection)
		{
			index++;
			
			//from second time check for debug point for current step
			if(index > 0)
			{
				DebugFlowManager.getInstance().checkForDebugPoint(this);
			}
			
			context.setAttribute(loopVar, obj);
			context.setAttribute(loopIdxVar, index);
			
			try
			{
				StepsExecutor.execute(steps, null, null);
			}catch(Exception ex)
			{
				if(ex instanceof BreakException)
				{
					break;
				}
				
				if(ex instanceof ContinueException)
				{
					continue;
				}
				
				throw ex;
			}
		}
	}
}
