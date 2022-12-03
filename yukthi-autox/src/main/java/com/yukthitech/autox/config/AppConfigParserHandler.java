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
package com.yukthitech.autox.config;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.util.StringUtil;

/**
 * Parser handler for parsing application configuration xml.
 * @author akiran
 */
public class AppConfigParserHandler extends DefaultParserHandler
{
	/**
	 * Expression to be used for accessing application properties or system/environment properties.
	 */
	public static final Pattern EXPR_PATTERN = Pattern.compile("\\#+\\{(.+?)\\}");
	
	/**
	 * Prefix to be used to skip expression patterns.
	 */
	public static final String EXPR_ESCAPE_PREFIX = "##";
	
	/**
	 * String to replace expression escape prefix.
	 */
	public static final String EXPR_ESCAPE_REPLACE = "#";

	/**
	 * Value provider for providing application properties and system/env properties.
	 */
	private AppConfigValueProvider appConfigValueProvider;
	
	/**
	 * Instantiates a new app config parser handler.
	 *
	 * @param appProperties the app properties
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AppConfigParserHandler(Properties appProperties)
	{
		this.appConfigValueProvider = new AppConfigValueProvider((Map) appProperties);
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.DefaultParserHandler#processText(java.lang.Object, java.lang.String)
	 */
	@Override
	public String processText(Object rootBean, String text)
	{
		return StringUtil.getPatternString(text, appConfigValueProvider, EXPR_PATTERN, EXPR_ESCAPE_PREFIX, EXPR_ESCAPE_REPLACE);
	}
}
