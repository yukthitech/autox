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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import com.yukthitech.autox.common.AutoxInfoException;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Factory for parsing expressions.
 * @author akiran
 */
public class ExpressionFactory
{
	private static Logger logger = LogManager.getLogger(ExpressionFactory.class);
	
	/**
	 * Singleton instance.
	 */
	private static ExpressionFactory expressionFactory;
	
	/**
	 * Available expression parsers.
	 */
	private Map<String, ExpressionParserDetails> parsers = new HashMap<>();
	
	private ExpressionFactory()
	{}
	
	/**
	 * Gets the expression factory.
	 *
	 * @return the expression factory
	 */
	public static void init(ClassLoader classLoader, Set<String> basePackagesAct)
	{
		if(classLoader == null)
		{
			classLoader = FreeMarkerMethodManager.class.getClassLoader();
		}
		
		Set<String> basePackages = new HashSet<>();
		
		if(basePackagesAct != null)
		{
			basePackages.addAll(basePackagesAct);
		}
		
		basePackages.add("com.yukthitech");
		
		ExpressionFactory factory = new ExpressionFactory();

		Reflections reflections = null;
		Set<Method> parserMethods = null;
		
		Map<Class<?>, Object> parserClasses = new HashMap<>();

		for(String pack : basePackages)
		{
			logger.debug("Scanning for expressions parser methods in package - {}", pack);
			reflections = new Reflections(
					ConfigurationBuilder.build(pack, Scanners.MethodsAnnotated)
				);
			
			parserMethods = reflections.getMethodsAnnotatedWith(ExpressionFilter.class);
			
			if(parserMethods != null)
			{
				Object parserObj = null;
				ExpressionFilter parserAnnot = null;
				ExpressionParserDetails parserDet = null;
				Class<?> paramTypes[] = null;
				
				for(Method method : parserMethods)
				{
					paramTypes = method.getParameterTypes();
					
					if(paramTypes.length == 2)
					{
						if(!FilterContext.class.equals(paramTypes[0]) || !String.class.equals(paramTypes[1]))
						{
							throw new InvalidStateException("Invalid arguments specified for expression parser method: {}.{}", method.getDeclaringClass().getName(), method.getName());
						}
					}
					else if(paramTypes.length == 3)
					{
						if(!FilterContext.class.equals(paramTypes[0]) || !String.class.equals(paramTypes[1]) || !String[].class.equals(paramTypes[2]))
						{
							throw new InvalidStateException("Invalid arguments specified for expression parser method: {}.{}", method.getDeclaringClass().getName(), method.getName());
						}
					} 
					else
					{
						throw new InvalidStateException("Invalid arguments specified for expression parser method: {}.{}", method.getDeclaringClass().getName(), method.getName());
					}
					
					if(!IPropertyPath.class.equals(method.getReturnType()))
					{
						throw new InvalidStateException("Expression parser method is not returning property path: {}.{}", method.getDeclaringClass().getName(), method.getName());
					}
					
					parserAnnot = method.getAnnotation(ExpressionFilter.class);
					parserObj = parserClasses.get(method.getDeclaringClass());
					
					if(parserObj == null)
					{
						try
						{
							parserObj = method.getDeclaringClass().newInstance();
						}catch(Exception ex)
						{
							throw new InvalidStateException("An error occurred while creating instance of expression parser enclosing object of type: {}", method.getDeclaringClass().getName(), ex);
						}
						
						parserClasses.put(method.getDeclaringClass(), parserObj);
					}
					
					parserDet = new ExpressionParserDetails(parserAnnot.type(), parserAnnot.description(), 
							parserAnnot.example(), parserObj, method, parserAnnot.contentType());
					
					for(ParserParam paramAnnot : parserAnnot.params())
					{
						parserDet.addParam(new ExpressionParserDetails.Param(paramAnnot.name(), paramAnnot.type(), 
								paramAnnot.defaultValue(), paramAnnot.description()));
					}
					
					factory.parsers.put(parserDet.getType(), parserDet);
				}
			}
		}
		
		logger.debug("Found expression parsers to be: {}", factory.parsers);
		ExpressionFactory.expressionFactory = factory;
	}
	
	public static ExpressionFactory getExpressionFactory()
	{
		return expressionFactory;
	}
	
	public static List<String> parseExpressionTokens(String expression)
	{
		//Parse the expression into tokens delimited by '|'
		List<String> lst = new ArrayList<String>();
		char ch[] = expression.toCharArray();
		StringBuilder token = new StringBuilder();
		
		for(int i = 0; i < ch.length; i++)
		{
			if(ch[i] == '\\')
			{
				if(i < ch.length - 1 && ch[i + 1] == '|')
				{
					token.append('|');
					i++;
					continue;
				}
			}
			
			if(ch[i] == '|')
			{
				if(token.length() > 0)
				{
					lst.add(token.toString());
				}
				
				token.setLength(0);
				continue;
			}
			
			token.append(ch[i]);
		}
		
		if(token.length() > 0)
		{
			lst.add(token.toString());
		}

		return lst;
	}
	
