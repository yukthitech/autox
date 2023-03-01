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
package com.yukthitech.autox.debug.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ILocationBased;
import com.yukthitech.autox.IStackableStep;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepIntegral;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.context.ExecutionStack.StackElement;
import com.yukthitech.autox.debug.common.ClientMssgDebugOp;
import com.yukthitech.autox.debug.common.ClientMssgDropToFrame;
import com.yukthitech.autox.debug.common.DebugOp;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.common.DebugUtils;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.common.ServerMssgEvalExprResult;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.debug.common.ServerMssgExecutionReleased;
import com.yukthitech.autox.debug.common.ServerMssgStepExecuted;
import com.yukthitech.autox.exec.HandledException;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.autox.test.DropToStackFrameException;
import com.yukthitech.autox.test.IgnoreErrorException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents a live debug point where a thread is on hold.
 * @author akranthikiran
 */
public class LiveDebugPoint
{
	private static Logger logger = LogManager.getLogger(LiveDebugPoint.class);
	
	private static final int LOCK_TIME_OUT_SEC = 10;
	
	private static ThreadLocal<LiveDebugPoint> livePointThreadLocal = new ThreadLocal<>();
	
	private static class Request
	{
		private String requestId;
		
		private Object data;
		
		private Object requestObject;
		
		private boolean ignoreError = false;

		public Request(String requestId, Object data, Object requestObject)
		{
			this.requestId = requestId;
			this.data = data;
			this.requestObject = requestObject;
		}
		
		public static Request ignoreError()
		{
			Request req = new Request(null, null, null);
			req.ignoreError = true;
			
			return req;
		}
	}
	
	private String id = UUID.randomUUID().toString();
	
	private DebugPoint debugPoint;
	
	private Thread threadOnHold;
	
	private boolean released = false;
	
	private LinkedList<Request> requests = new LinkedList<>();
	
	/**
	 * Last location where executed was halt is maintained here.
	 */
	private ILocationBased lastPauseLocation;
	
	/**
	 * Stack size when last pause occurred.
	 */
	private int lastPauseStackSize;
	
	private DebugOp lastDebugOp;
	
	private AtomicBoolean onPause = new AtomicBoolean(false);
	
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition releaseRequestCondition = pauseLock.newCondition();
	
	private AtomicBoolean requestExecutionInProgress = new AtomicBoolean(false);
	
	private Set<String> dropableFrameIds = new HashSet<>();
	
	/**
	 * Flag indicating if this live point is created because of the error.
	 */
	private boolean errorPoint;
	
	private LiveDebugPoint(ILocationBased location, DebugPoint debugPoint, Consumer<LiveDebugPoint> callback, boolean errorPoint)
	{
		this.debugPoint = debugPoint;
		this.threadOnHold = Thread.currentThread();
		this.errorPoint = errorPoint;
		
		pause(location, callback);
	}
	
	public static LiveDebugPoint pauseAtDebugPoint(ILocationBased location, DebugPoint debugPoint, Consumer<LiveDebugPoint> callback)
	{
		LiveDebugPoint liveDebugPoint = new LiveDebugPoint(location, debugPoint, callback, false);
		return liveDebugPoint;
	}
	
	public static LiveDebugPoint pauseAtErrorPoint(ILocationBased location, DebugPoint debugPoint, Consumer<LiveDebugPoint> callback)
	{
		LiveDebugPoint liveDebugPoint = new LiveDebugPoint(location, debugPoint, callback, true);
		return liveDebugPoint;
	}

