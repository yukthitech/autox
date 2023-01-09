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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.xml.sax.Locator;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.reserved.NodeName;
import com.yukthitech.utils.exceptions.InvalidStateException;

@NodeName(namePattern = "clone")
public class CloneReserveNodeHandler implements IReserveNodeHandler
{
	private static final String ATTR_BEAN_ID = "beanId";
	
	private static final String ATTR_PROPERTY = "property";
	
	private static final Pattern PROP_PATTERN = Pattern.compile("^\\s*\\w+\\s*\\:.*");
	
	/**
	 * Property info to be altered.
	 * @author akiran
	 */
	public static class SetInfo
	{
		/**
		 * Property expression.
		 */
		private String property;
		
		/**
		 * Value expression.
		 */
		private String value;

		public void setProperty(String property)
		{
			this.property = property;
		}

		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	public static class RemoveInfo
	{
		/**
		 * Property expression.
		 */
		private String property;
		
		public void setProperty(String property)
		{
			this.property = property;
		}
		
	}
	
	/**
	 * Used to store clone info.
	 * @author akiran
	 */
	public static class BeanCloneInfo
	{
		/**
		 * Bean to be cloned and modified.
		 */
		private Object bean;
		
		/**
		 * Json content to be used to process expressions.
		 */
		private Map<String, Object> jsonContext;
		
		/**
		 * Flag to enable or disable json expressions.
		 */
		private boolean jsonExpressionsEnabled = false;
		
		public BeanCloneInfo(Object bean)
		{
			this.bean = bean;
		}
		
		public void setBeanId(String beanId)
		{
		}
		
		public void setProperty(String property)
		{
		}
		
		public void setJsonExpressionsEnabled(boolean jsonExpressionsEnabled)
		{
			this.jsonExpressionsEnabled = jsonExpressionsEnabled;
		}
		
		@SuppressWarnings("unchecked")
		public void setJsonContextJson(String jsonContext)
		{
			try
			{
				this.jsonContext = (Map<String, Object>) IAutomationConstants.OBJECT_MAPPER.readValue(jsonContext, Object.class);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while parsing json context", ex);
			}
		}
		
		public void setJsonContext(Map<String, Object> jsonContext)
		{
			this.jsonContext = jsonContext;
		}

		public void setSet(SetInfo info)
		{
			AutomationContext automationContext = AutomationContext.getInstance();
			PrefixExpressionFactory exprFactory = PrefixExpressionFactory.getExpressionFactory();
			
			String prop = info.property;
			
			if(!PROP_PATTERN.matcher(prop).matches())
			{
				prop = "prop: " + prop;
			}
			
			Object value = exprFactory.parseExpression(automationContext, info.value);
			exprFactory.setExpressionValue(automationContext, prop, value, bean);
		}
		
		public void setRemove(RemoveInfo removeInfo)
		{
			AutomationContext automationContext = AutomationContext.getInstance();
			PrefixExpressionFactory exprFactory = PrefixExpressionFactory.getExpressionFactory();

			exprFactory.removeByExpression(automationContext, removeInfo.property, bean);
		}
	}
	
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		String id = att.get(ATTR_BEAN_ID, null);
		String property = att.get(ATTR_PROPERTY, null);
		
		Object actualBean = parserHandler.getBean(id);
		
		if(actualBean == null)
		{
			throw new InvalidArgumentException("Invalid/no id specified for bean cloning: " + id);
		}
		
		if(StringUtils.isBlank(property))
		{
			throw new InvalidArgumentException("No property name specified for cloned bean");
		}
		
		//create a clone
		try
		{
			Object clone = SerializationUtils.clone((Serializable) actualBean);
			return new BeanCloneInfo(clone);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to clone bean represented by id: {}", id, ex);
		}
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		//replace the modified bean on the node. So that, that will take effect
		BeanCloneInfo cloneInfo = (BeanCloneInfo) node.getActualBean();
		Object bean = cloneInfo.bean;
		
		if(cloneInfo.jsonExpressionsEnabled)
		{
			Map<String, Object> context = cloneInfo.jsonContext;
			
			if(context == null)
			{
				context = new HashMap<String, Object>();
			}
			
			context.put("context", AutomationContext.getInstance());
			bean = IAutomationConstants.JSON_EXPR_ENGINE.processObject(bean, context);
		}
		
		node.replaceBean(bean);
		
		String propName = att.get(ATTR_PROPERTY, null);
		Object parent = node.getParent();
		
		if(parent instanceof DynamicBean)
		{
			((DynamicBean) parent).add(propName, bean);
			return;
		}
		
		try
		{
			PropertyUtils.setProperty(parent, propName, bean);
		}catch(Exception ex)
		{
			String className = parent != null ? parent.getClass().getName() : "null";
			throw new InvalidStateException("An error occurred while setting property '{}' on bean of type: {}", propName, className, ex);
		}
	}
}