	public Object parseExpression(AutomationContext context, Object expressionObj)
	{
		return parseExpression(context, expressionObj, null);
	}
	
	public Object parseExpression(AutomationContext context, Object expressionObj, Object initValue)
	{
		return parseExpression(context, expressionObj, new ExpressionConfig(initValue, null));
	}
	
	public Object parseExpression(AutomationContext context, Object expressionObj, ExpressionConfig exprConfig)
	{
		if(!(expressionObj instanceof String))
		{
			return expressionObj;
		}
		
		String expression = (String) expressionObj;
		
		if(expression.trim().length() == 0)
		{
			return expression;
		}
			
		
		Object result = null;
		FilterContext expressionParserContext = new FilterContext(context, ((exprConfig != null) ? exprConfig.getInitValue() : null));
		ObjectWrapper<Boolean> expressionParsed = new ObjectWrapper<Boolean>(true);
		
		if(exprConfig != null)
		{
			expressionParserContext.setDefaultExpressionType(exprConfig.getDefaultExpectedType());
		}
		
		List<String> lst = parseExpressionTokens(expression);
		
		//convert tokens into objects
		for(String tokenStr : lst)
		{
			result = parseSingleExpression(expressionParserContext, tokenStr, expressionParsed);
			
			if(!expressionParsed.getValue())
			{
				return expressionObj;
			}
			
			if(result == null)
			{
				return null;
			}
			
			expressionParserContext.setCurrentValue(result);
		}
		
		return result;
	}
	
	public static boolean isExpression(String str)
	{
		str = str.trim();

		Matcher matcher = IAutomationConstants.EXPRESSION_PATTERN.matcher(str);
		Matcher matcherWithType = IAutomationConstants.EXPRESSION_WITH_PARAMS_PATTERN.matcher(str);
		
		return (matcher.matches() || matcherWithType.matches());
	}
	
	private IPropertyPath getPropertyPath(FilterContext context, String expression)
	{
		IExecutionLogger exeLogger = context.getAutomationContext().getExecutionLogger();
		
		exeLogger.debug("Fetching expression parser for value and parsing expression: {}", expression);
		
		context.clearParameters();
		
		//check if string is a reference
		String exprType = null, mainExpr = null;
		String exprTypeParams[] = null;
		
		Matcher matcher = IAutomationConstants.EXPRESSION_PATTERN.matcher(expression);
		Matcher matcherWithType = IAutomationConstants.EXPRESSION_WITH_PARAMS_PATTERN.matcher(expression);
		
		if(matcher.find())
		{
			exprType = matcher.group("exprType");
			mainExpr = expression.substring(matcher.end()).trim();
		}
		else if(matcherWithType.find())
		{
			exprType = matcherWithType.group("exprType");
			mainExpr = expression.substring(matcherWithType.end()).trim();
			
			String type = matcherWithType.group("params");
			exprTypeParams = type.trim().split("\\s*\\,\\s*");
			
			//parse parameter types
			List<String> typeParams = new ArrayList<>();
			Matcher keyValMatcher = null;
			
			for(String param : exprTypeParams)
			{
				keyValMatcher = IAutomationConstants.KEY_VALUE_PATTERN.matcher(param);
				
				if(keyValMatcher.matches())
				{
					context.addParameter(keyValMatcher.group("key"), keyValMatcher.group("value"));
				}
				else
				{
					typeParams.add(param);
				}
			}
			
			//Expression type params are params, which are specified in non-key-value format
			exprTypeParams = typeParams.isEmpty() ? null : typeParams.toArray(new String[0]);
		}
		else
		{
			return null;
		}
		
		ExpressionParserDetails parser = parsers.get(exprType);
		
		if(parser == null)
		{
			throw new InvalidArgumentException("Invalid expression type '{}' specified in expression: {}", exprType, expression);
		}
		
		if(exprTypeParams == null && context.getDefaultExpressionType() != null)
		{
			exprTypeParams = new String[] {context.getDefaultExpressionType().getName()};
		}
		
		context.setCurrentParser(parser);
		return parser.invoke(context, mainExpr, exprTypeParams);
	}
	
