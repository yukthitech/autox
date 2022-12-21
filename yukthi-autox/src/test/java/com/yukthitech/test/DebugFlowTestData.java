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
package com.yukthitech.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.yukthitech.autox.debug.common.DebugOp;

/**
 * Test data for debug flow test cases.
 * @author akranthikiran
 */
public class DebugFlowTestData
{
	private static Pattern STACK_TRACE_LINE = Pattern.compile("(\\d+)\\s*\\=\\>\\s*(.*)");
	
	public static class Case
	{
		private String name;
		
		private DebugOp op;
		
		private String testCase;
		
		private List<Integer> debugPoints;
		private List<Integer> pausePoints;
		private Map<Integer, String> stackTraces;
		
		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}
		
		public DebugOp getOp()
		{
			return op;
		}

		public void setOp(DebugOp op)
		{
			this.op = op;
		}
		
		public String getTestCase()
		{
			return testCase;
		}

		public void setTestCase(String testCase)
		{
			this.testCase = testCase;
		}

		public List<Integer> getDebugPoints()
		{
			return debugPoints;
		}
		
		public void setDebugPoints(String debugPoints)
		{
			debugPoints = debugPoints.trim().replaceAll("\\s+", "");
			
			this.debugPoints = Arrays.asList(debugPoints.split("\\,"))
					.stream()
					.map(str -> Integer.parseInt(str))
					.collect(Collectors.toList());
		}
		
		public List<Integer> getPausePoints()
		{
			return pausePoints;
		}
		
		public void setPausePoints(String pausePoints)
		{
			pausePoints = pausePoints.trim().replaceAll("\\s+", "");
			
			this.pausePoints = Arrays.asList(pausePoints.split("\\,"))
					.stream()
					.map(str -> Integer.parseInt(str))
					.collect(Collectors.toList());
		}
		
		public Map<Integer, String> getStackTraces()
		{
			return stackTraces;
		}
		
		public void setStackTraces(String stackTraces)
		{
			Map<Integer, String> res = new HashMap<>();
			
			for(String line : stackTraces.split("\\n"))
			{
				line = line.trim();
				
				if(line.length() == 0)
				{
					continue;
				}
				
				Matcher matcher = STACK_TRACE_LINE.matcher(line);
				matcher.matches();
				
				res.put(Integer.parseInt(matcher.group(1)), matcher.group(2));
			}
			
			this.stackTraces = res;
		}
	}
	
	private List<Case> cases = new ArrayList<>();
	
	public void addCase(Case caseObj)
	{
		this.cases.add(caseObj);
	}
	
	public List<Case> getCases()
	{
		return cases;
	}
}
