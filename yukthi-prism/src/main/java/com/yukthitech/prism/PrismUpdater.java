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
package com.yukthitech.prism;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yukthitech.prism.events.IdeStartedEvent;
import com.yukthitech.prism.services.IdeEventHandler;
import com.yukthitech.utils.ZipUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Service which can capable for updating the ide.
 * @author akranthikiran
 */
@Service
public class PrismUpdater
{
	private static Logger logger = LogManager.getLogger(PrismUpdater.class);
	
	private static long timeoutInSec = 180;
	
	@Value("${autox.ide.version.url}")
	private String versionUrl;
	
	@Value("${autox.ide.download.url}")
	private String downloadUrl;

	private String getLatestVersion() throws Exception
	{
		File verFile = downloadFile(versionUrl, "autox-ide-version", ".json");
		
		if(verFile == null)
		{
			return null;
		}
		
		FileInputStream fis = new FileInputStream(verFile);
		String latestVer = Version.getVersion(fis);
		fis.close();
		
		return latestVer;
	}
	
	private File downloadFile(String urlStr, String prefix, String extension) throws Exception
	{
		urlStr = String.format(urlStr, Version.getVersionMap().getIdeVersion());
		
		logger.debug("Downloading file from: " + urlStr);
		
		URL url = new URL(urlStr);
		URLConnection urlConnection = url.openConnection();
		InputStream is = urlConnection.getInputStream();
		
		File tempFile = File.createTempFile(prefix, extension);
		CountDownLatch latch = new CountDownLatch(1);
		
		Thread downloader = new Thread() 
		{
			public void run()
			{
				try
				{
					FileUtils.copyInputStreamToFile(is, tempFile);
					is.close();
				} catch(Exception ex)
				{
					logger.warn("An error occurred while downloading the remote file: " + ex);
				} finally
				{
					latch.countDown();					
				}
			}
		};
		
		downloader.start();
		
		if(!latch.await(timeoutInSec, TimeUnit.SECONDS))
		{
			logger.error("Download got timeout after waiting for {} Seconds.", timeoutInSec);
			
			if(is != null)
			{
				is.close();
			}
			
			if(urlConnection instanceof HttpURLConnection)
			{
				((HttpURLConnection) urlConnection).disconnect();
				logger.debug("Connection is forcefully closed..");
			}
			
			return null;
		}
		
		return tempFile;
	}
	
	private File downloadLatestIde() throws Exception
	{
		File tempFile = downloadFile(downloadUrl, "autox-ide-latest", ".zip");
		
		if(tempFile == null)
		{
			return null;
		}
		
		try
		{
			logger.debug("Download completed. Validating the download..");
			
			Set<String> entries = ZipUtils.getZipEntries(tempFile);
			boolean foundJar = false;
			
			for(String entry : entries)
			{
				entry = entry.toLowerCase();
				
				if(entry.startsWith("lib/yukthi-prism-") && entry.endsWith(".jar"))
				{
					foundJar = true;
					break;
				}
			}
			
			if(!foundJar)
			{
				logger.error("In downloaded zip found ide jar file to be missing. Assuming zip is corrupted, deleting zip file");
				tempFile.delete();
				return null;
			}
			
		} catch(Exception ex)
		{
			logger.error("Failed to extract zip entries from the downloaded ide zip file: {}.\n\tError: {}", tempFile.getPath(), "" + ex);
			return null;
		}
		
		return tempFile;
	}
	
	public void unzipLib(File zipFile, File newLibFolder)
	{
		try
		{
			//create fresh new lib folder
			FileUtils.deleteDirectory(newLibFolder);
			FileUtils.forceMkdir(newLibFolder);
			
			//start unzipping lib folder
			ZipFile zfile = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> it = zfile.entries();
			
			ZipEntry zipEntry = null;
			String entry = null;
			
			while(it.hasMoreElements())
			{
				zipEntry = it.nextElement();

				if(zipEntry.isDirectory())
				{
					continue;
				}
				
				entry = zipEntry.getName().toLowerCase();
				
				if(!entry.startsWith("lib/") || !entry.endsWith(".jar"))
				{
					continue;
				}
				
				File entryFile = new File(newLibFolder, zipEntry.getName().replace("/", File.separator));
				entryFile = new File(newLibFolder, entryFile.getName());
				
				InputStream entryStream = zfile.getInputStream(zipEntry);
				FileUtils.copyInputStreamToFile(entryStream, entryFile);
				entryStream.close();
			}

			zfile.close();
		} catch(IOException ex)
		{
			throw new InvalidStateException("An exception occurred while unzipping specified file: " + zipFile, ex);
		}
	}
	
	private void checkForUpdateSync()
	{
		try
		{
			String latestVersion = getLatestVersion();
			logger.debug("Got latest version as: {}", latestVersion);
			
			if(latestVersion == null)
			{
				logger.error("Failed to determine latest version. So skipping the upgrade");
				return;
			}
			
			String localVersion = Version.getLocalVersion();
			logger.debug("Got local version as: {}", localVersion);
			
			if(latestVersion.equals(localVersion))
			{
				logger.debug("Found the ide is upto date.");
				return;
			}
			
			int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), "There is a new version of Prism IDE. Would you like to download?", "IDE Upgrade", JOptionPane.YES_NO_OPTION);
			
			if(res == JOptionPane.NO_OPTION)
			{
				return;
			}
			
			logger.debug("Found new updates available. Downloading latest ide zip file (this may take few mins)...");
			File latestIde = downloadLatestIde();
			
			if(latestIde == null)
			{
				logger.error("An error occurred while downloading latest zip file. Hence skipping the upgrade..");
				return;
			}
			
			logger.debug("Downloaded latest ide as file: {}", latestIde.getPath());
			
			unzipLib(latestIde, new File(".." + File.separator + "lib-new"));
			logger.debug("New ide lib folder is created successfully...");
			
			latestIde.delete();
			
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Your Prism IDE is upgraded with latest version.\nPlease restart your IDE for changes to take affect.");
		}catch(Exception ex)
		{
			logger.error("An error occurred while fetching latest ide", ex);
		}
	}

	@IdeEventHandler
	public void checkForUpdate(IdeStartedEvent event)
	{
		IdeUtils.execute(() -> checkForUpdateSync(), 10);
	}
}
