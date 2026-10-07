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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.yukthitech.autox.config.ErrorDetails;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ReportLogFile;
import com.yukthitech.autox.event.EventManager;
import com.yukthitech.autox.exec.report.LogLevel;
import com.yukthitech.autox.plugin.AbstractPluginSession;
import com.yukthitech.autox.plugin.ui.common.Dimension;
import com.yukthitech.autox.plugin.ui.common.Point;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Session of playwright plugin. For each driver name, this session maintains - playwright instance, browser, 
 * browser-context, window (page) handles, current frame and pending dialogs.
 * <p>
 * Dialogs (alert/confirm/prompt): A dialog listener is registered on every page. Dialogs are queued and can be 
 * handled later using steps (uiHandleAlert, uiHandleConfirm, uiHandlePrompt). Note: as modal dialogs block the 
 * action which triggered them, alert (and beforeunload) dialogs are accepted immediately (after being queued, so that 
 * the message can still be validated). Confirm and prompt dialogs are kept pending till they are handled by the steps.
 * 
 * @author akiran
 */
public class PlaywrightPluginSession extends AbstractPluginSession<PlaywrightPluginSession, PlaywrightPlugin>
{
	private static Logger logger = LogManager.getLogger(PlaywrightPluginSession.class);
	
	/**
	 * Handle of the main window.
	 */
	public static final String MAIN_WINDOW_HANDLE = "main";
	
	/**
	 * Wrapper over playwright dialog which tracks the handling status.
	 */
	public static class PendingDialog
	{
		private Dialog dialog;
		
		private String type;
		
		private String message;
		
		private String defaultValue;
		
		private boolean resolved;
		
		private boolean accepted;
		
		PendingDialog(Dialog dialog)
		{
			this.dialog = dialog;
			this.type = dialog.type();
			this.message = dialog.message();
			this.defaultValue = dialog.defaultValue();
		}
		
		public String getType()
		{
			return type;
		}
		
		public String getMessage()
		{
			return message;
		}
		
		public String getDefaultValue()
		{
			return defaultValue;
		}
		
		public boolean isResolved()
		{
			return resolved;
		}
		
		/**
		 * Flag indicating if dialog was resolved by accepting it. Valid only if dialog is resolved.
		 */
		public boolean isAccepted()
		{
			return accepted;
		}
		
		public synchronized void accept()
		{
			accept(null);
		}
		
		/**
		 * Accepts the dialog.
		 * @param promptText text to be entered in prompt dialog. Can be null.
		 */
		public synchronized void accept(String promptText)
		{
			if(resolved)
			{
				return;
			}
			
			if(promptText != null)
			{
				dialog.accept(promptText);
			}
			else
			{
				dialog.accept();
			}
			
			resolved = true;
			accepted = true;
		}
		
		public synchronized void dismiss()
		{
			if(resolved)
			{
				return;
			}
			
			dialog.dismiss();
			
			resolved = true;
			accepted = false;
		}
		
		@Override
		public String toString()
		{
			return String.format("Dialog[type: %s, message: %s]", type, message);
		}
	}
	
	/**
	 * State maintained per driver name.
	 */
	private static class DriverContext
	{
		Playwright playwright;
		
		Browser browser;
		
		BrowserContext context;
		
		/**
		 * Handle to page mapping, includes main page.
		 */
		Map<String, Page> windows = new LinkedHashMap<>();
		
		Page mainPage;
		
		Page activePage;
		
		/**
		 * Frame (under active page) in which operations should be performed. When null, main frame is used.
		 */
		Frame currentFrame;
		
		Queue<PendingDialog> dialogs = new ConcurrentLinkedQueue<>();
		
		/**
		 * Last known mouse position, used for relative mouse moves.
		 */
		double mouseX, mouseY;
		
		String handleOf(Page page)
		{
			for(Map.Entry<String, Page> entry : windows.entrySet())
			{
				if(entry.getValue() == page)
				{
					return entry.getKey();
				}
			}
			
			return null;
		}
	}
	
	private Map<String, DriverContext> drivers = new HashMap<>();
	
