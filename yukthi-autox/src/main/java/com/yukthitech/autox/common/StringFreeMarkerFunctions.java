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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * String related free marker functions.
 * @author akiran
 */
public class StringFreeMarkerFunctions
{
	@FreeMarkerMethod(
			description = "Finds the first index of specified substring in specified string.",
			returnDescription = "index of subbstring. If not found -1."
			)
	public static int indexOf(
			@FmParam(name = "string", description = "String in which substring needs to be searched") String string,
			@FmParam(name = "substr", description = "Substring that needs to be searched") String substr)
	{
		return string.indexOf(substr);
	}

	@FreeMarkerMethod(
			description = "Finds the last index of specified substring in specified string.",
			returnDescription = "index of subbstring. If not found -1."
			)
	public static int lastIndexOf(
			@FmParam(name = "string", description = "String in which substring needs to be searched") String string,
			@FmParam(name = "substr", description = "Substring that needs to be searched") String substr)
	{
		return string.lastIndexOf(substr);
	}

	@FreeMarkerMethod(
			description = "Converts the specified string to lower case.",
			returnDescription = "Lower cased substring."
			)
	public static String lower(
			@FmParam(name = "string", description = "String to be converted") String string)
	{
		return string.toLowerCase();
	}

	@FreeMarkerMethod(
			description = "Converts the specified string to upper case.",
			returnDescription = "Lower cased substring."
			)
	public static String upper(
			@FmParam(name = "string", description = "String to be converted") String string)
	{
		return string.toUpperCase();
	}

	@FreeMarkerMethod(
			description = "Substring of speicifed string with specified range.",
			returnDescription = "Result substring."
			)
	public static String substr(
			@FmParam(name = "string", description = "String from which substring needs to be extracted") String string,
			@FmParam(name = "start", description = "Start from which substring") int start,
			@FmParam(name = "string", description = "End index of substring. If negative value is specified, this will be not be used.") int end)
	{
		if(end >= 0)
		{
			return string.substring(start, end);
		}
		
		return string.substring(start);
	}
	
	@FreeMarkerMethod(
			description = "Converts specified int value to string using specified radix.",
			returnDescription = "Result substring."
			)
	public static String intToStr(
			@FmParam(name = "value", description = "Int value to be converted") int value,
			@FmParam(name = "radix", description = "Radix to be used for conversion") int radix)
	{
		return Integer.toString(value, radix);
	}
	
	@FreeMarkerMethod(
			description = "Converts specified string to int using specified radix.",
			returnDescription = "Result int value."
			)
	public static int strToInt(
			@FmParam(name = "value", description = "String value to be converted") String value,
			@FmParam(name = "radix", description = "Radix to be used for conversion") int radix)
	{
		return Integer.parseInt(value, radix);
	}

	@FreeMarkerMethod(
			description = "Parses string to date using specified format.",
			returnDescription = "Parsed date object"
			)
	public static Date parseDate(
			@FmParam(name = "string", description = "String to parse") String string,
			@FmParam(name = "format", description = "Format to be used for parsing") String format
			)
	{
		SimpleDateFormat simpleDateFormat = null;
		
		try
		{
			simpleDateFormat = new SimpleDateFormat(format);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid date format specified: {}", format, ex);
		}
		
		try
		{
			return simpleDateFormat.parse(string);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Specified date {} is not in specified format: {}", string, format, ex);
		}
	}

	@FreeMarkerMethod(
			description = "Splits the given string into list of strings using specified delimiter.",
			returnDescription = "List of string resulted from spliting."
			)
	public static List<String> split(
			@FmParam(name = "string", description = "String to parse") String string,
			@FmParam(name = "delimiter", description = "Delimiter to be used for spliting") String delimiter
			)
	{
		if(string == null || delimiter == null)
		{
			return null;
		}
		
		String res[] = string.split(delimiter);
		return new ArrayList<>(Arrays.asList(res));
	}
}
