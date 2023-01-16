package com.yukthitech.autox.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.Log4jExecutionLogger;
import com.yukthitech.utils.CommonUtils;

public class TDeepEqualsProcessor
{
	private AutomationContext automationContext;
	
	@BeforeClass
	private void setup()
	{
		automationContext = mock(AutomationContext.class);
		when(automationContext.getExecutionLogger()).thenReturn(new Log4jExecutionLogger());
	}
	
	@Test
	public void testWithNulls()
	{
		//test when both are null
		String diffPath = DeepEqualsProcessor.newProcessor(true, automationContext)
			.deepCompare(null, null);
		
		Assert.assertNull(diffPath);
		
		//test when one is null
		diffPath = DeepEqualsProcessor.newProcessor(true, automationContext)
				.deepCompare(null, CommonUtils.toMap("key1", "val1"));
		
		Assert.assertEquals(diffPath, "$");

		//test when one is null
		diffPath = DeepEqualsProcessor.newProcessor(true, automationContext)
				.deepCompare(CommonUtils.toMap("key1", "val1"), null);
		
		Assert.assertEquals(diffPath, "$");
	}
	
	@Test
	public void testSimpleValues()
	{
		Assert.assertNull(
			DeepEqualsProcessor.newProcessor(true, automationContext)
				.deepCompare("test", "test"));

		Assert.assertEquals(
			DeepEqualsProcessor.newProcessor(true, automationContext)
				.deepCompare("test1", "test2"), 
			"$");

		Assert.assertEquals(
			DeepEqualsProcessor.newProcessor(true, automationContext)
				.deepCompare(5, 10), 
			"$");
	}	

	@Test
	public void testMaps()
	{
		Assert.assertNull(
			DeepEqualsProcessor.newProcessor(true, automationContext)
				.deepCompare(
					CommonUtils.toMap(
						"map1", CommonUtils.toMap("k1", "v1", "k2", "v2"),
						"map2", CommonUtils.toMap("k21", "v21", "k22", "v22")
					), 
					CommonUtils.toMap(
						"map1", CommonUtils.toMap("k1", "v1", "k2", "v2"),
						"map2", CommonUtils.toMap("k21", "v21", "k22", "v22")
					)
				));

		Assert.assertEquals(
			DeepEqualsProcessor.newProcessor(true, automationContext)
				.deepCompare(
					CommonUtils.toMap(
						"map1", CommonUtils.toMap("k1", "v1", "k2", "v2"),
						"map2", CommonUtils.toMap("k21", "v21-mod", "k22", "v22")
					), 
					CommonUtils.toMap(
						"map1", CommonUtils.toMap("k1", "v1", "k2", "v2"),
						"map2", CommonUtils.toMap("k21", "v21", "k22", "v22")
					)
				),
			"$.map2.k21");
		
		Assert.assertEquals(
			DeepEqualsProcessor.newProcessor(false, automationContext)
				.deepCompare(
					CommonUtils.toMap(
						"map1", CommonUtils.toMap("k1", "v1", "k2", "v2", "extraKey", "extraVal"),
						"map2", CommonUtils.toMap("k21", "v21", "k22", "v22")
					), 
					CommonUtils.toMap(
						"map1", CommonUtils.toMap("k1", "v1", "k2", "v2"),
						"map2", CommonUtils.toMap("k21", "v21", "k22", "v22")
					)
				),
			"$.map1");

		Assert.assertNull(
			DeepEqualsProcessor.newProcessor(true, automationContext)
				.deepCompare(
					CommonUtils.toMap(
						"map1", CommonUtils.toMap("k1", "v1", "k2", "v2", "extraKey", "extraVal"),
						"map2", CommonUtils.toMap("k21", "v21", "k22", "v22")
					), 
					CommonUtils.toMap(
						"map1", CommonUtils.toMap("k1", "v1", "k2", "v2"),
						"map2", CommonUtils.toMap("k21", "v21", "k22", "v22")
					)
				)
			);
	}	

	@Test
	public void testLists()
	{
		Assert.assertNull(
			DeepEqualsProcessor.newProcessor(true, automationContext)
				.deepCompare(
					CommonUtils.toMap(
						"lst1", Arrays.asList(CommonUtils.toMap("name", "emp1", "age", 20), 
								CommonUtils.toMap("name", "emp2", "age", 22)),
						"lst2", Arrays.asList(CommonUtils.toMap("name", "emp3", "age", 20), 
								CommonUtils.toMap("name", "emp4", "age", 22))
					), 
					CommonUtils.toMap(
						"lst1", Arrays.asList(CommonUtils.toMap("name", "emp1", "age", 20), 
								CommonUtils.toMap("name", "emp2", "age", 22)),
						"lst2", Arrays.asList(CommonUtils.toMap("name", "emp3", "age", 20), 
								CommonUtils.toMap("name", "emp4", "age", 22))
					) 
				));
		
		Assert.assertEquals(
			DeepEqualsProcessor.newProcessor(true, automationContext)
			.setListKeys(Arrays.asList("id", "name"))
			.deepCompare(
				CommonUtils.toMap(
					"lst1", Arrays.asList(CommonUtils.toMap("name", "emp1", "age", 20), 
							CommonUtils.toMap("name", "emp2", "age", 23)),
					"lst2", Arrays.asList(CommonUtils.toMap("name", "emp3", "age", 20), 
							CommonUtils.toMap("name", "emp4", "age", 22))
				), 
				CommonUtils.toMap(
					"lst1", Arrays.asList(CommonUtils.toMap("name", "emp1", "age", 20), 
							CommonUtils.toMap("name", "emp2", "age", 24)),
					"lst2", Arrays.asList(CommonUtils.toMap("name", "emp3", "age", 20), 
							CommonUtils.toMap("name", "emp4", "age", 22))
				) 
			), "$.lst1[1@name=emp2].age");
	}
}
