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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Common free marker methods.
 * @author akiran
 */
public class CommonFreeMarkerMethods
{
	/**
	 * Converts input file path (Can be relative, partial path) to full canonical path.
	 * @param path path to convert.
	 * @return converted path.
	 */
	@FreeMarkerMethod(
			description = "Converts input file path (Can be relative, partial path) to full canonical path.",
			returnDescription = "Canonical path of the specified path"
			)
	public static String fullPath(
			@FmParam(name = "path", description = "Path to be converted") String path)
	{
		try
		{
			return new File(path).getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while fetching full path of path: {}", path, ex);
		}
	}

	@FreeMarkerMethod(
			description = "Converts specified date into millis.",
			returnDescription = "Millis value"
			)
	public static Long toMillis(
			@FmParam(name = "date", description = "Date to be converted") Date date)
	{
		if(date == null)
		{
			return null;
		}
		
		return date.getTime();
	}

	@FreeMarkerMethod(
			description = "Checks if specified file path exists or not.",
			returnDescription = "Path to be checked"
			)
	public static boolean fileExists(
			@FmParam(name = "path", description = "Path to be checked") String path)
	{
		return new File(path).exists();
	}

	@FreeMarkerMethod(
			description = "Checks if specified file path is a file or not.",
			returnDescription = "Path to be checked"
			)
	public static boolean isFile(
			@FmParam(name = "path", description = "Path to be checked") String path)
	{
		return new File(path).isFile();
	}

	@FreeMarkerMethod(
			description = "Checks if specified file path is a directory or not.",
			returnDescription = "Path to be checked"
			)
	public static boolean isDirectory(
			@FmParam(name = "path", description = "Path to be checked") String path)
	{
		return new File(path).isDirectory();
	}

	/**
	 * Fetches the value for specified xpath.
	 * @param source source on which xpath has to be fetched.
	 * @param xpath xpath to be executed.
	 * @return matching value
	 */
	@FreeMarkerMethod(
			description = "Fetches the value for specified xpath from specified source object.",
			returnDescription = "Value of specified xpath"
			)
	public static Object getValueByXpath(
			@FmParam(name = "source", description = "Source object on which xpath needs to be evaluated") Object source, 
			@FmParam(name = "xpath", description = "Xpath to be evaluated") String xpath)
	{
		return JXPathContext.newContext(source).getValue(xpath);
	}
	
	/**
	 * Fetching all the values for specified xpath.
	 * @param source source on which xpath to be fetched
	 * @param xpath xpath to be executed.
	 * @return matching values.
	 */
	@SuppressWarnings("unchecked")
	@FreeMarkerMethod(
			description = "Fetches the value(s) list for specified xpath from specified source object.",
			returnDescription = "Value of specified xpath"
			)
	public static List<Object> getValuesByXpath(
			@FmParam(name = "source", description = "Source object on which xpath needs to be evaluated") Object source, 
			@FmParam(name = "xpath", description = "Xpath to be evaluated") String xpath)
	{
		return JXPathContext.newContext(source).selectNodes(xpath);
	}

	/**
	 * Fetches the count of values obtained from specified xpath.
	 * @param source
	 * @param xpath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@FreeMarkerMethod(
			description = "Fetches the count of values matching with specified xpath from specified source object.",
			returnDescription = "Number of values matching with specified xpath"
			)
	public static int countOfXpath(
			@FmParam(name = "source", description = "Source object on which xpath should be evaluated") Object source, 
			@FmParam(name = "xpath", description = "Xpath to be evaluated") String xpath)
	{
		try
		{
			List<Object> res = JXPathContext.newContext(source).selectNodes(xpath);
			
			if(res == null)
			{
				return 0;
			}
			
			return res.size();
		}catch(JXPathNotFoundException ex)
		{
			return 0;
		}
	}

	/**
	 * Fetches value from story with specified key.
	 * @param key key to be fetched
	 * @return matching value from store
	 */
	@FreeMarkerMethod(
			description = "Fetches the value of specified key from the store.",
			returnDescription = "Matched value"
			)
	public static Object storeValue(
			@FmParam(name = "key", description = "Key of value to be fetched") String key)
	{
		return AutomationContext.getInstance().getPersistenceStorage().get(key);
	}
	
