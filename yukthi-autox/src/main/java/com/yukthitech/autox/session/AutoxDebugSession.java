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
package com.yukthitech.autox.session;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.common.ContentStream;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.IDebugEventSink;
import com.yukthitech.autox.debug.InProcessDebugEventSink;
import com.yukthitech.autox.debug.common.ClientMssgDebugOp;
import com.yukthitech.autox.debug.common.ClientMssgDropToFrame;
import com.yukthitech.autox.debug.common.DebugOp;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.LiveDebugPoint;
import com.yukthitech.autox.event.AutomationEventManager;
import com.yukthitech.autox.interactive.InteractiveXmlSupport;
import com.yukthitech.autox.interactive.StepHolder;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Debug-capable {@link AutoxSession}. Enables debug mode with an {@link IDebugEventSink}
 * (default: {@link InProcessDebugEventSink}) so pause / step / resume work in-process.
 * <p>
 * Pause events are delivered asynchronously via the sink — call {@link #resume(String)} /
 * step methods from the host thread (or sink listener), not from the paused execution thread.
 * </p>
 * <pre>
 * InProcessDebugEventSink sink = new InProcessDebugEventSink();
 * sink.addListener(event -&gt; { ... handle ServerMssgExecutionPaused ... });
 * try (AutoxDebugSession session = AutoxDebugSession.start(ContentStream.fromFile(appConfig), args, sink))
 * {
 *     session.loadXml(ContentStream.fromString(xml, "host:flow.xml"));
 *     session.addDebugPoint(new DebugPoint(filePath, line, null));
 *     // runFunction on a worker thread; on pause, resume from listener / UI thread
 *     session.resume(executionId);
 * }
 * </pre>
 *
 * @author akranthikiran
 */
public class AutoxDebugSession extends AutoxSession
{
	private final IDebugEventSink eventSink;
	
	private final DebugFlowManager debugFlowManager;
	
	private AutoxDebugSession(AutomationContext context, IDebugEventSink eventSink)
	{
		super(context);
		this.eventSink = eventSink;
		this.debugFlowManager = (DebugFlowManager) context.getDebugFlowManager();
	}
	
	/**
	 * Starts a debug session with an {@link InProcessDebugEventSink}.
	 */
	public static AutoxDebugSession start(ContentStream appConfiguration, String[] extendedCommandLineArgs) throws Exception
	{
		return start(appConfiguration, extendedCommandLineArgs, new InProcessDebugEventSink());
	}
	
	/**
	 * Starts a debug session using the given sink for outbound debug events.
	 */
	public static AutoxDebugSession start(ContentStream appConfiguration, String[] extendedCommandLineArgs,
			IDebugEventSink eventSink) throws Exception
	{
		return start(appConfiguration, extendedCommandLineArgs, new AutomationEventManager(), eventSink);
	}
	
	/**
	 * Starts a debug session with a custom event manager and sink.
	 */
	public static AutoxDebugSession start(ContentStream appConfiguration, String[] extendedCommandLineArgs,
			AutomationEventManager automationEventManager, IDebugEventSink eventSink) throws Exception
	{
		if(eventSink == null)
		{
			throw new InvalidArgumentException("Debug event sink cannot be null");
		}
		
		AutomationContext context = createContext(appConfiguration, extendedCommandLineArgs, automationEventManager);
		DebugFlowManager flowManager = (DebugFlowManager) context.getDebugFlowManager();
		flowManager.enableDebug(eventSink);
		return new AutoxDebugSession(context, eventSink);
	}
	
	public IDebugEventSink getEventSink()
	{
		return eventSink;
	}
	
	public void setDebugPoints(Collection<DebugPoint> points)
	{
		ensureOpen();
		debugFlowManager.setDebugPoints(points);
	}
	
	public void addDebugPoint(DebugPoint point)
	{
		ensureOpen();
		
		if(point == null)
		{
			throw new InvalidArgumentException("Debug point cannot be null");
		}
		
		debugFlowManager.addDebugPoint(point);
	}
	
	public void removeDebugPoint(String filePath, int lineNumber)
	{
		ensureOpen();
		debugFlowManager.removeDebugPoint(filePath, lineNumber);
	}
	
	public void removeDebugPoint(DebugPoint point)
	{
		ensureOpen();
		debugFlowManager.removeDebugPoint(point);
	}
	
	public Collection<DebugPoint> getDebugPoints()
	{
		ensureOpen();
		return debugFlowManager.getDebugPoints();
	}
	
	public boolean resume(String executionId)
	{
		return resume(executionId, false);
	}
	
	public boolean resume(String executionId, boolean ignoreError)
	{
		return requestDebugOp(executionId, DebugOp.RESUME, ignoreError);
	}
	
	public boolean stepOver(String executionId)
	{
		return stepOver(executionId, false);
	}
	
	public boolean stepOver(String executionId, boolean ignoreError)
	{
		return requestDebugOp(executionId, DebugOp.STEP_OVER, ignoreError);
	}
	
	public boolean stepInto(String executionId)
	{
		return stepInto(executionId, false);
	}
	
	public boolean stepInto(String executionId, boolean ignoreError)
	{
		return requestDebugOp(executionId, DebugOp.STEP_INTO, ignoreError);
	}
	
	public boolean stepReturn(String executionId)
	{
		return stepReturn(executionId, false);
	}
	
	public boolean stepReturn(String executionId, boolean ignoreError)
	{
		return requestDebugOp(executionId, DebugOp.STEP_RETURN, ignoreError);
	}
	
	/**
	 * Evaluates an expression on the paused live point. Result is delivered via the sink
	 * as {@code ServerMssgEvalExprResult} for the returned request id.
	 *
	 * @return request id correlating the sink result
	 */
	public String evalWhilePaused(String executionId, String expression)
	{
		ensureOpen();
		
		if(StringUtils.isBlank(expression))
		{
			throw new InvalidArgumentException("Expression cannot be empty");
		}
		
		LiveDebugPoint livePoint = requireLivePoint(executionId);
		String requestId = UUID.randomUUID().toString();
		livePoint.requestEvalExpression(requestId, expression);
		return requestId;
	}
	
	/**
	 * Parses and executes step XML on the paused live point (same path as Prism execute-steps).
	 * {@code sourceName} is required for location attribution.
	 *
	 * @return request id correlating {@code ServerMssgStepExecuted} on the sink, or {@code null}
	 *         when only reload was applied (no steps)
	 */
	public String executeStepsWhilePaused(String executionId, String stepXml, String sourceName)
	{
		ensureOpen();
		
		if(StringUtils.isBlank(stepXml))
		{
			throw new InvalidArgumentException("Steps XML cannot be empty");
		}
		
		if(StringUtils.isBlank(sourceName))
		{
			throw new InvalidArgumentException("sourceName is required for executeStepsWhilePaused");
		}
		
		LiveDebugPoint livePoint = requireLivePoint(executionId);
		StepHolder stepHolder = InteractiveXmlSupport.parseSteps(getAutomationContext(), stepXml);
		stepHolder.updateLocations(new File(sourceName), 1);
		boolean reloadingDone = InteractiveXmlSupport.applyReloads(getAutomationContext(), stepHolder, null);
		
		if(CollectionUtils.isEmpty(stepHolder.getSteps()))
		{
			if(reloadingDone)
			{
				return null;
			}
			
			throw new InvalidArgumentException("No steps found to execute");
		}
		
		String requestId = UUID.randomUUID().toString();
		livePoint.requestExecuteSteps(requestId, stepHolder.getSteps());
		return requestId;
	}
	
	/**
	 * Drops execution to the given stack frame id (same semantics as Prism drop-to-frame).
	 */
	public boolean dropToFrame(String executionId, String stackElementId)
	{
		ensureOpen();
		
		if(StringUtils.isBlank(stackElementId))
		{
			throw new InvalidArgumentException("stackElementId cannot be empty");
		}
		
		LiveDebugPoint livePoint = requireLivePoint(executionId);
		return livePoint.requestDropToFrame(new ClientMssgDropToFrame(executionId, stackElementId));
	}
	
	private boolean requestDebugOp(String executionId, DebugOp debugOp, boolean ignoreError)
	{
		ensureOpen();
		LiveDebugPoint livePoint = requireLivePoint(executionId);
		return livePoint.requestRelease(new ClientMssgDebugOp(executionId, debugOp, ignoreError));
	}
	
	private LiveDebugPoint requireLivePoint(String executionId)
	{
		if(StringUtils.isBlank(executionId))
		{
			throw new InvalidArgumentException("executionId cannot be empty");
		}
		
		LiveDebugPoint livePoint = debugFlowManager.getLiveDebugPoint(executionId);
		
		if(livePoint == null)
		{
			throw new InvalidStateException("No live debug point found with execution id: {}", executionId);
		}
		
		return livePoint;
	}
}
