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
package com.yukthitech.autox.exec;

import java.util.LinkedList;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class AsyncTryCatchBlock
{
	private static Logger logger = LogManager.getLogger(AsyncTryCatchBlock.class);
	
	public static interface ConsumerWithException<V>
	{
		public void accept(AsyncTryCatchBlock current, V value) throws Exception;
	}
	
	private Consumer<AsyncTryCatchBlock> main;
	
	private Consumer<AsyncTryCatchBlock> onComplete;
	
	private ConsumerWithException<Exception> onError;
	
	private Consumer<AsyncTryCatchBlock> onFinally;
	
	private AsyncTryCatchBlock parent;
	
	private LinkedList<AsyncTryCatchBlock> childBlocks;
	
	private boolean autoComplete = true;
	
	private Exception unhandledException = null;
	
	private String representation;
	
	private AsyncTryCatchBlock()
	{}
	
	public static AsyncTryCatchBlock doTry(String representation, Consumer<AsyncTryCatchBlock> runnable)
	{
		AsyncTryCatchBlock callback = new AsyncTryCatchBlock();
		callback.main = runnable;
		callback.representation = representation;
		
		return callback;
	}

	public AsyncTryCatchBlock onTry(Consumer<AsyncTryCatchBlock> runnable)
	{
		this.main = runnable;
		return this;
	}

	public AsyncTryCatchBlock onError(ConsumerWithException<Exception> onError)
	{
		this.onError = onError;
		return this;
	}
	
	public AsyncTryCatchBlock onFinally(Consumer<AsyncTryCatchBlock> onFinally)
	{
		this.onFinally = onFinally;
		return this;
	}

	public AsyncTryCatchBlock onComplete(Consumer<AsyncTryCatchBlock> onComplete)
	{
		this.onComplete = onComplete;
		return this;
	}

	public void triggerComplete()
	{
		//if any child is present, call it
		// the child on completion will call parent approp again
		if(CollectionUtils.isNotEmpty(childBlocks))
		{
			AsyncTryCatchBlock child = childBlocks.removeFirst();
			child.execute();
			return;
		}
		
		try
		{
			if(onComplete != null)
			{
				onComplete.accept(this);
			}
		}catch(Exception ex)
		{
			triggerError(ex);
			return;
		}
		
		if(triggerFinally() && parent != null)
		{
			parent.triggerComplete();
		}
	}
	
	private boolean triggerFinally()
	{
		try
		{
			if(onFinally != null)
			{
				onFinally.accept(this);
			}
			
			return true;
		}catch(Exception ex)
		{
			if(parent != null)
			{
				parent.triggerError(ex);
			}
			else
			{
				unhandledException = ex;
				logger.warn("[{}] Unhandled error occurred", representation, ex);
			}
			
			return false;
		}
	}
	
	public void triggerError(Exception ex)
	{
		try
		{
			boolean errorHandled = true;
			
			if(onError != null)
			{
				onError.accept(this, ex);
				errorHandled = true;
			}
			
			if(triggerFinally() && parent != null)
			{
				//if error handling is already done trigger complete
				if(errorHandled)
				{
					parent.triggerComplete();
				}
				//otherwise trigger error handler of parent
				else
				{
					parent.triggerError(ex);
					errorHandled = true;
				}
			}

			if(!errorHandled)
			{
				unhandledException = ex;
				logger.warn("[{}] Unhandled error occurred", representation, ex);
			}
		}catch(Exception ex1)
		{
			if(ex != ex1)
			{
				logger.warn("[{}] Unhandled error occurred", representation, ex);
			}
			
			if(triggerFinally() && parent != null)
			{
				parent.triggerError(ex1);
			}
			else
			{
				unhandledException = ex1;
				logger.warn("[{}] Unhandled error occurred", representation, ex);
			}
		}
	}
	
	public AsyncTryCatchBlock setAutoComplete(boolean autoComplete)
	{
		if(!autoComplete && CollectionUtils.isNotEmpty(childBlocks))
		{
			throw new InvalidStateException("Auto-complete is getting disabled for block with child blocks");
		}
		
		this.autoComplete = autoComplete;
		return this;
	}
	
	private void addChild(AsyncTryCatchBlock child)
	{
		if(!this.autoComplete)
		{
			throw new InvalidStateException("Child is getting added to block for which auto-complete is disabled");
		}
		
		child.parent = this;
		
		if(this.childBlocks == null)
		{
			this.childBlocks = new LinkedList<>();
		}
		
		this.childBlocks.addLast(child);
	}
	
	public AsyncTryCatchBlock newChild(String representation, Consumer<AsyncTryCatchBlock> runnable)
	{
		AsyncTryCatchBlock child = AsyncTryCatchBlock.doTry(representation, runnable);
		addChild(child);
		
		return child;
	}
	
	public AsyncTryCatchBlock execute()
	{
		try
		{
			if(main != null)
			{
				main.accept(this);
			}
			
			//if auto complete is not set, then complete
			// is expected to trigger from outside
			if(autoComplete)
			{
				triggerComplete();
			}
		}catch(Exception ex)
		{
			triggerError(ex);
		}
		
		return this;
	}
	
	/**
	 * If parent is null, then execution happens directly. If not current 
	 * block gets added to parent and gets executed as part of parent.
	 * 
	 * @param parent
	 * @return
	 */
	public AsyncTryCatchBlock executeWithParent(AsyncTryCatchBlock parent)
	{
		if(parent != null)
		{
			parent.addChild(this);
			return this;
		}
		
		this.execute();
		return this;
	}
	
	public Exception getUnhandledException()
	{
		return unhandledException;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");
		builder.append(representation);
		builder.append("]");
		return builder.toString();
	}

}