	private String defaultDriverName;
	
	private String sessionId = UUID.randomUUID().toString();
	
	public PlaywrightPluginSession(PlaywrightPlugin parentPlugin, String defaultDriverName)
	{
		super(parentPlugin);
		this.defaultDriverName = defaultDriverName;
		
		EventManager.getInstance().invokePluginEventHandler(this, IUiEvent.EVENT_INIT, null);
	}
	
	/**
	 * Gets the resource url.
	 *
	 * @param resource the resource
	 * @return the resource url
	 */
	public String getResourceUrl(String resource)
	{
		if(!resource.startsWith("/"))
		{
			resource = "/" + resource;
		}
		
		return parentPlugin.getBaseUrl() + resource;
	}
	
	private String resolveName(String name)
	{
		if(StringUtils.isBlank(name))
		{
			if(StringUtils.isBlank(defaultDriverName))
			{
				throw new InvalidStateException("No default driver specified");
			}
			
			return defaultDriverName;
		}
		
		return name;
	}
	
	private BrowserType getBrowserType(Playwright playwright, String type)
	{
		switch(type)
		{
			case PlaywrightDriverConfig.BROWSER_FIREFOX:
				return playwright.firefox();
			case PlaywrightDriverConfig.BROWSER_WEBKIT:
				return playwright.webkit();
			case PlaywrightDriverConfig.BROWSER_CHROMIUM:
				return playwright.chromium();
			default:
				throw new InvalidArgumentException("Invalid browser type specified: {}", type);
		}
	}
	
	/**
	 * Registers specified page in the driver context and adds required listeners.
	 */
	private void registerPage(DriverContext driverCtx, Page page, String handle)
	{
		if(driverCtx.handleOf(page) != null)
		{
			return;
		}
		
		if(handle == null)
		{
			handle = "page-" + Integer.toHexString(System.identityHashCode(page));
			
			while(driverCtx.windows.containsKey(handle))
			{
				handle = handle + "_";
			}
		}
		
		driverCtx.windows.put(handle, page);
		
		page.onDialog(dialog -> onDialog(driverCtx, dialog));
		
		page.onClose(closedPage -> 
		{
			String closedHandle = driverCtx.handleOf(closedPage);
			
			if(closedHandle != null)
			{
				driverCtx.windows.remove(closedHandle);
			}
			
			if(driverCtx.activePage == closedPage)
			{
				driverCtx.activePage = null;
				driverCtx.currentFrame = null;
			}
		});
	}
	
	private void onDialog(DriverContext driverCtx, Dialog dialog)
	{
		PendingDialog pendingDialog = new PendingDialog(dialog);
		driverCtx.dialogs.add(pendingDialog);
		
		logger.debug("Encountered browser dialog: {}", pendingDialog);
		
		//as alert and before-unload dialogs have only one possible action, accept them immediately, so that 
		// the action which triggered them does not get blocked
		if("alert".equals(pendingDialog.getType()) || "beforeunload".equals(pendingDialog.getType()))
		{
			pendingDialog.accept();
		}
	}
	
