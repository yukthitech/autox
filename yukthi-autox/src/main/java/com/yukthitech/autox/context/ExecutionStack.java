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
package com.yukthitech.autox.context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.ThreadContext;

import com.yukthitech.autox.ILocationBased;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.test.IEntryPoint;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Keeps track of execution, which in turn will be used to display stack trace
 * on error.
 * @author akiran
 */
public class ExecutionStack
{
	public static class StackElement
	{
		private IEntryPoint entryPoint;
		
		private ILocationBased element;
		
		private int entryPointLevel;

		public StackElement(IEntryPoint entryPoint, ILocationBased element, int entryPointLevel)
		{
			this.entryPoint = entryPoint;
			this.element = element;
			this.entryPointLevel = entryPointLevel;
		}
		
		public String getLocation()
		{
			return element.getLocation().getPath();
		}
		
		public String getLocationName()
		{
			return element.getLocation().getName();
		}
		
		public int getLineNumber()
		{
			return element.getLineNumber();
		}
		
		public String getSourceLocation()
		{
			return element.getLocation().getName() + ":" + element.getLineNumber();
		}
		
		public boolean isSameLocation(ILocationBased element)
		{
			if(element == null)
			{
				return false;
			}
			
			if(this.element == element)
			{
				return true;
			}
			
			if(this.element.getLocation().equals(element.getLocation()) && this.element.getLineNumber() == element.getLineNumber())
			{
				return true;
			}
			
			return false;
		}
		
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append(entryPoint.toText());
			
			String location = element.getLocation().getName() + ":" + element.getLineNumber();
			builder.append("(").append(location).append(")");

			return builder.toString();
		}
	}
	
	/**
	 * Stack trace of the execution.
	 */
	private LinkedList<IEntryPoint> entryPointStack = new LinkedList<>();

	/**
	 * Stack trace of the execution.
	 */
	private LinkedList<StackElement> stackTrace = new LinkedList<>();
	
	/**
	 * Used internally to ensure, push and pop happens in same order.
	 */
	private LinkedList<Object> objectStack = new LinkedList<>();
	
	public List<StackElement> getStackTrace()
	{
		return new ArrayList<>(stackTrace);
	}
	
	private Object unwrapStep(Object object)
	{
		if(object instanceof IStep)
		{
			IStep step = (IStep) object;
			
			if(step.getSourceStep() != null)
			{
				return step.getSourceStep();
			}
			
			return step;
		}

		return object;
	}
	
	public void push(Object object)
	{
		object = unwrapStep(object);
		
		//System.out.println("==========> Pushing object: " + object);
		objectStack.push(object);
		
		if(object instanceof IEntryPoint)
		{
			entryPointStack.push((IEntryPoint) object);
			return;
		}
		
		IEntryPoint entryPoint = entryPointStack.getFirst();
		ILocationBased element = (ILocationBased) object;
		
		StackElement stackElement = new StackElement(entryPoint, element, entryPointStack.size());
		stackTrace.push(stackElement);
		
		ThreadContext.put("xmlLoc", stackElement.getSourceLocation());
	}
	
	public void pop(Object object)
	{
		object = unwrapStep(object);
		
		Object peekObj = objectStack.peek();
		
		if(peekObj != object)
		{
			throw new InvalidStateException("Object being popped is not same as the one on stack. [Expected: {}, Actual:{}]", object, peekObj);
		}
		
		if(object instanceof IEntryPoint)
		{
			//Object popped = entryPointStack.pop();
			entryPointStack.pop();
			objectStack.pop();
			//System.out.println("==========> Popping entry object: " + object + "  =========> " + popped);
			return;
		}
		
		//StackElement popped = stackTrace.pop();
		stackTrace.pop();
		objectStack.pop();
		
		//System.out.println("==========> Popping normal object: " + object + "  =========> " + popped.element);
		
		if(stackTrace.isEmpty())
		{
			ThreadContext.put("xmlLoc", null);
		}
		else
		{
			ThreadContext.put("xmlLoc", stackTrace.peek().getSourceLocation());
		}
			
	}
	
	public boolean isNotPeekElement(Object object)
	{
		object = unwrapStep(object);
		return (objectStack.peek() != object);
	}
	
	public boolean isPeekElement(Object object)
	{
		object = unwrapStep(object);
		return (objectStack.peek() == object);
	}
	
	/**
	 * Returns true if specified element is part of stack.
	 * @param element
	 * @return
	 */
	public boolean isSubexecutionOf(ILocationBased element)
	{
		if(stackTrace.isEmpty())
		{
			return false;
		}
		
		for(StackElement elem : this.stackTrace)
		{
			if(elem.isSameLocation(element))
			{
				return true;
			}
		}
		
		return false;
	}

	public String toStackTrace()
	{
		StringBuilder builder = new StringBuilder("\n");
		int entryPointLevel = -1;
		
		for(StackElement element : stackTrace)
		{
			//if entry point level is same as that of prev element
			if(element.entryPointLevel == entryPointLevel)
			{
				//skip the element from stack trace, which would
				// be the case with loops, try-catch etc
				continue;
			}
			
			builder.append("\t").append(element).append("\n");
			entryPointLevel = element.entryPointLevel;
		}
		
		return builder.toString();
	}
	
	public String getCurrentLocation()
	{
		if(stackTrace.isEmpty())
		{
			return null;
		}
		
		return stackTrace.getFirst().getSourceLocation();
	}
}
