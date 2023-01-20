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
package com.yukthitech.autox.ide.xmlfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.index.FileParseCollector;
import com.yukthitech.ccg.xml.XMLBeanParser;

/**
 * Manages rules related to text parsing.
 * @author akranthikiran
 */
public class TextParsingRuleManager
{
	private static Logger logger = LogManager.getLogger(TextParsingRuleManager.class);
	
	private static TextParsingRuleManager instance = new TextParsingRuleManager();
	
	/**
	 * Represents rule for error/warning.
	 * @author akranthikiran
	 */
	public static class Rule
	{
		/**
		 * Source type to which this rule is applicable.
		 */
		private SourceType sourceType;
		
		/**
		 * Considered only for {@link SourceType#EXPRESSION} or {@link SourceType#EXPRESSION_PATH}. And this represents
		 * expression type like - attr:, xpath: etc
		 */
		private String expressionType;
		
		/**
		 * If specified, this pattern will be search and marked marking text as error/warning.
		 */
		private String patternText;
		
		private Pattern pattern;
		
		/**
		 * Error/warning message to convey.
		 */
		private String message;
		
		/**
		 * Indicates whether this rule is for error or warning.
		 */
		private boolean error;
		
		/**
		 * Considered only for {@link SourceType#EXPRESSION} or {@link SourceType#EXPRESSION_PATH}. And represent from index (inclusive) of 
		 * current expression part within main expression.
		 */
		private Integer fromExpressionIndex;
		
		/**
		 * Considered only for {@link SourceType#EXPRESSION} or {@link SourceType#EXPRESSION_PATH}. And represent to index (inclusive) of 
		 * current expression part within main expression.
		 */
		private Integer toExpressionIndex;

		public void setSourceType(SourceType sourceType)
		{
			this.sourceType = sourceType;
		}
		
		public void setExpressionType(String expressionType)
		{
			this.expressionType = expressionType;
		}
		
		public void setFromExpressionIndex(Integer fromExpressionIndex)
		{
			this.fromExpressionIndex = fromExpressionIndex;
		}
		
		public void setToExpressionIndex(Integer toExpressionIndex)
		{
			this.toExpressionIndex = toExpressionIndex;
		}

		public void setPattern(String patternText)
		{
			this.patternText = patternText;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}
		
		public void setError(boolean error)
		{
			this.error = error;
		}
		
		public boolean isInIndexRange(int index)
		{
			if(fromExpressionIndex == null && toExpressionIndex == null)
			{
				return true;
			}
			
			if(fromExpressionIndex != null && index < fromExpressionIndex)
			{
				return false;
			}
			
			if(toExpressionIndex != null && index > toExpressionIndex)
			{
				return false;
			}
			
			return true;
		}
	}
	
	private Map<SourceType, List<Rule>> rules = new HashMap<>();
	
	private TextParsingRuleManager()
	{
		try
		{
			XMLBeanParser.parse(TextParsingRuleManager.class.getResourceAsStream("/rules/text-parsing-rules.xml"), this);
		}catch(RuntimeException ex)
		{
			logger.error("An error occurred while loading text parsing rules", ex);
			throw ex;
		}
	}
	
	public static TextParsingRuleManager getInstance()
	{
		return instance;
	}
	
	public void addRule(Rule rule)
	{
		rule.pattern = rule.patternText != null ? Pattern.compile(rule.patternText) : null;
		
		List<Rule> ruleLst = this.rules.get(rule.sourceType);
		
		if(ruleLst == null)
		{
			ruleLst = new ArrayList<>();
			this.rules.put(rule.sourceType, ruleLst);
		}
		
		ruleLst.add(rule);
	}
	
	public void parseText(int index, SourceType sourceType, String expressionType, LocationRange location, TextContent text, FileParseCollector collector)
	{
		List<Rule> ruleLst = this.rules.get(sourceType);
		
		if(CollectionUtils.isEmpty(ruleLst))
		{
			return;
		}
		
		for(Rule rule : ruleLst)
		{
			if(expressionType != null && rule.expressionType != null && !expressionType.equals(rule.expressionType))
			{
				continue;
			}
			
			if(!rule.isInIndexRange(index))
			{
				continue;
			}

			if(rule.pattern != null)
			{
				Matcher matcher = rule.pattern.matcher(text.getText());
				
				while(matcher.find())
				{
					collector.addMessage(new FileParseMessage(
							rule.error ? MessageType.ERROR : MessageType.WARNING, 
							rule.message, 
							location.getStartLineNumber() + text.lineOf(matcher.start()), 
							location.getStartOffset() + matcher.start(), 
							location.getStartOffset() + matcher.end()));
				}
			}
			//if pattern is not specified consider full value is errored
			else
			{
				collector.addMessage(new FileParseMessage(
						rule.error ? MessageType.ERROR : MessageType.WARNING, 
						rule.message, 
						location.getStartLineNumber(), 
						location.getStartOffset(), 
						location.getEndOffset()));
			}
		}
	}
}
