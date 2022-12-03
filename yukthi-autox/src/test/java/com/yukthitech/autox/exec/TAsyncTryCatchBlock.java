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
package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TAsyncTryCatchBlock
{
	private AsyncTryCatchBlock defaultBlock(List<String> flow, String prefix)
	{
		return AsyncTryCatchBlock.doTry("test", callback -> 
		{
			flow.add(prefix + "try");
		}).onComplete(callback -> 
		{
			flow.add(prefix + "complete");
		}).onError((callback, ex) -> 
		{
			flow.add(prefix + "error");
		}).onFinally(callback -> 
		{
			flow.add(prefix + "finally");
		});
	}
	
	private AsyncTryCatchBlock childBlock(List<String> flow, String prefix, AsyncTryCatchBlock parent)
	{
		return parent.newChild("test", callback -> 
		{
			flow.add(prefix + "try");
		}).onComplete(callback -> 
		{
			flow.add(prefix + "complete");
		}).onError((callback, ex) -> 
		{
			flow.add(prefix + "error");
		}).onFinally(callback -> 
		{
			flow.add(prefix + "finally");
		});
	}

	@Test
	public void basicTest_success()
	{
		List<String> flow = new ArrayList<>();
		
		defaultBlock(flow, "").execute();
		
		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList("try", "complete", "finally"));
	}

	@Test
	public void basicTest_error()
	{
		List<String> flow = new ArrayList<>();
		
		AsyncTryCatchBlock block = defaultBlock(flow, "").onTry(callback -> 
		{
			flow.add("try");
			throw new RuntimeException();
		}).execute();
		
		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList("try", "error", "finally"));
		Assert.assertNull(block.getUnhandledException());
	}

	@Test
	public void basicTest_compError()
	{
		List<String> flow = new ArrayList<>();
		
		AsyncTryCatchBlock block = defaultBlock(flow, "").onComplete(callback -> 
		{
			flow.add("complete");
			throw new RuntimeException();
		}).execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList("try", "complete", "error", "finally"));
		Assert.assertNull(block.getUnhandledException());
	}

	@Test
	public void basicTest_catchError()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock block = defaultBlock(flow, "").onTry(callback -> 
		{
			flow.add("try");
			throw exToThrow;
		}).onError((callback, ex) -> 
		{
			flow.add("error");
			throw ex;
		}).execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList("try", "error", "finally"));
		Assert.assertSame(exToThrow, block.getUnhandledException());
	}

	@Test
	public void basicTest_finallyError()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock block = defaultBlock(flow, "")
			.onFinally(callback -> 
			{
				flow.add("finally");
				throw exToThrow;
			}).execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList("try", "complete", "finally"));
		Assert.assertSame(block.getUnhandledException(), exToThrow);
	}

	@Test
	public void parentChild_basic()
	{
		List<String> flow = new ArrayList<>();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-");
		childBlock(flow, "child-", parent);
		
		parent.execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
					"child-try", "child-complete", "child-finally", 
				"parent-complete", "parent-finally"
				));
	}

	@Test
	public void parentChild_parentTryErr()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-")
				.onTry(callback -> 
				{
					flow.add("parent-try");
					throw exToThrow;
				});
		
		childBlock(flow, "child-", parent);
		
		parent.execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
				"parent-error", "parent-finally"));
	}

	@Test
	public void parentChild_childTryErr()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-");
		
		AsyncTryCatchBlock child = childBlock(flow, "child-", parent)
			.onTry(callback -> 
			{
				flow.add("child-try");
				throw exToThrow;
			});
		
		parent.execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
					"child-try", "child-error", "child-finally", 
				"parent-complete", "parent-finally"
				));
		
		Assert.assertNull(parent.getUnhandledException());
		Assert.assertNull(child.getUnhandledException());
	}

	@Test
	public void parentChild_childCatchErr()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-");
		
		AsyncTryCatchBlock child = childBlock(flow, "child-", parent)
			.onTry(callback -> 
			{
				flow.add("child-try");
				throw exToThrow;
			}).onError((callback, ex) -> 
			{
				flow.add("child-error");
				throw ex;
			});
		
		parent.execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
					"child-try", "child-error", "child-finally", 
				"parent-error", "parent-finally"
				));
		
		Assert.assertNull(parent.getUnhandledException());
		Assert.assertNull(child.getUnhandledException());
	}

	@Test
	public void parentChild_childFinallyErr()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-");
		
		AsyncTryCatchBlock child = childBlock(flow, "child-", parent)
			.onFinally(callback -> 
			{
				flow.add("child-finally");
				throw exToThrow;
			});
		
		parent.execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
					"child-try", "child-complete", "child-finally", 
				"parent-error", "parent-finally"
				));
		
		Assert.assertNull(parent.getUnhandledException());
		Assert.assertNull(child.getUnhandledException());
	}

	@Test
	public void parentChild_unhandledErr()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-")
			.onError((callback, ex) -> 
			{
				flow.add("parent-error");
				throw ex;
			});
	
		AsyncTryCatchBlock child = childBlock(flow, "child-", parent)
			.onFinally(callback -> 
			{
				flow.add("child-finally");
				throw exToThrow;
			});
		
		parent.execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
					"child-try", "child-complete", "child-finally", 
				"parent-error", "parent-finally"
				));
		
		Assert.assertSame(parent.getUnhandledException(), exToThrow);
		Assert.assertNull(child.getUnhandledException());
	}

	@Test
	public void threeLevel_unhandledErr()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-")
			.onError((callback, ex) -> 
			{
				flow.add("parent-error");
				throw ex;
			});
	
		AsyncTryCatchBlock child = childBlock(flow, "child-", parent)
			.onError((callback, ex) -> 
			{
				flow.add("child-error");
				throw ex;
			});
		
		AsyncTryCatchBlock subchild = childBlock(flow, "schild-", child)
				.onFinally(callback -> 
				{
					flow.add("schild-finally");
					throw exToThrow;
				});

		parent.execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
					"child-try",
						"schild-try", "schild-complete", "schild-finally",
					"child-error", "child-finally", 
				"parent-error", "parent-finally"
				));
		
		Assert.assertSame(parent.getUnhandledException(), exToThrow);
		Assert.assertNull(child.getUnhandledException());
		Assert.assertNull(subchild.getUnhandledException());
	}

	@Test
	public void asyncChild_basic()
	{
		List<String> flow = new ArrayList<>();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-");
		AsyncTryCatchBlock child = childBlock(flow, "child-", parent).setAutoComplete(false);
		
		parent.execute();

		System.out.println("Pre-child flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", "child-try"
				));

		child.triggerComplete();
		
		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
					"child-try", "child-complete", "child-finally", 
				"parent-complete", "parent-finally"
				));
	}

	@Test
	public void asyncChild_childFinallyErr()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-");
		
		AsyncTryCatchBlock child = childBlock(flow, "child-", parent)
			.onFinally(callback -> 
			{
				flow.add("child-finally");
				throw exToThrow;
			}).setAutoComplete(false);
		
		parent.execute();

		System.out.println("Pre-child flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", "child-try"
				));

		child.triggerComplete();
		
		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
					"child-try", "child-complete", "child-finally", 
				"parent-error", "parent-finally"
				));
		
		Assert.assertNull(parent.getUnhandledException());
		Assert.assertNull(child.getUnhandledException());
	}

	/**
	 * Though async, if the basic execution throws the error,
	 * the parent error handler will be invoked with error details. 
	 */
	@Test
	public void asyncChild_childCatchErr()
	{
		List<String> flow = new ArrayList<>();
		RuntimeException exToThrow = new RuntimeException();
		
		AsyncTryCatchBlock parent = defaultBlock(flow, "parent-");
		
		AsyncTryCatchBlock child = childBlock(flow, "child-", parent)
			.onTry(callback -> 
			{
				flow.add("child-try");
				throw exToThrow;
			}).onError((callback, ex) -> 
			{
				flow.add("child-error");
				throw ex;
			}).setAutoComplete(false);
		
		parent.execute();

		System.out.println("Actual flow: " + flow);
		Assert.assertEquals(flow, Arrays.asList(
				"parent-try", 
					"child-try", "child-error", "child-finally", 
				"parent-error", "parent-finally"
				));
		
		Assert.assertNull(parent.getUnhandledException());
		Assert.assertNull(child.getUnhandledException());
	}
}
