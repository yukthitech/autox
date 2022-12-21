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
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IMultiPartStep;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ExecutionStack;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.Function;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Evaluates specified condition and if evaluates to true execute 'then'
 * otherwise execute 'else'. For ease 'if' supports direct addition of steps which would be added to then block.
 * 
 * @author akiran
 */
@Executable(name = "if", group = Group.Lang, message = "Evaluates specified condition and if evaluates to true execute 'then' otherwise execute 'else'. "
		+ "For ease 'if' supports direct addition of steps which would be added to then block.")
public class IfConditionStep extends AbstractStep implements IStepContainer, IMultiPartStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Freemarker condition to be evaluated.", required = true, sourceType = SourceType.CONDITION)
	private String condition;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed when condition evaluated to be true.", required = true)
	private List<IStep> then = new ArrayList<IStep>();

	/**
	 * Else-if blocks.
	 */
	@SkipParsing
	private List<ElseIfStep> elseIfBlocks;
	
	/**
	 * Else block.
	 */
	@SkipParsing
	private ElseStep elseBlock;

	/**
	 * Sets the freemarker condition to be evaluated.
	 *
	 * @param condition the new freemarker condition to be evaluated
	 */
	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	/**
	 * Sets the group of steps/validations to be executed when condition evaluated to be true.
	 *
	 * @param then the new group of steps/validations to be executed when condition evaluated to be true
	 */
	@ChildElement(description = "Used to group steps to be executed when this if condition is true.")
	public void setThen(Function then)
	{
		this.then.addAll(then.getSteps());
	}

	/**
	 * Sets the group of steps/validations to be executed when condition evaluated to be false.
	 *
	 * @param elseGroup the new group of steps/validations to be executed when condition evaluated to be false
	 */
	@ChildElement(description = "Used to group steps to be executed when this if condition is false.")
	public void setElse(Function elseGroup)
	{
		if(elseBlock != null)
		{
			throw new InvalidStateException("else block is already defined.");
		}
		
		this.elseBlock = new ElseStep();
		this.elseBlock.setLocation(elseGroup.getLocation(), elseGroup.getLineNumber());
		this.elseBlock.setSteps(elseGroup.getSteps());
	}
	
	@Override
	public void addStep(IStep step)
	{
		then.add(step);
	}

	@Override
	public List<IStep> getSteps()
	{
		return then;
	}
	
	@Override
	public void addChildPart(IStep step)
	{
		if(step instanceof ElseIfStep)
		{
			if(elseBlock != null)
			{
				throw new InvalidStateException("else-if block cannot be used after else block.");
			}

			if(this.elseIfBlocks == null)
			{
				this.elseIfBlocks = new ArrayList<>();
			}
			
			this.elseIfBlocks.add((ElseIfStep) step);
		}
		else
		{
			if(elseBlock != null)
			{
				throw new InvalidStateException("else block is already defined.");
			}

			this.elseBlock = (ElseStep) step;
		}
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		boolean res = AutomationUtils.evaluateCondition(context, condition);
		
		exeLogger.trace("If-condition evaluation resulted in '{}'. Condition: {}", res, condition);
		
		if(res)
		{
			StepsExecutor.execute(then, null);
			return;
		}
		
		boolean matched = false;
		ExecutionStack executionStack = ExecutionContextManager.getInstance().getExecutionStack();
		
		if(CollectionUtils.isNotEmpty(elseIfBlocks))
		{
			for(ElseIfStep elseIfBlock : this.elseIfBlocks)
			{
				executionStack.push(elseIfBlock);
				DebugFlowManager.getInstance().checkForDebugPoint(elseIfBlock);
				
				try
				{
					res = AutomationUtils.evaluateCondition(context, elseIfBlock.getCondition());
					exeLogger.trace("Else-if-condition evaluation resulted in '{}'. Condition: {}", res, elseIfBlock.getCondition());
					
					if(res)
					{
						matched = true;
						StepsExecutor.execute(elseIfBlock.getSteps(), null);
						break;
					}
				}finally
				{
					executionStack.pop(elseIfBlock);
				}
			}
		}
		
		if(!matched && elseBlock != null)
		{
			executionStack.push(elseBlock);
			DebugFlowManager.getInstance().checkForDebugPoint(elseBlock);
			
			try
			{
				exeLogger.trace("Executing else block");
				StepsExecutor.execute(elseBlock.getSteps(), null);
			}finally
			{
				executionStack.pop(elseBlock);
			}
		}
	}
}
