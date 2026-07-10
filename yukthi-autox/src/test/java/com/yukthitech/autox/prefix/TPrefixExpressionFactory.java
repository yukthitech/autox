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
package com.yukthitech.autox.prefix;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TPrefixExpressionFactory
{
	@Test
	public void testParseExprList()
	{
		List<ExpressionToken> tokens = PrefixExpressionFactory.parseExpressionTokens("  attr:  test124   |   prop: d.test124  |xpath: /df/df");
		System.out.println(tokens);
	}
	
	@Test
	public void testStandardProtocolValuesAreNotExpressions()
	{
		Assert.assertTrue(PrefixExpressionFactory.isStandardProtocolValue("http://example.com"));
		Assert.assertTrue(PrefixExpressionFactory.isStandardProtocolValue("https://example.com/path"));
		Assert.assertTrue(PrefixExpressionFactory.isStandardProtocolValue("ftp://files.example.com"));
		Assert.assertTrue(PrefixExpressionFactory.isStandardProtocolValue("file:///C:/temp/data.txt"));
		Assert.assertTrue(PrefixExpressionFactory.isStandardProtocolValue("jdbc:mysql://localhost:3306/db"));
		Assert.assertTrue(PrefixExpressionFactory.isStandardProtocolValue("mailto:user@example.com"));
		
		Assert.assertFalse(PrefixExpressionFactory.isStandardProtocolValue("prop: user.name"));
		Assert.assertFalse(PrefixExpressionFactory.isStandardProtocolValue("file: ./src/test/data.json"));
		Assert.assertFalse(PrefixExpressionFactory.isStandardProtocolValue("OPEN"));
		
		Assert.assertFalse(PrefixExpressionFactory.isExpression("http://example.com"));
		Assert.assertFalse(PrefixExpressionFactory.isExpression("https://example.com/page"));
		Assert.assertTrue(PrefixExpressionFactory.isExpression("prop: user.name"));
	}
}