	private DriverContext createDriver(String name) throws Exception
	{
		PlaywrightDriverConfig driverConfig = parentPlugin.getDriverConfig(name);
		
		if(driverConfig == null)
		{
			throw new InvalidArgumentException("No driver found with specified name: {}", name);
		}
		
		//close old driver, if any (important when user-data-dir is used)
		DriverContext oldDriver = drivers.remove(name);
		
		if(oldDriver != null)
		{
			closeDriverContext(oldDriver);
		}
		
		if(driverConfig.getDownloadFolder() != null)
		{
			FileUtils.forceMkdir(new File(driverConfig.getDownloadFolder(), sessionId));
		}
		
		DriverContext driverCtx = new DriverContext();
		
		try
		{
			driverCtx.playwright = Playwright.create();
			BrowserType browserType = getBrowserType(driverCtx.playwright, driverConfig.getBrowserType());
			
			List<String> args = driverConfig.getExtraArgumentList();
			
			if(StringUtils.isNotBlank(driverConfig.getUserDataDir()))
			{
				BrowserType.LaunchPersistentContextOptions options = new BrowserType.LaunchPersistentContextOptions()
						.setHeadless(driverConfig.isHeadless())
						.setAcceptDownloads(true)
						.setArgs(args);
				
				if(!driverConfig.isHeadless())
				{
					//null view port disables fixed viewport emulation (browser window size is used)
					options.setViewportSize(null);
				}
				
				if(StringUtils.isNotBlank(driverConfig.getChannel()))
				{
					options.setChannel(driverConfig.getChannel());
				}
				
				if(driverConfig.getSlowMo() > 0)
				{
					options.setSlowMo(driverConfig.getSlowMo());
				}
				
				driverCtx.context = browserType.launchPersistentContext(Paths.get(driverConfig.getUserDataDir()), options);
				driverCtx.browser = driverCtx.context.browser();
			}
			else
			{
				BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
						.setHeadless(driverConfig.isHeadless())
						.setArgs(args);
				
				if(StringUtils.isNotBlank(driverConfig.getChannel()))
				{
					options.setChannel(driverConfig.getChannel());
				}
				
				if(driverConfig.getSlowMo() > 0)
				{
					options.setSlowMo(driverConfig.getSlowMo());
				}
				
				driverCtx.browser = browserType.launch(options);
				
				Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setAcceptDownloads(true);
				
				if(!driverConfig.isHeadless())
				{
					//null view port disables fixed viewport emulation (browser window size is used)
					contextOptions.setViewportSize(null);
				}
				
				driverCtx.context = driverCtx.browser.newContext(contextOptions);
			}
			
			List<Page> existingPages = driverCtx.context.pages();
			Page mainPage = existingPages.isEmpty() ? driverCtx.context.newPage() : existingPages.get(0);
			
			driverCtx.mainPage = mainPage;
			driverCtx.activePage = mainPage;
			registerPage(driverCtx, mainPage, MAIN_WINDOW_HANDLE);
			
			//register for new pages (popups, new tabs) 
			driverCtx.context.onPage(newPage -> registerPage(driverCtx, newPage, null));
			
			if(driverConfig.getDefaultPage() != null)
			{
				logger.debug("Taking driver to default page: " + driverConfig.getDefaultPage());
				mainPage.navigate(driverConfig.getDefaultPage());
			}
		}catch(Exception ex)
		{
			closeDriverContext(driverCtx);
			throw ex;
		}
		
		drivers.put(name, driverCtx);
		return driverCtx;
	}
	
	/**
	 * Fetches the driver context for specified driver name. If not available, creates new one.
	 */
	private DriverContext getDriverContext(String name)
	{
		name = resolveName(name);
		
		DriverContext driverCtx = this.drivers.get(name);
		
		if(driverCtx != null)
		{
			return driverCtx;
		}
		
		try
		{
			return createDriver(name);
		} catch(RuntimeException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new InvalidStateException("Failed to initialize driver with name: " + name, ex);
		}
	}
	
	private Page getActivePage(DriverContext driverCtx)
	{
		Page page = driverCtx.activePage;
		
		if(page != null && !page.isClosed())
		{
			return page;
		}
		
		//if active page is closed, fall back to main page or first available page
		driverCtx.currentFrame = null;
		
		if(driverCtx.mainPage != null && !driverCtx.mainPage.isClosed())
		{
			driverCtx.activePage = driverCtx.mainPage;
			return driverCtx.activePage;
		}
		
		for(Page candidate : new ArrayList<>(driverCtx.windows.values()))
		{
			if(!candidate.isClosed())
			{
				driverCtx.activePage = candidate;
				return candidate;
			}
		}
		
		throw new InvalidStateException("All the windows of the driver are closed. Reset the session to open new window.");
	}
	
	/**
	 * Fetches the active page of the specified driver. If driver is not yet created, it will be created.
	 * @param name name of the driver. If null, default driver will be used.
	 * @return active page.
	 */
	public Page getPage(String name)
	{
		return getActivePage(getDriverContext(name));
	}
	
