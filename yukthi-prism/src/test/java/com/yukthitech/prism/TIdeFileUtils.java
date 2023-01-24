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

import org.testng.Assert;
import org.testng.annotations.Test;

public class TIdeFileUtils
{
	@Test
	public void testRelativePath()
	{
		File parentFile = new File("c:\\test");

		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\test\\abc\\def.png")), "abc\\def.png");
		
		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\test123\\abc\\def.png")), null);
		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\xyz\\abc\\def.png")), null);
		
		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\test\\")), "");
		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\test")), "");
		
		Assert.assertEquals(IdeFileUtils.getRelativePath(new File("c:\\test\\t.png"), new File("c:\\test\\t.png")), "");
	}
}
