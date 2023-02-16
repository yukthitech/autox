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
package com.yukthitech.autox.common;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Common free marker methods related to files.
 * @author akiran
 */
public class FileFreeMarkerMethods
{
	/**
	 * Converts input file path (Can be relative, partial path) to full canonical path.
	 * @param path path to convert.
	 * @return converted path.
	 */
	@FreeMarkerMethod(
			description = "Converts input file path (Can be relative, partial path) to full canonical path.",
			returnDescription = "Canonical path of the specified path"
			)
	public static String fullPath(
			@FmParam(name = "path", description = "Path to be converted") String path)
	{
		try
		{
			return new File(path).getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while fetching full path of path: {}", path, ex);
		}
	}

	@FreeMarkerMethod(
			description = "Checks if specified file path exists or not.",
			returnDescription = "Path to be checked"
			)
	public static boolean fileExists(
			@FmParam(name = "path", description = "Path to be checked") String path)
	{
		return new File(path).exists();
	}

	@FreeMarkerMethod(
			description = "Checks if specified file path is a file or not.",
			returnDescription = "Path to be checked"
			)
	public static boolean isFile(
			@FmParam(name = "path", description = "Path to be checked") String path)
	{
		return new File(path).isFile();
	}

	@FreeMarkerMethod(
			description = "Checks if specified file path is a directory or not.",
			returnDescription = "Path to be checked"
			)
	public static boolean isDirectory(
			@FmParam(name = "path", description = "Path to be checked") String path)
	{
		return new File(path).isDirectory();
	}

	@FreeMarkerMethod(
			description = "Extracts file name from url.",
			returnDescription = "file name from url"
			)
	public static String fileNameFromUrl(
			@FmParam(name = "url", description = "URL from which file name to be extracted") String url) throws IOException, URISyntaxException
	{
		return Paths.get(new URI(url).getPath()).getFileName().toString();
	}

	@FreeMarkerMethod(
			description = "Extracts suffix from file name.",
			returnDescription = "Suffix of file name."
			)
	public static String suffixOfFile(
			@FmParam(name = "fileName", description = "File name from which suffix has to be extracted") String fileName)
	{
		return FilenameUtils.getBaseName(fileName);
	}

	@FreeMarkerMethod(
			description = "Extracts extension from file name.",
			returnDescription = "Extension of file name (without dot(.))"
			)
	public static String extensionOfFile(
			@FmParam(name = "fileName", description = "File name from which extension has to be extracted") String fileName)
	{
		return FilenameUtils.getExtension(fileName);
	}
}