	public static LiveDebugPoint getLivePoint()
	{
		LiveDebugPoint point = livePointThreadLocal.get();
		
		if(point != null && point.released)
		{
			livePointThreadLocal.remove();
			return null;
		}
		
		return point;
	}

	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return threadOnHold.getName();
	}
	
	public DebugPoint getDebugPoint()
	{
		return debugPoint;
	}
	
	private Map<String, byte[]> getContextAttr()
	{
		Map<String, byte[]> contextAttr = new HashMap<>(); 
		ExecutionContextManager.getExecutionContext().getAttr().forEach((key, val) ->
		{
			byte serData[] = DebugUtils.serialize(val);
			contextAttr.put(key, serData);
		});

		return contextAttr;
	}
	
	private Map<String, byte[]> getParams()
	{
		Map<String, byte[]> paramMap = new HashMap<>(); 
		Map<String, Object> params = ExecutionContextManager.getInstance().getExecutionContextStack().getParamForDebug();
		
		if(MapUtils.isEmpty(params))
		{
			return paramMap;
		}
		
		params.forEach((key, val) ->
		{
			byte serData[] = DebugUtils.serialize(val);
			paramMap.put(key, serData);
		});

		return paramMap;
	}

	private void sendOnHoldMssg()
	{
		List<ServerMssgExecutionPaused.StackElement> stackTrace = new ArrayList<>();
		int index = -1;
		ILocationBased prevStep = null;
		List<ServerMssgExecutionPaused.StackElement> elementsWithoutId = new ArrayList<>();
				
		synchronized(dropableFrameIds)
		{
			dropableFrameIds.clear();
			
			for(StackElement elem : ExecutionContextManager.getInstance().getExecutionStack().getStackTrace())
			{
				index++;
				
				String elemId = elem.getStackElementId();
				
				//when an element is found with id
				if(StringUtils.isNotBlank(elemId))
				{
					//and previously elements are found without id
					if(!elementsWithoutId.isEmpty())
					{
						//copy id from current element to prev elements without id
						elementsWithoutId.forEach(msgElem -> msgElem.setStackElementId(elemId));
						elementsWithoutId.clear();
					}
					
					//mark current element id as dropable
					dropableFrameIds.add(elemId);
				}
				
				/*
				 * Skip the step addition to stack trace in following case:
				 * 	 Step is not a top step (Top or latest step should be always part of stack trace)
				 *   AND current step is not stackable step (stackable step should be pushed to stack trace)
				 *   AND prev step in integral step (When integral step is being executed, next following step should always get added)
				 */
				if(index > 0 && !(elem.getElement() instanceof IStackableStep) && !(prevStep instanceof IStepIntegral))
				{
					prevStep = elem.getElement();
					continue;
				}

				prevStep = elem.getElement();
				
				ServerMssgExecutionPaused.StackElement mssgStackElem = new ServerMssgExecutionPaused.StackElement(elem.getLocation(), 
						elem.getLineNumber(), elemId);
				
				stackTrace.add(mssgStackElem);
			
				//when stack elem id is empty
				if(StringUtils.isBlank(elemId))
				{
					elementsWithoutId.add(mssgStackElem);
				}
			}
		}
		
		/*
		 * Populate stack-element-id for elements with missing stack-element-id.
		 * This would be the case in case of steps part of step-container-step like loop, try-catch etc.
		 */
		
		//get stack trace in reverse order as the parent/wrapping element comes in reverse order
		List<ServerMssgExecutionPaused.StackElement> revStackTrace = new ArrayList<>(stackTrace);
		Collections.reverse(revStackTrace);
		
		String prevStackElemId = null;
		String stackElemId = null;
		
		for(ServerMssgExecutionPaused.StackElement elem : revStackTrace)
		{
			stackElemId = elem.getStackElementId();
			stackElemId = StringUtils.isBlank(stackElemId) ? prevStackElemId : stackElemId;
			
			if(StringUtils.isBlank(stackElemId))
			{
				stackElemId = prevStackElemId;
				elem.setStackElementId(stackElemId);
			}
			
			prevStackElemId = stackElemId;
		}
		
		ServerMssgExecutionPaused pausedMssg = new ServerMssgExecutionPaused(id, threadOnHold.getName(), lastPauseLocation.getLocation().getPath(),
				lastPauseLocation.getLineNumber(), stackTrace, getContextAttr(),
				getParams(),
				errorPoint);
		DebugServer.getInstance().sendClientMessage(pausedMssg);
	}
	
	/**
	 * This is the main method which will hold current execution thread till approp debug operations
	 * are requested.
	 * 
	 * @param location
	 * @param callback
	 */
	private void pause(ILocationBased location, Consumer<LiveDebugPoint> callback)
	{
		livePointThreadLocal.set(this);
		
		pauseLock.lock();
		
		try
		{
			if(callback != null)
			{
				callback.accept(this);
			}
			
			this.lastPauseLocation = location;
			this.lastPauseStackSize = ExecutionContextManager.getInstance().getExecutionStack().getStackSize();
			
			sendOnHoldMssg();
			
			logger.trace("LivePOINT: Pause at location: {}:{}", lastPauseLocation.getLocation().getName(), lastPauseLocation.getLineNumber());
			
			onPause.set(true);

			while(!released)
			{
				try
				{
					//wait for release request
					releaseRequestCondition.await();
					
					//execute on hold tasks that might be required post release as well
					handleOnHoldTasks();
					
					//break the current loop (as signal also indicates release)
					break;
				}catch(InterruptedException ex)
				{
					//Interruption occurs for evaluation, which needs to be completed
					// and needs to get back to hold state
					handleOnHoldTasks();
				}
			}
		} catch(DropToStackFrameException | IgnoreErrorException ex)
		{
			DebugServer.getInstance().sendClientMessage(new ServerMssgExecutionReleased(id));
			throw ex;
		} catch(Exception ex)
		{
			logger.error("An error occurred during debug point hold", ex);
		} finally
		{
			logger.trace("LivePOINT: Pause released at location: {}:{}", lastPauseLocation.getLocation().getName(), lastPauseLocation.getLineNumber());
			DebugServer.getInstance().sendClientMessage(new ServerMssgExecutionReleased(id));

			onPause.set(false);
			pauseLock.unlock();
		}
	}
	
	private void executeStepsRequest(String reqId, List<IStep> steps)
	{
		try
		{
			StepsExecutor.execute(steps, null, null);
			
			DebugServer.getInstance().sendClientMessage(new ServerMssgStepExecuted(reqId, id, true, getContextAttr(), null));
		} catch(Exception ex)
		{
			if(ex instanceof HandledException)
			{
				logger.error("An error occurred during dynamic step execution: {}", ex.toString());
			}
			else
			{
				logger.error("An error occurred during dynamic step execution", ex);
			}
			
			DebugServer.getInstance().sendClientMessage(new ServerMssgStepExecuted(reqId, id, false, getContextAttr(), 
					"An error occurred during dynamic step execution:\n  " + ex));
		}
	}

	private void evalExprRequest(String reqId, String expression)
	{
		try
		{
			Object res = PrefixExpressionFactory.getExpressionFactory().getValueByExpressionString(AutomationContext.getInstance(), expression);
			DebugServer.getInstance().sendClientMessage(new ServerMssgEvalExprResult(reqId, true, res, null));
		}catch(Exception ex)
		{
			logger.error("An error occurred during expression evaluation: {}", expression, ex);
			DebugServer.getInstance().sendClientMessage(new ServerMssgEvalExprResult(reqId, false, null, 
					"An error occurred during expression evaluation:\n  " + ex));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void handleOnHoldTasks()
	{
		requestExecutionInProgress.set(true);
		
		try
		{
			List<Request> currentRequests = new ArrayList<>();
			
			synchronized(requests)
			{
				if(requests.isEmpty())
				{
					return;
				}
	
				currentRequests.addAll(this.requests);
				this.requests.clear();
			}
	
			for(Request req : currentRequests)
			{
				if(req.requestObject instanceof ClientMssgDropToFrame)
				{
					throw new DropToStackFrameException(((ClientMssgDropToFrame) req.requestObject).getStackElementId());
				}
				
				if(errorPoint && req.ignoreError)
				{
					//as current error is being ignored, set current point as non-error point
					this.errorPoint = false;
					
					//throw exception to ignore current error
					throw new IgnoreErrorException();
				}
				
				//when debug point is already released, dont execute steps
				//  or expressions request
				if(released)
				{
					continue;
				}
				
				if(req.data instanceof List)
				{
					executeStepsRequest(req.requestId, (List) req.data);
				}
				else
				{
					evalExprRequest(req.requestId, (String) req.data);
				}
			}
		}finally
		{
			requestExecutionInProgress.set(false);
		}
	}
	
	private void acquirePauseLock()
	{
		try
		{
			boolean res = pauseLock.tryLock(LOCK_TIME_OUT_SEC, TimeUnit.SECONDS);
			
			if(!res)
			{
				throw new DebugLockException("Failed to acquire required locks before timeout.");
			}
		}catch(InterruptedException ex)
		{
			throw new DebugLockException("Thread was interrupted before acquiring required locks");
		}
	}
	
	public void requestExecuteSteps(String reqId, List<IStep> steps)
	{
		acquirePauseLock();
		
		try
		{
			if(!onPause.get())
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgStepExecuted(reqId, id, false, null, 
						"Current live-point is not in paused state"));
				return;
			}
			
			this.requests.addLast(new Request(reqId, steps, null));
			threadOnHold.interrupt();
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	public void requestEvalExpression(String reqId, String expression)
	{
		acquirePauseLock();
		
		try
		{
			if(!onPause.get())
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgEvalExprResult(reqId, false, null, "Current live-point is not in paused state"));
				return;
			}
			
			synchronized(requests)
			{
				this.requests.addLast(new Request(reqId, expression, null));
			}
			
			threadOnHold.interrupt();
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	public boolean requestDropToFrame(ClientMssgDropToFrame dropToFrame)
	{
		acquirePauseLock();
		
		try
		{
			if(!onPause.get())
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgEvalExprResult(dropToFrame.getRequestId(), false, null, 
						"Current live-point is not in paused state"));
				return false;
			}
			
			synchronized(dropableFrameIds)
			{
				if(!dropableFrameIds.contains(dropToFrame.getStackElementId()))
				{
					DebugServer.getInstance().sendClientMessage(new ServerMssgEvalExprResult(dropToFrame.getRequestId(), false, null, 
							"Invalid frame id specified for drop"));
					return false;
				}
			}
			
			synchronized(requests)
			{
				this.requests.addLast(new Request(dropToFrame.getRequestId(), null, dropToFrame));
			}
			
			clearThread();
			return true;
		}finally
		{
			pauseLock.unlock();
		}
	}

	/*
	public boolean requestIgnoreError(ClientMssgIgnoreError dropToFrame)
	{
		pauseLock.lock();
		
		try
		{
			if(!onPause.get())
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgEvalExprResult(dropToFrame.getRequestId(), false, null, 
						"Current live-point is not in paused state"));
				return false;
			}
			
			if(!errorPoint)
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgEvalExprResult(dropToFrame.getRequestId(), false, null, 
						"Current break point is not halted on error"));
				return false;
			}
			
			synchronized(requests)
			{
				this.requests.addLast(new Request(dropToFrame.getRequestId(), null, dropToFrame));
			}
			
			clearThread();
			return true;
		}finally
		{
			pauseLock.unlock();
		}
	}
	*/

	/**
	 * Returns true, if the current live point is in pause state, and owner thread
	 * itself is invoking this method.
	 * @return
	 */
	public boolean isDynamicExecutionInProgress()
	{
		pauseLock.lock();
		
		try
		{
			return onPause.get() && (Thread.currentThread() == threadOnHold);
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	public boolean requestRelease(ClientMssgDebugOp request)
	{
		acquirePauseLock();
		
		try
		{
			DebugOp debugOp = request.getDebugOp();
			logger.trace("LivePOINT: Release operation request with op: {}", debugOp);

			if(!onPause.get())
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(request.getRequestId(), false, "Current live-point is not in paused state"));
				return false;
			}
			
			if(this.errorPoint)
			{
				if(request.isIgnoreErrorEnabled())
				{
					synchronized(requests)
					{
						this.requests.addLast(Request.ignoreError());
					}
				}
				else if(debugOp != DebugOp.RESUME)
				{
					DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(request.getRequestId(), false, 
							String.format("Current error-point does not support specified operation. [Op: %s, Ignore Error: %s]", debugOp, request.isIgnoreErrorEnabled())
							));
					return false;
				}
			}
			
			if(debugOp == DebugOp.RESUME)
			{
				clearThread();
			}
			else
			{
				this.lastDebugOp = debugOp;
				releaseRequestCondition.signal();
			}
			
			return true;
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	/**
	 * Checks if pause should happen because of this live debug point. Which in turn
	 * depends on operation that was taken current live point.
	 * @param step
	 * @return true if this live point is responsible for pausing or skipping
	 */
	public boolean checkForPause(ILocationBased step)
	{
		if(threadOnHold != Thread.currentThread())
		{
			throw new InvalidStateException("Pause check is called on non-owner thread");
		}
		
		logger.trace("LivePOINT: Checking for pause at location: {}", step);
		
		pauseLock.lock();
		
		try
		{
			//if dyn request execution in progress, dont pause anywhere
			if(requestExecutionInProgress.get())
			{
				//if request is in progress current point nor other points
				// should hold the flow
				return true;
			}
			
			DebugOp lastDebugOp = this.lastDebugOp;
			
			//when stepping into function, hold irrespective of location
			if(lastDebugOp == DebugOp.STEP_INTO)
			{
				//clear last debug op, as step-into will be completed
				//  by this pause
				this.lastDebugOp = null;
				pause(step, null);
				
				//as pause is taken care by current live point
				return true;
			}
			//when moving to next step, dont stop in case child steps are getting executed
			//  like steps in function call invoked from last location
			else if(lastDebugOp == DebugOp.STEP_OVER)
			{
				boolean subExecution = (lastPauseLocation instanceof IStackableStep) && 
						ExecutionContextManager.getInstance().getExecutionStack().isSubexecutionOf(lastPauseLocation);
				
				if(!subExecution)
				{
					//clear last debug op, as step-over will be completed
					//  by this pause
					this.lastDebugOp = null;
					pause(step, null);

					//as pause is taken care by current live point
					return true;
				}
				
				//as pause is not taken care by current point
				// return false so that other points are evaluated for pause
				return false;
			}
			//Step return should return to the previous caller which will be done based
			//  on stack size. Stack size should be less than that of previous pause
			else if(lastDebugOp == DebugOp.STEP_RETURN)
			{
				int newStackSize = ExecutionContextManager.getInstance().getExecutionStack().getStackSize();
				
				if(lastPauseStackSize > newStackSize)
				{
					//clear last debug op, as step-return will be completed by this pause
					this.lastDebugOp = null;
					this.lastPauseStackSize = -1;
					pause(step, null);

					//as pause is taken care by current live point
					return true;
				}

				//as pause is not taken care by current point
				// return false so that other points are evaluated for pause
				return false;
			}
			
			//Note: In case of step-return this live-point should have been released
			
			throw new InvalidStateException("Check for hold is called during debug operation: {}", lastDebugOp);
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	private void releaseLivePoint()
	{
		//remove from thread local as well (in case the execution directly happen)
		// post debug point halt
		
		//if current thread is same as owner, clear thread local
		// Note: though not removed from thread local, once marked as release getLivePoint() will not 
		//  return current live point (and also clears it)
		if(this.threadOnHold == Thread.currentThread())
		{
			livePointThreadLocal.remove();
		}
		
		released = true;
		DebugFlowManager.getInstance().removeLivePoint(id);
	}
	
	public void clearThread()
	{
		pauseLock.lock();
		
		try
		{
			if(released)
			{
				return;
			}
			
			releaseLivePoint();

			//if thread is not released, release it
			releaseRequestCondition.signal();
		}finally
		{
			pauseLock.unlock();
		}
	}
}