	/**
	 * Fetches the browser context of the specified driver.
	 * @param name name of the driver. If null, default driver will be used.
	 * @return browser context.
	 */
	public BrowserContext getBrowserContext(String name)
	{
		return getDriverContext(name).context;
	}
	
	/**
	 * Fetches the handle of the main window.
	 *
	 * @return the main window handle
	 */
	public String getMainWindowHandle(String name)
	{
		DriverContext driverCtx = this.drivers.get(resolveName(name));
		return (driverCtx != null) ? MAIN_WINDOW_HANDLE : null;
	}
	
	/**
	 * Fetches the handle of the active window.
	 */
	public String getCurrentWindowHandle(String name)
	{
		DriverContext driverCtx = getDriverContext(name);
		return driverCtx.handleOf(getActivePage(driverCtx));
	}
	
	/**
	 * Fetches the currently open windows of the driver.
	 * @return handle to page mapping
	 */
	public Map<String, Page> getWindows(String name)
	{
		DriverContext driverCtx = getDriverContext(name);
		return new LinkedHashMap<>(driverCtx.windows);
	}
	
	/**
	 * Finds the window with specified handle or window name (name specified during window.open()).
	 * @param name driver name
	 * @param handleOrName handle or window name
	 * @return matching page. Null if no window is found.
	 */
	public Page findWindow(String name, String handleOrName)
	{
		DriverContext driverCtx = getDriverContext(name);
		
		Page page = driverCtx.windows.get(handleOrName);
		
		if(page != null && !page.isClosed())
		{
			return page;
		}
		
		for(Page candidate : new ArrayList<>(driverCtx.windows.values()))
		{
			if(candidate.isClosed())
			{
				continue;
			}
			
			try
			{
				Object windowName = candidate.evaluate("window.name");
				
				if(handleOrName.equals(windowName))
				{
					return candidate;
				}
			}catch(PlaywrightException ex)
			{
				logger.debug("Ignoring error while fetching window name. Error: {}", "" + ex);
			}
		}
		
		return null;
	}
	
	/**
	 * Makes the window with specified handle/name as the active window.
	 * @param name driver name
	 * @param handleOrName handle or name of the window to switch to
	 * @return switched page
	 */
	public Page switchToWindow(String name, String handleOrName)
	{
		Page page = findWindow(name, handleOrName);
		
		if(page == null)
		{
			throw new InvalidArgumentException("No window found with handle/name: {}", handleOrName);
		}
		
		DriverContext driverCtx = getDriverContext(name);
		
		if(driverCtx.activePage != page)
		{
			driverCtx.activePage = page;
			driverCtx.currentFrame = null;
		}
		
		page.bringToFront();
		return page;
	}
	
	/**
	 * Closes the specified window. If handle/name is not specified, current window will be closed.
	 * @param name driver name
	 * @param handleOrName handle/name of the window to be closed. 
	 */
	public void closeWindow(String name, String handleOrName)
	{
		Page page = null;
		
		if(StringUtils.isBlank(handleOrName))
		{
			page = getPage(name);
		}
		else
		{
			page = findWindow(name, handleOrName);
			
			if(page == null)
			{
				throw new InvalidArgumentException("No window found with handle/name: {}", handleOrName);
			}
		}
		
		page.close();
	}
	
	/**
	 * Fetches the frame in which current operations should be performed.
	 */
	public Frame getCurrentFrame(String name)
	{
		DriverContext driverCtx = getDriverContext(name);
		Page page = getActivePage(driverCtx);
		Frame frame = driverCtx.currentFrame;
		
		if(frame != null && !frame.isDetached() && frame.page() == page)
		{
			return frame;
		}
		
		driverCtx.currentFrame = null;
		return page.mainFrame();
	}
	
	/**
	 * Sets the frame in which operations should be performed. Null can be used to go back to main frame.
	 */
	public void setCurrentFrame(String name, Frame frame)
	{
		getDriverContext(name).currentFrame = frame;
	}
	
