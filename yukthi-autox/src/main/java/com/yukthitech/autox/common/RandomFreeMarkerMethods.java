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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.RandomStringUtils;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

public class RandomFreeMarkerMethods
{
	/**
	 * Used to generate random numbers.
	 */
	private static Random random = new Random(System.currentTimeMillis());
	
	/**
	 * Used to generate unique strings.
	 */
	private static AtomicInteger UQ_NUMBER_TRACKER = new AtomicInteger(1);
	
	private static final char ALPHABETS[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 
			'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	private static final char ALPHABET_N_NUMBERS[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 
			'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	@FreeMarkerMethod(
			description = "Generates random int.",
			returnDescription = "Random number"
			)
	public static int random()
	{
		return random.nextInt();
	}

	@FreeMarkerMethod(
			description = "Generates random int in given range.",
			returnDescription = "Random number"
			)
	public static int randomInt(
			@FmParam(name = "min", description = "Min value of the expected range") int min, 
			@FmParam(name = "max", description = "Max value of the expected range") int max)
	{
		if(max <= min)
		{
			throw new InvalidArgumentException("Max value should be greater than min value");
		}
		
		int range = max - min;
		return min + random.nextInt(range);
	}

	@FreeMarkerMethod(
			description = "Generates random double in given range.",
			returnDescription = "Random number"
			)
	public static double randomDouble(
			@FmParam(name = "min", description = "Min value of the expected range") double min, 
			@FmParam(name = "max", description = "Max value of the expected range") double max)
	{
		if(max <= min)
		{
			throw new InvalidArgumentException("Max value should be greater than min value");
		}
		
		double range = max - min;
		return min + (random.nextDouble() * range);
	}

	@FreeMarkerMethod(
			description = "Generates random float in given range.",
			returnDescription = "Random number"
			)
	public static float randomFloat(
			@FmParam(name = "min", description = "Min value of the expected range") float min, 
			@FmParam(name = "max", description = "Max value of the expected range") float max)
	{
		if(max <= min)
		{
			throw new InvalidArgumentException("Max value should be greater than min value");
		}
		
		float range = max - min;
		return min + (random.nextFloat() * range);
	}

	@FreeMarkerMethod(
			description = "Generates random string with specified prefix (based on timestamp).",
			returnDescription = "Random string"
			)
	public static String randomString(
			@FmParam(name = "prefix", description = "Prefix that will added to generated random string") String prefix)
	{
		GregorianCalendar today = new GregorianCalendar();
		int dayOfYear = today.get(Calendar.DAY_OF_YEAR);
		int minOfDay = today.get(Calendar.HOUR_OF_DAY) * 60 + today.get(Calendar.MINUTE);
		int minOfYear = dayOfYear * (24 * 60) + minOfDay;

		prefix = prefix + Long.toHexString(minOfYear + random.nextInt(10000)) + Long.toHexString(UQ_NUMBER_TRACKER.incrementAndGet());
		prefix = prefix.toLowerCase();

		return prefix;
	}

	@FreeMarkerMethod(
			description = "Generates random alpha string with specified prefix.",
			returnDescription = "Random string"
			)
	public static String randomAlpha(
			@FmParam(name = "prefix", description = "Prefix that will added to generated random string") String prefix,
			@FmParam(name = "length", description = "Expected length of resulting random string") int length)
	{
		int randomLen = length - prefix.length() - 1;
		
		if(randomLen < 2)
		{
			throw new InvalidArgumentException("Specified length {} should be greater than specified prefix ({}) length at least by 2", length, prefix); 
		}
		
		String randomStr = RandomStringUtils.randomAlphabetic(randomLen);
		int nextRandomNum = UQ_NUMBER_TRACKER.incrementAndGet() % ALPHABETS.length;
		
		//add incremental alphabet, to ensure more uniquness
		randomStr = prefix + randomStr + ALPHABETS[nextRandomNum];

		return randomStr;
	}
	
	@FreeMarkerMethod(
			description = "Generates random alpha-numeric string with specified prefix.",
			returnDescription = "Random string"
			)
	public static String randomAlphaNumeric(
			@FmParam(name = "prefix", description = "Prefix that will added to generated random string") String prefix,
			@FmParam(name = "length", description = "Expected length of resulting random string") int length)
	{
		int randomLen = length - prefix.length() - 1;
		
		if(randomLen < 2)
		{
			throw new InvalidArgumentException("Specified length {} should be greater than specified prefix ({}) length at least by 2", length, prefix); 
		}
		
		String randomStr = RandomStringUtils.randomAlphanumeric(randomLen);
		int nextRandomNum = UQ_NUMBER_TRACKER.incrementAndGet() % ALPHABET_N_NUMBERS.length;
		
		//add incremental alphabet, to ensure more uniquness
		randomStr = prefix + randomStr + ALPHABET_N_NUMBERS[nextRandomNum];

		return randomStr;
	}
}
