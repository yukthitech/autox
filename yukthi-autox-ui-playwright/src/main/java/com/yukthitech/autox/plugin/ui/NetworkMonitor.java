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
package com.yukthitech.autox.plugin.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;

/**
 * Captures network traffic at {@link BrowserContext} level until {@link #close()} is called.
 * <p>
 * Live Playwright {@link Request} / {@link Response} objects are retained on each {@link NetworkCall}
 * so consumers can read headers and bodies on demand. Prefer inspecting those objects while the
 * browser context is still alive (before driver reset/quit). {@link #close()} only unregisters
 * listeners; it does not clear call references.
 * 
 * @author akiran
 */
public class NetworkMonitor implements AutoCloseable
{
	private static final Set<String> API_RESOURCE_TYPES = Set.of("xhr", "fetch");

	/**
	 * One observed network request, optionally paired with its response.
	 */
	public static class NetworkCall
	{
		private final Request request;

		private volatile Response response;

		private volatile Integer statusCode;

		private volatile boolean finished;

		private volatile boolean failed;

		private volatile String failureText;

		NetworkCall(Request request)
		{
			this.request = request;
		}

		public Request getRequest()
		{
			return request;
		}

		public Response getResponse()
		{
			return response;
		}

		public String getMethod()
		{
			return request.method();
		}

		public String getUrl()
		{
			return request.url();
		}

		public String getResourceType()
		{
			return request.resourceType();
		}

		public Integer getStatusCode()
		{
			return statusCode;
		}

		public boolean isFinished()
		{
			return finished;
		}

		public boolean isFailed()
		{
			return failed || (statusCode != null && statusCode >= 400);
		}

		/**
		 * True when a response was received with HTTP status in 200–399.
		 */
		public boolean isSuccessful()
		{
			return statusCode != null && statusCode >= 200 && statusCode < 400;
		}

		public String getFailureText()
		{
			return failureText;
		}

		void onResponse(Response response)
		{
			this.response = response;
			this.statusCode = response.status();

			if(statusCode >= 400)
			{
				this.failed = true;
			}
		}

		void onFinished()
		{
			this.finished = true;
		}

		void onFailed(String failureText)
		{
			this.failed = true;
			this.finished = true;
			this.failureText = failureText;
		}
	}

	private final BrowserContext browserContext;

	private final Map<Request, NetworkCall> callsByRequest = new ConcurrentHashMap<>();

	private final ConcurrentLinkedQueue<NetworkCall> callsInOrder = new ConcurrentLinkedQueue<>();

	private final AtomicInteger inProgressCount = new AtomicInteger();

	private final AtomicInteger requestCount = new AtomicInteger();

	private final AtomicInteger responseCount = new AtomicInteger();

	private final AtomicBoolean closed = new AtomicBoolean(false);

	private final Consumer<Request> requestListener = this::onRequest;

	private final Consumer<Response> responseListener = this::onResponse;

	private final Consumer<Request> requestFinishedListener = this::onRequestFinished;

	private final Consumer<Request> requestFailedListener = this::onRequestFailed;

	NetworkMonitor(BrowserContext browserContext)
	{
		this.browserContext = browserContext;
		browserContext.onRequest(requestListener);
		browserContext.onResponse(responseListener);
		browserContext.onRequestFinished(requestFinishedListener);
		browserContext.onRequestFailed(requestFailedListener);
	}

	private void onRequest(Request request)
	{
		NetworkCall call = new NetworkCall(request);
		callsByRequest.put(request, call);
		callsInOrder.add(call);
		requestCount.incrementAndGet();
		inProgressCount.incrementAndGet();
	}

	private void onResponse(Response response)
	{
		NetworkCall call = callsByRequest.get(response.request());

		if(call == null)
		{
			return;
		}

		call.onResponse(response);
		responseCount.incrementAndGet();
	}

	private void onRequestFinished(Request request)
	{
		NetworkCall call = callsByRequest.get(request);

		if(call != null)
		{
			call.onFinished();
		}

		decrementInProgress();
	}

	private void onRequestFailed(Request request)
	{
		NetworkCall call = callsByRequest.get(request);

		if(call != null)
		{
			call.onFailed(request.failure());
		}

		decrementInProgress();
	}

	private void decrementInProgress()
	{
		inProgressCount.updateAndGet(current -> current > 0 ? current - 1 : 0);
	}

	public int getInProgressCount()
	{
		return inProgressCount.get();
	}

	public int getRequestCount()
	{
		return requestCount.get();
	}

	public int getResponseCount()
	{
		return responseCount.get();
	}

	public int getFailedRequestCount()
	{
		int failed = 0;

		for(NetworkCall call : callsInOrder)
		{
			if(call.isFailed())
			{
				failed++;
			}
		}

		return failed;
	}

	/**
	 * Returns observed API calls ({@code xhr} / {@code fetch} only) in encounter order.
	 * Each call retains live {@link Request} / {@link Response} references for header/body access.
	 */
	public List<NetworkCall> getApiCalls()
	{
		List<NetworkCall> apiCalls = new ArrayList<>();

		for(NetworkCall call : callsInOrder)
		{
			if(API_RESOURCE_TYPES.contains(call.getResourceType()))
			{
				apiCalls.add(call);
			}
		}

		return apiCalls;
	}

	/**
	 * Unregisters network listeners. Idempotent. Does not clear captured {@link NetworkCall} refs.
	 */
	@Override
	public void close()
	{
		if(!closed.compareAndSet(false, true))
		{
			return;
		}

		try
		{
			browserContext.offRequest(requestListener);
			browserContext.offResponse(responseListener);
			browserContext.offRequestFinished(requestFinishedListener);
			browserContext.offRequestFailed(requestFailedListener);
		}catch(Exception ex)
		{
			// context may already be closed
		}
	}
}