	/**
	 * Creates a locator for specified playwright selector, under current frame of the driver.
	 * @param name driver name
	 * @param selector playwright selector
	 * @return locator
	 */
	public Locator newLocator(String name, String selector)
	{
		return getCurrentFrame(name).locator(selector);
	}
	
	/**
	 * Polls the dialog queue for specified time and fetches the first dialog from it.
	 * @param name driver name
	 * @param timeoutMillis max time to wait for the dialog
	 * @return next dialog in the queue. Null if no dialog appeared within specified time.
	 */
	public PendingDialog pollDialog(String name, long timeoutMillis)
	{
		DriverContext driverCtx = getDriverContext(name);
		long endTime = System.currentTimeMillis() + timeoutMillis;
		
		do
		{
			PendingDialog dialog = driverCtx.dialogs.poll();
			
			if(dialog != null)
			{
				return dialog;
			}
			
			if(timeoutMillis <= 0)
			{
				break;
			}
			
			try
			{
				//wait using page, so that playwright events (dialogs) can be processed during wait
				getActivePage(driverCtx).waitForTimeout(100);
			}catch(PlaywrightException ex)
			{
				try
				{
					Thread.sleep(100);
				}catch(InterruptedException iex)
				{
					Thread.currentThread().interrupt();
					break;
				}
			}
		}while(System.currentTimeMillis() < endTime);
		
		return null;
	}
	
	/**
	 * Checks if there is any dialog in the queue (handled by listener or not), which is yet to be fetched by dialog handling steps.
	 */
	public boolean hasQueuedDialog(String name)
	{
		DriverContext driverCtx = getDriverContext(name);
		
		//process any pending playwright events, before checking the queue
		try
		{
			getActivePage(driverCtx).waitForTimeout(0);
		}catch(PlaywrightException ex)
		{
			//ignore
		}
		
		return !driverCtx.dialogs.isEmpty();
	}
	
	/**
	 * Checks if there is any dialog which is waiting to be handled.
	 */
	public boolean hasPendingDialog(String name)
	{
		DriverContext driverCtx = this.drivers.get(resolveName(name));
		
		if(driverCtx == null)
		{
			return false;
		}
		
		for(PendingDialog dialog : driverCtx.dialogs)
		{
			if(!dialog.isResolved())
			{
				return true;
			}
		}
		
		return false;
	}
	
	private Dimension getBrowserSize(Page page)
	{
		Map<?, ?> res = (Map<?, ?>) page.evaluate("() => ({ width: window.outerWidth, height: window.outerHeight })");
		return new Dimension(((Number) res.get("width")).intValue(), ((Number) res.get("height")).intValue());
	}
	
	private Point getBrowserPosition(Page page)
	{
		Map<?, ?> res = (Map<?, ?>) page.evaluate("() => ({ x: window.screenX, y: window.screenY })");
		return new Point(((Number) res.get("x")).intValue(), ((Number) res.get("y")).intValue());
	}
	
	/**
	 * Fetches the browser (outer window) size.
	 */
	public Dimension getBrowserSize(String name)
	{
		return getBrowserSize(getPage(name));
	}
	
	/**
	 * Fetches the browser (outer window) position.
	 */
	public Point getBrowserPosition(String name)
	{
		return getBrowserPosition(getPage(name));
	}
	
	/**
	 * Moves the mouse to specified absolute position (relative to view port) and tracks the position.
	 */
	public void moveMouseTo(String name, double x, double y)
	{
		DriverContext driverCtx = getDriverContext(name);
		getActivePage(driverCtx).mouse().move(x, y);
		
		driverCtx.mouseX = x;
		driverCtx.mouseY = y;
	}
	
	/**
	 * Moves the mouse by specified offset from the last known mouse position.
	 */
	public void moveMouseBy(String name, double xOffset, double yOffset)
	{
		DriverContext driverCtx = getDriverContext(name);
		moveMouseTo(name, driverCtx.mouseX + xOffset, driverCtx.mouseY + yOffset);
	}
	
	/**
	 * Takes screen shot of the active page of the driver.
	 * @return png image content
	 */
	public byte[] takeScreenshot(String name)
	{
		return getPage(name).screenshot();
	}
	
