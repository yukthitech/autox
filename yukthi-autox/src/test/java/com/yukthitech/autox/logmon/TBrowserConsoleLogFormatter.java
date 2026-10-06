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
package com.yukthitech.autox.logmon;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TBrowserConsoleLogFormatter
{
	@Test
	public void testDecodeVueWarnStyleJsonArguments()
	{
		String raw = "http://localhost:8091/lib/vue-3.4.31/vue.esm-browser.js 1626:12 "
				+ "\"[Vue warn]: Unhandled error during execution of native event handler\" \"\\n\" "
				+ "\" at \\u003CYkModelForm\" \"id=\\\"lov-demo-form\\\"\" \"ref=\\\"lovForm\\\"\" "
				+ "\"model-name=\\\"LovDemoModel\\\"\" \" ...\" \">\" \"\\n\" \" at \\u003CApp>\"";

		String formatted = BrowserConsoleLogFormatter.format(raw);

		Assert.assertTrue(formatted.contains("[Vue warn]: Unhandled error during execution of native event handler"), formatted);
		Assert.assertTrue(formatted.contains("at <YkModelForm"), formatted);
		Assert.assertTrue(formatted.contains("id=\"lov-demo-form\""), formatted);
		Assert.assertTrue(formatted.contains("at <App>"), formatted);
		Assert.assertFalse(formatted.contains("\\u003C"), formatted);
		Assert.assertFalse(formatted.contains("\"\\n\""), formatted);
		Assert.assertTrue(formatted.contains("\n"), formatted);
	}

	@Test
	public void testPlainExceptionMessageRemainsReadable()
	{
		String raw = "http://localhost:8091/lib/vue-3.4.31/vue.esm-browser.js 1842:6 "
				+ "Uncaught TypeError: this.$refs.lovForm.validate is not a function";

		String formatted = BrowserConsoleLogFormatter.format(raw);

		Assert.assertEquals(formatted, "Uncaught TypeError: this.$refs.lovForm.validate is not a function");
	}

	@Test
	public void testParseJsonStringSequence()
	{
		List<String> args = BrowserConsoleLogFormatter.parseJsonStringSequence(
				"\"hello\" \"\\n\" \"at \\u003CApp>\"");

		Assert.assertEquals(args.size(), 3);
		Assert.assertEquals(args.get(0), "hello");
		Assert.assertEquals(args.get(1), "\n");
		Assert.assertEquals(args.get(2), "at <App>");
	}
}
