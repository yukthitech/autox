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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to access bean property in more efficient way.
 * 
 * @author akranthikiran
 */
public class PropertyAccessor
{
	/**
	 * Pattern for recognizing conditions within []
	 */
	private static Pattern CONDITION_PATTERN = Pattern.compile("^\\s*(.*)\\s*\\=\\s*(.*)\\s*$");
	
	/**
	 * Enumeration of path element types.
	 * @author akranthikiran
	 */
	private static enum PathElementType
	{
		PROPERTY, INDEX, KEY, CONDITION;
	}
	
	/**
	 * Represents a single element in property path.
	 * @author akranthikiran
	 */
	private static class PathElement
	{
		/**
		 * Path till parent element excluding current property.
		 */
		private String path;
		
		/**
		 * Path including current property.
		 */
		private String fullPath;
		
		/**
		 * Type of this element.
		 */
		private PathElementType type;
		
		/**
		 * Property name or index to be accessed on current value.
		 */
		private Object key;
		
		/**
		 * In case of conditions this will be populated with sub-prop path to be used on current object. 
		 */
		private List<PathElement> conditionPath;
		
		/**
		 * Value to be matched in condition.
		 */
		private String value;

		public PathElement(String path, String fullPath, PathElementType type, Object key)
		{
			this.path = path;
			this.fullPath = fullPath;
			this.type = type;
			this.key = key;
		}
		
		public static PathElement newCondition(String path, String fullPath, List<PathElement> conditionPath, String value)
		{
			PathElement pathElem = new PathElement(path, fullPath, PathElementType.CONDITION, null);
			pathElem.conditionPath = conditionPath;
			pathElem.value = value;
			
			return pathElem;
		}
		
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder("{");
			builder.append("Type: ").append(type);
			builder.append(", ").append("Path: ").append(path);
			
			if(conditionPath != null)
			{
				builder.append(", ").append("Cond Path: ").append(conditionPath);
				builder.append(", ").append("Value: ").append(value);
			}
			else
			{
				builder.append(", ").append("key: ").append(key);
			}
			
			builder.append("}");
			