	/**
	 * Recreates the driver object. Existing driver (if any) will be closed.
	 */
	public void resetDriver(String name)
	{
		name = resolveName(name);
		
		try
		{
			createDriver(name);
		} catch(RuntimeException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new InvalidStateException("Failed to initialize driver with name: " + name, ex);
		}
	}
	
	/**
	 * Returns true if downloads are supported by current driver.
	 * @return true if download automation is supported.
	 */
	public boolean isDownloadsSupported(String name)
	{
		name = resolveName(name);
		
		PlaywrightDriverConfig driverConfig = parentPlugin.getDriverConfig(name);
		return (driverConfig.getDownloadFolder() != null);
	}
	
	/**
	 * Fetches folder path when downloaded files can be expected.
	 * @return
	 */
	public String getDownloadFolder(String name)
	{
		name = resolveName(name);
		PlaywrightDriverConfig driverConfig = parentPlugin.getDriverConfig(name);

		String folder = driverConfig.getDownloadFolder();
		
		if(folder == null)
		{
			return null;
		}
		
		return new File(folder, sessionId).getPath();
	}
	
	/**
	 * Cleans the download folder.
	 */
	public void cleanDownloadFolder(String name)
	{
		String downloadFolder = getDownloadFolder(name); 
				
		if(downloadFolder == null)
		{
			return;
		}
		
		try
		{
			File folder = new File(downloadFolder);
			
			if(folder.exists())
			{
				FileUtils.forceDelete(folder);
			}
			
			FileUtils.forceMkdir(folder);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to clean download folder: {}", downloadFolder, ex);
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.config.IPlugin#handleError(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.config.ErrorDetails)
	 */
	@Override
	public void handleError(AutomationContext context, ErrorDetails errorDetails)
	{
		if(drivers.isEmpty())
		{
			return;
		}

		for(Map.Entry<String, DriverContext> driverEntry : drivers.entrySet())
		{
			try
			{
				Page activePage = getActivePage(driverEntry.getValue());
				
				byte[] screenshot = activePage.screenshot();
				ReportLogFile reportLogFile = context.newLogFile("error-screenshot", "png");
				FileUtils.writeByteArrayToFile(reportLogFile.getFile(), screenshot);
				
				errorDetails.getExecutionLogger().logImage(
						String.format("Screen shot during error. [Driver name: %s]", driverEntry.getKey()), 
						reportLogFile, LogLevel.ERROR);
				
				errorDetails.getExecutionLogger().error("During error browser details are: [Postition: {}, Size: {}]",
						getBrowserPosition(activePage),
						getBrowserSize(activePage));
			}catch(PlaywrightException | InvalidStateException ex)
			{
				logger.warn("Found the session/page to be closed, so skipping taking screen shot. Error: {}", "" + ex);
			}catch(Exception ex)
			{
				logger.warn("An error occurred while taking error screen shot. Error: {}", "" + ex);
			}
		}
	}
	
	private void closeDriverContext(DriverContext driverCtx)
	{
		try
		{
			if(driverCtx.context != null)
			{
				driverCtx.context.close();
			}
		}catch(RuntimeException ex)
		{
			//ignore if session is already closed
		}
		
		try
		{
			if(driverCtx.browser != null)
			{
				driverCtx.browser.close();
			}
		}catch(RuntimeException ex)
		{
			//ignore if session is already closed
		}
		
		try
		{
			if(driverCtx.playwright != null)
			{
				driverCtx.playwright.close();
			}
		}catch(RuntimeException ex)
		{
			//ignore if session is already closed
		}
		
		driverCtx.windows.clear();
		driverCtx.dialogs.clear();
	}
	
	public void closeDriver(String driverName)
	{
		DriverContext driverCtx = this.drivers.remove(resolveName(driverName));
		
		if(driverCtx == null)
		{
			return;
		}
		
		closeDriverContext(driverCtx);
	}
	
	@Override
	public void close()
	{
		for(DriverContext driverCtx : this.drivers.values())
		{
			closeDriverContext(driverCtx);
		}
		
		drivers.clear();
	}
}
