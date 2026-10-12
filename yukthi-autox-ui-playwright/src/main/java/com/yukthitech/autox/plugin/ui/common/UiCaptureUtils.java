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
package com.yukthitech.autox.plugin.ui.common;

import com.google.gson.JsonObject;
import com.microsoft.playwright.CDPSession;
import com.microsoft.playwright.Page;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Shared Playwright page capture helpers (PNG screenshot and MHTML snapshot).
 * Does not write report log files.
 *
 * @author akranthikiran
 */
public final class UiCaptureUtils
{
	private UiCaptureUtils()
	{
	}
	
	/**
	 * Takes a PNG screenshot of the given page.
	 *
	 * @param page active page
	 * @return png image bytes
	 */
	public static byte[] takeScreenshot(Page page)
	{
		return page.screenshot();
	}
	
	/**
	 * Captures an MHTML snapshot of the given page via CDP {@code Page.captureSnapshot}.
	 *
	 * @param page active page
	 * @return MHTML content
	 * @throws InvalidStateException if CDP capture is not supported or fails
	 */
	public static String captureHtmlSnapshot(Page page)
	{
		CDPSession cdpSession = null;
		
		try
		{
			cdpSession = page.context().newCDPSession(page);
			JsonObject result = cdpSession.send("Page.captureSnapshot", null);
			return result.get("data").getAsString();
		}catch(Exception ex)
		{
			throw new InvalidStateException(
					"Snapshot could not be taken. CDP based MHTML capture is not supported or failed for the current browser",
					ex);
		}finally
		{
			if(cdpSession != null)
			{
				try
				{
					cdpSession.detach();
				}catch(Exception ex)
				{
					// ignore detach failures
				}
			}
		}
	}
}