	/**
	 * Compares the specified values and returns the comparison result as int.
	 * @param value1
	 * @param value2
	 * @return
	 */
	@FreeMarkerMethod(
			description = "Compares the specified values and returns the comparison result as int.",
			returnDescription = "Comparison result."
			)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compare(
			@FmParam(name = "value1", description = "Value1 to compare") Object value1,
			@FmParam(name = "value2", description = "Value2 to compare") Object value2
			)
	{
		if(value1 == null && value2 == null)
		{
			return 0;
		}
		
		if(value1 == value2)
		{
			return 0;
		}
		
		if(!(value1 instanceof Comparable))
		{
			throw new InvalidArgumentException("Non comparable object is specified as value1: {}", value1);
		}

		if(!(value2 instanceof Comparable))
		{
			throw new InvalidArgumentException("Non comparable object is specified as value1: {}", value1);
		}
		
		return ((Comparable)value1).compareTo(value2);
	}
	
	@FreeMarkerMethod(
			description = "Used to set value as content attribute. This function will always return empty string.",
			returnDescription = "Always empty string."
			)
	public static String setAttr(String attrName, Object value)
	{
		AutomationContext.getInstance().setAttribute(attrName, value);
		return "";
	}
	
	@FreeMarkerMethod(
			description = "Used to convert specified data into a input stream. Supported parameter types - CharSequence, byte[].",
			returnDescription = "Converted input stream."
			)
	public static InputStream toStream(@FmParam(name = "input", description = "Input that needs to be converted to input stream") Object val)
	{
		if(val instanceof byte[])
		{
			return new ByteArrayInputStream((byte[]) val);
		}
		
		if(val instanceof CharSequence)
		{
			return new ByteArrayInputStream( val.toString().getBytes() );
		}
		
		throw new InvalidArgumentException("Invalid object (%s) specified for conversion to stream", val.getClass().getName());
	}

	@FreeMarkerMethod(
			description = "Used to convert specified data into a reader. Supported parameter types - CharSequence, byte[].",
			returnDescription = "Converted input stream."
			)
	public static Reader toReader(@FmParam(name = "input", description = "Input that needs to be converted to reader") Object val)
	{
		if(val instanceof byte[])
		{
			return new InputStreamReader( new ByteArrayInputStream((byte[]) val) );
		}
		
		if(val instanceof CharSequence)
		{
			return new StringReader( val.toString() );
		}
		
		throw new InvalidArgumentException("Invalid object (%s) specified for conversion to reader", val.getClass().getName());
	}
	
	@FreeMarkerMethod(
			description = "Used to compare specified attribute with specified value and return appropiate result.",
			returnDescription = "True or false value based on match."
			)
	public static String compareAndGet(
			@FmParam(name = "name", description = "Name of the attribute to check") String name, 
			@FmParam(name = "value", description = "Expected value of the attribute") String value, 
			@FmParam(name = "trueVal", description = "Value to be returned when the attribute value match with specified value") String trueVal, 
			@FmParam(name = "falseVal", description = "Value to be returned when the attribute value DOES NOT match with specified value") String falseVal)
	{
		AutomationContext context = AutomationContext.getInstance();
		String curVal = (String) context.getAttribute(name);
		
		if(Objects.equals(curVal, value))
		{
			return trueVal;
		}
		
		return falseVal;
	}
	
	@SuppressWarnings("unchecked")
	@FreeMarkerMethod(
			description = "Used to check if specified value is empty. "
					+ "For collection, map and string, along with null this will check for empty value.",
			returnDescription = "True if value is empty."
			)
	public static boolean isEmpty(
			@FmParam(name = "value", description = "Value to be checked for empty") Object value)
	{
		if(value == null)
		{
			return true;
		}
		
		if(value instanceof String)
		{
			String str = (String) value;
			return (str.trim().length() == 0);
		}
		
		if(value instanceof Collection)
		{
			Collection<Object> col = (Collection<Object>) value;
			return col.isEmpty();
		}

		if(value instanceof Map)
		{
			Map<Object, Object> map = (Map<Object, Object>) value;
			return map.isEmpty();
		}
		
		return false;
	}

	@FreeMarkerMethod(
			description = "Used to check if specified value is not empty. "
					+ "For collection, map and string, along with non-null this will check for non-empty value.",
			returnDescription = "True if value is empty."
			)
	public static boolean isNotEmpty(
			@FmParam(name = "value", description = "Value to be checked for empty") Object value)
	{
		return !isEmpty(value);
	}

