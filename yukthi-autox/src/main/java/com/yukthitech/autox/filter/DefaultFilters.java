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
package com.yukthitech.autox.filter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.common.PropertyAccessor;
import com.yukthitech.autox.common.ResourceNotFoundException;
import com.yukthitech.autox.common.ResourceNotFoundException.ResourceType;
import com.yukthitech.autox.config.AppConfigParserHandler;
import com.yukthitech.autox.config.AppConfigValueProvider;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ContextMap;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.ccg.xml.util.StringUtil;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Default filter methods.
 * @author akiran
 */
public class DefaultFilters
{
	@ExpressionFilter(type = "prop", description = "Parses specified expression as bean property on effective-context (context or current object in case of piping).", 
			example = "prop: attr.bean.value1",
			params = {
					@ParserParam(name = "add", type = "boolean", defaultValue = "false", 
							description = "If true and if specified property indicates a list, during set-value instead of replacing existing element new element will be added/inserted")
				})
	public IPropertyPath propertyParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				PropertyAccessor.setProperty(parserContext.getEffectiveContext(), expression, value);
			}
			
			@Override
			public Object getValue() throws Exception
			{
				return PropertyAccessor.getProperty(parserContext.getEffectiveContext(), expression);
			}
			
			@Override
			public void removeValue() throws Exception
			{
				PropertyAccessor.removeProperty(parserContext.getEffectiveContext(), expression);
			}
		};
	}
	
	@ExpressionFilter(type = "store", description = "Parses specified expression as value on/from store.", example = "store: key1")
	public IPropertyPath storeParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				parserContext.getAutomationContext().getPersistenceStorage().set(expression, value);
			}
			
			@Override
			public Object getValue() throws Exception
			{
				return parserContext.getAutomationContext().getPersistenceStorage().get(expression);
			}
			
			@Override
			public void removeValue() throws Exception
			{
				parserContext.getAutomationContext().getPersistenceStorage().remove(expression);
			}
		};
	}

	@ExpressionFilter(type = "attr", description = "Parses specified expression as context attribute.", example = "attr: attrName", contentType = ParserContentType.ATTRIBUTE,
			params = {
					@ParserParam(name = "global", type = "boolean", defaultValue = "false", description = "During set if value is true, the attribute will be set at global level"),
				})
	public IPropertyPath attrParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				if("true".equalsIgnoreCase(parserContext.getParameter("global")))
				{
					parserContext.getAutomationContext().setGlobalAttribute(expression, value);
				}
				else
				{
					parserContext.getAutomationContext().setAttribute(expression, value);
				}
			}
			
			@Override
			public Object getValue() throws Exception
			{
				return parserContext.getAutomationContext().getAttribute(expression);
			}
			
			@Override
			public void removeValue() throws Exception
			{
				parserContext.getAutomationContext().removeAttribute(expression);
			}
		};
	}

	@ExpressionFilter(type = "param", description = "Parses specified expression as parameter.", example = "param: paramName")
	public IPropertyPath paramParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return parserContext.getAutomationContext().getParameter(expression);
			}
		};
	}

	@ExpressionFilter(type = "xpath", description = "Parses specified expression as xpath on effective-context (context or current object in case of piping).", example = "xpath: /attr/bean/value1",
			params = {
					@ParserParam(name = "multi", type = "boolean", defaultValue = "false", description = "If true, list of matches will be returned"),
				})
	public IPropertyPath xpathParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				JXPathContext.newContext(parserContext.getEffectiveContext()).setValue(expression, value);
			}
			
			@Override
			public Object getValue() throws Exception
			{
				try
				{
					if("true".equalsIgnoreCase(parserContext.getParameter("multi")))
					{
						return JXPathContext.newContext(parserContext.getEffectiveContext()).selectNodes(expression);
					}
					else
					{
						return JXPathContext.newContext(parserContext.getEffectiveContext()).getValue(expression);
					}
				}catch(JXPathNotFoundException ex)
				{
					return null;
				}
			}
			
			@Override
			public void removeValue() throws Exception
			{
				try
				{
					//get the value of expression. Which would throw JXPathNotFoundException if path is not found
					// Note: removePath() or removeAll() will not throw JXPathNotFoundException when path is not found
					
					JXPathContext.newContext(parserContext.getEffectiveContext()).getValue(expression);
					
					if("true".equalsIgnoreCase(parserContext.getParameter("multi")))
					{
						JXPathContext.newContext(parserContext.getEffectiveContext()).removeAll(expression);
					}
					else
					{
						JXPathContext.newContext(parserContext.getEffectiveContext()).removePath(expression);
					}
				}catch(JXPathNotFoundException ex)
				{
				}
			}
		};
	}
	
	private String getStringValue(FilterContext parserContext, String expression) throws Exception
	{
		if("$".equals(expression.trim()))
		{
			Object curVal = parserContext.getCurrentValue();
			
			if(curVal == null)
			{
				return null;
			}
			
			if(curVal instanceof Clob)
			{
				Clob clob = (Clob) curVal;
				return IOUtils.toString(clob.getAsciiStream(), Charset.defaultCharset());
			}
			
			if(curVal instanceof Blob)
			{
				Blob clob = (Blob) curVal;
				return IOUtils.toString(clob.getBinaryStream(), Charset.defaultCharset());
			}

			if(curVal instanceof byte[])
			{
				byte[] byteArr = (byte[]) curVal;
				return IOUtils.toString(byteArr, Charset.defaultCharset().name());
			}

			return curVal.toString().trim();
		}
		
		return expression.trim();
	}

	@ExpressionFilter(type = "string", description = "Returns specified expression as stirng value after triming. In case of '$', current value will be converted to string. "
			+ "In case input object Blob/Clob, string value will be extracted from it.", 
			example = "string: str")
	public IPropertyPath strParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return getStringValue(parserContext, expression);
			}
		};
	}

	@ExpressionFilter(type = "int", description = "Parses specified expression into int. In case of '$', current value's string value will be parsed.", example = "int: 10")
	public IPropertyPath intParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String expr = getStringValue(parserContext, expression);
				return Integer.parseInt(expr);
			}
		};
	}

	@ExpressionFilter(type = "long", description = "Parses specified expression into long. In case of '$', current value's string value will be parsed.", example = "long: 10")
	public IPropertyPath longParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return Long.parseLong(getStringValue(parserContext, expression));
			}
		};
	}

	@ExpressionFilter(type = "float", description = "Parses specified expression into float. In case of '$', current value's string value will be parsed.", example = "float: 10.2")
	public IPropertyPath floatParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return Float.parseFloat(getStringValue(parserContext, expression));
			}
		};
	}

	@ExpressionFilter(type = "double", description = "Parses specified expression into double. In case of '$', current value's string value will be parsed.", example = "double: 10.2")
	public IPropertyPath doubleParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return Double.parseDouble(getStringValue(parserContext, expression));
			}
		};
	}

	@ExpressionFilter(type = "boolean", description = "Parses specified expression into boolean. If expression value is true (case insensitive), then result will be true.  "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "boolean: True")
	public IPropertyPath booleanParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return "true".equalsIgnoreCase(getStringValue(parserContext, expression));
			}
		};
	}

	@ExpressionFilter(type = "date", description = "Parses specified expression into date. "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "date: 21/3/2018, date(format=MM/dd/yyy): 3/21/2018")
	public IPropertyPath dateParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String format = parserContext.getParameter("template");
				
				if(format == null)
				{
					format = "dd/MM/yyyy";
				}
				
				SimpleDateFormat simpleDateFormat = null;
				
				try
				{
					simpleDateFormat = new SimpleDateFormat(format);
				}catch(Exception ex)
				{
					throw new InvalidArgumentException("Invalid date format specified: {}", format, ex);
				}
				
				try
				{
					return simpleDateFormat.parse(getStringValue(parserContext, expression));
				}catch(Exception ex)
				{
					throw new InvalidArgumentException("Specified date {} is not in specified format: {}", expression, format, ex);
				}
			}
		};
	}

	@ExpressionFilter(type = "list", description = "Parses specified expression into list of strings (using comma as delimiter). If type specified, strings will be converted to specified type. "
			+ "In case of '$', current value's string value will be parsed. If current value is collection, it will converted to list directly.", 
			example = "list: val1, val2, val3")
	public IPropertyPath listParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Object getValue() throws Exception
			{
				Object curVal = parserContext.getCurrentValue();
				
				if(curVal instanceof Collection)
				{
					return new ArrayList<>((Collection) curVal);
				}
				
				String parts[] = getStringValue(parserContext, expression).split("\\s*\\,\\s*");
				
				if(exprType == null)
				{
					return Arrays.asList(parts);
				}
				
				if(exprType.length > 1)
				{
					throw new InvalidArgumentException("Multiple type parameters are specified for list conversion: {}", Arrays.toString(exprType));
				}
				
				Class<?> elemType = CommonUtils.getClass(exprType[0]);
				List<Object> resultLst = new ArrayList<>(parts.length);
				
				for(String part : parts)
				{
					resultLst.add( ConvertUtils.convert(part, elemType) );
				}
				
				return resultLst;
			}
		};
	}

	@ExpressionFilter(type = "set", description = "Parses specified expression into set of strings (using comma as delimiter). If type specified, strings will be converted to specified type. "
			+ "In case of '$', current value's string value will be parsed. If current value is collection, it will converted to set directly.", 
			example = "set: val1, val2, val3")
	public IPropertyPath setParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Object getValue() throws Exception
			{
				Object curVal = parserContext.getCurrentValue();
				
				if(curVal instanceof Collection)
				{
					return new HashSet<>((Collection) curVal);
				}
				
				String parts[] = getStringValue(parserContext, expression).split("\\s*\\,\\s*");
				
				if(exprType == null)
				{
					return CommonUtils.toSet(parts);
				}
				
				if(exprType.length > 1)
				{
					throw new InvalidArgumentException("Multiple type parameters are specified for list conversion: {}", Arrays.toString(exprType));
				}
				
				Class<?> elemType = CommonUtils.getClass(exprType[0]);
				Set<Object> resultLst = new HashSet<>(parts.length);
				
				for(String part : parts)
				{
					resultLst.add( ConvertUtils.convert(part.trim(), elemType) );
				}
				
				return resultLst;
			}
		};
	}

	@ExpressionFilter(type = "sort", description = "Sorts the input collection and returns the result as list. As the input is expected to be collection this filter"
			+ "can be used only with $. If no property is specified, ordering will be done in natural order.", 
			example = "attr: lstAttr | sort(property=name): $",
			params = {
					@ParserParam(name = "propertyExpr", type = "String", defaultValue = "natural-ordering", 
							description = "A free marker expression which will be used to convert each object into string, post which sorting will be done. In order"
									+ "to overcome default expression parsing, optionally @{} expression format can be used instead of ${}."),
					@ParserParam(name = "desc", type = "boolean", defaultValue = "false", 
						description = "If set to true, the sorting will be done in reverse order.")
				})
	public IPropertyPath sortParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@SuppressWarnings({ "unchecked", "rawtypes"})
			@Override
			public Object getValue() throws Exception
			{
				Object curVal = parserContext.getCurrentValue();
				
				if(curVal == null)
				{
					return null;
				}
				
				if(!(curVal instanceof Collection))
				{
					throw new InvalidArgumentException("Non-collection is specified as input for sort: {} [Type: {}]", curVal, curVal.getClass().getName());
				}
				
				List<Object> lst = new ArrayList<>((Collection<Object>) curVal);
				String prop = parserContext.getParameter("propertyExpr");
				boolean desc = "true".equals(parserContext.getParameter("desc"));
				
				if(StringUtils.isBlank(prop))
				{
					if(desc)
					{
						Collections.sort((List) lst, Collections.reverseOrder());
					}
					else
					{
						Collections.sort((List) lst);
					}
					
					return lst;
				}
				
				prop = prop.replaceAll("\\@\\{(.*?)\\}", "\\${$1}");
				
				final String finalProp = prop;
				Comparator<Object> valueComparator = new Comparator<Object>()
				{
					@Override
					public int compare(Object o1, Object o2)
					{
						if(o1 == null)
						{
							return -1;
						}
						
						if(o2 == null)
						{
							return 1;
						}
						
						String val1 = FreeMarkerMethodManager.replaceExpressions("expression-obj1", o1, finalProp);
						String val2 = FreeMarkerMethodManager.replaceExpressions("expression-obj2", o2, finalProp);
						
						int diff = val1.compareTo(val2);
						return (diff == 0) ? 1 : diff;
					}
				};
				
				if(desc)
				{
					Collections.sort(lst, Collections.reverseOrder(valueComparator));	
				}
				else
				{
					Collections.sort(lst, valueComparator);
				}
				
				return lst;
			}
		};
	}

	@ExpressionFilter(type = "map", description = "Parses specified expression into map of strings (using comma as delimiter and = as delimiter for key and value). "
			+ "If types specified, strings will be converted to specified type. "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "map: key1 = val1, key2=val2, key3=val3")
	public IPropertyPath mapParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String parts[] = getStringValue(parserContext, expression).split("\\s*\\,\\s*");
				Class<?> keyType = String.class;
				Class<?> valType = String.class;
				
				if(exprType != null)
				{
					if(exprType.length != 2)
					{
						throw new InvalidArgumentException("Insufficient/extra type parameters are specified for map conversion: {}", Arrays.toString(exprType));
					}
					
					keyType = Class.forName(exprType[0]);
					valType = Class.forName(exprType[1]);
				}
	
				Map<Object, Object> resMap = new HashMap<>();
				int eqIdx = 0;
				String key = null, value = null;
				
				for(String part : parts)
				{
					eqIdx = part.indexOf("=");
					
					if(eqIdx < 0)
					{
						throw new InvalidArgumentException("Invalid map entry specified '{}' in expresssion: {}", part, expression);
					}
					
					key = (eqIdx == 0) ? "" : part.substring(0, eqIdx);
					value = (eqIdx == part.length() - 1) ? "" : part.substring(eqIdx + 1, part.length());
					
					resMap.put(ConvertUtils.convert(key, keyType), ConvertUtils.convert(value, valType));
				}
				
				return resMap;
			}
		};
	}

	@ExpressionFilter(type = "condition", description = "Evaluates specified expression as condition and resultant boolean value will be returned", 
			example = "condition: (attr.flag == true)", contentType = ParserContentType.FM_EXPRESSION)
	public IPropertyPath conditionParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return AutomationUtils.evaluateCondition(parserContext.getAutomationContext(), expression.trim());
			}
		};
	}
	
	@ExpressionFilter(type = "expr", description = "Evaluates specified expression as freemarker expression and resultant value will be returned", 
			example = "expr: today()", contentType = ParserContentType.FM_EXPRESSION)
	public IPropertyPath expressionParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String randomVarName = "tmp_" + Long.toHexString(System.currentTimeMillis());
				String fmCode = "${setAttr('" + randomVarName + "', " + expression.trim() + ")}";
				
				IExecutionLogger logger = parserContext.getAutomationContext().getExecutionLogger();
				
				logger.debug("Evaluating expression {} using code snippet: {}", expression, fmCode);
				
				AutomationUtils.replaceExpressionsInString("expression", parserContext.getAutomationContext(), fmCode);
				
				Object result = parserContext.getAutomationContext().removeAttribute(randomVarName);

				logger.debug("From expression {} got result as: {}", expression, result);
				return result;
			}
		};
	}

	/**
	 * Loads the specified input stream as bean.
	 * @param is input stream to load
	 * @param name name of the input
	 * @return loaded object
	 */
	@SuppressWarnings("unchecked")
	private Object loadInputStream(String data, String name, String exprType[], FilterContext parserContext) throws Exception
	{
		IExecutionLogger logger = parserContext.getAutomationContext().getExecutionLogger();
		name = name.trim();
		
		//if the input stream needs app prop replacement
		if("true".equalsIgnoreCase(parserContext.getParameter("propExpr")))
		{
			logger.debug("Processing property expressions: {}", name);
			
			AppConfigValueProvider appConfigValueProvider = new AppConfigValueProvider(parserContext.getAutomationContext().getProp());
			data = StringUtil.getPatternString(data, appConfigValueProvider, 
					AppConfigParserHandler.EXPR_PATTERN, AppConfigParserHandler.EXPR_ESCAPE_PREFIX, AppConfigParserHandler.EXPR_ESCAPE_REPLACE);
		}

		//if the input stream needs to be loaded as template, parse the expressions
		if("true".equalsIgnoreCase(parserContext.getParameter("template")))
		{
			logger.debug("Processing input data as template: {}", name);
			data = AutomationUtils.replaceExpressionsInString(name, parserContext.getAutomationContext(), data);
		}
		
		boolean textResExpected = "true".equalsIgnoreCase(parserContext.getParameter("text"));
		
		if("true".equalsIgnoreCase(parserContext.getParameter("jel")) && name.toLowerCase().endsWith(".json"))
		{
			logger.debug("Parsing json content for JEL: {}", name);
			
			String contextExpr = parserContext.getParameter("contextExpr");
			AutomationContext automationContext = parserContext.getAutomationContext();
			Map<String, Object> context = new ContextMap(automationContext);
			
			if(StringUtils.isNotBlank(contextExpr))
			{
				context = (Map<String, Object>) FreeMarkerMethodManager.fetchValue("Jel-context-expr", contextExpr, automationContext);
			}
			
			//if no further conversion is required, use JEL to convert to final object
			if(exprType == null && !textResExpected)
			{
				return IAutomationConstants.JSON_EXPR_ENGINE.processJsonAsObject(data, context);
			}
				
			//if further conversion is required, then generate json string using JEL 
			data = IAutomationConstants.JSON_EXPR_ENGINE.processJson(data, context);
		}

		//if input stream has to be loaded as simple text, simply return the current data string
		if(textResExpected)
		{
			logger.debug("Returning input data as string: {}", name);
			return data;
		}
		
		Class<?> type = null;
		
		if(exprType != null)
		{
			if(exprType.length != 1)
			{
				throw new InvalidArgumentException("Insufficient/extra type parameters are specified for file loading: {}", Arrays.toString(exprType));
			}
			
			type = Class.forName(exprType[0]);
		}
		
		Object val = AutomationUtils.loadObjectContent(data, name, type, logger);
		
		if("true".equalsIgnoreCase(parserContext.getParameter("expressions")))
		{
			val = AutomationUtils.replaceExpressions(name, parserContext.getAutomationContext(), val);
		}
		
		return val;
	}
	
	private String loadFile(String filePath) throws IOException
	{
		File file = new File(filePath);
		
		if(!file.exists() || !file.isFile())
		{
			throw new ResourceNotFoundException(ResourceType.FILE, filePath);
		}
		
		return FileUtils.readFileToString(file, Charset.defaultCharset());
	}

	@ExpressionFilter(type = "file", description = "Parses specified expression as file path and loads it as object. "
			+ "As part of 'set', the specified content will be converted to string and will be writtern to file. "
			+ "Supported object file types: xml, json, properties", 
			example = "file: /tmp/data.json",
			params = {
				@ParserParam(name = "template", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be parsed as freemarker template"),
				@ParserParam(name = "text", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be returned as text directly, without parsing into object."),
				@ParserParam(name = "propExpr", type = "boolean", defaultValue = "false", description = "If true, the property expressions #{} will be replaced with corresponding values."),
				@ParserParam(name = "jel", type = "boolean", defaultValue = "false", description = "If true, the json will be processed with Json expression language before object conversion. Applicable only for json content."),
				@ParserParam(name = "contextExpr", type = "String", defaultValue = "none", description = "Fmarker expression to context object for JEL (used only when jel = true)."),
				@ParserParam(name = "expressions", type = "boolean", defaultValue = "false", description = "If true, then post parsing into object, values will be searched and processed as autox expressions")
			})
	public IPropertyPath fileParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return loadInputStream(loadFile(expression), expression, exprType, parserContext);
			}
			
			@Override
			public void setValue(Object value) throws Exception
			{
				FileUtils.write(new File(expression), String.valueOf(value), Charset.defaultCharset());
			}
		};
	}

	@ExpressionFilter(type = "bfile", description = "Parses specified expression as file path and loads it as binary data (byte array)."
			+ "As part of 'set' the value is expected to be byte[] which will be written to specified file.", 
			example = "bfile: /tmp/data")
	public IPropertyPath bfileParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				parserContext.getAutomationContext().getExecutionLogger().debug("Loading binary content from file: {}", expression);
				
				File file = new File(expression);
				
				if(!file.exists())
				{
					throw new ResourceNotFoundException(ResourceType.FILE, expression);
				}
				
				return FileUtils.readFileToByteArray(file);
			}
			
			@Override
			public void setValue(Object value) throws Exception
			{
				parserContext.getAutomationContext().getExecutionLogger().debug("Writing binary content to file: {}", expression);
				
				FileUtils.writeByteArrayToFile(new File(expression), (byte[]) value);
			}
		};
	}

	@ExpressionFilter(type = "res", description = "Parses specified expression as resource path and loads it as object. Supported file types: xml, json, properties",
			example = "res: /tmp/data.json",
			params = {
				@ParserParam(name = "template", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be parsed as freemarker template"),
				@ParserParam(name = "text", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be returned as text directly, without parsing into object."),
				@ParserParam(name = "propExpr", type = "boolean", defaultValue = "false", description = "If true, the property expressions #{} will be replaced with corresponding values."),
				@ParserParam(name = "jel", type = "boolean", defaultValue = "false", description = "If true, the json will be processed with Json expression language before object conversion. Applicable only for json content."),
				@ParserParam(name = "contextExpr", type = "String", defaultValue = "none", description = "Fmarker expression to context object for JEL (used only when jel = true)."),
				@ParserParam(name = "expressions", type = "boolean", defaultValue = "false", description = "If true, then post parsing into object, values will be searched and processed as autox expressions")
			})
	public IPropertyPath resParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				parserContext.getAutomationContext().getExecutionLogger().debug("Loading text content from resource: {}", expression);
				
				String data = null;
				
				if("$".equals(expression.trim()))
				{
					Object curVal = parserContext.getCurrentValue();
					
					if(curVal == null || !(curVal instanceof String))
					{
						throw new InvalidStateException("No/incompatible data found on the pipe input. Piped Input: {}", curVal);
					}
					
					data = curVal.toString();
				}
				else
				{
					InputStream is = DefaultFilters.class.getResourceAsStream(expression); 

					if(is == null)
					{
						throw new ResourceNotFoundException(ResourceType.RESOURCE, expression);
					}
					
					data = IOUtils.toString(is, Charset.defaultCharset());
					is.close();
				}
				
				Object object = loadInputStream(data, expression, exprType, parserContext);
				return object;
			}
		};
	}

	@ExpressionFilter(type = "bres", description = "Parses specified expression as resource path and loads it as binary data (byte array).", 
			example = "bres: /tmp/data.png")
	public IPropertyPath bresParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				parserContext.getAutomationContext().getExecutionLogger().debug("Loading binary content from resource: {}", expression);
				
				byte data[] = null;
				
				if("$".equals(expression.trim()))
				{
					Object curVal = parserContext.getCurrentValue();
					
					if(curVal == null || (!(curVal instanceof String) && !(curVal instanceof byte[])) )
					{
						throw new InvalidStateException("No/incompatible data found on the pipe input. Piped Input: {}", curVal);
					}
					
					if(curVal instanceof String)
					{
						data = ((String) curVal).getBytes();
					}
					else
					{
						data = (byte[]) curVal;
					}
				}
				else
				{
					InputStream is = DefaultFilters.class.getResourceAsStream(expression); 

					if(is == null)
					{
						throw new ResourceNotFoundException(ResourceType.RESOURCE, expression);
					}
					
					data = IOUtils.toByteArray(is);
					is.close();
				}
				
				return data;
			}
		};
	}

	@ExpressionFilter(type = "json", description = "Parses specified expression as json string and loads it as object. "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "json: {\"a\": 2, \"b\": 3}",
			params = {
					@ParserParam(name = "template", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be parsed as freemarker template"),
					@ParserParam(name = "jel", type = "boolean", defaultValue = "false", description = "If true, the json will be processed with Json expression language before object conversion"),
					@ParserParam(name = "contextExpr", type = "String", defaultValue = "none", description = "Fmarker expression to context object for JEL (used only when jel = true)."),
					@ParserParam(name = "expressions", type = "boolean", defaultValue = "false", description = "If true, then post parsing into object, values will be searched and processed as autox expressions"),
					@ParserParam(name = "javaType", type = "String",  defaultValue = "null", description = "If specified, then json will be parsed to specified java type")
				}
	)
	public IPropertyPath jsonParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String data = getStringValue(parserContext, expression);
				String javaType = parserContext.getParameter("javaType");
				String finalExprType[] = exprType;
				
				if(javaType != null)
				{
					finalExprType = new String[] {javaType};
				}
				
				Object object = loadInputStream(data, ".json", finalExprType, parserContext);
				return object;
			}
		};
	}

	@ExpressionFilter(type = "jsonWithType", description = "Parses specified expression as json (with types) string and loads it as object. "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "jsonWithType: {\"a\": 2, \"b\": 3}",
			params = {
					@ParserParam(name = "template", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be parsed as freemarker template"),
					@ParserParam(name = "jel", type = "boolean", defaultValue = "false", description = "If true, the json will be processed with Json expression language before object conversion"),
					@ParserParam(name = "contextExpr", type = "String", defaultValue = "none", description = "Fmarker expression to context object for JEL (used only when jel = true)."),
					@ParserParam(name = "expressions", type = "boolean", defaultValue = "false", description = "If true, then post parsing into object, values will be searched and processed as autox expressions")
				}
	)
	public IPropertyPath jsonWithTypeParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String data = getStringValue(parserContext, expression);
				Object object = loadInputStream(data, ".jsonWithType", exprType, parserContext);
				return object;
			}
		};
	}

	@ExpressionFilter(type = "template", description = "Parses specified value for free marker expressions and returns the result.",
			example = "template: Value=${someAttr}"
	)
	public IPropertyPath templateParser(FilterContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String data = getStringValue(parserContext, expression);
				return AutomationUtils.replaceExpressionsInString("template", parserContext.getAutomationContext(), data);
			}
		};
	}
}