			return builder.toString();
		}
	}
	
	/**
	 * Create a new element based on info specified into elements.
	 * @param path path at which element is being added
	 * @param fullPath path along with current element
	 * @param builder builder which represents current key
	 * @param expectedType element type expected to be created
	 * @param elements list to which created element to be added
	 * @return true if element addition is successful
	 */
	private static boolean addElement(String path, String fullPath, StringBuilder builder, PathElementType expectedType, List<PathElement> elements)
	{
		if(builder.length() == 0)
		{
			return false;
		}
		
		if(expectedType == PathElementType.PROPERTY || expectedType == PathElementType.KEY)
		{
			elements.add(new PathElement(path, fullPath, expectedType, builder.toString()));
			builder.setLength(0);
			return true;
		}
		
		//when expected type is index (specified in square brackets)
		try
		{
			String expr = builder.toString();
			
			//before parsing as index, check if current one is condition
			Matcher condMatcher = CONDITION_PATTERN.matcher(expr);
			
			if(condMatcher.matches())
			{
				List<PathElement> condPath = parse(condMatcher.group(1));
				elements.add(PathElement.newCondition(path, fullPath, condPath, condMatcher.group(2)));
				
				builder.setLength(0);
				return true;
			}
			
			//parse the value as index
			int idx = Integer.parseInt(expr);
			elements.add(new PathElement(path, fullPath, PathElementType.INDEX, idx));
			
			builder.setLength(0);
			return true;
		} catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid index '{}' specified for: {}", builder.toString(), path, ex);
		}
	}
	
	/**
	 * Collects the content between brackets recursively.
	 *
	 * @param chArr
	 *            the ch arr
	 * @param idx
	 *            the idx
	 * @param builder
	 *            the builder
	 * @param curPath
	 *            the cur path
	 * @param squareBracket
	 *            the square bracket
	 * @return the int
	 */
	private static int collectBracketContent(char chArr[], int idx, StringBuilder builder, String curPath, boolean squareBracket)
	{
		int len = chArr.length;
		int finalIdx = -1;
		int initialLen = builder.length();
		
		for(int i = idx; i < len; i++)
		{
			char ch = chArr[i];
			
			if(ch == ')')
			{
				if(!squareBracket)
				{
					finalIdx = i;
					break;
				}
				
				throw new InvalidArgumentException("For property {} at index {} encountered ')' when expecting ']'", curPath, i);
			}
			
			if(ch == ']')
			{
				if(squareBracket)
				{
					finalIdx = i;
					break;
				}
				
				throw new InvalidArgumentException("For property {} at index {} encountered ']' when expecting ')'", curPath, i);
			}
			
			//if sub bracket is found, extract the content recursively
			if(ch == '(')
			{
				builder.append('(');
				i = collectBracketContent(chArr, i + 1, builder, curPath, false);
				builder.append(')');
				continue;
			}

			//if sub bracket is found, extract the content recursively
			if(ch == '[')
			{
				builder.append('[');
				i = collectBracketContent(chArr, i + 1, builder, curPath, true);
				builder.append(']');
				continue;
			}
			
			builder.append(ch);
		}
		
		if(finalIdx == -1)
		{
			throw new InvalidArgumentException("For property {} no closing bracket '{}' found.", curPath, (squareBracket ? '[' : ')'));
		}
		
		if(initialLen == builder.length())
		{
			throw new InvalidArgumentException("For property {} no content found in brackets.", curPath);
		}
		
		return finalIdx;
	}
	
	/**
	 * Parses the given property path into path elements.
	 * @param path
	 * @return
	 */
	private static List<PathElement> parse(String path)
	{
		if(StringUtils.isBlank(path))
		{
			throw new InvalidArgumentException("Null or empty property path specified.");
		}
		
		path = path.trim();
		char chArr[] = path.toCharArray();
		StringBuilder builder = new StringBuilder();
		String curPath = "";
		
		List<PathElement> elements = new ArrayList<>();
		int len = chArr.length;
		String newPath  = null;
		
		for(int i = 0; i < len; i++)
		{
			char ch = chArr[i];
			
			if(ch == '(' || ch == '[')
			{
				newPath = new String(chArr, 0, i);
				addElement(curPath, newPath, builder, PathElementType.PROPERTY, elements);

				curPath = newPath;
				
				i = collectBracketContent(chArr, i + 1, builder, curPath, (ch == '['));
				newPath = new String(chArr, 0, i + 1);
				
				addElement(curPath, newPath, builder, 
						(ch == '[') ? PathElementType.INDEX : PathElementType.KEY, 
						elements);
				
				curPath = newPath;
				continue;
			}
			
			if(ch == '.')
			{
				newPath = new String(chArr, 0, i);
				addElement(curPath, newPath, builder, PathElementType.PROPERTY, elements);
				
				curPath = newPath;
				continue;
			}

			builder.append(ch);
		}
		
		//if any content is left over at end
		if(builder.length() > 0)
		{
			addElement(curPath, path, builder, PathElementType.PROPERTY, elements);
		}
		
		return elements;
	}
	
	/**
	 * Fetches the specified property value from specified bean.
	 * @param bean
	 * @param prop
	 * @return
	 */
	private static Object getBeanProperty(Object bean, String prop)
	{
		//handle special property - @this
		if("@this".equals(prop))
		{
			return bean;
		}
		
		try
		{
			return PropertyUtils.getProperty(bean, prop);
		}catch(IllegalAccessException | NoSuchMethodException | InvocationTargetException ex)
		{
			throw new InvalidStateException("An error occurred while fetching value of property '{}' from bean of type: {}", prop, bean.getClass().getName(), ex);
		}
	}
	
	/**
	 * Sets the specified property value on specified bean.
	 * @param bean
	 * @param prop
	 * @param value
	 */
	private static void setBeanProperty(Object bean, String prop, Object value)
	{
		try
		{
			PropertyUtils.setProperty(bean, prop, value);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while setting value of property '{}' on bean of type: {}", prop, bean.getClass().getName(), ex);
		}
	}

	/**
	 * Fetches the value from the bean using specified property path till specified index.
	 * @param bean bean from which value needs to be extracted
	 * @param path property path to be extracted
	 * @param tillIdx index till which evaluation needs to be done
	 * @param throwErrOnNull if true, throws exception if any null is found on the path
	 * @return matching value
	 */
	@SuppressWarnings("unchecked")
	private static Object getValue(Object bean, List<PathElement> path, int tillIdx, boolean throwErrOnNull)
	{
		Object curValue = bean;
		
		for(int i = 0; i < tillIdx; i++)
		{
			PathElement elem = path.get(i);
			
			switch(elem.type)
			{
				case PROPERTY:
				{
					curValue = getBeanProperty(curValue, (String) elem.key);
					break;
				}
				case KEY:
				{
					curValue = getBeanProperty(curValue, (String) elem.key);
					break;
				}
				case INDEX:
				{
					if(curValue instanceof List)
					{
						curValue = ((List<Object>) curValue).get((Integer) elem.key);
					}
					else
					{
						throw new InvalidArgumentException("Index is used on non-list value at: {}", elem.path);
					}
					
					break;
				}
				case CONDITION:
				{
					if(!(curValue instanceof Collection))
					{
						throw new InvalidArgumentException("Condition is used on non-collection value at: {}", elem.path);
					}
					
					Collection<Object> collection = (Collection<Object>) curValue;
					Object matchedValue = null;
					
					for(Object obj : collection)
					{
						Object condValue = getValue(obj, elem.conditionPath, elem.conditionPath.size(), false);
						
						if(Objects.equals("" + condValue, elem.value))
						{
							matchedValue = obj;
							break;
						}
					}

					curValue = matchedValue;
					break;
				}
			}
			
			if(curValue == null)
			{
				if(throwErrOnNull)
				{
					throw new NullPointerException(String.format("Property path '%s' resulted in null", elem.fullPath));
				}
				
				break;
			}
		}
	
		return curValue;
	}
	
	/**
	 * Fetches specified composite property from the specified bean.
	 * @param bean bean from which property to be fetched
	 * @param property property path to fetch
	 * @return property value
	 */
	public static Object getProperty(Object bean, String property)
	{
		List<PathElement> pathElemLst = parse(property);
		return getValue(bean, pathElemLst, pathElemLst.size(), false);
	}

	/**
	 * Fetches specified composite property from the specified bean.
	 * @param bean bean from which property to be fetched
	 * @param property property path to fetch
	 * @param throwErrorOnNull if true, any null occurs on the path, NullPointerException is thrown indicating the path
	 * @return property value
	 */
	public static Object getProperty(Object bean, String property, boolean throwErrorOnNull)
	{
		List<PathElement> pathElemLst = parse(property);
		return getValue(bean, pathElemLst, pathElemLst.size(), throwErrorOnNull);
	}

	/**
	 * Sets the specified composite property on specified bean.
	 * @param bean bean on which property to be set
	 * @param property property path to set
	 * @param value value to set
	 * @param addElement if true, on the list property instead of using set, add will be used.
	 */
	@SuppressWarnings("unchecked")
	private static void setProperty(Object bean, String property, Object value, boolean addElement)
	{
		List<PathElement> pathElemLst = parse(property);
		
		Object parent = getValue(bean, pathElemLst, pathElemLst.size() - 1, true);
		
		PathElement lastElem = pathElemLst.get(pathElemLst.size() - 1);
		
		switch(lastElem.type)
		{
			case PROPERTY:
			{
				setBeanProperty(parent, (String) lastElem.key, value);
				break;
			}
			case KEY:
			{
				setBeanProperty(parent, (String) lastElem.key, value);
				break;
			}
			case INDEX:
			{
				int idx = (Integer) lastElem.key;
				
				if(idx < 0)
				{
					if(!(parent instanceof Collection))
					{
						throw new InvalidArgumentException("Index is used on non-collection value at: {}", lastElem.path);
					}
					
					((Collection<Object>) parent).add(value);
					return;
				}
				
				
				if(!(parent instanceof List))
				{
					throw new InvalidArgumentException("Index is used on non-list value at: {}", lastElem.path);
				}
				
				if(addElement)
				{
					((List<Object>) parent).add(idx, value);
				}
				//if set has to be done
				else
				{
					((List<Object>) parent).set(idx, value);
				}
				
				break;
			}
			case CONDITION:
			{
				throw new InvalidArgumentException("Condition is used as last property to set property: {}", lastElem.fullPath);
			}
		}
	}

	/**
	 * Sets the specified composite property on specified bean. If this property indicates list property at end, set will be used.
	 * @param bean bean on which property to be set
	 * @param property property path to set
	 * @param value value to set
	 */
	public static void setProperty(Object bean, String property, Object value)
	{
		setProperty(bean, property, value, false);
	}

	/**
	 * Sets the specified composite property on specified bean. If this property indicates list property at end, add will be used.
	 * @param bean bean on which property to be set
	 * @param property property path to set
	 * @param value value to set
	 */
	public static void addProperty(Object bean, String property, Object value)
	{
		setProperty(bean, property, value, true);
	}

	/**
	 * Removes the specified property from specified bean.
	 * @param bean bean from which property needs to be removed.
	 * @param property property to be removed.
	 */
	@SuppressWarnings("unchecked")
	public static void removeProperty(Object bean, String property)
	{
		List<PathElement> pathElemLst = parse(property);
		
		Object parent = getValue(bean, pathElemLst, pathElemLst.size() - 1, true);
		
		PathElement lastElem = pathElemLst.get(pathElemLst.size() - 1);
		
		switch(lastElem.type)
		{
			case PROPERTY:
			{
				if(parent instanceof Map)
				{
					((Map<Object, Object>) parent).remove(lastElem.key);
				}
				else
				{
					setBeanProperty(parent, (String) lastElem.key, null);
				}
				
				break;
			}
			case KEY:
			{
				if(parent instanceof Map)
				{
					((Map<Object, Object>) parent).remove(lastElem.key);
				}
				else
				{
					setProperty(parent, (String) lastElem.key, null);
				}
				
				break;
			}
			case INDEX:
			{
				if(!(parent instanceof List))
				{
					throw new InvalidArgumentException("Index is used on non-list value at: {}", lastElem.path);
				}
				
				int idx = (Integer) lastElem.key;
				((List<Object>) parent).remove(idx);
				
				break;
			}
			case CONDITION:
			{
				if(!(parent instanceof Collection))
				{
					throw new InvalidArgumentException("Condition is used on non-collection value at: {}", lastElem.path);
				}
				
				Collection<Object> collection = (Collection<Object>) parent;
				Iterator<Object> it = collection.iterator();
				
				while(it.hasNext())
				{
					Object obj = it.next();
					Object condValue = getValue(obj, lastElem.conditionPath, lastElem.conditionPath.size(), false);
					
					if(Objects.equals("" + condValue, lastElem.value))
					{
						it.remove();
						break;
					}
				}
			}
		}
	}
}