	@FreeMarkerMethod(
			description = "Used to check if specified value is null and return approp value when null and when non-null.",
			returnDescription = "Specified null-condition-value or non-null-condition-value."
			)
	public static Object nvl(
			@FmParam(name = "value", description = "Value to be checked for empty") Object value,
			@FmParam(name = "nullValue", description = "Value to be returned when value is null") Object nullValue,
			@FmParam(name = "nonNullValue", description = "Value to be returned when value is non-null") Object nonNullValue
			)
	{
		return (value == null) ? nullValue : nonNullValue;
	}

	@FreeMarkerMethod(
			description = "Used to check if specified value is true and return approp value"
					+ " Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false.",
			returnDescription = "Specified true-condition-value or false-condition-value."
			)
	public static Object ifTrue(
			@FmParam(name = "value", description = "Value to be checked for true.") Object value,
			@FmParam(name = "trueValue", description = "Value to be returned when value is true. Default: true") Object trueValue,
			@FmParam(name = "falseValue", description = "Value to be returned when value is false or null. Default: false") Object falseValue
			)
	{
		trueValue = (trueValue == null) ? true : trueValue;
		falseValue = (falseValue == null) ? false : falseValue;
		
		boolean bvalue = "true".equalsIgnoreCase("" + value) ? true : false;
		
		return bvalue ? trueValue : falseValue;
	}

	@FreeMarkerMethod(
			description = "Used to check if specified value is false and return approp value"
					+ " Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false. "
					+ "If null, the condition will be considered as false (hence returing falseValue)",
			returnDescription = "Specified true-condition-value or false-condition-value."
			)
	public static Object ifFalse(
			@FmParam(name = "value", description = "Value to be checked for false. Can be boolean true or string 'true'") Object value,
			@FmParam(name = "falseValue", description = "Value to be returned when value is false or null. Default: true") Object falseValue,
			@FmParam(name = "trueValue", description = "Value to be returned when value is true. Default: false") Object trueValue
			)
	{
		trueValue = (trueValue == null) ? false : trueValue;
		falseValue = (falseValue == null) ? true : falseValue;

		boolean bvalue = "true".equalsIgnoreCase("" + value) ? true : false;
		return bvalue ? trueValue : falseValue;
	}

