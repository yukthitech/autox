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
package com.yukthitech.autox;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.AppConfigParserHandler;
import com.yukthitech.autox.config.AppConfigValueProvider;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.ref.ContextAttributeReference;
import com.yukthitech.autox.ref.ContextAttributeXpathReference;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.XMLConstants;
import com.yukthitech.ccg.xml.util.StringUtil;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Parser handler for loading test suite files.
 * @author akiran
 */
public class TestSuiteParserHandler extends DefaultParserHandler
{
	private static final String ATTR_BEAN_REF = "beanRef";
	
	private static final String ATTR_BEAN_COPY = "beanCopy";
	
	private static final String ATTR_CONTEXT_ATTR_REF = "attrRef";
	
	private static final String ATTR_CONTEXT_ATTR_XPATH_REF = "attrXpathRef";
	
	private static Set<String> reservedNameSpaces = new HashSet<>();
	
	static
	{
		reservedNameSpaces.add(IAutomationConstants.STEP_NAME_SPACE);
		reservedNameSpaces.add(IAutomationConstants.FUNC_NAME_SPACE);
		
		reservedNameSpaces.add(XMLConstants.CCG_URI);
		reservedNameSpaces.add(XMLConstants.NEW_CCG_URI);
	}
	
	/**
	 * Application configuration.
	 */
	private ApplicationConfiguration appConfig;

	/**
	 * Value provider for providing application properties and system/env properties.
	 */
	private AppConfigValueProvider appConfigValueProvider;
	
	/**
	 * Automation reserve node handler.
	 */
	private AutomationReserveNodeHandler reserveNodeHandler;
	
	public TestSuiteParserHandler(AutomationContext context)
	{
		this(context, null);
	}

	public TestSuiteParserHandler(AutomationContext context, AutomationReserveNodeHandler reserveNodeHandler)
	{
		this.appConfig = context.getAppConfiguration();
		appConfigValueProvider = new AppConfigValueProvider(appConfig.getApplicationProperties());
		
		if(reserveNodeHandler == null)
		{
			reserveNodeHandler = new AutomationReserveNodeHandler(context, appConfig);
		}
		
		this.reserveNodeHandler = reserveNodeHandler;
		super.registerReserveNodeHandler(reserveNodeHandler);
	}
	
	/**
	 * Sets the maintains the file being parsed.
	 *
	 * @param fileBeingParsed the new maintains the file being parsed
	 */
	public void setFileBeingParsed(File fileBeingParsed)
	{
		reserveNodeHandler.setFileBeingParsed(fileBeingParsed);
	}
	
	@Override
	public Object createBean(BeanNode node, XMLAttributeMap att, ClassLoader loader)
	{
		String beanRefName = att.getReserved(ATTR_BEAN_REF, null);
		
		if(beanRefName != null)
		{
			Object bean = appConfig.getDataBean(beanRefName);
			
			if(bean == null)
			{
				throw new InvalidStateException("Invalid data-bean name specified in bean-ref. Name: ", beanRefName);
			}
			
			return bean;
		}

		//handle attribute to set context attribute reference
		String attrRefName = att.getReserved(ATTR_CONTEXT_ATTR_REF, null);
		
		if(attrRefName != null)
		{
			return new ContextAttributeReference(attrRefName);
		}

		//handle attribute to set context attribute reference
		String attrRefXpath = att.getReserved(ATTR_CONTEXT_ATTR_XPATH_REF, null);
		
		if(attrRefXpath != null)
		{
			return new ContextAttributeXpathReference(attrRefXpath);
		}

		//take care of bean copy
		String beanCopyName = att.getReserved(ATTR_BEAN_COPY, null);
		
		if(beanCopyName != null)
		{
			Object bean = appConfig.getDataBean(beanCopyName);
			
			if(bean == null)
			{
				throw new InvalidStateException("Invalid data-bean name specified in bean-copy. Name: {}", beanCopyName);
			}

			try
			{
				Object copy = bean.getClass().newInstance();
				BeanUtils.copyProperties(copy, bean);
				return copy;
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while making copy of bean - {}", beanCopyName);
			}
		}
		
		Object bean = super.createBean(node, att, loader);
		
		if(bean instanceof ILocationBased)
		{
			((ILocationBased) bean).setLocation(reserveNodeHandler.getFileBeingParsed(), saxLocator.getLineNumber());
		}
		
		return bean;
	}
	
	@Override
	public String processText(Object rootBean, String text)
	{
		return StringUtil.getPatternString(text, appConfigValueProvider, AppConfigParserHandler.EXPR_PATTERN, AppConfigParserHandler.EXPR_ESCAPE_PREFIX, AppConfigParserHandler.EXPR_ESCAPE_REPLACE);
	}
	
	@Override
	public boolean isReserveUri(String uri)
	{
		return reservedNameSpaces.contains(uri);
	}
	
}
