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
package com.yukthitech.autox.ide;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.ZipUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class IdeUpgradeChecker
{
	private static Logger logger = LogManager.getLogger(IdeUpgradeChecker.class);
	
	private static long timeoutInSec = 180;
	
	private static Properties loadAppProp() throws Exception
	{
		Properties prop = new Properties();
		InputStream is = IdeUpgradeChecker.class.getResourceAsStream("/application.properties");
		prop.load(is);
		
		is.close();
		return prop;
	}
	
	private static String getLatestVersion(Properties prop) throws Exception
	{
		String vesionUrl = prop.getProperty("autox.ide.version.url");
		File verFile = downloadFile(vesionUrl, "autox-ide-version", ".txt");
		
		if(verFile == null)
		{
			return null;
		}
		
		String latestVersion = FileUtils.readFileToString(verFile, Charset.defaultCharset());
		Pattern pattern = Pattern.compile("[\\w\\.\\-]+\\-\\d+");
		latestVersion = latestVersion.trim();
		
		if(!pattern.matcher(latestVersion).matches())
		{
			return null;
		}
		
		return latestVersion;
	}
	
	private static File downloadFile(String urlStr, String prefix, String extension) throws Exception
	{
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
	
	private static String getLocalVersion() throws Exception
	{
		InputStream is = IdeUpgradeChecker.class.getResourceAsStream("/version/version.txt");
		String version = IOUtils.toString(is, Charset.defaultCharset());
		is.close();
		
		return version;
	}
	
	private static File downloadLatestIde(Properties prop) throws Exception
	{
		String downloadUrl = prop.getProperty("autox.ide.download.url");
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
				
				if(entry.contains("yukthi-autox-ide") && entry.endsWith(".jar"))
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
	
	public static void unzipLib(File zipFile, File newLibFolder)
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

	public static void main(String[] args) throws Exception
	{
		if(args.length > 0)
		{
			timeoutInSec = Long.parseLong(args[0]);
		}
		
		Properties prop = loadAppProp();
		logger.debug("Loaded app properties...");
		
		String latestVersion = getLatestVersion(prop);
		logger.debug("Got latest version as: {}", latestVersion);
		
		if(latestVersion == null)
		{
			logger.error("Failed to determine latest version. So skipping the upgrade");
			System.exit(0);
		}
		
		String localVersion = getLocalVersion();
		logger.debug("Got local version as: {}", localVersion);
		
		if(latestVersion.equals(localVersion))
		{
			logger.debug("Found the ide is upto date.");
			System.exit(0);
		}
		
		logger.debug("Found new updates available. Downloading latest ide zip file (this may take few mins)...");
		File latestIde = downloadLatestIde(prop);
		
		if(latestIde == null)
		{
			logger.error("An error occurred while downloading latest zip file. Hence skipping the upgrade..");
			System.exit(0);
		}
		
		logger.debug("Downloaded latest ide as file: {}", latestIde.getPath());
		
		unzipLib(latestIde, new File(".." + File.separator + "lib-new"));
		logger.debug("New ide lib folder is created successfully...");
		
		latestIde.delete();
	}
}