	@FreeMarkerMethod(
			description = "Used to check if both values are equal or not. Nulls are also allowed.",
			returnDescription = "True if both are null or both are equal."
			)
	public static boolean isEqual(
			@FmParam(name = "value1", description = "Value1 to compare") Object value1,
			@FmParam(name = "value2", description = "Value2 to compare") Object value2
			)
	{
		return Objects.equals(value1, value2);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@FreeMarkerMethod(
			description = "Evaluates json expressions in the input template and returns the result json. To input context, automation-context will be added with name 'context'.",
			returnDescription = "Result of json expression evaluation. This will be json string."
			)
	public static String evalJsonExpr(
			@FmParam(name = "template", description = "Json template to be evaluated. If non-string is specified, it will be converted to json first.") Object template,
			@FmParam(name = "context", description = "Context to be used for evaluation") Map<String, Object> context
			) throws Exception
	{
		AutomationContext automationContext = AutomationContext.getInstance();
		Map<String, Object> localContext = new HashMap<String, Object>((Map) context);
		localContext.put("context", automationContext);
		
		String templateStr = (template instanceof String) ? (String) template : IAutomationConstants.OBJECT_MAPPER.writeValueAsString(template);

		String resJson = IAutomationConstants.JSON_EXPR_ENGINE.processJson(templateStr, localContext);
		return resJson;
	}
	
	@SuppressWarnings({ "unchecked"})
	@FreeMarkerMethod(
			description = "Checks whether specified value is present in specified collection/map. If map, value will be searched as key. "
					+ "If collection is null or non-collection and non-map, then nullValue will be returned.",
			returnDescription = "True if value/key is present in collection/map."
			)
	public static Boolean contains(
			@FmParam(name = "collection", description = "Collection/map in which value/key has to be searched.") Object collection,
			@FmParam(name = "value", description = "Value/key to be searched") Object value,
			@FmParam(name = "nullVal", description = "Flag to be returned when collection is null or non-suppported type") Boolean nullVal
			) throws Exception
	{
		if(collection instanceof Collection)
		{
			Collection<Object> col = (Collection<Object>) collection;
			return col.contains(value);
		}
		
		if(collection instanceof Map)
		{
			Map<Object, Object> map = (Map<Object, Object>) collection;
			return map.containsKey(value);
		}
		
		return nullVal;
	}

	@FreeMarkerMethod(
			description = "Checks whether specified value is NOT present in specified collection/map. If map, value will be searched as key. "
					+ "If collection is null or non-collection and non-map, then nullValue will be returned.",
			returnDescription = "True if value/key is NOT present in collection/map."
			)
	public static Boolean notContains(
			@FmParam(name = "collection", description = "Collection/map in which value/key has to be searched.") Object collection,
			@FmParam(name = "value", description = "Value/key to be searched") Object value,
			@FmParam(name = "nullVal", description = "Flag to be returned when collection is null or non-suppported type") Boolean nullVal
			) throws Exception
	{
		Boolean res = contains(collection, value, null);
		
		if(res == null)
		{
			return nullVal;
		}

		return res;
	}
	
	@FreeMarkerMethod(
			description = "Converts specified string value into long value.",
			returnDescription = "Converted long value."
			)
	public static Long toLong(
			@FmParam(name = "str", description = "String value to be converted") String str)
	{
		return Long.parseLong(str);
	}

	@FreeMarkerMethod(
			description = "Converts specified string value into int value.",
			returnDescription = "Converted int value."
			)
	public static Integer toInt(
			@FmParam(name = "str", description = "String value to be converted") String str)
	{
		return Integer.parseInt(str);
	}

	@FreeMarkerMethod(
			description = "Converts specified string value into boolean value.",
			returnDescription = "Converted boolean value."
			)
	public static Boolean toBoolean(
			@FmParam(name = "str", description = "String value to be converted") String str)
	{
		return "true".equalsIgnoreCase(str);
	}
	
	@FreeMarkerMethod(
			description = "Parses specified string as json and returns result.",
			returnDescription = "parsed json object"
			)
	public static Object parseJson(
			@FmParam(name = "str", description = "String to parse") String str) throws Exception
	{
		return IAutomationConstants.OBJECT_MAPPER.readValue(str, Object.class);
	}

	/**
	 * Removes trailing whitespaces b/w nodes and text-content, recursively. 
	 * @param node node to process
	 */
	private static void trimWhitespace(Node node)
	{
		NodeList children = node.getChildNodes();
		
		for(int i = 0; i < children.getLength(); ++i)
		{
			Node child = children.item(i);
			
			if(child.getNodeType() == Node.TEXT_NODE)
			{
				child.setTextContent(child.getTextContent().trim());
			}
			
			trimWhitespace(child);
		}
	}
	
	@FreeMarkerMethod(
			description = "Used for normalizing xml content, by removing trailing whitespaces between nodes, "
					+ "which in turn can be used during xml content comparison",
			returnDescription = "Nomralized xml content (witout white spaces)."
			)
	public static String normalizeXml(String xmlContent) throws Exception
	{
		if(xmlContent == null)
		{
			return null;
		}
		
		//convert xml content to DOM document
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(xmlContent.getBytes()));
		
		trimWhitespace(doc);
		
		//transform document into string, in standard format
		StringWriter buff = new StringWriter();
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		Result output = new StreamResult(buff);
		Source input = new DOMSource(doc);

		transformer.transform(input, output);
		
		//return result
		buff.flush();
		return buff.toString();
	}

	@FreeMarkerMethod(
			description = "Removes special characters from input string. Also all whitespaces chars will be replaced with space.",
			returnDescription = "String with special chars removed"
			)
	public static String removeSpecialChars(
			@FmParam(name = "str", description = "Input string") String str)
	{
		if(str == null)
		{
			return "null";
		}
		
		//remove special characters if any
		char chArr[] = str.toCharArray();
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < chArr.length; i++)
		{
			if(Character.isWhitespace(chArr[i]))
			{
				builder.append(' ');
				continue;
			}
			
			if(chArr[i] < 33 || chArr[i] > 126)
			{
				continue;
			}
			
			builder.append(chArr[i]);
		}
		
		str = builder.toString();;
		str = str.replaceAll("\\ +", " ");

		return str;
	}

	@FreeMarkerMethod(
			description = "Creates an empty map and returns the same.",
			returnDescription = "Empty map"
			)
	public static Map<Object, Object> emptyMap()
	{
		return new HashMap<Object, Object>();
	}
}
