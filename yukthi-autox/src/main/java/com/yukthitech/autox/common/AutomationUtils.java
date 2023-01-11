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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.prefix.PrefixExpressionContext;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.autox.ref.IReference;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.DynamicBeanParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.ccg.xml.XMLConstants;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Common util functions.
 * @author akiran
 */
public class AutomationUtils
{
	/**
	 * Pattern to parse java type.
	 */
	private static final Pattern TYPE_STR_PATTERN = Pattern.compile("([\\w\\.\\$]+)\\s*\\<\\s*([\\w\\.\\$\\,\\ ]+\\s*)\\>\\s*");
	
	/**
	 * Pattern to be used for identifying value expressions.
	 */
	private static final Pattern VALUE_EXPR_PATTERN = Pattern.compile("^\\[\\[(.*)\\]\\]$");
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static ObjectMapper objectMapperWithType = new ObjectMapper();
	
	static
	{
		objectMapperWithType.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
	}
	
	/**
	 * Loads the xml files from specified folder. Returned set will be ordered by their relative paths.
	 * @param folder folder to be loaded.
	 * @return loaded xml files ordered by relative path.
	 */
	public static TreeSet<File> loadXmlFiles(File folder)
	{
		final URI rootPath = folder.toURI();
		
		TreeSet<File> xmlFiles = new TreeSet<>(new Comparator<File>()
		{
			@Override
			public int compare(File o1, File o2)
			{
				String path1 = rootPath.relativize(o1.toURI()).getPath();
				String path2 = rootPath.relativize(o2.toURI()).getPath();
				
				return path1.compareTo(path2);
			}
		});
		
		Stack<File> folders = new Stack<>();
		folders.push(folder);

		// filter to filter xml files and add sub folder to stack
		FileFilter fileFiler = new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				if(pathname.isDirectory())
				{
					folders.push(pathname);
				}
				else if(pathname.getName().toLowerCase().endsWith(".xml"))
				{
					xmlFiles.add(pathname);
					return false;
				}

				return false;
			}
		};

		// loop till scanning is completed on test folder and its sub folders
		while(!folders.isEmpty())
		{
			folders.pop().listFiles(fileFiler);
		}

		return xmlFiles;
	}
	
	/**
	 * Treats provided template as freemarker template and processes them. The result will be returned.
	 * @param context Automation context which would be used as freemarker context for processing.
	 * @param templateStr Template in which expressions should be replaced
	 * @return Processed string
	 */
	public static String replaceExpressionsInString(String templateName, AutomationContext context, String templateStr)
	{
		return FreeMarkerMethodManager.replaceExpressions(templateName, context, templateStr);
	}
	
	private static Object parseExpressions(String templateName, AutomationContext context, String templateStr)
	{
		Matcher matcher = VALUE_EXPR_PATTERN.matcher(templateStr);
		
		if(matcher.matches())
		{
			String valueExpr = matcher.group(1);
			return PrefixExpressionFactory.getExpressionFactory().getValueByExpression(context, valueExpr);
		}
		
		return FreeMarkerMethodManager.replaceExpressions(templateName, context, templateStr);
	}

	/**
	 * Gets all fields of specified type and also its ancestors.
	 * @param type Type from which fields needs to be extracted
	 * @return Fields from specified type and its ancestors
	 */
	private static List<Field> getAllInstanceFields(Class<?> type)
	{
		List<Field> fields = new ArrayList<>();
		Class<?> curClass = type;
		
		while(true)
		{
			fields.addAll(Arrays.asList(curClass.getDeclaredFields()));
			curClass = curClass.getSuperclass();
			
			if(curClass.getName().startsWith("java"))
			{
				break;
			}
		}
		
		return fields;
	}

	/**
	 * Replaces expressions in specified step properties.
	 * @param context Context to fetch values for expressions.
	 * @param object Object in which expressions has to be replaced
	 */
	@SuppressWarnings("unchecked")
	public static <T> T replaceExpressions(String templateName, AutomationContext context, T object)
	{
		if(object == null)
		{
			return null;
		}
		
		Class<?> executableType = object.getClass();
		
		if(executableType.isPrimitive() || executableType.isArray())
		{
			return object;
		}
		
		if(object instanceof IReference)
		{
			IReference ref = (IReference) object;
			return (T) ref.getValue(context);
		}
		
		if(object instanceof String)
		{
			return (T) parseExpressions(templateName, context, (String) object);
		}
		
		//when executable is collection
		if(object instanceof Collection)
		{
			Collection<Object> collection = (Collection<Object>) object;
			
			if(collection.isEmpty())
			{
				return (T) collection;
			}
			
			Collection<Object> resCollection = null;
			
			try
			{
				resCollection = (Collection<Object>) executableType.newInstance();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while parsing expressions", ex);
			}
			
			int index = 0;
			
			for(Object element : collection)
			{
				resCollection.add( replaceExpressions(templateName + "["  + index + "]" ,context, element) );
				index++;
			}
			
			return (T) resCollection;
		}
			
		//when executable is map
		if(object instanceof Map)
		{
			Map<Object, Object> map = (Map<Object, Object>) object;
			
			if(map.isEmpty())
			{
				return (T) map;
			}
			
			Map<Object, Object> resMap = null;
			
			try
			{
				resMap = (Map<Object, Object>) executableType.newInstance();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while parsing expressions", ex);
			}
			
			for(Map.Entry<Object, Object> entry : map.entrySet())
			{
				resMap.put( entry.getKey(), replaceExpressions(templateName + "{" + entry.getKey() + "}", context, entry.getValue()) ) ;
			}
			
			return (T) resMap;
		}
		
		if(executableType.getName().startsWith("java"))
		{
			return object;
		}

		List<Field> fields = getAllInstanceFields(object.getClass());
		
		Object fieldValue = null;
		Class<?> fieldType = null;
		Param param = null;
		
		for(Field field : fields)
		{
			if(Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			
			//skip fields which are annotated with SkipParsing 
			if(field.getAnnotation(SkipParsing.class) != null)
			{
				continue;
			}
			
			try
			{
				fieldType = field.getType();
				
				if(fieldType.isPrimitive() || fieldType.isArray())
				{
					continue;
				}

				field.setAccessible(true);
				
				fieldValue = field.get(object);
				
				//ignore null field values
				if(fieldValue == null)
				{
					continue;
				}
				
				param = field.getAnnotation(Param.class);
				
				fieldValue = replaceExpressions(templateName + "." + field.getName(), context, fieldValue);
				field.set(object, fieldValue);
				
				if(param != null && param.sourceType() == SourceType.EXPRESSION)
				{
					Object result = mapObjects(context, field.get(object), val -> {
						return PrefixExpressionFactory.getExpressionFactory().getValueByExpression(context, val);
					});
					
					if(result != null && !Object.class.equals(param.expectedType()))
					{
						if(!param.expectedType().isAssignableFrom(result.getClass()))
						{
							try
							{
								result = ConvertUtils.convert(result, param.expectedType());
							}catch(Exception ex)
							{
								throw new InvalidArgumentException("For field {}.{} specified value {} (Type: {}) when {} is expected", 
										object.getClass().getName(), field.getName(), result, result.getClass().getName(), param.expectedType().getName());
							}
						}
					}
					
					field.set(object, result);
				}

			} catch(InvalidStateException ex)
			{
				throw ex;
			} catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while parsing expressions in field: {}.{}", 
					object.getClass().getName(), field.getName());
			}
		}
		
		return object;
	}
	
	@SuppressWarnings("unchecked")
	private static Object mapObjects(AutomationContext context, Object value, Function<Object, Object> consumer) throws Exception
	{
		if(value == null)
		{
			return null;
		}
		
		if(value instanceof Collection)
		{
			Collection<Object> input = (Collection<Object>) value;
			
			if(input.isEmpty())
			{
				return input;
			}
			
			Collection<Object> res = (Collection<Object>) input.getClass().newInstance();
			
			for(Object inputVal : input)
			{
				res.add(consumer.apply(inputVal));
			}
			
			return res;
		}

		if(value instanceof Map)
		{
			Map<Object, Object> input = (Map<Object, Object>) value;
			
			if(input.isEmpty())
			{
				return input;
			}
			
			Map<Object, Object> res = (Map<Object, Object>) input.getClass().newInstance();
			
			for(Object key : input.keySet())
			{
				res.put(consumer.apply(key), consumer.apply(input.get(key)));
			}
			
			return res;
		}
		
		return consumer.apply(value);
	}

	/**
	 * Validates required parameters, configured by {@link Param}, are specified for target bean.
	 * @param bean bean to validate
	 */
	public static void validateRequiredParams(Object bean)
	{
		List<Field> fields = getAllInstanceFields(bean.getClass());
		Param param = null;
		Object value = null;
		
		try
		{
			for(Field field : fields)
			{
				param = field.getAnnotation(Param.class);
				
				//if field is not require, ignore
				if(param == null || !param.required())
				{
					continue;
				}
	
				field.setAccessible(true);
				value = field.get(bean);
				
				//validate value is provided and not blank
				if(value == null)
				{
					throw new InvalidStateException("Required param {} is not specified", field.getName());
				}
				
				if( (value instanceof String) && StringUtils.isBlank(value.toString()) )
				{
					throw new InvalidStateException("Blank value is specified for required param {}", field.getName());
				}
				
				if( (value instanceof Collection) && CollectionUtils.isEmpty((Collection<?>)value) )
				{
					throw new InvalidStateException("No value(s) specified for required param {}", field.getName());
				}
			}
		} catch(InvalidStateException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while validating bean - {}", bean, ex);
		}
	}
	
	/**
	 * Deep clones the object by converting object to json and back to object.
	 * @param object object to be cloned.
	 * @return cloned object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deepClone(T object)
	{
		try
		{
			//covert object into bytes
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			
			oos.writeObject(object);
			oos.flush();
			bos.flush();
			
			//read bytes back to object which would be deep copy
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			return (T) ois.readObject();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while deep cloning object: {}", object, ex);
		}
	}
	
	/**
	 * Evaluates specified free marker condition and returns the result.
	 * @param context Context to be used
	 * @param condition Condition to be evaluated
	 * @return true, if condition evaluated to be true
	 */
	public static boolean evaluateCondition(AutomationContext context, String condition)
	{
		if("true".equalsIgnoreCase(condition))
		{
			return true;
		}

		if("false".equalsIgnoreCase(condition))
		{
			return false;
		}

		String ifCondition = String.format("<#if %s>true<#else>false</#if>", condition);
		String res = null;
		
		try
		{
			res = AutomationUtils.replaceExpressionsInString("condition", context, ifCondition);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while evaluating condition: {}", condition, ex);
		}
		
		return "true".equals(res);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JavaType parseJavaType(String typeStr)
	{
		typeStr = typeStr.trim();
		
		if(typeStr.indexOf("<") <= 0)
		{
			try
			{
				Class<?> type = Class.forName(typeStr);
				return TypeFactory.defaultInstance().constructSimpleType(type, null);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException("Invalid simple-type specified: {}", typeStr, ex);
			}
		}
		
		Matcher matcher = TYPE_STR_PATTERN.matcher(typeStr);
		
		if(!matcher.matches())
		{
			throw new InvalidArgumentException("Invalid type string specified: {}", typeStr);
		}
		
		String baseTypeStr = matcher.group(1);
		String paramLstStr = matcher.group(2);
		String paramTypeStr[] = paramLstStr.split("\\s*\\,\\s*");
		
		Class<?> baseType = null;
		Class<?> paramTypes[] = null;
		
		try
		{
			baseType = Class.forName(baseTypeStr);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid base-type '{}' specified in parameterized typ string: {}", baseTypeStr, typeStr, ex);
		}
		
		paramTypes = new Class<?>[paramTypeStr.length];
		
		for(int i = 0; i < paramTypes.length; i++)
		{
			try
			{
				paramTypes[i] = Class.forName(paramTypeStr[i]);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException("Invalid param-type '{}' specified in parameterized typ string: {}", paramTypeStr[i], typeStr, ex);
			}
		}
		
		if(Collection.class.isAssignableFrom(baseType) && paramTypes.length == 1)
		{
			return TypeFactory.defaultInstance().constructCollectionType( (Class) baseType, paramTypes[0]);
		}
		
		if(Map.class.isAssignableFrom(baseType) && paramTypes.length == 2)
		{
			return TypeFactory.defaultInstance().constructMapType( (Class) baseType, paramTypes[0], paramTypes[1]);
		}
		
		return TypeFactory.defaultInstance().constructParametricType(baseType, paramTypes);
	}

	/**
	 * Parses specified source (if string) and returns the result. If not string is specified, the 
	 * same will be returned.
	 * @param exeLogger Logger for logging messages
	 * @param source source to be passed
	 * @param defaultType Default type expected as result. Can be null.
	 * @return parsed value
	 */
	public static Object parseObjectSource(AutomationContext context, IExecutionLogger exeLogger, Object source, JavaType defaultType)
	{
		if(source instanceof IReference)
		{
			return ((IReference) source).getValue(context);
		}
		
		if(!(source instanceof String))
		{
			return source;
		}
		
		String sourceStr = (String) source;
		sourceStr = sourceStr.trim();
		
		//check if string is a reference
		String exprType = null;
		JavaType resultType = defaultType != null ? defaultType : TypeFactory.defaultInstance().constructSimpleType(Object.class, null);
		
		Matcher matcher = IAutomationConstants.EXPRESSION_PATTERN.matcher(sourceStr);
		Matcher matcherWithType = IAutomationConstants.EXPRESSION_WITH_PARAMS_PATTERN.matcher(sourceStr);
		
		if(matcher.find())
		{
			exprType = matcher.group("exprType");
			sourceStr = sourceStr.substring(matcher.end()).trim();
		}
		else if(matcherWithType.find())
		{
			exprType = matcherWithType.group("exprType");
			resultType = parseJavaType( matcherWithType.group("params") );
			sourceStr = sourceStr.substring(matcherWithType.end()).trim();
		}
		
		IResource resource = ResourceFactory.getResource(context, exeLogger, exprType, sourceStr);
		resource = ResourceFactory.parseExpressions(context, resource);
		
		String resourceName = resource.getName();
		
		if(resourceName != null && resourceName.toLowerCase().endsWith(".xml"))
		{
			Object rootBean = null;
			
			if(resultType != null && !Object.class.equals(resultType.getRawClass()))
			{
				try
				{
					rootBean = resultType.getRawClass().newInstance();
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while creating instance of {} [Raw type: {}]", resultType, resultType.getRawClass().getName(), ex);
				}
			}
			
			rootBean = XMLBeanParser.parse(resource.getInputStream(), rootBean);
			return rootBean;
		}

		try
		{
			Object value = IAutomationConstants.OBJECT_MAPPER.readValue(resource.getInputStream(), resultType);
			resource.close();
			
			return value;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An exception occurred while parsing json resource: " + resource.toText(), ex);
		}
	}
	
	/**
	 * Tries to delete specified folder. In case of exception 5 times it will tried to delete the folder
	 * with 2 seconds gap.
	 * @param reportFolder
	 * @throws IOException
	 */
	public static void deleteFolder(File reportFolder) throws IOException
	{
		if(!reportFolder.exists())
		{
			return;
		}
			
		
		for(int i = 0; ; i++)
		{
			try
			{
				FileUtils.forceDelete(reportFolder);
				break;
			}catch(IOException ex)
			{
				if(i < 5)
				{
					System.err.println("Ignored Error: " + ex);
					
					try
					{
						Thread.sleep(2000);
					}catch(Exception ex1)
					{}
					
				}
				else
				{
					//if folder is deleted, simply return
					if(!reportFolder.exists())
					{
						return;
					}
					
					throw ex;
				}
			}
		}
	}
	
	/**
	 * Converts original value into specified type. 
	 * @param originalValue value to be converted
	 * @param strType type to be converted. If null, original value will be retained.
	 * @return converted value
	 */
	public static Object convert(Object originalValue, String strType)
	{
		Class<?> type = null;
		
		if(strType != null)
		{
			type = CommonUtils.getClass(strType);
		}

		if(type != null)
		{
			return ConvertUtils.convert(originalValue, type);
		}

		return originalValue;
	}
	
	/**
	 * Checks whether specified child is really a child file/folder of specified parent.
	 *
	 * @param parent the parent file to check
	 * @param child the child file to check
	 * @return true, if specified child path is really child of specified parent path
	 */
	public static boolean isChild(File parent, File child)
	{
		try
		{
			Path parentPath = parent.getCanonicalFile().toPath();
			Path childPath = child.getCanonicalFile().toPath();
			
			return childPath.startsWith(parentPath);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while checking parent-child relationship of paths [Parent: %s, Child: %s]", 
					parent, child, ex);
		}
	}
	
	/**
	 * Converts specified value into writeable object recursively.
	 * @param value value to convert
	 * @return converted value.
	 */
	@SuppressWarnings("unchecked")
	public static Object convertToWriteable(Object value)
	{
		if(value instanceof Map)
		{
			Map<Object, Object> newMap = new LinkedHashMap<Object, Object>();
			Map<Object, Object> curMap = (Map<Object, Object>) value; 
			
			for(Object key : curMap.keySet())
			{
				Object entryValue = convertToWriteable(curMap.get(key));
				newMap.put(key, entryValue);
			}
			
			return newMap;
		}
		
		if(value instanceof List)
		{
			List<Object> newLst = new ArrayList<>();
			List<Object> curLst = (List<Object>) value;
			
			for(Object elem : curLst)
			{
				newLst.add( convertToWriteable(elem) );
			}
			
			return newLst;
		}
		
		return value;
	}

	public static boolean isReserveNamespace(String namespace)
	{
		if(namespace == null)
		{
			return false;
		}
		
		return XMLConstants.CCG_URI.equals(namespace)
				|| XMLConstants.NEW_CCG_URI.equals(namespace)
				|| IAutomationConstants.STEP_NAME_SPACE.equals(namespace);
	}
	
	/**
	 * Loads the specified content as object and returns the same. The way it is loaded based on extension specified in the
	 * name.
	 * 
	 * @param data
	 * @param name
	 * @param type
	 * @param logger
	 * @return
	 * @throws Exception
	 */
	public static Object loadObjectContent(String data, String name, Class<?> type, IExecutionLogger logger) throws Exception
	{
		InputStream is = new ByteArrayInputStream(data.getBytes());
		
		if(name.toLowerCase().endsWith(".properties"))
		{
			if(logger != null)
			{
				logger.debug("Processing input file as properties file: {}", name);
			}
			
			Properties prop = new Properties();
			prop.load(is);
			
			return new HashMap<>(prop);
		}
		
		if(name.toLowerCase().endsWith(".json"))
		{
			if(logger != null)
			{
				logger.debug("Processing input file as json file: {} [Type: {}]", name, type);
			}
			
			if(type == null)
			{
				type = Object.class;
			}
			
			Object res = AutomationUtils.convertToWriteable( objectMapper.readValue(is, type) );
			
			return res;
		}
		
		if(name.toLowerCase().endsWith(".jsonwithtype"))
		{
			if(logger != null)
			{
				logger.debug("Processing input file as jsonWithType file: {} [Type: {}]", name, type);
			}
			
			if(type == null)
			{
				type = Object.class;
			}
			
			Object res = AutomationUtils.convertToWriteable( objectMapperWithType.readValue(is, type) );
			
			return res;
		}

		if(name.toLowerCase().endsWith(".xml"))
		{
			if(logger != null)
			{
				logger.debug("Processing input file as xml file: {} [Type: {}]", name, type);
			}
			
			Object res = null;
			DefaultParserHandler parserHandler = null;
			
			if(type != null)
			{
				res = type.newInstance();
				parserHandler = new DefaultParserHandler();
			}
			else
			{
				parserHandler = new DynamicBeanParserHandler();
			}
			
			parserHandler.setExpressionEnabled(false);			
			parserHandler.registerReserveNodeHandler(new BeanReserveNodeHandler());
			parserHandler.registerReserveNodeHandler(new CloneReserveNodeHandler());
			
			res = XMLBeanParser.parse(is, res, parserHandler);
			
			if(res instanceof DynamicBean)
			{
				return ((DynamicBean) res).toSimpleMap();
			}
			
			return res;
		}
		
		throw new com.yukthitech.utils.exceptions.UnsupportedOperationException("Unsupported input specified for bean loading: '{}'", name);
	}
	
	/**
	 * Makes current thread for specified time.
	 * @param timeInMillis
	 */
	public static void sleep(long timeInMillis)
	{
		try
		{
			Thread.sleep(timeInMillis);
		}catch(InterruptedException ex)
		{
			throw new InvalidStateException("Thread was interrupted", ex);
		}
	}
	
	public static String toJson(Object obj)
	{
		try
		{
			return objectMapper.writeValueAsString(obj);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting object to json", ex);
		}
	}
	
	public static String toFormattedJson(Object obj)
	{
		try
		{
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting object to json", ex);
		}
	}

	public static String getTimeTaken(Date startTime, Date endTime)
	{
		if(startTime == null || endTime == null)
		{
			return null;
		}
		
		long diff = endTime.getTime() - startTime.getTime();
		
		long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
		diff = diff - hours * 3600_000;
		
		long minutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
		diff = diff - minutes * 60_000;
		
		long seconds = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);
		
		StringBuilder res = new StringBuilder();
		
		if(hours > 0)
		{
			res.append(hours).append(" Hr : ");
		}

		if(minutes > 0 || res.length() > 0)
		{
			res.append(minutes).append(" Min : ");
		}

		res.append(seconds).append(" Sec");
		return res.toString();
	}
	
	public static boolean equals(Object val1, Object val2)
	{
		//when two numbers of different data types has to be compared
		// like long and BigDecimal
		if(val1 != null && val2 != null && !val1.getClass().equals(val2.getClass()) 
				&& (val1 instanceof Number) && (val2 instanceof Number))
		{
			//compare them with their long value
			return ((Number) val1).longValue() == ((Number) val2).longValue();
		}
		
		//in all other cases follow standard comparison
		return Objects.equals(val1, val2);
	}

	public static String getStringValue(PrefixExpressionContext parserContext, String expression) throws Exception
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
}
