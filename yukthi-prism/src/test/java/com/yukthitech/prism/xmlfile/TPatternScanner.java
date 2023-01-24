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
package com.yukthitech.prism.xmlfile;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TPatternScanner
{
	@Test
	public void testScanner()
	{
		String src = "one  1   someva-123\n"
				+ "two     2    someva-132";
		
		PatternScanner patternScanner = new PatternScanner(src);
		
		Assert.assertEquals(patternScanner.next("\\w+"), "one");
		patternScanner.skip("\\s*");
		Assert.assertEquals(patternScanner.next("\\d+"), "1");
		patternScanner.skip("\\s*");
		
		Assert.assertEquals(patternScanner.next("(\\w+)\\-(\\d+)"), "someva-123");
		PatternScanner.ScannerMatch matcher = patternScanner.getLastMatch();
		
		Assert.assertEquals(matcher.group(1), "someva");
		Assert.assertEquals(matcher.group(2), "123");
		
		patternScanner.skip("\\s*");
		Assert.assertEquals(patternScanner.next("\\w+"), "two");
	}
	
}