	private Object parseSingleExpression(FilterContext context, String expression, ObjectWrapper<Boolean> expressionParsed)
	{
		IPropertyPath propPath = getPropertyPath(context, expression);
		
		if(propPath == null)
		{
			expressionParsed.setValue(false);
			return expression;
		}
		
		IExecutionLogger exeLogger = context.getAutomationContext().getExecutionLogger();
		logger.debug("Executing expression: {}", expression);
		
		try
		{
			Object result = propPath.getValue();
			ExpressionParserDetails parser = context.getCurrentParser();
			String exprTypeParams[] = context.getExpressionTypeParameters();
			
			exeLogger.debug("Execution of property expression '{}' resulted in: {} [Type: {}]", expression, result, (result != null ? result.getClass().getName() : "") );
	
			if(!parser.isConversionHandled() && exprTypeParams != null)
			{
				if(exprTypeParams.length > 0)
				{
					throw new InvalidArgumentException("Multiple result type parameters specified in expression: {}", expression);
				}
				
				Class<?> resultType = null;
				
				try
				{
					resultType = CommonUtils.getClass(exprTypeParams[0]);
				}catch(Exception ex)
				{
					throw new InvalidArgumentException("Invalid result type '{}' specified in expression: {}", exprTypeParams[0], expression, ex);
				}
	
				if(resultType != null)
				{
					result = ConvertUtils.convert(result, resultType);
				}
			}
			
			return result;
		}catch(Exception ex)
		{
			exeLogger.error("Evaluation of expression {} resulted in error: \n{}", expression, CommonUtils.getRootCauseMessages(ex));
			
			if(ex instanceof AutoxInfoException)
			{
				throw (AutoxInfoException) ex;
			}
			
			throw new InvalidStateException("An error occurred while evaluating expression '{}'", expression, ex);
		}
	}
	
	public void removeByExpression(AutomationContext context, String expression)
	{
		this.removeByExpression(context, expression, null);
	}
	
	/**
	 * Removes specified property value.
	 * @param context current context
	 * @param expression expression based on which removal should happen
	 * @param effectiveContext if specified, removal operation will be performed on this object instead of context.
	 */
	public void removeByExpression(AutomationContext context, String expression, Object effectiveContext)
	{
		FilterContext expressionParserContext = new FilterContext(context, effectiveContext);
		IPropertyPath propertyPath = getPropertyPath(expressionParserContext, expression);
		IExecutionLogger exeLogger = context.getExecutionLogger();
		
		if(propertyPath == null)
		{
			exeLogger.debug("Removing attribute with name'{}'", expression);
			context.removeAttribute(expression);
			return;
		}
		
		exeLogger.debug("Removing expression '{}'", expression);
		
		try
		{
			propertyPath.removeValue();
		}catch(Exception ex)
		{
			exeLogger.error("Failed to remove using expression - {}", expression, ex);
			throw new InvalidStateException("Failed to remove using expression - {}", expression, ex);
		}
	}
	
	public void setExpressionValue(AutomationContext context, String expression, Object value)
	{
		this.setExpressionValue(context, expression, value, null);
	}
	
	/**
	 * Sets the expression value.
	 * @param context current context
	 * @param expression expression based on which value need to be set
	 * @param value value need to be set
	 * @param effectiveContext if specified, instead of using context, expression will be executed on this object
	 */
	public void setExpressionValue(AutomationContext context, String expression, Object value, Object effectiveContext)
	{
		FilterContext expressionParserContext = new FilterContext(context, effectiveContext);
		IPropertyPath propertyPath = getPropertyPath(expressionParserContext, expression);
		IExecutionLogger exeLogger = context.getExecutionLogger();
		
		if(propertyPath == null)
		{
			exeLogger.debug("Setting attribute '{}' as value: {}", expression, value);
			context.setAttribute(expression, value);
			return;
		}
		
		exeLogger.debug("Setting expression '{}' as value: {}", expression, value);
		
		ExpressionParserDetails parser = expressionParserContext.getCurrentParser();
		String exprTypeParams[] = expressionParserContext.getExpressionTypeParameters();

		if(!parser.isConversionHandled() && exprTypeParams != null)
		{
			if(exprTypeParams.length > 0)
			{
				throw new InvalidArgumentException("Multiple result type parameters specified in expression: {}", expression);
			}
			
			Class<?> valueType = null;
			
			try
			{
				valueType = CommonUtils.getClass(exprTypeParams[0]);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException("Invalid result type '{}' specified in expression: {}", exprTypeParams[0], expression, ex);
			}

			if(valueType != null)
			{
				value = ConvertUtils.convert(value, valueType);
			}
		}

		try
		{
			propertyPath.setValue(value);
		}catch(Exception ex)
		{
			exeLogger.error("Failed to set specified value {} on path {}", value, expression, ex);
			throw new InvalidStateException("Failed to set specified value {} on path {}", value, expression, ex);
		}
	}
	
	public Collection<ExpressionParserDetails> getParserDetails()
	{
		return parsers.values();
	}
}
