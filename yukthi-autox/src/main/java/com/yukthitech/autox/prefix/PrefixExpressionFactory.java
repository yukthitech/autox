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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import com.yukthitech.autox.common.AutoxInfoException;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.common.IndexRange;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.DummyExecutionLogger;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.CustomPrefixExpression;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Factory for parsing expressions.
 * @author akiran
 */
public class PrefixExpressionFactory
{
	private static Logger logger = LogManager.getLogger(PrefixExpressionFactory.class);
	
	/**
	 * Singleton instance.
	 */
	private static PrefixExpressionFactory expressionFactory;
	
	/**
	 * Available expression parsers.
	 */
	private Map<String, PrefixExpressionDetails> prefixExpressions = new HashMap<>();
	
	private Map<String, CustomPrefixExpression> customPrefixExpressions = new HashMap<>();
	
	private Map<String, CustomPrefixExpression> customUiLocators = new HashMap<>();

	private PrefixExpressionFactory()
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
		
		PrefixExpressionFactory factory = new PrefixExpressionFactory();

		Reflections reflections = null;
		Set<Method> parserMethods = null;
		
		Map<Class<?>, Object> parserClasses = new HashMap<>();

		for(String pack : basePackages)
		{
			logger.debug("Scanning for expressions parser methods in package - {}", pack);
			reflections = new Reflections(
					ConfigurationBuilder.build(pack, Scanners.MethodsAnnotated)
				);
			
			parserMethods = reflections.getMethodsAnnotatedWith(PrefixExpressionAnnot.class);
			
			if(parserMethods != null)
			{
				Object parserObj = null;
				PrefixExpressionAnnot parserAnnot = null;
				PrefixExpressionDetails parserDet = null;
				Class<?> paramTypes[] = null;
				
				for(Method method : parserMethods)
				{
					paramTypes = method.getParameterTypes();
					
					if(paramTypes.length == 2)
					{
						if(!PrefixExpressionContext.class.equals(paramTypes[0]) || !String.class.equals(paramTypes[1]))
						{
							throw new InvalidStateException("Invalid arguments specified for expression parser method: {}.{}", method.getDeclaringClass().getName(), method.getName());
						}
					}
					else if(paramTypes.length == 3)
					{
						if(!PrefixExpressionContext.class.equals(paramTypes[0]) || !String.class.equals(paramTypes[1]) || !String[].class.equals(paramTypes[2]))
						{
							throw new InvalidStateException("Invalid arguments specified for expression parser method: {}.{}", method.getDeclaringClass().getName(), method.getName());
						}
					} 
					else
					{
						throw new InvalidStateException("Invalid arguments specified for expression parser method: {}.{}", method.getDeclaringClass().getName(), method.getName());
					}
					
					if(!PrefixEpression.class.equals(method.getReturnType()))
					{
						throw new InvalidStateException("Expression parser method is not returning property path: {}.{}", method.getDeclaringClass().getName(), method.getName());
					}
					
					parserAnnot = method.getAnnotation(PrefixExpressionAnnot.class);
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
					
					parserDet = new PrefixExpressionDetails(parserAnnot.type(), parserAnnot.description(), 
							parserAnnot.example(), parserObj, method, parserAnnot.contentType());
					
					for(PrefixExprParam paramAnnot : parserAnnot.params())
					{
						parserDet.addParam(new PrefixExpressionDetails.Param(paramAnnot.name(), paramAnnot.type(), 
								paramAnnot.defaultValue(), paramAnnot.required(), paramAnnot.description()));
					}
					
					factory.prefixExpressions.put(parserDet.getType(), parserDet);
				}
			}
		}
		
