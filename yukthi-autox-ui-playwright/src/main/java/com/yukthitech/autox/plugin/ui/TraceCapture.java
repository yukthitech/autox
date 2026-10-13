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

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Tracing;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Captures Playwright tracing on a {@link BrowserContext} until {@link #stop()} is called.
 * <p>
 * Start options match AutoX {@code ui-capture-tracing}: screenshots, DOM snapshots, and sources.
 * The resulting zip is written to a temp file for the consumer to use (not auto-attached to the report).
 * <p>
 * Only one Playwright trace may be active per context — do not overlap with another
 * {@code startTracing()} or {@code s:ui-capture-tracing} on the same driver context.
 * 
 * @author akiran
 */
public class TraceCapture
{
	private final BrowserContext browserContext;

	private final AtomicBoolean stopped = new AtomicBoolean(false);

	private volatile File traceFile;

	TraceCapture(BrowserContext browserContext)
	{
		this.browserContext = browserContext;
		browserContext.tracing().start(new Tracing.StartOptions()
				.setScreenshots(true)
				.setSnapshots(true)
				.setSources(true));
	}

	/**
	 * Stops tracing and writes the trace zip to a temp file.
	 * Idempotent: subsequent calls return the same file without stopping again.
	 *
	 * @return the trace zip file
	 */
	public File stop()
	{
		if(!stopped.compareAndSet(false, true))
		{
			return traceFile;
		}

		File tempFile = null;

		try
		{
			tempFile = File.createTempFile("autox-trace-", ".zip");
			browserContext.tracing().stop(new Tracing.StopOptions()
					.setPath(tempFile.toPath()));
			this.traceFile = tempFile;
			return tempFile;
		}catch(Exception ex)
		{
			if(tempFile != null && tempFile.exists())
			{
				tempFile.delete();
			}

			throw new InvalidStateException("Failed to stop Playwright tracing and write temp zip", ex);
		}
	}

	/**
	 * Trace zip after a successful {@link #stop()}; {@code null} before stop or if stop has not succeeded.
	 */
	public File getTraceFile()
	{
		return traceFile;
	}
}
