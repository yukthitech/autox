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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.persistence.UnsupportedOperationException;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Unit tests for push: prefix expression.
 */
public class TPushPrefixExpression
{
	private final DefaultPrefixExpressions parsers = new DefaultPrefixExpressions();

	private PrefixExpressionContext contextOn(Map<String, Object> bean)
	{
		return new PrefixExpressionContext(null, bean);
	}

	@Test
	public void testPushMissingCreatesList() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		PrefixExpressionContext ctx = contextOn(bean);

		parsers.pushParser(ctx, "items").setValue("a");
		parsers.pushParser(ctx, "items").setValue("b");

		Object value = bean.get("items");
		Assert.assertTrue(value instanceof List);
		Assert.assertEquals(value, Arrays.asList("a", "b"));
	}

	@Test
	public void testPushMissingUniqueCreatesSet() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		PrefixExpressionContext ctx = contextOn(bean);
		ctx.addParameter("unique", "true");

		parsers.pushParser(ctx, "tags").setValue("red");
		parsers.pushParser(ctx, "tags").setValue("blue");

		Object value = bean.get("tags");
		Assert.assertTrue(value instanceof Set);
		Assert.assertEquals(value, new HashSet<>(Arrays.asList("red", "blue")));
	}

	@Test
	public void testPushMissingMapCreatesMap() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		PrefixExpressionContext ctx = contextOn(bean);

		Map<String, Object> input = new HashMap<>();
		input.put("a", 1);
		parsers.pushParser(ctx, "config").setValue(input);

		Object value = bean.get("config");
		Assert.assertTrue(value instanceof Map);
		Assert.assertEquals(((Map<?, ?>) value).get("a"), 1);
		Assert.assertNotSame(value, input);
	}

	@Test
	public void testPushOntoExistingListWithScalarAndCollection() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		bean.put("items", Arrays.asList("a"));
		PrefixExpressionContext ctx = contextOn(bean);

		parsers.pushParser(ctx, "items").setValue("b");
		parsers.pushParser(ctx, "items").setValue(Arrays.asList("c", "d"));

		Object value = bean.get("items");
		Assert.assertTrue(value instanceof List);
		Assert.assertEquals(value, Arrays.asList("a", "b", "c", "d"));
	}

	@Test
	public void testPushOntoExistingSet() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		bean.put("tags", new HashSet<>(Arrays.asList("red")));
		PrefixExpressionContext ctx = contextOn(bean);

		parsers.pushParser(ctx, "tags").setValue(Arrays.asList("blue", "green"));

		Object value = bean.get("tags");
		Assert.assertTrue(value instanceof Set);
		Assert.assertEquals(value, new HashSet<>(Arrays.asList("red", "blue", "green")));
	}

	@Test
	public void testPushOntoExistingMapMergeAndOverwrite() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		Map<String, Object> existing = new HashMap<>();
		existing.put("a", 1);
		existing.put("b", 2);
		bean.put("config", existing);

		PrefixExpressionContext ctx = contextOn(bean);
		Map<String, Object> next = new HashMap<>();
		next.put("b", 3);
		next.put("c", 4);
		parsers.pushParser(ctx, "config").setValue(next);

		@SuppressWarnings("unchecked")
		Map<String, Object> value = (Map<String, Object>) bean.get("config");
		Assert.assertEquals(value.get("a"), 1);
		Assert.assertEquals(value.get("b"), 3);
		Assert.assertEquals(value.get("c"), 4);
		Assert.assertNotSame(value, existing);
	}

	@Test
	public void testPushOntoMapWithNonMapFails() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		bean.put("config", new HashMap<>());
		PrefixExpressionContext ctx = contextOn(bean);

		try
		{
			parsers.pushParser(ctx, "config").setValue("not-a-map");
			Assert.fail("Expected InvalidArgumentException");
		}catch(InvalidArgumentException ex)
		{
			Assert.assertTrue(ex.getMessage().toLowerCase().contains("map"));
		}
	}

	@Test
	public void testPushOntoScalarFails() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		bean.put("items", "scalar");
		PrefixExpressionContext ctx = contextOn(bean);

		try
		{
			parsers.pushParser(ctx, "items").setValue("x");
			Assert.fail("Expected InvalidArgumentException");
		}catch(InvalidArgumentException ex)
		{
			Assert.assertTrue(ex.getMessage().toLowerCase().contains("list"));
		}
	}

	@Test
	public void testPushReadUnsupported() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		PrefixExpressionContext ctx = contextOn(bean);

		try
		{
			parsers.pushParser(ctx, "items").getValue();
			Assert.fail("Expected UnsupportedOperationException");
		}catch(UnsupportedOperationException ex)
		{
			Assert.assertTrue(ex.getMessage().toLowerCase().contains("read"));
		}
	}

	@Test
	public void testPushMissingCollectionInputCopiesElements() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		PrefixExpressionContext ctx = contextOn(bean);

		parsers.pushParser(ctx, "items").setValue(Arrays.asList("a", "b"));

		Object value = bean.get("items");
		Assert.assertTrue(value instanceof List);
		Assert.assertEquals(value, Arrays.asList("a", "b"));
	}

	@Test
	public void testPushDoesNotMutateExistingCollection() throws Exception
	{
		Map<String, Object> bean = new HashMap<>();
		ArrayList<String> existing = new ArrayList<>(Arrays.asList("a"));
		bean.put("items", existing);

		PrefixExpressionContext ctx = contextOn(bean);
		parsers.pushParser(ctx, "items").setValue("b");

		Assert.assertEquals(existing, Arrays.asList("a"));
		Assert.assertEquals(bean.get("items"), Arrays.asList("a", "b"));
		Assert.assertNotSame(bean.get("items"), existing);
	}
}