		logger.debug("Found expression parsers to be: {}", factory.prefixExpressions);
		PrefixExpressionFactory.expressionFactory = factory;
	}
	
	public static PrefixExpressionFactory getExpressionFactory()
	{
		return expressionFactory;
	}
	
	public void addCustomPrefixExpression(CustomPrefixExpression expression)
	{
		CustomPrefixExpression existing = customPrefixExpressions.get(expression.getName());
		
		if(existing != null)
		{
			throw new InvalidStateException("Multiple custom-prefix-expressions defined with same name: {} [Location 1: {}, Location 2: {}]", 
					expression.getName(), existing.getLocation().getName(), existing.getLineNumber(), 
					expression.getLocation().getName(), expression.getLineNumber());
		}
		
		this.customPrefixExpressions.put(expression.getName(), expression);
	}
	
	public void addOrReplacePrefixExpression(CustomPrefixExpression expression)
	{
		this.customPrefixExpressions.put(expression.getName(), expression);
	}

	public void addCustomUiLocator(CustomPrefixExpression expression)
	{
		CustomPrefixExpression existing = customUiLocators.get(expression.getName());
		
		if(existing != null)
		{
			throw new InvalidStateException("Multiple custom-ui-locators defined with same name: {} [Location 1: {}, Location 2: {}]", 
					expression.getName(), existing.getLocation().getName(), existing.getLineNumber(), 
					expression.getLocation().getName(), expression.getLineNumber());
		}
		
		this.customUiLocators.put(expression.getName(), expression);
	}

	public void addOrReplaceUiLocator(CustomPrefixExpression expression)
	{
		this.customUiLocators.put(expression.getName(), expression);
	}

	public static List<ExpressionToken> parseExpressionTokens(String expression)
	{
		//Parse the expression into tokens delimited by '|'
		List<ExpressionToken> lst = new ArrayList<>();
		char ch[] = expression.toCharArray();
		StringBuilder token = new StringBuilder();
		int start = 0;
		
		for(int i = 0; i < ch.length; i++)
		{
			if(ch[i] == '\\')
			{
				if(i < ch.length - 1 && ch[i + 1] == '|')
				{
					start = i;
					token.append('|');
					i++;
					continue;
				}
			}
			
			if(ch[i] == '|')
			{
				if(token.length() > 0)
				{
					lst.add(new ExpressionToken(token.toString(), start, i - 1));
				}
				
				token.setLength(0);
				continue;
			}
			
			start = i;
			token.append(ch[i]);
		}
		
		if(token.length() > 0)
		{
			lst.add(new ExpressionToken(token.toString(), start, token.length() - 1));
		}

		return lst;
	}
	
	public Object getValueByExpressionString(AutomationContext context, String expression)
	{
		//if no prefix is used, by default use expr prefix
		if(!isExpression(expression))
		{
			expression = "expr: " + expression;
		}

		return getValueByExpression(context, expression, null);
	}

	public Object getValueByExpression(AutomationContext context, Object expressionObj)
	{
		return getValueByExpression(context, expressionObj, null);
	}
	
	public Object getValueByExpression(AutomationContext context, Object expressionObj, Object initValue)
	{
		return getValueByExpression(context, expressionObj, new ExpressionConfig(initValue, null));
	}
	
	public Object getValueByExpression(AutomationContext context, Object expressionObj, ExpressionConfig exprConfig)
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
		PrefixExpressionContext expressionParserContext = new PrefixExpressionContext(context, ((exprConfig != null) ? exprConfig.getInitValue() : null));
		ObjectWrapper<Boolean> expressionParsed = new ObjectWrapper<Boolean>(true);
		
		if(exprConfig != null)
		{
			expressionParserContext.setDefaultExpressionType(exprConfig.getDefaultExpectedType());
		}
		
		List<ExpressionToken> lst = parseExpressionTokens(expression);
		
		//convert tokens into objects
		for(ExpressionToken exprToken : lst)
		{
			result = getValueOfSingleExpression(expressionParserContext, exprToken.getValue(), expressionParsed);
			
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
		
		return (matcher.find() || matcherWithType.find());
	}
	
	/*
	public static String getExpressionType(String str)
	{
		str = str.trim();

		Matcher matcher = IAutomationConstants.EXPRESSION_PATTERN.matcher(str);
		Matcher matcherWithParams = IAutomationConstants.EXPRESSION_WITH_PARAMS_PATTERN.matcher(str);
		
		String custPrefix = null;
		String exprType = null;
		
		if(matcher.find())
		{
			custPrefix = matcher.group("custom");
			exprType = matcher.group("exprType");
		}

		if(matcherWithParams.find())
		{
			custPrefix = matcherWithParams.group("custom");
			exprType = matcherWithParams.group("exprType");
		}
		
		if(custPrefix != null)
		{
			return custPrefix;
		}
		
		return exprType;
	}
	*/
	
	public List<PrefixEpression> parseExpressionList(AutomationContext context, String expression, Function<String, CustomPrefixExpression> customPrefixProvider)
	{
		List<ExpressionToken> lst = parseExpressionTokens(expression);
		List<PrefixEpression> exprLst = new ArrayList<>(lst.size());
		PrefixExpressionContext expressionParserContext = new PrefixExpressionContext(context, null);

		for(ExpressionToken tokenExpr : lst)
		{
			PrefixEpression exprPath = parseExpressionPath(expressionParserContext, tokenExpr.getValue(), customPrefixProvider);
			
			//when valid expression path is not found
			if(exprPath == null)
			{
				return null;
			}
			
			exprPath.setIndexRange(new IndexRange(tokenExpr.getStartIndex(), tokenExpr.getEndIndex()));
			exprLst.add(exprPath);
		}
		
		return exprLst;
	}

	private PrefixEpression parseExpressionPath(PrefixExpressionContext context, String expression, Function<String, CustomPrefixExpression> customPrefixProvider)
	{
		AutomationContext automationContext = context.getAutomationContext();
		IExecutionLogger exeLogger = automationContext == null ? DummyExecutionLogger.getInstance() : automationContext.getExecutionLogger();
		
		exeLogger.debug("Fetching expression parser for value and parsing expression: {}", expression);
		
		context.clearParameters();
		
		//check if string is a reference
		String prefix = null, mainExpr = null;
		String exprTypeParams[] = null;
		
		Matcher matcher = IAutomationConstants.EXPRESSION_PATTERN.matcher(expression);
		Matcher matcherWithType = IAutomationConstants.EXPRESSION_WITH_PARAMS_PATTERN.matcher(expression);
		boolean customExpr = false;
		
		IndexRange prefixRange = null;
		IndexRange valueRange = null;
		IndexRange paramRange = null;
		
		if(matcher.find())
		{
			prefix = matcher.group("exprType");
			mainExpr = expression.substring(matcher.end());
			
			prefixRange = new IndexRange(matcher.start("exprType"), matcher.end("exprType"));
			valueRange = new IndexRange(matcher.end(), expression.length() - 1);
			
			customExpr = StringUtils.isNotBlank(matcher.group("custom"));
		}
		else if(matcherWithType.find())
		{
			prefix = matcherWithType.group("exprType");
			mainExpr = expression.substring(matcherWithType.end()).trim();
			
			valueRange = new IndexRange(matcherWithType.end(), expression.length() - 1);
			paramRange = new IndexRange(matcherWithType.start("params"), matcherWithType.end("params"));;

			customExpr = StringUtils.isNotBlank(matcherWithType.group("custom"));
			
			if(customExpr)
			{
				prefixRange = new IndexRange(matcherWithType.start("custom"), matcherWithType.end("custom"));
			}
			else
			{
				prefixRange = new IndexRange(matcherWithType.start("exprType"), matcherWithType.end("exprType"));				
			}
			
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
		
		PrefixEpression prefixExpr = null;
		
		if(customExpr)
		{
			CustomPrefixExpression customPrefixExpression = customPrefixProvider != null ? 
					customPrefixProvider.apply(prefix) : customPrefixExpressions.get(prefix);
			
			if(customPrefixExpression == null)
			{
				throw new InvalidStateException("No custom prefix expression found with name: {}", prefix);
			}
			
			prefixExpr = new CustomPrefixExpressionWrapper(customPrefixExpression, context, mainExpr);
		}
		else
		{
			PrefixExpressionDetails parser = prefixExpressions.get(prefix);
			
			if(parser == null)
			{
				throw new InvalidArgumentException("Invalid expression type '{}' specified in expression: {}", prefix, expression);
			}
			
			if(exprTypeParams == null && context.getDefaultExpressionType() != null)
			{
				exprTypeParams = new String[] {context.getDefaultExpressionType().getName()};
			}
			
			context.setCurrentExpression(parser);
			prefixExpr = parser.invoke(context, mainExpr, exprTypeParams);
		}
		
		prefixExpr.setPrefix(prefix);
		prefixExpr.setPrefixIndexRange(prefixRange);
		prefixExpr.setParamIndexRange(paramRange);
		prefixExpr.setValueIndexRange(valueRange);
		
		return prefixExpr;
	}
	
	public PrefixEpression parseCustomUiLocator(String expression)
	{
		AutomationContext automationContext = AutomationContext.getInstance();
		PrefixExpressionContext context = new PrefixExpressionContext(automationContext, null);
		IExecutionLogger exeLogger = automationContext.getExecutionLogger();
		
		exeLogger.debug("Fetching expression parser for value and parsing expression: {}", expression);
		
		//check if string is a reference
		String exprType = null, mainExpr = null;
		String exprTypeParams[] = null;
		
		Matcher matcher = IAutomationConstants.EXPRESSION_PATTERN.matcher(expression);
		Matcher matcherWithType = IAutomationConstants.EXPRESSION_WITH_PARAMS_PATTERN.matcher(expression);
		boolean customExpr = false;
		
		if(matcher.find())
		{
			customExpr = StringUtils.isNotBlank(matcher.group("custom"));
			
			if(!customExpr)
			{
				return null;
			}
			
			exprType = matcher.group("exprType");
			mainExpr = expression.substring(matcher.end()).trim();
		}
		else if(matcherWithType.find())
		{
			customExpr = StringUtils.isNotBlank(matcher.group("custom"));

			if(!customExpr)
			{
				return null;
			}
			
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
		
		CustomPrefixExpression customUiLocator = customUiLocators.get(exprType);
		
		if(customUiLocator == null)
		{
			throw new InvalidStateException("No custom ui locator found with name: {}", exprType);
		}
		
		return new CustomPrefixExpressionWrapper(customUiLocator, context, mainExpr);
	}

	private Object getValueOfSingleExpression(PrefixExpressionContext context, String expression, ObjectWrapper<Boolean> expressionParsed)
	{
		PrefixEpression propPath = parseExpressionPath(context, expression, null);
		
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
			PrefixExpressionDetails parser = context.getCurrentExpression();
			String exprTypeParams[] = context.getExpressionTypeParameters();
			
			exeLogger.debug("Execution of property expression '{}' resulted in: {} [Type: {}]", expression, result, (result != null ? result.getClass().getName() : "") );
	
			if(parser != null && !parser.isConversionHandled() && exprTypeParams != null)
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
		PrefixExpressionContext expressionParserContext = new PrefixExpressionContext(context, effectiveContext);
		PrefixEpression propertyPath = parseExpressionPath(expressionParserContext, expression, null);
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
		PrefixExpressionContext expressionParserContext = new PrefixExpressionContext(context, effectiveContext);
		PrefixEpression propertyPath = parseExpressionPath(expressionParserContext, expression, null);
		IExecutionLogger exeLogger = context.getExecutionLogger();
		
		if(propertyPath == null)
		{
			exeLogger.debug("Setting attribute '{}' as value: {}", expression, value);
			context.setAttribute(expression, value);
			return;
		}
		
		exeLogger.debug("Setting expression '{}' as value: {}", expression, value);
		
		PrefixExpressionDetails parser = expressionParserContext.getCurrentExpression();
		String exprTypeParams[] = expressionParserContext.getExpressionTypeParameters();

		if(parser != null && !parser.isConversionHandled() && exprTypeParams != null)
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
	
	public Collection<PrefixExpressionDetails> getParserDetails()
	{
		return prefixExpressions.values();
	}
}
