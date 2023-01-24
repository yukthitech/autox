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

public class TXmlFileParser
{
	@Test
	public void testNodeLocations()
	{
		String content = "<root><parent>"
				+ "\n<child>"
				+ "\n </child>"
				+ "\n</parent></root>";
		XmlFile file = XmlFileParser.parse(content, null);
		
		Element root = file.getRootElement();
		Assert.assertEquals(root.getStartLocation().getStartLineNumber(), 1);
		Assert.assertEquals(root.getStartLocation().getStartColumnNumber(), 1);
		Assert.assertEquals(root.getStartLocation().getStartOffset(), 0);
		Assert.assertEquals(root.getStartLocation().getEndLineNumber(), 1);
		Assert.assertEquals(root.getStartLocation().getEndColumnNumber(), 6);
		Assert.assertEquals(root.getStartLocation().getEndOffset(), 5);

		Assert.assertEquals(root.getEndLocation().getStartLineNumber(), 4);
		Assert.assertEquals(root.getEndLocation().getStartColumnNumber(), 10);
		Assert.assertEquals(root.getEndLocation().getStartOffset(), 42);
		Assert.assertEquals(root.getEndLocation().getEndLineNumber(), 4);
		Assert.assertEquals(root.getEndLocation().getEndColumnNumber(), 16);
		Assert.assertEquals(root.getEndLocation().getEndOffset(), 48);

		Element parent = root.getElementWithName("parent");
		Element child = parent.getElementWithName("child");

		Assert.assertEquals(child.getStartLocation().getStartLineNumber(), 2);
		Assert.assertEquals(child.getStartLocation().getStartColumnNumber(), 1);
		Assert.assertEquals(child.getStartLocation().getStartOffset(), 15);
		Assert.assertEquals(child.getStartLocation().getEndLineNumber(), 2);
		Assert.assertEquals(child.getStartLocation().getEndColumnNumber(), 7);
		Assert.assertEquals(child.getStartLocation().getEndOffset(), 21);

		Assert.assertEquals(child.getEndLocation().getStartLineNumber(), 3);
		Assert.assertEquals(child.getEndLocation().getStartColumnNumber(), 2);
		Assert.assertEquals(child.getEndLocation().getStartOffset(), 24);
		Assert.assertEquals(child.getEndLocation().getEndLineNumber(), 3);
		Assert.assertEquals(child.getEndLocation().getEndColumnNumber(), 9);
		Assert.assertEquals(child.getEndLocation().getEndOffset(), 31);
	}
}
